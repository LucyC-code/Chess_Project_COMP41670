package chess.pieces;

import boardgame.Board;
import boardgame.Piece;
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

        // 8 neighboring directions (row, col)
        int[][] directions = {
                {-1,  0}, { 1,  0}, { 0, -1}, { 0,  1},
                {-1, -1}, {-1,  1}, { 1, -1}, { 1,  1}
        };

        for (int[] d : directions) {
            Position p = new Position(position.getRow() + d[0], position.getColumn() + d[1]);
            if (getBoard().positionExists(p) && canMove(p)) {
                mat[p.getRow()][p.getColumn()] = true;
            }
        }

        // Castling
        if (getMoveCounter() == 0 && !chessMatch.isCheck()) {
            tryCastling(position, +3, new int[]{+1, +2}, mat);          // Kingside
            tryCastling(position, -4, new int[]{-1, -2, -3}, mat);      // Queenside
        }



        return mat;
    }
    public ChessMatch getChessMatch() {

        return chessMatch;
    }
    /**
     * Tries to mark castling moves if the path is clear and the rook is eligible.
     */
    private void tryCastling(Position position, int rookOffset, int[] pathOffsets, boolean[][] mat) {
        Position rookPos = new Position(position.getRow(), position.getColumn() + rookOffset);
        if (testRookCastling(rookPos)) {
            boolean pathClear = true;
            for (int offset : pathOffsets) {
                Position pathPos = new Position(position.getRow(), position.getColumn() + offset);
                if (getBoard().piece(pathPos) != null) {
                    pathClear = false;
                    break;
                }
            }
            // mark destination square if path is clear
            if (pathClear) {
                int kingTargetOffset = (rookOffset > 0) ? +2 : -2;
                mat[position.getRow()][position.getColumn() + kingTargetOffset] = true;
            }
        }
    }

    private boolean testRookCastling(Position position) {
        Piece p = getBoard().piece(position);
        return (p instanceof Rook) &&
                ((Rook) p).getColour() == getColour() &&
                ((Rook) p).getMoveCounter() == 0;
    }


}
