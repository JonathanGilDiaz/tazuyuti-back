/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Feb 2025
 * @date 23/02/2025
 */
package com.example.tazuyuti_back.helpers;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.tazuyuti_back.models.administration.UserDetail;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;

/**
* A utility class for various helper methods related to tools and utilities in the application.
*
* This class may contain static methods for common operations, 
* data transformations, or other utility functions that can be reused across 
* different parts of the application.
*/

public class ToolHelper {

    private static final SimpleDateFormat DTF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String getCurrentDateTime() {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(new Date());
    }

    public static Timestamp castDateTime(String timeStamp) {
        Timestamp dateTime = null;
        try {
            dateTime = new Timestamp(DTF.parse(timeStamp).getTime());
        } catch (ParseException ex) {
            Logger.getLogger(ToolHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dateTime;
    }

    public static Date timeStampToDate(Timestamp expiration) {
        return new Date(expiration.getTime());
    }

    public static String generatePassword(int length) {
        final char[] numbers = "0123456789".toCharArray();
        final char[] symbols = "@.$?¿¡!/-_#()*$".toCharArray();
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom random = new SecureRandom();
        String pp = IntStream.range(0, length - 2).map(i -> random.nextInt(chars.length())).mapToObj(randomIndex -> String.valueOf(chars.charAt(randomIndex))).collect(Collectors.joining());
        StringBuilder password = new StringBuilder(pp);
        password.insert(random.nextInt(password.length()), numbers[random.nextInt(numbers.length)]);
        password.insert(random.nextInt(password.length()), symbols[random.nextInt(symbols.length)]);
        return password.toString();
    }

    public static String generateCode(int length) {
        final char[] numbers = "0123456789".toCharArray();
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        SecureRandom random = new SecureRandom();
        String pp = IntStream.range(0, length - 2).map(i -> random.nextInt(chars.length())).mapToObj(randomIndex -> String.valueOf(chars.charAt(randomIndex))).collect(Collectors.joining());
        StringBuilder password = new StringBuilder(pp);
        password.insert(random.nextInt(password.length()), numbers[random.nextInt(numbers.length)]);
        return password.toString();
    }

    public static int getDayNow() {
        LocalDate currentdate = LocalDate.now();
        return currentdate.getDayOfMonth();
    }

    public static String getMonthNow() {
        LocalDate currentdate = LocalDate.now();
        int month = (currentdate.getMonthValue() - 1);
        String months[] = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        return months[month];
    }
    public static int getNumberMonthNow() {
        LocalDate currentdate = LocalDate.now();
        int month = currentdate.getMonthValue();
        return month;
    }
    

    public static int getYearNow() {
        LocalDate currentdate = LocalDate.now();
        return currentdate.getYear();
    }

    public static String getUserNameAuthenticate() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String userName = "anonymousUser";
		if (!auth.getPrincipal().getClass().getSimpleName().equals("String")){
			userName = ((UserDetail) auth.getPrincipal()).getUsername();

		}
        return userName.trim();
    }

    public static String decrypt(String encryptedData, String privateKeyPem) throws Exception {
        try {
            // Removemos los headers y espacios de la clave privada
            privateKeyPem = privateKeyPem.replace("-----BEGIN PRIVATE KEY-----", "")
                                         .replace("-----END PRIVATE KEY-----", "")
                                         .replaceAll("\\s+", "");

            // Convertimos la clave de Base64 a bytes
            byte[] keyBytes = Base64.getDecoder().decode(privateKeyPem);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

            // Configuramos el cifrado para desencriptar con RSA-OAEP
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            // Desencriptamos el mensaje (decodificamos de Base64 primero)
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(decryptedBytes, "UTF-8");
        } catch (Exception e) {
            throw new Exception("Error al desencriptar los datos: " + e.getMessage());
        }
    }

}

