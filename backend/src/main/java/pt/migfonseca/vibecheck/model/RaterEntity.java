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
import lombok.NoArgsConstructor;
import pt.migfonseca.vibecheck.model.ratings.GenreRating;
import pt.migfonseca.vibecheck.model.ratings.VibeRating;

@Data
@Entity(name="raterEntity")
@Inheritance(strategy = JOINED)
@NoArgsConstructor
public abstract class RaterEntity {
    @Id
    @GeneratedValue
    protected Long raterEntityId;

    protected String spotifyId;
    
    @OneToMany(mappedBy="raterEntity")
    protected List<GenreRating> genreRatings;

    @OneToMany(mappedBy="raterEntity")
    protected List<VibeRating> vibeRatings;
    
    public GenreRating addGenreRating(Genre genre, long ratingValue) {
        GenreRating genreRating = new GenreRating(ratingValue);
        genreRating.setGenre(genre);
        genreRating.setGenreId(genre.getGenreId());
        genreRating.setRaterEntity(this);
        genreRating.setRaterEntityId(this.raterEntityId);
        if (this.genreRatings == null)
            this.genreRatings = new LinkedList<>();
        this.genreRatings.add(genreRating);
        genre.getRatings().add(genreRating);
        return genreRating;
    }
}
