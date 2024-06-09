package pt.migfonseca.vibecheck.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pt.migfonseca.vibecheck.dto.PlaylistDTO;
import pt.migfonseca.vibecheck.service.PlaylistService;

@RestController
@RequestMapping("/playlist")
public class PlaylistController {

    @Autowired
    PlaylistService service;

    @GetMapping
    ResponseEntity<PlaylistDTO> createPlaylist(@RequestParam("query") String query, @RequestParam(required = false, defaultValue = "") String artist, @RequestParam("type") String type) {
        try {
            return new ResponseEntity<PlaylistDTO>(service.getPlaylist(query, artist, type), HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

}
