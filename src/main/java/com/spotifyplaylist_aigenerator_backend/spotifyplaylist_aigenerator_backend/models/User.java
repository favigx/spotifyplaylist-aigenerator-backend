package com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.models;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Users")
public class User {
    @Id
    private String userId;
    private String email;
    private String username;
    private String password;
    private byte[] profileImage;
    private String spotifyAccessToken;
    private boolean isPremium;
    private int playlistsCreated;
    private List<Playlist> playlists;

    public User(String userId, String email, String username, String password, byte[] profileImage,
            String spotifyAccessToken, boolean isPremium, int playlistsCreated, List<Playlist> playlists) {
        this.userId = userId;
        this.email = email;
        this.username = username;
        this.password = password;
        this.profileImage = profileImage;
        this.spotifyAccessToken = spotifyAccessToken;
        this.isPremium = isPremium;
        this.playlistsCreated = playlistsCreated;
        this.playlists = playlists != null ? playlists : new ArrayList<>();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public byte[] getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(byte[] profileImage) {
        this.profileImage = profileImage;
    }

    public String getSpotifyAccessToken() {
        return spotifyAccessToken;
    }

    public void setSpotifyAccessToken(String spotifyAccessToken) {
        this.spotifyAccessToken = spotifyAccessToken;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean isPremium) {
        this.isPremium = isPremium;
    }

    public int getPlaylistsCreated() {
        return playlistsCreated;
    }

    public void setPlaylistsCreated(int playlistsCreated) {
        this.playlistsCreated = playlistsCreated;
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(List<Playlist> playlists) {
        this.playlists = playlists;
    }
}