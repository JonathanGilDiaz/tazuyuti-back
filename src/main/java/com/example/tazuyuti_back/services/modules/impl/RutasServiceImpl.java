/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Feb 2025
 * @date 23/02/2025
 */
package com.example.tazuyuti_back.services.modules.impl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.example.tazuyuti_back.entities.catalogs.Sucursal;
import com.example.tazuyuti_back.entities.modules.DetalleRuta;
import com.example.tazuyuti_back.entities.modules.Ruta;
import com.example.tazuyuti_back.helpers.SystemText;
import com.example.tazuyuti_back.helpers.Utils;
import com.example.tazuyuti_back.models.utilities.Pagination;
import com.example.tazuyuti_back.models.utilities.Response;
import com.example.tazuyuti_back.repositories.catalogs.SucursalRepository;
import com.example.tazuyuti_back.repositories.modules.DetalleRutaRepository;
import com.example.tazuyuti_back.repositories.modules.RutasRepository;
import com.example.tazuyuti_back.repositories.modules.UnidadRepository;
import com.example.tazuyuti_back.services.modules.RutaService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@Service
public class RutasServiceImpl implements RutaService {

        @Autowired
        private RutasRepository rutasRepository;

        @Autowired
        private UnidadRepository unidadRepository;

        @Autowired
        private DetalleRutaRepository detalleRutaRepository;

        @Autowired
        private SucursalRepository sucursalRepository;

      

        @Override
        public ResponseEntity<Response> catalogs() {
                Map<String, Object> response = new HashMap<>();
                response.put("unidades", unidadRepository.findByEstadoTrue());
                return ResponseEntity.status(HttpStatus.OK)
                                .body(new Response(true, SystemText.General.PROCESO_EXITOSO, response));
        }

