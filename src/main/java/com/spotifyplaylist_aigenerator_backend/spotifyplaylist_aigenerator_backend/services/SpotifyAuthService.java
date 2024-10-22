package com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
public class SpotifyAuthService {

    private final UserService userService;

    public SpotifyAuthService(UserService userService) {
        this.userService = userService;
    }

    @Value("${spotifyclientid}")
    private String clientId;

    @Value("${spotifyclientsecret}")
    private String clientSecret;

    @Value("${spotifyredirecturi}")
    private String redirectUri;

    private final RestTemplate restTemplate = new RestTemplate();

    private String extractAccessToken(String responseBody) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode node = mapper.readTree(responseBody);
            return node.get("access_token").asText();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getSpotifyAuthorizationUrl(String username) {
        String scopes = "playlist-modify-public playlist-modify-private";

        return "https://accounts.spotify.com/authorize?response_type=code"
                + "&client_id=" + clientId
                + "&scope=" + scopes
                + "&redirect_uri=" + redirectUri
                + "&state=" + username;
    }

    public String exchangeCodeForAccessToken(String code, String username) {
        String url = "https://accounts.spotify.com/api/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri", redirectUri);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            String accessToken = extractAccessToken(response.getBody());

            userService.saveSpotifyAccessToken(username, accessToken);

            return null;
        }
        return null;
    }
}