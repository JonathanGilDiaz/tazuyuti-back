/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 03 Jun 2025
 * @date 03/06/2025
 */
package com.example.tazuyuti_back.entities.modules;
   
import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.tazuyuti_back.helpers.SystemText;
import com.example.tazuyuti_back.validator.onUpdate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(schema = "modulos", name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = SystemText.Producto.ENTITY_ID, example = "10")
    @NotNull(groups = onUpdate.class, message = SystemText.Producto.ENTITY_ID_REQUERIDO)
    @Column(name = "id")
    private Integer id;

    @Schema(description = "Nombre del cliente", example = "Jonathan")        
    @Column(name = "nombre", nullable = true)         
    private String nombre;

    @Schema(description = "Apellido paterno  del cliente", example = "Diaz")        
    @Column(name = "apellido_paterno", nullable = true)         
    private String apellidoPaterno;

    @Schema(description = "Apellido materno del cliente", example = "Diaz")        
    @Column(name = "apellido_Materno", nullable = true)         
    private String apellidoMaterno;

    @Column(name = "nombre_comercial", nullable = true)         
    private String nombreComercial;

    @Column(name = "rfc", nullable = true)         
    private String rfc;

     @Column(name = "sociedad", nullable = true)         
    private String sociedad;

       @Column(name = "telefono", nullable = true)         
    private String telefono;

       @Column(name = "regimen_fiscal", nullable = true)         
    private String regimenFiscal;

       @Column(name = "direccion", nullable = true)         
    private String direccion;

       @Column(name = "codigo_postal", nullable = true)         
    private double codigoPostal;

       @Column(name = "tipo_persona", nullable = true)         
    private String tipoPersona;

     @Column(name = "estado", nullable = true)         
    private boolean estado;

    @CreationTimestamp
    @Schema(hidden = true)
    @Column(name = "fecha_creacion", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Timestamp fechaCreacion;

    @Temporal(TemporalType.TIMESTAMP)
    @Schema(hidden = true)
    @Column(name = "fecha_actualizacion", nullable = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Timestamp fechaActualizacion;

}
