package chess.pieces;

import boardgame.Board;
import chess.model.ChessPiece;
import chess.model.Colour;

public class Bishop extends ChessPiece {
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

    @Override
    public String toString() {
        return "B";
    }
}