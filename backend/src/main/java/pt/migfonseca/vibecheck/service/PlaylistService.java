package pt.migfonseca.vibecheck.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pt.migfonseca.vibecheck.dto.PlaylistDTO;
import pt.migfonseca.vibecheck.model.Playlist;
import pt.migfonseca.vibecheck.repo.PlaylistRepository;

@Service
public class PlaylistService {
    @Autowired
    private PlaylistRepository playlistRepository;
    @Autowired
    private SongService songService;

    public PlaylistDTO createPlaylist(String name) {
        Playlist playlist = new Playlist(name, songService.getAllSongs());
        playlistRepository.save(playlist);
        return new PlaylistDTO(playlist);
    }


}
