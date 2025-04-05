package com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend;

import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.websocket.Chat;
import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.websocket.ChatService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ChatServiceTest {

    @Mock
    private MongoOperations mongoOperations;

    @InjectMocks
    private ChatService chatService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetMessages() {
        String roomName = "testRoom";

        Chat chat1 = new Chat("Hej", "User1", roomName);
        Chat chat2 = new Chat("Hallå", "User2", roomName);
        List<Chat> expectedChats = Arrays.asList(chat1, chat2);

        when(mongoOperations.find(new Query(Criteria.where("roomName").is(roomName)), Chat.class))
                .thenReturn(expectedChats);

        List<Chat> actualChats = chatService.getMessages(roomName);

        assertEquals(expectedChats, actualChats);
        verify(mongoOperations, times(1)).find(new Query(Criteria.where("roomName").is(roomName)), Chat.class);
    }
}