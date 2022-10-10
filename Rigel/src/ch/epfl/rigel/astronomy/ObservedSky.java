package ch.epfl.rigel.astronomy;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.coordinates.EquatorialToHorizontalConversion;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;

/**
 * Classe représentant un ensemble d'objets célestes projetés dans le plan par une projéction stéréographique
 * <p>
 * Publique, immuable.
 * 
 * @author Orfeas Liossatos (310738)
 * @author Henrique Da Silva Gameiro (315689)
 */
public final class ObservedSky {
    
    private final Sun sun;
    private final CartesianCoordinates sunPosition;
    
    private final Moon moon;
    private final CartesianCoordinates moonPosition;
    
    private final List<Planet> planets;
    private final double[] planetPositions;
    
    private final StarCatalogue starCatalogue;
    private final double[] starPositions;
    
    // BONUS
    private final Map<CelestialObject, CartesianCoordinates> celestialObjectMap = new HashMap<>();
    
    /**
     * Constructeur de ObservedSky. Construit le ciel observé et ses objets célestes {@code starCatalogue} à un instant donné {@code when}, 
     * depuis une position sur la Terre donnée {@code geographicPos}, par une projection stéréographique {@code stereographicProjection}
     * @param when
     * @param geographicPos
     * @param stereographicProjection
     * @param starCatalogue
     */
    public ObservedSky(ZonedDateTime when, GeographicCoordinates geographicPos,
            StereographicProjection stereographicProjection, StarCatalogue starCatalogue) {
        
        // Le soleil
        sun = SunModel.SUN.at(
                Epoch.J2010.daysUntil(when), 
                new EclipticToEquatorialConversion(when));
        
        sunPosition = equatorialToCartesianCoordinates(
                when, stereographicProjection, geographicPos,
                sun.equatorialPos());
        
        celestialObjectMap.put(sun, sunPosition);
        
        // La lune
        moon = MoonModel.MOON.at(
                Epoch.J2010.daysUntil(when),
                new EclipticToEquatorialConversion(when));
        
        moonPosition = equatorialToCartesianCoordinates(
                when, stereographicProjection, geographicPos,
                moon.equatorialPos());
        
        celestialObjectMap.put(moon, moonPosition);
        
        // Les planètes extraterrestres
        planets = new ArrayList<Planet>();
        for (PlanetModel planetModel : PlanetModel.ALLEXTRATERRESTRIAL) {
            planets.add(planetModel.at(
                    Epoch.J2010.daysUntil(when), 
                    new EclipticToEquatorialConversion(when)));
        }
        
        int numPlanetPositionComponents = planets.size() * 2;
        planetPositions = new double[numPlanetPositionComponents];
        
        fillCelestialObjectPositionsArray(planetPositions, planets, 
                when, geographicPos, stereographicProjection);
        
        
        // Les étoiles
        this.starCatalogue = starCatalogue;
        
        int numStarPositionComponents = stars().size() * 2;
        starPositions = new double[numStarPositionComponents];
        
        fillCelestialObjectPositionsArray(starPositions, stars(), 
                when, geographicPos, stereographicProjection);

    }
    
    private CartesianCoordinates equatorialToCartesianCoordinates(ZonedDateTime when, 
            StereographicProjection stereographicProjection, GeographicCoordinates geographicPos,
            EquatorialCoordinates equatorialPos) {

        return stereographicProjection.compose(
                new EquatorialToHorizontalConversion(when, geographicPos))
                .apply(equatorialPos);
    }
    
    private void fillCelestialObjectPositionsArray(double[] positionsArray, List<? extends CelestialObject> celestialObjects,
            ZonedDateTime when, GeographicCoordinates geographicPos, StereographicProjection stereographicProjection) {
        
        Preconditions.checkArgument(positionsArray.length == celestialObjects.size() * 2);
        
        int i = 0;
        for (CelestialObject obj : celestialObjects) {

            CartesianCoordinates objCoordinates = equatorialToCartesianCoordinates(when,
                    stereographicProjection, geographicPos,
                    obj.equatorialPos());
            
            positionsArray[i++] = objCoordinates.x();
            positionsArray[i++] = objCoordinates.y();
            
            celestialObjectMap.put(obj, objCoordinates);
        }
    }
    
    /**
     * Retourne le soleil du ciel observée
     * @return
     *          le soleil du ciel observée
     */
    public Sun sun() {
        return sun;
    }
    
    /**
     * Retourne la position du soleil en coordonnées cartésiennes du ciel observé
     * @return
     *          la position du soleil en coordonnées cartésiennes du ciel observé
     */
    public CartesianCoordinates sunPosition() {
        return sunPosition;
    }
    
    /**
     * Retourne la lune du ciel observée
     * @return
     *          la lune du ciel observée
     */
    public Moon moon() {
        return moon;
    }
    
    /**
     * Retourne la position de la lune en coordonnées cartésiennes du ciel observée
     * @return
     *          la position de la lune en coordonnées cartésiennes du ciel observée
     */
    public CartesianCoordinates moonPosition() {
        return moonPosition;
    }
    
    /**
     * Retourne une liste des planètes du ciel observé
     * @return
     *          une liste des planètes du ciel observé
     */
    public List<Planet> planets() {
        return planets;
    }
    
    /**
     * Retourne un tableau des positions en coordonnées cartésiennes des planètes du ciel observé
     * @return
     *          un tableau des positions en coordonnées cartésiennes des planètes du ciel observé
     */
    public double[] planetPositions() {
        return Arrays.copyOf(planetPositions, planets.size() * 2);
    }
    
