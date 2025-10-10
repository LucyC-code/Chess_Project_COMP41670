package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.model.ChessPiece;
import chess.model.Colour;

public class Knight extends ChessPiece {

    public Knight(Board board, Colour colour) {
        super(board, colour);
    }

    @Override
    public String toString() {
        return "N";
    }

    private boolean canMove(Position position) {
        ChessPiece p = (ChessPiece) getBoard().piece(position);
        return p == null || p.getColour() != getColour();
    }

    @Override
    public boolean[][] possibleMoves() {
        boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];

        // All 8 possible knight moves
        int[][] directions = {
                {-2, -1}, {-2, 1},
                {-1, -2}, {-1, 2},
                {1, -2},  {1, 2},
                {2, -1},  {2, 1}
        };

        for (int[] dir : directions) {
            Position newPos = new Position(position.getRow() + dir[0], position.getColumn() + dir[1]);
            if (getBoard().positionExists(newPos) && canMove(newPos)) {
                mat[newPos.getRow()][newPos.getColumn()] = true;
            }
        }

        return mat;
    }
}
