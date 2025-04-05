package com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.integrations.ai.openai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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