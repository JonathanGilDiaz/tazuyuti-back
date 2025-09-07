package com.example.tazuyuti_back.dto.modules;

import java.util.List;
import com.example.tazuyuti_back.entities.modules.DetalleRuta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RutaDisponibleDTO {
    private int id; // id del primer tramo (para el select)
    private String viaje; // nombre del viaje
    private String hora; // hora de salida
    private List<DetalleRuta> tramos; // todos los tramos del viaje
}
