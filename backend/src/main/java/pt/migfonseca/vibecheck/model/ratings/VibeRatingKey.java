package pt.migfonseca.vibecheck.model.ratings;

import java.io.Serializable;

import lombok.Data;

@Data
public class VibeRatingKey implements Serializable {
    Long vibeId;
    Long raterEntityId;
}
