package pt.migfonseca.vibecheck.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pt.migfonseca.vibecheck.dto.PlaylistDTO;
import pt.migfonseca.vibecheck.dto.SettingsDTO;
import pt.migfonseca.vibecheck.service.PlaylistService;

@RestController
@RequestMapping("/playlist")
public class PlaylistController {

    @Autowired
    PlaylistService service;


    @PostMapping("/create")
    public ResponseEntity<PlaylistDTO> createPlaylist(@RequestParam("query") String query,
            @RequestParam(name = "artist", required = false, defaultValue = "") String artist,
            @RequestParam(name = "type") String type,
            @RequestBody SettingsDTO settings) throws IOException {
        System.out.println(settings);
        PlaylistDTO playlist = service.getPlaylist(query, artist, type, settings);
        return new ResponseEntity<>(playlist, HttpStatus.OK);
    }

    @GetMapping("/create")
    ResponseEntity<PlaylistDTO> createPlaylistWithSettings(
            @RequestParam("query") String query,
            @RequestParam(name = "artist", required = false, defaultValue = "") String artist,
            @RequestParam("type") String type) {
        try {
            SettingsDTO settings = new SettingsDTO(null, 20, null, null, null, null, null, null, null);
            return new ResponseEntity<>(service.getPlaylist(query, artist, type, settings), HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
}
