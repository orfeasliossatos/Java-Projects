package ch.epfl.rigel.astronomy;

import java.util.Arrays;
import java.util.List;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.Angle;

/**
 * Type énuméré contenant les modèles des huit planètes du système solaire.
 * <p>
 * Publique, immuable.
 * 
 * @author Orfeas Liossatos (310738)
 * @author Henrique Da Silva Gameiro (315689)
 */
public enum PlanetModel implements CelestialObjectModel<Planet> {

    /**
     * Les modèles des planètes
     */
    
    MERCURY ("Mercure", 0.24085, 75.5671, 77.612, 0.205627,
            0.387098, 7.0051, 48.449, 6.74, -0.42, 0,
            "Mercure est la planète la plus\nproche du Soleil et la moins massive\ndu Système solaire. Son éloignement au \nSoleil est compris entre 0.31 et 0.47 UA."),
    
    VENUS   ("Vénus", 0.615207, 272.30044, 131.54, 0.006812, 
            0.723329, 3.3947, 76.769, 16.92, -4.40, 0,
            "Vénus est une des quatre planètres\ntelluriques du Système solaire. Elle est\nla deuxième planète par ordre d'éloignement\ndu Soleil, et la sixième par\nmasse ou par taille décroissantes."),
    
    EARTH   ("Terre", 0.999996, 99.556772, 103.2055, 0.016671,
            0.999985, 0, 0, 0, 0, 1,
            "Terre est une planète du Système\nsolaire, la troisième plus proche du\nSoleil et la cinquième plus grande,\ntant en taille qu'en masse,\nde ce système planétaire dont elle\nest également la plus massive des\nplanètes telluriques."),
    
    MARS    ("Mars", 1.880765, 109.09646, 336.217, 0.093348, 
            1.523689, 1.8497, 49.632, 9.36, -1.52, 2,
            "Mars est la quatrième planète par\nordre de distance croissante au Soleil\net la deuxième par masse et\npar taille croissantes. Son éloignement au\nSoleil est compris entre 1'381 et\n1'666 UA, avec une période orbitale\nde 669.58 jours martiens."),
    
    JUPITER ("Jupiter", 11.857911, 337.917132, 14.6633, 0.048907, 
            5.20278, 1.3035, 100.595, 196.74, -9.40, 79,
            "Jupiter est une planète géante gazeuse.\nIl s'agit de la plus grosse\nplanète du Système solaire, plus volumineuse\net massive que toutes les autres\nplanètes réunies, et la cinquième planète\npar sa distance au Soleil."),
    
    SATURN  ("Saturne", 29.310579, 172.398316, 89.567, 0.053853,
            9.51134, 2.4873, 113.752, 165.60, -8.88, 82,
            "Saturne est une planète géante.\nSaturne est la sixième planète du\nSystème solaire par ordre de distance\nau Soleil et la deuxième après\nJupiter tant par sa taille que\npar sa masse."),
    
    URANUS  ("Uranus",  84.039492, 356.135400, 172.884833, 0.046321,
            19.21814, 0.773059, 73.926961, 65.80, -7.19, 27,
            "Uranus est la septième planète du\nSystème solaire par sa distance au\nSoleil et la quatrième par la\nmasse. Elle doit son nom à\nla divinité romaine du ciel Uranus,\npère de Saturne et grand-père de\nJupiter, noms que portent les deux\nplanètes la précédant dans le Système solaire."),
    
    NEPTUNE ("Neptune", 165.84539, 326.895127, 23.07, 0.010483, 
            30.1985, 1.7673, 131.879, 62.20, -6.87, 14,
            "Neptune est géante de glace.\nNeptune est la huitième et dernière\nplanète du Système solaire par distance\ncroissante au Soleil");
    
