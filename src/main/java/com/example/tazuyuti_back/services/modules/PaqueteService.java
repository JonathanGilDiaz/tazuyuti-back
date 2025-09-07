/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Feb 2025
 * @date 23/02/2025
 */
package com.example.tazuyuti_back.services.modules;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import com.example.tazuyuti_back.entities.modules.CancelarPaquete;
import com.example.tazuyuti_back.entities.modules.EntregarPaquete;
import com.example.tazuyuti_back.entities.modules.EnviarPaquete;
import com.example.tazuyuti_back.entities.modules.Paquete;
import com.example.tazuyuti_back.entities.modules.RecibirPaquete;
import com.example.tazuyuti_back.models.utilities.Pagination;
import com.example.tazuyuti_back.models.utilities.Response;

public interface PaqueteService {

    ResponseEntity<Response> save(Paquete paaquete, HttpServletRequest request);

    ResponseEntity<Response> index(int user, Pagination request);

    ResponseEntity<Response> catalogs();

    ResponseEntity<Response> detail(int id);

    ResponseEntity<Response> cancelar(CancelarPaquete dato);

    ResponseEntity<Response> enviar(EnviarPaquete dato);

    ResponseEntity<Response> recibir(RecibirPaquete dato);

    ResponseEntity<Response> entregar(EntregarPaquete dato);

    ResponseEntity<Response> ticketInterno(int id);

    ResponseEntity<Response> ticketCliente(int id);

}
