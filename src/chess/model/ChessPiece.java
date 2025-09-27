package chess.model;

import boardgame.Board;
import boardgame.Position;
import boardgame.Piece;
import chess.model.Colour;


public abstract class ChessPiece extends Piece {
    private int moveCounter;
    private Colour colour;

    public ChessPiece(Board board, Colour colour) {
        super(board);
        this.colour = colour;
    }

    public int getMoveCounter(){
        return moveCounter;
    }

    public void increaseMoveCounter(){
        moveCounter++;
    }
    public void decreaseMoveCounter(){
        moveCounter--;
    }

    public Colour getColour(){
        return colour;
    }

    protected boolean isThereOpponentPiece(Position position){
        ChessPiece targetPiece = (ChessPiece)getBoard().piece(position);
        return targetPiece != null && targetPiece.getColour() != colour;
    }


}
