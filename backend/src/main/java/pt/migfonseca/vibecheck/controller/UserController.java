package pt.migfonseca.vibecheck.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
        return new ResponseEntity<UserDTO>(userService.createUser(json.get("username").asText(),
                                        json.get("password").asText()), HttpStatus.OK);
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<UserDTO>> getLeaderboard() {
        return new ResponseEntity<List<UserDTO>>(userService.getLeaderBoard(), HttpStatus.OK);
    }
}
