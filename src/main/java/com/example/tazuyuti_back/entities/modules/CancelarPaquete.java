/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 03 Jun 2025
 * @date 03/06/2025
 */
package com.example.tazuyuti_back.entities.modules;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

import com.example.tazuyuti_back.entities.administration.User;
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
@Table(schema = "modulos", name = "cancelar_paquete")
public class CancelarPaquete {

    @Id                        
    @GeneratedValue(strategy = GenerationType.IDENTITY)                      
    @Schema(description = SystemText.User.ENTITY_ID, example = "10")        
    @NotNull(groups = onUpdate.class, message = SystemText.User.ENTITY_USUARIO_ID_REQUERIDO)        
    @Column(name = "id")        
    private int id;

    @Schema(description = "descripcion", example = "Mi carro")        
    @Column(name = "descripcion", nullable = true)         
    private String descripcion;

    @ManyToOne
    @Schema(description = SystemText.User.ENTITY_ROL, example = "{ \"id\": 1 }")        
    @NotNull(groups = {onCreate.class, onUpdate.class}, message = "El Usuario es requerido")            
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")                
    private User usuario;

    @CreationTimestamp
    @Schema(hidden = true)
    @Column(name = "fecha   ", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Timestamp fecha;

    @OneToOne
        @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @Schema(description = SystemText.User.ENTITY_ROL, example = "{ \"id\": 1 }")        
    @NotNull(groups = {onCreate.class, onUpdate.class}, message = "El Usuario es requerido")            
    @JoinColumn(name = "paquete_id", referencedColumnName = "id")                
    private Paquete paquete;

}
