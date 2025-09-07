/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 08 Mar 2025
 * @date 08/03/2025
 */
package com.example.tazuyuti_back.controllers.modules;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.tazuyuti_back.entities.modules.CancelarPaquete;
import com.example.tazuyuti_back.entities.modules.EntregarPaquete;
import com.example.tazuyuti_back.entities.modules.EnviarPaquete;
import com.example.tazuyuti_back.entities.modules.Paquete;
import com.example.tazuyuti_back.entities.modules.RecibirPaquete;
import com.example.tazuyuti_back.helpers.SystemText;
import com.example.tazuyuti_back.helpers.Utils;
import com.example.tazuyuti_back.models.utilities.Pagination;
import com.example.tazuyuti_back.models.utilities.Response;
import com.example.tazuyuti_back.services.modules.PaqueteService;
import com.example.tazuyuti_back.validator.onCreate;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
public class PaqueteController {

    @Autowired
    private PaqueteService service;

    @PostMapping(value = "/paquete/save", consumes = { "application/json" })
    public ResponseEntity<Response> save(
            @Validated(onCreate.class) @RequestBody Paquete paquete,
            HttpServletRequest request) {
        return service.save(paquete, request);
    }

    @PostMapping(value = "/paquete/{idUsuario}/index", consumes = { "application/xml", "application/json" })
    public ResponseEntity<Response> index(
            @PathVariable int idUsuario,
            @Validated @RequestBody Pagination request) {
        try {
            if (request.getPage() <= 0 || request.getSize() <= 0 || request.getSort().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new Response(false, SystemText.General.FALTA_INFORMACION, null));
            }

            String message = Utils.validateFilteringInformation(
                    SystemText.Paquete.OPCIONES_VALIDAS_PAGINACION, request.getSort(),
                    request.getFilters());
            if (message.equals("")) {
                return service.index(idUsuario, request);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(false, message, null));
            }
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, "Solicitud no encontrada", null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(false, "Error interno en el servidor", null));
        }
    }

    @GetMapping(value = "/paquete/catalogs")
    public ResponseEntity<Response> catalogs() {
        return service.catalogs();
    }

    @GetMapping(value = "/paquete/{id}/detail")
    public ResponseEntity<Response> detail(@PathVariable(name = "id", required = true) int id) {
        return service.detail(id);
    }

    @PostMapping(value = "/paquete/cancelar", consumes = { "application/json" })
    public ResponseEntity<Response> cancelar(
            @Validated(onCreate.class) @RequestBody CancelarPaquete paquete,
            HttpServletRequest request) {
        return service.cancelar(paquete);
    }

    @PostMapping(value = "/paquete/enviar", consumes = { "application/json" })
    public ResponseEntity<Response> enviar(
            @Validated(onCreate.class) @RequestBody EnviarPaquete paquete,
            HttpServletRequest request) {
        return service.enviar(paquete);
    }

    @PostMapping(value = "/paquete/recibir", consumes = { "application/json" })
    public ResponseEntity<Response> recibir(
            @Validated(onCreate.class) @RequestBody RecibirPaquete paquete,
            HttpServletRequest request) {
        return service.recibir(paquete);
    }

    @PostMapping(value = "/paquete/entregar", consumes = { "application/json" })
    public ResponseEntity<Response> entregar(
            @Validated(onCreate.class) @RequestBody EntregarPaquete paquete,
            HttpServletRequest request) {
        return service.entregar(paquete);
    }

        @GetMapping(value = "/paquete/{id}/ticketCliente")
    public ResponseEntity<Response> ticketCliente(@PathVariable(name = "id", required = true) int id) {
        return service.ticketCliente(id);
    }

        @GetMapping(value = "/paquete/{id}/ticketInterno")
    public ResponseEntity<Response> ticketInterno(@PathVariable(name = "id", required = true) int id) {
        return service.ticketInterno(id);
    }
}
