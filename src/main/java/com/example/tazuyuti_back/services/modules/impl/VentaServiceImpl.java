/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Feb 2025
 * @date 23/02/2025
 */
package com.example.tazuyuti_back.services.modules.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.example.tazuyuti_back.entities.administration.User;
import com.example.tazuyuti_back.entities.modules.DetalleOrdenCompra;
import com.example.tazuyuti_back.entities.modules.DetalleVenta;
import com.example.tazuyuti_back.entities.modules.OrdenCompra;
import com.example.tazuyuti_back.entities.modules.Producto;
import com.example.tazuyuti_back.entities.modules.Venta;
import com.example.tazuyuti_back.helpers.DocumentHelper;
import com.example.tazuyuti_back.helpers.SystemText;
import com.example.tazuyuti_back.helpers.Utils;
import com.example.tazuyuti_back.models.utilities.Pagination;
import com.example.tazuyuti_back.models.utilities.Response;
import com.example.tazuyuti_back.repositories.administration.UserRepository;
import com.example.tazuyuti_back.repositories.modules.OrdenCompraRepository;
import com.example.tazuyuti_back.repositories.modules.ProductoRepository;
import com.example.tazuyuti_back.repositories.modules.VentaRepository;
import com.example.tazuyuti_back.services.modules.VentaService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@Service
public class VentaServiceImpl implements VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DocumentHelper documentHelper;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private OrdenCompraRepository ordenCompraRepository;

    @Override
    @Transactional
    public ResponseEntity<Response> save(Venta venta, HttpServletRequest request) {
        Optional<User> userOp = userRepository.findById(venta.getUsuario().getId());
        if (!userOp.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, SystemText.General.REGISTRO_NO_ENCONTRADO, null));

        }
        User usuarioCompleto = userOp.get();
        venta.setUsuario(userOp.get());
        String nombreSucursal = usuarioCompleto.getSucursal().getNombre();
        String letraSucursal = nombreSucursal.substring(0, 1).toUpperCase();
        long conteo = ventaRepository.countByUsuario_Sucursal_Id(usuarioCompleto.getSucursal().getId());
        String folio = letraSucursal + (conteo + 1);
        venta.setFolio(folio);
        if (venta.getDetalleVentas() != null) {
            venta.getDetalleVentas().forEach(det -> det.setVenta(venta));
        }
        venta.setFechaCreacion(Timestamp.valueOf(LocalDateTime.now()));
        venta.setEstado(true);
        Venta ventaGuardada = ventaRepository.save(venta);
        if (venta.getDetalleVentas() != null && !venta.getDetalleVentas().isEmpty()) {
            for (DetalleVenta detalle : venta.getDetalleVentas()) {
                Optional<Producto> productoOp = productoRepository.findById(detalle.getProducto().getId());
                if (productoOp.isPresent()) {
                    Producto producto = productoOp.get();

                    double nuevaCantidad = producto.getCantidad() - detalle.getCantidad();
                    producto.setCantidad(nuevaCantidad);

                    producto.setFechaActualizacion(Timestamp.valueOf(LocalDateTime.now()));
                    productoRepository.save(producto);
                } else {
                    System.err.println("⚠️ Producto con ID " + detalle.getProducto().getId()
                            + " no encontrado para actualizar stock.");
                }
            }
        }
        Map<String, Object> data = new HashMap<>();
        data.put("id", ventaGuardada.getId());
        data.put("folio", ventaGuardada.getFolio());
        data.put("total", ventaGuardada.getTotal());
        return ResponseEntity.status(HttpStatus.OK)
                .body(new Response(true, SystemText.General.PROCESO_EXITOSO, data));
    }

    @SuppressWarnings("unchecked")
    @Override
    public ResponseEntity<Response> index(int idUsuario, Pagination requestT) {
        User usuario = userRepository.findById(idUsuario).get();
        int sucursalId = usuario.getSucursal().getId();
        Map<String, Object> data = Utils.getSpecificationAndPageable(requestT, Venta.class);
        Specification<Venta> specs = (Specification<Venta>) data.get("specification");
        Specification<Venta> filtroSucursal = (root, query, cb) -> cb
                .equal(root.get("usuario").get("sucursal").get("id"), sucursalId);
        Specification<Venta> finalSpecs = specs == null ? filtroSucursal : specs.and(filtroSucursal);
        Page<Venta> list = ventaRepository.findAll(finalSpecs, (Pageable) data.get("pageable"));
        if (list.hasContent()) {
            return ResponseEntity.ok(new Response(true, SystemText.General.PROCESO_EXITOSO, list));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, SystemText.General.REGISTRO_NO_ENCONTRADO, null));
        }
    }

    @Override
    public ResponseEntity<Response> detail(int id) {
        Optional<Venta> item = ventaRepository.findById(id);
        if (item.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new Response(true, SystemText.General.REGISTRO_ENCONTRADO, item.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, SystemText.General.REGISTRO_ENCONTRADO, null));
        }
    }

    @Override
    public ResponseEntity<Response> ticket(int id) {
        Optional<Venta> ventaOp = ventaRepository.findById(id);
        if (ventaOp.isPresent()) {
            Map<String, Object> pdfData = documentHelper.createTicketVenta(ventaOp.get());
            return ResponseEntity.ok(new Response(true, "Ticket generado", pdfData));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, "Venta no encontrada", null));
        }
    }

    @Override
    @Transactional
    public ResponseEntity<Response> ordenSave(OrdenCompra venta, HttpServletRequest request) {
        Optional<User> userOp = userRepository.findById(venta.getUsuario().getId());
        if (!userOp.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, SystemText.General.REGISTRO_NO_ENCONTRADO, null));

        }
        venta.setUsuario(userOp.get());
        if (venta.getDetalleOrdenCompras() != null) {
            venta.getDetalleOrdenCompras().forEach(det -> det.setOrden(venta));
        }
        ordenCompraRepository.save(venta);
        venta.setFechaCreacion(Timestamp.valueOf(LocalDateTime.now()));
        if (venta.getDetalleOrdenCompras() != null && !venta.getDetalleOrdenCompras().isEmpty()) {
            for (DetalleOrdenCompra detalle : venta.getDetalleOrdenCompras()) {
                Optional<Producto> productoOp = productoRepository.findById(detalle.getProducto().getId());
                if (productoOp.isPresent()) {
                    Producto producto = productoOp.get();
                    double nuevaCantidad = producto.getCantidad() + detalle.getCantidad();
                    producto.setCantidad(nuevaCantidad);
                    if (detalle.getPrecio() != producto.getCosto()) {
                        producto.setCosto(detalle.getPrecio());
                    }
                    producto.setFechaActualizacion(Timestamp.valueOf(LocalDateTime.now()));
                    productoRepository.save(producto);
                } else {
                    System.err.println("⚠️ Producto con ID " + detalle.getProducto().getId()
                            + " no encontrado para actualizar stock.");
                }
            }
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(new Response(true, SystemText.General.PROCESO_EXITOSO, null));
    }

    @SuppressWarnings("unchecked")
    @Override
    public ResponseEntity<Response> ordenIndex(int idUsuario, Pagination requestT) {
        User usuario = userRepository.findById(idUsuario).get();
        int sucursalId = usuario.getSucursal().getId();
        Map<String, Object> data = Utils.getSpecificationAndPageable(requestT, OrdenCompra.class);
        Specification<OrdenCompra> specs = (Specification<OrdenCompra>) data.get("specification");
        Specification<OrdenCompra> filtroSucursal = (root, query, cb) -> cb
                .equal(root.get("usuario").get("sucursal").get("id"), sucursalId);
        Specification<OrdenCompra> finalSpecs = specs == null ? filtroSucursal : specs.and(filtroSucursal);
        Page<OrdenCompra> list = ordenCompraRepository.findAll(finalSpecs, (Pageable) data.get("pageable"));
        if (list.hasContent()) {
            return ResponseEntity.ok(new Response(true, SystemText.General.PROCESO_EXITOSO, list));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, SystemText.General.REGISTRO_NO_ENCONTRADO, null));
        }
    }

    @Override
    public ResponseEntity<Response> ordenDetail(int id) {
        Optional<OrdenCompra> item = ordenCompraRepository.findById(id);
        if (item.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new Response(true, SystemText.General.REGISTRO_ENCONTRADO, item.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, SystemText.General.REGISTRO_ENCONTRADO, null));
        }
    }
}
