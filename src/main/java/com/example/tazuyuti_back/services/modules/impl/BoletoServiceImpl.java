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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.example.tazuyuti_back.entities.administration.User;
import com.example.tazuyuti_back.entities.modules.Bitacora;
import com.example.tazuyuti_back.entities.modules.Boleto;
import com.example.tazuyuti_back.entities.modules.DetalleRuta;
import com.example.tazuyuti_back.entities.modules.DetalleRutasViaje;
import com.example.tazuyuti_back.entities.modules.DetalleRutasViajePaquete;
import com.example.tazuyuti_back.entities.modules.Paquete;
import com.example.tazuyuti_back.entities.modules.PrecioBoleto;
import com.example.tazuyuti_back.entities.modules.Ruta;
import com.example.tazuyuti_back.entities.modules.Unidad;
import com.example.tazuyuti_back.helpers.DocumentHelper;
import com.example.tazuyuti_back.helpers.SystemText;
import com.example.tazuyuti_back.helpers.Utils;
import com.example.tazuyuti_back.models.utilities.Pagination;
import com.example.tazuyuti_back.models.utilities.Response;
import com.example.tazuyuti_back.repositories.administration.UserRepository;
import com.example.tazuyuti_back.repositories.catalogs.SucursalRepository;
import com.example.tazuyuti_back.repositories.modules.BitacoraRepository;
import com.example.tazuyuti_back.repositories.modules.BoletoRepository;
import com.example.tazuyuti_back.repositories.modules.DetalleRutaRepository;
import com.example.tazuyuti_back.repositories.modules.DetalleRutasViajePaqueteRepository;
import com.example.tazuyuti_back.repositories.modules.DetalleRutasViajeRepository;
import com.example.tazuyuti_back.repositories.modules.PaqueteRepository;
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

    @Autowired
    private DocumentHelper documentHelper;

    @Autowired
    private BitacoraRepository bitacoraRepository;

    @Autowired
    private PaqueteRepository paqueteRepository;

    @Autowired
    private DetalleRutasViajeRepository detalleRutasViajeRepository;

    @Autowired
    private DetalleRutasViajePaqueteRepository detalleRutasViajePaqueteRepository;

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
                    .findByEstadoAndFechaAndSalida_Id("Activo", fechaBuscada, origen);

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

            List<DetalleRutasViaje> detalleRutasViajeList = new ArrayList<>();
            List<DetalleRuta> tramosViaje = new ArrayList<DetalleRuta>();
            tramosViaje.add(salida);
            int idDetalleRutaSalida = salida.getId();
            int sucursaLSalida = salida.getSalida().getId();
            boolean bandera = true;
            while (bandera) {
                if (detalleRutaRepository.findById(idDetalleRutaSalida).get().getLlegada().getId() == precio.getEntre2()
                        .getId()) {
                    bandera = false;
                    break;
                }
                if (sucursaLSalida < precio.getEntre2().getId()) {
                    DetalleRuta rutaAgregar = detalleRutaRepository.findById(idDetalleRutaSalida + 1).get();
                    tramosViaje.add(rutaAgregar);

                    DetalleRutasViaje detalleRutasViaje = new DetalleRutasViaje();
                    detalleRutasViaje.setDetalleRuta(rutaAgregar);
                    detalleRutasViaje.setBoleto(boleto);
                    detalleRutasViajeList.add(detalleRutasViaje);

                    idDetalleRutaSalida++;
                } else {
                    DetalleRuta rutaAgregar = detalleRutaRepository.findById(idDetalleRutaSalida - 1).get();
                    tramosViaje.add(rutaAgregar);

                    DetalleRutasViaje detalleRutasViaje = new DetalleRutasViaje();
                    detalleRutasViaje.setDetalleRuta(rutaAgregar);
                    detalleRutasViaje.setBoleto(boleto);
                    detalleRutasViajeList.add(detalleRutasViaje);

                    idDetalleRutaSalida--;
                }
            }

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
            System.out.println("tramosViaje " + tramosViaje.size());
            System.out.println("La fecha : " + primerTramo.getFecha() + " la hora: " + primerTramo.getSalidaHora());
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

            for (DetalleRutasViaje relacion : detalleRutasViajeList) {
                relacion.setBoleto(boleto); // asegurar que tenga el boleto ya persistido
                detalleRutasViajeRepository.save(relacion);
            }

            return ResponseEntity.ok(new Response(true, SystemText.General.PROCESO_EXITOSO, boleto));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(false, "Error al guardar boleto: " + e.getMessage(), null));
        }
    }

    @Override
    public ResponseEntity<Response> detail(int id) {
        Optional<Boleto> item = boletoRepository.findById(id);
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
        Optional<Boleto> ventaOp = boletoRepository.findById(id);
        if (ventaOp.isPresent()) {
            Map<String, Object> pdfData = documentHelper.createTicketBoleto(ventaOp.get());
            return ResponseEntity.ok(new Response(true, "Ticket generado", pdfData));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, "Venta no encontrada", null));
        }
    }

    @Transactional
    @Override
    public ResponseEntity<Response> cancelar(int boletoId) {
        try {
            // 1. Buscar el boleto
            Boleto boleto = boletoRepository.findById(boletoId)
                    .orElseThrow(() -> new RuntimeException("Boleto no encontrado"));

            if ("Cancelado".equalsIgnoreCase(boleto.getEstado())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new Response(false, "El boleto ya está cancelado.", null));
            }

            if ("Cerrado".equals(boleto.getDetalleRutaSalida().getEstado())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new Response(false, "No se puede cancelar, el recorrido ya ha comenzado.", null));

            }

            // 2. Obtener salida y destino
            DetalleRuta salida = detalleRutaRepository.findById(boleto.getDetalleRutaSalida().getId())
                    .orElseThrow(() -> new RuntimeException("DetalleRuta salida no encontrada"));

            PrecioBoleto precio = precioBoletoRepository.findById(boleto.getPrecioBoleto().getId())
                    .orElseThrow(() -> new RuntimeException("Precio no encontrado"));

            Ruta ruta = salida.getRuta();

            // 3. Tramos de la ruta para ese día (misma lógica que en save)
            List<DetalleRuta> tramosViaje = new ArrayList<DetalleRuta>();
            tramosViaje.add(salida);
            int idDetalleRutaSalida = salida.getId();
            int sucursaLSalida = salida.getSalida().getId();
            System.out.println("El id de la sucrusal " + idDetalleRutaSalida);
            boolean bandera = true;
            while (bandera) {
                if (detalleRutaRepository.findById(idDetalleRutaSalida).get().getLlegada().getId() == precio.getEntre2()
                        .getId()) {
                    bandera = false;
                    break;
                }
                if (sucursaLSalida < precio.getEntre2().getId()) {
                    tramosViaje.add(detalleRutaRepository.findById(idDetalleRutaSalida + 1).get());
                    idDetalleRutaSalida++;
                } else {
                    tramosViaje.add(detalleRutaRepository.findById(idDetalleRutaSalida - 1).get());
                    idDetalleRutaSalida--;
                }
            }

            if (tramosViaje.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Response(false, "No se encontraron tramos para cancelar.", null));
            }

            // 5. Asientos que deben liberarse
            Set<Integer> asientosCancelados = Arrays.stream(boleto.getAsientos().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .map(Integer::parseInt)
                    .collect(HashSet::new, HashSet::add, HashSet::addAll);

            // 6. Actualizar ocupados en cada tramo
            for (DetalleRuta tramo : tramosViaje) {
                Set<Integer> ocupados = new HashSet<>();
                if (tramo.getOcupados() != null) {
                    Arrays.stream(tramo.getOcupados().split(","))
                            .map(String::trim)
                            .filter(s -> !s.isBlank())
                            .map(Integer::parseInt)
                            .forEach(ocupados::add);
                }

                // Eliminar los asientos cancelados
                ocupados.removeAll(asientosCancelados);

                // Guardar ocupados actualizados
                tramo.setOcupados(
                        ocupados.stream()
                                .sorted()
                                .map(String::valueOf)
                                .reduce((a, b) -> a + "," + b)
                                .orElse(""));

                // Recalcular disponibilidad
                int capacidad = ruta.getUnidad().getTipoCamioneta().getCapacidad();
                tramo.setDisponibilidad(capacidad - ocupados.size());

                detalleRutaRepository.save(tramo);
            }

            // 7. Actualizar boleto como cancelado
            boleto.setEstado("Cancelado");
            boletoRepository.save(boleto);

            return ResponseEntity.ok(new Response(true, "Boleto cancelado con éxito.", boleto));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(false, "Error al cancelar boleto: " + e.getMessage(), null));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public ResponseEntity<Response> indexBitacoras(int idUsuario, Pagination requestT) {
        User usuario = userRepository.findById(idUsuario).get();
        int sucursalId = usuario.getSucursal().getId();
        Map<String, Object> data = Utils.getSpecificationAndPageable(requestT, DetalleRuta.class);
        Specification<DetalleRuta> specs = (Specification<DetalleRuta>) data.get("specification");
        Specification<DetalleRuta> filtroSucursal = (root, query, cb) -> cb
                .equal(root.get("salida").get("id"), sucursalId);
        Specification<DetalleRuta> finalSpecs = specs == null ? filtroSucursal : specs.and(filtroSucursal);
        Page<DetalleRuta> list = detalleRutaRepository.findAll(finalSpecs, (Pageable) data.get("pageable"));
        if (list.hasContent()) {
            return ResponseEntity.ok(new Response(true, SystemText.General.PROCESO_EXITOSO, list));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, SystemText.General.REGISTRO_NO_ENCONTRADO, null));
        }
    }

    @Override
    @Transactional
    public ResponseEntity<Response> cerrarViaje(Bitacora bitacora) {
        try {
            Optional<DetalleRuta> item = detalleRutaRepository.findById(bitacora.getDetalleRuta().getId());
            if (!item.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Response(false, SystemText.General.REGISTRO_NO_ENCONTRADO, null));
            }

            DetalleRuta detalleRuta = item.get();
            detalleRuta.setEstado("Cerrado");
            detalleRutaRepository.save(detalleRuta);

            List<Boleto> boletos = boletoRepository.findByDetalleRutaSalida_IdAndEstado(detalleRuta.getId(), "Activo");

            if (boletos.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Response(false, "No se encontraron boletos para este viaje", null));
            }

            double total = boletos.stream()
                    .mapToDouble(Boleto::getTotal)
                    .sum();

            User usuarioActual = userRepository.findById(bitacora.getUsuario().getId()).get();
            String nombreSucursal = usuarioActual.getSucursal().getNombre();
            String letraSucursal = nombreSucursal.substring(0, 1).toUpperCase();
            long conteo = /* necesitas un repository para bitácora */
                    bitacoraRepository.countByUsuario_Sucursal_Id(usuarioActual.getSucursal().getId());
            String folio = "BI" + letraSucursal + (conteo + 1);

            // 🔹 4. Completar la bitácora
            bitacora.setTotal(total);
            bitacora.setFolio(folio);
            bitacora.setDetalleRuta(detalleRuta);

            bitacoraRepository.save(bitacora);

            return ResponseEntity.ok(new Response(true, "Viaje cerrado con éxito", bitacora));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(false, "Error al cerrar viaje: " + e.getMessage(), null));
        }
    }

    @Override
    public ResponseEntity<Response> detailDetalleRuta(int id) {
        Optional<DetalleRuta> item = detalleRutaRepository.findById(id);

        if (item.isPresent()) {
            DetalleRuta detalleRuta = item.get();

            List<Boleto> boletos = boletoRepository.findByDetalleRutaSalida_IdAndEstado(id, "Activo");
            List<Paquete> paquetes = paqueteRepository.findByDetalleRutaAndEstadoNot5(id);

            List<DetalleRutasViaje> detalleRutasViajeActivos = detalleRutasViajeRepository
                    .findByDetalleRuta_IdAndBoleto_Estado(id, "Activo");
            List<Boleto> boletosSiguen = detalleRutasViajeActivos.stream()
                    .map(DetalleRutasViaje::getBoleto)
                    .collect(Collectors.toList());

            List<DetalleRutasViajePaquete> detalleRutasViajePaquetesActivos = detalleRutasViajePaqueteRepository
                    .findByDetalleRutaAndPaqueteEstadoNot5(id);

            List<Paquete> paquetesSiguen = detalleRutasViajePaquetesActivos.stream()
                    .map(DetalleRutasViajePaquete::getPaquete)
                    .collect(Collectors.toList());

            Bitacora bitacora = new Bitacora();
            Optional<Bitacora> bitacoraOp = bitacoraRepository.findByDetalleRuta_Id(detalleRuta.getId());
            if(bitacoraOp.isPresent()){
                bitacora = bitacoraOp.get();
            }
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("detalleRuta", detalleRuta);
            responseData.put("boletosEnviados", boletos);
            responseData.put("paquetesEnviados", paquetes);
            responseData.put("boletosSiguen", boletosSiguen);
            responseData.put("paquetesSiguen", paquetesSiguen);
            responseData.put("bitacora", bitacora);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new Response(true, SystemText.General.REGISTRO_ENCONTRADO, responseData));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, SystemText.General.REGISTRO_NO_ENCONTRADO, null));
        }
    }

     @SuppressWarnings("unchecked")
    @Override
    public ResponseEntity<Response> indexBitacorasChofer(int idUsuario, Pagination requestT) {
        User usuario = userRepository.findById(idUsuario).get();
        int sucursalId = usuario.getSucursal().getId();
        Map<String, Object> data = Utils.getSpecificationAndPageable(requestT, DetalleRuta.class);
        Specification<DetalleRuta> specs = (Specification<DetalleRuta>) data.get("specification");
        Specification<DetalleRuta> filtroSucursal = (root, query, cb) -> cb
                .equal(root.get("ruta").get("unidad").get("usuario").get("id"), sucursalId);
        Specification<DetalleRuta> finalSpecs = specs == null ? filtroSucursal : specs.and(filtroSucursal);
        Page<DetalleRuta> list = detalleRutaRepository.findAll(finalSpecs, (Pageable) data.get("pageable"));
        if (list.hasContent()) {
            return ResponseEntity.ok(new Response(true, SystemText.General.PROCESO_EXITOSO, list));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, SystemText.General.REGISTRO_NO_ENCONTRADO, null));
        }
    }
}
