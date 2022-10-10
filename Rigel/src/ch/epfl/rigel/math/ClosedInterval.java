package ch.epfl.rigel.math;

import java.util.Locale;

import ch.epfl.rigel.Preconditions;

/**
 * Intervalle fermé.
 * <p>
 * Publique, finale, immuable.
 * 
 * @author Orfeas Liossatos (310738)
 * @author Henrique Da Silva Gameiro (315689)
 */
public final class ClosedInterval extends Interval {

    /**
     * Constructeur par défaut de ClosedInterval. 
     * <p>
     * Bien que privé, cette classe reste instanciable,
     * car ce constructeur est appele par des methodes statiques.
     * 
     * @param low 
     *          la borne inferieure 
     * @param high
     *          la borne superieure
     */
    private ClosedInterval(double low, double high) {
        super(low, high);
    }

    /**
     * Retourne un intervalle ferme allant de low a high.
     * 
     * @param low
     *          la borne inferieure
     * @param high 
     *          la borne superieure
     * @return l'intervervalle ferme allant de low a high
     * @throws IllegalArgumentException 
     *          si low est plus grand ou egal a high.
     */
    public static ClosedInterval of(double low, double high) {
        Preconditions.checkArgument(low < high);
        
        return new ClosedInterval(low, high);
    }
    
    /**
     * Retourne un intervalle fermé symétrique centré en 0.
     * 
     * @param size 
     *          la taille de l'intervalle
     * @return l'intervalle fermé symétrique centré en 0
     * @throws IllegalArgumentException
     *          si size n'est pas strictement positive
     */
    public static ClosedInterval symmetric(double size) {
        Preconditions.checkArgument(size > 0);
        
        return new ClosedInterval(-size / 2.0, size / 2.0);
    }
    
    /**
     * La fonction d'ecretage correspondant a cet intervalle ferme.
     * <p>
     * {@code v <= low ? clip(v) = low}
     * <p>
     * {@code v >= high ? clip(v) = high}
     * <p>
     * {@code sinon, clip(v) = v}
     * @param v : L'argument
     * @return {@code clip(v)}
     * 
     */
    public double clip(double v) {
        if      (v <= low())  { return low(); } 
        else if (v >= high()) { return high(); }
        else                  { return v; }
        
    }
    
    /**
     * {@inheritDoc}
     * fermé.
     */
    @Override
    public boolean contains(double v) {
        return (low() <= v && v <= high());
    }
    
    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "[%f,%f]", low(), high());
    }
    
}
