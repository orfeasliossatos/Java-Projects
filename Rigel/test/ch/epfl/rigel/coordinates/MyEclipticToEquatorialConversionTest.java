package ch.epfl.rigel.coordinates;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.Angle;

class MyEclipticToEquatorialConversionTest {

    @Test
    void EclipticToEquatorialConversionWorksForKnownValues() {
        ZonedDateTime d = ZonedDateTime.of(
                LocalDate.of(2020, Month.FEBRUARY, 28),
                LocalTime.of(0, 0),
                ZoneOffset.UTC);
        
        EclipticToEquatorialConversion convertor = new EclipticToEquatorialConversion(d);
        
        EclipticCoordinates ecl = EclipticCoordinates.of(Angle.TAU / 8, Angle.TAU / 8);
        EquatorialCoordinates equ = convertor.apply(ecl);
        assertEquals(Angle.toDeg(Angle.ofDMS(57, 57, 20.57)), equ.decDeg(), 1e-6);
        
        ecl = EclipticCoordinates.of(Angle.TAU / 2, Angle.TAU / 4);
        equ = convertor.apply(ecl);
        assertEquals(Angle.toDeg(Angle.ofDMS(66, 33, 47.99)), equ.decDeg(), 1e-6);
    }

}
