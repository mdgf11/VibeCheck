package pt.migfonseca.vibecheck.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pt.migfonseca.vibecheck.repo.PlaylistRepository;

@Service
public class PlaylistService {
    @Autowired
    private PlaylistRepository playlistRepository;
}
