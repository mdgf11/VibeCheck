package pt.migfonseca.vibecheck.repo;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import pt.migfonseca.vibecheck.model.ratings.GenreRating;

@RepositoryRestResource
public interface GenreRatingRepository extends JpaRepository<GenreRating, Long> {    
}
