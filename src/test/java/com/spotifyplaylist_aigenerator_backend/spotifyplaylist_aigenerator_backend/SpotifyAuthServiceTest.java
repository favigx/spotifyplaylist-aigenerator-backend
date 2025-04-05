package com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.integrations.spotify.SpotifyAuthService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SpotifyAuthServiceTest {

    @InjectMocks
    private SpotifyAuthService spotifyAuthService;

    @BeforeEach
    public void setUp() {
        spotifyAuthService = new SpotifyAuthService(null);
    }

    @Test
    public void testExtractAccessToken_ValidResponse() throws Exception {
        String responseBody = "{ \"access_token\": \"BQC0...your_access_token_here...\" }";

        String accessToken = spotifyAuthService.extractAccessToken(responseBody);

        assertEquals("BQC0...your_access_token_here...", accessToken);
    }

    @Test
    public void testExtractAccessToken_InvalidResponse() {

        String responseBody = "{ \"error\": \"invalid_grant\" }";

        String accessToken = spotifyAuthService.extractAccessToken(responseBody);

        assertNull(accessToken);
    }

    @Test
    public void testExtractAccessToken_InvalidJson() {
        String responseBody = "this is not a json";

        String accessToken = spotifyAuthService.extractAccessToken(responseBody);

        assertNull(accessToken);
    }
}