package pt.migfonseca.vibecheck.model.ratings;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;
import pt.migfonseca.vibecheck.model.RaterEntity;
import pt.migfonseca.vibecheck.model.Vibe;

@Entity
@Data
@IdClass(VibeRatingKey.class)
@NoArgsConstructor
public class VibeRating {

    @Id
    private long vibeId;
    @Id
    private long raterEntityId;

    @ManyToOne
    @JoinColumn(name="raterEntityId",
                referencedColumnName="raterEntityId",
                updatable=false,
                insertable=false)
    private RaterEntity raterEntity;

    @ManyToOne
    @JoinColumn(name="vibeId",
                referencedColumnName="vibeId",
                updatable=false,
                insertable=false)
    private Vibe vibe;

    private double rating;
}
