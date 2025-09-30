package chess.pieces;

import boardgame.Position;
import boardgame.Board;
import chess.core.ChessMatch;
import chess.model.Colour;
import chess.model.ChessPiece;

public class Pawn extends ChessPiece {

    private final ChessMatch chessMatch;

    public Pawn(Board board, Colour colour, ChessMatch chessMatch) {
        super(board, colour);
        this.chessMatch = chessMatch;
    }

    @Override
    public boolean[][] possibleMoves() {
        boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];
        int dir = (getColour() == Colour.WHITE) ? -1 : 1;
        int startRow = (getColour() == Colour.WHITE) ? 6 : 1;
        int enPassantRow = (getColour() == Colour.WHITE) ? 3 : 4;

        Board board = getBoard();

        // Forward moves
        Position oneStep = new Position(position.getRow() + dir, position.getColumn());
        if (board.positionExists(oneStep) && !board.thereIsAPiece(oneStep)) {
            mat[oneStep.getRow()][oneStep.getColumn()] = true;

            Position twoStep = new Position(position.getRow() + 2 * dir, position.getColumn());
            if (position.getRow() == startRow && board.positionExists(twoStep) && !board.thereIsAPiece(twoStep)) {
                mat[twoStep.getRow()][twoStep.getColumn()] = true;
            }
        }
    }


    @Override
    public String toString() {
        return "P";
    }
}

