package ch.epfl.rigel.astronomy;

import java.time.*;
import java.time.temporal.ChronoUnit;

/**
 * Type énuméré représentant une époque astronomique.
 * 
 * @author Orfeas Liossatos (310738)
 * @author Henrique Da Silva Gameiro (315689)
 */
public enum Epoch {
    
    /**
     * Le 1er janvier 2000 à 12h00 UTC
     */
    J2000(ZonedDateTime.of(
            LocalDate.of(2000, Month.JANUARY, 1),
            LocalTime.NOON,
            ZoneOffset.UTC)),
   
    /**
     * Le 31 décembre 2009 à 0h00 UTC
     */
    J2010(ZonedDateTime.of(
            LocalDate.of(2010, Month.JANUARY, 1).minusDays(1),
            LocalTime.MIDNIGHT,
            ZoneOffset.UTC));

    private final ZonedDateTime zonedDateTime;
    
    private static final long MILLIS_PER_DAY = ChronoUnit.DAYS.getDuration().toMillis();
    private static final long DAYS_PER_JULIAN_CENTURIES = 36525;
    
    /**
     * Constructuer de l'enum Epoch. 
     * @param zonedDateTime
     *          la date et l'heure avec fuseau horaire
     */
    private Epoch(ZonedDateTime zonedDateTime) {
        this.zonedDateTime = zonedDateTime;
    }
    
    /**
     * Retourne le nombre de jours (positives ou négatives) entre l'époque à laquelle on l'applique et 
     * l'instant when.
     * @param when
     *          l'instant qu'on considère
     * @return le nombre de jours entre l'époque à laquelle on l'applique et l'instant when.
     */
    public double daysUntil(ZonedDateTime when) {
        long millisDifference = this.zonedDateTime.until(when, ChronoUnit.MILLIS);
        
        return (double) millisDifference / MILLIS_PER_DAY;
    }

    /**
     * Retourne le nombre de siècles juliens (positifs ou négatifs) entre l'époque à laquelle on l'applique et
     * l'instant when.
     * @param when
     *          l'instant qu'on considère
     * @return le nombre de siècles juliens entre l'époque à laquelle on l'applique et l'instant when.
     */
    public double julianCenturiesUntil(ZonedDateTime when) {
        double daysDifference = daysUntil(when);
        
        return daysDifference / DAYS_PER_JULIAN_CENTURIES;
    }
}