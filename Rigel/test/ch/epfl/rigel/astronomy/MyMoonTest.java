package ch.epfl.rigel.astronomy;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.test.TestRandomizer;

class MyMoonTest {

    @Test
    void myMoonFailsForInvalidPhase() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Moon(EquatorialCoordinates.of(0, 0), 0, 0, -1);
        });
    }
    
    @Test 
    void myMoonSucceedsForValidPhases() {
        var rng = TestRandomizer.newRandom();
        for (int i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i++) {
            float phase = (float) rng.nextDouble(0, 1);
            new Moon(EquatorialCoordinates.of(0, 0), 0, 0, phase);
            
        }
    }
    
    @Test
    void myMoonInfoPrintsAsExpected() {
        Moon myMoon = new Moon(EquatorialCoordinates.of(0, 0), 0, 0, 0.3752f);
        System.out.println(myMoon.info());
        assertTrue(myMoon.info().equals("Lune (37.5%)"));
    }

}
