package com.example.tazuyuti_back.helpers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.example.tazuyuti_back.models.utilities.Pagination;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class FiltersAndSortHelper {

    /**
     * Applies filtering to a list of DTO objects based on the filters provided in the request.
     * 
     * @param <T> The type of objects in the list.
     * @param data The list of DTO objects to filter.
     * @param request The pagination object containing filters.
     * @param clazz The class type of the DTO.
     * @return A filtered list of objects.
     */
   public static <T> List<T> applyFilters(List<T> data, Pagination request, Class<T> clazz) {
    if (request.getFilters() == null || request.getFilters().isEmpty()) {
        return data;
    }
    return data.stream()
            .filter(item -> request.getFilters().entrySet().stream().allMatch(entry -> {
                try {
                    Field field = clazz.getDeclaredField(entry.getKey());
                    field.setAccessible(true);
                    Object fieldValue = field.get(item);
                    Object filterValue = entry.getValue();
                    if (fieldValue == null || filterValue == null) {
                        return false;
                    }
                    if (fieldValue instanceof String && filterValue instanceof String) {
                        return ((String) fieldValue).toLowerCase().contains(((String) filterValue).toLowerCase());
                    } else if (fieldValue instanceof Integer) {
                        return fieldValue.equals(Integer.parseInt(filterValue.toString()));
                    } 
                    else if (fieldValue instanceof LocalDateTime) {
                        LocalDateTime fieldDateTime = (LocalDateTime) fieldValue;
                        try {
                            LocalDate filterDate = LocalDate.parse(filterValue.toString()); 
                            return fieldDateTime.toLocalDate().equals(filterDate); 
                        } catch (Exception e) {
                            return false;
                        }
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    return true;
                }
                return false;
            }))
            .collect(Collectors.toList());
}


    /**
     * Applies sorting to a list of DTO objects based on sorting criteria.
     * 
     * @param <T> The type of objects in the list.
     * @param data The list of DTO objects to sort.
     * @param sort The sorting criteria.
     * @param clazz The class type of the DTO.
     * @return A sorted list of objects.
     */
    public static <T> List<T> applySorting(List<T> data, List<Map<String, String>> sortCriteria, Class<T> clazz) {
        if (sortCriteria == null || sortCriteria.isEmpty()) {
            return data;
        }
        Comparator<T> finalComparator = null;
        for (Map<String, String> order : sortCriteria) {
            for (Map.Entry<String, String> entry : order.entrySet()) {
                String fieldName = entry.getKey();
                boolean ascending = entry.getValue().equalsIgnoreCase("asc");
                Comparator<T> comparator = (o1, o2) -> {
                    try {
                        Field field = clazz.getDeclaredField(fieldName);
                        field.setAccessible(true);
                        Object value1 = field.get(o1);
                        Object value2 = field.get(o2);
    
                        if (value1 == null && value2 == null) {
                            return 0;
                        }
                        if (value1 == null) {
                            return ascending ? -1 : 1;
                        }
                        if (value2 == null) {
                            return ascending ? 1 : -1;
                        }
                        if ("folioPeticion".equals(fieldName) && value1 instanceof String && value2 instanceof String) {
                            return compareFolio((String) value1, (String) value2, ascending);
                        }
                        if (value1 instanceof Comparable && value2 instanceof Comparable) {
                            @SuppressWarnings("unchecked")
                            Comparable<Object> comparableValue1 = (Comparable<Object>) value1;
                            int result = comparableValue1.compareTo(value2);
                            return ascending ? result : -result;
                        }
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        return 0;
                    }
                    return 0;
                };
                if (finalComparator == null) {
                    finalComparator = comparator;
                } else {
                    finalComparator = finalComparator.thenComparing(comparator);
                }
            }
        }
        if (finalComparator != null) {
            data.sort(finalComparator);
        }
        return data;
    }
    

    private static int compareFolio(String folio1, String folio2, boolean ascending) {
        String[] parts1 = folio1.split("-");
        String[] parts2 = folio2.split("-");
    
        int num1 = Integer.parseInt(parts1[0]);
        int num2 = Integer.parseInt(parts2[0]);
    
        int result = Integer.compare(num1, num2);
        if (result == 0) { // Si los primeros números son iguales, comparar la segunda parte
            int subNum1 = Integer.parseInt(parts1[1]);
            int subNum2 = Integer.parseInt(parts2[1]);
            result = Integer.compare(subNum1, subNum2);
        }
    
        return ascending ? result : -result;
    }
    
    
    
    
    

    /**
     * Applies pagination to a list of DTO objects.
     * 
     * @param <T> The type of objects in the list.
     * @param list The list of DTO objects.
     * @param page The page number (zero-based index).
     * @param size The number of elements per page.
     * @return A paginated list of objects.
     */
    public static <T> Page<T> applyPagination(List<T> list, int page, int size) {
        if (list.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), PageRequest.of(page, size), 0);
        }
        if (page * size >= list.size()) {
            page = 0;
        }
        int start = Math.min(page * size, list.size());
        int end = Math.min(start + size, list.size());

        List<T> sublist = list.subList(start, end);
        return new PageImpl<>(sublist, PageRequest.of(page, size), list.size());
    }

    /**
     * Converts sorting criteria into a Spring Data Sort object.
     * 
     * @param sortList The list of sorting criteria.
     * @return A Sort object representing the sorting criteria.
     */
    public static Sort getSortFromRequest(List<Map<String, String>> sortList) {
        if (sortList == null || sortList.isEmpty()) {
            return Sort.unsorted();
        }
        List<Sort.Order> orders = new ArrayList<>();
        for (Map<String, String> orderMap : sortList) {
            for (Map.Entry<String, String> entry : orderMap.entrySet()) {
                Sort.Direction direction = entry.getValue().equalsIgnoreCase("asc") ? Sort.Direction.ASC
                        : Sort.Direction.DESC;
                orders.add(new Sort.Order(direction, entry.getKey()));
            }
        }
        return Sort.by(orders);
    }

    /**
     * Returns the priority ranking for a given priority type.
     * 
     * @param prioridad The priority level as a string.
     * @return An integer representing the priority rank.
     */
    public static int getPriorityRank(String prioridad) {
        return switch (prioridad) {
            case "INDICACIÓN DEL GOBERNADOR" -> 1;
            case "URGENTE" -> 2;
            case "NORMAL" -> 3;
            default -> 4;
        };
    }
}
