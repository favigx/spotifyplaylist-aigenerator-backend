package com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.models.AiChatRequest;
import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.models.AiChatResponse;

@Service
public class AiChatService {

    @Value("${openai.api.url}")
    String apiUrl;

    private final RestTemplate restTemplate;

    public AiChatService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public AiChatResponse sendAiChatResponse(String prompt) {
        AiChatRequest aiChatRequest = new AiChatRequest("gpt-4o", prompt, 1);
        AiChatResponse aiChatResponse = restTemplate.postForObject(apiUrl, aiChatRequest, AiChatResponse.class);

        return aiChatResponse;
    }
}