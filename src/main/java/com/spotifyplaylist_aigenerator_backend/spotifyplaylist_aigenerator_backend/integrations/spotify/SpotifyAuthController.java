package com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.integrations.spotify;

import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.user.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Collections;
import org.springframework.web.servlet.view.RedirectView;

@RequestMapping("/api/spotify")
@RestController
public class SpotifyAuthController {

    private final SpotifyAuthService spotifyAuthService;
    private final UserService userService;

    public SpotifyAuthController(SpotifyAuthService spotifyAuthService, UserService userService) {
        this.spotifyAuthService = spotifyAuthService;
        this.userService = userService;
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
    public RedirectView handleCallbackApp(@RequestParam("code") String code,
            @RequestParam("state") String username) {
        spotifyAuthService.exchangeCodeForAccessTokenForApp(code, username);

        RedirectView redirectView = new RedirectView();
        String redirectUrl = "exp://192.168.50.248:8081/callback?status=success";
        redirectView.setUrl(redirectUrl);
        return redirectView;
    }

    @GetMapping("/{loggedInUser}/top-ten-tracks")
    public List<Track> getTopTenTracks(@PathVariable String loggedInUser) {
        String accessToken = userService.getSpotifyAccessToken(loggedInUser);

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