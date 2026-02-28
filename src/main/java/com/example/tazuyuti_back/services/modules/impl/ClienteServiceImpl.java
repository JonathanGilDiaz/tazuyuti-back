/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Feb 2025
 * @date 23/02/2025
 */
package com.example.tazuyuti_back.services.modules.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.example.tazuyuti_back.entities.modules.Cliente;
import com.example.tazuyuti_back.helpers.SystemText;
import com.example.tazuyuti_back.helpers.Utils;
import com.example.tazuyuti_back.models.utilities.Pagination;
import com.example.tazuyuti_back.models.utilities.Response;
import com.example.tazuyuti_back.repositories.modules.ClienteRepository;
import com.example.tazuyuti_back.services.modules.ClienteService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@Service
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    @Transactional
    public ResponseEntity<Response> save(Cliente cliente, HttpServletRequest request) {
        cliente.setFechaCreacion(Timestamp.valueOf(LocalDateTime.now()));
        cliente.setFechaActualizacion(Timestamp.valueOf(LocalDateTime.now()));
        cliente.setEstado(true);
        clienteRepository.save(cliente);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new Response(true, SystemText.General.PROCESO_EXITOSO, null));
    }

    @Override
    public ResponseEntity<Response> getAll() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new Response(true, SystemText.General.PROCESO_EXITOSO, clienteRepository.findByEstadoTrue()));
    }

    @SuppressWarnings("unchecked")
    @Override
    public ResponseEntity<Response> index(Pagination requestT) {
        Map<String, Object> data = Utils.getSpecificationAndPageable(requestT, Cliente.class);
        Specification<Cliente> specs = (Specification<Cliente>) data.get("specification");
        Specification<Cliente> estadoTrue = (root, query, cb) -> cb.isTrue(root.get("estado"));
        Specification<Cliente> finalSpec = specs.and(estadoTrue);
        Page<Cliente> list = clienteRepository.findAll(finalSpec, (Pageable) data.get("pageable"));
        if (list.hasContent()) {
            return ResponseEntity.ok(new Response(true, SystemText.General.PROCESO_EXITOSO, list));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, SystemText.General.REGISTRO_NO_ENCONTRADO, null));
        }
    }

    @Override
    @Transactional
    public ResponseEntity<Response> update(Cliente cliente, HttpServletRequest request) {
        Optional<Cliente> clienteOp = clienteRepository.findById(cliente.getId());
        if (!clienteOp.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, SystemText.General.REGISTRO_NO_ENCONTRADO, null));
        }
        cliente.setFechaCreacion(clienteOp.get().getFechaCreacion());
        cliente.setFechaActualizacion(Timestamp.valueOf(LocalDateTime.now()));
        cliente.setEstado(true);
        clienteRepository.save(cliente);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new Response(true, SystemText.General.PROCESO_EXITOSO, null));
    }

    @Override
    @Transactional
    public ResponseEntity<Response> delete(int id, HttpServletRequest request) {
        try {
            Optional<Cliente> clienteId = clienteRepository.findById(id);
            if (!clienteId.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Response(false, SystemText.General.REGISTRO_NO_ENCONTRADO, null));
            }
            try {
                clienteRepository.deleteById(id);
            } catch (Exception e) {
                Cliente cliente = clienteId.get();
                cliente.setEstado(false);
                clienteRepository.save(cliente);
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new Response(true, SystemText.General.REGISTRO_ELIMINADO_CORRECTAMENTE, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(false, SystemText.General.NO_ELIMINAR_REGISTRO_ASOCIADO, null));
        }

    }

    @Override
    public ResponseEntity<Response> detail(int id) {
        Optional<Cliente> item = clienteRepository.findById(id);
        if (item.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new Response(true, SystemText.General.REGISTRO_ENCONTRADO, item.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, SystemText.General.REGISTRO_ENCONTRADO, null));
        }
    }

}
