/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Feb 2025
 * @date 23/02/2025
 */
package com.example.tazuyuti_back.services.modules;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import com.example.tazuyuti_back.entities.modules.OrdenCompra;
import com.example.tazuyuti_back.entities.modules.Venta;
import com.example.tazuyuti_back.models.utilities.Pagination;
import com.example.tazuyuti_back.models.utilities.Response;

public interface VentaService {

    ResponseEntity<Response> save(Venta venta, HttpServletRequest request);
    
    ResponseEntity<Response> index(int user, Pagination request);
    
    ResponseEntity<Response> detail(int id);

    ResponseEntity<Response> ticket(int id);

    ResponseEntity<Response> ordenSave(OrdenCompra venta, HttpServletRequest request);
    
    ResponseEntity<Response> ordenIndex(int user, Pagination request);
    
    ResponseEntity<Response> ordenDetail(int id);
}    
