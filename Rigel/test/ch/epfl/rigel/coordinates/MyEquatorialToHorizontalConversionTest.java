package ch.epfl.rigel.coordinates;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

import ch.epfl.rigel.astronomy.SiderealTime;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.coordinates.EquatorialToHorizontalConversion;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.math.Angle;

class MyEquatorialToHorizontalConversionTest {

    @Test
    void EquatorialToHorizontalConversionWorksForKnownValues() {
        ZonedDateTime d = ZonedDateTime.of(
                LocalDate.of(2020, Month.FEBRUARY, 29),
                LocalTime.of(0, 0),
                ZoneOffset.UTC);
        GeographicCoordinates g = GeographicCoordinates.ofDeg(45, 0);
        
        System.out.print(SiderealTime.local(d, g));
        
        EquatorialToHorizontalConversion convertor = new EquatorialToHorizontalConversion(d, g);
        
        EquatorialCoordinates equ = EquatorialCoordinates.of(Angle.TAU / 4, Angle.TAU / 4);
        HorizontalCoordinates hor = convertor.apply(equ);
        
        assertEquals(0, hor.az(), 1e-6);
        assertEquals(0, hor.alt(), 1e-6);
    }
}
