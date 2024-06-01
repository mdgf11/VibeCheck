package pt.migfonseca.vibecheck.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import pt.migfonseca.vibecheck.model.Artist;
import pt.migfonseca.vibecheck.model.Song;

@RepositoryRestResource
public interface SongRepository extends JpaRepository<Song, Long> {    

    @Query("SELECT COUNT(s) > 0 FROM Song s JOIN s.artists a WHERE s.songName = :songName AND a = :artist")
    boolean existsBySongNameAndArtist(@Param("songName") String name, @Param("artist") Artist artist);

    @Query("SELECT s FROM Song s JOIN s.artists a WHERE s.songName = :songName AND a = :artist")
    Optional<Song> findByNameWithArtist(@Param("songName") String name, @Param("artist") Artist artist);

}