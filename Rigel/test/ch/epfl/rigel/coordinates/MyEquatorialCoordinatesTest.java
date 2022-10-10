package ch.epfl.rigel.coordinates;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import ch.epfl.rigel.math.Angle;

public class MyEquatorialCoordinatesTest {
    
    @Test
    void equatorialCoordinatesIsImmutable() {
        
    }
    
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
        EquatorialCoordinates equatorialcoords1 = EquatorialCoordinates.of(0, Angle.TAU / 4);
        assertTrue(equatorialcoords1.toString().equals("(ra=0.0000h, dec=90.0000°)"));

        EquatorialCoordinates equatorialcoords2 = EquatorialCoordinates.of(Angle.TAU / 2, 0);
        assertTrue(equatorialcoords2.toString().equals("(ra=12.0000h, dec=0.0000°)"));
    }
}
