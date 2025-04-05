package com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.user;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@RequestMapping("/api/user")
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

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/register")
    public ResponseEntity<?> addUser(@RequestBody User user) {
        try {
            User addedUser = userService.addUser(user);
            return ResponseEntity.ok(addedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ett oväntat fel uppstod. Försök igen senare.");
        }
    }

    @PostMapping("/register/app")
    public ResponseEntity<?> addUserForMobileApplication(@RequestBody User user) {
        try {
            User addedUser = userService.addUserForMobileApplication(user);
            return ResponseEntity.ok(addedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ett oväntat fel uppstod. Försök igen senare.");
        }
    }

    @GetMapping("/{email}/check-email")
    public ResponseEntity<String> checkEmail(@PathVariable String email) throws IOException {
        try {
            userService.existingEmail(email);
            return ResponseEntity.ok("E-posten är tillgänglig.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping("{username}/check-username")
    public ResponseEntity<String> checkUsername(@PathVariable String username) throws IOException {
        try {
            userService.existingUsername(username);
            return ResponseEntity.ok("\"Användarnamnet är tillgängligt.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        User existingUser = userService.getUserByUsername(user.getUsername());

        if (existingUser != null) {
            String encodedPassword = existingUser.getPassword();
            String incomingPassword = user.getPassword();

            if (passwordEncoder.matches(incomingPassword, encodedPassword)) {
                System.out.println("inloggad");
                String token = Jwts.builder()
                        .setSubject(existingUser.getUsername())
                        .setIssuedAt(new Date())
                        .setExpiration(new Date(System.currentTimeMillis() + jwtExpriationMs))
                        .signWith(SignatureAlgorithm.HS512, jwtSecret.getBytes())
                        .compact();
                return ResponseEntity.ok(token);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Fel användarnamn eller lösenord");
    }

    @GetMapping("/{loggedInUser}/spotify-token")
    public String getAccessToken(@PathVariable String loggedInUser) {
        String accessToken = userService.getSpotifyAccessToken(loggedInUser);
        if (accessToken == null) {
            throw new RuntimeException("Kunde inte hämta access token för användare: " + loggedInUser);
        }
        return accessToken;
    }

    @GetMapping("/{loggedInUser}/user")
    public User getUserByUsername(@PathVariable String loggedInUser) {
        return userService.getUserByUsername(loggedInUser);
    }

    @PutMapping("/{loggedInUser}/profile-image")
    public ResponseEntity<String> updateProfileImage(
            @PathVariable String loggedInUser,
            @RequestParam("file") MultipartFile file) throws IOException {
        try {
            userService.uploadProfileImage(loggedInUser, file);
            return ResponseEntity.ok("Profilbild uppladdad framgångsrikt");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}