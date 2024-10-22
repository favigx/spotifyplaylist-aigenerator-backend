package com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.controllers;

import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.services.SpotifyAuthService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@CrossOrigin(origins = "*")
@RestController
public class SpotifyAuthController {

    private final SpotifyAuthService spotifyAuthService;

    public SpotifyAuthController(SpotifyAuthService spotifyAuthService) {
        this.spotifyAuthService = spotifyAuthService;
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
        String redirectUrl = "http://localhost:8080/success";
        redirectView.setUrl(redirectUrl);
        return redirectView;
    }
}