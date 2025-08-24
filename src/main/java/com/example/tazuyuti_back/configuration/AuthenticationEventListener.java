/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Feb 2025
 * @date 23/02/2025
 */
package com.example.tazuyuti_back.configuration;

import java.sql.Timestamp;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import com.example.tazuyuti_back.entities.administration.SessionAttempt;
import com.example.tazuyuti_back.entities.administration.User;
import com.example.tazuyuti_back.helpers.SystemText;
import com.example.tazuyuti_back.helpers.ToolHelper;
import com.example.tazuyuti_back.services.administration.SessionAttemptService;
import com.example.tazuyuti_back.services.administration.UserService;

@Component("authenticationEventListner")
public class AuthenticationEventListener implements AuthenticationEventPublisher {

    private static final int MAX_ATTEMPTS = 4;
    @Autowired
    private UserService usuarioService;

    @Autowired
    private SessionAttemptService intentoSessionService;

    @Override
    public void publishAuthenticationSuccess(Authentication authentication) {
        User usuario = usuarioService.findFirstByUsuarioAndActivoTrue(authentication.getName()).get();
        if (usuario.getActivo()) {
            String dateNow = ToolHelper.getYearNow() + "-" + ToolHelper.getNumberMonthNow() + "-" + ToolHelper.getDayNow();
            Timestamp startDate = ToolHelper.castDateTime(dateNow + " 00:00:00");
            Timestamp endDate = ToolHelper.castDateTime(dateNow + " 23:59:59");
            intentoSessionService.deleteByUsuarioAndFechaBetween(usuario, startDate, endDate);
        }
    }
    
    @Override
    public void publishAuthenticationFailure(AuthenticationException exception, Authentication authentication) {
        Optional<User> user = usuarioService.findFirstByUsuarioOrderByIdDesc(authentication.getName());
        if (user.isPresent()) {
			User usuario = user.get();
			if (usuario.getActivo()){
				String dateNow = ToolHelper.getYearNow() + "-" + ToolHelper.getNumberMonthNow() + "-" + ToolHelper.getDayNow();
				Timestamp startDate = ToolHelper.castDateTime(dateNow + " 00:00:00");
				Timestamp endDate = ToolHelper.castDateTime(dateNow + " 23:59:59");
				int counterUserAttempt = intentoSessionService.countByUsuarioAndFechaBetween(usuario, startDate, endDate);
				if (counterUserAttempt >= MAX_ATTEMPTS) {
					usuarioService.activeUser(false, usuario.getId());
					String error = SystemText.Login.INACTIVACION_CUENTA_USUARIO(usuario.getUsuario());
					throw new LockedException(error);
				}
				SessionAttempt intentoSession = SessionAttempt.builder().usuario(usuario).build();
				intentoSessionService.save(intentoSession);
			} else {
				throw new LockedException(SystemText.Login.CREDENCIALES_INVALIDAS_COMUNIQUESE_ADMINISTRADOR);
			}
        } else {
            throw new LockedException(SystemText.Login.CREDENCIALES_INVALIDAS);
        }
    }

}
