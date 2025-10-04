package chess.core;

import boardgame.*;
import chess.exceptions.ChessException;
import chess.model.ChessPiece;
import chess.model.ChessPosition;
import chess.model.Colour;
import chess.pieces.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChessMatch {

    private Board board;
    private Colour currentPlayer;
    private Integer turn;
    private boolean check;
    private boolean checkMate;

    private List<Piece> piecesOnTheBoard = new ArrayList<>();
    private List<Piece> capturedPieces = new ArrayList<>();

    public Integer getTurn() {
        return turn;
    }

    public boolean isCheck() {
        return check;
    }

    public boolean isCheckMate() {
        return checkMate;
    }

    public Colour getCurrentPlayer() {
        return currentPlayer;
    }

    public ChessMatch() {
        this.board = new Board(8, 8);
        turn = 1;
        currentPlayer = Colour.WHITE;
        initialSetup();
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

    public boolean[][] possibleMoves(ChessPosition sourcePosition) {
        checkInitialPosition(sourcePosition.toPosition());
        return board.piece(sourcePosition.toPosition()).possibleMoves();
    }

    public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
        Position source = sourcePosition.toPosition();
        Position target = targetPosition.toPosition();
        checkInitialPosition(source);
        checkTargetPosition(source, target);
        Piece capturedPiece = makeMove(source, target);
        nextTurn();
        return (ChessPiece) capturedPiece;
    }

    private Piece makeMove(Position source, Position target) {
        ChessPiece p = (ChessPiece) board.removePiece(source);
        p.increaseMoveCounter();
        Piece capturedPiece = board.removePiece(target);
        board.placePiece(p, target);
        if (capturedPiece != null) {
            piecesOnTheBoard.remove(capturedPiece);   // same logic
            capturedPieces.add(capturedPiece);
        }
        return capturedPiece;
    }



    private void placeNewPiece(char column, int row, Piece piece) {
        board.placePiece(piece, new ChessPosition(column, row).toPosition());
    }

    private void checkInitialPosition(Position position) {
        if (!board.thereIsAPiece(position)) {
            throw new ChessException("No piece has an origin in this position.");
        }
        if (currentPlayer != board.piece(position).getColour()) {
            throw new ChessException("The piece selected is not your colour.");
        }

        if (!board.piece(position).isThereAnyPossibleMove()) {
            throw new ChessException("There are no possible moves for the selected piece.");
        }
    }





    private void checkTargetPosition(Position source, Position target) {
        Piece piece = board.piece(source);
        if (piece == null || !piece.possibleMove(target))
            throw new ChessException("Invalid target position for this piece.");
    }

    private Colour opponent(Colour colour) {
        return (colour == Colour.WHITE) ? Colour.BLACK : Colour.WHITE;
    }

    private void nextTurn() {
        turn++;
        currentPlayer = (currentPlayer == Colour.WHITE) ? Colour.BLACK : Colour.WHITE;
    }

    private void placeNewPiece(char column, int row, ChessPiece piece) {
        board.placePiece(piece, new ChessPosition(column, row).toPosition());
        piecesOnTheBoard.add(piece);
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

