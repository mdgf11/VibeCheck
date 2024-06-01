package pt.migfonseca.vibecheck.exeption;

public class ArtistNotFoundException extends RuntimeException {

    public ArtistNotFoundException() {
        super();
    }

    public ArtistNotFoundException(String message) {
        super(message);
    }

    public ArtistNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArtistNotFoundException(Throwable cause) {
        super(cause);
    }
}