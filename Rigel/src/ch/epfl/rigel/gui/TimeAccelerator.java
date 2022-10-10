package ch.epfl.rigel.gui;

import java.time.Duration;
import java.time.ZonedDateTime;

/**
 * Interface représentant un accélérateur de temps : une fonction permettant de calculer
 * le temps simulé en fonction du temps réel.
 * <p>
 * Publique.
 * 
 * @author Orfeas Liossatos (310738)
 * @author Henrique Da Silva Gameiro (315689)
 */
@FunctionalInterface
public interface TimeAccelerator {

    /**
     * Accélère le temps simulé
     * @param initialTime
     *          le temps simulé initial
     * @param timeSinceStart
     *          le temps réel écoulé depuis le début de l'animation
     * @return
     *          le temps simulé accéléré.
     */
    ZonedDateTime adjust(ZonedDateTime initialTime, long nanosSinceStart);
    
    /**
     * Retourne un accélérateur du temps qui accélère continûment le temps simulé 
     * par un facteur d'accélération entier {@code accelerationFactor}
     * @param accelerationFactor
     *          le facteur d'accélération du temps
     * @return
     *          un accélérateur du temps qui accélère continûment le temps simulé.
     *          
     */
    static TimeAccelerator continuous(int accelerationFactor) {
        
        return (ZonedDateTime initialTime, long nanosSinceStart) 
                -> initialTime.plusNanos(accelerationFactor * nanosSinceStart);
    }
    
    /**
     * Retourne un accélérateur du temps qui accélère par pas discrets de durée 
     * {@code timeStep} le temps simulé avec une fréquence d'avancements {@code advanceFrequency} 
     * @param advanceFrequency
     *          la fréquence d'avancements
     * @param timeStep
     *          la durée d'un pas
     * @return
     *          un accélérateur du temps qui accélère par pas discrets le temps simulé.
     */
    static TimeAccelerator discrete(int advanceFrequency, Duration timeStep) {
        double nanosPerSecond = 1e9;
        
        return (ZonedDateTime initialTime, long nanosSinceStart) 
                -> initialTime.plusNanos(
                        Math.round(advanceFrequency * nanosSinceStart / nanosPerSecond) * timeStep.toNanos());
    }
}
