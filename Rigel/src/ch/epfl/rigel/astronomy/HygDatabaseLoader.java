package ch.epfl.rigel.astronomy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import ch.epfl.rigel.astronomy.StarCatalogue.Builder;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;

/**
 * Type énuméré représentant un chargeur de catalogue HYG.
 * <p>
 * Publique, immuable.
 * 
 * @author Orfeas Liossatos (310738)
 * @author Henrique Da Silva Gameiro (315689)
 */
public enum HygDatabaseLoader implements StarCatalogue.Loader {
    
    /**
     * Un chargeur de catalogue HYG
     */
    INSTANCE;

    // Indices de colonnes
    private static final int HIP_INDEX      = 1;
    private static final int PROPER_INDEX   = 6; 
    private static final int DIST_INDEX     = 9;
    private static final int MAG_INDEX      = 13; 
    private static final int SPEC_INDEX     = 15;
    private static final int CI_INDEX       = 16;
    private static final int RARAD_INDEX    = 23;
    private static final int DECRAD_INDEX   = 24; 
    private static final int BAYER_INDEX    = 27;
    private static final int CON_INDEX      = 29;
    
    /**
     * Charge les étoiles du flot d'entrée {@code inputStream} et les ajoute
     * au catalogue en cours de construction du bâtisseur {@code builder}, 
     * ou lève IOException en cas d'erreur d'entrée/sortie.
     * @param inputStream
     *          le flot d'entrée
     * @param builder
     *          le bâtisseur de catalogue d'étoiles
     * @throws IOException
     *          en cas d'erreur d'entrée/sortie.
     */
    @Override
    public void load(InputStream inputStream, Builder builder) throws IOException {
        try (InputStreamReader inputReader = new InputStreamReader(inputStream, StandardCharsets.US_ASCII);
                BufferedReader buffReader = new BufferedReader(inputReader)) {
            
            int hip;        // Le numéro hipparcos
            String proper;  // Le nom propre
            String bayer;   // L'appélation bayer
            double mag;     // La magnitude
            double ci;      // L'indice de couleur
            double dist;    // La distance
            String spec;    // Le type spectral
            
            String line = buffReader.readLine(); // Skip the fist line
            while ((line = buffReader.readLine()) != null) {
                
                String features[] = line.split(",");
                
                // Les valeurs qui ont des valeurs par défaut
                hip     = (features[HIP_INDEX].isBlank()) ? 0 
                            : Integer.parseInt(features[HIP_INDEX]); 
                
                bayer   = (features[BAYER_INDEX].isBlank()) ? "?" 
                            : features[BAYER_INDEX];
                
                proper  = (features[PROPER_INDEX].isBlank()) ? bayer + " " + features[CON_INDEX] 
                            : features[PROPER_INDEX];

                mag     = (features[MAG_INDEX]).isBlank() ? 0
                            : Double.parseDouble(features[MAG_INDEX]);
                
                ci      = (features[CI_INDEX]).isBlank() ? 0
                            : Double.parseDouble(features[CI_INDEX]);
                
                dist    = (features[DIST_INDEX]).isBlank() ? 0
                            : Double.parseDouble(features[DIST_INDEX]);
                
                spec    = (features[SPEC_INDEX]).isBlank() ? "?"
                            : features[SPEC_INDEX];
                
                builder.addStar(new Star(
                        hip, 
                        proper, 
                        EquatorialCoordinates.of(
                                Double.parseDouble(features[RARAD_INDEX]), 
                                Double.parseDouble(features[DECRAD_INDEX])), 
                        (float) mag, 
                        (float) ci,
                        (float) dist,
                        spec));
            }
        }
    }
}
