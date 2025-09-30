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

    protected void sweepDirection(int[] dir, boolean[][] mat, Position position) {
        Position pos = new Position(position.getRow() + dir[0], position.getColumn() + dir[1]);

        while (getBoard().positionExists(pos) && !getBoard().thereIsAPiece(pos)) {
            mat[pos.getRow()][pos.getColumn()] = true;
            pos.setValues(pos.getRow() + dir[0], pos.getColumn() + dir[1]);
        }

        if (getBoard().positionExists(pos) && isThereOpponentPiece(pos)) {
            mat[pos.getRow()][pos.getColumn()] = true;
        }
    }



}
