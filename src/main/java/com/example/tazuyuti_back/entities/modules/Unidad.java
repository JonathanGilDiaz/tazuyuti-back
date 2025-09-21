/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 03 Jun 2025
 * @date 03/06/2025
 */
package com.example.tazuyuti_back.entities.modules;

import com.example.tazuyuti_back.entities.administration.User;
import com.example.tazuyuti_back.entities.catalogs.TipoCamioneta;
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
import jakarta.persistence.OneToOne;
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
@Table(schema = "modulos", name = "unidades")
public class Unidad {

    @Id                        
    @GeneratedValue(strategy = GenerationType.IDENTITY)                      
    @Schema(description = SystemText.User.ENTITY_ID, example = "10")        
    @NotNull(groups = onUpdate.class, message = SystemText.User.ENTITY_USUARIO_ID_REQUERIDO)        
    @Column(name = "id")        
    private int id;

    @NotNull(groups = {onCreate.class, onUpdate.class}, message = "El nombre de la camioneta es requerido")         
    @Schema(description = "Nombre de la camioenta", example = "Mi carro")        
    @Column(name = "nombre", nullable = false)         
    private String nombre;

    @ManyToOne
    @Schema(description = SystemText.User.ENTITY_ROL, example = "{ \"id\": 1 }")        
    @NotNull(groups = {onCreate.class, onUpdate.class}, message = "El Usuario es requerido")            
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")                
    private User usuario;

    @Column(name = "placas", length = 20, nullable = true)         
    private String placas;

    @Schema(description = SystemText.User.ENTITY_ROL, example = "{ \"id\": 1 }")        
    @NotNull(groups = {onCreate.class, onUpdate.class}, message = "El tipo de camioneta es requerido")            
    @OneToOne               // This annotation indicates that it has a one-to-one relationship with the assigned table.
    @JoinColumn(name = "tipo_camioneta_id", referencedColumnName = "id")                
    private TipoCamioneta tipoCamioneta;

    @Builder.Default                    // This annotation indicates that the attribute has a default value
    @Schema(hidden = true)              // This annotation indicates that the attribute will not be displayed in the swagger
    @Column(name = "estado", nullable = true)         
    private Boolean estado = true;

}
