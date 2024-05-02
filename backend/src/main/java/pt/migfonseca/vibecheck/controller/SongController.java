package pt.migfonseca.vibecheck.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pt.migfonseca.vibecheck.model.Song;
import pt.migfonseca.vibecheck.service.SongService;

@RestController
@RequestMapping("/songs")
public class SongController {

    @Autowired
    SongService service;

    @GetMapping("/")
    public ResponseEntity<List<Song>> getAllSongs(){
        return new ResponseEntity<List<Song>>(service.getAllSongs(), HttpStatus.OK);
    }
}
