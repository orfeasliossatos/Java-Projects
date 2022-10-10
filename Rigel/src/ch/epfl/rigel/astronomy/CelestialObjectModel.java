package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;

/**
 * Interface représentant un modèle d'objet céleste.
 * <p>
 * Publique.
 * 
 * @author Orfeas Liossatos (310738)
 * @author Henrique Da Silva Gameiro (315689)
 */
public interface CelestialObjectModel<O> {
    
    /**
     * Retourne l'objet céleste modélisé par le modèle pour le nombre (éventuellement négatif)
     * de jours après l'époque J2010 donné, en utilisant la conversion donnée pour
     * obtenir ses coordonnées équatoriales à partir de ses coordonnées écliptiques.
     * @param daysSinceJ2010
     *          le nombre de jours (éventuellement négatifs) depuis l'époque J2010
     * @param eclipticToEquatorialConversion
     *          la conversion donnée
     * @return
     *          l'objet céleste modélisé par le modèle.
     */
    public abstract O at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion);

}
