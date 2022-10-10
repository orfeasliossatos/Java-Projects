package ch.epfl.rigel.astronomy;

import static org.junit.jupiter.api.Assertions.*;

import java.time.*;

import org.junit.jupiter.api.Test;

import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.math.Angle;
import ch.epfl.test.TestRandomizer;

class MyPlanetModelTest {

    @Test
    void myPlanetModelWorksForKnownValues() {
        Planet jupiter = PlanetModel.JUPITER.at(-2231.0,
                new EclipticToEquatorialConversion(
                        ZonedDateTime.of(LocalDate.of(2003, Month.NOVEMBER, 22),
                                LocalTime.of(0, 0, 0, 0), ZoneOffset.UTC)));
   
        // CORRECT
        assertEquals(11.18715493470968, jupiter.equatorialPos().raHr(), 1e-10);
        assertEquals(6.35663550668575 , jupiter.equatorialPos().decDeg(), 1e-10);
        assertEquals(35.11141185362771, Angle.toDeg(jupiter.angularSize()) * 3600);
        assertEquals(-1.9885659217834473, jupiter.magnitude());
        
        Planet mercury = PlanetModel.MERCURY.at(-2231.0,
                new EclipticToEquatorialConversion(
                        ZonedDateTime.of(LocalDate.of(2003, Month.NOVEMBER, 22),
                                LocalTime.of(0, 0, 0, 0), ZoneOffset.UTC)));

        assertEquals(16.8200745658971 , mercury.equatorialPos().raHr(), 1e-12);
        assertEquals(-24.500872462861 , mercury.equatorialPos().decDeg(), 1e-12);
    }
    
    @Test
    void myPlanetModelWorksForRandomValues() {
        var rng = TestRandomizer.newRandom();
        for (int i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i++) {
            double daysSinceJ2010 = rng.nextDouble(-1000, 1000);
            
            PlanetModel.ALL.get(rng.nextInt(0, 2)).at(daysSinceJ2010,
                    new EclipticToEquatorialConversion(
                            ZonedDateTime.of(LocalDate.of(rng.nextInt(2010, 2020), Month.NOVEMBER, rng.nextInt(1, 28)),
                                    LocalTime.of(rng.nextInt(0, 24), 0, 0, 0), ZoneOffset.UTC)));

            PlanetModel.ALL.get(rng.nextInt(3, 7)).at(daysSinceJ2010,
                    new EclipticToEquatorialConversion(
                            ZonedDateTime.of(LocalDate.of(rng.nextInt(2010, 2020), Month.NOVEMBER, rng.nextInt(1, 28)),
                                    LocalTime.of(rng.nextInt(0, 24), 0, 0, 0), ZoneOffset.UTC)));
        }
    }
}
