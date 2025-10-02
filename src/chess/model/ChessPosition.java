package chess.model;

import boardgame.Position;
import chess.exceptions.ChessException;

public class ChessPosition {
    private char column;
    private int row;

    public ChessPosition(char column, int row) {
        if (column < 'a' || column > 'h' || row < 1 || row > 8) {
            throw new ChessException("Invalid position: use values from a1 to h8.");
        }
        this.column = column;
        this.row = row;
    }

    public Position toPosition() {
        int boardRow = 8 - row;              // row 1 → index 7, row 8 → index 0
        int boardCol = column - 'a';         // 'a' → 0, 'h' → 7
        return new Position(boardRow, boardCol);
    }

    @Override
    public String toString() {
        return "" + column + row;
    }
}
