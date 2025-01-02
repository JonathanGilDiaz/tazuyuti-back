/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 02 Ene 2025
 * @date 02/01/2025
 */
package com.example.residencia_back.services.administration.impl;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.residencia_back.entities.administration.User;
import com.example.residencia_back.models.administration.UserDetail;
import com.example.residencia_back.repositories.administration.UserRepository;

/**
 * Service implementation for loading user details.
 *
 * This class implements the UserDetailsService interface to provide user
 * authentication functionality by loading user-specific data. It retrieves user
 * information from the UsuarioRepository based on the provided username
 * (email).
 */
@Service
public class UserDetailServiceImpl {

    @Autowired
    private UserRepository usuarioRepository;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> usuario = usuarioRepository.findFirstByUsuarioAndActivoTrue(username);
        if (usuario.isPresent()) {
            return new UserDetail(usuario.get());
        } else {
            return null;
        }
    }
}
