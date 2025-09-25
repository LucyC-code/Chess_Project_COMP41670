package boardgame;

import chess.exceptions.BoardException;

public class Board {
    private Integer rows;
    private Integer columns;
    private Piece[][] pieces;

    public Board(int rows, int columns) {
        if (rows < 1 || columns < 1) {
            throw new BoardException("Error in creating the board" + "There must be at least one row and one column.");

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
            throw new BoardException("Position is outside of the board!");
        }
        Piece existing = piece(position);
        if (existing == null) {
            return null;
        }
        pieces[position.getRow()][position.getColumn()] = null;
        existing.position = null;
        return existing;

    }
    private boolean positionExists(int row, int column){
        return row >= 0 && row < rows && column >= 0 && column < columns;
    }

    public boolean positionExists(Position position) {
        if (position == null) {
            return false; // or throw new IllegalArgumentException("Position cannot be null");
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