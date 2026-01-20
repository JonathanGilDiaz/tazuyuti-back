/**
 * @author ...
 */
package com.example.tazuyuti_back.controllers.modules;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.example.tazuyuti_back.entities.administration.User;
import com.example.tazuyuti_back.entities.modules.Bitacora;
import com.example.tazuyuti_back.entities.modules.Boleto;
import com.example.tazuyuti_back.entities.modules.Corte;
import com.example.tazuyuti_back.entities.modules.DetalleVenta;
import com.example.tazuyuti_back.entities.modules.Paquete;
import com.example.tazuyuti_back.entities.modules.Producto;
import com.example.tazuyuti_back.helpers.SystemText;
import com.example.tazuyuti_back.helpers.ToolHelper;
import com.example.tazuyuti_back.helpers.Utils;
import com.example.tazuyuti_back.models.utilities.Pagination;
import com.example.tazuyuti_back.models.utilities.Response;
import com.example.tazuyuti_back.repositories.administration.UserRepository;
import com.example.tazuyuti_back.repositories.modules.BitacoraRepository;
import com.example.tazuyuti_back.repositories.modules.BoletoRepository;
import com.example.tazuyuti_back.repositories.modules.CorteRepository;
import com.example.tazuyuti_back.repositories.modules.DetalleVentaRepository;
import com.example.tazuyuti_back.repositories.modules.PaqueteRepository;
import com.example.tazuyuti_back.repositories.modules.VentaRepository;

import jakarta.transaction.Transactional;

import com.example.tazuyuti_back.entities.modules.Venta;

@RestController
@RequestMapping("/api/corte")
public class CorteController {

        @Autowired
        private CorteRepository corteRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private BoletoRepository boletoRepository;

        @Autowired
        private VentaRepository ventaRepository;

        @Autowired
        private PaqueteRepository paqueteRepository;

        @Autowired
        private BitacoraRepository bitacoraRepository;

        @Autowired
        private DetalleVentaRepository detalleVentaRepository;

