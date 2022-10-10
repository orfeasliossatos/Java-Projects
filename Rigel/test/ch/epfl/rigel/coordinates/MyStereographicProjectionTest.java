package ch.epfl.rigel.coordinates;

import static java.lang.Math.PI;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import ch.epfl.rigel.math.Angle;
import ch.epfl.test.TestRandomizer;

class MyStereographicProjectionTest {

    @Test
    void sterApplyWorksForKnownValues() {
        CartesianCoordinates c;
        
        HorizontalCoordinates center = HorizontalCoordinates.ofDeg(45, 45);
        
        StereographicProjection s = new StereographicProjection(center);
        
        c = s.apply(HorizontalCoordinates.ofDeg(45, 30));
        
        
        assertEquals(-0.13165249758739583, c.y());
    }
    
    @Test
    void sterInverseApplyWorksForKnownValues() {
        HorizontalCoordinates h;
        
        HorizontalCoordinates center = HorizontalCoordinates.ofDeg(45, 45);
        
        StereographicProjection s = new StereographicProjection(center);
        
        h = s.inverseApply(CartesianCoordinates.of(10, 0));
        
        assertEquals(3.648704634091643  , h.az());
    }
    
    @Test
    void sterApplyToAngleWorksForKnownValues() {
        HorizontalCoordinates center = HorizontalCoordinates.ofDeg(23, 45);
        
        StereographicProjection s = new StereographicProjection(center);
        
        assertEquals(0.00436333005262522, s.applyToAngle(Angle.ofDeg(0.5)));
    }
    
    @Test
    void sterCircleCenterWorksForKnownValues() {
        HorizontalCoordinates center = HorizontalCoordinates.ofDeg(45, 45);
        
        StereographicProjection s = new StereographicProjection(center);
        
        CartesianCoordinates circleCenter = s.circleCenterForParallel(HorizontalCoordinates.ofDeg(0, 27));

        assertEquals(0.6089987400733187, circleCenter.y());
    }
    
    @Test
    void sterCircleRadiusForParallel() {
        HorizontalCoordinates center = HorizontalCoordinates.ofDeg(45, 45);
        
        StereographicProjection s = new StereographicProjection(center);
        
        double r = s.circleRadiusForParallel(HorizontalCoordinates.ofDeg(0, 27));
        
        assertEquals(0.767383180397855, r);
    }
    
    
    @Test
    void stertoStringWorksWithValidCoordinates() {
        var rng = TestRandomizer.newRandom();
        for (int i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i++) {
            var az = rng.nextDouble(0, 2d * PI);
            var alt = rng.nextDouble(-PI / 2d, PI / 2d);
            var c = HorizontalCoordinates.of(az, alt);
            var s = new StereographicProjection(c);
            
            assertTrue(s.toString().equals("StereographicProjection with center "
                    + "(az=" + az + " rad, alt=" + alt + " rad)"));
        }
    }
    
    @Test
    void sterCircleCenterForParallelReturnsInfinity() {
        var rng = TestRandomizer.newRandom();
        for (int i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i++) {
            var az = rng.nextDouble(0, 2d * PI);
            var alt = rng.nextDouble(-PI / 2d, PI / 2d);
            var c = HorizontalCoordinates.of(az, alt);
            var s = new StereographicProjection(c);
            
            var h = HorizontalCoordinates.of(az, -alt);
            
            assertTrue((s.circleCenterForParallel(h).toString().equals("(x=0.0000, y=Infinity)")));
        }
    }

    @Test
    void sterCircleRadiusForParallelReturnsInfinity() {
        var rng = TestRandomizer.newRandom();
        for (int i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i++) {
            var az = rng.nextDouble(0, 2d * PI);
            var alt = rng.nextDouble(-PI / 2d, PI / 2d);
            var c = HorizontalCoordinates.of(az, alt);
            var s = new StereographicProjection(c);
            
            var h = HorizontalCoordinates.of(az, -alt);
            
            assertEquals(1.0 / 0.0, s.circleRadiusForParallel(h));
        }
    }

}
