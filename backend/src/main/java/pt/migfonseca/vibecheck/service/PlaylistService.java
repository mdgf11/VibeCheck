package pt.migfonseca.vibecheck.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pt.migfonseca.vibecheck.model.Playlist;
import pt.migfonseca.vibecheck.repo.PlaylistRepository;

@Service
public class PlaylistService {
    @Autowired
    PlaylistRepository playlistRepository;

    public void addPlaylist(Playlist playlist) {
        playlistRepository.save(playlist);
    }


}
