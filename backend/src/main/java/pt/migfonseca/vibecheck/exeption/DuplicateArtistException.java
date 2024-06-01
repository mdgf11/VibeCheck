package pt.migfonseca.vibecheck.exeption;

public class DuplicateArtistException extends RuntimeException {

    public DuplicateArtistException() {
        super();
    }

    public DuplicateArtistException(String message) {
        super(message);
    }

    public DuplicateArtistException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateArtistException(Throwable cause) {
        super(cause);
    }
}