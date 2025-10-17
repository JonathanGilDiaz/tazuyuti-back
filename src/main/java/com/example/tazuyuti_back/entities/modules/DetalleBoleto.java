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
@Table(schema = "modulos", name = "detalle_boleto")
public class DetalleBoleto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = SystemText.User.ENTITY_ID, example = "10")
    @NotNull(groups = onUpdate.class, message = SystemText.User.ENTITY_USUARIO_ID_REQUERIDO)
    @Column(name = "id")
    private int id;

    @JsonBackReference
    @ManyToOne(optional = false) 
    @JoinColumn(name = "boleto_id", referencedColumnName = "id", nullable = false)
    @NotNull(groups = { onCreate.class, onUpdate.class }, message = "La venta es requerida")
    private Boleto boleto;

    @ManyToOne(optional = false)
    @JoinColumn(name = "precio_boleto_id", referencedColumnName = "id", nullable = false)
    @NotNull(groups = { onCreate.class, onUpdate.class }, message = "El producto es requerido")
    private PrecioBoleto precioBoleto;

    @Column(name = "nino", nullable = true)
    private boolean nino;

    @Column(name = "cantidad", nullable = true)
    private Integer cantidad;

        @Column(name = "precio", nullable = true)
    private double precio;

    @Column(name = "subtotal", nullable = true)
    private double subtotal;
}
