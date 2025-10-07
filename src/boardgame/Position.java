package boardgame;

/** This code represents a position on a board using row and column indices
 * Rows and columns are typically zero based.
 */

public class Position {
    private int row;
    private int column;

    /**
     * Creates a new position with the given row and column
     * @param row the row index (0 based)
     * @param column the column index (0 based)
     */

    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    /** Updates both the row and column at once.
     *
     * @param row new row index
     * @param column new column index
     */
    public void setValues(int row, int column) {
        this.row = row;
        this.column = column;
    }



    @Override
    public String toString() {
        return row + ", " + column;
    }
}
