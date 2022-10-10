package ch.epfl.rigel.math;

import ch.epfl.rigel.Preconditions;

/**
 * Une fonction polynomiale
 * <p>
 * Publique, non-heritable, immuable
 * 
 * @author Orfeas Liossatos (310738)
 * @author Henrique Da Silva Gameiro (315689)
 */
public final class Polynomial {

    private final double[] coefficients;
    
    /**
     * Constructeur par defaut de Polynomial. 
     * <p>
     * Bien que prive, cette classe reste instanciable,
     * car ce constructeur est appelé par une methodes statiques.
     * 
     * @param coefficients
     *          les coefficients de la fonction polynomiale 
     */
    private Polynomial(double[] coefficients) {
        this.coefficients = new double[coefficients.length];
        
        // L'immuabilite est garantie.
        System.arraycopy(coefficients, 0, this.coefficients, 0, coefficients.length);
    }
    
    /**
     * Retourne une fonction polynomiale avec les coefficients donnes, ordonnes par degre
     * en ordre decroissant.
     * 
     * @param coefficientN
     *          le coefficient de plus haut degre
     * @param coefficients 
     *          les coefficients de la fonction polynomiale
     * @return la fonction polynomiale avec les coefficients donnes
     * @throws IllegalArgumentException
     *          si le coefficient de plus haut degre {@code coefficientN} vaut 0.
     */
    public static Polynomial of(double coefficientN, double... coefficients) {
        Preconditions.checkArgument(coefficientN != 0);

        // Prepend 
        double[] prependArray = new double[coefficients.length + 1];
      
        prependArray[0] = coefficientN;
        
        for (int i = 0; i < coefficients.length; i++) {
            prependArray[i + 1] = coefficients[i];
        }
        
        return new Polynomial(prependArray);
    }
   
    
    /**
     * Retourne la valeur de la fonction polynomiale pour l'argument {@code x}.
     * 
     * @param x
     *          l'argument
     * @return la valeur de la fonction polynomiale
     */
    public double at(double x) {
        double value = coefficients[0];
        
        for (int i = 1; i < coefficients.length; i++) {
            value *= x;
            value += coefficients[i];
        }
        
        return value;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        
        for (int i = 0; i < coefficients.length; i++) {
            // La puissance du terme
            int power = (coefficients.length - 1) - i; // Si n coefficients, alors degré vaut n-1
            
            // Si le coefficient n'est pas nul
            if (coefficients[i] != 0) {
                // Le coefficient
                if (coefficients[i] != 1) {
                    builder.append((coefficients[i] == -1 ) ? "-" : coefficients[i]);
                }
                
                // Le variable
                if (power >= 1) {
                    builder.append("x");   
                }
                
                // La puissance
                if (power >= 2) {
                    builder.append("^").append(power);
                }
            } 
            
            // Le plus
            if (power >= 1 && coefficients[i + 1] > 0) {
                builder.append("+");                
            }
        }
        
        return builder.toString();
    }
    
    // Operations non pris en charge
    
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
