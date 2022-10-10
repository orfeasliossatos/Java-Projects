package ch.epfl.rigel.gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.stream.Collectors;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.math.ClosedInterval;
import javafx.scene.paint.Color;

/**
 * Classe offrant une méthode permettant d'obtenir la couleur d'un corps noir étant donnée sa température.
 * <p>
 * Publique, finale, non-instanciable
 * 
 * @author Orfeas Liossatos (310738)
 * @author Henrique Da Silva Gameiro (315689)
 */
public final class BlackBodyColor {

    private static final int DEG_BEGIN_INDEX = 1;
    private static final int DEG_END_INDEX = 6;
    
    private static final int CMF_BEGIN_INDEX = 10;
    private static final int CMF_END_INDEX = 15;
    
    private static final int RGB_BEGIN_INDEX = 80;
    private static final int RGB_END_INDEX = 87;
    
    private static final String BBR_COLOR_CATALOGUE_NAME = "/bbr_color.txt";
        
    private static final Map<Integer, Color> COLOR_TEMP_MAP = initializeColorTemperatureMap();
    
    private static final ClosedInterval COLOR_TEMP_INTERVAL = ClosedInterval.of(1000, 40000);
    
    private BlackBodyColor() {};
    
    private static Map<Integer, Color> initializeColorTemperatureMap() {
        try (BufferedReader buffReader = 
                new BufferedReader(
                        new InputStreamReader(
                                BlackBodyColor.class.getResourceAsStream(
                                        BBR_COLOR_CATALOGUE_NAME)))) {
            
            Map<Integer, Color> colorTempMap =
                    buffReader.lines()
                    .filter( l -> !(l.charAt(0) == '#' || "2deg".equals(l.substring(CMF_BEGIN_INDEX + 1, CMF_END_INDEX))))
                    .collect(Collectors.toMap(
                            l -> Integer.parseInt(
                                    (l.charAt(1) == ' ') ? l.substring(DEG_BEGIN_INDEX + 1, DEG_END_INDEX) 
                                            : l.substring(DEG_BEGIN_INDEX, DEG_END_INDEX)),
                            l -> Color.web(l.substring(RGB_BEGIN_INDEX, RGB_END_INDEX))));
            
            return colorTempMap;
            
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    /**
     * Retourne la couleur d'un corps noir étant donné sa température de couleur {@code colorTemperature}
     * @param colorTemperature
     *          la température de couleur d'un corps noir.
     * @return
     *          la couleur d'un corps noir 
     */
    public static Color colorForTemperature(double colorTemperature) {
        Preconditions.checkInInterval(COLOR_TEMP_INTERVAL, colorTemperature);
        
        int colorTempRounded = ((int) Math.round(colorTemperature / 100.0) * 100); 
        
        return COLOR_TEMP_MAP.get(colorTempRounded);
    }
}
