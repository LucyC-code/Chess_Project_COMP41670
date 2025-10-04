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
                BoardView.clearScreen();
                BoardView.printGame(match, capturedPieces);
                System.out.println();

                String currentPlayer = match.getCurrentPlayer() == Colour.WHITE ? whitePlayer : blackPlayer;

                System.out.print(currentPlayer + "'s move (e.g., e2 e4) or type q to quit: ");
                String input = sc.nextLine().trim();

                if (input.equalsIgnoreCase("q") || input.equalsIgnoreCase("quit")) {
                    System.out.println("Game ended by player.");
                    break;
                }

                String[] positions = input.split("\\s+");
                if (positions.length != 2) {
                    throw new InputMismatchException("Invalid input. Use format 'e2 e4'.");
                }

                ChessPosition source = BoardView.readChessPosition(positions[0]);
                ChessPosition target = BoardView.readChessPosition(positions[1]);

                boolean[][] validMoves = match.possibleMoves(source);

                ChessPiece captured = match.performChessMove(source, target);
                if (captured != null) capturedPieces.add(captured);


            } catch (ChessException | InputMismatchException e) {
                System.out.println(e.getMessage());
                System.out.print("Press Enter to try again...");
                sc.nextLine();
            }
        }
        BoardView.clearScreen();
        BoardView.printGame(match, capturedPieces);


    }

    }

