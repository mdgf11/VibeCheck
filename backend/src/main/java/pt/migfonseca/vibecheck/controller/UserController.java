package pt.migfonseca.vibecheck.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.node.ObjectNode;

import pt.migfonseca.vibecheck.dto.UserDTO;
import pt.migfonseca.vibecheck.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<UserDTO> createUser(@RequestBody ObjectNode json) {
        String email = json.has("email") ? json.get("email").asText() : null;
        String username = json.has("username") ? json.get("username").asText() : null;
        String spotifyId = json.has("spotifyId") ? json.get("spotifyId").asText() : null;
        String password = json.has("password") ? json.get("password").asText() : null;
        UserDTO user = userService.createUser(email, username, spotifyId, password);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping("/{userId}/spotifyId")
    public ResponseEntity<UserDTO> updateSpotifyId(@PathVariable Long userId, @RequestBody ObjectNode json) {
        String spotifyId = json.get("spotifyId").asText();
        UserDTO user = userService.updateSpotifyId(userId, spotifyId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<UserDTO>> getLeaderboard() {
        return new ResponseEntity<>(userService.getLeaderBoard(), HttpStatus.OK);
    }
}
