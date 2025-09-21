/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 03 Jun 2025
 * @date 03/06/2025
 */
package com.example.tazuyuti_back.entities.modules;

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
@Table(schema = "modulos", name = "precios_boletos")
public class PrecioBoleto {

    @Id                        
    @GeneratedValue(strategy = GenerationType.IDENTITY)                      
    @Schema(description = SystemText.User.ENTITY_ID, example = "10")        
    @NotNull(groups = onUpdate.class, message = SystemText.User.ENTITY_USUARIO_ID_REQUERIDO)        
    @Column(name = "id")        
    private int id;

    @Schema(description = SystemText.User.ENTITY_SUCURSAL_ID, example = "{ \"id\": 1 }")        
    @NotNull(groups = {onCreate.class, onUpdate.class}, message = SystemText.User.ENTITY_SUCURSAL_ID_REQUERIDO)            
    @ManyToOne              // This annotation indicates that it has a one-to-one relationship with the assigned table.
    @JoinColumn(name = "origen", referencedColumnName = "id")                
    private Sucursal origen;

    @NotNull(groups = {onCreate.class, onUpdate.class}, message = "destino requerido")         
    @Schema(description = "destino", example = "Caja grande")        
    @Column(name = "destino", nullable = false)         
    private String destino;

    @Schema(description = SystemText.User.ENTITY_SUCURSAL_ID, example = "{ \"id\": 1 }")        
    @NotNull(groups = {onCreate.class, onUpdate.class}, message = SystemText.User.ENTITY_SUCURSAL_ID_REQUERIDO)            
    @ManyToOne              // This annotation indicates that it has a one-to-one relationship with the assigned table.
    @JoinColumn(name = "entre_1", referencedColumnName = "id")                
    private Sucursal entre1;

       @Schema(description = SystemText.User.ENTITY_SUCURSAL_ID, example = "{ \"id\": 1 }")        
    @NotNull(groups = {onCreate.class, onUpdate.class}, message = SystemText.User.ENTITY_SUCURSAL_ID_REQUERIDO)            
    @ManyToOne              // This annotation indicates that it has a one-to-one relationship with the assigned table.
    @JoinColumn(name = "entre_2", referencedColumnName = "id")                
    private Sucursal entre2;

    @Column(name = "precio", nullable = true)         
    private double precio;

    @Builder.Default                    // This annotation indicates that the attribute has a default value
    @Schema(hidden = true)              // This annotation indicates that the attribute will not be displayed in the swagger
    @Column(name = "estado", nullable = true)         
    private Boolean estado = true;
}
