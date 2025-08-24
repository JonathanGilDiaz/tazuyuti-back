/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Feb 2025
 * @date 23/02/2025
 */
package com.example.tazuyuti_back.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.http.HttpHeaders;

@OpenAPIDefinition(
        info = @Info(
                title = "SISTEMA PARA TAZUYUTI",
                description = "Plataforma digital para transporte y paqueteria",
                version = "1.0",
                termsOfService = "Terminos y servicios",
                contact = @Contact(name = "Jonathan Gilberto Diaz Reyes", url = "", email = "jonathangildiaz211@gmail.com"),
                license = @License(name = "JonathanGil", url = "www.JonathanGil.gob.mx")
        ),
        servers = {
            @Server(
                    description = "Local Environment",
                    url = "http://127.0.0.1:8080"
            ),
            @Server(
                    description = "Developer Environment",
                    url = "http://127.0.0.1:8080"
            )
        },
        security = @SecurityRequirement(
                name = "Security Token"
        )
)
@SecurityScheme(
        name = "Security Token",
        description = "Access Token For API",
        type = SecuritySchemeType.HTTP,
        paramName = HttpHeaders.AUTHORIZATION,
        in = SecuritySchemeIn.HEADER,
        scheme = "Bearer",
        bearerFormat = "JWT"
)

public class SwaggerConfig {

}
