package pt.migfonseca.vibecheck.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import pt.migfonseca.vibecheck.model.Vibe;

@RepositoryRestResource
public interface VibeRepository extends JpaRepository<Vibe, Long> {
    public List<Vibe> findByNameContainingIgnoreCase(String name);
    
    public Optional<Vibe> findByName(String name);

}
