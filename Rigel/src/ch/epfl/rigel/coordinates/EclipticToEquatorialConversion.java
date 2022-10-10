package ch.epfl.rigel.coordinates;

import java.time.ZonedDateTime;
import java.util.function.Function;

import ch.epfl.rigel.astronomy.Epoch;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.Polynomial;

/**
 * Un changement de système de coordonnées depuis les coordonnées écliptiques 
 * vers les coordonnées équatoriales, à un instant donné. 
 * <p>
 * Publique, finale, immuable.
 * 
 * @author Orfeas Liossatos (310738)
 * @author Henrique Da Silva Gameiro (315689)
 */
public final class EclipticToEquatorialConversion implements Function<EclipticCoordinates, EquatorialCoordinates> {
    
    // Calculés à la construction
    private final double cosObliquity;
    private final double sinObliquity;
    
    private static final Polynomial OBLIQUITY_POLYNOMIAL = Polynomial.of(
            Angle.ofArcsec(0.00181), 
            Angle.ofArcsec(-0.0006), 
            Angle.ofArcsec(-46.815), 
            Angle.ofDMS(23, 26, 21.45));
    
    /**
     * Le constructeur par défaut de EclipticToEquatorialConversion. Construit un changement de système
     * de coordonnées entre les coordonnées écliptiques et les coordonnées équatoriales 
     * pour l'instant when.
     * @param when
     *          l'instant qu'on considère.
     */
    public EclipticToEquatorialConversion(ZonedDateTime when) {
        // L'obliquité de l'ecliptique.
        double obliquity = OBLIQUITY_POLYNOMIAL.at(
                Epoch.J2000.julianCenturiesUntil(when));
        
        this.cosObliquity = Math.cos(obliquity);
        this.sinObliquity = Math.sin(obliquity);
    }
    
    /**
     * Convertit les coordonnées écliptiques en coordonnées équatoriales ecl.
     * @param ecl
     *          les coordonnées écliptiques qu'on considère.
     * @return les coordonnées équatoriales correspondant aux coordonnées écliptiques ecl.
     */
    @Override
    public EquatorialCoordinates apply(EclipticCoordinates ecl) {        
        // L'ascension droite
        double ra = Math.atan2(Math.sin(ecl.lon()) * cosObliquity - Math.tan(ecl.lat()) * sinObliquity, 
                Math.cos(ecl.lon()));
        
        // La déclinaison
        double dec = Math.asin(Math.sin(ecl.lat()) * cosObliquity 
                + Math.cos(ecl.lat()) * sinObliquity * Math.sin(ecl.lon()));
        
        // Réduit aux intervalles de coordonnées équatoriales
        if (ra < 0) { ra += Angle.TAU; }
        
        ra = Angle.normalizePositive(ra);
        
        return EquatorialCoordinates.of(ra, dec);
    }
    
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
