package ch.epfl.rigel.coordinates;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import ch.epfl.rigel.math.Angle;


public class MyEclipticCoordinatesTest {

    @Test
    void ofFailsForValuesNotInInterval() {
        assertThrows(IllegalArgumentException.class, () -> {
            HorizontalCoordinates.of(-1, 0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            HorizontalCoordinates.of(Angle.TAU, 0);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            HorizontalCoordinates.of(0, Angle.TAU / 4 + 0.00001);
        });
    }
    
    @Test
    void toStringPrintsCorrectly() {
        EclipticCoordinates eclipcoords1 = EclipticCoordinates.of(0, Angle.TAU / 4);
        assertTrue(eclipcoords1.toString().equals("(λ=0.0000°, β=90.0000°)"));
    }
}
