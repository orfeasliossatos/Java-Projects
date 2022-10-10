package ch.epfl.rigel;

import ch.epfl.rigel.math.Interval;

/**
 * Les preconditions qui doivent etre satisfaites avant l'appel d'une methode.
 * <p>
 * Publique, non-instanciable, finale.
 * @author Orfeas Liossatos (310738)
 * @author Henrique Da Silva Gameiro (315689)
 */
public final class Preconditions {

    // Non-instanciable
    private Preconditions() {}
    
    /**
     * Leve une exception si son argument est faux.
     * 
     * @param isTrue 
     *          l'argument
     * @throws IllegalArgumentException
     *          si son argument est faux.
     */
    public static void checkArgument(boolean isTrue) {
        if (!isTrue) {
            throw new IllegalArgumentException("The argument is false");
        }
    }
    
    /**
     * Lève une exception si la valeur n'appartient pas à l'intervalle.
     * 
     * @param interval
     *            l'intervalle
     * @param value
     *            la valeur
     * @return la valeur
     * @throws IllegalArgumentException
     *              si la valeur n'appartient pas à l'intervalle.
     */
    public static double checkInInterval(Interval interval, double value) {
        if (!interval.contains(value)) {
            throw new IllegalArgumentException("The interval does not contain the value");
        } else {
            return value;
        }
    }
}
