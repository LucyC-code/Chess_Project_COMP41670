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

/**
 * Main class managing the chess match.
 * Handles turns, moves, checks, and checkmate logic.
 */
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

    /** @return current turn number */
    public Integer getTurn() {
        return turn;
    }

    /** @return true if the current player is in check */
    public boolean isCheck() {
        return check;
    }

    /** @return true if the game is over (checkmate) */
    public boolean isCheckMate() {
        return checkMate;
    }

    /** @return the colour of the player to move */
    public Colour getCurrentPlayer() {
        return currentPlayer;
    }

    /** @return the pawn currently vulnerable to en passant */
    public ChessPiece getEnPassantVulnerable() {
        return enPassantVulnerable;
    }

    /** Initializes the chess match with the standard setup. */
    public ChessMatch() {
        this.board = new Board(8, 8);
        turn = 1;
        currentPlayer = Colour.WHITE;
        initialSetup();
    }

    /**
     * @return matrix representing all pieces on the board
     */
    public ChessPiece[][] getPieces() {
        ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
        for (int i = 0; i < board.getRows(); i++) {
            for (int j = 0; j < board.getColumns(); j++) {
                mat[i][j] = (ChessPiece) board.piece(i, j);
            }
        }
        return mat;
    }

    /**
     * Gets possible moves for a piece at a given position.
     */
    public boolean[][] possibleMoves(ChessPosition sourcePosition) {
        checkInitialPosition(sourcePosition.toPosition());
        return board.piece(sourcePosition.toPosition()).possibleMoves();
    }

    /**
     * Executes a chess move if valid and updates the game state.
     */
    public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition, Scanner sc) {
        Position source = sourcePosition.toPosition();
        Position target = targetPosition.toPosition();

        checkInitialPosition(source);
        checkTargetPosition(source, target);

        Piece capturedPiece = makeMove(source, target);
        ChessPiece movedPiece = (ChessPiece) board.piece(target);

        if (testCheck(currentPlayer)) {
            undoMove(source, target, capturedPiece);
            throw new ChessException("Put yourself in check");
        }

        // Promotion
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

        check = testCheck(opponent(currentPlayer));

        if (testCheckMate(opponent(currentPlayer))) {
            checkMate = true;
        } else {
            nextTurn();
        }

        // En passant tracking
        if (movedPiece instanceof Pawn && Math.abs(source.getRow() - target.getRow()) == 2) {
            enPassantVulnerable = movedPiece;
        } else {
            enPassantVulnerable = null;
        }

        return (ChessPiece) capturedPiece;
    }

    /**
     * Replaces a promoted pawn with the selected piece type.
     */
    public ChessPiece replacePromotedPiece(String type) {
        if (promoted == null) {
            throw new IllegalStateException("There is no piece to promote.");
        }

        if (!type.matches("[QRBN]")) {
            return promoted;
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
            default -> newPiece = new Queen(board, colour);
        }

        board.placePiece(newPiece, pos);
        piecesOnTheBoard.remove(promoted);
        piecesOnTheBoard.add(newPiece);

        return (ChessPiece) newPiece;
    }

    /**
     * Moves a piece and handles captures and en passant.
     */
    private Piece makeMove(Position source, Position target) {
        ChessPiece p = (ChessPiece) board.removePiece(source);
        p.increaseMoveCounter();

        Piece capturedPiece = board.removePiece(target);

        if (capturedPiece instanceof King) {
            board.placePiece(capturedPiece, target);
            board.placePiece(p, source);
            throw new IllegalStateException("You cannot capture the King! Checkmate ends the game.");
        }

        board.placePiece(p, target);

        // Castling move (King-side and Queen-side)
        if (p instanceof King) {
            // Kingside castling
            if (target.getColumn() == source.getColumn() + 2) {
                Position sourceR = new Position(source.getRow(), source.getColumn() + 3);
                Position targetR = new Position(source.getRow(), source.getColumn() + 1);
                Piece rook = board.removePiece(sourceR);
                board.placePiece(rook, targetR);
                ((ChessPiece) rook).increaseMoveCounter();
            }

            // Queenside castling
            else if (target.getColumn() == source.getColumn() - 2) {
                Position sourceR = new Position(source.getRow(), source.getColumn() - 4);
                Position targetR = new Position(source.getRow(), source.getColumn() - 1);
                Piece rook = board.removePiece(sourceR);
                board.placePiece(rook, targetR);
                ((ChessPiece) rook).increaseMoveCounter();
            }
        }


        if (p instanceof Pawn) {
            boolean movedDiagonally = source.getColumn() != target.getColumn();
            boolean noDirectCapture = capturedPiece == null;

            if (movedDiagonally && noDirectCapture) {
                int capturedRow = (p.getColour() == Colour.WHITE)
                        ? target.getRow() + 1
                        : target.getRow() - 1;

                Position pawnPosition = new Position(capturedRow, target.getColumn());
                capturedPiece = board.removePiece(pawnPosition);

                if (capturedPiece != null) {
                    capturedPieces.add(capturedPiece);
                    piecesOnTheBoard.remove(capturedPiece);
                }
            }
        }

        if (capturedPiece != null) {
            capturedPieces.add(capturedPiece);
            piecesOnTheBoard.remove(capturedPiece);
        }

        return capturedPiece;
    }

    /** Places a piece at the given position. */
    private void placeNewPiece(char column, int row, Piece piece) {
        board.placePiece(piece, new ChessPosition(column, row).toPosition());
    }

    /** Checks if a position is valid for moving a piece. */
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

    /** Validates the target position for a move. */
    private void checkTargetPosition(Position source, Position target) {
        Piece piece = board.piece(source);
        if (piece == null || !piece.possibleMove(target))
            throw new ChessException("Invalid target position for this piece.");
    }

    /** @return the opposite colour */
    private Colour opponent(Colour colour) {
        return (colour == Colour.WHITE) ? Colour.BLACK : Colour.WHITE;
    }

    /** Checks if the given colour's king is in check. */
    private boolean testCheck(Colour colour) {
        Position kingPos = king(colour).getChessPosition().toPosition();
        for (Piece p : piecesOnTheBoard) {
            ChessPiece cp = (ChessPiece) p;
            if (cp.getColour() == opponent(colour)) {
                boolean[][] moves = p.possibleMoves();
                if (moves[kingPos.getRow()][kingPos.getColumn()]) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Tests whether the given colour is in checkmate. */
    private boolean testCheckMate(Colour colour) {
        if (!testCheck(colour)) return false;

        List<Piece> playerPieces = new ArrayList<>(piecesOnTheBoard);
        for (Piece p : playerPieces) {
            ChessPiece piece = (ChessPiece) p;
            if (piece.getColour() != colour) continue;

            boolean[][] possibleMoves = piece.possibleMoves();
            for (int i = 0; i < board.getRows(); i++) {
                for (int j = 0; j < board.getColumns(); j++) {
                    if (!possibleMoves[i][j]) continue;

                    Position source = piece.getChessPosition().toPosition();
                    Position target = new Position(i, j);

                    Piece captured = makeMove(source, target);
                    boolean testCheck = testCheck(colour);
                    undoMove(source, target, captured);

                    if (!testCheck) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /** Finds the king of the given colour. */
    private King king(Colour colour) {
        return (King) piecesOnTheBoard.stream()
                .filter(p -> p.getColour() == colour && p instanceof King)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No " + colour + " king on the board!"));
    }

    /** Reverts a move after testing it (used for check/checkmate validation). */
    private void undoMove(Position source, Position target, Piece captured) {
        ChessPiece p = (ChessPiece) board.removePiece(target);
        p.decreaseMoveCounter();
        board.placePiece(p, source);

        // Undo castling (King-side and Queen-side)
        if (p instanceof King) {
            // Kingside
            if (target.getColumn() == source.getColumn() + 2) {
                Position sourceR = new Position(source.getRow(), source.getColumn() + 3);
                Position targetR = new Position(source.getRow(), source.getColumn() + 1);
                Piece rook = board.removePiece(targetR);
                board.placePiece(rook, sourceR);
                ((ChessPiece) rook).decreaseMoveCounter();
            }
            // Queenside
            else if (target.getColumn() == source.getColumn() - 2) {
                Position sourceR = new Position(source.getRow(), source.getColumn() - 4);
                Position targetR = new Position(source.getRow(), source.getColumn() - 1);
                Piece rook = board.removePiece(targetR);
                board.placePiece(rook, sourceR);
                ((ChessPiece) rook).decreaseMoveCounter();
            }
        }


        if (captured != null) {
            board.placePiece(captured, target);
            capturedPieces.remove(captured);
            piecesOnTheBoard.add(captured);
        }
    }

    /** Switches the turn to the next player. */
    private void nextTurn() {
        turn++;
        currentPlayer = (currentPlayer == Colour.WHITE) ? Colour.BLACK : Colour.WHITE;
    }

    /** Places a new chess piece and adds it to the list. */
    private void placeNewPiece(char column, int row, ChessPiece piece) {
        board.placePiece(piece, new ChessPosition(column, row).toPosition());
        piecesOnTheBoard.add(piece);
    }

    /** Sets up the standard initial chessboard layout. */
    private void initialSetup() {
        // White
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

        // Black
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
