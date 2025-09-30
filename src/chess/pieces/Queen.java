package chess.pieces;

/**
 *
 * Author: Lucy Crowe
 * Version: 1.0.0
 * Queen class
 * Finds possible moves for a queen piece 'Q'
 *
 */

import boardgame.Board;
import chess.model.ChessPiece;
import chess.model.Colour;

public class Queen extends ChessPiece {

    public Queen(Board board, Colour colour) {
        super(board, colour);
    }

    @Override
    public String toString() {
        return "Q";
    }

    @Override
    public boolean[][] possibleMoves() {
        boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];

        int[][] directions = {
                {-1,  0}, {1,  0}, {0, -1}, {0, 1},     // vertical & horizontal
                {-1, -1}, {-1, 1}, {1, -1}, {1, 1}      // diagonals
        };

        for (int[] dir : directions) {
            sweepDirection(dir, mat, position);
        }


        return mat;
    }
}