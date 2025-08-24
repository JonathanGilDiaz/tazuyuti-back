/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Feb 2025
 * @date 23/02/2025
 */
package com.example.tazuyuti_back.services.administration.impl;

import java.sql.Timestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.tazuyuti_back.entities.administration.SessionAttempt;
import com.example.tazuyuti_back.entities.administration.User;
import com.example.tazuyuti_back.repositories.administration.SessionAttemptRepository;
import com.example.tazuyuti_back.services.administration.SessionAttemptService;

/**
* Service implementation for managing IntentoSession entities.
*
* This class implements the IntentoSessionService interface and provides 
* methods for handling operations related to IntentoSession records, 
* such as saving entries, counting attempts based on user and date range, 
* and deleting records. It interacts with the IntentoSessionRepository 
* to perform the required data operations.
*/

@Service
public class SessionAttemptServiceImpl implements SessionAttemptService {

    @Autowired
    private SessionAttemptRepository intentoSessionRepository;

    @Override
    public void save(SessionAttempt intentoSession) {
        intentoSessionRepository.save(intentoSession);
    }

    @Override
    public int countByUsuarioAndFechaBetween(User usuario, Timestamp fechaInicial, Timestamp fechaFinal) {
        return intentoSessionRepository.countByUsuarioAndFechaBetween(usuario, fechaInicial, fechaFinal);
    }

    @Override
    public int deleteByUsuarioAndFechaBetween(User usuario,Timestamp fechaInicial, Timestamp fechaFinal) {
        return intentoSessionRepository.deleteByUsuarioAndFechaBetween(usuario,fechaInicial,fechaFinal);
    }

}
