package ch.epfl.rigel.coordinates;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;


class MyGeographicCoordinatesTest {

    @Test
    void ofDegFailsForValuesNotInInterval() {
        assertThrows(IllegalArgumentException.class, () -> {
            GeographicCoordinates.ofDeg(-1, 0);
        });


        assertThrows(IllegalArgumentException.class, () -> {
            GeographicCoordinates.ofDeg(360, 0);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            GeographicCoordinates.ofDeg(0, 91);
        });
    }
    
    @Test
    void ofDegSucceedsForValuesInInterval() {
        GeographicCoordinates.ofDeg(0, -90);
        GeographicCoordinates.ofDeg(359.9999, 90);
    }
    
    @Test
    void toStringPrintsCorrectly() {
        GeographicCoordinates geocoords1 = GeographicCoordinates.ofDeg(0, 46.52);
        assertTrue(geocoords1.toString().equals("(lon=0.0000°, lat=46.5200°)"));

        GeographicCoordinates geocoords2 = GeographicCoordinates.ofDeg(359, 46.52343434);
        assertTrue(geocoords2.toString().equals("(lon=359.0000°, lat=46.5234°)"));
    }

}
