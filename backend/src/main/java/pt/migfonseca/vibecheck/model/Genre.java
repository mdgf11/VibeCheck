package pt.migfonseca.vibecheck.model;

import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import pt.migfonseca.vibecheck.model.ratings.GenreRating;

@Entity
@Data
@Table(name = "genre")
public class Genre {
    @Id
    private Long genreId;

    private String name;

    @OneToMany(mappedBy = "genre")
    Set<GenreRating> ratings;

}
