package com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.services;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.models.Chat;

@Service
public class ChatService {

    private final MongoOperations mongoOperations;

    public ChatService(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    public Chat saveChat(Chat chat) {
        return mongoOperations.save(chat);
    }

    public List<Chat> getMessages(String roomName) {
        Query query = new Query(Criteria.where("roomName").is(roomName));
        return mongoOperations.find(query, Chat.class);
    }
}