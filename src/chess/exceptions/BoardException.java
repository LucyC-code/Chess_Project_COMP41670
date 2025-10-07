package chess.exceptions;

/**
 * Exception for invalid board operations (e.g., out-of-bounds or occupied position).
 */
public class BoardException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public BoardException(String message) {
        super(message);
    }
}
