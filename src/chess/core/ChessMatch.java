package chess.core;

import boardgame.*;
import chess.exceptions.ChessException;
import chess.model.ChessPiece;
import chess.model.ChessPosition;
import chess.model.Colour;
import chess.pieces.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ChessMatch {

    private Board board;
    private Colour currentPlayer;
    private Integer turn;
    private boolean check;
    private boolean checkMate;
    private ChessPiece promoted;
    private ChessPiece enPassantVulnerable;


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

    public ChessPiece getEnPassantVulnerable() {
        return enPassantVulnerable;
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

    public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition, Scanner sc) {
        Position source = sourcePosition.toPosition();
        Position target = targetPosition.toPosition();

        checkInitialPosition(source);
        checkTargetPosition(source, target);

        Piece capturedPiece = makeMove(source, target);
        ChessPiece movedPiece = (ChessPiece) board.piece(target);

        if (testCheck(currentPlayer)) {
            undoMove(source, target, capturedPiece);
            System.out.println("Invalid move — you must move out of check!");
            return null;
        }


        // special move promotion
        promoted = null;
        if (movedPiece instanceof Pawn pawn &&
                ((pawn.getColour() == Colour.WHITE && target.getRow() == 0) ||
                        (pawn.getColour() == Colour.BLACK && target.getRow() == 7))) {


            promoted = movedPiece;

            String type;
            do {
                System.out.print("Promote pawn to type *Q/R/B/N* ~ (Queen/Rook/Bishop/Knight): ");
                type = sc.nextLine().trim().toUpperCase();
            } while (!type.matches("[QRBN]"));

            promoted = replacePromotedPiece(type);
        }

        check = (testCheck(opponent(currentPlayer))) ? true : false;

        if (testCheck(opponent(getCurrentPlayer()))) {
            System.out.println(opponent(getCurrentPlayer()) + " is in check!");
            if (testCheckMate(opponent(getCurrentPlayer()))) {
                System.out.println("CHECKMATE! " + getCurrentPlayer() + " wins!");
                checkMate = true;
                return (ChessPiece) capturedPiece;
            }
        }

        if (testCheckMate(opponent(currentPlayer))) {
            checkMate = true;
        } else {
            nextTurn();
        }

        // special move en passant vulnerability
        if (movedPiece instanceof Pawn && Math.abs(source.getRow() - target.getRow()) == 2) {
            enPassantVulnerable = movedPiece;
        } else {
            enPassantVulnerable = null;
        }

        return (ChessPiece) capturedPiece;
    }

    public ChessPiece replacePromotedPiece(String type) {
        if (promoted == null) {
            throw new IllegalStateException("There is no piece to promote.");
        }

        if (!type.matches("[QRBN]")) {
            return promoted; // ignore invalid input, keep as queen by default
        }

        Position pos = promoted.getChessPosition().toPosition();
        Colour colour = promoted.getColour();

        board.removePiece(pos);
        piecesOnTheBoard.remove(promoted);

        Piece newPiece;
        switch (type.toUpperCase()) {
            case "B" -> newPiece = new Bishop(board, colour);
            case "N" -> newPiece = new Knight(board, colour);
            case "R" -> newPiece = new Rook(board, colour);
            default -> newPiece = new Queen(board, colour); // default to Queen
        }

        board.placePiece(newPiece, pos);
        piecesOnTheBoard.remove(promoted);
        piecesOnTheBoard.add(newPiece);

        return (ChessPiece) newPiece;
    }


    private Piece makeMove(Position source, Position target) {
        ChessPiece p = (ChessPiece) board.removePiece(source);
        p.increaseMoveCounter();

        // Try to capture whatever is on the target square first
        Piece capturedPiece = board.removePiece(target);

        if (capturedPiece instanceof King) {
            // Undo the removal immediately
            board.placePiece(capturedPiece, target);
            board.placePiece(p, source);
            throw new IllegalStateException("You cannot capture the King! Checkmate ends the game.");
        }

        // Place the moving piece
        board.placePiece(p, target);

        if (p instanceof Pawn) {
            boolean movedDiagonally = source.getColumn() != target.getColumn();
            boolean noDirectCapture = capturedPiece == null;

            if (movedDiagonally && noDirectCapture) {
                int capturedRow = (p.getColour() == Colour.WHITE)
                        ? target.getRow() + 1   // White captures pawn below
                        : target.getRow() - 1;  // Black captures pawn above

                Position pawnPosition = new Position(capturedRow, target.getColumn());
                capturedPiece = board.removePiece(pawnPosition);

                if (capturedPiece != null) {
                    capturedPieces.add(capturedPiece);
                    piecesOnTheBoard.remove(capturedPiece);
                }
            }
        }
        // Update captured piece list if any
        if (capturedPiece != null) {
            capturedPieces.add(capturedPiece);
            piecesOnTheBoard.remove(capturedPiece);
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

    private boolean testCheck(Colour colour) {
        Position kingPos = king(colour).getChessPosition().toPosition();

        return piecesOnTheBoard.stream()
                .filter(p -> ((ChessPiece) p).getColour() == opponent(colour))
                .map(Piece::possibleMoves)
                .anyMatch(moves -> moves[kingPos.getRow()][kingPos.getColumn()]);
    }

    private boolean testCheckMate(Colour colour) {
        // If the player is NOT in check → not checkmate
        if (!testCheck(colour)) {
            return false;
        }

        //  Copy list to avoid ConcurrentModificationException
        List<Piece> playerPieces = new ArrayList<>(piecesOnTheBoard);

        // Try all possible moves for this colour
        for (Piece p : playerPieces) {
            ChessPiece piece = (ChessPiece) p;

            // Skip pieces of the other colour
            if (piece.getColour() != colour) continue;

            boolean[][] possibleMoves = piece.possibleMoves();

            for (int i = 0; i < board.getRows(); i++) {
                for (int j = 0; j < board.getColumns(); j++) {
                    if (!possibleMoves[i][j]) continue;

                    Position source = piece.getChessPosition().toPosition();
                    Position target = new Position(i, j);

                    Piece captured = makeMove(source, target);
                    boolean stillInCheck = testCheck(colour);
                    undoMove(source, target, captured);

                    // If there's *any* legal move that gets out of check → not checkmate
                    if (!stillInCheck) {
                        return false;
                    }
                }
            }
        }

        // No legal move avoids check → checkmate
        return true;
    }

    private King king(Colour colour) {
        return (King) piecesOnTheBoard.stream()
                .filter(p -> p.getColour() == colour && p instanceof King)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No " + colour + " king on the board!"));
    }

    private void undoMove(Position source, Position target, Piece captured) {
        ChessPiece p = (ChessPiece) board.removePiece(target);
        p.decreaseMoveCounter();
        board.placePiece(p, source);

        if (captured != null) {
            board.placePiece(captured, target);
            capturedPieces.remove(captured);
            piecesOnTheBoard.add(captured);
        }
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

