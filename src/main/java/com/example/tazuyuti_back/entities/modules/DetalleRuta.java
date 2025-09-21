/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 03 Jun 2025
 * @date 03/06/2025
 */
package com.example.tazuyuti_back.entities.modules;

import java.time.LocalDate;
import java.time.LocalTime;
import com.example.tazuyuti_back.entities.catalogs.Sucursal;
import com.example.tazuyuti_back.helpers.SystemText;
import com.example.tazuyuti_back.validator.onCreate;
import com.example.tazuyuti_back.validator.onUpdate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(schema = "modulos", name = "detalle_ruta")
public class DetalleRuta {

     @Id                        
    @GeneratedValue(strategy = GenerationType.IDENTITY)                      
    @Schema(description = SystemText.User.ENTITY_ID, example = "10")        
    @NotNull(groups = onUpdate.class, message = SystemText.User.ENTITY_USUARIO_ID_REQUERIDO)        
    @Column(name = "id")        
    private int id;

    @ManyToOne
    @Schema(description = SystemText.User.ENTITY_ROL, example = "{ \"id\": 1 }")        
    @NotNull(groups = {onCreate.class, onUpdate.class}, message = "El Usuario es requerido")            
    @JoinColumn(name = "ruta_id", referencedColumnName = "id")                
    private Ruta ruta;

    @NotNull(groups = { onCreate.class, onUpdate.class }, message = "La fecha es requerida")
    @Schema(description = "Fecha de la ruta", example = "2025-09-05")
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "disponibilidad", nullable = true)
    private Integer disponibilidad;

    @Column(name = "ocupados", nullable = true)
    private String ocupados;

    @NotNull(groups = { onCreate.class, onUpdate.class }, message = SystemText.User.ENTITY_SUCURSAL_ID_REQUERIDO)
    @ManyToOne
    @JoinColumn(name = "salida", referencedColumnName = "id")
    private Sucursal salida;

    @NotNull(groups = { onCreate.class, onUpdate.class }, message = SystemText.User.ENTITY_SUCURSAL_ID_REQUERIDO)
    @ManyToOne
    @JoinColumn(name = "llegada", referencedColumnName = "id")
    private Sucursal llegada;

    @NotNull(groups = { onCreate.class, onUpdate.class }, message = "La hora es requerida")
    @Column(name = "salida_hora", nullable = false)
    private LocalTime salidaHora;

    @NotNull(groups = { onCreate.class, onUpdate.class }, message = "La hora es requerida")
    @Column(name = "llegada_hora", nullable = false)
    private LocalTime llegadaHora;

    @Column(name = "estado", nullable = true)
    private String estado;
}
