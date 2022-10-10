package ch.epfl.rigel.astronomy;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.Test;

class HygDatabaseLoaderTest {
    private static final String HYG_CATALOGUE_NAME =
      "/hygdata_v3.csv";

    @Test
    void hygDatabaseIsCorrectlyInstalled() throws IOException {
      try (InputStream hygStream = getClass()
         .getResourceAsStream(HYG_CATALOGUE_NAME)) {
        assertNotNull(hygStream);
      }
    }

    @Test
    void hygDatabaseContainsRigel() throws IOException {
      try (InputStream hygStream = getClass().getResourceAsStream(HYG_CATALOGUE_NAME)) {
        StarCatalogue catalogue = new StarCatalogue.Builder().loadFrom(hygStream, HygDatabaseLoader.INSTANCE).build();
        Star rigel = null;
        for (Star s : catalogue.stars()) {
            if (s.name().equalsIgnoreCase("rigel"))
                rigel = s;
        }
        assertNotNull(rigel);
      }
    }
  }