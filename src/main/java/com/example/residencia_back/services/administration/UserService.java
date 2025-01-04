/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 04 Ene 2025
 * @date 04/01/2025
 */
package com.example.residencia_back.services.administration;

import com.example.residencia_back.entities.administration.User;
import java.util.Optional;

public interface UserService {

    Optional<User> findFirstByUsuarioAndActivoTrue(String usuario);

}
