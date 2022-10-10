package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.ClosedInterval;

/**
 * Classe représentant la Lune.
 * <p>
 * Publique, finale, immuable.
 * 
 * @author Orfeas Liossatos (310738)
 * @author Henrique Da Silva Gameiro (315689)
 */
public final class Moon extends CelestialObject {

    private final float phase;
    
    private static final ClosedInterval PHASE_INTERVAL = ClosedInterval.of(0, 1);
    
    /**
     * Constructeur de Moon.
     * Construit la Lune avec la position écliptique, la taille angulaire, la magnitude et la phase données,
     * ou lève IllegalArgumentException si la phase n'est pas comprise dans l'intervalle [0, 1].
     * @param equatorialPos
     *          les coordonées équatoriales de la Lune
     * @param angularSize
     *          la taille angulaire (diamètre apparent) de la Lune
     * @param magnitude
     *          la magnitude de la Lune
     * @param phase
     *          la phase de la Lune
     * @throws IllegalArgumentException
     *          si la phase n'est pas contenue dans l'intervalle {@code [0, 1[}.
     */
    public Moon(EquatorialCoordinates equatorialPos, float angularSize, float magnitude, float phase) {
        super("Lune", equatorialPos, angularSize, magnitude);
        
        Preconditions.checkInInterval(PHASE_INTERVAL, phase);
        
        this.phase = phase;
    }
    
    public double phase() {
        return phase;
    }
    
    /**
     * Retourne un texte informatif au sujet de la lune.
     * @return
     *          un texte informatif au sujet de la lune.
     */
    @Override
    public String info() {
        return String.format("Lune (%.1f%%)", phase * 100);
    }
    
    /**
     * Retourne un court texte explicatif de la Lune
     * @return
     *          un court texte explicatif de la Lune
     */
    public String description() {
        return "La Lune est un objet céleste qui orbite\nautour de la planète Terre et "
        + "le seul \nsatellite naturel permanent de la Terre. \nC'est le cinquième plus grand \n"
        + "satellite naturel du système solaire \net le plus grand des satellites planétaires \npar "
        + "rapport à la taille de la \nplanète autour de laquelle elle orbite."; 
    }
}
