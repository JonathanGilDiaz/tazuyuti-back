/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Feb 2025
 * @date 23/02/2025
 */
package com.example.tazuyuti_back.services.modules.impl;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.example.tazuyuti_back.entities.administration.User;
import com.example.tazuyuti_back.entities.modules.Boleto;
import com.example.tazuyuti_back.entities.modules.DetalleRuta;
import com.example.tazuyuti_back.entities.modules.PrecioBoleto;
import com.example.tazuyuti_back.entities.modules.Ruta;
import com.example.tazuyuti_back.entities.modules.Unidad;
import com.example.tazuyuti_back.helpers.SystemText;
import com.example.tazuyuti_back.helpers.Utils;
import com.example.tazuyuti_back.models.utilities.Pagination;
import com.example.tazuyuti_back.models.utilities.Response;
import com.example.tazuyuti_back.repositories.administration.UserRepository;
import com.example.tazuyuti_back.repositories.catalogs.SucursalRepository;
import com.example.tazuyuti_back.repositories.modules.BoletoRepository;
import com.example.tazuyuti_back.repositories.modules.DetalleRutaRepository;
import com.example.tazuyuti_back.repositories.modules.PrecioBoletoRepository;
import com.example.tazuyuti_back.repositories.modules.PrecioEquipajeRepository;
import com.example.tazuyuti_back.services.modules.BoletoService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@Service
public class BoletoServiceImpl implements BoletoService {

    @Autowired
    private BoletoRepository boletoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PrecioBoletoRepository precioBoletoRepository;

    @Autowired
    private SucursalRepository sucursalRepository;

    @Autowired
    private DetalleRutaRepository detalleRutaRepository;

    @Autowired
    private PrecioEquipajeRepository precioEquipajeRepository;

