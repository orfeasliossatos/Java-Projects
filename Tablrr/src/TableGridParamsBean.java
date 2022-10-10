import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

public class TableGridParamsBean {

    private final ObjectProperty<Integer> numRowsProperty;
    private final ObjectProperty<Integer> numColsProperty;
    private final BooleanProperty snapToGridProperty;
    
    public TableGridParamsBean(int numRows, int numCols, boolean snapToGrid) {
        numRowsProperty = new SimpleObjectProperty<Integer>(numRows);
        numColsProperty = new SimpleObjectProperty<Integer>(numCols);
        snapToGridProperty = new SimpleBooleanProperty(snapToGrid);
    }

    public ObjectProperty<Integer> numRowsProperty() { return numRowsProperty; }
    public ObjectProperty<Integer> numColsProperty() { return numColsProperty; }
    public BooleanProperty snapToGridProperty() { return snapToGridProperty; }
    
    public Integer getNumRows() { return numRowsProperty.getValue(); }
    public Integer getNumCols() { return numColsProperty.getValue(); }
    public boolean getSnapToGrid() { return snapToGridProperty.get(); }
    
    public void setNumRows(int numRows) { numRowsProperty.set(numRows); }
    public void setNumCols(int numCols) { numColsProperty.set(numCols); }
    public void setSnapToGrid(boolean snapToGrid) { snapToGridProperty.set(snapToGrid); }
    
}
