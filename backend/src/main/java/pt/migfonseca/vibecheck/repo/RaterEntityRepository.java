package pt.migfonseca.vibecheck.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import pt.migfonseca.vibecheck.model.RaterEntity;

@RepositoryRestResource
public interface RaterEntityRepository extends JpaRepository<RaterEntity, Long> {
}