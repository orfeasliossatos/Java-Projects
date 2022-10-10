package ch.epfl.rigel.math;

/**
 * Intervalle mathématique.
 * <p>
 * Publique, abstraite, immuable.
 * 
 * @author Orfeas Liossatos (310738)
 * @author Henrique Da Silva Gameiro (315689)
 */
public abstract class Interval {

    private final double low;
    private final double high;
    
    /**
     * Constructeur d'Interval.
     * Construit un intervalle avec la borne inférieure et la borne supérieure données
     * 
     * @param low
     *          la borne supérieure
     * @param high
     *          la borne inférieure
     */
    protected Interval(double low, double high) {
        this.low = low;
        this.high = high;
    }
    
    /**
     * Retourne la borne inférieure de l'intervalle
     * @return 
     *          la borne inférieure de l'intervalle
     */
    public double low() {
        return low;
    }
    
    /**
     * Retourne la borne supérieure de l'intervalle
     * @return 
     *          la borne superieure de l'intervalle
     */
    public double high() {
        return high;
    }
    
    /**
     * Retourne la taille de l'intervalle.
     * @return 
     *          la taille de l'intervalle.
     */
    public double size() {
        return high - low;
    }
    
    /**
     * Retourne {@code true} si et seulement si la valeur appartient a l'intervalle
     * 
     * @param v
     *          la valeur
     * @return si la valeur appartient a l'intervalle, ou non.
     */
    public abstract boolean contains(double v);
    
    // Operations non prises en charge
    
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
