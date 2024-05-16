package pt.migfonseca.vibecheck.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pt.migfonseca.vibecheck.model.ratings.VibeRating;

@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@PrimaryKeyJoinColumn(name = "vibeId")
public class Vibe extends RaterEntity{

    private String title;
    
    @OneToMany(mappedBy="vibe")
    private List<VibeRating> vibeRatings;

}
