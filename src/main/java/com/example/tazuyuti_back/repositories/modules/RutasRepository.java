package com.example.tazuyuti_back.repositories.modules;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.example.tazuyuti_back.entities.modules.Ruta;

public interface RutasRepository extends JpaRepository<Ruta, Integer>, JpaSpecificationExecutor<Ruta> {

    List<Ruta> findByEstadoTrue();

}
