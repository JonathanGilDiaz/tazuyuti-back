/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 04 Ene 2025
 * @date 04/01/2025
 */
package com.example.residencia_back.configuration;

import com.example.residencia_back.entities.administration.User;
import com.example.residencia_back.helpers.ToolHelper;
import com.example.residencia_back.services.administration.UserService;
import java.sql.Timestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component("authenticationEventListner")
public class AuthenticationEventListener implements AuthenticationEventPublisher {

    private static final int MAX_ATTEMPTS = 4;
    @Autowired
    private UserService usuarioService;

    @Override
    public void publishAuthenticationSuccess(Authentication authentication) {
        User usuario = usuarioService.findFirstByUsuarioAndActivoTrue(authentication.getName()).get();
        if (usuario.getActivo()) {
            String dateNow = ToolHelper.getYearNow() + "-" + ToolHelper.getNumberMonthNow() + "-" + ToolHelper.getDayNow();
            Timestamp startDate = ToolHelper.castDateTime(dateNow + " 00:00:00");
            Timestamp endDate = ToolHelper.castDateTime(dateNow + " 23:59:59");
        }
    }

    @Override
    public void publishAuthenticationFailure(AuthenticationException exception, Authentication authentication) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
