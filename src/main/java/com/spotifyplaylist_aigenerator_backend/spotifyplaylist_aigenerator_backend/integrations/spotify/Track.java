package com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.integrations.spotify;

public class Track {

    private String name;
    private String artist;
    private String album;
    private String uri;

    public Track(String name, String artist, String album, String uri) {
        this.name = name;
        this.artist = artist;
        this.album = album;
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}