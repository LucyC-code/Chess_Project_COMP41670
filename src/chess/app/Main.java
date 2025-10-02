package chess.app;

import chess.core.ChessMatch;
import chess.model.ChessPiece;
import chess.model.Colour;
import view.BoardView;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.println("WELCOME TO THE CONSOLE CHESS GAME\n");

        System.out.print("Enter name for White player: ");
        String whitePlayer = sc.nextLine().trim();

        System.out.print("Enter name for Black player: ");
        String blackPlayer = sc.nextLine().trim();

        ChessMatch match = new ChessMatch();

        while (!match.isCheckMate()) {
            try {
                ChessPiece[][] pieces = match.getPieces();
                BoardView.printSimpleBoard(match.getPieces());

                String currentPlayer = match.getCurrentPlayer() == Colour.WHITE ? whitePlayer : blackPlayer;
                System.out.print(currentPlayer + "'s move (e.g., e2 e4) or type q to quit: ");
                String input = sc.nextLine().trim();

                if (input.equalsIgnoreCase("q") || input.equalsIgnoreCase("quit")) {
                    System.out.println("Game ended by player.");
                    break;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }


    }

    }

