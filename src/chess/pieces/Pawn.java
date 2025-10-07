package chess.pieces;

import boardgame.Position;
import boardgame.Board;
import chess.core.ChessMatch;
import chess.model.Colour;
import chess.model.ChessPiece;

public class Pawn extends ChessPiece {

    private ChessMatch chessMatch;

    public Pawn(Board board, Colour colour, ChessMatch chessMatch) {
        super(board, colour);
        this.chessMatch = chessMatch;
    }

    @Override
    public boolean[][] possibleMoves() {
        boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];
        int dir = (getColour() == Colour.WHITE) ? -1 : 1;  // movement direction
        int startRow = (getColour() == Colour.WHITE) ? 6 : 1; // starting row (0-indexed, row 6 = white pawns, row 1 = black pawns)
        int enPassantRow = (getColour() == Colour.WHITE) ? 3 : 4; //adding en Passant logic

        Board board = getBoard();

        // Forward moves
        Position oneStep = new Position(position.getRow() + dir, position.getColumn());
        if (board.positionExists(oneStep) && !board.thereIsAPiece(oneStep)) {
            mat[oneStep.getRow()][oneStep.getColumn()] = true;

            // Two-step move from starting row
            Position twoStep = new Position(position.getRow() + 2 * dir, position.getColumn());
            if (position.getRow() == startRow && board.positionExists(twoStep) && !board.thereIsAPiece(twoStep)) {
                mat[twoStep.getRow()][twoStep.getColumn()] = true;
            }
        }

        // Capture diagonally left
        Position p = new Position(position.getRow() + dir, position.getColumn() - 1);
        if (board.positionExists(p) && isThereOpponentPiece(p)) {
            mat[p.getRow()][p.getColumn()] = true;
        }

        // Capture diagonally right
        p.setValues(position.getRow() + dir, position.getColumn() + 1);
        if (board.positionExists(p) && isThereOpponentPiece(p)) {
            mat[p.getRow()][p.getColumn()] = true;
        }

        // En Passant
        if (position.getRow() == enPassantRow) {
            for (int dc : new int[]{-1, 1}) {
                Position side = new Position(position.getRow(), position.getColumn() + dc);
                if (getBoard().positionExists(side) && isThereOpponentPiece(side) &&
                        getBoard().piece(side) == chessMatch.getEnPassantVulnerable()) {
                    mat[side.getRow() + dir][side.getColumn()] = true;
                }
            }
        }


        return mat;
    }

    @Override
    public String toString() {
        return "P";
    }
}
