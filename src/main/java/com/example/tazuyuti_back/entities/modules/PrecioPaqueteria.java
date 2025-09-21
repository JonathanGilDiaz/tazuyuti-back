/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 03 Jun 2025
 * @date 03/06/2025
 */
package com.example.tazuyuti_back.entities.modules;

import com.example.tazuyuti_back.helpers.SystemText;
import com.example.tazuyuti_back.validator.onCreate;
import com.example.tazuyuti_back.validator.onUpdate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(schema = "modulos", name = "precio_paqueteria")
public class PrecioPaqueteria {

    @Id                        
    @GeneratedValue(strategy = GenerationType.IDENTITY)                      
    @Schema(description = SystemText.User.ENTITY_ID, example = "10")        
    @NotNull(groups = onUpdate.class, message = SystemText.User.ENTITY_USUARIO_ID_REQUERIDO)        
    @Column(name = "id")        
    private int id;

    @NotNull(groups = {onCreate.class, onUpdate.class}, message = "El nombre del precio es requerido")         
    @Schema(description = "Nombre del precio", example = "Caja grande")        
    @Column(name = "nombre", nullable = false)         
    private String nombre;

    @NotNull(groups = {onCreate.class, onUpdate.class}, message = "Descripción de los precios es requerido")         
    @Schema(description = "Descripción de los precios", example = "Es una caja grande")        
    @Column(name = "descripcion", nullable = false)         
    private String descripcion;

    @NotNull(groups = {onCreate.class, onUpdate.class}, message = "medidas de los precios es requerido")         
    @Schema(description = "medidas de los precios", example = "10x20")        
    @Column(name = "medidas", nullable = false)         
    private String medidas;

    @Column(name = "peso", nullable = true)         
    private double peso;

    @Column(name = "precio", nullable = true)         
    private double precio;

    @Builder.Default                    // This annotation indicates that the attribute has a default value
    @Schema(hidden = true)              // This annotation indicates that the attribute will not be displayed in the swagger
    @Column(name = "estado", nullable = true)         
    private Boolean estado = true;

}
