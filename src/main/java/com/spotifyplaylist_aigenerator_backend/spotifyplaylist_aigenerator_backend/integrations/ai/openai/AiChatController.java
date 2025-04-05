package com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.integrations.ai.openai;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.integrations.spotify.Playlist;
import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.integrations.spotify.SpotifyAuthService;
import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.user.User;
import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.user.UserService;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/api/ai")
@RestController
public class AiChatController {

    @Autowired
    private AiChatService aiChatService;

    @Autowired
    private SpotifyAuthService spotifyAuthService;

    @Autowired
    private UserService userService;

    @PostMapping("/{loggedInUser}/generate-playlist")
    public String postAiChat(@RequestBody String jsonRequest, @PathVariable String loggedInUser) {
        User user = userService.getUserByUsername(loggedInUser);

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
        String accessToken = userService.getSpotifyAccessToken(loggedInUser);
        List<String> songUris = new ArrayList<>();

        System.out.println("AI Response:");
        for (AiChatResponse.Choice choice : aiChatResponse.getChoices()) {
            System.out.println(choice.getMessage().getContent());
            String[] lines = choice.getMessage().getContent().split("\n");

            for (String line : lines) {
                line = line.trim();

                String uri = spotifyAuthService.searchTrack(line, accessToken);

                if (uri != null && !songUris.contains(uri)) {
                    System.out.println("Hittad URI: " + uri);
                    songUris.add(uri);
                } else {
                    System.out.println("Ingen låt hittades.");
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        if (songUris.isEmpty()) {
            return "Ingen låt hittades eller lades till i spellistan.";
        }

        System.out.println("Alla URI:er som kommer att läggas till i spellistan:");
        for (String uri : songUris) {
            System.out.println(uri);
        }

        String playlistId = spotifyAuthService.createPlaylist(accessToken, playlistName);
        if (playlistId != null) {
            boolean added = spotifyAuthService.addTracksToPlaylist(accessToken, playlistId, songUris);
            String spotifyUri = "spotify:playlist:" + playlistId;
            Playlist newPlaylist = new Playlist(playlistId, playlistName, spotifyUri, "");

            user.getPlaylists().add(newPlaylist);
            if (added) {
                user.setPlaylistsCreated(user.getPlaylistsCreated() + 1);
                userService.updateUser(user);
                return "Spellista skapad: " + spotifyUri;
            } else {
                return "Spellista skapad men inga låtar kunde läggas till.";
            }
        }
        return "Det gick inte att skapa spellistan. Försök igen senare.";
    }
}