package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;

/**
 * Classe représentant une planète
 * <p>
 * Publique, finale, immuable.
 * 
 * @author Orfeas Liossatos (310738)
 * @author Henrique Da Silva Gameiro (315689)
 */
public final class Planet extends CelestialObject {

    private final String description;
    private final int moons;
    
    /**
     * Constructeur par défaut de Planet.
     * Construit une planète portant le nom name, situé aux coordonnées équatoriales equatorialPos, 
     * de taille angulaire angularSize et de magnitude magnitude.
     * @param name
     *          le nom de la planète
     * @param equatorialPos
     *          les coordonées équatoriales de la planète
     * @param angularSize
     *          la taille angulaire (diamètre apparent) de la planète
     * @param magnitude
     *          la magnitude de la planète
     */
    public Planet(String name, EquatorialCoordinates equatorialPos, float angularSize, float magnitude, String description, int moons) {
        super(name, equatorialPos, angularSize, magnitude);
        
        this.description = description;
        this.moons = moons;
    }
    
    /**
     * Retourne le nombre de satellites naturels nommés de la Planète
     * @return
     *          le nombre de satellites naturels nommés de la Planète
     */
    public int moons() {
        return moons;
    }
    
    /**
     * Retourne un court texte explicatif de la Planète
     * @return
     *          un court texte explicatif de la Planète
     */
    public String description() {
        return description;
    }
    
}
