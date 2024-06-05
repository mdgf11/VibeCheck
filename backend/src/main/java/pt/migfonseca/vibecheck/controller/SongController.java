package pt.migfonseca.vibecheck.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pt.migfonseca.vibecheck.dto.ArtistDTO;
import pt.migfonseca.vibecheck.dto.SongDTO;
import pt.migfonseca.vibecheck.service.SongService;
import pt.migfonseca.vibecheck.service.SpotifyTokenService;

@RestController
@RequestMapping("/songs")
public class SongController {

    @Autowired
    SongService songService;

    @Autowired
    SpotifyTokenService spotifyTokenService;

    @GetMapping
    public ResponseEntity<List<SongDTO>> getSongs() {
        return new ResponseEntity<List<SongDTO>>(songService.getAllSongs(), HttpStatus.OK);
    }

    @PostMapping("/addArtist")
    public ResponseEntity<List<ArtistDTO>> addArtist(@RequestParam String name, @RequestParam(required = false, defaultValue = "1") Integer size) throws IOException {
        return new ResponseEntity<List<ArtistDTO>>(songService.addArtist(name, size), HttpStatus.OK);
    }

    @PostMapping("/discover")
    public ResponseEntity<List<ArtistDTO>> discover(@RequestParam Integer size, @RequestParam Integer popularity) throws IOException {
        songService.discover(size, popularity);
        return new ResponseEntity<List<ArtistDTO>>(songService.getAllUndicoveredArtistsWithPopularity(size, popularity), HttpStatus.OK);
    }

}
    
