package pt.migfonseca.vibecheck.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Image {
    
    @Id
    @GeneratedValue
    private Long id;
    
    private int height;
    private String url;

    public Image(int height, String url) {
        this.height = height;
        this.url = url;
    }
}
