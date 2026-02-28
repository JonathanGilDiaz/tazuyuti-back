/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Feb 2025
 * @date 23/02/2025
 */
package com.example.tazuyuti_back.services.modules;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import com.example.tazuyuti_back.entities.modules.Bitacora;
import com.example.tazuyuti_back.entities.modules.Boleto;
import com.example.tazuyuti_back.models.utilities.Pagination;
import com.example.tazuyuti_back.models.utilities.Response;

import jakarta.servlet.http.HttpServletRequest;

public interface BoletoService {

    ResponseEntity<Response> index(int user, Pagination request);

    ResponseEntity<Response> catalogs();

    ResponseEntity<Response> horarios(String fecha, int origen, int precioId, Integer hasta);

    ResponseEntity<Response> save(Boleto boleto, HttpServletRequest request);

    ResponseEntity<Response> detail(int id);

    ResponseEntity<Response> ticket(int id);

    ResponseEntity<Response> cancelar(int id);

    ResponseEntity<Response> indexBitacoras(int user, Pagination request);

    ResponseEntity<Response> cerrarViaje(Bitacora bitacora);

    ResponseEntity<Response> detailDetalleRuta(int id);

    ResponseEntity<Response> indexBitacorasChofer(int user, Pagination request);

    ResponseEntity<Response> cambiarHora(int id, Map<String, String> body);
}
