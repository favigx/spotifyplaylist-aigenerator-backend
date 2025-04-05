package com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.websocket;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/chat")
@RestController
public class ChatController {

    private ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/{roomName}")
    public List<Chat> getChat(@PathVariable String roomName) {
        if (roomName == null || roomName.isEmpty()) {
            throw new IllegalArgumentException("Room name cannot be empty");
        }
        return chatService.getMessages(roomName);
    }
}