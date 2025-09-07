/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 03 Jun 2025
 * @date 03/06/2025
 */
package com.example.tazuyuti_back.entities.modules;

import java.time.LocalTime;
import com.example.tazuyuti_back.helpers.SystemText;
import com.example.tazuyuti_back.validator.onCreate;
import com.example.tazuyuti_back.validator.onUpdate;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
@Table(schema = "modulos", name = "ruta")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Ruta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = SystemText.User.ENTITY_ID, example = "10")
    @NotNull(groups = onUpdate.class, message = SystemText.User.ENTITY_USUARIO_ID_REQUERIDO)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @Schema(description = SystemText.User.ENTITY_ROL, example = "{ \"id\": 1 }")
    @NotNull(groups = {onCreate.class, onUpdate.class}, message = "El Usuario es requerido")
    @JoinColumn(name = "unidad_id", referencedColumnName = "id")
    private Unidad unidad;

    @NotNull(groups = {onCreate.class, onUpdate.class}, message = "viaje requerido")
    @Column(name = "viaje", nullable = false)
    private String viaje;

    @NotNull(groups = {onCreate.class, onUpdate.class}, message = "repeticion requerido")
    @Column(name = "repeticion", nullable = false)
    private String repeticion;

    @NotNull(groups = {onCreate.class, onUpdate.class}, message = "La hora es requerida")
    @Schema(description = "Hora de salida", example = "08:30:00")
    @Column(name = "hora", nullable = false)
    @JsonFormat(pattern = "HH:mm")
    private LocalTime hora;

    @Builder.Default
    @Column(name = "estado", nullable = true)
    private Boolean estado = true;
}
