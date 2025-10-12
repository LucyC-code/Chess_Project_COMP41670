package boardgame;

import chess.exceptions.BoardException;

/**
 * Represents a chess board.
 * A board consists of a fixed number of rows and columns and
 * holds pieces in a two-dimensional matrix.
 */


public class Board {
    private Integer rows;
    private Integer columns;
    private Piece[][] pieces;

    /**
     * Constructs a new board with the given dimensions.
     *
     * @param rows    number of rows on the board (must be >= 1)
     * @param columns number of columns on the board (must be >= 1)
     * @throws BoardException if rows or columns are less than 1
     */

    public Board(int rows, int columns) {
        if (rows < 1 || columns < 1) {
            throw new BoardException("Error in creating the board - There must be at least one row and one column.");

        }
        this.rows = rows;
        this.columns = columns;
        pieces = new Piece[rows][columns];

    }

    public Integer getRows() {
        return rows;
    }

    public Integer getColumns() {
        return columns;
    }


    public Piece piece(Integer row, Integer column) {
        return piece(new Position(row, column));
    }

    public Piece piece(Position position) {
        if (!positionExists(position)) {
            throw new BoardException("Position is outside of the board!");
        }
        return pieces[position.getRow()][position.getColumn()];
    }

    public void placePiece(Piece piece, Position position) {
        if (!positionExists(position)) {
            throw new BoardException("Position is outside of the board!");
        }
        if (thereIsAPiece(position)) {
            throw new BoardException("There is already a piece at " + position);
        }
        pieces[position.getRow()][position.getColumn()] = piece;
        piece.position = position;
    }

    public Piece removePiece(Position position) {
        if (!positionExists(position)) {
            throw new IllegalArgumentException("Position not on the board");
        }
        if (piece(position) == null) {
            return null; // nothing to remove
        }
        Piece removed = pieces[position.getRow()][position.getColumn()];
        pieces[position.getRow()][position.getColumn()] = null; // clear square
        removed.position = null;
        return removed; // return the exact same object reference
    }


    // checks the position is within the bounds of the board
    private boolean positionExists(int row, int column){
        return row >= 0 && row < rows && column >= 0 && column < columns;
    }

    public boolean positionExists(Position position) {
        if (position == null) {
            return false;
        }
        return positionExists(position.getRow(), position.getColumn());
    }

    public boolean thereIsAPiece(Position position) {
        if (!positionExists(position)) {
            throw new BoardException("Position is outside of the board!");
        }
        return piece(position) != null;
    }

}