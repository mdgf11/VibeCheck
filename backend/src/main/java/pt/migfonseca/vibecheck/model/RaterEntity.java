package pt.migfonseca.vibecheck.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import pt.migfonseca.vibecheck.model.ratings.GenreRating;
import pt.migfonseca.vibecheck.model.ratings.TagRating;

@Entity(name="raterEntity")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class RaterEntity {
    @Id
    private Long raterEntityId;

    @OneToMany(mappedBy="raterEntity")
    private List<GenreRating> genreRatings;

    @OneToMany(mappedBy="raterEntity")
    private List<TagRating> tagRatings;
    
}
