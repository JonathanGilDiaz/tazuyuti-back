/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Feb 2025
 * @date 23/02/2025
 */
package com.example.tazuyuti_back.services.administration;

import java.sql.Timestamp;

import com.example.tazuyuti_back.entities.administration.SessionAttempt;
import com.example.tazuyuti_back.entities.administration.User;

public interface SessionAttemptService {

    void save(SessionAttempt intentoSession);

    public int countByUsuarioAndFechaBetween(User usuario, Timestamp fechaInicial, Timestamp fechaFinal);

    int deleteByUsuarioAndFechaBetween(User usuario, Timestamp fechaInicial, Timestamp fechaFinal);
}
