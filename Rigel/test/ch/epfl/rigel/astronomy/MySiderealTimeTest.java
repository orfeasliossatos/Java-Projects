package ch.epfl.rigel.astronomy;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.math.Angle;

class MySiderealTimeTest {

    @Test
    void greenwichWorksForKnownValues() {
        ZonedDateTime d = ZonedDateTime.of(
                LocalDate.of(1980, Month.APRIL, 22),
                LocalTime.of(14, 36, 51, 670000000),
                ZoneOffset.UTC);
        assertEquals(Angle.ofHr(4.668120), SiderealTime.greenwich(d), 1e-6);
    }
    
    @Test
    void greenwichWorksForLaterGreenwich() {
        ZonedDateTime d = ZonedDateTime.of(
                LocalDate.of(2020, Month.FEBRUARY, 24),
                LocalTime.of(19, 32, 33),
                ZoneOffset.UTC);
        ZonedDateTime dLater = ZonedDateTime.of(
                LocalDate.of(2020, Month.FEBRUARY, 25),
                LocalTime.of(19, 28, 37),
                ZoneOffset.UTC);
        assertEquals(SiderealTime.greenwich(dLater), SiderealTime.greenwich(d));
    }
    
    
    @Test
    void localWorksForKnownValues() {
        ZonedDateTime d = ZonedDateTime.of(
                LocalDate.of(1980, Month.APRIL, 22),
                LocalTime.of(14, 36, 51, 670000000),
                ZoneOffset.UTC);
        GeographicCoordinates g = GeographicCoordinates.ofDeg(10, 0);
        assertEquals(Angle.ofHr(4.668120) + Angle.ofDeg(10), SiderealTime.local(d, g), 1e-6);
    }


}
