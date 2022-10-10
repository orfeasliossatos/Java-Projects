import java.time.LocalDate;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Class containing a date. JavaFX Bean
 * @author Orfeas Liossatos
 *
 */
public class DateBean {

    private final ObjectProperty<LocalDate> dateProperty;

    public DateBean() {
        this.dateProperty = new SimpleObjectProperty<>();
    }
    
    public DateBean(LocalDate date) {
        this.dateProperty = new SimpleObjectProperty<>(date);
    }
    
    /**
     * Returns the date property
     * @return the date property
     */
    public ObjectProperty<LocalDate> dateProperty() { return this.dateProperty; }

    /**
     * Returns the value of the date property
     * @return the value of the date property
     */
    public LocalDate getDate() { return this.dateProperty.getValue(); }
    

    /**
     * Sets the date property to {@code date}
     * @param date the new date
     */
    public void setDate(LocalDate date) { this.dateProperty.setValue(date); }
}
