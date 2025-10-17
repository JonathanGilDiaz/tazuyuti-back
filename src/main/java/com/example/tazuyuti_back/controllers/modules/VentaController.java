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

import com.example.tazuyuti_back.entities.modules.OrdenCompra;
import com.example.tazuyuti_back.entities.modules.Venta;
import com.example.tazuyuti_back.helpers.SystemText;
import com.example.tazuyuti_back.helpers.Utils;
import com.example.tazuyuti_back.models.utilities.Pagination;
import com.example.tazuyuti_back.models.utilities.Response;
import com.example.tazuyuti_back.services.modules.VentaService;
import com.example.tazuyuti_back.validator.onCreate;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
public class VentaController {

    @Autowired
    private VentaService service;

    @PostMapping(value = "/venta/save", consumes = { "application/json" })
    public ResponseEntity<Response> save(
            @Validated(onCreate.class) @RequestBody Venta venta,
            HttpServletRequest request) {
        return service.save(venta, request);
    }

    @PostMapping(value = "/venta/{idUsuario}/index", consumes = { "application/xml", "application/json" })
    public ResponseEntity<Response> index(
            @PathVariable int idUsuario,
            @Validated @RequestBody Pagination request) {
        try {
            if (request.getPage() <= 0 || request.getSize() <= 0 || request.getSort().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new Response(false, SystemText.General.FALTA_INFORMACION, null));
            }

            String message = Utils.validateFilteringInformation(
                    SystemText.Venta.OPCIONES_VALIDAS_PAGINACION, request.getSort(),
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

    @GetMapping(value = "/venta/{id}/detail")
    public ResponseEntity<Response> detail(@PathVariable(name = "id", required = true) int id) {
        return service.detail(id);
    }

    @GetMapping(value = "/venta/{id}/ticket")
    public ResponseEntity<Response> ticket(@PathVariable(name = "id", required = true) int id) {
        return service.ticket(id);
    }

      @PostMapping(value = "/ordenCompra/save", consumes = { "application/json" })
    public ResponseEntity<Response> ordenSave(
            @Validated(onCreate.class) @RequestBody OrdenCompra venta,
            HttpServletRequest request) {
        return service.ordenSave(venta, request);
    }

    @PostMapping(value = "/ordenCompra/{idUsuario}/index", consumes = { "application/xml", "application/json" })
    public ResponseEntity<Response> ordenIndex(
            @PathVariable int idUsuario,
            @Validated @RequestBody Pagination request) {
        try {
            if (request.getPage() <= 0 || request.getSize() <= 0 || request.getSort().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new Response(false, SystemText.General.FALTA_INFORMACION, null));
            }

            String message = Utils.validateFilteringInformation(
                    SystemText.Venta.OPCIONES_VALIDAS_PAGINACION_ORDEB_COMPRA, request.getSort(),
                    request.getFilters());
            if (message.equals("")) {
                return service.ordenIndex(idUsuario, request);
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

    @GetMapping(value = "/ordenCompra/{id}/detail")
    public ResponseEntity<Response> ordenDetail(@PathVariable(name = "id", required = true) int id) {
        return service.ordenDetail(id);
    }
}
