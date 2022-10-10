package ch.epfl.rigel.coordinates;

import java.util.Locale;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;

/**
 * Classe représentant des coordinnées écliptiques
 * <p>
 * Publique, finale, immuable
 * 
 * @author Orfeas Liossatos (310738)
 * @author Henrique Da Silva Gameiro (315689)
 */
public final class EclipticCoordinates extends SphericalCoordinates {
    
    private final static RightOpenInterval LONGITUDE_INTERVAL = RightOpenInterval.of(0, Angle.TAU);
    private final static ClosedInterval LATITUDE_INTERVAL = ClosedInterval.symmetric(Angle.TAU / 2.0);
    
    private EclipticCoordinates(double lon, double lat) {
        super(lon, lat);
    }
    
    /**
     * Retourne des coordonnées écliptiques avec la longitude (en radians) et la latitude (en radians) spécifiées.
     * @param lon
     *          la longitude en radians
     * @param lat
     *          la latitude en radians
     * @return
     *          des coordonnées équatoriales avec la longitude (en radians) et la latitude (en radians) spécifiées.
     */
    public static EclipticCoordinates of(double lon, double lat) {
        Preconditions.checkInInterval(LONGITUDE_INTERVAL, lon);
        Preconditions.checkInInterval(LATITUDE_INTERVAL, lat);
        
        return new EclipticCoordinates(lon, lat);
    }
    
    /**
     * Retourne la longitude en radians
     * @return la longitude en radians
     */
    public double lon() {
        return super.lon();
    }

    /**
     * Retourne la longitude en degrés
     * @return la longitude en degrés
     */
    public double lonDeg() {
        return super.lonDeg();
    }

    /**
     * Retourne la latitude en degrés
     * @return la latitude en degrés
     */
    public double lat() {
        return super.lat();
    }

    /**
     * Retourne la latitude en degrés
     * @return la latitude en degrés
     */
    public double latDeg() {
        return super.latDeg();
    }
   
    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(λ=%.4f°, β=%.4f°)", lonDeg(), latDeg());
    }
}