    /**
     * Retourne une liste des étoiles du ciel observé
     * @return
     *          une liste des étoiles du ciel observé
     */
    public List<Star> stars() {
        return starCatalogue.stars();
    }
    
    /**
     * Retourne un tableau des positions en coordonnées cartésiennes des étoiles du ciel observé
     * @return
     *          un tableau des positions en coordonnées cartésiennes des étoiles du ciel observé
     */
    public double[] starPositions() {
        return Arrays.copyOf(starPositions, stars().size() * 2);
    }
    
    /**
     * Retourne les astérismes du ciel observé
     * @return
     *          les astérismes du ciel observé
     */
    public Set<Asterism> asterisms() {
        return starCatalogue.asterisms();
    }
    
    /**
     * Retourne les indices de l'astérisme {@code asterism} donné du ciel observé
     * @param asterism
     *          l'astérisme donné
     * @return
     *          les indices de l'astérisme {@code asterism} donné du ciel observé
     */
    public List<Integer> asterismIndices(Asterism asterism) {
        return starCatalogue.asterismIndices(asterism);
    }
    
    
    /** BONUS
     * Retourne les coordonnées cartésiennes de l'objet céleste non-nul {@code celestialObject}.
     * @param celestialObject
     *          l'objet céleste en question
     * @return
     *          les coordonnées cartésiennes de l'objet céleste.
     */
    public CartesianCoordinates getPosition(CelestialObject celestialObject) {
        Objects.requireNonNull(celestialObject);
        
        
        // Un "workaround" de l'immuabilité des Planètes, du Soleil, et de la Lune.
        
        if (celestialObject.name() == sun.name()) {
            return celestialObjectMap.get(sun);
        }
        
        if (celestialObject.name() == moon.name()) {
            return celestialObjectMap.get(moon);
        }
        
        for (Planet planet : planets) {
            if (celestialObject.name() == planet.name()) {
                return celestialObjectMap.get(planet);
            }
        }
        
        return celestialObjectMap.get(celestialObject);
    }
    
    /** BONUS
     * Retourne la version "à jour" de l'objet céleste en question {@code celestialObject}.
     * @param celestialObject
     *          l'objet céleste en question
     * @return
     *          la version "à jour" de l'objet céleste
     */
    public CelestialObject getCurrent(CelestialObject celestialObject) {
        Objects.requireNonNull(celestialObject);
        
        
        // Un "workaround" de l'immuabilité des Planètes, du Soleil, et de la Lune.
        
        if (celestialObject.name() == sun.name()) {
            return sun;
        }
        
        if (celestialObject.name() == moon.name()) {
            return moon;
        }
        
        for (Planet planet : planets) {
            if (celestialObject.name() == planet.name()) {
                return planet;
            }
        }
        
        for (Star star : stars()) {
            if (star.hipparcosId() == ((Star)celestialObject).hipparcosId())
                return star;
        }
        
        return null;
    }
   
    private double distanceSquaredTo(CartesianCoordinates cartesianPos1, CartesianCoordinates cartesianPos2) {
        return Math.hypot(cartesianPos1.x() - cartesianPos2.x(), cartesianPos1.y() - cartesianPos2.y());
    }
    
    /**
     * Retourne l'objet céleste le plus proche des coordonnées cartésiennes {@code cartesianPos} dans 
     * un rayon {@code range} autour des coordonnées cartésiennes.
     * @param cartesianPos
     *          les coordonnées cartésiennes données
     * @param range
     *          le rayon donné
     * @return
     *          l'objet céleste le plus proche des coordonnées cartésiennes données.  
     */
    public Optional<CelestialObject> objectClosestTo(CartesianCoordinates cartesianPos, double range) {
        double rangeSquared = Math.pow(range, 2);
        double smallestDistance = rangeSquared;
        CelestialObject closestObject = null;

        // Sun
        double distanceToOtherObject = distanceSquaredTo(cartesianPos, sunPosition);
        if (smallestDistance > distanceToOtherObject) { 
            smallestDistance = distanceToOtherObject;   
            closestObject = sun;
        }
        
        // Moon
        distanceToOtherObject = distanceSquaredTo(cartesianPos, moonPosition);
        if (smallestDistance > distanceToOtherObject) {
            smallestDistance = distanceToOtherObject;
            closestObject = moon;
        }
        
        // Planets
        for (int i = 0; i < planetPositions.length; i += 2) {
            distanceToOtherObject = distanceSquaredTo(cartesianPos,
                    CartesianCoordinates.of(planetPositions[i], planetPositions[i + 1]));
            
            if (smallestDistance > distanceToOtherObject) {
                smallestDistance = distanceToOtherObject;
                closestObject = planets.get(i / 2);
            }
        }
        
        // Stars
        for (int i = 0; i < starPositions.length; i += 2) {
            distanceToOtherObject = distanceSquaredTo(cartesianPos,
                    CartesianCoordinates.of(starPositions[i], starPositions[i + 1]));
            
            if (smallestDistance > distanceToOtherObject) {
                smallestDistance = distanceToOtherObject;
                closestObject = stars().get(i / 2);
            }
        }

        
        // If there is no closest Object
        if (closestObject == null) {
            return Optional.empty();
        } else {
            return Optional.of(closestObject);
        }
    }

}
