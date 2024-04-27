package pt.migFonseca.vibecheck;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SongService {
    @Autowired
    private SongRepository songRepository;

    public List<Song> allSongs() {
        return songRepository.findAll();
    }

    public Optional<Song> getSong(String imdbId) {
        return songRepository.findSongByImdbId(imdbId);
    }

    
}
