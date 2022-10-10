package ch.epfl.rigel.coordinates;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import ch.epfl.rigel.math.Angle;

public class MyHorizontalCoordinatesTest {

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
    void ofDegFailsForValuesNotInInterval() {
        assertThrows(IllegalArgumentException.class, () -> {
            HorizontalCoordinates.ofDeg(-1, 0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            HorizontalCoordinates.ofDeg(360, 0);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            HorizontalCoordinates.ofDeg(0, 91);
        });
    }
    
    @Test
    void azOctantNamePrintsCorrectly() {
        assertEquals("N", HorizontalCoordinates.ofDeg(337.5, 0).azOctantName("N", "E", "S", "W"));
        assertEquals("N", HorizontalCoordinates.ofDeg(0, 0).azOctantName("N", "E", "S", "W"));
        
        assertEquals("NE", HorizontalCoordinates.ofDeg(22.5, 0).azOctantName("N", "E", "S", "W"));
        assertEquals("NE", HorizontalCoordinates.ofDeg(45, 0).azOctantName("N", "E", "S", "W"));
        
        assertEquals("E", HorizontalCoordinates.ofDeg(67.5, 0).azOctantName("N", "E", "S", "W"));
        assertEquals("E", HorizontalCoordinates.ofDeg(90, 0).azOctantName("N", "E", "S", "W"));
        
        assertEquals("SE", HorizontalCoordinates.ofDeg(112.5, 0).azOctantName("N", "E", "S", "W"));
        assertEquals("SE", HorizontalCoordinates.ofDeg(135, 0).azOctantName("N", "E", "S", "W"));
        
        assertEquals("S", HorizontalCoordinates.ofDeg(157.5, 0).azOctantName("N", "E", "S", "W"));
        assertEquals("S", HorizontalCoordinates.ofDeg(180, 0).azOctantName("N", "E", "S", "W"));
        
        assertEquals("SW", HorizontalCoordinates.ofDeg(202.5, 0).azOctantName("N", "E", "S", "W"));
        assertEquals("SW", HorizontalCoordinates.ofDeg(225, 0).azOctantName("N", "E", "S", "W"));
        
        assertEquals("W", HorizontalCoordinates.ofDeg(247.5, 0).azOctantName("N", "E", "S", "W"));
        assertEquals("W", HorizontalCoordinates.ofDeg(270, 0).azOctantName("N", "E", "S", "W"));
        
        assertEquals("NW", HorizontalCoordinates.ofDeg(292.5, 0).azOctantName("N", "E", "S", "W"));
        assertEquals("NW", HorizontalCoordinates.ofDeg(315, 0).azOctantName("N", "E", "S", "W"));
    }
    
    @Test
    void checkAngularDistanceForKnownValues() {

        HorizontalCoordinates horicoords1 = HorizontalCoordinates.ofDeg(6.5682, 46.5183);
        HorizontalCoordinates horicoords2 = HorizontalCoordinates.ofDeg(8.5476, 47.3763);
        
        assertEquals(0.027935461189288496, horicoords1.angularDistanceTo(horicoords2));
    }
    
    @Test
    void toStringPrintsCorrectly() {
        HorizontalCoordinates horicoords1 = HorizontalCoordinates.ofDeg(350, 7.2);
        assertTrue(horicoords1.toString().equals("(az=350.0000°, alt=7.2000°)"));

        HorizontalCoordinates horicoords2 = HorizontalCoordinates.of(0, Angle.TAU / 4);
        assertTrue(horicoords2.toString().equals("(az=0.0000°, alt=90.0000°)"));
    }
}
