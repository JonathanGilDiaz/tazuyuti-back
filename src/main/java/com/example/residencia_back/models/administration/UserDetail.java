/**
 * @author Dirección de Tecnologías e Innovación Digital - Secretaría de Finanzas
 * @version 1.0.0 Creado el 23 Oct 2024
 * @date 23/10/2024
 */
package com.example.residencia_back.models.administration;

import com.example.residencia_back.entities.administration.User;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


/**
* Implementation of Spring Security's UserDetails interface, representing the details of a user.
*
* This class serves as a wrapper around the Usuario entity, providing necessary 
* user details to the Spring Security framework for authentication and authorization.
* 
* It implements methods to retrieve user authorities, username, password, and 
* account status information.
*/

@AllArgsConstructor
public class UserDetail implements UserDetails {

    private final User usuario;

    @Override
    public String getPassword() {
        return usuario.getContrasenia();
    }

    @Override
    public String getUsername() {
        return usuario.getUsuario();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public User getUsuario() {
        return usuario;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
