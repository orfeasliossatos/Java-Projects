package ch.epfl.rigel.astronomy;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import ch.epfl.rigel.Preconditions;

/**
 * Classe représentant un catalogue d'étoiles et d'astérismes.
 * <p>
 * Publique, finale, immuable.
 * 
 * @author Orfeas Liossatos (310738)
 * @author Henrique Da Silva Gameiro (315689)
 */
public final class StarCatalogue {

    private final List<Star> stars;
    private final Map<Asterism, List<Integer>> asterismsWithIndices;
    
    /**
     * Constructeur de StarCatalogue. Construit un catalogue d'étoiles {@code stars} et d'astérismes {@code asterisms},
     * ou lève IllegalArgumentException si un des astérismes contient une étoile qui ne fait pas partie de la liste d'étoiles.
     * @param stars
     *          la liste d'étoiles du catalogue.
     * @param asterisms
     *          la list d'astérismes du catalogue.
     * @throws IllegalArgumentException
     *          si un des astérismes contient une étoile qui ne fait pas partie de la liste d'étoiles.
     */
    public StarCatalogue(List<Star> stars, List<Asterism> asterisms) {
        // Precondition : asterism.stars() are included in stars
        for (Asterism asterism : asterisms) {
            Preconditions.checkArgument(
                    stars.containsAll(
                            asterism.stars()));               
        }
        // Copy stars list
        this.stars = List.copyOf(stars);

        // Compute asterismsWithIndices
        this.asterismsWithIndices = new HashMap<>();
        
        Map<Star, Integer> starIndexMap = new HashMap<>();
        for (int i = 0; i < stars.size(); i++) {
            starIndexMap.put(stars.get(i), i);
        }

        for (Asterism asterism : asterisms) {   // Pour chaque astérisme
            List<Integer> starIndicies = new ArrayList<Integer>();

            for (Star star : asterism.stars()) { // Pour chaque étoile dans l'astérisme
                starIndicies.add(starIndexMap.get(star));  
            }
            
            this.asterismsWithIndices.put(asterism, starIndicies);
        }
    }
    
    /**
     * Retourne la liste des étoiles du catalogue.
     * @return
     *          la liste des étoiles du catalogue.
     */
    public List<Star> stars() {
        return stars;
    }
    
    /**
     * Retourne l'ensemble des astérismes du catalogue.
     * @return
     *          l'ensemble des astérismes du catalogue.
     */
    public Set<Asterism> asterisms() {
       return Set.copyOf(asterismsWithIndices.keySet());
    }
    
    /**
     * Retourne la liste des index--dans le catalogue--des étoiles contituant l'astérisme donné,
     * ou lève IllegalArgumentException si l'astérisme donné ne fait pas partie du catalogue.
     * @param asterism
     * @return IllegalArgumentException
     *      la liste des index des étoiles constituant l'astérisme donné.
     */
    public List<Integer> asterismIndices(Asterism asterism) {
        List<Integer> asterismIndices = asterismsWithIndices.get(asterism);
        
        Preconditions.checkArgument(Objects.nonNull(asterismIndices));
        
        return List.copyOf(asterismIndices);
    }
    
    /**
     * Interface représentant un chargeur de catalogue d'étoiles et d'astérismes.
     * 
     * @author Orfeas Liossatos (310738)
     * @author Henrique Da Silva Gameiro (315689)
     */
    public interface Loader {
        
        /**
         * Charge les étoiles et/ou astérismes du flot d'entrée {@code inputStream} et les ajoute
         * au catalogue en cours de construction du bâtisseur {@code builder}, 
         * ou lève IOException en cas d'erreur d'entrée/sortie.
         * @param inputStream
         *          le flot d'entrée
         * @param builder
         *          le bâtisseur de catalogue d'étoiles
         * @throws IOException
         *          en cas d'erreur d'entrée/sortie.
         */
        public abstract void load(InputStream inputStream, Builder builder) throws IOException;
    }
    
    /**
     * Classe représentant un bâtisseur de catalogue d'étoiles.
     * <p>
     * Publique, finale.
     * 
     * @author Orfeas Liossatos (310738)
     * @author Henrique Da Silva Gameiro (315689)
     */
    public final static class Builder {

        private List<Star> stars;
        private List<Asterism> asterisms;
        
        /**
         * Constructeur de Starcatalogue.Builder.
         * Construit un bâtisseur de catalogue d'étoiles.
         */
        public Builder() {
            stars = new ArrayList<>();
            asterisms = new ArrayList<>();
        }
        
        /**
         * Ajoute l'étoile donnée au catalogue en cours de construction, et retourne le bâtisseur
         * @param star
         *          l'étoile donnée
         * @return
         *          le bâtisseur de catalogue d'étoiles.
         */
        public Builder addStar(Star star) {
            stars.add(star);
            return this;
        }
        
        /**
         * Retourne une vue non modifiable sur les étoiles du catalogue en cours de construction.
         * @return
         *          une vue non modifiable sur les étoiles du catalogue en cours de construction
         */
        public List<Star> stars() {
            return Collections.unmodifiableList(stars);
        }
        
        /**
         * Ajoute l'astérisme donné au catalogue en cours de construction et retourne le bâtisseur.
         * @param asterism
         *          l'astérisme donné
         * @return
         *          le bâtisseur de catalogue d'étoiles.
         */
        public Builder addAsterism(Asterism asterism) {
            asterisms.add(asterism);
            return this;
        }

        /**
         * Retourne une vue non modifiable sur les astérismes du catalogue en cours de construction.
         * @return
         *          une vue non modifiable sur les astérismes du catalogue en cours de construction.
         */
        public List<Asterism> asterisms() {
            return Collections.unmodifiableList(asterisms);
        }
        
        /**
         * Demande au chargeur {@code loader} d'ajouter au catalogue les étoiles et/ou astérismes qu'il
         * obtient depuis le flot d'entrée {@code inputStream}, et retourne le bâtisseur, ou lève IOException
         * en cas d'erreur d'entrée/sortie.
         * @param inputStream
         *          le flot d'entrée
         * @param loader
         *          le chargeur de catalogue d'étoiles et d'astérismes
         * @return
         *          le bâtisseur de catalogue d'étoiles
         * @throws IOException
         *          s'il y a erreur d'entrée/sortie.
         */
        public Builder loadFrom(InputStream inputStream, Loader loader) throws IOException {
            loader.load(inputStream, this);
            return this;
        }
        
        /**
         * Retourne le catalogue contenant les étoiles et astérismes ajoutés jusqu'alors au bâtisseur.
         * @return
         *          le catalogue contenant les étoiles et astérismes ajoutés jusqu'alors au bâtisseur.
         */
        public StarCatalogue build() {
            return new StarCatalogue(stars, asterisms);
        }
    }
}

