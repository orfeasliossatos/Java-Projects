package ch.epfl.rigel.coordinates;

import java.time.ZonedDateTime;
import java.util.function.Function;

import ch.epfl.rigel.astronomy.SiderealTime;
import ch.epfl.rigel.math.Angle;

/**
 * Un changement de systèmes de coordonnées depuis les coordonnées équatoriales 
 * vers les coordonnées horizontales, à un instant et pour un lieu donnés. 
 * <p>
 * Publique, finale, immuable.
 * 
 * @author Orfeas Liossatos (310738)
 * @author Henrique Da Silva Gameiro (315689)
 */
public final class EquatorialToHorizontalConversion implements Function<EquatorialCoordinates, HorizontalCoordinates> {

    // Valeurs aidant au calcul, calculés à la construction
    private final double cosObsLat;
    private final double sinObsLat;
    private final double localSiderealTime;
    
    /**
     * Le constructeur par défaut de EquatorialToHorizontalConversion. Construit un changement de système
     * de coordonnées entre les coordonnées équatoriales et les coordonnées horizontales 
     * pour l'instant when et un lieu where.
     * @param when
     *          l'instant qu'on considère.
     * @param where
     *          le lieu qu'on considère.
     */
    public EquatorialToHorizontalConversion(ZonedDateTime when, GeographicCoordinates where) {
        this.cosObsLat = Math.cos(where.lat());
        this.sinObsLat = Math.sin(where.lat());
        this.localSiderealTime = SiderealTime.local(when, where);
    }

    /**
     * Convertit les coordonnées équatoriales equ en coordonnées horizontales.
     * @param ecl
     *          les coordonnées équatoriales qu'on considère.
     * @return les coordonnées horizontales correspondant aux coordonnées équatoriales ecl.
     */
    @Override
    public HorizontalCoordinates apply(EquatorialCoordinates equ) {
        double hourAngle = localSiderealTime - equ.ra();
        double cosDec = Math.cos(equ.dec());
        double sinDec = Math.sin(equ.dec());
        
        // L'altitude
        double alt = Math.asin(sinDec * sinObsLat + cosDec * cosObsLat * Math.cos(hourAngle));
        // L'azimut
        double az = Math.atan2(-cosDec * cosObsLat * Math.sin(hourAngle), sinDec - sinObsLat * Math.sin(alt));
        
        // Réduit aux intervalles de coordonnées horizontales
        if (az < 0) az += Angle.TAU;
        
        az = Angle.normalizePositive(az);
        
        return HorizontalCoordinates.of(az, alt);
    }
 
    // Operations non prises en charge
    
    /**
     * @see Object#hashCode()
     */
    @Override
    public final int hashCode() {
        throw new UnsupportedOperationException();
    }
    
    /**
     * @see Object#equals(Object)
     */
    @Override
    public final boolean equals(Object obj) {
        throw new UnsupportedOperationException();
    }


}
