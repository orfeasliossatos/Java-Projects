package ch.epfl.rigel.math;

import ch.epfl.rigel.Preconditions;

/**
 * Classe facilitant la conversion d'unités d'angle
 * <p>
 * Publique, finale, non-instanciable
 * 
 * @author Orfeas Liossatos (310738)
 * @author Henrique Da Silva Gameiro (315689)
 */
public final class Angle {

    /**
     * TAU = 6.2831853... soit {@code 2 * Math.PI}
     */
    public final static double TAU = 2 * Math.PI;
    
    private final static double DEG_PER_SEC = 1.0 / 3600.0;
    private final static double DEG_PER_MIN = 1.0 / 60.0;
    private final static double DEG_PER_HOUR = 15;
    private final static int SEC_PER_MIN = 60;
    
    private final static RightOpenInterval MOD_TAU_INTERVAL = RightOpenInterval.of(0, TAU);
    private final static RightOpenInterval MOD_60_INTERVAL = RightOpenInterval.of(0, SEC_PER_MIN);
    
    // Non-instanciable
    private Angle() {}
    
    /**
     * Normalise l'angle {@code rad} en le reduisant a l'intervalle {@code [0, TAU[}.
     *  
     * @param rad 
     *          l'angle
     * @return l'angle normalise
     */
    public static double normalizePositive(double rad) {
        return MOD_TAU_INTERVAL.reduce(rad);
    }
    
    /**
     * Retourne l'angle en radians correspondant au nombre de secondes d'arc donne (quelconque).
     * 
     * @param sec 
     *          les secondes d'arc
     * @return l'angle en radians
     */
    public static double ofArcsec(double sec) {
        return Math.toRadians(DEG_PER_SEC * sec);
    }
    
    /**
     * Retourne l'angle en radians correspondant a l'angle {@code deg°min'sec"}
     * 
     * @param deg
     *          les degres
     * @param min 
     *          les minutes d'arc
     * @param sec 
     *          les secondes d'arc
     * @return l'angle en radians
     * @throws IllegalArgumentException 
     *          si les minutes ou les secondes d'arc ne sont pas comprises dans l'intervalle {@code [0, 60[}. 
     */
    public static double ofDMS(int deg, int min, double sec) {
        Preconditions.checkArgument(deg >= 0);
        Preconditions.checkInInterval(MOD_60_INTERVAL, min);
        Preconditions.checkInInterval(MOD_60_INTERVAL, sec);
 
        return Math.toRadians(deg + DEG_PER_MIN * min + DEG_PER_SEC * sec);
    }
    
    /**
     * Retourne (approximativement) l'angle en radians correspondant a l'angle en degres donne
     * 
     * @param deg 
     *          les degres
     * @return l'angle en radians
     */
    public static double ofDeg(double deg) {
        return Math.toRadians(deg);
    }

    /**
     * Retourne (approximativement) l'angle en degres correspondant a l'angle en radians donne
     * 
     * @param rad 
     *          les radians
     * @return l'angle en degres
     */
    public static double toDeg(double rad) {
        return Math.toDegrees(rad);
    }
    
    /**
     * Retourne l'angle en radians correspondant a l'angle en heures d'arc donne
     * @param hr 
     *          les heures d'arc
     * @return l'angle en radians
     */
    public static double ofHr(double hr) {
        return Math.toRadians(DEG_PER_HOUR * hr);
    }
    
    /**
     * Retourne l'angle en heures d'arc correspondant a l'angle en radians donne
     * @param rad 
     *          les radians
     * @return l'angle en heures d'arc
     */
    public static double toHr(double rad) {
        return Math.toDegrees(rad) / DEG_PER_HOUR;
    }
}
