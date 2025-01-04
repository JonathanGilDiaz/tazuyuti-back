/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 04 Ene 2025
 * @date 04/01/2025
 */
package com.example.residencia_back.services.administration.impl;

import com.example.residencia_back.entities.administration.User;
import com.example.residencia_back.repositories.administration.UserRepository;
import com.example.residencia_back.services.administration.UserService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of UsuarioService to manage user operations.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository usuarioRepo;

    @Override
    public Optional<User> findFirstByUsuarioAndActivoTrue(String usuario) {
        return usuarioRepo.findFirstByUsuarioAndActivoTrue(usuario);
    }
}
