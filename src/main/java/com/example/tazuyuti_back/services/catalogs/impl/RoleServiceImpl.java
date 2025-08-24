/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Oct 2024
 * @date 23/10/2024
 */
package com.example.tazuyuti_back.services.catalogs.impl;

import org.springframework.stereotype.Service;

import com.example.tazuyuti_back.entities.administration.Role;
import com.example.tazuyuti_back.repositories.catalogs.RoleRepository;
import com.example.tazuyuti_back.services.catalogs.RoleService;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Implementation of RolService to manage user operations.
 */

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository repo;

    @Override
    public List<Role> findAll() {
        return repo.findAll();
    }

}
