package ch.epfl.rigel.astronomy;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Queue;

import org.junit.jupiter.api.Test;

class MyAsterismLoaderTest {

    private static final String HYG_CATALOGUE_NAME ="/hygdata_v3.csv";
    private static final String ASTERISM_CATALOGUE_NAME ="/asterisms.txt";
    
    @Test
    void MyAsterismLoaderWorksRight() throws IOException {
        
        try (InputStream hygStream = getClass().getResourceAsStream(HYG_CATALOGUE_NAME);
                InputStream astStream = getClass().getResourceAsStream(ASTERISM_CATALOGUE_NAME)) {
            
            StarCatalogue.Builder builder = new StarCatalogue.Builder();
            builder.loadFrom(hygStream, HygDatabaseLoader.INSTANCE)
                .loadFrom(astStream, AsterismLoader.INSTANCE);
            
            StarCatalogue catalogue = builder.build();
            
            Queue<Asterism> a = new ArrayDeque<>();
            Star betelgeuse = null;
            for (Asterism ast : catalogue.asterisms()) {
                for (Star s : ast.stars()) {
                    if (s.name().equalsIgnoreCase("Rigel")) {
                        a.add(ast);
                    }
                }
            }
            
            int astCount = 0;
            for (Asterism ast : a) {
                ++astCount;
                for (Star s : ast.stars()) {
                    if (s.name().equalsIgnoreCase("Betelgeuse")) {
                        betelgeuse = s;
                    }
                }
            }
            assertNotNull(betelgeuse);
            assertEquals(2, astCount);
            
          }
        
    }

}
