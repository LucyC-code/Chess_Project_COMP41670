package chess.pieces;

import boardgame.Board;
import chess.model.ChessPiece;
import chess.model.Colour;

/**
 * Represents a Bishop chess piece.
 * The Bishop can move diagonally in all four directions
 * until it encounters another piece or the edge of the board.
 */

public class Bishop extends ChessPiece {

    /**
     * Creates a Bishop with the specified board and colour.
     *
     * @param board  the board the piece belongs to
     * @param colour the colour of the piece (e.g., WHITE or BLACK)
     */
    public Bishop(Board board, Colour colour) {
        super(board, colour);
    }

    @Override
    public boolean[][] possibleMoves() {
        boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];
        int[][] directions = {
                {-1, -1},
                {-1, 1},
                {1, -1},
                {1, 1},
        };

        for (int[] dir : directions) {
            sweepDirection(dir, mat, position);
        }

        return mat;
    }

    /**
     * Returns the string representation of the Bishop.
     *
     * @return "B" as the symbol for the Bishop.
     */
    @Override
    public String toString() {
        return "B";
    }
}