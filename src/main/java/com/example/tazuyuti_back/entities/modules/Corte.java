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
@Table(schema = "modulos", name = "corte")
public class Corte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = SystemText.User.ENTITY_ID, example = "10")
    @NotNull(groups = onUpdate.class, message = SystemText.User.ENTITY_USUARIO_ID_REQUERIDO)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @Schema(description = SystemText.User.ENTITY_ROL, example = "{ \"id\": 1 }")
    @NotNull(groups = { onCreate.class, onUpdate.class }, message = "El Usuario es requerido")
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private User usuario;

    @Column(name = "saldo_inicial", nullable = false)
    private double saldoInicial;

    @CreationTimestamp
    @Schema(hidden = true)
    @Column(name = "inicio", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
    private Timestamp inicio;

    @Column(name = "cierre", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
    private Timestamp cierre;

    @Column(name = "estado", nullable = true)
    private String estado;

    @Schema(description = "Folio de la venta", example = "D58")
    @Column(name = "folio", nullable = true)
    private String folio;

    @Column(name = "tarjeta", nullable = true)
    private double tarjeta;

    @Column(name = "transferencia", nullable = true)
    private double transferencia;

    @Column(name = "efectivo", nullable = true)
    private double efectivo;

    @Column(name = "total_caja", nullable = true)
    private double totalCaja;

    @Column(name = "total_cobros", nullable = true)
    private double totalCobros;

    @Column(name = "retiro", nullable = true)
    private double retiro;

    @Column(name = "saldo_final", nullable = true)
    private double saldoFinal;

    @Column(name = "observaciones", nullable = true)
    private String observaciones;

    @Column(name = "faltante", nullable = true)
    private double faltante;

    @Column(name = "sobrante", nullable = true)
    private double sobrante;

    @Column(name = "efectivo_caja", nullable = true)
    private double efectivoCaja;

    @Column(name = "total_retiros", nullable = true)
    private double totalRetiros;
}
