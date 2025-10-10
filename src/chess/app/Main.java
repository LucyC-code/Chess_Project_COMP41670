package chess.app;

import chess.core.ChessMatch;
import chess.exceptions.ChessException;
import chess.model.ChessPiece;
import chess.model.ChessPosition;
import chess.model.Colour;
import view.BoardView;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import static view.BoardView.*;

public class Main {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        List<ChessPiece> capturedPieces = new ArrayList<>();

        System.out.println("WELCOME TO THE CONSOLE CHESS GAME\n");

        System.out.print("Enter name for White player: ");
        String whitePlayer = sc.nextLine().trim();

        System.out.print("Enter name for Black player: ");
        String blackPlayer = sc.nextLine().trim();

        ChessMatch match = new ChessMatch();

        while (!match.isCheckMate()) {
            try {
                clearScreen();
                BoardView.printGame(match, capturedPieces);
                System.out.println();

                String currentPlayer = match.getCurrentPlayer() == Colour.WHITE ? whitePlayer : blackPlayer;

                currentPlayer = currentPlayer.substring(0, 1).toUpperCase() + currentPlayer.substring(1);

                System.out.print(currentPlayer + "'s move (e.g., e2 e4 or e2e4) or type q to quit: ");
                String input = sc.nextLine().trim().toLowerCase();

                if (input.equalsIgnoreCase("q") || input.equalsIgnoreCase("quit")) {
                    System.out.println("Game ended by player.");
                    break;
                }

                // Normalize input
                input = input.replaceAll("\\s+", ""); // remove all spaces

                String[] positions;

                // Case 1: no space (e.g., "e2e4")
                if (input.length() == 4) {
                    positions = new String[] { input.substring(0, 2), input.substring(2, 4) };
                }
                // Case 2: had spaces (e.g., "e2 e4")
                else {
                    positions = input.split("\\s+");
                }

                if (input.matches("^[a-h][1-8]$")) {
                    ChessPosition source = new ChessPosition(input.charAt(0), Character.getNumericValue(input.charAt(1)));
                    boolean[][] moves = match.possibleMoves(source);

                    clearScreen();
                    printBoard(match.getPieces());
                    printPossibleMoves(moves); // show e.g. "Possible moves: e3 e4"

                    System.out.println("\nPress Enter to continue...");
                    sc.nextLine();
                    continue;
                }

                // Still invalid - throw error
                if (positions.length != 2) {
                    throw new InputMismatchException("Invalid input. Use format 'e2 e4' or 'e2e4'.");
                }

                ChessPosition source = BoardView.readChessPosition(positions[0]);
                ChessPosition target = BoardView.readChessPosition(positions[1]);

                ChessPiece captured = match.performChessMove(source, target, sc);
                if (captured != null) capturedPieces.add(captured);

                if (match.isCheckMate()) {
                    clearScreen();
                    BoardView.printGame(match, capturedPieces);
                    System.out.println();

                    // Determine winner
                    String winner = (match.getCurrentPlayer() == Colour.WHITE) ? whitePlayer : blackPlayer;
                    winner = winner.substring(0, 1).toUpperCase() + winner.substring(1);

                    System.out.println("CHECKMATE!");
                    System.out.println(winner + " wins!");
                    break; // exit the main game loop
                }



            } catch (ChessException | InputMismatchException e) {
                System.out.println(e.getMessage());
                System.out.print("Press Enter to try again...");
                sc.nextLine();
            }
        }

    }

    }