    /**
     * Constructeur de PlanetModel. Construit un modèle de planète avec le nom français,
     * l'année tropique, la longitude à J2010 (en degrés), la longitude au périgée (en degrés), 
     * l'excentricité de l'orbite, le demi-grand axe de l'orbite, l'inclinaison de l'orbite à l'écliptique (en degrés),
     * la longitude du noeud ascendant (en degrés), la taille angulaire à une distace de 1 UA, et la magnitude à une distance de 1 UA.
     * @param frenchName
     *          le nom français de la planète
     * @param tropicalYear
     *          l'année tropique
     * @param longJ2010
     *          la longitude à J2010 (en degrés) de la planète
     * @param longPerigee
     *          la longitude au périgée (en degrés) de la planète
     * @param orbitalEccentricity
     *          l'excentricité de l'orbite de la planète
     * @param semiMajorAxis
     *          le demi-grand axe de l'orbite de la planète
     * @param orbitalInclination
     *          l'inclinaison de l'orbite à l'écliptique (en degrés) de la planète
     * @param longAscendingNode
     *          la longitude du noeud ascendant (en degrés) de la planète
     * @param angularSizeFrom1UA
     *          la taille angulaire à un distance de 1 UA de la planète
     * @param magnitudefrom1UA
     *          la magnitude à une distance de 1 UA de la planète
     */
    private PlanetModel(String frenchName, double tropicalYear, double lonJ2010, double lonPerigee, double orbitalEccentricity,
            double semiMajorAxis, double orbitalInclination, double lonAscendingNode, double angularSizeFrom1UA, double magnitudefrom1UA, int moons, String description) {
        this.frenchName             = frenchName;
        this.tropicalYear           = tropicalYear;
        this.lonJ2010               = Angle.ofDeg(lonJ2010);
        this.lonPerigee             = Angle.ofDeg(lonPerigee);
        this.orbitalEccentricity    = orbitalEccentricity;
        this.semiMajorAxis          = semiMajorAxis;
        this.lonAscendingNode       = Angle.ofDeg(lonAscendingNode);
        this.angularSizeFrom1UA     = Angle.ofArcsec(angularSizeFrom1UA);
        this.magnitudefrom1UA       = magnitudefrom1UA;
        this.planetRadiusFactor     = (1 - Math.pow(orbitalEccentricity, 2));
        this.sinOrbitalInclination  = Math.sin(Angle.ofDeg(orbitalInclination));
        this.cosOrbitalInclination  = Math.cos(Angle.ofDeg(orbitalInclination));
        this.description            = description;
        this.moons                  = moons;
    }
    
    /**
     * Une liste immuable constituée des huit modèles de planètes
     */
    public static final List<PlanetModel> ALL = List.copyOf(Arrays.asList(PlanetModel.values()));
    public static final List<PlanetModel> ALLEXTRATERRESTRIAL = List.copyOf(Arrays.asList(
            MERCURY, VENUS, MARS, JUPITER, SATURN, NEPTUNE, URANUS));
    
    private final String frenchName;
    private final double tropicalYear;
    private final double lonJ2010;
    private final double lonPerigee;
    private final double orbitalEccentricity;
    private final double semiMajorAxis;
    private final double lonAscendingNode;
    private final double angularSizeFrom1UA;
    private final double magnitudefrom1UA;
    private final double planetRadiusFactor;
    private final double sinOrbitalInclination;
    private final double cosOrbitalInclination;
    private final String description;
    private final int moons;

    // Des constantes caractéristiques aux modèles.
    private static final double MEAN_ANGULAR_VELOCITY = Angle.TAU / 365.242191;
    
    private double trueAnomaly(PlanetModel planetModel, double daysSinceJ2010) {
        // Anomalie moyenne
        double meanAnomaly = MEAN_ANGULAR_VELOCITY * (daysSinceJ2010 / planetModel.tropicalYear) + planetModel.lonJ2010 - planetModel.lonPerigee;
        
        // Anomalie vraie
        double trueAnomaly = meanAnomaly + 2 * planetModel.orbitalEccentricity * Math.sin(meanAnomaly);

        return trueAnomaly;
    }
    
    private double planetRadius(PlanetModel planetModel, double trueAnomaly) {
        return (planetModel.semiMajorAxis * planetModel.planetRadiusFactor)
                / (1 + planetModel.orbitalEccentricity * Math.cos(trueAnomaly));
    }
    
