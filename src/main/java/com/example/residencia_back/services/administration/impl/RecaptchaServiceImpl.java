/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Feb 2025
 * @date 23/02/2025
 */
package com.example.residencia_back.services.administration.impl;

/**
 * Verifies the reCAPTCHA token by sending it to Google's API.
 *
 * This method takes the reCAPTCHA token received from the client (frontend),
 * and sends it along with the secret key to Google’s API to check if
 * the user passed the reCAPTCHA validation.
 *
 * @param response The reCAPTCHA token sent from the client.
 * @return true if the reCAPTCHA validation was successful; false otherwise.
 */
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.example.residencia_back.services.administration.RecaptchaService;

import java.util.Map;

@Service
public class RecaptchaServiceImpl implements RecaptchaService {

    @Value("${google.recaptcha.secret}")
    private String recaptchaSecret;

    private static final String RECAPTCHA_URL_V2 = "https://www.google.com/recaptcha/api/siteverify";

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public boolean verifyRecaptcha(String response, boolean isRecaptchaV3) {
        RestTemplate restTemplate = new RestTemplate();
        
        // Create the parameter map as a MultiValueMap
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("secret", recaptchaSecret);
        requestBody.add("response", response);

        // If it's reCAPTCHA v3, an additional score parameter is added
        if (isRecaptchaV3) {
            requestBody.add("score", "0.5"); // This is optional, you can adjust it to your needs
        }

        // Set the request headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Create the entity with the data and headers
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        // Make the POST request
        ResponseEntity<Map> recaptchaResponse = restTemplate.exchange(
                RECAPTCHA_URL_V2,
                HttpMethod.POST,
                requestEntity,
                Map.class
        );

        // Get the response and process the data
        Map<String, Object> body = recaptchaResponse.getBody();

        if (body != null) {
            if (isRecaptchaV3) {
                // reCAPTCHA v3 returns a score instead of a success
                Double score = (Double) body.get("score");
                return score != null && score >= 0.5; // Uses a minimum score of 0.5, you can adjust it
            } else {
                // reCAPTCHA v2 returns a boolean success
                Boolean success = (Boolean) body.get("success");
                return Boolean.TRUE.equals(success);
            }
        }

        return false;
    }
}