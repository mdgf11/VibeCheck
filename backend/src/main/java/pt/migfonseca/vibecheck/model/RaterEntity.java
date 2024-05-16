package pt.migfonseca.vibecheck.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import lombok.NoArgsConstructor;
import pt.migfonseca.vibecheck.model.ratings.GenreRating;
import pt.migfonseca.vibecheck.model.ratings.VibeRating;

@Entity(name="raterEntity")
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor
public abstract class RaterEntity {
    @Id
    @GeneratedValue
    protected Long raterEntityId;

    @OneToMany(mappedBy="raterEntity")
    private List<GenreRating> genreRatings;

    @OneToMany(mappedBy="raterEntity")
    private List<VibeRating> vibeRatings;
    
}