    @SuppressWarnings("unchecked")
    @Override
    public ResponseEntity<Response> index(int idUsuario, Pagination requestT) {
        User usuario = userRepository.findById(idUsuario).get();
        int sucursalId = usuario.getSucursal().getId();
        Map<String, Object> data = Utils.getSpecificationAndPageable(requestT, Boleto.class);
        Specification<Boleto> specs = (Specification<Boleto>) data.get("specification");
        Specification<Boleto> filtroSucursal = (root, query, cb) -> cb
                .equal(root.get("usuario").get("sucursal").get("id"), sucursalId);
        Specification<Boleto> finalSpecs = specs == null ? filtroSucursal : specs.and(filtroSucursal);
        Page<Boleto> list = boletoRepository.findAll(finalSpecs, (Pageable) data.get("pageable"));
        if (list.hasContent()) {
            return ResponseEntity.ok(new Response(true, SystemText.General.PROCESO_EXITOSO, list));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, SystemText.General.REGISTRO_NO_ENCONTRADO, null));
        }
    }

    @Override
    public ResponseEntity<Response> catalogs() {
        Map<String, Object> response = new HashMap<>();
        response.put("pasajes", precioBoletoRepository.findByEstadoTrue());
        response.put("sucursal", sucursalRepository.findAll());
        response.put("equipajes", precioEquipajeRepository.findByEstadoTrue());
        return ResponseEntity.status(HttpStatus.OK)
                .body(new Response(true, SystemText.General.PROCESO_EXITOSO, response));
    }

    @Override
    public ResponseEntity<Response> horarios(String fecha, int origen, int precioId, Integer hasta) {
        try {
            LocalDate fechaBuscada = LocalDate.parse(fecha);

            PrecioBoleto precio = precioBoletoRepository.findById(precioId)
                    .orElseThrow(() -> new RuntimeException("Precio no encontrado"));

            int destino = precio.getEntre2() != null ? precio.getEntre2().getId() : precio.getOrigen().getId();

            List<DetalleRuta> detalles = detalleRutaRepository
                    .findByEstadoTrueAndFechaAndSalida_Id(fechaBuscada, origen);

            if (detalles.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Response(false, "No se encontraron horarios", null));
            }

            List<Map<String, Object>> horarios = new ArrayList<>();

            for (DetalleRuta d : detalles) {
                Ruta ruta = d.getRuta();
                Unidad unidad = ruta.getUnidad();

               List<DetalleRuta> tramos = new ArrayList<DetalleRuta>();
               tramos.add(d);
                tramos.add(detalleRutaRepository.findById(d.getId() + 1).get());
                tramos.add(detalleRutaRepository.findById(d.getId() + 2).get());

                List<DetalleRuta> tramosFiltrados = tramos.stream()
                        .filter(t -> {
                            int s = t.getSalida().getId();
                            int l = t.getLlegada().getId();
                            if (origen < destino) {
                                return s >= origen && l <= destino && s < l;
                            } else {
                                return s <= origen && l >= destino && s > l;
                            }
                        })
                        .sorted(Comparator.comparing(DetalleRuta::getSalidaHora))
                        .toList();

                if (tramosFiltrados.isEmpty())
                    continue;

                Set<Integer> ocupadosUnion = new HashSet<>();
                for (DetalleRuta tramo : tramosFiltrados) {
                    if (tramo.getOcupados() != null) {
                        Arrays.stream(tramo.getOcupados().split(","))
                                .map(String::trim) 
                                .filter(s -> !s.isBlank())
                                .map(Integer::parseInt)
                                .forEach(ocupadosUnion::add);
                    }
                }

                Map<String, Object> map = new HashMap<>();
                map.put("rutaId", ruta.getId());
                map.put("detalleRutaId", d.getId());
                map.put("hora", d.getSalidaHora().toString());
                map.put("unidad", Map.of(
                        "id", unidad.getId(),
                        "nombre", unidad.getNombre(),
                        "capacidad", unidad.getTipoCamioneta().getCapacidad()));
                map.put("segmentos", tramosFiltrados.stream().map(t -> Map.of(
                        "salida", t.getSalida(),
                        "llegada", t.getLlegada(),
                        "ocupados", t.getOcupados() == null ? List.of()
                                : Arrays.stream(t.getOcupados().split(","))
                                        .map(String::trim) // 👈 limpiar espacios
                                        .filter(s -> !s.isBlank())
                                        .map(Integer::parseInt)
                                        .toList()))
                        .toList());
                map.put("ocupadosUnion", ocupadosUnion);

                horarios.add(map);
            }

            return ResponseEntity.ok(new Response(true, SystemText.General.PROCESO_EXITOSO, horarios));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(false, "Error al obtener horarios: " + e.getMessage(), null));
        }
    }

    @Transactional
    @Override
    public ResponseEntity<Response> save(Boleto boleto, HttpServletRequest request) {
        try {
            // Usuario que registra
            User usuarioCompleto = userRepository.findById(boleto.getUsuario().getId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            boleto.setUsuario(usuarioCompleto);

            // Validamos precio del boleto
            PrecioBoleto precio = precioBoletoRepository.findById(boleto.getPrecioBoleto().getId())
                    .orElseThrow(() -> new RuntimeException("Precio no encontrado"));

            // Obtenemos la ruta de salida
            DetalleRuta salida = detalleRutaRepository.findById(boleto.getDetalleRutaSalida().getId())
                    .orElseThrow(() -> new RuntimeException("DetalleRuta salida no encontrada"));

            Ruta ruta = salida.getRuta();
            int origenId = salida.getSalida().getId();
            int destinoId = precio.getEntre2() != null ? precio.getEntre2().getId() : precio.getOrigen().getId();

            // Tramos de la ruta para ese día
            List<DetalleRuta> tramos = detalleRutaRepository.findByRutaIdAndEstadoTrue(ruta.getId())
                    .stream()
                    .filter(t -> (t.getFecha().isAfter(salida.getFecha()) || t.getFecha().isEqual(salida.getFecha())) &&
                            (t.getSalidaHora().isAfter(salida.getSalidaHora())
                                    || t.getFecha().isAfter(salida.getFecha())))
                    .sorted(Comparator.comparing(DetalleRuta::getFecha)
                            .thenComparing(DetalleRuta::getSalidaHora))
                    .toList();

            // Filtrar tramos que van del origen al destino
            List<DetalleRuta> tramosViaje = tramos.stream()
                    .filter(t -> {
                        int s = t.getSalida().getId();
                        int l = t.getLlegada().getId();
                        if (origenId < destinoId) {
                            return s >= origenId && l <= destinoId && s < l;
                        } else {
                            return s <= origenId && l >= destinoId && s > l;
                        }
                    })
                    .sorted(Comparator.comparing(DetalleRuta::getSalidaHora))
                    .toList();

            if (tramosViaje.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Response(false, "No se encontraron tramos para el viaje", null));
            }

            // Asientos que se quieren reservar (limpiar espacios antes de parsear)
            String nuevosAsientos = boleto.getAsientos(); // ejemplo "3, 4, 5"
            Set<Integer> asientosNuevos = Arrays.stream(nuevosAsientos.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .map(Integer::parseInt)
                    .collect(HashSet::new, HashSet::add, HashSet::addAll);

            // 🔹 VALIDAR DISPONIBILIDAD ANTES DE GUARDAR
            for (DetalleRuta tramo : tramosViaje) {
                Set<Integer> ocupados = new HashSet<>();

                if (tramo.getOcupados() != null) {
                    Arrays.stream(tramo.getOcupados().split(","))
                            .map(String::trim)
                            .filter(s -> !s.isBlank())
                            .map(Integer::parseInt)
                            .forEach(ocupados::add);
                }

                // 1. Verificar si algún asiento ya está ocupado
                for (Integer asiento : asientosNuevos) {
                    if (ocupados.contains(asiento)) {
                        return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body(new Response(false,
                                        "El asiento " + asiento + " ya está ocupado en este horario.", null));
                    }
                }

                // 2. Verificar capacidad disponible
                int capacidad = ruta.getUnidad().getTipoCamioneta().getCapacidad();
                if (ocupados.size() + asientosNuevos.size() > capacidad) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(new Response(false,
                                    "No hay suficientes lugares disponibles. Disponibles: "
                                            + (capacidad - ocupados.size()),
                                    null));
                }
            }

            // 🔹 Si pasa las validaciones → actualizar tramos
            for (DetalleRuta tramo : tramosViaje) {
                Set<Integer> ocupados = new HashSet<>();
                if (tramo.getOcupados() != null) {
                    Arrays.stream(tramo.getOcupados().split(","))
                            .map(String::trim)
                            .filter(s -> !s.isBlank())
                            .map(Integer::parseInt)
                            .forEach(ocupados::add);
                }

                ocupados.addAll(asientosNuevos);

                tramo.setOcupados(
                        ocupados.stream()
                                .sorted()
                                .map(String::valueOf)
                                .reduce((a, b) -> a + "," + b)
                                .orElse(""));

                int capacidad = ruta.getUnidad().getTipoCamioneta().getCapacidad();
                tramo.setDisponibilidad(capacidad - ocupados.size());

                detalleRutaRepository.save(tramo);
            }

            // 🔹 Generar folio para el boleto
            String nombreSucursal = usuarioCompleto.getSucursal().getNombre();
            String letraSucursal = nombreSucursal.substring(0, 1).toUpperCase();
            long conteo = boletoRepository.countByUsuario_Sucursal_Id(usuarioCompleto.getSucursal().getId());
            String folio = "B" + letraSucursal + (conteo + 1);
            boleto.setFolio(folio);

            // 🔹 Fecha y hora de salida = fecha del primer tramo del viaje
            DetalleRuta primerTramo = tramosViaje.get(0);
            boleto.setFechaSalida(
                    Timestamp.valueOf(primerTramo.getFecha().atTime(primerTramo.getSalidaHora())));

            // 🔹 Estado como true
            boleto.setEstado("Activo");

            // Guardar boleto
            boleto.setDetalleRutaSalida(salida);
            boleto.setPrecioBoleto(precio);
            boleto.setTotalBoletos(asientosNuevos.size());
            boleto.setTotal(boleto.getTotal());

            boletoRepository.save(boleto);

            return ResponseEntity.ok(new Response(true, SystemText.General.PROCESO_EXITOSO, boleto));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(false, "Error al guardar boleto: " + e.getMessage(), null));
        }
    }

}
