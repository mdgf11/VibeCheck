package pt.migfonseca.vibecheck.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pt.migfonseca.vibecheck.model.ratings.TagRating;

@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@PrimaryKeyJoinColumn(name = "tagId")
public class Tag extends RaterEntity{

    private String title;
    
    @OneToMany(mappedBy="tag")
    private List<TagRating> tagRatings;

    

}
