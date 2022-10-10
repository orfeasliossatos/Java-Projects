package back;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class representing a catalogue of reservation beans
 * @author Orfeas Liossatos
 *
 */
public final class ReservationBeansCatalogue {
    
    private final List<ReservationBean> reservations;
    
    public ReservationBeansCatalogue(List<ReservationBean> reservations) {
        this.reservations = List.copyOf(reservations);
    }
    
    /**
     * Returns the list of reservations
     * @return the list of reservations
     */
    public List<ReservationBean> reservations() {
        return this.reservations;
    }
    
    /**
     * Interface representing a loader of reservations
     * @author Orfeas Liossatos
     *
     */
    public interface Loader {

        /**
         * Loads the reservations from the input stream {@code inputStream} and adds them 
         * to the reservation catalogue of the builder {@code builder}, 
         * or raises IOException in case of an input/output error.
         * @param inputStream
         *          the input stream
         * @param builder
         *          the list of reservations builder
         * @throws IOException
         *          in case of an input/output error.
         */
        public abstract void load(InputStream inputStream, Builder builder) throws IOException;
    }
    
    /**
     * Classe représentant un bâtisseur de catalogue d'étoiles.
     * <p>
     * Publique, finale.
     */
    public final static class Builder {

        private List<ReservationBean> reservations;
        
        public Builder() {
            reservations = new ArrayList<>();
        }
        
        public Builder addReservationBean(ReservationBean reservation) {
            reservations.add(reservation);
            return this;
        }
        
        public List<ReservationBean> reservationBeans() {
            return Collections.unmodifiableList(reservations);
        }
        
        public Builder loadFrom(InputStream inputStream, Loader loader) throws IOException {
            loader.load(inputStream, this);
            return this;
        }
        
        public ReservationBeansCatalogue build() {
            return new ReservationBeansCatalogue(reservations);
        }
    }
}
