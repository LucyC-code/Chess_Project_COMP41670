package chess.model;

import boardgame.Position;
import chess.exceptions.ChessException;


public class ChessPosition {
    private final int row;
    private final char column;

    public ChessPosition(char column, int row) {
        if (column < 'a' || column > 'h' || row < 1 || row > 7)
            throw new ChessException("Invalid position: use values from a1 to h8.");

        this.row = row;
        this.column = column;

    }

    public int getRow() {
        return row;
    }

    public char getColumn() {
        return column;
    }

    protected Position toPosition() {
        return new Position(8 - row, column - 'a');
    }

    protected static ChessPosition fromPosition(Position position) {
        return new ChessPosition((char) ('a' + position.getColumn()),8 - position.getRow());
    }

    @Override
    public String toString() {
        return "" + column + row;
    }


}
