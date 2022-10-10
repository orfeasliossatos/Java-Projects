package ch.epfl.rigel.coordinates;

import java.util.function.Function;

import ch.epfl.rigel.math.Angle;

/**
 * Classe représentant une projection stéréographique de coordonnées horizontales.
 * <p>
 * Publique, finale, immuable.
 * 
 * @author Orfeas Liossatos (310738)
 * @author Henrique Da Silva Gameiro (315689)
 */
public final class StereographicProjection implements Function<HorizontalCoordinates, CartesianCoordinates> {
   
    private final double azProjCenter;  // azimut du centre de la projection stéréographique
    private final double altProjCenter; // hauteur du centre de la projection stéréographique
    
    // Valeurs aidant au calcul, calculés à la construction
    private final double cosAltProjCenter; // cos(alt)
    private final double sinAltProjCenter; // sin(alt)
    
    /**
     * Constructeur de StereographicProjection.
     * Retourne la projection stéréographique centrée en center.
     * @param center
     *          les coordonnées horizontales en lesquelles est centrée la projection stéréographique.
     */
    public StereographicProjection(HorizontalCoordinates center) {
        azProjCenter = center.az();
        altProjCenter = center.alt();
        
        cosAltProjCenter = Math.cos(center.alt());
        sinAltProjCenter = Math.sin(center.alt());
    }
    
    
    /** BONUS
     * Retourne les coordonnées horizontales du centre de la projection stéréographique 
     * @return
     *          les coordonnées horizontales du centre de la projection stéréographique
     */
    public HorizontalCoordinates getProjectionCenter() {
        return HorizontalCoordinates.of(azProjCenter, altProjCenter);
    }
    
    /**
     * Retourne les coordonnées du centre du cercle correspondant à la projection
     * du parallèle passant par le point de coordonnées horizontales hor. L'ordonnée de ce centre peut être infinie.
     * @param hor
     *          le point par lequel passe le parallèle
     * @return
     *          les coordonnées du centre du cercle correspondant à la projection
     *          du parallèle passant par le point de coordonnées horizontales hor.
     */
    public CartesianCoordinates circleCenterForParallel(HorizontalCoordinates hor) {
        double yCircleCenter = cosAltProjCenter / (Math.sin(hor.alt()) + sinAltProjCenter);
        return CartesianCoordinates.of(0.0, yCircleCenter);
    }
    
    /**
     * Retourne le rayon du cercle correspondant à la projection 
     * du parallèle passant par le point de coordonnées horizontales parallel. Ce rayon peut être infini.
     * @param parallel
     *          le point par lequel passe le parallèle
     * @return
     *          le rayon du cercle correspondant à la projection 
     *          du parallèle passant par le point de coordonnées horizontales parallel
     */
    public double circleRadiusForParallel(HorizontalCoordinates parallel) {
       return Math.cos(parallel.alt()) / (Math.sin(parallel.alt()) + sinAltProjCenter);
    }
    
    /**
     * Retourne le diamètre projeté d'une sphère de taille angulaire rad centrée au centre de projection, en admettant que 
     * celui-ci soit sur l'horizon.
     * @param rad
     *          La taille angulaire (diamètre apparent) de l'objet céleste.
     * @return
     *          Retourne le diamètre projeté d'une sphère de taille angulaire rad centrée au centre de projection.
     */
    public double applyToAngle(double rad) {
        return 2 * Math.tan(rad / 4);
    }
   
    /**
     * Retourne les coordonnées cartésiennes de la projection du point de coordonnées horizontales azAlt.
     * 
     * @param azAlt
     *          les coordonnées horizontales 
     * @return
     *          les coordonnées cartésiennes de la projection du point de coordonnées horizontales.
     */
    @Override
    public CartesianCoordinates apply(HorizontalCoordinates azAlt) {
        // Variables intermédiaires
        double cosAlt = Math.cos(azAlt.alt());
        double sinAlt = Math.sin(azAlt.alt());
        
        double azDelta = azAlt.az() - azProjCenter;
        double cosAzDelta = Math.cos(azDelta);
        double d = 1 / (1 + sinAlt * sinAltProjCenter + cosAlt * cosAltProjCenter * cosAzDelta);
        
        // Coordonnées cartésiennes
        double x = d * cosAlt * Math.sin(azDelta);
        double y = d * (sinAlt * cosAltProjCenter - cosAlt * sinAltProjCenter * cosAzDelta);
        
        return CartesianCoordinates.of(x, y);
    }
    
    /**
     * Retourne les coordonnées horizontales du point dont la projection est le point de coordonnées cartésiennes xy.
     * @param xy
     *          les coordonnées cartésiennes 
     * @return
     *          les coordonnées horizontales du point dont la projection est le point de coordonnées cartésiennes.
     */
    public HorizontalCoordinates inverseApply(CartesianCoordinates xy) {
        if (xy.x() == 0 && xy.y() == 0) {           
            return HorizontalCoordinates.of(azProjCenter, altProjCenter);
        } else {
            // Valeurs intermédiaires
            double radSquared = Math.pow(xy.x(), 2) + Math.pow(xy.y(), 2);
            double rad = Math.sqrt(radSquared);
            
            double sinc = (2 * rad) / (radSquared + 1);
            double cosc = (1 - radSquared) / (radSquared + 1);
           
            // Coordonnées horizontales
            double az = Math.atan2(xy.x() * sinc, rad * cosAltProjCenter * cosc - xy.y() * sinAltProjCenter * sinc) + azProjCenter;
            double alt = Math.asin(cosc * sinAltProjCenter + ((xy.y() * sinc * cosAltProjCenter) / rad));
            
            if (az < 0) { az += Angle.TAU; }
            
            az = Angle.normalizePositive(az);
            
            return HorizontalCoordinates.of(az, alt);
        }
    }

    @Override
    public String toString() {
        return String.format("StereographicProjection with center (az= %f rad, alt= &f rad)", 
                azProjCenter, altProjCenter);
    }
    
    // Opérations non prises en charge
    
    /**
     * @see Object#hashCode()
     */
    @Override
    public final int hashCode() {
        throw new UnsupportedOperationException();
    }
    
    /**
     * @see Object#equals(Object)
     */
    @Override
    public final boolean equals(Object obj) {
        //throw new UnsupportedOperationException();
        return super.equals(obj);
    }
}
