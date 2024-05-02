package pt.migfonseca.vibecheck.model.ratings;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import pt.migfonseca.vibecheck.model.RaterEntity;
import pt.migfonseca.vibecheck.model.Tag;

@Entity
@Data
@IdClass(TagRatingKey.class)
public class TagRating {

    @Id
    private long tagId;
    @Id
    private long raterEntityId;

    @ManyToOne
    @JoinColumn(name="raterEntityId",
                referencedColumnName="raterEntityId",
                updatable=false,
                insertable=false)
    private RaterEntity raterEntity;

    @ManyToOne
    @JoinColumn(name="tagId",
                referencedColumnName="tagId",
                updatable=false,
                insertable=false)
    private Tag tag;

    private Long rating;
}
