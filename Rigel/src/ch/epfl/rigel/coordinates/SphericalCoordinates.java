package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;

/**
 * Classe mère à toutes les classes représentant des coordonnées sphériques
 * <p>
 * Package private, abstraite.
 * 
 * @author Orfeas Liossatos (310738)
 * @author Henrique Da Silva Gameiro (315689)
 */
abstract class SphericalCoordinates {

    private final double lon;
    private final double lat;
    
    /**
     * Constructeur par défaut de SphericalCoordinates
     * 
     * @param lon
     *          la longitude
     * @param lat
     *          la latitude
     */
    SphericalCoordinates(double lon, double lat) {
        this.lon = lon;
        this.lat = lat;
    }
    
    /**
     * Retourne la longitude en radians
     * @return la longitude en radians
     */
    double lon() {
        return lon;
    }
    

    /**
     * Retourne la longitude en degrés
     * @return la longitude en degrés
     */
    double lonDeg() {
        return Angle.toDeg(lon);
    }
    

    /**
     * Retourne la latitude en degrés
     * @return la latitude en degrés
     */
    double lat() {
        return lat;
    }
    

    /**
     * Retourne la latitude en degrés
     * @return la latitude en degrés
     */
    double latDeg() {
        return Angle.toDeg(lat);
    }
    
    // Opérations non-prises en charge
    
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
