package ch.epfl.rigel.math;

import java.util.Locale;

import ch.epfl.rigel.Preconditions;

/**
 * Intervalle semi-ouvert à droite. 
 * <p>
 * Publique, finale, immuable.
 * 
 * @author Orfeas Liossatos (310738)
 * @author Henrique Da Silva Gameiro (315689)
 */
public final class RightOpenInterval extends Interval {

    private RightOpenInterval(double low, double high) {
        super(low, high);
    }

    /**
     * Retourne un intervalle semi-ouvert à droite allant de low à high.
     * 
     * @param low
     *          la borne inférieure
     * @param high
     *          la borne supérieure
     * @return 
     *          l'intervalle semi-ouvert à droite
     * @throws IllegalArgumentException
     *          si low est plus grand ou égal à high.
     */
    public static RightOpenInterval of(double low, double high) {
        Preconditions.checkArgument(low < high);

        return new RightOpenInterval(low, high);
    }

    /**
     * Retourne un intervalle semi-ouvert a droite symetrique centre en 0.
     * 
     * @param size
     *            : La taille de l'intervalle
     * @return L'intervalle semi-ouvert a droite
     * @throws IllegalArgumentException
     *             : Si size n'est pas strictement positive
     */
    public static RightOpenInterval symmetric(double size) {
        Preconditions.checkArgument(size > 0);

        return new RightOpenInterval(-size / 2.0, size / 2.0);
    }

    /**
     * Le reste de la partie entière par défaut.
     * 
     * @param x
     *          le premier argument
     * @param y
     *          le deuxième argument
     * @return 
     *          Le reste de la partie entière par defaut.
     */
    private double floorMod(double x, double y) {
        return x - y * Math.floor(x / y);
    }

    /**
     * Retourne le résultat de la fonction de réduction correspondant à cet intervalle.
     * 
     * @param v
     *          l'argument
     * @return
     *          le résultat de la fonction de réduction correspondant à cet intervalle.
     */
    public double reduce(double v) {
        return low() + floorMod(v - low(), size());
    }

    /**
     * {@inheritDoc}
     * semi-ouvert à droite
     */
    @Override
    public boolean contains(double v) {
        return (v >= low() && v < high());
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "[%f,%f[", low(), high());
    }

}
