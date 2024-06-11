package pt.migfonseca.vibecheck.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import pt.migfonseca.vibecheck.model.Album;
import pt.migfonseca.vibecheck.model.Artist;
import pt.migfonseca.vibecheck.model.Genre;
import pt.migfonseca.vibecheck.model.Song;
import pt.migfonseca.vibecheck.model.Vibe;

@RepositoryRestResource
public interface SongRepository extends JpaRepository<Song, Long> {    

    @Query("SELECT COUNT(s) > 0 FROM Song s JOIN s.artists a WHERE s.songName = :songName AND a = :artist")
    boolean existsBySongNameAndArtist(@Param("songName") String name, @Param("artist") Artist artist);

    @Query("SELECT s FROM Song s JOIN s.artists a WHERE s.songName = :songName AND a = :artist")
    Optional<Song> findByNameWithArtist(@Param("songName") String name, @Param("artist") Artist artist);

    List<Song> findBySongNameContainingIgnoreCase(String name);

    @Query("SELECT s FROM Song s JOIN s.artists a WHERE a = :artist")
    List<Song> findAllByArtist(@Param("artist") Artist artist);
    
    @Query("SELECT s FROM Song s JOIN s.albums a JOIN a.artists ar WHERE a = :album AND ar = :artist")
    List<Song> findAllByAlbumAndArtist(@Param("album") Album album, @Param("artist") Artist artist);

    @Query("SELECT s FROM Song s JOIN s.vibeRatings vr WHERE vr.vibe = :vibe")
    List<Song> findAllByVibe(@Param("vibe") Vibe vibe);

    @Query("SELECT s FROM Song s JOIN s.genreRatings gr WHERE gr.genre = :genre")
    List<Song> findAllByGenre(@Param("genre") Genre genre);

    @Query("SELECT s FROM Song s JOIN s.artists a WHERE a IN :artists")
    List<Song> findAllByArtistsIn(@Param("artists") List<Artist> artists);

    @Query("SELECT s FROM Song s JOIN s.genreRatings gr WHERE gr.genre IN :genres")
    List<Song> findAllByGenresIn(@Param("genres") List<Genre> genres);

    @Query("SELECT s FROM Song s JOIN s.vibeRatings vr WHERE vr.vibe IN :vibes")
    List<Song> findAllByVibesIn(@Param("vibes") List<Vibe> vibes);
}
