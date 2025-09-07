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
import com.fasterxml.jackson.annotation.JsonBackReference;
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

    @JsonBackReference
    @ManyToOne(optional = false) // 🔹 Un detalle pertenece a UNA venta
    @JoinColumn(name = "ruta_id", referencedColumnName = "id", nullable = false)
    @NotNull(groups = { onCreate.class, onUpdate.class }, message = "La ruta es requerida")
    private Ruta ruta;

    @NotNull(groups = {onCreate.class, onUpdate.class}, message = "La fecha es requerida")
    @Schema(description = "Fecha de la ruta", example = "2025-09-05")
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "disponibilidad", nullable = true)
    private Integer disponibilidad;

    @NotNull(groups = {onCreate.class, onUpdate.class}, message = "ocupados requerido")         // This annotation indicates that this parameter must not be null.
    @Schema(description = "ocupados", example = "A1")        // This annotation indicates that information for the swagger
    @Column(name = "ocupados", nullable = true)         // This annotation indicates that Associates the attribute with a database column and indicates the validations it must have in order to be assigned a value.
    private String ocupados;

    @Schema(description = SystemText.User.ENTITY_SUCURSAL_ID, example = "{ \"id\": 1 }")        // This annotation indicates that information for the swagger
    @NotNull(groups = {onCreate.class, onUpdate.class}, message = SystemText.User.ENTITY_SUCURSAL_ID_REQUERIDO)            // This annotation indicates that this parameter must not be null.
    @ManyToOne              // This annotation indicates that it has a one-to-one relationship with the assigned table.
    @JoinColumn(name = "salida", referencedColumnName = "id")                // This annotation indicates that Relate the table to another
    private Sucursal salida;

    @Schema(description = SystemText.User.ENTITY_SUCURSAL_ID, example = "{ \"id\": 1 }")        // This annotation indicates that information for the swagger
    @NotNull(groups = {onCreate.class, onUpdate.class}, message = SystemText.User.ENTITY_SUCURSAL_ID_REQUERIDO)            // This annotation indicates that this parameter must not be null.
    @ManyToOne              // This annotation indicates that it has a one-to-one relationship with the assigned table.
    @JoinColumn(name = "llegada", referencedColumnName = "id")                // This annotation indicates that Relate the table to another
    private Sucursal llegada;

    @NotNull(groups = {onCreate.class, onUpdate.class}, message = "La hora es requerida")
    @Schema(description = "Hora de salida", example = "08:30:00-06")
    @Column(name = "salida_hora", nullable = false)
    private LocalTime  salidaHora;

    @NotNull(groups = {onCreate.class, onUpdate.class}, message = "La hora es requerida")
    @Schema(description = "Hora de salida", example = "08:30:00-06")
    @Column(name = "llegada_hora", nullable = false)
    private LocalTime  llegadaHora;

        @Builder.Default                    // This annotation indicates that the attribute has a default value
    @Column(name = "estado", nullable = true)         // This annotation indicates that Associates the attribute with a database column and indicates the validations it must have in order to be assigned a value.
    private Boolean estado = true;
   
}
