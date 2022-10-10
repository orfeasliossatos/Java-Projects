package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.ClosedInterval;

/**
 * Classe représentant une étoile.
 * <p>
 * Publique, finale, immuable.
 * 
 * @author Orfeas Liossatos (310738)
 * @author Henrique Da Silva Gameiro (315689)
 */
public final class Star extends CelestialObject {

    private final int hipparcosId;
    private final int colorTemperature;
    private final double distance;
    private final String spectralType;
    
    private static final ClosedInterval COLOR_INDEX_INTERVAL = ClosedInterval.of(-0.5, 5.5);
    
    /**
     * Constructeur de Star. Construit une étoile avec le numéro Hipparcos, le nom, 
     * la position équatoriale, la magnitude et l'indice de couleur donnés. 
     * Lève IllegalArgumentException si le numéro Hipparcos est négatif, 
     * ou si l'indice de couleur n'est pas compris dans l'intervalle [-0.5, 5.5].
     * @param hipparcosId
     *          le numéro Hipparcos de l'étoile
     * @param name
     *          le nom de l'étoile
     * @param equatorialPos
     *          la position équatoriale de l'étoile
     * @param magnitude
     *          la magnitude de l'étoile
     * @param colorIndex
     *          l'indice de couleur de l'étoile
     * @throws IllegalArgumentException
     *          si le numéro Hipparcos est négatif, ou si l'indice de couleur 
     *          n'est pas compris dans l'intervalle [-0.5, 5.5].
     */
    public Star(int hipparcosId, String name, EquatorialCoordinates equatorialPos,
            float magnitude, float colorIndex, float distance, String spectralType) {

        super(name, equatorialPos, 0, magnitude);
        
        Preconditions.checkArgument(hipparcosId >= 0);
        Preconditions.checkInInterval(COLOR_INDEX_INTERVAL, colorIndex);
        
        this.hipparcosId = hipparcosId;
        this.colorTemperature = (int) (4600 * ((1.0 / (0.92 * colorIndex + 1.7)) + (1.0 / (0.92 * colorIndex + 0.62))));
        this.distance = distance;
        this.spectralType = spectralType;
    }
    
    /**
     * Retourne le numéro Hipparcos de l'étoile
     * @return
     *          le numéro Hipparcos de l'étoile
     */
    public int hipparcosId() {
        return this.hipparcosId;
    }
    
    /**
     * Retourne la température de couleur de l'étoile, en degrés Kelvin, arrondie à l'entier inférieur le plus proche.   
     * @return
     *          la température de couleur de l'étoile
     */
    public int colorTemperature() {
        return colorTemperature;
    }
    
    /**
     * Retourne la distance de l'étoile depuis la Terre, en parsecs.
     * @return
     *          la distance de l'étoile depuis la Terre, en parsecs.
     */
    public double distance() {
        return distance;
    }
    

    /**
     * Retourne le type spéctral de l'étoile, selon la classification de Harvard.
     * @return
     *          le type spéctral de l'étoile, selon la classification de Harvard.
     */
    public String spectralType() {
        return spectralType;
    }
}
