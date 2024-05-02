package pt.migfonseca.vibecheck.model.ratings;

import java.io.Serializable;

import lombok.Data;

@Data
public class GenreRatingKey implements Serializable {
    Long genreId;

    Long raterEntityId;
}
