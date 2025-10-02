package chess.core;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.exceptions.ChessException;
import chess.model.ChessPiece;
import chess.model.ChessPosition;
import chess.model.Colour;
import chess.pieces.*;

public class ChessMatch {

    private Board board;
    private Colour currentPlayer;
    private Integer turn;
    private boolean checkMate;

    public ChessMatch() {
        this.board = new Board(8, 8);
        initialSetup();
    }
    public Integer getTurn() {
        return turn;
    }

    public boolean isCheckMate() {
        return checkMate;
    }

    public Colour getCurrentPlayer() {
        return currentPlayer;
    }

    public ChessPiece[][] getPieces() {
        ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
        for (int i = 0; i < board.getRows(); i++) {
            for (int j = 0; j < board.getColumns(); j++) {
                mat[i][j] = (ChessPiece) board.piece(i, j);
            }
        }
        return mat;
    }

    public ChessPiece performChessMove(ChessPosition sourcePos, ChessPosition targetPos) {
        Position source = sourcePos.toPosition();
        Position target = targetPos.toPosition();

        checkInitialPosition(source);
        checkTargetPosition(source, target);

        ChessPiece captured = (ChessPiece) makeMove(source, target);
        ChessPiece moved = (ChessPiece) board.piece(target);


        return captured;
    }

    private Piece makeMove(Position source, Position target) {
        ChessPiece piece = (ChessPiece) board.removePiece(source);
        piece.increaseMoveCounter();

        Piece captured = board.removePiece(target);
        board.placePiece(piece, target);
        return captured;
    }



    private void placeNewPiece(char column, int row, ChessPiece piece) {
        board.placePiece(piece, new ChessPosition(column, row).toPosition());
    }

    private void checkInitialPosition(Position position) {
        Piece piece = board.piece(position);

        if (piece == null)
            throw new ChessException("No piece at this position.");
        if (((ChessPiece) piece).getColour() != currentPlayer)
            throw new ChessException("That piece is not yours.");
        if (!piece.isThereAnyPossibleMove())
            throw new ChessException("No legal moves for this piece.");
    }

    private void checkTargetPosition(Position source, Position target) {
        Piece piece = board.piece(source);
        if (piece == null || !piece.possibleMove(target))
            throw new ChessException("Invalid target position for this piece.");
    }



    private void initialSetup() {
        // White pieces
        placeNewPiece('a', 1, new Rook(board, Colour.WHITE));
        placeNewPiece('b', 1, new Knight(board, Colour.WHITE));
        placeNewPiece('c', 1, new Bishop(board, Colour.WHITE));
        placeNewPiece('d', 1, new Queen(board, Colour.WHITE));
        placeNewPiece('e', 1, new King(board, Colour.WHITE, this));
        placeNewPiece('f', 1, new Bishop(board, Colour.WHITE));
        placeNewPiece('g', 1, new Knight(board, Colour.WHITE));
        placeNewPiece('h', 1, new Rook(board, Colour.WHITE));

        for (char c = 'a'; c <= 'h'; c++) {
            placeNewPiece(c, 2, new Pawn(board, Colour.WHITE, this));
        }

// Black pieces
        placeNewPiece('a', 8, new Rook(board, Colour.BLACK));
        placeNewPiece('b', 8, new Knight(board, Colour.BLACK));
        placeNewPiece('c', 8, new Bishop(board, Colour.BLACK));
        placeNewPiece('d', 8, new Queen(board, Colour.BLACK));
        placeNewPiece('e', 8, new King(board, Colour.BLACK, this));
        placeNewPiece('f', 8, new Bishop(board, Colour.BLACK));
        placeNewPiece('g', 8, new Knight(board, Colour.BLACK));
        placeNewPiece('h', 8, new Rook(board, Colour.BLACK));

        for (char c = 'a'; c <= 'h'; c++) {
            placeNewPiece(c, 7, new Pawn(board, Colour.BLACK, this));
        }


    }

}

