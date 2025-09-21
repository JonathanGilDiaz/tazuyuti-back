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
import com.example.tazuyuti_back.entities.catalogs.EstadoPaquete;
import com.example.tazuyuti_back.entities.catalogs.Sucursal;
import com.example.tazuyuti_back.helpers.SystemText;
import com.example.tazuyuti_back.validator.onCreate;
import com.example.tazuyuti_back.validator.onUpdate;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
@Table(schema = "modulos", name = "paquete")
public class Paquete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // This annotation indicates that the primary key value is
    @Schema(description = SystemText.User.ENTITY_ID, example = "10") // This annotation indicates that information for
    @NotNull(groups = onUpdate.class, message = SystemText.User.ENTITY_USUARIO_ID_REQUERIDO) // This annotation
    @Column(name = "id") // This annotation indicates that Associates the attribute with a database
    private int id;

    @NotNull(groups = { onCreate.class, onUpdate.class }, message = "destinatario es requerido") // This annotation
    @Schema(description = "destinatario", example = "Jonathan") // This annotation indicates that information for the
    @Column(name = "destinatario", nullable = false) // This annotation indicates that Associates the attribute with a
    private String destinatario;

    @NotNull(groups = { onCreate.class, onUpdate.class }, message = "remitente es requerido") // This annotation
    @Schema(description = "remitente", example = "Jonathan") // This annotation indicates that information for the
    @Column(name = "remitente", nullable = false) // This annotation indicates that Associates the attribute with a
    private String remitente;

    @CreationTimestamp
    @Schema(hidden = true)
    @Column(name = "fecha_creacion", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Timestamp fechaCreacion;

    @ManyToOne
    @Schema(description = SystemText.User.ENTITY_ROL, example = "{ \"id\": 1 }") // This annotation indicates that
    @NotNull(groups = { onCreate.class, onUpdate.class }, message = "El Usuario es requerido") // This annotation
    @JoinColumn(name = "usuario_id", referencedColumnName = "id") // This annotation indicates that Relate the table to
    private User usuario;

    @NotNull(groups = { onCreate.class, onUpdate.class }, message = "La forma de pago es requerido") // This annotation
    @Schema(description = "Nombre de la camioenta", example = "01 Efectivo") // This annotation indicates that
    @Column(name = "forma_pago", nullable = false) // This annotation indicates that Associates the attribute with a
    private String formaPago;

    @NotNull(groups = { onCreate.class, onUpdate.class }, message = "Total de la venta es requerido") // This annotation
    @Schema(description = "Total de la venta", example = "10.50") // This annotation indicates that information for the
    @Column(name = "total", nullable = false) // This annotation indicates that Associates the attribute with a database
    private double total;

    @Schema(description = "Folio de la venta", example = "D58") // This annotation indicates that information for the
    @Column(name = "folio", nullable = true) // This annotation indicates that Associates the attribute with a database
     private String folio;

    @Schema(description = SystemText.User.ENTITY_ROL, example = "{ \"id\": 1 }") // This annotation indicates that
    @OneToOne // This annotation indicates that it has a one-to-one relationship with the
    @JoinColumn(name = "estado_id", referencedColumnName = "id") // This annotation indicates that Relate the table to
    private EstadoPaquete estado;

    @Schema(description = SystemText.User.ENTITY_SUCURSAL_ID, example = "{ \"id\": 1 }") // This annotation indicates
    @NotNull(groups = { onCreate.class, onUpdate.class }, message = SystemText.User.ENTITY_SUCURSAL_ID_REQUERIDO) // This                                                                               // null.
    @ManyToOne // This annotation indicates that it has a one-to-one relationship with the
    @JoinColumn(name = "destino_id", referencedColumnName = "id") // This annotation indicates that Relate the table to
    private Sucursal destino;

    @Column(name = "pago", nullable = true) // This annotation indicates that Associates the attribute with a database
    private double pago;

    @Column(name = "cambio", nullable = true) // This annotation indicates that Associates the attribute with a database
    private double cambio;

    @Valid
    @Builder.Default // This annotation indicates that the attribute has a default value
    @JsonManagedReference
    @Schema(description = "Arreglo de los detalles de la venta") // This annotation indicates that information for the
                                                                 // swagger // This annotation indicates that
                                                                 // information for the swagger
    @OneToMany(mappedBy = "paquete", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallePaquete> detallePaquete = null;

   @JsonIdentityReference(alwaysAsId = false)
    @OneToOne(mappedBy = "paquete", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private CancelarPaquete cancelacion;

     @JsonIdentityReference(alwaysAsId = false)
    @OneToOne(mappedBy = "paquete", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private EnviarPaquete envio;

    @JsonIdentityReference(alwaysAsId = false)
    @OneToOne(mappedBy = "paquete", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
   private RecibirPaquete recibo;

    @JsonIdentityReference(alwaysAsId = false)
    @OneToOne(mappedBy = "paquete", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private EntregarPaquete entrega;
}
