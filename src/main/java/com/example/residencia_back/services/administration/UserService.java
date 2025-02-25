/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Feb 2025
 * @date 23/02/2025
 */
package com.example.residencia_back.services.administration;

import com.example.residencia_back.entities.administration.User;
import com.example.residencia_back.models.utilities.Pagination;
import com.example.residencia_back.models.utilities.Response;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

import org.springframework.http.ResponseEntity;

public interface UserService {

    Optional<User> findByUsuario(String usuario);

    Optional<User> findFirstByUsuarioAndActivoTrue(String usuario);

	Optional<User> findFirstByUsuarioOrderByIdDesc(String usuario);

    ResponseEntity<Response> active(User usuario, HttpServletRequest request);

    ResponseEntity<Response> save(User usuario, HttpServletRequest request);
    
    void activeUser(boolean activo, int id);
    
    ResponseEntity<Response> index(Pagination request);
    
    ResponseEntity<Response> catalogs();
    
    ResponseEntity<Response> detail(int id);

    ResponseEntity<Response> update(User usuario, HttpServletRequest request);

}    
