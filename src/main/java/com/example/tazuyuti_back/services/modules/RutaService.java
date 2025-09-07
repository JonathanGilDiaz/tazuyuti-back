package com.example.tazuyuti_back.services.modules;

import org.springframework.http.ResponseEntity;

import com.example.tazuyuti_back.entities.modules.Ruta;
import com.example.tazuyuti_back.models.utilities.Pagination;
import com.example.tazuyuti_back.models.utilities.Response;

import jakarta.servlet.http.HttpServletRequest;

public interface RutaService {

    ResponseEntity<Response> index(Pagination request);

    ResponseEntity<Response> catalogs();

    ResponseEntity<Response> save(Ruta ruta, HttpServletRequest request);

    ResponseEntity<Response> delete(int id, HttpServletRequest request);

    ResponseEntity<Response> update(Ruta ruta, HttpServletRequest request);

    ResponseEntity<Response> detail(int id);
}
