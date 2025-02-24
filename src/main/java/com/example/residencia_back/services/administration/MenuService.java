/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Feb 2025
 * @date 23/02/2025
 */
package com.example.residencia_back.services.administration;

import java.util.List;
import com.example.residencia_back.entities.administration.Menu;

public interface MenuService {

    List<Menu> findByRolIdAndOpcionNivel(int rolId, int nivel);

    List<Menu> findByRolIdAndDepensAndOpcionNivel(int rolId, int depens, int nivel);
}
