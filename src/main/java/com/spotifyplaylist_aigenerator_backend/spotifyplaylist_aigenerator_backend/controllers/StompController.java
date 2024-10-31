package com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.controllers;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.models.Chat;
import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.models.ChatMessage;
import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.models.HelloMessage;
import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.services.ChatService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Controller
public class StompController {

    private final Map<String, Set<String>> usersInRooms = new HashMap<>();

    private final ChatService chatService;

    public StompController(ChatService chatService) {
        this.chatService = chatService;
    }

    @MessageMapping("/room/{roomName}")
    @SendTo("/topic/{roomName}")
    public Map<String, Object> handleRoomMessage(@DestinationVariable String roomName, HelloMessage message) {
        String userName = message.getName();

        usersInRooms.putIfAbsent(roomName, new HashSet<>());
        Set<String> usersInRoom = usersInRooms.get(roomName);

        if (usersInRoom.add(userName)) {
            System.out.println("Användare har gått in i rummet: " + roomName + " - " + userName);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("users", String.join(", ", usersInRoom));
        response.put("userCount", usersInRoom.size());
        return response;
    }

    @MessageMapping("/leave/{roomName}")
    @SendTo("/topic/{roomName}")
    public Map<String, Object> handleLeaveMessage(@DestinationVariable String roomName, HelloMessage message) {
        String userName = message.getName();

        Set<String> usersInRoom = usersInRooms.get(roomName);
        if (usersInRoom != null && usersInRoom.remove(userName)) {
            System.out.println("Användare har lämnat rummet: " + roomName + " - " + userName);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("users", usersInRoom != null ? String.join(", ", usersInRoom) : "");
        response.put("userCount", usersInRoom != null ? usersInRoom.size() : 0);
        return response;
    }

    @MessageMapping("/chat/{roomName}")
    @SendTo("/topic/chat/{roomName}")
    public Chat chat(@DestinationVariable String roomName, ChatMessage chatMessage) {

        String loggedInUser = chatMessage.getSender();

        System.out.println("Meddelande till rum: " + roomName + ": " + chatMessage.getContent());

        Chat chat = new Chat(chatMessage.getContent(), loggedInUser, roomName);
        chatService.saveChat(chat);

        return chat;
    }
}