package com.example.tazuyuti_back.repositories.modules;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.example.tazuyuti_back.entities.modules.OrdenCompra;

public interface OrdenCompraRepository extends JpaRepository<OrdenCompra, Integer>, JpaSpecificationExecutor<OrdenCompra> {

}
