package com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "chats")
public class Chat {

    @Id
    private String id;
    private String content;
    private String sender;
    private String roomName;
    private LocalDateTime timestamp;

    public Chat(String content, String sender, String roomName) {
        this.content = content;
        this.sender = sender;
        this.roomName = roomName;
        this.timestamp = LocalDateTime.now();
    }

    public Chat() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}