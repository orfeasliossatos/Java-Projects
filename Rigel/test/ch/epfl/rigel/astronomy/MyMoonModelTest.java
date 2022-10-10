package ch.epfl.rigel.astronomy;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;

class MyMoonModelTest {

    @Test
    void test() {
        Moon moon = MoonModel.MOON.at(-2313, 
                new EclipticToEquatorialConversion(
                        ZonedDateTime.of(LocalDate.of(2003,  Month.SEPTEMBER, 1),
                                LocalTime.of(0,0), ZoneOffset.UTC)));
        
        assertEquals(14.211456457836, moon.equatorialPos().raHr(), 1e-12);
        assertEquals(-0.20114171346019355, moon.equatorialPos().dec(), 1e-12);
        
        Moon moon2 = MoonModel.MOON.at(Epoch.J2010.daysUntil(
                ZonedDateTime.of(LocalDate.of(1979, 9, 1),LocalTime.of(0, 0), ZoneOffset.UTC)), 
                    new EclipticToEquatorialConversion(ZonedDateTime.of(
                        LocalDate.of(1979, 9, 1),LocalTime.of(0, 0),ZoneOffset.UTC)));
        
        assertEquals(0.009225908666849136, moon2.angularSize());
        
        Moon moon3 = MoonModel.MOON.at(Epoch.J2010.daysUntil(ZonedDateTime.of(LocalDate.of(2003, 9, 1),LocalTime.of(0, 0),
                ZoneOffset.UTC)), new EclipticToEquatorialConversion(ZonedDateTime.of( LocalDate.of(2003, 9, 1),
                        LocalTime.of(0, 0),ZoneOffset.UTC)));
        
        assertEquals("Lune (22.5%)", moon3.info());
    }

}
