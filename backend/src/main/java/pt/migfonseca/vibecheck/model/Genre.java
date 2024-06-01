package pt.migfonseca.vibecheck.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.NoArgsConstructor;
import pt.migfonseca.vibecheck.model.ratings.GenreRating;

@Entity
@Data
@Table(name = "genre",
        uniqueConstraints =
        @UniqueConstraint(columnNames = "name"))
@NoArgsConstructor
public class Genre {
    @Id
    @GeneratedValue
    private Long genreId;

    private String name;

    public Genre(String name) {
        this.name = name;
        this.ratings = new HashSet<>();
    }

    @OneToMany(mappedBy = "genre")
    Set<GenreRating> ratings;

    public GenreRating addGenreRating(RaterEntity raterEntity, long ratingValue) {
        GenreRating genreRating = new GenreRating(ratingValue);
        genreRating.setGenre(this);
        genreRating.setGenreId(this.genreId);
        genreRating.setRaterEntity(raterEntity);
        genreRating.setRaterEntityId(raterEntity.getRaterEntityId());
        this.ratings.add(genreRating);
        raterEntity.getGenreRatings().add(genreRating);
        return genreRating;
    }
}
