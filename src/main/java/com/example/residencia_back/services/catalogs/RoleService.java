/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Oct 2024
 * @date 23/10/2024
 */
package com.example.residencia_back.services.catalogs;

import java.util.List;

import com.example.residencia_back.entities.administration.Role;

public interface RoleService {

    List<Role> findAll();

}
