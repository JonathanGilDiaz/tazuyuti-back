/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 03 Jun 2025
 * @date 03/06/2025
 */
package com.example.tazuyuti_back.entities.modules;

import java.sql.Timestamp;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;
import com.example.tazuyuti_back.entities.administration.User;
import com.example.tazuyuti_back.helpers.SystemText;
import com.example.tazuyuti_back.validator.onCreate;
import com.example.tazuyuti_back.validator.onUpdate;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
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
@Table(schema = "modulos", name = "boleto")
public class Boleto {

    @Id                        
    @GeneratedValue(strategy = GenerationType.IDENTITY)                      
    @Schema(description = SystemText.User.ENTITY_ID, example = "10")        
    @NotNull(groups = onUpdate.class, message = SystemText.User.ENTITY_USUARIO_ID_REQUERIDO)        
    @Column(name = "id")        
    private int id;

    @ManyToOne
    @Schema(description = SystemText.User.ENTITY_ROL, example = "{ \"id\": 1 }")        
    @NotNull(groups = {onCreate.class, onUpdate.class}, message = "El Usuario es requerido")            
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")                
    private User usuario;

    @NotNull(groups = {onCreate.class, onUpdate.class}, message = "La forma de pago es requerido")         
    @Schema(description = "Nombre de la camioenta", example = "01 Efectivo")        
    @Column(name = "forma_pago", nullable = false)         
    private String formaPago;

    @NotNull(groups = {onCreate.class, onUpdate.class}, message = "Total de la venta es requerido")         
    @Schema(description = "Total de la venta", example = "10.50")        
    @Column(name = "total", nullable = false)         
    private double total;

    @CreationTimestamp
    @Schema(hidden = true)
    @Column(name = "fecha_creacion", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
    private Timestamp fechaCreacion;

    @Column(name = "estado", nullable = true)         
    private String estado;

    @Schema(description = "Folio de la venta", example = "D58")        
    @Column(name = "folio", nullable = true)         
    private String folio;

    @Schema(description = SystemText.User.ENTITY_ROL, example = "{ \"id\": 1 }")        
    @NotNull(groups = {onCreate.class, onUpdate.class}, message = "El tipo de camioneta es requerido")            
    @ManyToOne(optional = false)
    @JoinColumn(name = "detalle_ruta_salida_id", referencedColumnName = "id")                
    private DetalleRuta detalleRutaSalida;

    @Schema(description = SystemText.User.ENTITY_ROL, example = "{ \"id\": 1 }")        
    @NotNull(groups = {onCreate.class, onUpdate.class}, message = "El tipo de camioneta es requerido")            
    @ManyToOne(optional = false)
    @JoinColumn(name = "boleto_precio_id", referencedColumnName = "id")                
    private PrecioBoleto precioBoleto;

    @Column(name = "total_boletos", nullable = true)         
    private Integer totalBoletos;

    @Schema(description = "Nombre de la camioenta", example = "01 Efectivo")        
    @Column(name = "asientos", nullable = true)         
    private String asientos;


    @Column(name = "pago", nullable = true)         
    private double pago;

    @Column(name = "cambio", nullable = true)         
    private double cambio;

    @Column(name = "fecha_salida", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
    private Timestamp fechaSalida;

    @Valid
    @Builder.Default                    // This annotation indicates that the attribute has a default value
    @JsonManagedReference             
    @Schema(description = "Arreglo de los detalles de la venta")                
    @OneToMany(mappedBy = "boleto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleBoleto> detalleBoletos = null;

    @Column(name = "viaje", nullable = true)         
    private String viaje;

    @Valid
    @Builder.Default                    // This annotation indicates that the attribute has a default value
    @JsonManagedReference             
    @Schema(description = "Arreglo de los detalles de la venta")                
    @OneToMany(mappedBy = "boleto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleEquipajeBoleto> detalleEquipajeBoleto = null;

        @Column(name = "cliente", nullable = true)         
    private String cliente;
}
