package ch.epfl.rigel.coordinates;

import java.util.Locale;

import javafx.geometry.Point2D;

/**
 * Classe représentant des coordonnées cartésiennes.
 * <p>
 * Publique, finale, immuable.
 * 
 * @author Orfeas Liossatos (310738)
 * @author Henrique Da Silva Gameiro (315689)
 */
public final class CartesianCoordinates {
    
    private final double x; 
    private final double y;
    
    private CartesianCoordinates(double x, double y) {
        this.x = x;
        this.y = y;        
    }
    
    /**
     * Retourne les coordonnées cartésiennes d'abscisse et d'ordonnée données
     * 
     * @param x
     *          l'abscisse
     * @param y
     *          l'ordonnée
     * @return
     *          les coordonnées cartésiennes d'abscisse x et d'ordonnée y.         
     */
    public static CartesianCoordinates of(double x, double y) {
        return new CartesianCoordinates(x, y);
    }
    
    /**
     * Retourne les coordonnées cartésiennes d'abscisse et d'ordonnée données
     * 
     * @param point2D
     * 
     * @return
     *          les coordonnées cartésiennes d'abscisse x et d'ordonnée y.         
     */
    public static CartesianCoordinates ofPoint2D(Point2D point2D) {
        return new CartesianCoordinates(point2D.getX(), point2D.getY());
    }
    
    /**
     * Retourne l'abscisse.
     * @return
     *          l'abscisse.
     */
    public double x() {
        return this.x;
    }
    
    /**
     * Retourne l'ordonnée.
     * @return
     *          l'ordonnée.
     */
    public double y() {
        return this.y;
    }
    
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(x=%f, y=%f)", x(), y());
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
        throw new UnsupportedOperationException();
    }
    
}
