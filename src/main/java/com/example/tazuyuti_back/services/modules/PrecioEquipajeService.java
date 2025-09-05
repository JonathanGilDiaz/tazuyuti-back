/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Feb 2025
 * @date 23/02/2025
 */
package com.example.tazuyuti_back.services.modules;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import com.example.tazuyuti_back.entities.modules.PrecioEquipaje;
import com.example.tazuyuti_back.models.utilities.Pagination;
import com.example.tazuyuti_back.models.utilities.Response;

public interface PrecioEquipajeService {

    ResponseEntity<Response> save(PrecioEquipaje precioEquipaje, HttpServletRequest request);
    
    ResponseEntity<Response> index(Pagination request);
    
    ResponseEntity<Response> detail(int id);

    ResponseEntity<Response> update(PrecioEquipaje precioEquipaje, HttpServletRequest request);

    ResponseEntity<Response> delete(int id, HttpServletRequest request);

}    
