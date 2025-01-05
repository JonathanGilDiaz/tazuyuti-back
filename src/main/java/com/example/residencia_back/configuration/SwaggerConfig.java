/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 05 Ene 2025
 * @date 05/01/2025
 */
package com.example.residencia_back.configuration;

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
                title = "SISTEMA MUNICIPIOS",
                description = "Plataforma digital para el municipio",
                version = "1.0",
                termsOfService = "Terminos y servicios",
                contact = @Contact(name = "Jonathan&Sabdiel", url = "www.pendiente.mx", email = "pendiente@gmail.com"),
                license = @License(name = "Jonathan&Sabdiel", url = "www.pendiente.mx")
        ),
        servers = {
            @Server(
                    description = "Local Environment",
                    url = "http://127.0.0.1:8080"
            )
           /** @Server(
                    description = "Quality Environment",
                    url = "https://LINK_SERVIDOR"
            ) */

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
