package com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.config.webconfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(@SuppressWarnings("null") CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://lobster-app-ebdey.ondigitalocean.app",
                        "http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE");
    }
}