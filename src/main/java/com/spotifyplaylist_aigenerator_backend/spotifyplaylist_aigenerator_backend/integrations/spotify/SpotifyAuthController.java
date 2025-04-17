package com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.integrations.spotify;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;

import org.springframework.web.servlet.view.RedirectView;

@RequestMapping("/api/spotify")
@RestController
public class SpotifyAuthController {

    private final SpotifyAuthService spotifyAuthService;

    public SpotifyAuthController(SpotifyAuthService spotifyAuthService) {
        this.spotifyAuthService = spotifyAuthService;
    }

    @GetMapping("/{loggedInUser}/login")
    public String login(@PathVariable String loggedInUser) {
        return spotifyAuthService.getSpotifyAuthorizationUrl(loggedInUser);
    }

    @GetMapping("/callback")
    public RedirectView handleCallback(@RequestParam("code") String code,
            @RequestParam("state") String username) {
        spotifyAuthService.exchangeCodeForAccessToken(code, username);

        RedirectView redirectView = new RedirectView();
        String redirectUrl = "https://lobster-app-ebdey.ondigitalocean.app/?page=generateplaylist";
        redirectView.setUrl(redirectUrl);
        return redirectView;
    }

    @GetMapping("/app/{loggedInUser}/login")
    public String loginApp(@PathVariable String loggedInUser) {
        return spotifyAuthService.getSpotifyAuthorizationUrlForApp(loggedInUser);
    }

    @GetMapping("/app/callback")
    public ResponseEntity<Void> handleCallbackApp(
            @RequestParam("code") String code,
            @RequestParam("state") String username) {
        try {
            String accessToken = spotifyAuthService.exchangeCodeForAccessTokenForApp(code, username);

            URI redirectUri = URI.create("exp://192.168.1.13:8081/callback?access_token=" + accessToken);

            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(redirectUri)
                    .build();

        } catch (Exception e) {
            URI errorUri = URI.create("yourapp://callback?error=spotify_login_failed");
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(errorUri)
                    .build();
        }
    }

    @GetMapping("/{loggedInUser}/top-ten-tracks")
    public List<Track> getTopTenTracks(@PathVariable String loggedInUser) {
        String accessToken = spotifyAuthService.getValidSpotifyAccessToken(loggedInUser);

        if (accessToken != null) {
            return spotifyAuthService.getTopTenPlayedTracks(accessToken);
        } else {
            System.out.println("Access token not found for user: " + loggedInUser);
            return Collections.emptyList();
        }
    }

    @GetMapping("/{loggedInUser}/playlist")
    public ResponseEntity<List<Playlist>> getUserPlaylists(@PathVariable String loggedInUser) {
        List<Playlist> playlists = spotifyAuthService.getUserPlaylists(loggedInUser);
        return ResponseEntity.ok(playlists);
    }
}