        @PostMapping("/crear")
        public ResponseEntity<Response> crearCorte(@RequestBody Corte request) {
                try {
                        int usuarioId = request.getUsuario().getId();
                        double saldoInicial = request.getSaldoInicial();
                        User usuario = userRepository.findById(usuarioId)
                                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

                        // Validar que no tenga corte abierto
                        boolean tieneCorteAbierto = corteRepository.findFirstByUsuarioAndEstado(usuario, "Abierto")
                                        .isPresent();
                        if (tieneCorteAbierto) {
                                return ResponseEntity.status(HttpStatus.CONFLICT)
                                                .body(new Response(false, "El usuario ya tiene un corte abierto",
                                                                null));
                        }

                        // Crear corte
                        Corte corte = new Corte();
                        corte.setUsuario(usuario);
                        corte.setSaldoInicial(saldoInicial);
                        corte.setEfectivo(saldoInicial); // lo ponemos como efectivo inicial
                        corte.setInicio(ToolHelper.castDateTime(ToolHelper.getCurrentDateTime()));
                        corte.setCierre(ToolHelper.castDateTime(ToolHelper.getCurrentDateTime())); // o null si quieres
                        corte.setEstado("Abierto");
                        corteRepository.save(corte);

                        return ResponseEntity.status(HttpStatus.CREATED)
                                        .body(new Response(true, SystemText.General.PROCESO_EXITOSO, corte));
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new Response(false, "Error al crear el corte: " + e.getMessage(), null));
                }
        }

        @GetMapping("/usuario/{usuarioId}/activo")
        public ResponseEntity<Response> obtenerCorteActivo(@PathVariable int usuarioId) {
                try {
                        User usuario = userRepository.findById(usuarioId)
                                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

                        Corte corte = corteRepository.findFirstByUsuarioAndEstado(usuario, "Abierto")
                                        .orElseThrow(() -> new RuntimeException(
                                                        "No hay corte abierto para este usuario"));

                        Timestamp inicioCorte = corte.getInicio();

                        List<Venta> ventas = ventaRepository.findByUsuarioAndFechaCreacionAfterAndEstado(usuario,
                                        inicioCorte,
                                        true);

                        List<DetalleVenta> detalles = detalleVentaRepository.findByVentas(ventas);
                        Map<Producto, Double> resumenProductosMap = new HashMap<>();
                        for (DetalleVenta dv : detalles) {
                                Producto producto = dv.getProducto();
                                double cantidad = dv.getCantidad();

                                resumenProductosMap.merge(producto, cantidad, Double::sum);
                        }
                        List<Map<String, Object>> resumenProductos = new ArrayList<>();
                        for (Map.Entry<Producto, Double> entry : resumenProductosMap.entrySet()) {
                                Map<String, Object> item = new HashMap<>();
                                item.put("producto", entry.getKey());
                                item.put("cantidad", entry.getValue());
                                resumenProductos.add(item);
                        }

                        List<Boleto> boletos = boletoRepository.findByUsuarioAndFechaCreacionAfterAndEstado(usuario,
                                        inicioCorte,
                                        "Activo");
                        List<Paquete> paquetes = paqueteRepository.findPaquetesActivosByUsuarioDesdeFecha(usuario,
                                        inicioCorte);

                        List<Bitacora> bitacoras = bitacoraRepository.findByUsuarioAndFechaCreacionAfter(usuario,
                                        inicioCorte);

                        double totalEfectivo = ventas.stream()
                                        .mapToDouble(v -> v.getFormaPago().equals("01 Efectivo") ? v.getTotal() : 0)
                                        .sum() +
                                        boletos.stream().mapToDouble(
                                                        b -> b.getFormaPago().equals("01 Efectivo") ? b.getTotal() : 0)
                                                        .sum()
                                        +
                                        paquetes.stream().mapToDouble(
                                                        p -> p.getFormaPago().equals("01 Efectivo") ? p.getTotal() : 0)
                                                        .sum();

                        double totalTransferencia = ventas.stream()
                                        .mapToDouble(v -> v.getFormaPago().equals("03 Transferencia") ? v.getTotal()
                                                        : 0)
                                        .sum() +
                                        boletos.stream().mapToDouble(
                                                        b -> b.getFormaPago().equals("03 Transferencia") ? b.getTotal()
                                                                        : 0)
                                                        .sum()
                                        +
                                        paquetes.stream()
                                                        .mapToDouble(p -> p.getFormaPago().equals("03 Transferencia")
                                                                        ? p.getTotal()
                                                                        : 0)
                                                        .sum();
                        double totalTarjeta = ventas.stream()
                                        .mapToDouble(v -> v.getFormaPago().equals("04 Tarjeta") ? v.getTotal() : 0)
                                        .sum() +
                                        boletos.stream().mapToDouble(
                                                        b -> b.getFormaPago().equals("04 Tarjeta") ? b.getTotal() : 0)
                                                        .sum()
                                        +
                                        paquetes.stream().mapToDouble(
                                                        p -> p.getFormaPago().equals("04 Tarjeta") ? p.getTotal() : 0)
                                                        .sum();

                        double totalGeneral = totalEfectivo + totalTransferencia + totalTarjeta;
                        double totalBitacoras = bitacoras.stream().mapToDouble(Bitacora::getTotal).sum();
                        double saldoEnCaja = corte.getSaldoInicial() + totalEfectivo - totalBitacoras;

                        Map<String, Object> resultado = new HashMap<>();
                        resultado.put("corte", corte);
                        resultado.put("ventas", ventas);
                        resultado.put("boletos", boletos);
                        resultado.put("paquetes", paquetes);
                        resultado.put("bitacoras", bitacoras);
                        resultado.put("resumenProductos", resumenProductos);
                        Map<String, Double> totales = new HashMap<>();
                        totales.put("efectivo", totalEfectivo);
                        totales.put("transferencia", totalTransferencia);
                        totales.put("tarjeta", totalTarjeta);
                        totales.put("total", totalGeneral);
                        totales.put("totalBitacoras", totalBitacoras);
                        totales.put("saldoCaja", saldoEnCaja);
                        resultado.put("totales", totales);
                        return ResponseEntity
                                        .ok(new Response(true, "Corte y movimientos activos obtenidos", resultado));

                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new Response(false, "Error al obtener corte activo: " + e.getMessage(),
                                                        null));
                }
        }

        @Transactional
        @PostMapping("/cerrar")
        public ResponseEntity<Response> cerrarCorte(@RequestBody Corte corteRequest) {
                try {
                        // Obtener el corte existente
                        Corte corte = corteRepository.findById(corteRequest.getId())
                                        .orElseThrow(() -> new RuntimeException("Corte no encontrado"));

                        // Actualizar todos los campos del corte con los valores recibidos
                        corte.setEfectivo(corteRequest.getEfectivo());
                        corte.setTarjeta(corteRequest.getTarjeta());
                        corte.setTransferencia(corteRequest.getTransferencia());
                        corte.setTotalCaja(corteRequest.getTotalCaja());
                        corte.setTotalCobros(corteRequest.getTotalCobros());
                        corte.setObservaciones(corteRequest.getObservaciones());
                        corte.setRetiro(corteRequest.getRetiro());
                        corte.setSaldoFinal(corteRequest.getSaldoFinal());
                        corte.setFaltante(corteRequest.getFaltante());
                        corte.setSobrante(corteRequest.getSobrante());
                        corte.setEfectivoCaja(corteRequest.getEfectivoCaja());
                        corte.setTotalRetiros(corteRequest.getTotalRetiros());
                        corte.setEstado("Cerrado");
                        corte.setCierre(ToolHelper.castDateTime(ToolHelper.getCurrentDateTime()));
                        User usuarioCompleto = corte.getUsuario();

                        // Calcular folio igual que en ventas
                        String nombreSucursal = usuarioCompleto.getSucursal().getNombre();
                        String letraSucursal = nombreSucursal.substring(0, 1).toUpperCase();
                        long conteo = corteRepository.countByUsuario_Sucursal_Id(usuarioCompleto.getSucursal().getId());
                        String folio = "C" + letraSucursal + (conteo + 1);
                        corte.setFolio(folio);
                        corteRepository.save(corte);

                        return ResponseEntity.ok(new Response(true, "Corte cerrado exitosamente", corte));
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new Response(false, "Error al cerrar el corte: " + e.getMessage(), null));
                }
        }

        @SuppressWarnings("unchecked")
        @PostMapping(value = "/{idUsuario}/index", consumes = { "application/xml", "application/json" })
        public ResponseEntity<Response> index(
                        @PathVariable int idUsuario,
                        @Validated @RequestBody Pagination requestT) {
                User usuario = userRepository.findById(idUsuario).get();
                int sucursalId = usuario.getSucursal().getId();
                Map<String, Object> data = Utils.getSpecificationAndPageable(requestT, Corte.class);
                Specification<Corte> specs = (Specification<Corte>) data.get("specification");
                Specification<Corte> filtroSucursal = (root, query, cb) -> cb.and(
                                cb.equal(root.get("usuario").get("sucursal").get("id"), sucursalId),
                                cb.equal(root.get("estado"), "Cerrado"));
                Specification<Corte> finalSpecs = specs == null ? filtroSucursal : specs.and(filtroSucursal);
                Page<Corte> list = corteRepository.findAll(finalSpecs, (Pageable) data.get("pageable"));
                if (list.hasContent()) {
                        return ResponseEntity.ok(new Response(true, SystemText.General.PROCESO_EXITOSO, list));
                } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(new Response(false, SystemText.General.REGISTRO_NO_ENCONTRADO, null));
                }
        }

        @GetMapping("/{corteId}/detalle")
        public ResponseEntity<Response> obtenerDetalleCorteCerrado(@PathVariable int corteId) {
                try {
                        Corte corte = corteRepository.findById(corteId)
                                        .orElseThrow(() -> new RuntimeException("Corte no encontrado"));

                        if (!"Cerrado".equalsIgnoreCase(corte.getEstado())) {
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                                .body(new Response(false, "El corte no está cerrado", null));
                        }

                        User usuario = corte.getUsuario();
                        Timestamp inicioCorte = corte.getInicio();
                        Timestamp cierreCorte = corte.getCierre();

                        List<Venta> ventas = ventaRepository.findByUsuarioAndFechaCreacionBetweenAndEstado(
                                        usuario, inicioCorte, cierreCorte, true);
                        List<DetalleVenta> detalles = detalleVentaRepository.findByVentas(ventas);

                        Map<Producto, Double> resumenProductosMap = new HashMap<>();

                        for (DetalleVenta dv : detalles) {
                                Producto producto = dv.getProducto();
                                double cantidad = dv.getCantidad();
                                resumenProductosMap.merge(producto, cantidad, Double::sum);
                        }

                        List<Map<String, Object>> resumenProductos = new ArrayList<>();
                        for (Map.Entry<Producto, Double> entry : resumenProductosMap.entrySet()) {
                                Map<String, Object> item = new HashMap<>();
                                item.put("producto", entry.getKey());
                                item.put("cantidad", entry.getValue());
                                resumenProductos.add(item);
                        }

                        List<Boleto> boletos = boletoRepository.findByUsuarioAndFechaCreacionBetweenAndEstado(
                                        usuario, inicioCorte, cierreCorte, "Activo");

                        List<Paquete> paquetes = paqueteRepository.findPaquetesActivosByUsuarioEntreFechas(
                                        usuario, inicioCorte, cierreCorte);

                        List<Bitacora> bitacoras = bitacoraRepository.findByUsuarioAndFechaCreacionBetween(
                                        usuario, inicioCorte, cierreCorte);

                        Map<String, Object> resultado = new HashMap<>();
                        resultado.put("corte", corte);
                        resultado.put("ventas", ventas);
                        resultado.put("boletos", boletos);
                        resultado.put("paquetes", paquetes);
                        resultado.put("bitacoras", bitacoras);
                        resultado.put("resumenProductos", resumenProductos);

                        return ResponseEntity.ok(new Response(true, "Detalle del corte cerrado obtenido", resultado));
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new Response(false,
                                                        "Error al obtener detalle de corte cerrado: " + e.getMessage(),
                                                        null));
                }
        }

}
