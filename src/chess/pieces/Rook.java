package chess.pieces;

import boardgame.Board;
import chess.model.ChessPiece;
import chess.model.Colour;

public class Rook extends ChessPiece {

    public Rook(Board board, Colour colour) {
        super(board, colour);
    }

    @Override
    public String toString() {
        return "R";
    }

    @Override
    public boolean[][] possibleMoves() {
        boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];
        int[][] directions = {
                {-1, 0}, // up
                {1, 0},  // down
                {0, -1}, // left
                {0, 1}   // right
        };

        for (int[] dir : directions) {
            sweepDirection(dir, mat, position);
        }
        return mat;
    }
}
