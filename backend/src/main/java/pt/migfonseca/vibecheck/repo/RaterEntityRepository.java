package pt.migfonseca.vibecheck.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import pt.migfonseca.vibecheck.model.RaterEntity;

public interface RaterEntityRepository extends JpaRepository<RaterEntity, Long> {
}