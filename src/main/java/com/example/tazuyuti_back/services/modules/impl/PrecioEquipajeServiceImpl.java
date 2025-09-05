/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Feb 2025
 * @date 23/02/2025
 */
package com.example.tazuyuti_back.services.modules.impl;

import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.example.tazuyuti_back.entities.modules.PrecioEquipaje;
import com.example.tazuyuti_back.helpers.SystemText;
import com.example.tazuyuti_back.helpers.Utils;
import com.example.tazuyuti_back.models.utilities.Pagination;
import com.example.tazuyuti_back.models.utilities.Response;
import com.example.tazuyuti_back.repositories.modules.PrecioEquipajeRepository;
import com.example.tazuyuti_back.services.modules.PrecioEquipajeService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@Service
public class PrecioEquipajeServiceImpl  implements PrecioEquipajeService {

    @Autowired
    private PrecioEquipajeRepository repository;

    @Override
    @Transactional
    public ResponseEntity<Response> save(PrecioEquipaje precioEquipaje, HttpServletRequest request) {
        precioEquipaje.setEstado(true);
        repository.save(precioEquipaje);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new Response(true, SystemText.General.PROCESO_EXITOSO, null));
    }

    @SuppressWarnings("unchecked")
    @Override
    public ResponseEntity<Response> index(Pagination requestT) {
        Map<String, Object> data = Utils.getSpecificationAndPageable(requestT, PrecioEquipaje.class);
        Specification<PrecioEquipaje> specs = (Specification<PrecioEquipaje>) data.get("specification");
        Specification<PrecioEquipaje> estadoTrue = (root, query, cb) -> cb.isTrue(root.get("estado"));
        Specification<PrecioEquipaje> finalSpec = specs.and(estadoTrue);
        Page<PrecioEquipaje> list = repository.findAll(finalSpec, (Pageable) data.get("pageable"));
        if (list.hasContent()) {
            return ResponseEntity.ok(new Response(true, SystemText.General.PROCESO_EXITOSO, list));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, SystemText.General.REGISTRO_NO_ENCONTRADO, null));
        }
    }

    @Override
    @Transactional
    public ResponseEntity<Response> update(PrecioEquipaje precioEquipaje, HttpServletRequest request) {
        Optional<PrecioEquipaje> itemOp = repository.findById(precioEquipaje.getId());
        if (!itemOp.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, SystemText.General.REGISTRO_NO_ENCONTRADO, null));
        }
        precioEquipaje.setEstado(true);
        repository.save(precioEquipaje);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new Response(true, SystemText.General.PROCESO_EXITOSO, null));
    }

    @Override
    @Transactional
    public ResponseEntity<Response> delete(int id, HttpServletRequest request) {
        try {
            Optional<PrecioEquipaje> itemId = repository.findById(id);
            if (!itemId.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Response(false, SystemText.General.REGISTRO_NO_ENCONTRADO, null));
            }
            try {
                repository.deleteById(id);
            } catch (Exception e) {
                PrecioEquipaje precioEquipaje = itemId.get();
                precioEquipaje.setEstado(false);
                repository.save(precioEquipaje);
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
        Optional<PrecioEquipaje> item = repository.findById(id);
        if (item.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new Response(true, SystemText.General.REGISTRO_ENCONTRADO, item.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, SystemText.General.REGISTRO_ENCONTRADO, null));
        }
    }

}
