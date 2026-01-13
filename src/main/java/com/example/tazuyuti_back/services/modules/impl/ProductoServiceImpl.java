/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Feb 2025
 * @date 23/02/2025
 */
package com.example.tazuyuti_back.services.modules.impl;

import java.io.File;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.example.tazuyuti_back.entities.administration.User;
import com.example.tazuyuti_back.entities.modules.Producto;
import com.example.tazuyuti_back.helpers.DocumentHelper;
import com.example.tazuyuti_back.helpers.SystemText;
import com.example.tazuyuti_back.helpers.ToolHelper;
import com.example.tazuyuti_back.helpers.Utils;
import com.example.tazuyuti_back.models.utilities.Pagination;
import com.example.tazuyuti_back.models.utilities.Response;
import com.example.tazuyuti_back.repositories.administration.UserRepository;
import com.example.tazuyuti_back.repositories.modules.ProductoRepository;
import com.example.tazuyuti_back.services.modules.ProductoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UserRepository usuarioRepo;

    @Autowired
    private DocumentHelper documentHelper;

    @Value("${files.upload-directory-reporteExcel}")
    private String directoryExcel;

    @Override
    @Transactional
    public ResponseEntity<Response> save(Producto producto, HttpServletRequest request) {
        if (productoRepository.existsByNombreOrCodigoAndEstadoTrue(producto.getNombre(), producto.getCodigo())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new Response(false, SystemText.Producto.PRODUCTO_REPETIDO, null));
        }
        producto.setFechaCreacion(Timestamp.valueOf(LocalDateTime.now()));
        producto.setFechaActualizacion(Timestamp.valueOf(LocalDateTime.now()));
        producto.setEstado(true);
        productoRepository.save(producto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new Response(true, SystemText.General.PROCESO_EXITOSO, null));
    }

    @SuppressWarnings("unchecked")
    @Override
    public ResponseEntity<Response> index(Pagination requestT) {
        Map<String, Object> data = Utils.getSpecificationAndPageable(requestT, Producto.class);
        Specification<Producto> specs = (Specification<Producto>) data.get("specification");
        Specification<Producto> estadoTrue = (root, query, cb) -> cb.isTrue(root.get("estado"));
        Specification<Producto> finalSpec = specs.and(estadoTrue);
        Page<Producto> list = productoRepository.findAll(finalSpec, (Pageable) data.get("pageable"));
        if (list.hasContent()) {
            return ResponseEntity.ok(new Response(true, SystemText.General.PROCESO_EXITOSO, list));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, SystemText.General.REGISTRO_NO_ENCONTRADO, null));
        }
    }

    @Override
    @Transactional
    public ResponseEntity<Response> update(Producto producto, HttpServletRequest request) {
        Optional<Producto> productoOp = productoRepository.findById(producto.getId());
        if (!productoOp.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, SystemText.General.REGISTRO_NO_ENCONTRADO, null));
        }

        if (productoRepository.existsByNombreOrCodigoAndEstadoTrueAndIdNot(producto.getNombre(), producto.getCodigo(),
                producto.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new Response(false, SystemText.Producto.PRODUCTO_REPETIDO, null));
        }
        Producto productoBD = productoOp.get();
        producto.setFechaCreacion(productoOp.get().getFechaCreacion());
        producto.setFechaActualizacion(Timestamp.valueOf(LocalDateTime.now()));
        producto.setEstado(true);
        User userAuthenticated = usuarioRepo.findFirstByUsuarioAndActivoTrue(ToolHelper.getUserNameAuthenticate())
                .get();
        if (userAuthenticated.getRol().getId() != 1) {
            producto.setCantidad(productoBD.getCantidad());
        }
        productoRepository.save(producto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new Response(true, SystemText.General.PROCESO_EXITOSO, null));
    }

    @Override
    @Transactional
    public ResponseEntity<Response> delete(int id, HttpServletRequest request) {
        try {
            Optional<Producto> productoId = productoRepository.findById(id);
            if (!productoId.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Response(false, SystemText.General.REGISTRO_NO_ENCONTRADO, null));
            }
            try {
                productoRepository.deleteById(id);
            } catch (Exception e) {
                Producto producto = productoId.get();
                producto.setEstado(false);
                productoRepository.save(producto);
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
        Optional<Producto> item = productoRepository.findById(id);
        if (item.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new Response(true, SystemText.General.REGISTRO_ENCONTRADO, item.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, SystemText.General.REGISTRO_ENCONTRADO, null));
        }
    }

    @Override
    public ResponseEntity<Response> getAll() {
        List<Producto> productos = productoRepository.findByEstadoTrue();
        if (!productos.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new Response(true, SystemText.General.REGISTRO_ENCONTRADO, productos));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, SystemText.General.REGISTRO_ENCONTRADO, null));
        }
    }

    @Override
    public ResponseEntity<Response> excel(int idUsuario) {
        List<Producto> list = productoRepository.findByEstadoTrue();
        if (!list.isEmpty()) {
            String fileUrl = directoryExcel + "ReporteProductos.xlsx";
            try {
                File file = new File(fileUrl);
                if (file.exists()) {
                    file.delete();
                }
                List<Map.Entry<String, Boolean>> encabezados = new ArrayList<>();
                encabezados.add(new AbstractMap.SimpleEntry<>("Código", false));
                encabezados.add(new AbstractMap.SimpleEntry<>("Nombre", false));
                encabezados.add(new AbstractMap.SimpleEntry<>("Unidad", false));
                encabezados.add(new AbstractMap.SimpleEntry<>("Costo de compra", false));
                encabezados.add(new AbstractMap.SimpleEntry<>("Precio de venta", false));
                encabezados.add(new AbstractMap.SimpleEntry<>("Existencia", false));
                List<List<Object>> datos = list.stream()
                        .map(dato -> {
                            List<Object> fila = new ArrayList<>();
                            fila.add(dato.getCodigo());
                            fila.add(dato.getNombre());
                            fila.add(dato.getUnidad());
                            fila.add(dato.getCosto());
                            fila.add(dato.getPrecio());
                            fila.add(dato.getCantidad());
                            return fila;
                        })
                        .collect(Collectors.toList());

                Optional<User> userOp = usuarioRepo.findById(idUsuario);
                if (!userOp.isPresent()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new Response(false, SystemText.General.REGISTRO_NO_ENCONTRADO, null));
                }
                documentHelper.ReporteExcels(fileUrl, "Productos", userOp.get(), encabezados, datos);

                return ResponseEntity.status(HttpStatus.OK).body(
                        new Response(true, SystemText.General.REGISTRO_ENCONTRADO, Utils.encodeFileToBase64(fileUrl)));
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Response(false, e.getMessage(), null));
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, SystemText.General.REGISTRO_NO_ENCONTRADO, null));
        }
    }
}