        @SuppressWarnings("unchecked")
        @Override
        public ResponseEntity<Response> index(Pagination requestT) {
                Map<String, Object> data = Utils.getSpecificationAndPageable(requestT, Ruta.class);
                Specification<Ruta> specs = (Specification<Ruta>) data.get("specification");
                Specification<Ruta> estadoTrue = (root, query, cb) -> cb.isTrue(root.get("estado"));
                Specification<Ruta> finalSpec = specs.and(estadoTrue);
                Page<Ruta> list = rutasRepository.findAll(finalSpec, (Pageable) data.get("pageable"));
                if (list.hasContent()) {
                        return ResponseEntity.ok(new Response(true, SystemText.General.PROCESO_EXITOSO, list));
                } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(new Response(false, SystemText.General.REGISTRO_NO_ENCONTRADO, null));
                }
        }

        @Override
        @Transactional
        public ResponseEntity<Response> save(Ruta ruta, HttpServletRequest request) {
                try {
                        Ruta rutaGuardada = rutasRepository.save(ruta);

                        Sucursal sucursalOaxaca = sucursalRepository.findById(1)
                                        .orElseThrow(() -> new RuntimeException("Sucursal Oaxaca no encontrada"));
                        Sucursal sucursalHuajuapam = sucursalRepository.findById(2)
                                        .orElseThrow(() -> new RuntimeException("Sucursal Huajuapam no encontrada"));
                        Sucursal sucursalTonala = sucursalRepository.findById(3)
                                        .orElseThrow(() -> new RuntimeException("Sucursal Tonalá no encontrada"));
                        Sucursal sucursalJuxtlahuaca = sucursalRepository.findById(4)
                                        .orElseThrow(() -> new RuntimeException("Sucursal Juxtlahuaca no encontrada"));

                        LocalDate hoy = LocalDate.now();
                        LocalTime horaActual = LocalTime.now();

                        List<String> diasRepeticion = Arrays.stream(ruta.getRepeticion().split(","))
                                        .map(String::toLowerCase)
                                        .toList();

                        int generados = 0;
                        LocalDate fecha = hoy;

                        while (generados < 3) {
                                String diaSemana = fecha.getDayOfWeek()
                                                .getDisplayName(TextStyle.FULL, new Locale("es", "MX"))
                                                .toLowerCase();

                                if (diasRepeticion.contains(diaSemana)) {
                                        if (!fecha.equals(hoy) || horaActual.isBefore(ruta.getHora())) {
                                                if ("oaxaca-juxtlahuaca".equalsIgnoreCase(ruta.getViaje())) {
                                                        crearDetalle(rutaGuardada, fecha, ruta.getHora(),
                                                                        sucursalOaxaca, sucursalHuajuapam, 3);
                                                        crearDetalle(rutaGuardada, fecha, ruta.getHora().plusHours(3),
                                                                        sucursalHuajuapam, sucursalTonala, 1.5);
                                                        crearDetalle(rutaGuardada, fecha,
                                                                        ruta.getHora().plusHours(5).plusMinutes(30),
                                                                        sucursalTonala, sucursalJuxtlahuaca, 0);
                                                } else if ("juxtlahuaca-oaxaca".equalsIgnoreCase(ruta.getViaje())) {
                                                        crearDetalle(rutaGuardada, fecha, ruta.getHora(),
                                                                        sucursalJuxtlahuaca, sucursalTonala, 2.5);
                                                        crearDetalle(rutaGuardada, fecha,
                                                                        ruta.getHora().plusHours(2).plusMinutes(30),
                                                                        sucursalTonala, sucursalHuajuapam, 2);
                                                        crearDetalle(rutaGuardada, fecha,
                                                                        ruta.getHora().plusHours(4).plusMinutes(30),
                                                                        sucursalHuajuapam, sucursalOaxaca, 1);
                                                }
                                                generados++;
                                        }
                                }

                                fecha = fecha.plusDays(1);
                        }

                        return ResponseEntity.status(HttpStatus.OK)
                                        .body(new Response(true, SystemText.General.PROCESO_EXITOSO, rutaGuardada));
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new Response(false, "Error al guardar la ruta: " + e.getMessage(), null));
                }
        }

        private void crearDetalle(Ruta ruta, LocalDate fechaBase, LocalTime horaSalida,
                        Sucursal salida, Sucursal llegada, double horasExtra) {
                DetalleRuta detalle = new DetalleRuta();
                detalle.setRuta(ruta);
                detalle.setSalida(salida);
                detalle.setLlegada(llegada);

                detalle.setFecha(fechaBase);
                detalle.setSalidaHora(horaSalida);

                LocalTime horaLlegada = horaSalida.plusMinutes((long) (horasExtra * 60));
                LocalDate fechaLlegada = fechaBase;
                if (horaLlegada.isAfter(horaSalida)) {
                        fechaLlegada = fechaBase.plusDays(1);
                }
                detalle.setFecha(fechaLlegada);
                detalle.setLlegadaHora(horaLlegada);

                detalleRutaRepository.save(detalle);
        }

        @Scheduled(cron = "0 0 * * * *")
        @Transactional
        public void mantenerRutas() {
                List<Ruta> rutas = rutasRepository.findByEstadoTrue();

                for (Ruta ruta : rutas) {
                        generarDetallesFaltantes(ruta);
                }
        }

        private void generarDetallesFaltantes(Ruta ruta) {
                Sucursal sucursalOaxaca = sucursalRepository.findById(1).orElseThrow();
                Sucursal sucursalHuajuapam = sucursalRepository.findById(2).orElseThrow();
                Sucursal sucursalTonala = sucursalRepository.findById(3).orElseThrow();
                Sucursal sucursalJuxtlahuaca = sucursalRepository.findById(4).orElseThrow();
                List<String> diasRepeticion = Arrays.stream(ruta.getRepeticion().split(","))
                                .map(String::toLowerCase)
                                .toList();

                LocalDate hoy = LocalDate.now();
                for (int i = 0; i < 3; i++) {
                        LocalDate fecha = hoy.plusDays(i);

                        String diaSemana = fecha.getDayOfWeek()
                                        .getDisplayName(TextStyle.FULL, new Locale("es", "MX"))
                                        .toLowerCase();

                        boolean existe = detalleRutaRepository.existsByRutaIdAndFecha(ruta.getId(), fecha);

                        if (!existe && diasRepeticion.contains(diaSemana)) {
                                if ("oaxaca-juxtlahuaca".equalsIgnoreCase(ruta.getViaje())) {
                                        crearDetalle(ruta, fecha, ruta.getHora(),
                                                        sucursalOaxaca, sucursalHuajuapam, 3);
                                        crearDetalle(ruta, fecha, ruta.getHora().plusHours(3),
                                                        sucursalHuajuapam, sucursalTonala, 1.5);
                                        crearDetalle(ruta, fecha, ruta.getHora().plusHours(5).plusMinutes(30),
                                                        sucursalTonala, sucursalJuxtlahuaca, 0);
                                } else if ("juxtlahuaca-oaxaca".equalsIgnoreCase(ruta.getViaje())) {
                                        crearDetalle(ruta, fecha, ruta.getHora(),
                                                        sucursalJuxtlahuaca, sucursalTonala, 2.5);
                                        crearDetalle(ruta, fecha, ruta.getHora().plusHours(2).plusMinutes(30),
                                                        sucursalTonala, sucursalHuajuapam, 2);
                                        crearDetalle(ruta, fecha, ruta.getHora().plusHours(4).plusMinutes(30),
                                                        sucursalHuajuapam, sucursalOaxaca, 1);
                                }
                        }
                }
        }

        @Override
        @Transactional
        public ResponseEntity<Response> delete(int id, HttpServletRequest request) {
                try {
                        Optional<Ruta> itemOp = rutasRepository.findById(id);
                        if (!itemOp.isPresent()) {
                                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                .body(new Response(false, SystemText.General.REGISTRO_NO_ENCONTRADO,
                                                                null));
                        }
                        try {
                                rutasRepository.deleteById(id);
                        } catch (Exception e) {
                                Ruta item = itemOp.get();
                                item.setEstado(false);
                                rutasRepository.save(item);
                        }
                        return ResponseEntity.status(HttpStatus.OK)
                                        .body(new Response(true, SystemText.General.REGISTRO_ELIMINADO_CORRECTAMENTE,
                                                        null));
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new Response(false, SystemText.General.NO_ELIMINAR_REGISTRO_ASOCIADO,
                                                        null));
                }

        }

        @Override
        @Transactional
        public ResponseEntity<Response> update(Ruta ruta, HttpServletRequest request) {
                Optional<Ruta> op = rutasRepository.findById(ruta.getId());
                if (op.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(new Response(false, SystemText.General.REGISTRO_NO_ENCONTRADO, null));
                }

                Ruta actual = op.get();

                var unidadNueva = unidadRepository.findById(ruta.getUnidad().getId())
                                .orElseThrow(() -> new RuntimeException("Unidad no encontrada"));

                var unidadAnterior = actual.getUnidad();
                int capNueva = unidadNueva.getTipoCamioneta().getCapacidad();
                int capAnterior = unidadAnterior.getTipoCamioneta().getCapacidad();

                List<DetalleRuta> detalles = detalleRutaRepository
                                .findByRutaIdAndEstadoTrueOrderByFechaAscIdAsc(actual.getId());

                if (capNueva < capAnterior) {
                        for (DetalleRuta d : detalles) {
                                var ocupados = parseOcupados(d.getOcupados());
                                if (ocupados.size() > capNueva) {
                                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                                        .body(new Response(false,
                                                                        "No se puede cambiar a una unidad de "
                                                                                        + capNueva +
                                                                                        " asientos: el día "
                                                                                        + d.getFecha() +
                                                                                        " tiene " + ocupados.size()
                                                                                        + " asientos ocupados.",
                                                                        null));
                                }
                                if (ocupados.stream().anyMatch(n -> n > capNueva)) {
                                        d.setOcupados(formatear(reasignarAjustandoCapacidad(ocupados, capNueva)));
                                }
                        }
                }

                actual.setUnidad(unidadNueva);
                actual.setViaje(ruta.getViaje());
                actual.setRepeticion(ruta.getRepeticion());
                actual.setHora(ruta.getHora());
                if (ruta.getEstado() != null) {
                        actual.setEstado(ruta.getEstado());
                }
                rutasRepository.save(actual);

                Sucursal oaxaca = sucursalRepository.findById(1).orElseThrow();
                Sucursal huajuapam = sucursalRepository.findById(2).orElseThrow();
                Sucursal tonala = sucursalRepository.findById(3).orElseThrow();
                Sucursal juxtlahuaca = sucursalRepository.findById(4).orElseThrow();

                int idxTramo = 0;

                for (DetalleRuta d : detalles) {
                        var t0 = actual.getHora();
                        LocalDate fechaBase = d.getFecha(); // 👈 siempre respetar fecha de salida

                        if ("Oaxaca-Juxtlahuaca".equalsIgnoreCase(actual.getViaje())) {
                                switch (idxTramo) {
                                        case 0 -> {
                                                d.setSalida(oaxaca);
                                                d.setLlegada(huajuapam);
                                                d.setSalidaHora(t0);
                                                LocalTime llegada = t0.plusHours(3);
                                                d.setLlegadaHora(llegada);
                                        }
                                        case 1 -> {
                                                d.setSalida(huajuapam);
                                                d.setLlegada(tonala);
                                                LocalTime salidaHora = t0.plusHours(3);
                                                LocalTime llegada = t0.plusHours(4).plusMinutes(30);
                                                d.setSalidaHora(salidaHora);
                                                d.setLlegadaHora(llegada);
                                        }
                                        case 2 -> {
                                                d.setSalida(tonala);
                                                d.setLlegada(juxtlahuaca);
                                                LocalTime salidaHora = t0.plusHours(5).plusMinutes(30);
                                                d.setSalidaHora(salidaHora);
                                                d.setLlegadaHora(salidaHora); // mismo día
                                        }
                                }
                        } else if ("Juxtlahuaca-Oaxaca".equalsIgnoreCase(actual.getViaje())) {
                                switch (idxTramo) {
                                        case 0 -> {
                                                d.setSalida(juxtlahuaca);
                                                d.setLlegada(tonala);
                                                d.setSalidaHora(t0);
                                                d.setLlegadaHora(t0.plusHours(2).plusMinutes(30));
                                        }
                                        case 1 -> {
                                                d.setSalida(tonala);
                                                d.setLlegada(huajuapam);
                                                LocalTime salidaHora = t0.plusHours(2).plusMinutes(30);
                                                LocalTime llegada = t0.plusHours(4).plusMinutes(30);
                                                d.setSalidaHora(salidaHora);
                                                d.setLlegadaHora(llegada);
                                        }
                                        case 2 -> {
                                                d.setSalida(huajuapam);
                                                d.setLlegada(oaxaca);
                                                LocalTime salidaHora = t0.plusHours(4).plusMinutes(30);
                                                LocalTime llegada = t0.plusHours(5).plusMinutes(30);
                                                d.setSalidaHora(salidaHora);
                                                d.setLlegadaHora(llegada);
                                        }
                                }
                        }

                        if (d.getLlegadaHora().isAfter(d.getSalidaHora())) {
                                d.setFecha(fechaBase.plusDays(1));
                        } else {
                                d.setFecha(fechaBase);
                        }

                        detalleRutaRepository.save(d);
                        idxTramo++;

                }

                return ResponseEntity.ok(new Response(true, SystemText.General.PROCESO_EXITOSO, null));
        }

        private Set<Integer> parseOcupados(String s) {
                if (s == null || s.isBlank())
                        return new TreeSet<>();
                return Arrays.stream(s.split(","))
                                .map(String::trim)
                                .filter(x -> !x.isEmpty())
                                .map(Integer::parseInt)
                                .collect(Collectors.toCollection(TreeSet::new));
        }

        private String formatear(Set<Integer> seats) {
                return seats.stream().sorted()
                                .map(String::valueOf)
                                .collect(Collectors.joining(","));
        }

        /**
         * Reasigna asientos > capNueva a los lugares disponibles dentro [1..capNueva].
         * Si no hay suficientes lugares libres, lanza excepción (pero arriba ya
         * validamos size<=capNueva).
         */
        private Set<Integer> reasignarAjustandoCapacidad(Set<Integer> ocupados, int capNueva) {
                Set<Integer> dentro = ocupados.stream().filter(n -> n <= capNueva)
                                .collect(Collectors.toCollection(TreeSet::new));
                List<Integer> fuera = ocupados.stream().filter(n -> n > capNueva)
                                .sorted()
                                .toList();

                Deque<Integer> libres = new ArrayDeque<>();
                for (int i = 1; i <= capNueva; i++) {
                        if (!dentro.contains(i))
                                libres.add(i);
                }

                for (@SuppressWarnings("unused")
                int asientoFuera : fuera) {
                        if (libres.isEmpty()) {
                                throw new IllegalStateException("No hay asientos libres para reubicar.");
                        }
                        int nuevoAsiento = libres.removeFirst();
                        dentro.add(nuevoAsiento);
                }
                return dentro;

        }

        @Override
        public ResponseEntity<Response> detail(int id) {
                Optional<Ruta> item = rutasRepository.findById(id);
                if (item.isPresent()) {
                        return ResponseEntity.status(HttpStatus.OK)
                                        .body(new Response(true, SystemText.General.REGISTRO_ENCONTRADO, item.get()));
                } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(new Response(false, SystemText.General.REGISTRO_ENCONTRADO, null));
                }
        }

}
