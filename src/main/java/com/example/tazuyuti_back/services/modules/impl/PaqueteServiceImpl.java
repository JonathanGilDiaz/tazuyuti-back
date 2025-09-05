/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Feb 2025
 * @date 23/02/2025
 */
package com.example.tazuyuti_back.services.modules.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.example.tazuyuti_back.entities.administration.User;
import com.example.tazuyuti_back.entities.catalogs.EstadoPaquete;
import com.example.tazuyuti_back.entities.modules.Paquete;
import com.example.tazuyuti_back.entities.modules.Venta;
import com.example.tazuyuti_back.helpers.DocumentHelper;
import com.example.tazuyuti_back.helpers.SystemText;
import com.example.tazuyuti_back.helpers.Utils;
import com.example.tazuyuti_back.models.utilities.Pagination;
import com.example.tazuyuti_back.models.utilities.Response;
import com.example.tazuyuti_back.repositories.administration.UserRepository;
import com.example.tazuyuti_back.repositories.catalogs.SucursalRepository;
import com.example.tazuyuti_back.repositories.modules.PaqueteRepository;
import com.example.tazuyuti_back.repositories.modules.PrecioPaqueteriaRepository;
import com.example.tazuyuti_back.services.modules.PaqueteService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@Service
public class PaqueteServiceImpl implements PaqueteService {

    @Autowired
    private PaqueteRepository paqueteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DocumentHelper documentHelper;

    @Autowired
    private PrecioPaqueteriaRepository precioPRepository;

    @Autowired
    private SucursalRepository sucursalRepository;

    @Override
    @Transactional
    public ResponseEntity<Response> save(Paquete paquete, HttpServletRequest request) {
        Optional<User> userOp = userRepository.findById(paquete.getUsuario().getId());
        if (!userOp.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, SystemText.General.REGISTRO_NO_ENCONTRADO, null));

        }
        User usuarioCompleto = userOp.get();
        paquete.setUsuario(userOp.get());
        String nombreSucursal = usuarioCompleto.getSucursal().getNombre();
        String letraSucursal = nombreSucursal.substring(0, 1).toUpperCase();
        long conteo = paqueteRepository.countByUsuario_Sucursal_Id(usuarioCompleto.getSucursal().getId());
        String folio = letraSucursal + (conteo + 1);
        paquete.setFolio(folio);
        if (paquete.getDetallePaquete() != null) {
            paquete.getDetallePaquete().forEach(det -> det.setPaquete(paquete));
        }
        paquete.setFechaCreacion(Timestamp.valueOf(LocalDateTime.now()));
        paquete.setEstado(new EstadoPaquete(1,"Recibido"));
        Paquete paqueteGuardado = paqueteRepository.save(paquete);
        Map<String, Object> data = new HashMap<>();
        data.put("idPaquete", paqueteGuardado.getId());
        data.put("folio", paqueteGuardado.getFolio());
        data.put("total", paqueteGuardado.getTotal());

        return ResponseEntity.status(HttpStatus.OK)
                .body(new Response(true, SystemText.General.PROCESO_EXITOSO, data));
    }

    @SuppressWarnings("unchecked")
    @Override
    public ResponseEntity<Response> index(int idUsuario, Pagination requestT) {
        User usuario = userRepository.findById(idUsuario).get();
        int sucursalId = usuario.getSucursal().getId();
        Map<String, Object> data = Utils.getSpecificationAndPageable(requestT, Paquete.class);
        Specification<Paquete> specs = (Specification<Paquete>) data.get("specification");
        Specification<Paquete> filtroSucursal = (root, query, cb) -> cb
                .equal(root.get("usuario").get("sucursal").get("id"), sucursalId);
        Specification<Paquete> finalSpecs = specs == null ? filtroSucursal : specs.and(filtroSucursal);
        Page<Paquete> list = paqueteRepository.findAll(finalSpecs, (Pageable) data.get("pageable"));
        if (list.hasContent()) {
            return ResponseEntity.ok(new Response(true, SystemText.General.PROCESO_EXITOSO, list));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, SystemText.General.REGISTRO_NO_ENCONTRADO, null));
        }
    }

    @Override
    public ResponseEntity<Response> catalogs() {
        Map<String, Object> response = new HashMap<>();
        response.put("precios", precioPRepository.findByEstadoTrue());
        response.put("sucursal", sucursalRepository.findAll());
        return ResponseEntity.status(HttpStatus.OK)
                .body(new Response(true, SystemText.General.PROCESO_EXITOSO, response));
    }
}
