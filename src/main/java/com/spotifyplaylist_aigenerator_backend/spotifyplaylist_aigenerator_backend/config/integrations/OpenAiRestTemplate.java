package com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.config.integrations;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OpenAiRestTemplate {

    @Value("${openaiapikey}")
    private String apiKey;

    @Bean
    @Qualifier("customOpenAiRestTemplate")
    public RestTemplate customOpenAiRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + apiKey);
            return execution.execute(request, body);
        });
        return restTemplate;
    }
}