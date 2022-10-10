package ch.epfl.rigel.gui;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Classe contenant l'instant d'observation. Bean JavaFX.
 * <p>
 * Publique, finale.
 * 
 * @author Orfeas Liossatos (310738)
 * @author Henrique Da Silva Gameiro (315689)
 */
public final class DateTimeBean {

    /// Propriétés
    private final ObjectProperty<LocalDate> dateProperty  = new SimpleObjectProperty<>(null);
    private final ObjectProperty<LocalTime> timeProperty  = new SimpleObjectProperty<>(null);
    private final ObjectProperty<ZoneId> zoneProperty     = new SimpleObjectProperty<>(null);


    /// Méthodes retournant les propriétés
    
    /**
     * Retourne la propriété de la date d'observation
     * @return
     *          la propriété de la date d'observation
     */
    public ObjectProperty<LocalDate> dateProperty(){
        return this.dateProperty;
    }   

    /**
     * Retourne la propriété du temps d'observation
     * @return
     *          la propriété du temps d'observation
     */
    public ObjectProperty<LocalTime> timeProperty(){
        return this.timeProperty;
    }
    
    /**
     * Retourne la propriété de la zone d'observation
     * @return
     *          la propriété de la zone d'observation
     */
    public ObjectProperty<ZoneId> zoneProperty(){
        return this.zoneProperty;
    }

    /// Méthodes retournant le contenu des propriétés.
    
    /**
     * Retourne la date d'observation
     * @return
     *          la date d'observation
     */
    public LocalDate getDate() {
        return this.dateProperty.getValue();
    }

    /**
     * Retourne le temps d'observation
     * @return
     *          le temps d'observation
     */
    public LocalTime getTime() {
        return this.timeProperty.getValue();
    }

    /**
     * Retourne la zone d'observation
     * @return
     *          la zone d'observation
     */
    public ZoneId getZone() {
        return this.zoneProperty.getValue();
    }
    
    /// Méthodes permettant de modifier le contenu de la propriété
    
    /**
     * Modifie la date d'observation pour qu'elle soit égale à {@code date}
     * @param date
     *          la nouvelle date
     */
    public void setDate(LocalDate date) {
        this.dateProperty.setValue(date);
    }
    
    /**
     * Modifie le temps d'observation pour qu'il soit égal à {@code time}
     * @param time
     *          le nouveau temps
     */
    public void setTime(LocalTime time) {
        this.timeProperty.setValue(time);
    }
    
    /**
     * Modifie la zone d'observation pour qu'elle soit égale à {@code zone}
     * @param zone
     *          la nouvelle zone
     */
    public void setZone(ZoneId zone) {
        this.zoneProperty.setValue(zone);
    }
    
    /// Méthodes sur ZonedDateTime
    
    /**
     * Retourne l'instant d'observation sous la forme d'un ZonedDateTime.
     * @return
     *          l'instant d'observation sous la forme d'un ZonedDateTime
     */
    public ZonedDateTime getZonedDateTime() {
        return ZonedDateTime.of(
                this.getDate(),
                this.getTime(), 
                this.getZone());
    }

    /**
     * Modifie l'instant d'observation pour qu'il soit égal à {@code now}
     * @param then
     *          le nouveau ZonedDateTime.
     */
    public void setZonedDateTime(ZonedDateTime then) {
        this.setDate(then.toLocalDate());
        this.setTime(then.toLocalTime());
        this.setZone(then.getZone());
    }
}
