package pt.migfonseca.vibecheck.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.NoArgsConstructor;
import pt.migfonseca.vibecheck.dto.SearchResponseDTO;
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
        this.ratings = new ArrayList<>();
    }

    @OneToMany(mappedBy = "genre")
    List<GenreRating> ratings;

    public void addGenreRating(GenreRating genreRating) {
        this.ratings.add(genreRating);
    }

    public SearchResponseDTO toResponseDTO() {
        return new SearchResponseDTO(this.name, "genre");
    }

    @Override
    public String toString() {
        return "Genre{" +
                "genreId=" + genreId +
                ", name='" + name + '\'' +
                '}';
    }

}
