package back;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.StringJoiner;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Class containing a reservation. JavaFX Bean.
 * @author Orfeas Liossatos
 * 
 */
public final class ReservationBean {

    /// Properties
    private final StringProperty nameProperty;
    private final ObjectProperty<Preference> preferenceProperty;
    private final ObjectProperty<Integer> numberOfPeopleProperty;
    private final ObjectProperty<ITable> iTableProperty;
    private final ObjectProperty<LocalDateTime> reservationDateTimeProperty;
    private final StringProperty commentProperty;
    private final ObjectProperty<LocalDate> concertDateProperty;
    private final BooleanProperty blockProperty;
    
    public ReservationBean(String name, Preference preference, int numberOfPeople, ITable iTable,
             LocalDateTime reservationDateTime, String comment, LocalDate concertDate, boolean block) {
        
        nameProperty            = new SimpleStringProperty(name);
        commentProperty         = new SimpleStringProperty(comment);
        numberOfPeopleProperty  = new SimpleObjectProperty<>(numberOfPeople);
        iTableProperty          = new SimpleObjectProperty<>(iTable);
        preferenceProperty      = new SimpleObjectProperty<>(preference);
        concertDateProperty     = new SimpleObjectProperty<>(concertDate);
        reservationDateTimeProperty = new SimpleObjectProperty<>(reservationDateTime);
        blockProperty           = new SimpleBooleanProperty(block);
    }
    
    public static ReservationBean ofReservation(Reservation reservation) {
        return new ReservationBean(
                reservation.name(), 
                reservation.preference(), 
                reservation.numberOfPeople(), 
                reservation.iTable(), 
                reservation.reservationDateTime(), 
                reservation.comment(), 
                reservation.concertDate(),
                reservation.block());
    }
    
    /// Property getters
    public StringProperty nameProperty() { return nameProperty; }
    public ObjectProperty<Preference> preferenceProperty() { return preferenceProperty; }
    public ObjectProperty<Integer> numberOfPeopleProperty() { return numberOfPeopleProperty; }
    public ObjectProperty<ITable> iTableProperty() { return iTableProperty; }
    public ObjectProperty<LocalDateTime> reservationDateTimeProperty() { return reservationDateTimeProperty; }
    public StringProperty commentProperty() { return commentProperty; }
    public ObjectProperty<LocalDate> concertDateProperty() { return concertDateProperty; }
    public BooleanProperty blockProperty() { return blockProperty; }
    
    
    /// Value getters
    public String getName() { return nameProperty.getValue(); }
    public Preference getPreference() { return preferenceProperty.getValue(); }
    public Integer getNumberOfPeople() { return numberOfPeopleProperty.getValue(); }
    public ITable getITable() { return iTableProperty.getValue(); }
    public LocalDateTime getReservationDateTime() { return reservationDateTimeProperty.getValue(); }
    public String getComment() { return commentProperty.getValue(); }
    public LocalDate getConcertDate() { return concertDateProperty.getValue(); }
    public Boolean getBlock() { return blockProperty.getValue(); }
    
    /// Value setters
    public void setName(String name) { this.nameProperty.setValue(name); }
    public void setNumberOfPeople(Integer numberOfPeople) { this.numberOfPeopleProperty.setValue(numberOfPeople); }
    public void setITable(ITable iTable) { this.iTableProperty.setValue(iTable); }
    public void setReservationDateTime(LocalDateTime reservationDate) { this.reservationDateTimeProperty.setValue(reservationDate); }
    public void setPreference(Preference preference) { this.preferenceProperty.setValue(preference); }
    public void setComment(String comment) { this.commentProperty.setValue(comment); }
    public void setConcertDate(LocalDate concertDate) { this.concertDateProperty.setValue(concertDate); }
    public void setBlock(Boolean block) { this.blockProperty.setValue(block); }

    // Reservation methods
    public Reservation getReservation() { 
        return new Reservation(
                getITable(), 
                getName(), 
                getNumberOfPeople(), 
                getReservationDateTime(), 
                getPreference(), 
                getComment(), 
                getConcertDate(),
                getBlock()); 
    }
    
    public void setReservation(Reservation reservation) { 
        this.setName(reservation.name());
        this.setPreference(reservation.preference());
        this.setNumberOfPeople(reservation.numberOfPeople());
        this.setITable(reservation.iTable());
        this.setReservationDateTime(reservation.reservationDateTime());
        this.setComment(reservation.comment());
        this.setConcertDate(reservation.concertDate());
        this.setBlock(reservation.block());
    }
    
    @Override
    public String toString() {
        StringJoiner j = new StringJoiner(",");
        j.add(getName())
            .add(getPreference().toString())
            .add(String.valueOf(getNumberOfPeople()))
            .add(getITable().toString())
            .add(getReservationDateTime().toString())
            .add(getComment())
            .add(getConcertDate().toString())
            .add(getBlock().toString());
        return j.toString() + "\n";
    }

}

