/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Feb 2025
 * @date 23/02/2025
 */
package com.example.tazuyuti_back.controllers.administration;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tazuyuti_back.entities.administration.User;
import com.example.tazuyuti_back.entities.administration.UserActive;
import com.example.tazuyuti_back.helpers.SystemText;
import com.example.tazuyuti_back.helpers.Utils;
import com.example.tazuyuti_back.models.utilities.Pagination;
import com.example.tazuyuti_back.models.utilities.Response;
import com.example.tazuyuti_back.services.administration.UserService;
import com.example.tazuyuti_back.validator.onCreate;
import com.example.tazuyuti_back.validator.onUpdate;

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

    /**
    * Retrieves a paginated list of items based on the given pagination and filtering parameters.
    *
    * @param request Contains pagination and sorting details for the request.
    *                - page: The number of the page to retrieve (required).
    *                - size: The number of items per page (default is 50).
    *                - sort: The field by which the results will be sorted.
    *                - filers: A search query to filter the results based on a specific term.
    * @return Response containing the paginated data and relevant metadata.
    */
    @PostMapping(value = "", consumes = { "application/xml", "application/json" })
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Response> index(@Validated @RequestBody Pagination request) {
        if (request.getPage() == 0 || request.getSize() == 0 || request.getSort() == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(false, SystemText.General.FALTA_INFORMACION, null));
        }
        String message = Utils.validateFilteringInformation(SystemText.User.OPCIONES_VALIDAS_PAGINACION, request.getSort(), request.getFilters());
        if (message.equals("")){
            return usuarioService.index(request);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(false, message, null));
        }
    }

     /**
    * Retrieves a list of catalog data used for populating selection options in the user interface.
    *
    * This method fetches all available `Dependencia` and `Rol` records for use in dropdown menus or selection lists
    * in the application, ensuring users have the latest options available.
    *
    * @return Response containing a map with lists of available `Dependencia` and `Rol` entities:
    *         - dependencias: List of all `Dependencia` entities.
    *         - roles: List of all `Rol` entities.
    */
    @GetMapping(value = "/catalogs")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Response> catalogs() {
        return usuarioService.catalogs();
    }

     /**
    * Retrieves the detailed information of a specific user based on their ID.
    *
    * @param id The unique identifier of the user whose details are being requested (required).
    * @return Response containing the detailed information of the specified user, or an error message if the user is not found.
    */
    @GetMapping(value = "/{id}/detail")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Response> detail(@PathVariable(name = "id", required = true) int id) {
        return usuarioService.detail(id);
    }

    /**
    * Updates the details of an existing user in the system.
    *
    * @param usuario Contains the updated details of the user.
    *                - id: The unique identifier of the user (required for updates).
    *                - nombre: The first name of the user (required, 5-40 characters).
    *                - apellidoPaterno: The user's paternal surname (required, 5-40 characters).
    *                - apellidoMaterno: The user's maternal surname (optional, 5-40 characters).
    *                - correo: The email address of the user (required, must be valid and unique).
    *                - contrasenia: The user's password (hidden for security).
    *                - cargo: The user's job title or role in the company (required, 5-50 characters).
    *                - telefono: The user's phone number (required, 7-10 characters).
    *                - extension: The user's phone extension (optional, 4-6 characters).
    *                - rol: The user's role in the system (required).
    *                - activo: Indicates if the user is active or not.
    * @return Response indicating whether the update was successful, including a message and any relevant data.
    */
    @PostMapping(value = "/update", consumes = { "application/xml", "application/json" })
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Response> update(@Validated(onUpdate.class) @RequestBody User usuario, HttpServletRequest request) {
        return usuarioService.update(usuario, request);
    }

}
