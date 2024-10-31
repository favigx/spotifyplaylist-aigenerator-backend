package com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.services;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.models.User;

import io.jsonwebtoken.io.IOException;

@Service
public class UserService {

    private final MongoOperations mongoOperations;
    private final EncryptionService encryptionService;
    private PasswordEncoder passwordEncoder;

    public UserService(MongoOperations mongoOperations, EncryptionService encryptionService,
            PasswordEncoder passwordEncoder) {
        this.mongoOperations = mongoOperations;
        this.encryptionService = encryptionService;
        this.passwordEncoder = passwordEncoder;
    }

    public User addUser(User user) {
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(user.getUsername()));
        User dbUser = mongoOperations.findOne(query, User.class);

        if (dbUser != null) {
            throw new RuntimeException("Användarnamn upptaget");
        }
        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);
        return mongoOperations.insert(user);
    }

    public User getUserByUsername(String username) {
        Query query = new Query(Criteria.where("username").is(username));
        return mongoOperations.findOne(query, User.class);
    }

    public void saveSpotifyAccessToken(String username, String accessToken) {
        try {
            String encryptedToken = encryptionService.encrypt(accessToken);

            User user = getUserByUsername(username);

            if (user != null) {
                user.setSpotifyAccessToken(encryptedToken);
                mongoOperations.save(user);
            } else {
                throw new RuntimeException("Användaren hittades inte");
            }

        } catch (Exception e) {
            System.err.println("Kunde inte spara spotify-accesstoken: " + e.getMessage());
        }
    }

    public String getSpotifyAccessToken(String username) {
        try {
            User user = getUserByUsername(username);

            if (user != null) {
                String encryptedToken = user.getSpotifyAccessToken();
                return encryptionService.decrypt(encryptedToken);
            } else {
                throw new RuntimeException("Användaren hittades inte");
            }

        } catch (Exception e) {
            System.err.println("Kunde inte hämta spotify-accesstoken: " + e.getMessage());
            return null;
        }
    }

    public User updateUser(User user) {
        return mongoOperations.save(user);
    }

    public void uploadProfileImage(String username, MultipartFile file) throws java.io.IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("Ingen fil vald");
        }

        try {
            byte[] imageBytes = file.getBytes();

            User user = getUserByUsername(username);
            user.setProfileImage(imageBytes);
            updateUser(user);
        } catch (IOException e) {
            throw new RuntimeException("Kunde inte läsa profilbilden: " + e.getMessage());
        }
    }
}