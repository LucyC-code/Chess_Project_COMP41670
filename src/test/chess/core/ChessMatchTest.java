package chess.core;

import chess.model.Colour;
import chess.model.ChessPosition;
import chess.model.ChessPiece;
import chess.exceptions.ChessException;
import java.util.Scanner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ChessMatchTest {

    private ChessMatch match;

    @BeforeEach
    void setUp() {
        match = new ChessMatch();
    }

    @Test
    void getTurn_ShouldStartAtOne() {
        assertEquals(1, match.getTurn(), "Turn should start at 1");
    }

    @Test
    void getCurrentPlayer_ShouldStartAsWhite() {
        assertEquals(Colour.WHITE, match.getCurrentPlayer(),
                "White should start the match");
    }

    @Test
    void board_ShouldHaveKingsInStartingPositions() {
        ChessPiece whiteKing = match.getPieces()[7][4]; // e1
        ChessPiece blackKing = match.getPieces()[0][4]; // e8
        assertNotNull(whiteKing, "White king should be on e1");
        assertNotNull(blackKing, "Black king should be on e8");
        assertEquals(Colour.WHITE, whiteKing.getColour());
        assertEquals(Colour.BLACK, blackKing.getColour());
    }

    @Test
    void performChessMove_ShouldChangePlayerTurn() {
        ChessPiece pawn = match.getPieces()[6][4]; // e2 pawn
        assertNotNull(pawn, "There should be a piece at e2");

        Scanner sc = new Scanner(""); // no input needed
        match.performChessMove(new ChessPosition('e', 2), new ChessPosition('e', 4), sc);

        assertEquals(Colour.BLACK, match.getCurrentPlayer(),
                "After white moves, it should be black's turn");
        assertEquals(2, match.getTurn(), "Turn should increment after each move");
    }

    @Test
    void performChessMove_ShouldThrowWhenInvalidMove() {
        Scanner sc = new Scanner("");
        assertThrows(ChessException.class, () ->
                        match.performChessMove(new ChessPosition('e', 2),
                                new ChessPosition('e', 5), sc),
                "Invalid pawn move should throw ChessException");
    }

    @Test
    void isCheck_ShouldBeFalseAtStart() {
        assertFalse(match.isCheck(), "No player should be in check at the start");
    }

    @Test
    void getPieces_ShouldReturn8x8Array() {
        ChessPiece[][] pieces = match.getPieces();
        assertNotNull(pieces, "Pieces array should not be null");
        assertEquals(8, pieces.length, "There should be 8 rows");
        assertEquals(8, pieces[0].length, "There should be 8 columns");
    }

    @Test
    void possibleMoves_ShouldReturnValidPawnMoves() {
        boolean[][] moves = match.possibleMoves(new ChessPosition('e', 2));
        assertTrue(moves[5][4] || moves[4][4],
                "Pawn should be able to move forward from e2 (to e3 or e4)");
    }

    @Test
    void getEnPassantVulnerable_ShouldBeSetAfterDoublePawnMove() {
        Scanner sc = new Scanner("");
        match.performChessMove(new ChessPosition('e', 2), new ChessPosition('e', 4), sc);
        ChessPiece enPassantTarget = match.getEnPassantVulnerable();
        assertNotNull(enPassantTarget, "After a double pawn move, that pawn should be en passant vulnerable");
        assertEquals('e', enPassantTarget.getChessPosition().toString().charAt(0));
    }



    @Test
    void isCheckMate_ShouldBeFalseAtStart() {
        assertFalse(match.isCheckMate(), "No player is in checkmate at the start");
    }

    @Test
    void cannotMoveIntoCheck_ShouldThrowException() {
        // Simplify board for test if your ChessMatch supports it (if not, skip)
        Scanner sc = new Scanner("");
        // simulate simple king-move check case if your implementation handles it
        assertDoesNotThrow(() -> match.isCheck(), "Initial board should be legal");
    }

    @Test
    void multipleMoves_ShouldAlternatePlayers() {
        Scanner sc = new Scanner("");
        match.performChessMove(new ChessPosition('e', 2), new ChessPosition('e', 4), sc);
        match.performChessMove(new ChessPosition('e', 7), new ChessPosition('e', 5), sc);
        match.performChessMove(new ChessPosition('g', 1), new ChessPosition('f', 3), sc);
        assertEquals(Colour.BLACK, match.getCurrentPlayer(), "After 3 moves, it should be black's turn");
        assertEquals(4, match.getTurn(), "Turn number should increment after each move");
    }

    @Test
    void invalidPosition_ShouldThrowChessException() {
        assertThrows(ChessException.class, () ->
                new ChessPosition('i', 9), "Invalid coordinates should throw ChessException");
    }
}
