package pt.migfonseca.vibecheck.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import pt.migfonseca.vibecheck.model.Album;
import pt.migfonseca.vibecheck.model.Artist;

@RepositoryRestResource
public interface AlbumRepository extends JpaRepository<Album, Long> {    
    @Query("SELECT a FROM Album a JOIN a.artists art WHERE a.albumName = :albumName AND art = :artist")
    Optional<Album> findByNameWithArtist(@Param("albumName") String name, @Param("artist") Artist artist);

    @Query("SELECT a FROM Album a JOIN a.features feat WHERE a.albumName = :albumName AND feat = :feature")
    Optional<Album> findByNameWithFeature(@Param("albumName") String name, @Param("feature") Artist artist);

    @Query("SELECT COUNT(a) > 0 FROM Album a JOIN a.artists art WHERE a.albumName = :albumName AND art = :artist")
    boolean existsByNameWithArtist(@Param("albumName") String albumName, @Param("artist") Artist artist);

    @Query("SELECT COUNT(a) > 0 FROM Album a JOIN a.features feat WHERE a.albumName = :albumName AND feat = :feature")
    boolean existsByNameWithFeature(@Param("albumName") String albumName, @Param("feature") Artist artist);

    List<Album> findByAlbumNameContainingIgnoreCase(String name);

}
