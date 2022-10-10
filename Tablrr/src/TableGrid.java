public final class TableGrid {
    private final int numRows;
    private final int numCols;
    
    public TableGrid(int numRows, int numCols) {
        this.numRows = numRows;
        this.numCols = numCols;
    }
    
    public int getNumRows() { return numRows; }
    public int getNumCols() { return numCols; }
}
