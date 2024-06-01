package pt.migfonseca.vibecheck.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import pt.migfonseca.vibecheck.model.Artist;

@RepositoryRestResource
public interface ArtistRepository extends JpaRepository<Artist, Long> {    
    public Optional<Artist> findByName(String name);

    public boolean existsBySpotifyId(String spotifyId);
}
