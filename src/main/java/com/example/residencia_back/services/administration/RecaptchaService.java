/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Feb 2025
 * @date 23/02/2025
 */
package com.example.residencia_back.services.administration;

public interface RecaptchaService {
    boolean verifyRecaptcha(String response, boolean isRecaptchaV3);
}
