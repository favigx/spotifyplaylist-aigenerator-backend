package com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.controllers;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.models.User;
import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.services.UserService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@CrossOrigin(origins = "*")
@RestController
public class UserController {

    private UserService userService;
    private PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Value("${jwtSecret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpriationMs;

    @PostMapping("/user")
    public ResponseEntity<?> addUser(@RequestBody User user) {
        try {
            User addedUser = userService.addUser(user);
            return ResponseEntity.ok(addedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Användarnamnet är upptaget, pröva något annat");
        }
    }

    @PostMapping("/loginuser")
    public ResponseEntity<?> login(@RequestBody User user) {
        User existingUser = userService.getUserByUsername(user.getUsername());

        if (existingUser != null) {
            String encodedPassword = existingUser.getPassword();
            String incomingPassword = user.getPassword();

            if (passwordEncoder.matches(incomingPassword, encodedPassword)) {
                System.out.println("inloggad");
                @SuppressWarnings("deprecation")
                String token = Jwts.builder()
                        .setSubject(existingUser.getUsername())
                        .setIssuedAt(new Date())
                        .setExpiration(new Date(System.currentTimeMillis() + jwtExpriationMs))
                        .signWith(SignatureAlgorithm.HS512, jwtSecret)
                        .compact();
                return ResponseEntity.ok(token);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Fel användarnamn eller lösenord");
    }

    @GetMapping("/user/{username}/accesstoken")
    public String getAccessToken(@PathVariable String username) {
        String accessToken = userService.getSpotifyAccessToken(username);
        if (accessToken == null) {
            throw new RuntimeException("Kunde inte hämta access token för användare: " + username);
        }
        return accessToken;
    }

    @GetMapping("/user/{loggedInUser}")
    public User getUserByUsername(@PathVariable String loggedInUser) {
        return userService.getUserByUsername(loggedInUser);
    }
}