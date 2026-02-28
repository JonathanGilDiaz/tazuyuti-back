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
import org.springframework.web.bind.annotation.RequestParam;
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

    @PostMapping(value = "/save", consumes = { "application/xml", "application/json" })
    public ResponseEntity<Response> save(@Validated(onCreate.class) @RequestBody User usuario,
            HttpServletRequest request) {
        return usuarioService.save(usuario, request);
    }

    @PostMapping(value = "/active", consumes = { "application/xml", "application/json" })
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Response> active(@Validated @RequestBody UserActive usuarioActive,
            HttpServletRequest request) {
        User usuario = new User();
        usuario.setId(usuarioActive.getId());
        usuario.setActivo(usuarioActive.getActivo());
        return usuarioService.active(usuario, request);
    }

    @PostMapping(value = "", consumes = { "application/xml", "application/json" })
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Response> index(@Validated @RequestBody Pagination request) {
        if (request.getPage() == 0 || request.getSize() == 0 || request.getSort() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Response(false, SystemText.General.FALTA_INFORMACION, null));
        }
        String message = Utils.validateFilteringInformation(SystemText.User.OPCIONES_VALIDAS_PAGINACION,
                request.getSort(), request.getFilters());
        if (message.equals("")) {
            return usuarioService.index(request);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(false, message, null));
        }
    }

    @GetMapping(value = "/catalogs")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Response> catalogs() {
        return usuarioService.catalogs();
    }

    @GetMapping(value = "/{id}/detail")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Response> detail(@PathVariable(name = "id", required = true) int id) {
        return usuarioService.detail(id);
    }

    @GetMapping(value = "/{id}/detailTransferencias")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Response> detailTransferencias(
            @PathVariable int id,
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin) {
        return usuarioService.detailTransferencias(id, fechaInicio, fechaFin);
    }

    @PostMapping(value = "/update", consumes = { "application/xml", "application/json" })
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Response> update(@Validated(onUpdate.class) @RequestBody User usuario,
            HttpServletRequest request) {
        return usuarioService.update(usuario, request);
    }

}
