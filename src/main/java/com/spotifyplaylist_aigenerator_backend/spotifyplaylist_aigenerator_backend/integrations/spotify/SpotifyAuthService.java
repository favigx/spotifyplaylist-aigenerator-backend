package com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.integrations.spotify;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.security.EncryptionService;
import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.user.User;
import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.user.UserService;

import java.util.Collections;

import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpotifyAuthService {

    @Value("${spotifyclientid}")
    private String clientId;

    @Value("${spotifyclientsecret}")
    private String clientSecret;

    @Value("${spotifyredirecturi}")
    private String redirectUri;

    @Value("${spotifyredirecturiapp}")
    private String redirectUriApp;

    private final UserService userService;
    private final EncryptionService encryptionService;

    public SpotifyAuthService(UserService userService, EncryptionService encryptionService) {
        this.userService = userService;
        this.encryptionService = encryptionService;
    }

    private final RestTemplate restTemplate = new RestTemplate();

    public String extractAccessToken(String responseBody) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode node = mapper.readTree(responseBody);
            return node.get("access_token").asText();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String extractRefreshToken(String responseBody) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode node = mapper.readTree(responseBody);
            return node.get("refresh_token").asText();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getValidSpotifyAccessToken(String username) {
        try {
            User user = userService.getUserByUsername(username);
            if (user == null) {
                throw new RuntimeException("Användaren hittades inte");
            }

            String encryptedAccessToken = user.getSpotifyAccessToken();
            String encryptedRefreshToken = user.getSpotifyRefreshToken();

            String accessToken = encryptionService.decrypt(encryptedAccessToken);

            if (isAccessTokenExpired(accessToken)) {
                String refreshToken = encryptionService.decrypt(encryptedRefreshToken);
                accessToken = refreshAccessToken(refreshToken);

                if (accessToken != null) {
                    userService.saveSpotifyAccessToken(username, accessToken, refreshToken);
                } else {
                    System.err.println("Kunde inte förnya access token för användare: " + username);
                    return null;
                }
            }

            return accessToken;
        } catch (Exception e) {
            System.err.println("Kunde inte hämta spotify-access token: " + e.getMessage());
            return null;
        }
    }

    public String getSpotifyAuthorizationUrl(String username) {
        String scopes = "playlist-modify-public playlist-modify-private user-library-read user-read-private user-read-playback-state user-top-read";

        return "https://accounts.spotify.com/authorize?response_type=code"
                + "&client_id=" + clientId
                + "&scope=" + scopes
                + "&redirect_uri=" + redirectUri
                + "&state=" + username
                + "&prompt=login"
                + "&show_dialog=true";
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
            String refreshToken = extractRefreshToken(response.getBody());

            userService.saveSpotifyAccessToken(username, accessToken, refreshToken);
            return accessToken;
        }
        return null;
    }

    public String getSpotifyAuthorizationUrlForApp(String username) {
        String scopes = "playlist-modify-public playlist-modify-private user-library-read user-read-private user-read-playback-state user-top-read";

        return "https://accounts.spotify.com/authorize?response_type=code"
                + "&client_id=" + clientId
                + "&scope=" + scopes
                + "&redirect_uri=" + redirectUriApp
                + "&state=" + username
                + "&prompt=login"
                + "&show_dialog=true";
    }

    public String exchangeCodeForAccessTokenForApp(String code, String username) {
        String url = "https://accounts.spotify.com/api/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri", redirectUriApp);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            String accessToken = extractAccessToken(response.getBody());
            String refreshToken = extractRefreshToken(response.getBody());

            userService.saveSpotifyAccessToken(username, accessToken, refreshToken);
            return accessToken;
        }
        return null;
    }

    public String refreshAccessToken(String refreshToken) {
        String url = "https://accounts.spotify.com/api/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("refresh_token", refreshToken);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return extractAccessToken(response.getBody());
        }
        return null;
    }

    public boolean isAccessTokenExpired(String accessToken) {
        String url = "https://api.spotify.com/v1/me";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            return false; // fortfarande giltig
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return true;
            }
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Track> getTopTenPlayedTracks(String accessToken) {
        String url = "https://api.spotify.com/v1/me/top/tracks?limit=10";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return extractTopTracks(response.getBody());
            } else {
                System.out.println("Misslyckade med att hitta top 10. Status kod: " + response.getStatusCode());
                System.out.println("Response body: " + response.getBody());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public List<Track> extractTopTracks(String responseBody) {
        List<Track> topTracks = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(responseBody);
            JsonNode items = root.get("items");
            if (items != null) {
                for (JsonNode item : items) {
                    String trackName = item.get("name").asText();
                    String artistName = item.get("artists").get(0).get("name").asText();
                    String albumName = item.get("album").get("name").asText();
                    String trackUri = item.get("uri").asText();

                    topTracks.add(new Track(trackName, artistName, albumName, trackUri));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return topTracks;
    }

    public String searchTrack(String trackAndArtist, String accessToken) {
        String url = "https://api.spotify.com/v1/search?q=" + trackAndArtist + "&type=track&limit=1";
        System.out.println("Spotify search URL: " + url);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            String uri = extractTrackUri(response.getBody());
            if (uri != null) {
                System.out.println("Exakt extraherad track URI: " + uri);
            } else {
                System.out.println("Ingen låt hittades i resultatet.");
            }
            return uri;
        } else {
            System.out.println("Misslyckades med att söka låt. Statuskod: " + response.getStatusCode());
            return null;
        }
    }

    private String extractTrackUri(String responseBody) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(responseBody);
            JsonNode items = root.path("tracks").path("items");
            if (items.size() > 0) {
                String trackUri = items.get(0).path("uri").asText();
                System.out.println("Extraherad track URI i metoden: " + trackUri);
                return trackUri;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String createPlaylist(String accessToken, String playlistName) {
        String url = "https://api.spotify.com/v1/me/playlists";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String jsonBody = "{ \"name\": \"" + playlistName
                + "\", \"description\": \"Spellista skapad baserat på AI-förslag\", \"public\": true }";

        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        if (response.getStatusCode() == HttpStatus.CREATED) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                JsonNode node = mapper.readTree(response.getBody());
                return node.get("id").asText();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public boolean addTracksToPlaylist(String accessToken, String playlistId, List<String> trackUris) {
        String url = "https://api.spotify.com/v1/playlists/" + playlistId + "/tracks";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        StringBuilder jsonBody = new StringBuilder("{ \"uris\": [");
        for (int i = 0; i < trackUris.size(); i++) {
            jsonBody.append("\"").append(trackUris.get(i)).append("\"");
            if (i < trackUris.size() - 1) {
                jsonBody.append(", ");
            }
        }
        jsonBody.append("]}");

        HttpEntity<String> entity = new HttpEntity<>(jsonBody.toString(), headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        System.out.println("Spotify API-respons vid tillägg av låtar: " + response.getBody());
        return response.getStatusCode() == HttpStatus.CREATED;
    }

    public List<Playlist> getUserPlaylists(String username) {
        String accessToken = getValidSpotifyAccessToken(username);
        String url = "https://api.spotify.com/v1/me/playlists";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                List<Playlist> allPlaylists = extractPlaylists(response.getBody());
                User user = userService.getUserByUsername(username);

                List<String> appCreatedPlaylistIds = user.getPlaylists().stream()
                        .map(Playlist::getId)
                        .collect(Collectors.toList());

                List<Playlist> createdByAppPlaylists = allPlaylists.stream()
                        .filter(playlist -> appCreatedPlaylistIds.contains(playlist.getId()))
                        .toList();

                return createdByAppPlaylists;
            } else {
                System.out.println("Misslyckades med att hämta spellistor. Statuskod: " + response.getStatusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    private List<Playlist> extractPlaylists(String responseBody) {
        List<Playlist> playlists = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(responseBody);
            JsonNode items = root.get("items");
            if (items != null) {
                for (JsonNode item : items) {
                    String playlistId = item.get("id").asText();
                    String playlistName = item.get("name").asText();

                    String spotifyUri = "spotify:playlist:" + playlistId;

                    String artworkUrl = "";
                    if (item.has("images") && item.get("images").size() > 0) {
                        artworkUrl = item.get("images").get(0).get("url").asText();
                    }
                    playlists.add(new Playlist(playlistId, playlistName, spotifyUri, artworkUrl));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return playlists;
    }
}