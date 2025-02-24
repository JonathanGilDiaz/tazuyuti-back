/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Feb 2025
 * @date 23/02/2025
 */
package com.example.residencia_back.controllers.administration;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.residencia_back.entities.administration.User;
import com.example.residencia_back.entities.administration.UserActive;
import com.example.residencia_back.models.utilities.Response;
import com.example.residencia_back.services.administration.UserService;
import com.example.residencia_back.validator.onCreate;

@RestController
@RequestMapping("/api/usuarios")
public class UserController {

    @Autowired
    private UserService usuarioService;

    /**
    * Saves a new user to the system with the provided user details.
    *
    * @param usuario Contains the details of the user to be created.
    *                - nombre: The first name of the user (required, 5-40 characters).
    *                - apellidoPaterno: The user's paternal surname (required, 5-40 characters).
    *                - apellidoMaterno: The user's maternal surname (optional, 5-40 characters).
    *                - correo: The email address of the user.
    *                - contrasenia: The user's password (hidden for security).
    *                - cargo: The user's job title or role in the company (required, 5-50 characters).
    *                - telefono: The user's phone number (required, 7-10 characters).
    *                - extension: The user's phone extension (optional, 4-6 characters).
    *                - rol: The user's role in the system (required).
    *                - activo: Indicates if the user is active or not.
    * @return Response indicating whether the user was successfully saved, including a message and any relevant data.
    */
    @PostMapping(value = "/save", consumes = { "application/xml", "application/json" })
    public ResponseEntity<Response> save(@Validated(onCreate.class) @RequestBody User usuario, HttpServletRequest request) {
        return usuarioService.save(usuario, request);
    }

    /**
    * Activates or deactivates a user in the system based on their current status.
    *
    * @param usuario Contains the details of the user whose active status is being changed.
    *                - id: The unique identifier of the user (required).
    *                - activo: The current active status of the user. Set to true to activate or false to deactivate.
    * @return Response indicating whether the operation to change the user's active status was successful, including a message and any relevant data.
    */
    @PostMapping(value = "/active", consumes = { "application/xml", "application/json" })
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Response> active(@Validated @RequestBody UserActive usuarioActive, HttpServletRequest request) {
        User usuario = new User();
        usuario.setId(usuarioActive.getId());
        usuario.setActivo(usuarioActive.getActivo());
        return usuarioService.active(usuario, request);
    }

}
