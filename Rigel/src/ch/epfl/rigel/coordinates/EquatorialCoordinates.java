package ch.epfl.rigel.coordinates;

import java.util.Locale;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;

/**
 * Classe représentant des coordonnées équatoriales
 * <p>
 * Publique, finale, immuable
 * 
 * @author Orfeas Liossatos (310738)
 * @author Henrique Da Silva Gameiro (315689)
 */
public final class EquatorialCoordinates extends SphericalCoordinates {

    private final static RightOpenInterval RIGHT_ASCENSION_INTERVAL = RightOpenInterval.of(0, Angle.TAU);
    private final static ClosedInterval DECLINATION_INTERVAL = ClosedInterval.symmetric(Angle.TAU / 2.0);
    
    /**
     * Constructeur par défaut de EquatorialCoordinates.
     * <p>
     * Bien que privé, cette classe reste instanciable,
     * car ce constructeur est appelé par une méthode statique.
     * 
     * @param ra
     *          l'ascension droite en radians
     * @param dec
     *          la déclinaison en radians
     */
    private EquatorialCoordinates(double ra, double dec) {
        super(ra, dec);
    }
    
    /**
     * Retourne des coordonnées équatoriales avec la longitude (en radians) et la latitude (en radians) spécifiées.
     * @param ra
     *          l'ascension droite en radians
     * @param dec
     *          la déclinaison en radians
     * @return
     *          des coordonnées équatoriales avec la longitude (en radians) et la latitude (en radians) spécifiées.
     */
    public static EquatorialCoordinates of(double ra, double dec) {
        Preconditions.checkInInterval(RIGHT_ASCENSION_INTERVAL, ra);
        Preconditions.checkInInterval(DECLINATION_INTERVAL, dec);
        
        return new EquatorialCoordinates(ra, dec);
    }
    
    /**
     * Retourne l'ascension droite en radians
     * @return l'ascension droite en radians
     */
    public double ra() {
        return super.lon();
    }
    
    /**
     * Retourne l'ascension droite en degrés
     * @return l'ascension droite en degrés
     */
    public double raDeg() {
        return super.lonDeg();
    }

    /**
     * Retourne l'ascension droite en heures
     * @return l'ascension droite en heures
     */
    public double raHr() {
        return Angle.toHr(super.lon());
    }

    /**
     * Retourne la déclinaison en radians
     * @return la déclinaison en radians
     */
    public double dec() {
        return super.lat();
    }
    
    /**
     * Retourne la déclinaison en degrés
     * @return la déclinaison en degrés
     */
    public double decDeg() {
        return super.latDeg();
    }
   
    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(ra=%.4fh, dec=%.4f°)", raHr(), decDeg());
    }
    
}
