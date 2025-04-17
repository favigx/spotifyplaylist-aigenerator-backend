package com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.config.webconfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @SuppressWarnings("null")
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://lobster-app-ebdey.ondigitalocean.app", "http://localhost:5173",
                        "http://192.168.50.248:8081", "http://localhost:8081", "exp://192.168.50.248:8081",
                        "exp://192.168.1.13:8081",
                        "https://sea-turtle-app-le797.ondigitalocean.app")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}