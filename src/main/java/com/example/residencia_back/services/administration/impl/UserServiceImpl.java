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
import com.example.residencia_back.helpers.Utils;
import com.example.residencia_back.models.utilities.Pagination;
import com.example.residencia_back.models.utilities.Response;
import com.example.residencia_back.repositories.administration.SessionAttemptRepository;
import com.example.residencia_back.repositories.administration.UserRepository;
import com.example.residencia_back.repositories.catalogs.RoleRepository;
import com.example.residencia_back.services.administration.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
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

    @Autowired
    private RoleRepository rolRepo;

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

    @SuppressWarnings({ "unchecked"})
    @Override
    public ResponseEntity<Response> index(Pagination request) {
        Pagination requestT = new Pagination(request.getPage(), request.getSize(), request.getSort(), request.getFilters());
        Map<String, Object> data = Utils.getSpecificationAndPageable(requestT, User.class);
        Page<User> list = usuarioRepo.findAll((Specification<User>) data.get("specification"), (Pageable)data.get("pageable"));
        if (list.getContent().size() > 0){
            return ResponseEntity.status(HttpStatus.OK).body(new Response(true, SystemText.General.PROCESO_EXITOSO, list));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(false, SystemText.General.PROCESO_EXITOSO, null));
        }
    }

    @Override
    public ResponseEntity<Response> catalogs() {
        Map<String, Object> response = new HashMap<>();
        response.put("roles", rolRepo.findByActivoTrue());
        return ResponseEntity.status(HttpStatus.OK).body(new Response(true, SystemText.General.PROCESO_EXITOSO, response));
    }

    @Override
    public ResponseEntity<Response> detail(int id) {
        Optional<User> usuario = usuarioRepo.findById(id);
        if (usuario.isPresent()){
            return ResponseEntity.status(HttpStatus.OK)
                .body(new Response(true, SystemText.General.REGISTRO_ENCONTRADO, usuario.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(false, SystemText.General.REGISTRO_ENCONTRADO, null));
        }

    }

    @Override
    public ResponseEntity<Response> update(User usuario, HttpServletRequest request) {
        int counterMails = usuarioRepo.countByUsuarioIgnoringCaseAndActivoTrueAndIdNot(usuario.getUsuario(), usuario.getId());
        if (counterMails > 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response(false, SystemText.User.EXISTE_USUARIO_SISTEMA, null));
        }

        Optional<User> user = usuarioRepo.findById(usuario.getId());
        if (user.isPresent()) {
            User userToUpdate = user.get();
            Timestamp currentDate = ToolHelper.castDateTime(ToolHelper.getCurrentDateTime());
            userToUpdate.setNombre(usuario.getNombre());
            userToUpdate.setApellidoPaterno(usuario.getApellidoPaterno());
            userToUpdate.setApellidoMaterno(usuario.getApellidoMaterno());
            userToUpdate.setCargo(usuario.getCargo());
            userToUpdate.setTelefono(usuario.getTelefono());
            userToUpdate.setExtension(usuario.getExtension());
            userToUpdate.setCelular(usuario.getCelular());
            userToUpdate.setCorreoPersonal(usuario.getCorreoPersonal());
            userToUpdate.setUsuario(usuario.getUsuario());
            userToUpdate.setRol(usuario.getRol());
            userToUpdate.setActivo(usuario.getActivo());
            userToUpdate.setFecha_actualizacion(currentDate);
            if(!usuario.getContrasenia().equals("")){
                String encodedPassword = new BCryptPasswordEncoder().encode(usuario.getContrasenia());
                userToUpdate.setContrasenia(encodedPassword);
            }
            usuario = usuarioRepo.save(userToUpdate);
            return ResponseEntity.status(HttpStatus.OK).body(new Response(true, SystemText.General.REGISTRO_ACTUALIZADO_CORRECTAMENTE, null));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(false, SystemText.User.EXISTE_USUARIO_SISTEMA, null));
        }
    }
}
