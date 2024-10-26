// package
// com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.controllers;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.messaging.handler.annotation.MessageMapping;
// import org.springframework.messaging.handler.annotation.Payload;
// import org.springframework.messaging.handler.annotation.SendTo;
// import org.springframework.messaging.simp.SimpMessagingTemplate;
// import org.springframework.stereotype.Controller;
// import org.springframework.web.bind.annotation.PathVariable;

// import
// com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.services.UserService;

// @Controller
// public class StompController {

// @Autowired
// UserService userService;

// private final SimpMessagingTemplate messagingTemplate;

// @Autowired
// public StompController(SimpMessagingTemplate messagingTemplate) {
// this.messagingTemplate = messagingTemplate;
// }

// @MessageMapping("/check-access-token/{username}")
// @SendTo("/topic/access-token-status")
// public String checkAccessToken(@Payload String message, @PathVariable String
// username) {
// String accessToken = userService.getSpotifyAccessToken(username);

// if (accessToken == null) {
// return "Du har inte loggat in på Spotify och kan därför inte skapa
// spellistor.";
// }

// return "Access token är giltig. Du kan skapa spellistor.";
// }
// }