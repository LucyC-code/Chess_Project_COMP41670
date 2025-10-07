package view;

import chess.core.ChessMatch;
import chess.model.ChessPiece;
import chess.model.ChessPosition;
import chess.model.Colour;

import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class BoardView {

    private static final Map<String, String[]> UNICODE_MAP = new HashMap<>();
    static {
        UNICODE_MAP.put("K", new String[]{"♔", "♚"});
        UNICODE_MAP.put("Q", new String[]{"♕", "♛"});
        UNICODE_MAP.put("R", new String[]{"♖", "♜"});
        UNICODE_MAP.put("B", new String[]{"♗", "♝"});
        UNICODE_MAP.put("N", new String[]{"♘", "♞"});
        UNICODE_MAP.put("P", new String[]{"♙", "♟"});
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static ChessPosition readChessPosition(String input) {
        if (input == null || !input.matches("^[a-hA-H][1-8]$")) {
            throw new InputMismatchException("Invalid input. Use positions between a1 and h8.");
        }
        return new ChessPosition(
                Character.toLowerCase(input.charAt(0)),
                input.charAt(1) - '0'
        );
    }

    public static void printGame(ChessMatch match, List<ChessPiece> captured) {
        printBoard(match.getPieces());
        System.out.println();
        printCapturedPieces(captured);

        if (match.isCheckMate()) {
            System.out.println("CHECKMATE.");
            System.out.println("Winner: " + match.getCurrentPlayer());
        } else {
            System.out.println("Waiting for Player: " + match.getCurrentPlayer());
            if (match.isCheck()) System.out.println("Check.");
        }
    }

    // 24-bit ANSI escape codes
    private static final String RESET = "\u001B[0m";

    // Square colours (RGB with 24-bit ANSI)
    private static final String LIGHT_SQUARE = "\u001B[48;2;240;217;181m"; // light beige
    private static final String DARK_SQUARE  = "\u001B[48;2;181;136;99m";  // brown

    public static void printBoard(ChessPiece[][] pieces) {
        int squareHeight = 1; // taller squares
        int squareWidth = 4;  // wider squares

        for (int i = 0; i < pieces.length; i++) {
            for (int line = 0; line < squareHeight; line++) {

                if (line == squareHeight / 2) {
                    System.out.print((8 - i) + " "); // print rank label in middle row
                } else {
                    System.out.print("  "); // align left side
                }

                for (int j = 0; j < pieces[i].length; j++) {
                    ChessPiece piece = pieces[i][j];
                    String bg = ((i + j) % 2 == 0) ? LIGHT_SQUARE : DARK_SQUARE;

                    // compute padding for horizontal centering
                    int padding = squareWidth / 2 - 1;
                    String pad = " ".repeat(padding);

                    if (line == squareHeight / 2 && piece != null) {
                        // Center piece symbol in middle line
                        String symbol = toStyledUnicode(piece);
                        System.out.print(bg + pad + symbol + bg +  pad + RESET);
                    } else {
                        // Empty fill line
                        System.out.print(bg + " ".repeat(squareWidth) + RESET);
                    }
                }
                System.out.println();
            }
        }

        // print file letters (a–h)
        System.out.print(" ");
        for (char c = 'a'; c <= 'h'; c++) {
            System.out.print(" ".repeat(squareWidth / 4) + c + " ".repeat(squareWidth / 2));
        }
        System.out.println();
    }


    private static String toUnicode(String piece, Colour colour) {
        String[] symbols = UNICODE_MAP.getOrDefault(piece.toUpperCase(), new String[]{"?", "?"});
        return "\u001B[1m" + (colour == Colour.WHITE ? symbols[0] : symbols[1]) + "\u001B[0m";
    }

    private static String toStyledUnicode(ChessPiece piece) {
        String[] symbols = UNICODE_MAP.getOrDefault(piece.toString().toUpperCase(), new String[]{"?", "?"});
        String symbol = (piece.getColour() == Colour.WHITE) ? symbols[0] : symbols[1];

        if (piece.getColour() == Colour.WHITE) {
            return "\u001B[1;30m" + symbol + "\u001B[0m";
        } else {
            return "\u001B[1;30m" + symbol + "\u001B[0m";
        }
    }

    public static void printPossibleMoves(boolean[][] possibleMoves) {
        System.out.print("\nPossible moves: ");
        for (int i = 0; i < possibleMoves.length; i++) {
            for (int j = 0; j < possibleMoves[i].length; j++) {
                if (possibleMoves[i][j]) {
                    char column = (char) ('a' + j);
                    int row = 8 - i; // convert from array index to chessboard row
                    System.out.print(column + "" + row + " ");
                }
            }
        }
        System.out.println();
    }


    public static void printCapturedPieces(List<ChessPiece> pieces) {
        String white = pieces.stream()
                .filter(p -> p.getColour() == Colour.WHITE)
                .map(p -> toUnicode(p.toString(), p.getColour()))
                .collect(Collectors.joining(" "));

        String black = pieces.stream()
                .filter(p -> p.getColour() == Colour.BLACK)
                .map(p -> toUnicode(p.toString(), p.getColour()))
                .collect(Collectors.joining(" "));


        System.out.println("Captured Pieces:");
        System.out.println("White: " + white);
        System.out.println("Black: " + black);
    }
}
