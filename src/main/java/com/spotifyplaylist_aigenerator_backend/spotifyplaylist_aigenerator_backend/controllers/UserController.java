package com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.models.User;
import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.services.UserService;

@CrossOrigin(origins = "*")
@RestController
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

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
}
