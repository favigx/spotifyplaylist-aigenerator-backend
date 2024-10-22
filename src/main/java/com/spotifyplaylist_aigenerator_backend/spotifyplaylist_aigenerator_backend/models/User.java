package com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Users")
public class User {
    @Id
    private String userId;
    private String username;
    private String password;
    private String spotifyAccessToken;

    public User(String userId, String username, String password, String spotifyAccessToken) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.spotifyAccessToken = spotifyAccessToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSpotifyAccessToken() {
        return spotifyAccessToken;
    }

    public void setSpotifyAccessToken(String spotifyAccessToken) {
        this.spotifyAccessToken = spotifyAccessToken;
    }
}