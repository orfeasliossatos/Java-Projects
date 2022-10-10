package ch.epfl.rigel.astronomy;

import java.time.*;
import java.time.temporal.ChronoUnit;

import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.Polynomial;

/**
 * Classe contentant des méthodes permettant de calculer le temps sidéral.
 * <p>
 * Publique, finale, non-instanciable.
 * 
 * @author Orfeas Liossatos (310738)
 * @author Henrique Da Silva Gameiro (315689)
 */
public final class SiderealTime {

    private static final Polynomial S0_POLYNOMIAL = Polynomial.of(0.000025862, 2400.051336, 6.697374558);
    private static final double S1_FACTOR = 1.002737909;
    
    // Non-instanciable
    private SiderealTime() {}
    
    /**
     * Retourne le temps sidéral de Greenwich en radians et compris dans l'intervalle {@code [0, TAU[ }
     * pour l'instant when.
     * @param when
     *          l'instant qu'on considère
     * @return le temps sidéral de Greenwich en radians et compris dans l'intervalle {@code [0, TAU[ }
     * pour l'instant when.
     */
    public static double greenwich(ZonedDateTime when) {
        // Convertir en UTC
        when = when.withZoneSameInstant(ZoneOffset.UTC);
        // Le même jour, mais à 0h00.
        ZonedDateTime whenDayStart = when.truncatedTo(ChronoUnit.DAYS);
        
        double julianCenturiesDifference = Epoch.J2000.julianCenturiesUntil(whenDayStart);
        
        double hourDifference = (double) whenDayStart.until(when, ChronoUnit.MILLIS)
                / ChronoUnit.HOURS.getDuration().toMillis();
        
        double s0 = S0_POLYNOMIAL.at(julianCenturiesDifference);
        double s1 = S1_FACTOR * hourDifference;
        
        // Le temps sidéral de Greenwich, en heures d'arc
        double greenwichSiderealTime = s0 + s1;
                
        return Angle.normalizePositive(Angle.ofHr(greenwichSiderealTime));
    }
    
    /**
     * Retourne le temps sidéral local en radians et compris dans l'intervalle {@code [0, τ[ }
     * pour l'instant when et la position where.
     * @param when
     *          l'instant qu'on considère
     * @param where
     *          la position qu'on considère
     * @return le temps sidéral local en radians et compris dans l'intervalle {@code [0, τ[ }
     * pour l'instant when et la position where.
     */
    public static double local(ZonedDateTime when, GeographicCoordinates where) {
        return Angle.normalizePositive(greenwich(when) + where.lon());
    }
}
