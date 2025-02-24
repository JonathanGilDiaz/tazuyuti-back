/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Feb 2025
 * @date 23/02/2025
 */
package com.example.residencia_back.entities.administration;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
* Represents a menu item in the system, linking roles to specific options.
*
* @param id       The unique identifier of the menu item (automatically generated).
* @param opcion   The option associated with this menu item (Many-to-One relationship with Opcion).
* @param depens   The dependency level of the menu item (required).
* @param rol      The role associated with this menu item (Many-to-One relationship with Rol, not returned in the response).
* @param orden    The order in which the menu item appears (required).
* @param activo    Indicates whether the menu item is active (default is true).
*/

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(schema = "administracion", name = "menu")
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "opcion_id", referencedColumnName = "id")
    private Option opcion;

    @Column(name = "depens", nullable = false)
    private Short depens;

    @JsonIgnore //no regresa el objeto rol 
    @ManyToOne
    @JoinColumn(name = "rol_id", referencedColumnName = "id")
    private Role rol;

    @Column(name = "orden", nullable = false)
    private Short orden;

    @Column(name = "activo", nullable = false)
    @Builder.Default
    private Boolean activo = true;

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", this.id);
        map.put("depens", this.depens);
        map.put("orden", this.orden);
        map.put("activo", this.activo);
    
        if (this.opcion != null) {
            HashMap<String, Object> mapOpcion = new HashMap<>();
            mapOpcion.put("id", this.opcion.getId());
            mapOpcion.put("opcion", this.opcion.getOpcion());  
            mapOpcion.put("descripcion", this.opcion.getDescripcion()); 
            mapOpcion.put("url", this.opcion.getUrl()); 
            mapOpcion.put("icono", this.opcion.getIcono()); 
            mapOpcion.put("nivel", this.opcion.getNivel());
            map.put("opcion", mapOpcion);
        }
    
        return map;
    }
    
}
