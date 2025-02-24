/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Feb 2025
 * @date 23/02/2025
 */
package com.example.residencia_back.services.administration.impl;

import org.springframework.stereotype.Service;

import com.example.residencia_back.entities.administration.User;
import com.example.residencia_back.helpers.SystemText;
import com.example.residencia_back.helpers.ToolHelper;
import com.example.residencia_back.models.utilities.Response;
import com.example.residencia_back.repositories.administration.SessionAttemptRepository;
import com.example.residencia_back.repositories.administration.UserRepository;
import com.example.residencia_back.services.administration.UserService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Implementation of UsuarioService to manage user operations.
 */

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository usuarioRepo;

    @Autowired
    private SessionAttemptRepository intentoSessionRepo;

    @Override
    public ResponseEntity<Response> save(User usuario, HttpServletRequest request) {
        String plainPassword = usuario.getContrasenia();
        if (plainPassword == null || plainPassword.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Response(false, SystemText.User.CONTRASEÑA_VACIA, null));
        }
        String encodedPassword = new BCryptPasswordEncoder().encode(plainPassword);
        usuario.setContrasenia(encodedPassword);
        usuario.setFecha_creacion(Timestamp.valueOf(LocalDateTime.now()));
        usuario = usuarioRepo.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new Response(true, SystemText.General.REGISTRO_CREADO_CORRECTAMENTE, null));
    }
    

    @Override
    public ResponseEntity<Response> active(User usuario, HttpServletRequest request) {
        try {
            Optional<User> user = usuarioRepo.findById(usuario.getId());
            if (user.isPresent()) {
                usuarioRepo.setActivoForUsuario(usuario.getActivo(), usuario.getId());
                if (usuario.getActivo()) {
                    String dateNow = ToolHelper.getYearNow() + "-" + ToolHelper.getNumberMonthNow() + "-" + ToolHelper.getDayNow();
                    Timestamp startDate = ToolHelper.castDateTime(dateNow + " 00:00:00");
                    Timestamp endDate = ToolHelper.castDateTime(dateNow + " 23:59:59");
                    intentoSessionRepo.deleteByUsuarioAndFechaBetween(usuario, startDate, endDate);
                }
                return ResponseEntity.status(HttpStatus.OK).body(new Response(true, SystemText.General.PROCESO_EXITOSO, null));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(false, SystemText.General.REGISTRO_NO_ENCONTRADO, null));
            }
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response(false, SystemText.General.ERROR_PROCESO, null));
        }
    }    

    @Override
    public Optional<User> findByUsuario(String usuario) {
        return usuarioRepo.findByUsuario(usuario);
    }

    @Override
    public Optional<User> findFirstByUsuarioAndActivoTrue(String usuario) {
        return usuarioRepo.findFirstByUsuarioAndActivoTrue(usuario);
    }

    @Override
    public Optional<User> findFirstByUsuarioOrderByIdDesc(String usuario) {
        return usuarioRepo.findFirstByUsuarioOrderByIdDesc(usuario);
    }

    @Override
    public void activeUser(boolean activo, int id) {
        usuarioRepo.setActivoForUsuario(activo, id);
    }

    
}
