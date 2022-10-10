package ch.epfl.rigel.astronomy;

import java.util.Objects;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;

/**
 * Classe abstraite représentant des objets célestes.
 * <p>
 * Publique, abstraite, immuable.
 * 
 * @author Orfeas Liossatos (310738)
 * @author Henrique Da Silva Gameiro (315689)
 */
public abstract class CelestialObject {
    
    private final String name;
    private final EquatorialCoordinates equatorialPos;
    private final float angularSize;
    private final float magnitude;
  
    /**
     * Constructeur de CelestialObject. 
     * Construit un objet céleste portant le nom {@code name}, situé aux coordonnées équatoriales {@code equatorialPos}, 
     * de taille angulaire {@code angularSize} et de magnitude {@code magnitude}.
     * @param name
     *          le nom de l'objet céleste, non-null
     * @param equatorialPos
     *          les coordonées équatoriales de l'objet céleste, non-null
     * @param angularSize
     *          la taille angulaire (diamètre apparent) de l'objet céleste
     * @param magnitude
     *          la magnitude de l'objet céleste
     * @throws IllegalArgumentException
     *          si la taille angulaire est négative.
     * @throws NullPointerException
     *          si le nom ou la position équatoriale sont nuls.
     */
    CelestialObject(String name, EquatorialCoordinates equatorialPos, float angularSize, float magnitude) {
        Preconditions.checkArgument(angularSize >= 0);
        
        this.name = Objects.requireNonNull(name);   
        this.equatorialPos = Objects.requireNonNull(equatorialPos);
        this.angularSize = angularSize;
        this.magnitude = magnitude;
    }
    
    /**
     * Retourne le nom du CelestialObject.
     * @return
     *          le nom de CelestialObject.
     */
    public String name() {
        return name; // String est immuable
    }
    
    /**
     * Retourne la taille angulaire du CelestialObject.
     * @return
     *          la taille angulaire du CelestialObject.
     */
    public double angularSize() {
        return angularSize;
    }
    
    /**
     * Retourne la magnitude du CelestialObject.
     * @return
     *          la magnitude du CelestialObject.
     */
    public double magnitude() {
        return magnitude;
    }
    
    /**
     * Retourne la position équatoriale du CelestialObject.
     * @return
     *          la position équatoriale du CelestialObject.
     */
    public EquatorialCoordinates equatorialPos() {
        return this.equatorialPos; 
    }
    
    /**
     * Retourne un texte informatif au sujet de l'objet céleste.
     * @return
     *          un texte informatif au sujet de l'objet céleste.
     */
    public String info() {
        return name;
    }
    
    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return info();
    }
}