package pt.migfonseca.vibecheck.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import pt.migfonseca.vibecheck.model.Genre;

@RepositoryRestResource
public interface GenreRepository extends JpaRepository<Genre, Long> {    
        public Optional<Genre> findByName(String name);
}
