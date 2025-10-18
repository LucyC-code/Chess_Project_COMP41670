package chess.model;

import boardgame.Position;
import chess.exceptions.ChessException;

/**
 * Represents a chessboard position using standard chess notation (e.g., a1, e4).
 * Provides conversion between chess notation and internal board coordinates.
 */
public class ChessPosition {

    private char column;
    private int row;

    /**
     * Creates a new chess position.
     *
     * @param column column letter (a–h)
     * @param row    row number (1–8)
     * @throws ChessException if the position is outside the valid chessboard range
     */
    public ChessPosition(char column, int row) {
        if (column < 'a' || column > 'h' || row < 1 || row > 8) {
            throw new ChessException("Invalid position: use values from a1 to h8.");
        }
        this.column = column;
        this.row = row;
    }

    /**
     * Converts this chess position to a board matrix position.
     * <p>
     * For example, a1 → (7,0), h8 → (0,7).
     *
     * @return equivalent {@link Position} object in matrix coordinates
     */
    public Position toPosition() {
        int boardRow = 8 - row;
        int boardCol = column - 'a';
        return new Position(boardRow, boardCol);
    }

    /**
     * Converts from a board matrix position to a chess notation position.
     * <p>
     *
     * @param position internal board {@link Position}
     * @return corresponding {@link ChessPosition}
     */
    public static ChessPosition fromPosition(Position position) {
        return new ChessPosition((char) ('a' + position.getColumn()), 8 - position.getRow());
    }

    /**
     * @return position in standard chess notation (e.g., "e4")
     */
    @Override
    public String toString() {
        return "" + column + row;
    }
}
