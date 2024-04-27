package pt.migFonseca.vibecheck;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongRepository extends MongoRepository<Song, ObjectId> {

    Optional<Song> findSongByImdbId(String imdbId);

} 
