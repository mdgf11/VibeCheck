package pt.migfonseca.vibecheck.model;

import java.util.LinkedList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import static jakarta.persistence.InheritanceType.JOINED;
import jakarta.persistence.OneToMany;
import lombok.Data;
import pt.migfonseca.vibecheck.dto.SearchResponseDTO;
import pt.migfonseca.vibecheck.model.ratings.GenreRating;
import pt.migfonseca.vibecheck.model.ratings.VibeRating;

@Data
@Entity(name="raterEntity")
@Inheritance(strategy = JOINED)
public abstract class RaterEntity {
    @Id
    @GeneratedValue
    protected Long raterEntityId;

    protected String spotifyId;

    protected RaterEntity() {
        this.genreRatings = new LinkedList<>();
        this.vibeRatings = new LinkedList<>();
    }
    
    @OneToMany(mappedBy="raterEntity")
    protected List<GenreRating> genreRatings;

    @OneToMany(mappedBy="raterEntity")
    protected List<VibeRating> vibeRatings;
    
    public GenreRating addGenreRating(GenreRating genreRating) {
        this.genreRatings.add(genreRating);
        return genreRating;
    }

    abstract public SearchResponseDTO toResponseDTO();

}
