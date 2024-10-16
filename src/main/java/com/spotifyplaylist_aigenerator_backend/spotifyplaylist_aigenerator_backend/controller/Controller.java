package com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @GetMapping
    public String getIndex() {
        return "{'message': 'Hello, AI-generator!'}";
    }

}
