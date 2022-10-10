package ch.epfl.rigel.astronomy;

import java.util.Objects;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;

/**
 * Classe représentant le Soleil.
 * <p>
 * Publique, finale, immuable.
 * 
 * @author Orfeas Liossatos (310738)
 * @author Henrique Da Silva Gameiro (315689)
 */
public final class Sun extends CelestialObject {

    private final EclipticCoordinates eclipticPos;
    private final float meanAnomaly;
    
    /**
     * Constructeur par défaut de Sun.
     * Construit le Soleil avec la position écliptique, la position équatoriale, la taille angulaire et l'anomalie moyenne données,
     * ou lève NullPointerException si la position écliptique est nulle.
     * @param eclipticPos
     *          la position écliptique du Soleil
     * @param equatorialPos
     *          la position équatoriale du Soleil
     * @param angularSize
     *          la taille angulaire du Soleil
     * @param meanAnomaly
     *          l'anomalie moyenne du Soleil
     * @throws NullPointerException
     *          si la position écliptique est nulle.     
     */
    public Sun(EclipticCoordinates eclipticPos, EquatorialCoordinates equatorialPos, float angularSize, float meanAnomaly) {
        super("Soleil", equatorialPos, angularSize, -26.7f);
        
        this.eclipticPos = Objects.requireNonNull(eclipticPos); // EclipticCoordinates est immuable
        this.meanAnomaly = meanAnomaly;            
    }
    
    /**
     * Retourne la position écliptique du Soleil.
     * @return
     *          la position écliptique du Soleil;
     */
    public EclipticCoordinates eclipticPos() {
        return this.eclipticPos;
    }

    /**
     * Retourne l'anomalie moyenne du Soleil.
     * @return
     *          l'anomalie moyenne du Soleil.
     */
    public double meanAnomaly() {
        return this.meanAnomaly;
    }
    
    /**
     * Retourne l'age du Soleil en milliards d'années
     * @return
     *          l'age du Soleil en milliards d'années
     */
    public String age() {
        return "46";
    }
    
    /**
     * Retourne le type spectral du Soleil
     * @return
     *          le type spectral du Soleil
     */
    public String spectralType() {
        return "G2V";
    }
    
    /**
     * Retourne un court texte explicatif du Soleil
     * @return
     *          un court texte explicatif du Soleil
     */
    public String description() {
        return "Le Soleil est l’étoile du système solaire.\n"
        + "Dans la classification astronomique, \nc’est une étoile de type "
        + "naine jaune.\nLe Soleil est composée d’hydrogène et d’hélium,\n"
        + "et représente à lui seul environ 99.854%\n"
        + "de la masse du système solaire."; 
    }

}
