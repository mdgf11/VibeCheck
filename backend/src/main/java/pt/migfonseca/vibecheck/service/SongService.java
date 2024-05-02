package pt.migfonseca.vibecheck.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pt.migfonseca.vibecheck.model.Song;
import pt.migfonseca.vibecheck.repo.SongRepository;

@Service
public class SongService {

    @Autowired
    SongRepository repository;

    public  List<Song> getAllSongs() {
        return repository.findAll();
    }
    
}
