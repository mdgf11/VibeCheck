package pt.migFonseca.vibecheck;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/songs")
@CrossOrigin(origins = "*")
public class SongController {
    @Autowired
    private SongService songService;
    
    @GetMapping
    public ResponseEntity<List<Song>> getAllSongs() {
        return new ResponseEntity<List<Song>>(songService.allSongs(), HttpStatus.OK);
    }

    @GetMapping("/{imdbId}")
    public ResponseEntity<Optional<Song>> getSong(@PathVariable String imdbId) {
        return new ResponseEntity<Optional<Song>>(songService.getSong(imdbId), HttpStatus.OK);
    }
}
