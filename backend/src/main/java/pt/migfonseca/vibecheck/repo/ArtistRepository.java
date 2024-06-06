package pt.migfonseca.vibecheck.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import pt.migfonseca.vibecheck.model.Artist;

@RepositoryRestResource
public interface ArtistRepository extends JpaRepository<Artist, Long> {    
    Optional<Artist> findByName(String name);

    boolean existsBySpotifyId(String spotifyId);
    
    List<Artist> findByNameContainingIgnoreCase(String name);

    @Query("SELECT DISTINCT a FROM Artist a " +
       "JOIN a.songs s " +
       "JOIN s.artists sa " +
       "WHERE sa = :artist " +
       "AND a != :artist")
    List<Artist> findAllArtistsInSongs(@Param("artist") Artist artist);

}
