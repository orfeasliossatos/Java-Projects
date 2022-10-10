import java.time.LocalDate;
import java.util.StringJoiner;

import back.ITable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public final class TableBean {
    
    private final ObjectProperty<ITable> tableProperty;
    private final ObjectProperty<LocalDate> concertDateProperty;
    
    public TableBean(ITable table, LocalDate concertDate) {
        tableProperty = new SimpleObjectProperty<>(table);
        concertDateProperty = new SimpleObjectProperty<>(concertDate);
    }

    public ObjectProperty<ITable> tableProperty() { return tableProperty; }
    public ObjectProperty<LocalDate> concertDateProperty() { return concertDateProperty; }

    public ITable getTable() { return tableProperty.getValue(); }
    public LocalDate getConcertDate() { return concertDateProperty.getValue(); }
    
    public void setTable(ITable table) { tableProperty.setValue(table); }
    public void setConcertDate(LocalDate concertDate) { this.concertDateProperty.setValue(concertDate); }
    

    @Override
    public String toString() {
        StringJoiner j = new StringJoiner(",");
        j.add(getConcertDate().toString())
            .add(getTable().csvString());
        return j.toString() + "\n";
    }

}
