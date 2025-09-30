package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.core.ChessMatch;
import chess.model.ChessPiece;
import chess.model.Colour;

public class King extends ChessPiece {

    private final ChessMatch chessMatch;

    public King(Board board, Colour colour, ChessMatch chessMatch) {
        super(board, colour);
        this.chessMatch = chessMatch;
    }

    @Override
    public String toString() {
        return "K";
    }

    private boolean canMove(Position pos) {
        ChessPiece piece = (ChessPiece) getBoard().piece(pos);
        return piece == null || piece.getColour() != getColour();
    }


    @Override
    public boolean[][] possibleMoves() {
        boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];
        int[][] directions = {
                {-1, 0}, {1, 0}, {0, -1}, {0, 1},
                {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
        };

        for (int[] dir : directions) {
            sweepDirection(dir, mat, position);
        }
        return mat;
    }

    public ChessMatch getChessMatch() {
        return chessMatch;
    }
}
