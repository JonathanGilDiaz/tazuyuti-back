/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Feb 2025
 * @date 23/02/2025
 */
package com.example.tazuyuti_back.services.modules.impl;

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
import com.example.tazuyuti_back.entities.modules.Unidad;
import com.example.tazuyuti_back.helpers.SystemText;
import com.example.tazuyuti_back.helpers.Utils;
import com.example.tazuyuti_back.models.utilities.Pagination;
import com.example.tazuyuti_back.models.utilities.Response;
import com.example.tazuyuti_back.repositories.catalogs.TipoCamionetaRepository;
import com.example.tazuyuti_back.repositories.modules.UnidadRepository;
import com.example.tazuyuti_back.services.modules.UnidadesService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@Service
public class UnidadServiceImpl implements UnidadesService {

    @Autowired
    private UnidadRepository repository;

    @Autowired TipoCamionetaRepository tipoCamionetaRepository;

    @Override
    @Transactional
    public ResponseEntity<Response> save(Unidad unidad, HttpServletRequest request) {
        unidad.setEstado(true);
        repository.save(unidad);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new Response(true, SystemText.General.PROCESO_EXITOSO, null));
    }

        @Override
    public ResponseEntity<Response> catalogs() {
        Map<String, Object> response = new HashMap<>();
        response.put("tipo", tipoCamionetaRepository.findAll());
        return ResponseEntity.status(HttpStatus.OK).body(new Response(true, SystemText.General.PROCESO_EXITOSO, response));
    }


    @SuppressWarnings("unchecked")
    @Override
    public ResponseEntity<Response> index(Pagination requestT) {
        Map<String, Object> data = Utils.getSpecificationAndPageable(requestT, Unidad.class);
        Specification<Unidad> specs = (Specification<Unidad>) data.get("specification");
        Specification<Unidad> estadoTrue = (root, query, cb) -> cb.isTrue(root.get("estado"));
        Specification<Unidad> finalSpec = specs.and(estadoTrue);
        Page<Unidad> list = repository.findAll(finalSpec, (Pageable) data.get("pageable"));
        if (list.hasContent()) {
            return ResponseEntity.ok(new Response(true, SystemText.General.PROCESO_EXITOSO, list));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, SystemText.General.REGISTRO_NO_ENCONTRADO, null));
        }
    }

    @Override
    @Transactional
    public ResponseEntity<Response> update(Unidad unidad, HttpServletRequest request) {
        Optional<Unidad> itemOp = repository.findById(unidad.getId());
        if (!itemOp.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, SystemText.General.REGISTRO_NO_ENCONTRADO, null));
        }
        unidad.setEstado(true);
        repository.save(unidad);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new Response(true, SystemText.General.PROCESO_EXITOSO, null));
    }

    @Override
    @Transactional
    public ResponseEntity<Response> delete(int id, HttpServletRequest request) {
        try {
            Optional<Unidad> itemOp = repository.findById(id);
            if (!itemOp.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Response(false, SystemText.General.REGISTRO_NO_ENCONTRADO, null));
            }
            try {
                repository.deleteById(id);
            } catch (Exception e) {
                Unidad item = itemOp.get();
                item.setEstado(false);
                repository.save(item);
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
        Optional<Unidad> item = repository.findById(id);
        if (item.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new Response(true, SystemText.General.REGISTRO_ENCONTRADO, item.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, SystemText.General.REGISTRO_ENCONTRADO, null));
        }
    }

}
