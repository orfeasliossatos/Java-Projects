package back;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * A single reservation. Immutable.
 * @author Orfeas Liossatos
 */
public final class Reservation {
    
    private final ITable iTable;
    private final String name;
    private final int numberOfPeople;
    private final LocalDateTime reservationDateTime;
    private final Preference preference;
    private final String comment;
    private final LocalDate concertDate;
    private final boolean block;
    
    public Reservation(ITable iTable, String name, int numberOfPeople, LocalDateTime reservationDateTime, Preference preference,    
             String comment, LocalDate concertDate, boolean block) {
        
        this.iTable = iTable;
        this.name = name;
        this.numberOfPeople = numberOfPeople;
        this.reservationDateTime = reservationDateTime;
        this.preference = preference;
        this.comment = comment;
        this.concertDate = concertDate;
        this.block = block;
    }

    public static Reservation emptyReservation() {
        return new Reservation(
                SingleTable.chairBehind(), 
                "-", 
                0, 
                LocalDateTime.now(), 
                Preference.NONE, 
                "-", 
                LocalDate.now(),
                false);
    }
    
    public String name() { return this.name; }
    public Preference preference() { return this.preference; }
    public int numberOfPeople() { return this.numberOfPeople; }
    public ITable iTable() { return this.iTable; }
    public LocalDateTime reservationDateTime() { return this.reservationDateTime; }
    public String comment() { return this.comment; }
    public LocalDate concertDate() { return this.concertDate; }
    public boolean block() { return this.block; }
    
    @Override
    public boolean equals(Object that) {
        if (that instanceof Reservation) {
            if (((Reservation)that).name.equals(this.name)
                    && ((Reservation)that).reservationDateTime.equals(this.reservationDateTime)
                    && ((Reservation)that).concertDate.equals(this.concertDate)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, reservationDateTime, concertDate);
    }
    
}
