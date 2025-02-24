/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Feb 2025
 * @date 23/02/2025
 */
package com.example.residencia_back.configuration;

import jakarta.annotation.PostConstruct;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

@Configuration
@ComponentScan({"com.example.residencia_back.*"})
@PropertySources({
    @PropertySource("classpath:application.properties"),
    @PropertySource(value = "classpath:application-${spring.profiles.active}.properties", ignoreResourceNotFound = true)
})
public class SpringConfig {
    @Autowired
    private Environment environment;
    private static final Logger logger = Logger.getLogger(SpringConfig.class.getName());

    @PostConstruct
    public void post() {
        logger.info("--------------------------------------------------------------------------------------------------");
        logger.info("-------------------------   SISTEMA PARA MUNICIPIO ZIMATLAN DE ALVAREZ V.1.0  ---------------------------------");
        logger.info("-------------------------------------  ENTORNO: " + environment.getProperty("deployMessage") + "  -----------------------------------------");
        logger.info("--------------------------------------------------------------------------------------------------");
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
