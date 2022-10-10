package ch.epfl.rigel.coordinates;

import java.util.Locale;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;

/**
 * Classe représentant des coordonnées horizontales
 * <p>
 * Publique, finale, immuable
 * 
 * @author Orfeas Liossatos (310738)
 * @author Henrique Da Silva Gameiro (315689)
 */
public final class HorizontalCoordinates extends SphericalCoordinates {

    private static final int OCTANT_DEG = 45;
    
    private static final RightOpenInterval AZIMUT_INTERVAL = RightOpenInterval.of(0, Angle.TAU);
    private static final ClosedInterval ALTITUDE_INTERVAL = ClosedInterval.symmetric(Angle.TAU / 2.0);
    
    private static final RightOpenInterval AZIMUT_DEG_INTERVAL = RightOpenInterval.of(0, 360);
    private static final ClosedInterval ALTITUDE_DEG_INTERVAL = ClosedInterval.symmetric(180);
    
    /**
     * Constructeur par defaut de HorizontalCoordinates. 
     * <p>
     * Bien que prive, cette classe reste instanciable,
     * car ce constructeur est appele par des methodes statiques.
     * 
     * 
     * @param az
     *          l'azimut
     * @param alt
     *          la hauteur
     */
    private HorizontalCoordinates(double az, double alt) {
        super(az, alt);
    }

    /**
     * Retourne des coordonnées horizontales avec l'azimut (en radians) et la hauteur (en radians) spécifiées.
     * @param az
     *          l'azimut en radians
     * @param alt
     *          la hauteur en radians
     * @return
     *          des coordonnées horizontales avec l'azimut (en radians) et la hauteur (en radians) spécifiées.
     */
    public static HorizontalCoordinates of(double az, double alt) {
        Preconditions.checkInInterval(AZIMUT_INTERVAL, az);
        Preconditions.checkInInterval(ALTITUDE_INTERVAL, alt);
        
        return new HorizontalCoordinates(az, alt);
    }
    
    /**
     * Retourne des coordonnées horizontales avec l'azimut (en degrés) et la hauteur (en degrés) spécifiées.
     * @param az
     *          l'azimut en degrés
     * @param alt
     *          la hauteur en degrés
     * @return
     *          des coordonnées horizontales avec l'azimut (en degrés) et la hauteur (en degrés) spécifiées.
     */
    public static HorizontalCoordinates ofDeg(double azDeg, double altDeg) {
        Preconditions.checkInInterval(AZIMUT_DEG_INTERVAL, azDeg);
        Preconditions.checkInInterval(ALTITUDE_DEG_INTERVAL, altDeg);
        
        return new HorizontalCoordinates(Angle.ofDeg(azDeg), Angle.ofDeg(altDeg));
    }
    
    /**
     * Retourne l'azimut en radians
     * @return l'azimut en radians
     */
    public double az() {
        return super.lon();
    }

    /**
     * Retourne l'azimut en degrés
     * @return l'azimut en degrés
     */
    public double azDeg() {
        return super.lonDeg();
    }
    
    /**
     * Retourne une chaîne correspondant à l'octant dans lequel se trouve l'azimut du
     * récepteur, chaîne formée en combinant les chaînes n, e, s, et w correspondant aux 
     * quatre points cardinaux (resp. nord, est, sud et ouest).
     * @param n
     *          la chaîne correspondant au nord
     * @param e
     *          la chaîne correspondant au est
     * @param s
     *          la chaîne correspondant au sud
     * @param w
     *          la chaîne correspondant au ouest
     * @return 
     *          une chaîne correspondant à l'octant dans lequel se trouve l'azimut du
     *          récepteur
     */
    public String azOctantName(String n, String e, String s, String w) {
        // Transformation des degrés en un nombre entre 0 et 8   
        int octant =  (int) Math.round(azDeg() / OCTANT_DEG);
        
        String azOctantName = null;
        
        // Mapping de F(8) à l'espace des noms des octants
        switch(octant) {
            case 8:
            case 0: { azOctantName = n;     break;}
            case 1: { azOctantName = n + e; break;}
            case 2: { azOctantName = e;     break;}
            case 3: { azOctantName = s + e; break;}
            case 4: { azOctantName = s;     break;}
            case 5: { azOctantName = s + w; break;}
            case 6: { azOctantName = w;     break;}
            case 7: { azOctantName = n + w; break;}
            default: { break; }
        }
        
        return azOctantName;
    }
    
    /**
     * Retourne la hauteur en degrés
     * @return la hauteur en degrés
     */
    public double alt() {
        return super.lat();
    }

    /**
     * Retourne la hauteur en degrés
     * @return la hauteur en degrés
     */
    public double altDeg() {
        return super.latDeg();
    }
    
    /**
     * Retourne la distance entre ces coordonnées horizontales-ci et ces coordonnées horizontales-là.
     * @param that
     *              ces coordonnées horizontales-là
     * @return
     *              la distance angulaire entre les deux coordonnées horizontales.
     */
    public double angularDistanceTo(HorizontalCoordinates that) {
        return Math.acos(Math.sin(this.alt()) * Math.sin(that.lat())
                + Math.cos(this.alt()) * Math.cos(that.alt()) * Math.cos(this.az() - that.az()));
    }
    
    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(az=%.4f°, alt=%.4f°)", azDeg(), altDeg());
    }
}
