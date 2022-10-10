package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.Angle;

/**
 * Type énuméré représentant un modèle du Soleil.
 * <p>
 * Publique, immuable.
 * 
 * @author Orfeas Liossatos (310738)
 * @author Henrique Da Silva Gameiro (315689)
 */
public enum SunModel implements CelestialObjectModel<Sun> {
    
    /**
     * Un modèle du soleil
     */
    SUN; 

    // Des constantes caractéristiques du Soleil.
    private static final double LON_J2010             = Angle.ofDeg(279.557208);
    private static final double LON_PERIGEE           = Angle.ofDeg(283.112438);
    private static final double ORBITAL_ECCENTRICITY  = 0.016705;
    private static final double ANGULAR_SIZE_CIRCULAR = Angle.ofDeg(0.533128);
    
    // Des constantes dérivées
    private static final double LON_DIFF              = LON_J2010 - LON_PERIGEE;
    private static final double ANGULAR_SIZE_DENOM    = 1 - Math.pow(ORBITAL_ECCENTRICITY, 2);
    private static final double MEAN_ANGULAR_VELOCITY = Angle.TAU / 365.242191;
    
    /**
     * Retourne le Soleil modélisé par le modèle pour le nombre (éventuellement négatif)
     * de jours après l'époque J2010 donné, en utilisant la conversion donnée pour
     * obtenir ses coordonnées équatoriales à partir de ses coordonnées écliptiques.
     * @param daysSinceJ2010
     *          le nombre de jours (éventuellement négatifs) depuis l'époque J2010
     * @param eclipticToEquatorialConversion
     *          la conversion donnée
     * @return
     *          le Soleil modélisée par le modèle.
     */
    @Override
    public Sun at(double daysSinceJ2010,
            EclipticToEquatorialConversion eclipticToEquatorialConversion) {
        
        // Anomalie moyenne
        double meanAnomaly = MEAN_ANGULAR_VELOCITY * daysSinceJ2010 + LON_DIFF;
        
        // Anomalie vraie
        double trueAnomaly = meanAnomaly + 2 * ORBITAL_ECCENTRICITY * Math.sin(meanAnomaly);
        
        // Coordonnées écliptiques
        double lonEcliptic = trueAnomaly + LON_PERIGEE;
        
        // Conversion en coordonnées équatoriales
        if (lonEcliptic < 0) { lonEcliptic += Angle.TAU; }
        lonEcliptic = Angle.normalizePositive(lonEcliptic);

        EclipticCoordinates eclipticPos = EclipticCoordinates.of(lonEcliptic, 0);
        EquatorialCoordinates equatorialPos = eclipticToEquatorialConversion.apply(eclipticPos);
        
        // Taille angulaire
        double angularSize = ANGULAR_SIZE_CIRCULAR * ((1 + ORBITAL_ECCENTRICITY * Math.cos(trueAnomaly)) 
                / ANGULAR_SIZE_DENOM);
        
        return new Sun(eclipticPos, equatorialPos, (float) angularSize, (float) meanAnomaly);
    }

}
