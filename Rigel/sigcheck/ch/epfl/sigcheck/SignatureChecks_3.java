package ch.epfl.sigcheck;

import ch.epfl.rigel.astronomy.Epoch;
import ch.epfl.rigel.astronomy.SiderealTime;
import ch.epfl.rigel.coordinates.*;

import java.time.ZonedDateTime;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

final class SignatureChecks_3 {
    @Test
    void checkEpoch() {
        double d;
        ZonedDateTime z = null;
        Epoch e = Epoch.J2000;
        e = Epoch.J2010;
        d = e.daysUntil(z);
        d = e.julianCenturiesUntil(z);
    }

    @Test
    void checkSiderealTime() {
        double d;
        ZonedDateTime z = null;
        GeographicCoordinates g = null;
        d = SiderealTime.greenwich(z);
        d = SiderealTime.local(z, g);
    }

    @Test
    void checkEclipticToEquatorialConversion() {
        ZonedDateTime z = null;
        EclipticToEquatorialConversion e = new EclipticToEquatorialConversion(z);
        Function<EclipticCoordinates, EquatorialCoordinates> f = e;
    }

    @Test
    void checkEquatorialToHorizontalConversion() {
        ZonedDateTime z = null;
        GeographicCoordinates g = null;
        EquatorialToHorizontalConversion e = new EquatorialToHorizontalConversion(z, g);
        Function<EquatorialCoordinates, HorizontalCoordinates> f = e;
    }
}
