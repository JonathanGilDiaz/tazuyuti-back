/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 03 Jun 2025
 * @date 03/06/2025
 */
package com.example.tazuyuti_back.entities.modules;

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
@Table(schema = "modulos", name = "detalle_rutas_viaje")
public class DetalleRutasViaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = SystemText.User.ENTITY_ID, example = "10")
    @NotNull(groups = onUpdate.class, message = SystemText.User.ENTITY_USUARIO_ID_REQUERIDO)
    @Column(name = "id")
    private int id;

    @JsonBackReference
    @ManyToOne(optional = false) // 🔹 Un detalle pertenece a UNA venta
    @JoinColumn(name = "boleto_id", referencedColumnName = "id", nullable = false)
    @NotNull(groups = { onCreate.class, onUpdate.class }, message = "La venta es requerida")
    private Boleto boleto;


    @Schema(description = SystemText.User.ENTITY_ROL, example = "{ \"id\": 1 }")        
    @NotNull(groups = {onCreate.class, onUpdate.class}, message = "El tipo de camioneta es requerido")            
    @ManyToOne(optional = false)
    @JoinColumn(name = "detalle_ruta_id", referencedColumnName = "id")                
    private DetalleRuta detalleRuta;
}
