package ch.epfl.rigel.astronomy;

import static org.junit.jupiter.api.Assertions.*;

import java.time.*;

import org.junit.jupiter.api.Test;

import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.test.TestRandomizer;

class MySunModelTest {

    @Test
    void mySunModelProducesExpectedValues() {
        Sun sun = SunModel.SUN.at(-2349,
                new EclipticToEquatorialConversion(
                        ZonedDateTime.of(LocalDate.of(2003, Month.JULY, 27),
                                LocalTime.of(0, 0, 0, 0), ZoneOffset.UTC)));
        
        assertEquals(8.3926828082978, sun.equatorialPos().raHr(), 1e-12);
        assertEquals(19.35288373097352, sun.equatorialPos().decDeg(), 1e-12);
        
        Sun sun2 = SunModel.SUN.at(27 + 31, 
                new EclipticToEquatorialConversion(
                        ZonedDateTime.of(LocalDate.of(2010,  Month.FEBRUARY, 27),
                                LocalTime.of(0,0), ZoneOffset.UTC)));
        
        assertEquals(5.9325494700300885, sun2.equatorialPos().ra());
        
        ZonedDateTime zone1988 = ZonedDateTime.of(
                LocalDate.of(1988,Month.JULY,27),
                LocalTime.of(0,0),ZoneOffset.UTC);
        Sun sun3 = SunModel.SUN.at(Epoch.J2010.daysUntil(zone1988), 
                new EclipticToEquatorialConversion(zone1988));
        
        assertEquals(0.009162353351712227, sun3.angularSize());
        
   }
    
    @Test
    void mySunModelWorksForRandomValues() {
        var rng = TestRandomizer.newRandom();
        for (int i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i++) {
            double daysSinceJ2010 = rng.nextDouble(-1000, 1000);
            
            SunModel.SUN.at(daysSinceJ2010,
                    new EclipticToEquatorialConversion(
                            ZonedDateTime.of(LocalDate.of(rng.nextInt(2010, 2020), Month.NOVEMBER, rng.nextInt(1, 28)),
                                    LocalTime.of(rng.nextInt(0, 24), 0, 0, 0), ZoneOffset.UTC)));
        }
    }

}
