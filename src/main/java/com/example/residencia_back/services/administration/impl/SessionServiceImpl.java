/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Feb 2025
 * @date 23/02/2025
 */

package com.example.residencia_back.services.administration.impl;

import java.sql.Timestamp;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.residencia_back.entities.administration.Session;
import com.example.residencia_back.entities.administration.User;
import com.example.residencia_back.repositories.administration.SessionRepository;
import com.example.residencia_back.services.administration.SessionService;

/**
 * Service implementation for managing user sessions.
 *
 * This class implements the SessionService interface and provides 
 * methods for updating session status, retrieving sessions by token,
 * and saving new session records. It interacts with the SesionRepository 
 * to perform the required data operations.
 */

@Service
public class SessionServiceImpl implements SessionService{
  
    @Autowired
    private SessionRepository sessionRepository;
    
    @Override
    public void setUpdateActivo(boolean status, User user, Timestamp fecha_fin) {
       sessionRepository.setUpdateActivo(status, user, fecha_fin);
    }

    @Override
    public Optional<Session> findByToken(String token) {
        return sessionRepository.findByToken(token);
    }

    @Override
    public void save(Session sesion) {
        sessionRepository.save(sesion);
    }
    
}
