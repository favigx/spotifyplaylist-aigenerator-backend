package com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.models.AiChatResponse;
import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.models.User;
import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.services.AiChatService;
import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.services.SpotifyAuthService;
import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.services.UserService;

import java.util.ArrayList;
import java.util.List;

@RestController
public class AiChatController {

    @Autowired
    private AiChatService aiChatService;

    @Autowired
    private SpotifyAuthService spotifyAuthService;

    @Autowired
    private UserService userService;

    @PostMapping("/aichat/{username}")
    public List<String> postAiChat(@RequestBody String prompt, @PathVariable String username) {

        User user = userService.getUserByUsername(username);

        if (!user.isPremium() && user.getPlaylistsCreated() >= 2) {
            return List.of("Du har nått din gräns på 2 spellistor. Uppgradera till premium för att skapa fler.");
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

        String playlistId = spotifyAuthService.createPlaylist(accessToken);
        if (playlistId != null) {
            boolean added = spotifyAuthService.addTracksToPlaylist(accessToken, playlistId, songLinks);
            if (added) {
                user.setPlaylistsCreated(user.getPlaylistsCreated() + 1);
                userService.updateUser(user);
                return List.of("Spellista skapad: https://open.spotify.com/playlist/" + playlistId);
            }
        }
        return songLinks;
    }
}