package ch.epfl.rigel.astronomy;

import java.util.List;

import ch.epfl.rigel.Preconditions;

/**
 * Classe représentant un astérisme.
 * <p>
 * Publique, finale, immuable.
 * 
 * @author Orfeas Liossatos (310738)
 * @author Henrique Da Silva Gameiro (315689)
 */
public final class Asterism {
    
    private final List<Star> stars;
    
    /**
     * Constructeur de Asterism. Construit un astérisme composé de la liste
     * d'étoiles données {@code stars}, ou lève IllegalArgumentException si celle-ci est vide.
     * @param stars
     *          les étoiles de l'asterisme
     * @throws IllegalArgumentException
     *          si {@code stars} est vide.
     */
    public Asterism(List<Star> stars) {
        Preconditions.checkArgument(!stars.isEmpty());

        this.stars = List.copyOf(stars);   
    }
    
    /**
     * Retourne la liste des étoiles formant l'astérisme.
     * @return
     *          la liste des étoiles formant l'astérisme.
     */
    public List<Star> stars() {
        return stars; // L'immuabilité est garantie par List.copyOf() dans le constructeur.
    }
    
}
