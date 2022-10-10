package ch.epfl.rigel.astronomy;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import java.time.*;
class MyEpochTest {

    @Test
    void daysUntilWorksForSameDay() {
        ZonedDateTime d = ZonedDateTime.of(
                LocalDate.of(2000, Month.JANUARY, 1), 
                LocalTime.of(12, 0), 
                ZoneOffset.UTC);
        assertEquals(0, Epoch.J2000.daysUntil(d));
    }
    
    @Test
    void daysUntilWorksForKnownValues() {
        ZonedDateTime d = ZonedDateTime.of(
                LocalDate.of(2000, Month.JANUARY, 3),
                LocalTime.of(18, 0),
                ZoneOffset.UTC);
        assertEquals(2.25, Epoch.J2000.daysUntil(d));
    }
    
    @Test
    void daysUntilWorksForAnteriorDates() {
        ZonedDateTime d = ZonedDateTime.of(
                LocalDate.of(1999, Month.DECEMBER, 31),
                LocalTime.of(12, 0),
                ZoneOffset.UTC);
        assertEquals(-1, Epoch.J2000.daysUntil(d));
    }
    
    @Test
    void julianCenturiesUntilWorksForKnownValues() {
        ZonedDateTime d = ZonedDateTime.of(
                LocalDate.of(2000, Month.JANUARY, 3),
                LocalTime.of(18, 0),
                ZoneOffset.UTC);
        assertEquals(0.00006160164271047228, Epoch.J2000.julianCenturiesUntil(d));
    }
    
    
    
}
