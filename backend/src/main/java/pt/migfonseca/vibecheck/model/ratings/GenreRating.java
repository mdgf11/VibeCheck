package pt.migfonseca.vibecheck.model.ratings;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;
import pt.migfonseca.vibecheck.model.Genre;
import pt.migfonseca.vibecheck.model.RaterEntity;

@Entity
@Data
@IdClass(GenreRatingKey.class)
@NoArgsConstructor
public class GenreRating {

    public GenreRating(RaterEntity raterEntity, Genre genre, long rating) {
        this.genreId = genre.getGenreId();
        this.genre = genre;
        genre.addGenreRating(this);
        this.raterEntity = raterEntity;
        this.raterEntityId = raterEntity.getRaterEntityId();
        this.rating = rating;
    }

    @Id
    private long genreId;
    @Id
    private long raterEntityId;

    @ManyToOne
    @JoinColumn(name="raterEntityId",
                referencedColumnName="raterEntityId",
                updatable=false,
                insertable=false)
    private RaterEntity raterEntity;

    @ManyToOne
    @JoinColumn(name="genreId",
                referencedColumnName="genreId",
                updatable=false,
                insertable=false)
    private Genre genre;

    private long rating;
}
