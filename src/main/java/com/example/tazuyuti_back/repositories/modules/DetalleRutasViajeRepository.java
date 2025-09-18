package com.example.tazuyuti_back.repositories.modules;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.example.tazuyuti_back.entities.modules.DetalleRutasViaje;

public interface DetalleRutasViajeRepository
        extends JpaRepository<DetalleRutasViaje, Integer>, JpaSpecificationExecutor<DetalleRutasViaje> {

    List<DetalleRutasViaje> findByDetalleRuta_IdAndBoleto_Estado(int detalleRutaId, String estado);

}
