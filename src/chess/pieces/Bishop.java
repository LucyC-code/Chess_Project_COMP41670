package chess.pieces;

import boardgame.Board;
import boardgame.Position;
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
            Position pos = new Position(position.getRow() + dir[0], position.getColumn() + dir[1]);
            while (getBoard().positionExists(pos) && !getBoard().thereIsAPiece(pos)) {
                mat[pos.getRow()][pos.getColumn()] = true;
                pos.setValues(pos.getRow() + dir[0], pos.getColumn() + dir[1]);
            }
            if (getBoard().positionExists(pos) && isThereOpponentPiece(pos)) {
                mat[pos.getRow()][pos.getColumn()] = true;
            }
        }

        return mat;
    }

    @Override
    public String toString() {
        return "B";
    }
}