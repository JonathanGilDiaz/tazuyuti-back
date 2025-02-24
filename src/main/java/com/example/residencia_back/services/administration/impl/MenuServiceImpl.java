/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Feb 2025
 * @date 23/02/2025
 */
package com.example.residencia_back.services.administration.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.residencia_back.entities.administration.Menu;
import com.example.residencia_back.repositories.administration.MenuRepository;
import com.example.residencia_back.services.administration.MenuService;

/**
* Service implementation for managing Menu entities.
*
* This class implements the MenuService interface and provides 
* methods for retrieving Menu records based on role ID and option level 
* or dependency. It interacts with the MenuRepository to perform 
* the required data operations.
*/

@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuRepository menuRepository;

    @Override
    public List<Menu> findByRolIdAndOpcionNivel(int rolId, int nivel) {
        return menuRepository.findByRolIdAndOpcionNivelOrderById(rolId, nivel);
    }

    @Override
    public List<Menu> findByRolIdAndDepensAndOpcionNivel(int rolId, int depens, int nivel) {
        return menuRepository.findByRolIdAndDepensAndOpcionNivel(rolId, depens, nivel);
    }

}
