package com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.websocket.Chat;
import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.websocket.ChatMessage;
import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.websocket.ChatService;
import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.websocket.StompController;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class StompControllerTest {

    @Mock
    private ChatService chatService;

    @InjectMocks
    private StompController stompController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testChat() {
        String roomName = "testRoom";
        String sender = "testUser";
        String content = "Hello, this is a test message";

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSender(sender);
        chatMessage.setContent(content);

        Chat expectedChat = new Chat(content, sender, roomName);

        Chat result = stompController.chat(roomName, chatMessage);

        assertEquals(expectedChat.getContent(), result.getContent(), "Meddelandets innehåll ska matcha");
        assertEquals(expectedChat.getSender(), result.getSender(), "Avsändaren ska matcha");
        assertEquals(expectedChat.getRoomName(), result.getRoomName(), "Rum-namnet ska matcha");

        verify(chatService, times(1)).saveChat(result);
    }
}