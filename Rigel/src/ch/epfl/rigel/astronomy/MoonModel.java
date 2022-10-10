package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.RightOpenInterval;

/**
 * Type énuméré représentant un modèle de la Lune.
 * <p>
 * Publique, immuable.
 * 
 * @author Orfeas Liossatos (310738)
 * @author Henrique Da Silva Gameiro (315689)
 */
public enum MoonModel implements CelestialObjectModel<Moon> {
    
    /**
     * Un modèle de la Lune
     */
    MOON; 

    // Des constantes caractéristiques de la Lune
    private final static double LON_MEAN = Angle.ofDeg(91.929336);
    private final static double LON_MEAN_PERIGEE = Angle.ofDeg(130.143076);
    private final static double LON_ASCENDING_NODE = Angle.ofDeg(291.682547);
    private final static double ORBITAL_INCLINATION = Angle.ofDeg(5.145396);
    private final static double COS_ORBITAL_INCLINATION = Math.cos(ORBITAL_INCLINATION);
    private final static double SIN_ORBITAL_INCLINATION = Math.sin(ORBITAL_INCLINATION);
    private final static double ORBITAL_ECCENTRICITY = 0.0549;
    private final static double ANGULAR_SIZE_FROM_SEMI_MAJOR_AXIS = Angle.ofDeg(0.5181);
    private final static double PLANET_DIST_FACTOR = (1 - Math.pow(ORBITAL_ECCENTRICITY, 2));
    
    /**
     * Retourne la Lune modélisée par le modèle pour le nombre (éventuellement négatif)
     * de jours après l'époque J2010 donné, en utilisant la conversion donnée pour
     * obtenir ses coordonnées équatoriales à partir de ses coordonnées écliptiques.
     * @param daysSinceJ2010
     *          le nombre de jours (éventuellement négatifs) depuis l'époque J2010
     * @param eclipticToEquatorialConversion
     *          la conversion donnée
     * @return
     *          la Lune modélisée par le modèle.
     */
    @Override
    public Moon at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion) {
        
        // Modèle du soleil
        Sun sun = SunModel.SUN.at(daysSinceJ2010, eclipticToEquatorialConversion);

        // Calcul de la longitude orbitale 
        double lonOrbitalMean = (Angle.ofDeg(13.1763966) * daysSinceJ2010) + LON_MEAN;
        
        double meanAnomaly = lonOrbitalMean - (Angle.ofDeg(0.1114041) * daysSinceJ2010) - LON_MEAN_PERIGEE;
        
        double sunMeanAnomaly = sun.meanAnomaly();
       
        double sinSunMeanAnomaly = Math.sin(sunMeanAnomaly);
        
        double sunLonEclipticGeo = sun.eclipticPos().lon();

        double evection = Angle.ofDeg(1.2739) * Math.sin(2 * (lonOrbitalMean - sunLonEclipticGeo) - meanAnomaly);
        
        double correctionAnnualEquation = Angle.ofDeg(0.1858) * sinSunMeanAnomaly;
        
        double correction3 = Angle.ofDeg(0.37) * sinSunMeanAnomaly;

        double correctedAnomaly = meanAnomaly + evection - correctionAnnualEquation - correction3;

        double correctionCenterEquation = Angle.ofDeg(6.2886) * Math.sin(correctedAnomaly);
        
        double correction4 = Angle.ofDeg(0.214) * Math.sin(2 * correctedAnomaly);

        double lonOrbitalCorrected = lonOrbitalMean + evection + correctionCenterEquation - correctionAnnualEquation + correction4;

        double variation = Angle.ofDeg(0.6583) * Math.sin(2 * (lonOrbitalCorrected - sunLonEclipticGeo));
        
        double lonOrbitalTrue = lonOrbitalCorrected + variation;

        // Calcul de la position écliptique
        double lonAscendingNodeMean = LON_ASCENDING_NODE - Angle.ofDeg(0.0529539) * daysSinceJ2010;
        
        double lonAscendingNodeCorrected = lonAscendingNodeMean - Angle.ofDeg(0.16) * sinSunMeanAnomaly;
        
        double sinLonDiff = Math.sin(lonOrbitalTrue - lonAscendingNodeCorrected);
        
        double lonEcliptic = Math.atan2(sinLonDiff * COS_ORBITAL_INCLINATION,
                Math.cos(lonOrbitalTrue - lonAscendingNodeCorrected)) + lonAscendingNodeCorrected;
        
        double latEcliptic = Math.asin(sinLonDiff * SIN_ORBITAL_INCLINATION);
        
        // Conversion en coordonnées équatoriales
        if (lonEcliptic < 0) { lonEcliptic += Angle.TAU; }
        lonEcliptic = RightOpenInterval.of(0, Angle.TAU).reduce(lonEcliptic);
        
        EclipticCoordinates eclipticPos = EclipticCoordinates.of(lonEcliptic, latEcliptic);
        EquatorialCoordinates equatorialPos = eclipticToEquatorialConversion.apply(eclipticPos);
        
        // Phase de la lune 
        double phase = (1 - Math.cos(lonOrbitalTrue - sunLonEclipticGeo)) / 2;
        
        // Taille angulaire de la lune
        double moonEarthDist = PLANET_DIST_FACTOR / (1 + ORBITAL_ECCENTRICITY * Math.cos(correctedAnomaly + correctionCenterEquation));
       
        double angularSize = ANGULAR_SIZE_FROM_SEMI_MAJOR_AXIS / moonEarthDist;
        
        return new Moon(equatorialPos, (float) angularSize, 0, (float) phase);
    }

    
    
}
