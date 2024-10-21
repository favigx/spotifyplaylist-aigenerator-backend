package com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.models.AiChatResponse;
import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.services.AiChatService;

@RestController
public class AiChatController {

    @Autowired
    private AiChatService aiChatService;

    @PostMapping("/aichat")
    public String postAiChat(@RequestBody String prompt) {
        AiChatResponse aiChatResponse = aiChatService.sendAiChatResponse(prompt);

        return aiChatResponse.getChoices().get(0).getMessage().getContent();
    }
}