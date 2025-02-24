/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Feb 2025
 * @date 23/02/2025
 */
package com.example.residencia_back.helpers;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.ZoneId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import com.example.residencia_back.helpers.Anottation.FileConstraint;
import com.example.residencia_back.models.utilities.Pagination;
import io.jsonwebtoken.io.IOException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Utility class that provides static methods for performing common
 * text manipulation operations.
 */
public class Utils {

    private static Logger logger = LoggerFactory.getLogger(Utils.class);

    public static final String[] ORIGINAL_CHAR = {"á", "é", "í", "ó", "ú", "ü", "ñ", ",", ".", ":", ";", "!", "?", "\"", "'", "(", ")", "[", "]", "{", "}", "*", "@", "  "};
    public static final String[] REPLACEMENT_CHARS = {"a", "e", "i", "o", "u", "u", "n", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", " "};

    /**
     * Normalizes the provided text. This method converts the text
     * to lowercase, removes accents and diacritical marks, and
     * also eliminates unwanted special characters, leaving only
     * letters and numbers.
     *
     * @param text The text to normalize. Can be null.
     * @return The normalized text in lowercase, without accents or special 
     * characters, or null if a null text is provided.
     */
    public static String normalizeText(String text) {
        if (text == null) { return ""; }
        // Convert to lowercase
        String lowerText = text.toLowerCase();
        // Remove accents and diacritics
        String normalizedText = Normalizer.normalize(lowerText, Normalizer.Form.NFD);
        normalizedText = normalizedText.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        // Remove unwanted special characters
        normalizedText = normalizedText.replaceAll("[^a-z0-9 ]", "");
        normalizedText = normalizedText.replaceAll("  ", " ");
        return normalizedText;
    }

    /**
     * Converts a camel case string into a lower case string with words separated by underscores.
     * The first word will have its first letter in lowercase, while the subsequent words will have
     * their first letter in uppercase.
     *
     * @param input The camel case string to be converted. Can be null.
     * @return The transformed string with words separated by underscores in lowercase, or null if the input is null.
     */
    public static String convertCamelCaseToUnderscore(String input) {
        if (input == null) { return null; }
        StringBuilder result = new StringBuilder();
        // Iterate through each character in the input string
        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);
            // If the character is uppercase
            if (Character.isUpperCase(currentChar)) {
                // If it's not the first character, add an underscore before the uppercase letter
                if (i > 0) { result.append("_"); }
                // Append the lowercase version of the uppercase letter
                result.append(Character.toLowerCase(currentChar));
            } else {
                // Append the current character as is
                result.append(currentChar);
            }
        }
        return result.toString();
    }

    /**
     * Retrieves both a Specification and a Pageable object based on the filters and sorting parameters
     * provided in a generic pagination object. The Specification allows for dynamic queries, while
     * the Pageable object is used for paginating the results. Both are returned in a map.
     * 
     * @param request A Pagination object containing filters, sorting, page, and size parameters.
     * @param entityClass The entity class on which the specification and pagination will be built.
     * @param <T> The entity type for which the specification and pagination are being created.
     * @return A map containing the specification and the Pageable object for use in queries.
     */
    public static <T> Map<String, Object> getSpecificationAndPageable(Pagination request, Class<T> entityClass) {
        Specification<T> specs = getSpecification(request.getFilters());
        Sort sort = Sort.by(request.getSort().stream()
            .map(order -> {
                Map.Entry<String, String> entry = order.entrySet().iterator().next();
                String field = entry.getKey();
                String direction = entry.getValue();
                return new Sort.Order(Sort.Direction.fromString(direction), field);
            }).toList());
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize(), sort);
        
        Map<String, Object> data = new HashMap<>();
        data.put("specification", specs);
        data.put("pageable", pageable);
        return data;
    }

    /**
     * Creates a dynamic specification based on the provided filters to be used in a JPA query.
     * The filters are applied to the entity fields using the LIKE function and the values are normalized
     * to make the comparison case-insensitive and accent-insensitive. This function is useful for creating
     * flexible queries based on dynamic criteria.
     * 
     * @param filters A map of filters where keys are field names and values are the filter values.
     * @param <T> The entity type for which the specification is being built.
     * @return A specification that can be used in a JPA query.
     */
    @SuppressWarnings("unchecked")
    public static <T> Specification<T> getSpecification(Map<String, String> filters) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filters != null){
                filters.forEach((field, value) -> {
                    try {
                        String[] fieldParts = field.split("\\.");
                        Path<?> fieldPath = root;
    
                        // Process multiple levels of relationships (nested joins)
                        for (int i = 0; i < fieldParts.length - 1; i++) {
                            fieldPath = ((From<?, ?>) fieldPath).join(fieldParts[i], JoinType.LEFT);
                        }
    
                        String fieldName = fieldParts[fieldParts.length - 1]; // Campo final
                        Path<?> path = fieldPath.get(fieldName);

                        // Detect the field type and apply the filter appropriately
                        if (path.getJavaType().equals(String.class)) {

                            String normalizedValue = "%" + normalizeText(value) + "%";

                            // Initial expression with LOWER and TRIM function
                            Expression<String> expression = criteriaBuilder.function("LOWER", String.class, (Expression<String>) path);
                            
                            // Apply REPLACE in a loop for each pair of characters
                            for (int i = 0; i < ORIGINAL_CHAR.length; i++) {
                                expression = criteriaBuilder.function(
                                    "REPLACE", 
                                    String.class, 
                                    expression, 
                                    criteriaBuilder.literal(ORIGINAL_CHAR[i]), 
                                    criteriaBuilder.literal(REPLACEMENT_CHARS[i])
                                );
                            }

                            // Add the predicate using the normalized value
                            predicates.add(criteriaBuilder.like(expression, normalizedValue));
                        } else if (path.getJavaType().equals(Boolean.class) || path.getJavaType().equals(boolean.class)) {
                            // If the field is Boolean, convert the text value to Boolean
                            Boolean booleanValue = Boolean.parseBoolean(value);
                            predicates.add(criteriaBuilder.equal(path, booleanValue));
                        } else if (path.getJavaType().equals(Timestamp.class)) {
                            // If field is Timestamp, create a range from start to end of day
                            LocalDate localDate = LocalDate.parse(value); // Assume "yyyy-MM-dd" format
                            Timestamp startOfDay = Timestamp.valueOf(localDate.atStartOfDay());
                            Timestamp endOfDay = Timestamp.valueOf(localDate.atTime(23, 59, 59, 999999999));
                            // Explicitly convert path to Timestamp
                            Path<Timestamp> timestampPath = (Path<Timestamp>) path;
                            predicates.add(criteriaBuilder.between(timestampPath, startOfDay, endOfDay));
                        } else if (path.getJavaType().equals(Date.class)) {
                            // If the field is Date, create a range from the start to the end of the day
                            LocalDate localDate = LocalDate.parse(value); // Assume "yyyy-MM-dd" format
                            Date startOfDay = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                            Date endOfDay = Date.from(localDate.atTime(23, 59, 59, 999999999).atZone(ZoneId.systemDefault()).toInstant());
                            
                            Path<Date> datePath = (Path<Date>) path;
                            predicates.add(criteriaBuilder.between(datePath, startOfDay, endOfDay));
                        } else if (path.getJavaType().equals(LocalDate.class)) {
                            LocalDate localDate = LocalDate.parse(value);
                            Path<LocalDate> localDatePath = (Path<LocalDate>) path;
                            predicates.add(criteriaBuilder.equal(localDatePath, localDate));
                        }
                        // This code is to perform filtering and sorting of folio with zeros included
                        /*
                        else if (path.getJavaType().equals(Integer.class)) {
                            // Convert Integer to String and perform "like" search
                            Expression<String> expression = criteriaBuilder.concat(path.as(String.class), "");
                            String normalizedValue = "%" + value + "%";
                            predicates.add(criteriaBuilder.like(expression, normalizedValue));
                        }
                        */
                        else {
                            // If it's another data type (e.g. Integer, Double), apply an equality filter
                            predicates.add(criteriaBuilder.equal(path, value));
                        }
                    } catch (Exception e) {
                        System.err.println("-----------------");
                        logger.error(e.getMessage().toString());
                        System.err.println("-----------------");
                    }
                });
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static String validateFilteringInformation(String validOptionsString, List<Map<String, String>> sort, Map<String, String> filter){
        List<String> validOptions = List.of(validOptionsString.split(","));
        for (Map<String, String> order : sort) {
            for (Map.Entry<String, String> entry : order.entrySet()) {
                String field = entry.getKey();
                String direction = entry.getValue();

				if (!validOptions.contains(field)) {
                    return SystemText.General.CAMPO_INVALIDO_SORT(field);
                } else {
                    if (!"asc".equals(direction) && !"desc".equals(direction)) {
                        return SystemText.General.DATO_INVALIDO_SORT(direction, field);
                    }
                }
            }
        }
        if (filter != null){
            for (String key : filter.keySet()) {
                if (!validOptions.contains(key)) {
                    return SystemText.General.CAMPO_INVALIDO_FILTERS(key);
                }
            }
        }
        return "";
    }

    public static Expression<String> clearTextExpression(Expression<String> expression, CriteriaBuilder criteriaBuilder){
        // Apply the REPLACE function for each special character in the concatenated expression
        for (int i = 0; i < Utils.ORIGINAL_CHAR.length; i++) {
            expression = criteriaBuilder.function("REPLACE", String.class, 
            expression, 
            criteriaBuilder.literal(Utils.ORIGINAL_CHAR[i]), 
            criteriaBuilder.literal(Utils.REPLACEMENT_CHARS[i])
            );
        }
        return expression;
    }
    
    public static <T> Specification<T> filterConcat(Map<String, String> filters, String field, String[] fieldsConcatenate) {
        return (root, query, criteriaBuilder) -> {
            // Normalize the lookup value
            String normalizedValue = "%" + Utils.normalizeText(filters.get(field).toString()) + "%";

            // Initialize the concatenation expression
            Expression<String> concatExpression = null;

            // Iterate over the fields in fieldsConcatenate and build the concatenation expression
            for (String fieldName : fieldsConcatenate) {
                if (concatExpression == null) {
                    concatExpression = root.get(fieldName);
                } else {
                    concatExpression = criteriaBuilder.concat(concatExpression, criteriaBuilder.concat(" ", root.get(fieldName)));
                }
            }

            // Normalize the concatenated expression
            if (concatExpression != null) {
                concatExpression = criteriaBuilder.function("LOWER", String.class, concatExpression);
                concatExpression = Utils.clearTextExpression(concatExpression, criteriaBuilder);
            }

            // Create and return the predicate with the `like` filter for the normalized expression
            return criteriaBuilder.like(concatExpression, normalizedValue);
        };
    }

	public static <T> Specification<T> filterConcatInner(Map<String, String> filters, String field, String[] fieldsConcatenate) {
		return (root, query, criteriaBuilder) -> {
			// Normalize the search value
			String normalizedValue = "%" + Utils.normalizeText(filters.get(field)) + "%";
	
			// Expression to concatenate
			Expression<String> concatExpression = null;
	
			for (String fieldName : fieldsConcatenate) {
				Expression<String> fieldExpression;
	
				if (fieldName.equals("-")) {
					// If it is a separator, we treat it as a constant
					fieldExpression = criteriaBuilder.literal("-");
				} else if (fieldName.contains(".")) {
					// If the field is from a relationship (e.g. request.folio), we do a join
					String[] path = fieldName.split("\\.");
					Path<?> joinPath = root;
					for (int i = 0; i < path.length - 1; i++) {
						joinPath = root.join(path[i], JoinType.LEFT);
					}
					fieldExpression = joinPath.get(path[path.length - 1]).as(String.class);
				} else {
					// If it is a direct field of the current entity
					fieldExpression = root.get(fieldName).as(String.class);
				}
	
				// Build the concatenation
				if (concatExpression == null) {
					concatExpression = fieldExpression;
				} else {
					concatExpression = criteriaBuilder.concat(concatExpression, fieldExpression);
				}
			}
	
			// Normalize the concatenated expression
			if (concatExpression != null) {
				concatExpression = criteriaBuilder.function("LOWER", String.class, concatExpression);
				concatExpression = Utils.clearTextExpression(concatExpression, criteriaBuilder);
			}
	
			// Return the LIKE filter
			return criteriaBuilder.like(concatExpression, normalizedValue);
		};
	}

    /**
     * Convierte un archivo a Base64 junto con su tipo MIME.
     *
     * @param filePath Ruta del archivo.
     * @return Un mapa con el tipo MIME y el contenido en Base64.
     * @throws IllegalArgumentException Si la ruta no es válida.
     * @throws IOException              Si ocurre un problema al leer el archivo.
     */
    public static Map<String, String> encodeFileToBase64(String filePath) throws Exception {
        java.nio.file.Path path = java.nio.file.Path.of(filePath);

        // Validate file existence
        if (!Files.exists(path) || !Files.isRegularFile(path)) {
            throw new IllegalArgumentException("El archivo no existe o la ruta no es válida.");
        }

        // Read file as bytes
        byte[] fileBytes = Files.readAllBytes(path);

        // Detect MIME type
        String mimeType = Files.probeContentType(path);
        if (mimeType == null) {
            mimeType = "application/octet-stream"; // Generic type if not detected
        }

        // Encode to Base64
        String base64Content = Base64.getEncoder().encodeToString(fileBytes);

        // Build response
        Map<String, String> result = new HashMap<>();
        result.put("mimeType", mimeType);
        result.put("base64Content", base64Content);
        return result;

    }

    public static List<String> validateFields(Object object) {
        List<String> errors = new ArrayList<>();
        for (Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true); // Allow access to private fields
            try {
                Object value = field.get(object); // Get the value of the field
                // Check if it has @NotNull

                if (field.isAnnotationPresent(NotNull.class) && (value == null || value.equals(""))) {
                    NotNull notNullAnnotation = field.getAnnotation(NotNull.class);
                    // errors.add(String.format("Field: '%s'. Error: '%s'.", field.getName(), notNullAnnotation.message()));
                    errors.add(notNullAnnotation.message());
                }
                
                // Check if it has @Size
                if (((field.isAnnotationPresent(NotNull.class) || value != null) && (value instanceof String ? !value.equals("") : false) ) &&
                    field.isAnnotationPresent(Size.class) && value instanceof String) {
                    Size sizeAnnotation = field.getAnnotation(Size.class);
                    String strValue = (String) value;
                    if (strValue.length() < sizeAnnotation.min() || strValue.length() > sizeAnnotation.max()) {
                        // errors.add(String.format("Field: '%s'. Current size: '%d'. Allowed size: [%d - %d].",field.getName(), strValue.length(), sizeAnnotation.min(), sizeAnnotation.max()));
                        errors.add(sizeAnnotation.message());
                    }
                }
    
                // Check if it has @Pattern
                if (((field.isAnnotationPresent(NotNull.class) || value != null) && (value instanceof String ? !value.equals("") : false) ) &&
                    field.isAnnotationPresent(Pattern.class) && value instanceof String) {
                    Pattern patternAnnotation = field.getAnnotation(Pattern.class);
                    String strValue = (String) value;
                    if (!strValue.matches(patternAnnotation.regexp())) {
                        // errors.add(String.format("Field: '%s'. Current value: '%s'. Expected pattern: '%s'. Message: '%s'.", field.getName(), strValue, patternAnnotation.regexp(), patternAnnotation.message()));
                        errors.add(patternAnnotation.message());
                    }
                }

                // Check if it has @Email
                if (((field.isAnnotationPresent(NotNull.class) || value != null) && (value instanceof String ? !value.equals("") : false) ) &&
                    field.isAnnotationPresent(Email.class) && value instanceof String) {
                    Email emailAnnotation = field.getAnnotation(Email.class);
                    String strValue = (String) value;
                    if (!strValue.matches(emailAnnotation.regexp())) {
                        errors.add(emailAnnotation.message());
                    }
                }

                // Check if it has File
                if ((field.isAnnotationPresent(NotNull.class) || value != null) && value instanceof MultipartFile) {
                    MultipartFile file = (MultipartFile) value;
                    if (file != null && !file.isEmpty()){
                        if (field.isAnnotationPresent(FileConstraint.class)) {
                            FileConstraint fileConstraint = field.getAnnotation(FileConstraint.class);
                            if (file.getSize() > fileConstraint.maxSize()) {
                                errors.add(SystemText.General.LIMITE_PERMITIDO_ARCHIVO);
                            }
                            String fileName = file.getOriginalFilename();
                            if (fileName != null && !fileName.matches(fileConstraint.allowedExtensions())) {
                                errors.add(SystemText.General.FORMATO_PERMITIDO_ARCHIVO);
                            }
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                errors.add(String.format(SystemText.General.ACCESO_DENEGADO_CAMPO + "'%s'.", field.getName()));
            }
        }
    
        return errors;
    }

    /**
     * It takes a MultipartFile and a path, and saves the file in the path
     *
     * @param multipart   The file that you want to save.
     * @param pathDestino
     * @param name
     */
    public static void saveFileInServer(MultipartFile multipart, String pathDestino, String name) {
        java.nio.file.Path path = Paths.get(pathDestino);
        try {
            Files.copy(multipart.getInputStream(), path.resolve(name));
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The function creates a folder at the specified path with the given folder
     * name and returns the
     * folder path.
     * 
     * @param path       The `path` parameter is the directory where you want to
     *                   create the folder. It should be
     *                   a valid file path on your system. For example, if you want
     *                   to create the folder in the current
     *                   directory, you can pass "." as the `path` parameter.
     * @param folderName The name of the folder you want to create.
     * @return The method is returning the folder path.
     */
    public static String createFolder(String path, String folderName) {
        String message = "";
        String folderPath = path + folderName;
        File folder = new File(folderPath);
        if (!folder.exists()) {
            boolean created = folder.mkdir();
            if (created) {
                message = "Folder created successfully.";
            } else {
                message = "Failed to create the folder.";
            }
        } else {
            message = "The folder already exists.";
        }
        System.out.println(message);
        return folderPath;
    }

    /**
     * The function `deleteFolder` recursively deletes a folder and all its
     * contents.
     * 
     * @param folder The "folder" parameter is a File object representing the folder
     *               that you want to
     *               delete.
     */
    public static void deleteFolder(File folder) {
        if (folder.exists()) {
            if (folder.isDirectory()) {
                File[] allContents = folder.listFiles();
                if (allContents != null) {
                    for (File file : allContents) {
                        deleteFolder(file);
                    }
                }
                folder.delete();
            } else {
                folder.delete();
            }
        }
    }

    /**
     * The function checks if a folder exists at the specified path and returns true
     * if it does, and false
     * otherwise.
     * 
     * @param folderPath The folderPath parameter is a string that represents the
     *                   path to a folder on the
     *                   file system.
     * @return The method returns a boolean value indicating whether the folder
     *         specified by the folderPath
     *         exists and is a directory.
     */
    public static boolean doesFolderExist(String folderPath) {
        File folder = new File(folderPath);
        return folder.exists() && folder.isDirectory();
    }

	public static String getMime(String extension) {
        Map<String, String> mimesMap = new HashMap<>();
        mimesMap.put("jpg", "image/jpeg");
        mimesMap.put("jpeg", "image/jpeg");
        mimesMap.put("png", "image/png");
        mimesMap.put("gif", "image/gif");
        mimesMap.put("pdf", "application/pdf");
        mimesMap.put("doc", "application/msword");
        mimesMap.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        mimesMap.put("xls", "application/vnd.ms-excel");
        mimesMap.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    
        return mimesMap.get(extension);
    }

	/**
     * This Java function encodes a file to Base64 binary format.
     *
     * @param fileName The name (including the path) of the file that needs to
     *                 be encoded to Base64 binary format.
     * @return The method returns a String that represents the contents of the
     *         file encoded in Base64 format. If the file does not exist, it returns
     *         null.
     */
    public static String encodeFileToBase64Binary(String fileName) throws IOException {
		File file = new File(fileName);
		if (file.exists()) {
			byte[] fileBytes = null;
			try {
				fileBytes = Files.readAllBytes(file.toPath());
			} catch (java.io.IOException e) {
				e.printStackTrace();
			}
			String encoded = Base64.getEncoder().encodeToString(fileBytes);
			return encoded;
		} else {
			return null;
		}
	}



    

}

