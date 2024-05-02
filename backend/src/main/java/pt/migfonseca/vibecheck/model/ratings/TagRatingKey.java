package pt.migfonseca.vibecheck.model.ratings;

import java.io.Serializable;

import lombok.Data;

@Data
public class TagRatingKey implements Serializable {
    Long tagId;
    Long raterEntityId;
}
