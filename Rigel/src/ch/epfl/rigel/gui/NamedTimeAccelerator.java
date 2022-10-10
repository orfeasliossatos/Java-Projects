package ch.epfl.rigel.gui;

import java.time.Duration;

/**
 * Type énuméré représentant un accélérateur de temps donné.
 * <p>
 * Publique.
 * 
 * @author Orfeas Liossatos (310738)
 * @author Henrique Da Silva Gameiro (315689)
 */
public enum NamedTimeAccelerator {

    TIMES_1     ("1x",      TimeAccelerator.continuous(1)),
    TIMES_30    ("30x",     TimeAccelerator.continuous(30)),
    TIMES_300   ("300x",    TimeAccelerator.continuous(300)),
    TIMES_3000  ("3000x",   TimeAccelerator.continuous(3000)),
    
    DAY         ("jour",         TimeAccelerator.discrete(60, Duration.ofDays(1))),
    SIDEREAL_DAY("jour sidéral", TimeAccelerator.discrete(60, Duration.parse("PT23H56M4S")));
    
    private NamedTimeAccelerator(String name, TimeAccelerator accelerator) {
        this.name = name;
        this.accelerator = accelerator;        
    }
    
    private final String name;
    private final TimeAccelerator accelerator;
    
    
    /**
     * Retourne le nom du NamedTimeAccelerator
     * @return
     *          le nom du NamedTimeAccelerator
     */
    public String getName() {
        return name;
    }
    
    /**
     * Retourne l'accélérateur du NamedTimeAccelerator
     * @return
     *          l'accélérateur du NamedTimeAccelerator
     */
    public TimeAccelerator getAccelerator() {
        return accelerator;
    }
    
    @Override
    public String toString() {
        return getName();
    }
    
}
