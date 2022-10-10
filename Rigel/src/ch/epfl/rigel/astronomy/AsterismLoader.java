package ch.epfl.rigel.astronomy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.rigel.astronomy.StarCatalogue.Builder;

/**
 * Type énuméré représentant un chargeur de catalogue d'astérismes.
 * <p>
 * Publique, immuable.
 * 
 * @author Orfeas Liossatos (310738)
 * @author Henrique Da Silva Gameiro (315689)
 */
public enum AsterismLoader implements StarCatalogue.Loader {
    
    /**
     * Un chargeur de catalogue d'astérismes
     */
    INSTANCE;

    /**
     * Charge les astérismes du flot d'entrée {@code inputStream} et les ajoute
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

            
            Map<Integer, Star> hipStarMap = new HashMap<>();
            for (Star star : builder.stars()) {
                hipStarMap.put(star.hipparcosId(), star);
            }
            
            String line;
            while ((line = buffReader.readLine()) != null) {
                
                String[] hipStrings = line.split(",");
                
                List<Star> asterismStars = new ArrayList<Star>();
                
                // Récupère les numéros hipparcos du fichier
                List<Integer> hipInts = new ArrayList<Integer>();
                for (String hipString : hipStrings) {
                    hipInts.add(Integer.parseInt(hipString));
                }
                
                // En présumant que le builder a chargé les étoiles, ajouter ceux dont le numéro hipparcos correspond.
                for (Integer hipInt : hipInts) {
                    asterismStars.add(
                            hipStarMap.get(hipInt));
                }

                builder.addAsterism(new Asterism(asterismStars));
            }
        }
    }
}
