package boardgame;

import chess.model.Colour;

public abstract class Piece {
    protected Position position;
    private Board board;
    private Colour colour;

    public Piece(Board board, Colour colour) {
        this.board = board;
        this.colour = colour;
        position = null;
    }

    public Board getBoard() {
        return board;
    }

    public Colour getColour() {
        return colour;
    }

    public Position getPosition() {
        return position;
    }

    public abstract boolean[][] possibleMoves();

    public boolean possibleMove(Position position) {
        return possibleMoves()[position.getRow()][position.getColumn()];
    }

    public boolean isThereAnyPossibleMove() {
        boolean[][] mat = possibleMoves();
        for (int row = 0; row < mat.length; row++) {
            for (int col = 0; col < mat[row].length; col++)
                if (mat[row][col]) {
                    return true;
                }
        }
        return false;
    }
}