    /**
     * Retourne la planète modélisé par le modèle pour le nombre (éventuellement négatif)
     * de jours après l'époque J2010 donné, en utilisant la conversion donnée pour
     * obtenir ses coordonnées équatoriales à partir de ses coordonnées écliptiques.
     * @param daysSinceJ2010
     *          le nombre de jours (éventuellement négatifs) depuis l'époque J2010
     * @param eclipticToEquatorialConversion
     *          la conversion donnée
     * @return
     *          la planète modélisée par le modèle.
     */
    @Override
    public Planet at(double daysSinceJ2010,
            EclipticToEquatorialConversion eclipticToEquatorialConversion) {

        // Rayon, longitude et latitude héliocentrique de this PlanetModel
        double trueAnomaly = trueAnomaly(this, daysSinceJ2010);
        
        double rad = planetRadius(this, trueAnomaly);
        
        double lonHelio = trueAnomaly + lonPerigee;
        
        // Rayon, longitude et latitude héliocentrique de la Terre
        double earthTrueAnomaly = trueAnomaly(PlanetModel.EARTH, daysSinceJ2010);
        
        double earthRad = planetRadius(PlanetModel.EARTH, earthTrueAnomaly);
        
        double earthLonHelio = earthTrueAnomaly + PlanetModel.EARTH.lonPerigee; 
        
        // Projection du rayon sur l'écliptique et la longitude écliptique héliocentrique
        double sinDiff = Math.sin(lonHelio - lonAscendingNode);
        
        double latHelio = Math.asin(sinDiff * sinOrbitalInclination);        
        
        double radEcliptic = rad * Math.cos(latHelio);
        
        double lonEclipticHelio = Math.atan2(
                sinDiff * cosOrbitalInclination,
                Math.cos(lonHelio - lonAscendingNode)) + lonAscendingNode;
        
        // Longitude et latitude écliptiques géocentriques
        double lonHelioDiff = earthLonHelio - lonEclipticHelio;

        double sinLonHelioDiff = Math.sin(lonHelioDiff);
        
        double cosLonHelioDiff = Math.cos(lonHelioDiff);
        
        double lonEclipticGeo = 0;
        switch(this) {
            // Planètes inférieures
            case MERCURY:
            case VENUS: { lonEclipticGeo = Math.PI + earthLonHelio 
                    + Math.atan2(radEcliptic * sinLonHelioDiff,
                            earthRad - radEcliptic * cosLonHelioDiff); 
                break; }
            // Planètes supérieures
            case MARS:
            case JUPITER:
            case SATURN:
            case URANUS:
            case NEPTUNE: { lonEclipticGeo = lonEclipticHelio 
                    + Math.atan2(earthRad * (-sinLonHelioDiff), 
                            radEcliptic - earthRad * cosLonHelioDiff); 
                break; }
            // La Terre (on présume jamais appelé)
            default:
                break;
        }
        
        double latEclipticGeo = Math.atan(
                radEcliptic * Math.tan(latHelio) * Math.sin(lonEclipticGeo - lonEclipticHelio) 
                / (earthRad * (-sinLonHelioDiff)));

        // Conversion de coordonnées écliptiques en coordonnées équatoriales
        if (lonEclipticGeo < 0) { lonEclipticGeo += Angle.TAU; }
        lonEclipticGeo = Angle.normalizePositive(lonEclipticGeo);
        
        EclipticCoordinates eclipticPos = EclipticCoordinates.of(lonEclipticGeo, latEclipticGeo);
        EquatorialCoordinates equatorialPos = eclipticToEquatorialConversion.apply(eclipticPos);
        
        // Taille angulaire
        double planetEarthDist = Math.sqrt(Math.pow(earthRad, 2) + Math.pow(rad, 2) - 2 * earthRad * rad * Math.cos(lonHelio - earthLonHelio) * Math.cos(latHelio));
        
        double angularSize = angularSizeFrom1UA / planetEarthDist;
        
        // Magnitude
        double phase = (1 + Math.cos(lonEclipticGeo - lonHelio)) / 2;
        
        double magnitude = magnitudefrom1UA + 5 * Math.log10((rad * planetEarthDist) / (Math.sqrt(phase)));
        
        return new Planet(frenchName, equatorialPos, (float) angularSize, (float) magnitude, description, moons);
    }

}
