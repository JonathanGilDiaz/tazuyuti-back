/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Feb 2025
 * @date 23/02/2025
 */
package com.example.tazuyuti_back.services.administration.impl;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.tazuyuti_back.entities.administration.User;
import com.example.tazuyuti_back.models.administration.UserDetail;
import com.example.tazuyuti_back.repositories.administration.UserRepository;

/**
 * Service implementation for loading user details.
 *
 * This class implements the UserDetailsService interface to provide 
 * user authentication functionality by loading user-specific data.
 */

 @Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> usuario = usuarioRepository.findFirstByUsuarioAndActivoTrue(username);
        if (usuario.isPresent()) {
            return new UserDetail(usuario.get());
        } else {
            return null;
        }
    }
}
