package com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.models.AiChatResponse;
import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.models.Playlist;
import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.models.User;
import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.services.AiChatService;
import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.services.SpotifyAuthService;
import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.services.UserService;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
public class AiChatController {

    @Autowired
    private AiChatService aiChatService;

    @Autowired
    private SpotifyAuthService spotifyAuthService;

    @Autowired
    private UserService userService;

    @PostMapping("/aichat/{username}")
    public String postAiChat(@RequestBody String jsonRequest, @PathVariable String username) {
        User user = userService.getUserByUsername(username);

        if (!user.isPremium() && user.getPlaylistsCreated() >= 2) {
            return "Du har nått din gräns på 2 spellistor. Uppgradera till premium för att skapa fler.";
        }

        String prompt;
        String playlistName;

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(jsonRequest);
            prompt = node.get("prompt").asText();
            playlistName = node.get("playlistName").asText();
        } catch (JsonProcessingException e) {
            return "Felaktig begäran: " + e.getMessage();
        }

        AiChatResponse aiChatResponse = aiChatService.sendAiChatResponse(prompt);
        String accessToken = userService.getSpotifyAccessToken(username);
        List<String> songLinks = new ArrayList<>();

        for (AiChatResponse.Choice choice : aiChatResponse.getChoices()) {
            String content = choice.getMessage().getContent();
            String[] lines = content.split("\n");

            for (String line : lines) {
                String[] parts = line.split(" - ");
                if (parts.length == 2) {
                    String trackName = parts[0].trim();
                    String artistName = parts[1].trim();
                    String link = spotifyAuthService.searchTrack(trackName, artistName, accessToken);
                    if (link != null) {
                        songLinks.add(link);
                    }
                }
            }
        }

        String playlistId = spotifyAuthService.createPlaylist(accessToken, playlistName);
        if (playlistId != null) {

            String artworkUrl = "";
            boolean added = spotifyAuthService.addTracksToPlaylist(accessToken, playlistId, songLinks);

            String spotifyLink = "https://open.spotify.com/playlist/" + playlistId;
            Playlist newPlaylist = new Playlist(playlistId, playlistName, spotifyLink, artworkUrl);

            user.getPlaylists().add(newPlaylist);

            if (added) {
                user.setPlaylistsCreated(user.getPlaylistsCreated() + 1);
                userService.updateUser(user);
                return "Spellista skapad: " + spotifyLink;
            } else {
                return "Spellista skapad men inga låtar kunde läggas till.";
            }
        }
        return "Det gick inte att skapa spellistan. Försök igen senare.";
    }
}