package com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.controllers;

import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.models.Playlist;
import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.models.Track;
import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.services.SpotifyAuthService;
import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.services.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Collections;
import org.springframework.web.servlet.view.RedirectView;

@CrossOrigin(origins = "*")
@RestController
public class SpotifyAuthController {

    private final SpotifyAuthService spotifyAuthService;
    private final UserService userService;

    public SpotifyAuthController(SpotifyAuthService spotifyAuthService, UserService userService) {
        this.spotifyAuthService = spotifyAuthService;
        this.userService = userService;
    }

    @GetMapping("/spotifylogin/{loggedInUsername}")
    public String login(@PathVariable String loggedInUsername) {
        return spotifyAuthService.getSpotifyAuthorizationUrl(loggedInUsername);
    }

    @GetMapping("/callback")
    public RedirectView handleCallback(@RequestParam("code") String code,
            @RequestParam("state") String username) {
        spotifyAuthService.exchangeCodeForAccessToken(code, username);

        RedirectView redirectView = new RedirectView();
        String redirectUrl = "https://lobster-app-ebdey.ondigitalocean.app?page=generateplaylist";
        redirectView.setUrl(redirectUrl);
        return redirectView;
    }

    @GetMapping("/top-ten-tracks/{username}")
    public List<Track> getTopTenTracks(@PathVariable String username) {
        String accessToken = userService.getSpotifyAccessToken(username);

        if (accessToken != null) {
            return spotifyAuthService.getTopTenPlayedTracks(accessToken);
        } else {
            System.out.println("Access token not found for user: " + username);
            return Collections.emptyList();
        }
    }

    @GetMapping("/playlists/{username}")
    public ResponseEntity<List<Playlist>> getUserPlaylists(@PathVariable String username) {
        List<Playlist> playlists = spotifyAuthService.getUserPlaylists(username);
        return ResponseEntity.ok(playlists);
    }
}