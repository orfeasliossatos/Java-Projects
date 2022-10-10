package ch.epfl.rigel.coordinates;

import java.util.Locale;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;

/**
 * Classe représentant des coordonnées géographiques
 * <p>
 * Publique, finale, immuable
 * 
 * @author Orfeas Liossatos (310738)
 * @author Henrique Da Silva Gameiro (315689)
 */
public final class GeographicCoordinates extends SphericalCoordinates {
    
    public final static RightOpenInterval LONGITUDE_INTERVAL = RightOpenInterval.of(-180, 180);
    public final static ClosedInterval LATITUDE_INTERVAL = ClosedInterval.symmetric(180);
    
    /**
     * Constructeur par defaut de GeographicCoordinates. 
     * <p>
     * Bien que privé, cette classe reste instanciable,
     * car ce constructeur est appelé par une méthode statique.
     *
     * @param lonDeg
     *          la longitude
     * @param latDeg
     *          la latitude
     */
    private GeographicCoordinates(double lonDeg, double latDeg) {
        super(lonDeg, latDeg);
    }
    
    /**
     * Retourne des coordonnées géographiques avec la longitude (en degrés) et la latitude (en degrés) spécifiées.
     * @param lonDeg
     *          la longitude en degrés
     * @param latDeg
     *          la latitude en degrés
     * @return
     *          des coordonnées géographiques avec la longitude (en degrés) et la latitude (en degrés) spécifiées.
     */
    public static GeographicCoordinates ofDeg(double lonDeg, double latDeg) {
        Preconditions.checkInInterval(LONGITUDE_INTERVAL, lonDeg);
        Preconditions.checkInInterval(LATITUDE_INTERVAL, latDeg);
        
        return new GeographicCoordinates(Angle.ofDeg(lonDeg), Angle.ofDeg(latDeg));
    }
    
    /**
     * Vérifie si la longitude donnée est contenue dans l'intervalle {@code [0, 360[}
     * @param lonDeg
     *          la longitude donnee
     * @return
     *          sila longitude donnée est contenue dans l'intervalle {@code [0, 360[}
     */
    public static boolean isValidLonDeg(double lonDeg) {
        return LONGITUDE_INTERVAL.contains(lonDeg);
    }

    /**
     * Vérifie si la latitude donnée est contenue dans l'intervalle {@code [-90, 90]}
     * @param lonDeg
     *          la latitude donnee
     * @return
     *          si la latitude donnée est contenue dans l'intervalle {@code [-90, 90]}
     */
    public static boolean isValidLatDeg(double latDeg) {
        return LATITUDE_INTERVAL.contains(latDeg);
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
        return String.format(Locale.ROOT, "(lon=%.4f°, lat=%.4f°)", lonDeg(), latDeg());
    }
    
}
