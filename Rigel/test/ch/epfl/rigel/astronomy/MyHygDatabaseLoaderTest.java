package ch.epfl.rigel.astronomy;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.Test;

class MyHygDatabaseLoaderTest {
    private static final String HYG_CATALOGUE_NAME = "/hygdata_v3.csv";

    @Test
    void MyHygDatabaseLoaderAddsSpaceAfterEveryQuestionMark() throws IOException { 
        try (InputStream hygStream = getClass().getResourceAsStream(HYG_CATALOGUE_NAME)) {
            StarCatalogue catalogue = new StarCatalogue.Builder().loadFrom(hygStream, HygDatabaseLoader.INSTANCE).build();

            int i = 0;
            for (Star star : catalogue.stars()) {
                if (star.name().charAt(0) == '?') {
                    i = 1;
                    assertEquals(' ', star.name().charAt(1));
                }
            }
            assertEquals(1, i);
          }
    }

}
