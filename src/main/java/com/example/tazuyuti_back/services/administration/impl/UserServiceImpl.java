/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Feb 2025
 * @date 23/02/2025
 */
package com.example.tazuyuti_back.services.administration.impl;

import org.springframework.stereotype.Service;
import com.example.tazuyuti_back.entities.administration.User;
import com.example.tazuyuti_back.entities.modules.Boleto;
import com.example.tazuyuti_back.entities.modules.Unidad;
import com.example.tazuyuti_back.helpers.SystemText;
import com.example.tazuyuti_back.helpers.ToolHelper;
import com.example.tazuyuti_back.helpers.Utils;
import com.example.tazuyuti_back.models.utilities.Pagination;
import com.example.tazuyuti_back.models.utilities.Response;
import com.example.tazuyuti_back.repositories.administration.SessionAttemptRepository;
import com.example.tazuyuti_back.repositories.administration.UserRepository;
import com.example.tazuyuti_back.repositories.catalogs.RoleRepository;
import com.example.tazuyuti_back.repositories.catalogs.SucursalRepository;
import com.example.tazuyuti_back.repositories.modules.BoletoRepository;
import com.example.tazuyuti_back.repositories.modules.UnidadRepository;
import com.example.tazuyuti_back.services.administration.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.sql.Timestamp;
import java.time.LocalDate;
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

    @Autowired
    private SucursalRepository sucursalRepo;

    @Autowired
    private UnidadRepository unidadRepository;

    @Autowired
    private BoletoRepository boletoRepository;

    @Override
    public ResponseEntity<Response> save(User usuario, HttpServletRequest request) {
        String plainPassword = usuario.getContrasenia();
        if (plainPassword == null || plainPassword.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Response(false, SystemText.User.CONTRASEÑA_VACIA, null));
        }
        String encodedPassword = new BCryptPasswordEncoder().encode(plainPassword);
        usuario.setContrasenia(encodedPassword);
        usuario.setUsuario(usuario.getNombre());
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
                    String dateNow = ToolHelper.getYearNow() + "-" + ToolHelper.getNumberMonthNow() + "-"
                            + ToolHelper.getDayNow();
                    Timestamp startDate = ToolHelper.castDateTime(dateNow + " 00:00:00");
                    Timestamp endDate = ToolHelper.castDateTime(dateNow + " 23:59:59");
                    intentoSessionRepo.deleteByUsuarioAndFechaBetween(usuario, startDate, endDate);
                }
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new Response(true, SystemText.General.PROCESO_EXITOSO, null));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Response(false, SystemText.General.REGISTRO_NO_ENCONTRADO, null));
            }
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new Response(false, SystemText.General.ERROR_PROCESO, null));
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

    @SuppressWarnings({ "unchecked" })
    @Override
    public ResponseEntity<Response> index(Pagination request) {
        Pagination requestT = new Pagination(request.getPage(), request.getSize(), request.getSort(),
                request.getFilters());
        Map<String, Object> data = Utils.getSpecificationAndPageable(requestT, User.class);
        Page<User> list = usuarioRepo.findAll((Specification<User>) data.get("specification"),
                (Pageable) data.get("pageable"));
        if (list.getContent().size() > 0) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new Response(true, SystemText.General.PROCESO_EXITOSO, list));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, SystemText.General.PROCESO_EXITOSO, null));
        }
    }

    @Override
    public ResponseEntity<Response> catalogs() {
        Map<String, Object> response = new HashMap<>();
        response.put("roles", rolRepo.findByActivoTrue());
        response.put("sucursales", sucursalRepo.findAll());
        return ResponseEntity.status(HttpStatus.OK)
                .body(new Response(true, SystemText.General.PROCESO_EXITOSO, response));
    }

    @Override
    public ResponseEntity<Response> detail(int id) {
        Optional<User> usuario = usuarioRepo.findById(id);
        if (usuario.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new Response(true, SystemText.General.REGISTRO_ENCONTRADO, usuario.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, SystemText.General.REGISTRO_ENCONTRADO, null));
        }

    }

    @Override
    public ResponseEntity<Response> detailTransferencias(
            int id, String fechaInicioStr, String fechaFinStr) {
        Optional<User> usuarioOp = usuarioRepo.findById(id);
        if (usuarioOp.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, "Usuario no encontrado", null));
        }
        User usuario = usuarioOp.get();
        List<Unidad> unidadList = unidadRepository.findByUsuarioId(id);
        if (unidadList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, "El chofer no cuenta con unidades asignadas", null));
        }
        Unidad unidad = unidadList.get(0);
        LocalDate fechaInicio;
        LocalDate fechaFin;
        try {
            fechaInicio = LocalDate.parse(fechaInicioStr);
            fechaFin = LocalDate.parse(fechaFinStr);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new Response(false, "Formato de fecha inválido. Use yyyy-MM-dd", null));
        }
        if (fechaInicio.isAfter(fechaFin)) {
            return ResponseEntity.badRequest()
                    .body(new Response(false, "La fecha de inicio no puede ser mayor que la fecha final", null));
        }
        Timestamp inicio = Timestamp.valueOf(fechaInicio.atStartOfDay());
        Timestamp fin = Timestamp.valueOf(fechaFin.atTime(23, 59, 59));
        List<Boleto> boletos = boletoRepository
                .findTransferenciasByUnidadAndFecha(unidad.getId(), inicio, fin);
        double totalGeneral = boletos.stream()
                .mapToDouble(Boleto::getTotal)
                .sum();
        Map<String, Object> data = new HashMap<>();
        data.put("usuario", usuario);
        data.put("unidad", unidad);
        data.put("boletos", boletos);
        data.put("totalGeneral", totalGeneral);
        data.put("cantidad", boletos.size());
        return ResponseEntity.ok(
                new Response(true, "Registros encontrados", data));
    }

    @Override
    public ResponseEntity<Response> update(User usuario, HttpServletRequest request) {
        int counterMails = usuarioRepo.countByUsuarioIgnoringCaseAndActivoTrueAndIdNot(usuario.getUsuario(),
                usuario.getId());
        if (counterMails > 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new Response(false, SystemText.User.EXISTE_USUARIO_SISTEMA, null));
        }
        Optional<User> user = usuarioRepo.findById(usuario.getId());
        if (user.isPresent()) {
            User userToUpdate = user.get();
            if (userToUpdate.getRol().getId() == 5 && usuario.getRol().getId() != 5) {
                List<Unidad> unidadOp = unidadRepository.findByUsuarioId(userToUpdate.getId());
                if (!unidadOp.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(new Response(false,
                                    "No es posible modificar el rol del chofer, aun pertenece a una unidad", null));
                }
            }
            Timestamp currentDate = ToolHelper.castDateTime(ToolHelper.getCurrentDateTime());
            userToUpdate.setNombre(usuario.getNombre());
            userToUpdate.setUsuario(usuario.getNombre());
            userToUpdate.setRol(usuario.getRol());
            userToUpdate.setActivo(usuario.getActivo());
            userToUpdate.setSucursal(usuario.getSucursal());
            userToUpdate.setFecha_actualizacion(currentDate);
            if (!usuario.getContrasenia().equals("")) {
                String encodedPassword = new BCryptPasswordEncoder().encode(usuario.getContrasenia());
                userToUpdate.setContrasenia(encodedPassword);
            }
            usuario = usuarioRepo.save(userToUpdate);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new Response(true, SystemText.General.REGISTRO_ACTUALIZADO_CORRECTAMENTE, null));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, SystemText.User.EXISTE_USUARIO_SISTEMA, null));
        }
    }
}
