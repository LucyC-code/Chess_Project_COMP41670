package boardgame;

public abstract class Piece {
    protected Position position;
    private Board board;

    public Piece(Board board){
        this.board = board;
        position = null;
    }

    public Board getBoard() {
        return board;
    }

    public abstract boolean [][] possibleMoves( );

    public boolean isThereAnyPossibleMove() {
        boolean[][] mat = possibleMoves();
        for (int row = 0; row < mat.length; row++) {
            for (int col = 0; col < mat[row].length; col++) {
                if (mat[row][col]) {
                    return true; // Found at least one possible move
                }
            }
        }
        return false;
    }






}
