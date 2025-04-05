package com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.integrations.spotify;

public class Playlist {
    private String id;
    private String name;
    private String spotifyLink;
    private String artworkUrl;

    public Playlist() {
    }

    public Playlist(String id, String name, String spotifyLink, String artworkUrl) {
        this.id = id;
        this.name = name;
        this.spotifyLink = spotifyLink;
        this.artworkUrl = artworkUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpotifyLink() {
        return spotifyLink;
    }

    public void setSpotifyLink(String spotifyLink) {
        this.spotifyLink = spotifyLink;
    }

    public String getArtworkUrl() {
        return artworkUrl;
    }

    public void setArtworkUrl(String artworkUrl) {
        this.artworkUrl = artworkUrl;
    }
}