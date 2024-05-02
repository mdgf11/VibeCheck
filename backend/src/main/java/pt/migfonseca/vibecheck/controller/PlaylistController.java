package pt.migfonseca.vibecheck.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pt.migfonseca.vibecheck.model.Playlist;
import pt.migfonseca.vibecheck.service.PlaylistService;

@RestController
@RequestMapping("/playlist")
public class PlaylistController {

    @Autowired
    PlaylistService service;

    @PostMapping("/addPlaylist")
    public void addPlaylist(@RequestBody Playlist playlist){
        service.addPlaylist(playlist);
    }
}
