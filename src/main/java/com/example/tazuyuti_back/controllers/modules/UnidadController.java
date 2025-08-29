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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController; 
import com.example.tazuyuti_back.entities.modules.Unidad;
import com.example.tazuyuti_back.helpers.SystemText;
import com.example.tazuyuti_back.helpers.Utils;
import com.example.tazuyuti_back.models.utilities.Pagination;
import com.example.tazuyuti_back.models.utilities.Response;
import com.example.tazuyuti_back.services.modules.UnidadesService;
import com.example.tazuyuti_back.validator.onCreate;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api")
public class UnidadController {

    @Autowired
    private UnidadesService service;

    @PostMapping(value = "/unidad/save", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<Response> save(@Validated(onCreate.class) @ModelAttribute Unidad unidad,
            HttpServletRequest request) {
        return service.save(unidad, request);
    }
   
    @PostMapping(value = "/unidad/index", consumes = { "application/xml", "application/json" })
    public ResponseEntity<Response> index(
            @Validated @RequestBody Pagination request) {
        try {
            if (request.getPage() <= 0 || request.getSize() <= 0 || request.getSort().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new Response(false, SystemText.General.FALTA_INFORMACION, null));
            }

            String message = Utils.validateFilteringInformation(
                    SystemText.Unidad.OPCIONES_VALIDAS_PAGINACION, request.getSort(),
                    request.getFilters());
            if (message.equals("")) {
                return service.index(request);
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

    @PostMapping(value = "/unidad/update", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<Response> update(@Valid @ModelAttribute @Validated(onCreate.class) Unidad unidad,
            HttpServletRequest request) {
        return service.update(unidad, request);
    }

    @PostMapping(value = "/unidad/{id}/delete")
    public ResponseEntity<Response> delete(@PathVariable(name = "id", required = true) int id,
            HttpServletRequest request) {
        return service.delete(id, request);
    }

    @GetMapping(value = "/unidad/{id}/detail")
    public ResponseEntity<Response> detail(@PathVariable(name = "id", required = true) int id) {
        return service.detail(id);
    }

    @GetMapping(value = "/unidad/catalogs")
    public ResponseEntity<Response> catalogs() {
        return service.catalogs();
    }
}
