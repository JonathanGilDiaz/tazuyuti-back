/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Feb 2025
 * @date 23/02/2025
 */
package com.example.residencia_back.services.administration;

import java.sql.Timestamp;
import java.util.Optional;

import com.example.residencia_back.entities.administration.Session;
import com.example.residencia_back.entities.administration.User;

public interface SessionService {

    void setUpdateActivo(boolean status, User user, Timestamp fecha_fin);

    Optional<Session> findByToken(String token);

    void save(Session sesion);
}
