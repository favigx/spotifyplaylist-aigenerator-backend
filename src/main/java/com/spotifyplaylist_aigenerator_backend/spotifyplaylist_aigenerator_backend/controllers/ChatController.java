package com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.models.Chat;
import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.services.ChatService;

@CrossOrigin(origins = "*")
@RestController
public class ChatController {

    private ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/chat/{roomName}")
    public List<Chat> getChat(@PathVariable String roomName) {
        return chatService.getMessages(roomName);
    }
}