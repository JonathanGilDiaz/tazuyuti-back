/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 05 Ene 2025
 * @date 05/01/2025
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
})
public class SpringConfig {
    @Autowired
    private Environment environment;
    private static final Logger logger = Logger.getLogger(SpringConfig.class.getName());

    @PostConstruct
    public void post() {
        logger.info("--------------------------------------------------------------------------------------------------");
        logger.info("-------------------------  SISTEMA DE MUNICIPIO V.1.0  ---------------------------------");
        logger.info("-------------------------------------  ENTORNO: DESARROLLO  -----------------------------------------");
        logger.info("--------------------------------------------------------------------------------------------------");
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
