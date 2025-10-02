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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.tazuyuti_back.entities.modules.Bitacora;
import com.example.tazuyuti_back.entities.modules.Boleto;
import com.example.tazuyuti_back.helpers.SystemText;
import com.example.tazuyuti_back.helpers.Utils;
import com.example.tazuyuti_back.models.utilities.Pagination;
import com.example.tazuyuti_back.models.utilities.Response;
import com.example.tazuyuti_back.services.modules.BoletoService;
import com.example.tazuyuti_back.validator.onCreate;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
public class TaquillaController {

    @Autowired
    private BoletoService service;

    @PostMapping(value = "/taquilla/{idUsuario}/index", consumes = { "application/xml", "application/json" })
    public ResponseEntity<Response> index(
            @PathVariable int idUsuario,
            @Validated @RequestBody Pagination request) {
        try {
            if (request.getPage() <= 0 || request.getSize() <= 0 || request.getSort().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new Response(false, SystemText.General.FALTA_INFORMACION, null));
            }

            String message = Utils.validateFilteringInformation(
                    SystemText.Taquilla.OPCIONES_VALIDAS_PAGINACION, request.getSort(),
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

    @GetMapping(value = "/taquilla/catalogs")
    public ResponseEntity<Response> catalogs() {
        return service.catalogs();
    }

    @GetMapping(value = "/taquilla/horarios")
    public ResponseEntity<Response> horarios(
            @RequestParam String fecha,
            @RequestParam int origen,
            @RequestParam int precioId,
            @RequestParam(required = false) Integer hasta) {
        return service.horarios(fecha, origen, precioId, hasta);
    }

    @PostMapping(value = "/taquilla/save", consumes = { "application/json" })
    public ResponseEntity<Response> save(
            @Validated(onCreate.class) @RequestBody Boleto venta,
            HttpServletRequest request) {
        return service.save(venta, request);
    }

    @GetMapping(value = "/taquilla/{id}/detail")
    public ResponseEntity<Response> detail(@PathVariable(name = "id", required = true) int id) {
        return service.detail(id);
    }

    @GetMapping(value = "/taquilla/{id}/ticket")
    public ResponseEntity<Response> ticket(@PathVariable(name = "id", required = true) int id) {
        return service.ticket(id);
    }

    @GetMapping(value = "/taquilla/{id}/cancelar")
    public ResponseEntity<Response> cancelar(@PathVariable(name = "id", required = true) int id) {
        return service.cancelar(id);
    }

    @GetMapping(value = "/taquilla/{id}/detailDetalleRuta")
    public ResponseEntity<Response> detailDetalleRuta(@PathVariable(name = "id", required = true) int id) {
        return service.detailDetalleRuta(id);
    }

    @PostMapping(value = "/taquilla/cerrarViaje", consumes = { "application/json" })
    public ResponseEntity<Response> cerrarViaje(
            @Validated(onCreate.class) @RequestBody Bitacora bitacora,
            HttpServletRequest request) {
        return service.cerrarViaje(bitacora);
    }

    @PostMapping(value = "/taquilla/{idUsuario}/indexBitacoras", consumes = { "application/xml", "application/json" })
    public ResponseEntity<Response> indexBitacoras(
            @PathVariable int idUsuario,
            @Validated @RequestBody Pagination request) {
        try {
            if (request.getPage() <= 0 || request.getSize() <= 0 || request.getSort().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new Response(false, SystemText.General.FALTA_INFORMACION, null));
            }

            String message = Utils.validateFilteringInformation(
                    SystemText.Taquilla.OPCIONES_VALIDAS_PAGINACION_BITACORA, request.getSort(),
                    request.getFilters());
            if (message.equals("")) {
                return service.indexBitacoras(idUsuario, request);
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

      @PostMapping(value = "/taquilla/{idUsuario}/indexBitacorasChofer", consumes = { "application/xml", "application/json" })
    public ResponseEntity<Response> indexBitacorasChofer(
            @PathVariable int idUsuario,
            @Validated @RequestBody Pagination request) {
        try {
            if (request.getPage() <= 0 || request.getSize() <= 0 || request.getSort().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new Response(false, SystemText.General.FALTA_INFORMACION, null));
            }

            String message = Utils.validateFilteringInformation(
                    SystemText.Taquilla.OPCIONES_VALIDAS_PAGINACION_BITACORA, request.getSort(),
                    request.getFilters());
            if (message.equals("")) {
                return service.indexBitacorasChofer(idUsuario, request);
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

}
