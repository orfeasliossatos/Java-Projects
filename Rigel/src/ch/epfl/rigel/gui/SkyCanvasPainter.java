package ch.epfl.rigel.gui;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import ch.epfl.rigel.astronomy.Asterism;
import ch.epfl.rigel.astronomy.CelestialObject;
import ch.epfl.rigel.astronomy.Moon;
import ch.epfl.rigel.astronomy.ObservedSky;
import ch.epfl.rigel.astronomy.Planet;
import ch.epfl.rigel.astronomy.Star;
import ch.epfl.rigel.astronomy.Sun;
import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Transform;

/**
 * Classe représentant un objet capable de dessiner le ciel sur un canevas
 * <p>
 * Publique, finale.
 * 
 * @author Orfeas Liossatos (310738)
 * @author Henrique Da Silva Gameiro (315689)
 */
public final class SkyCanvasPainter {

    private static final double ASTERISM_LINE_WIDTH = 1.0d;
    private static final Point2D OFFSCREEN_POINT    = new Point2D(-1d, -1d);
    
    private static final Color HALO_COLOR           = Color.YELLOW.deriveColor(0d, 1d, 1d, 0.25d);
    private static final double HALO_SCALE_FACTOR   = 2.2d;
    private static final double SURFACE_DILATION    = 2d;
    
    private static final int CARD_NUM                                   = 8;
    private static final double CARDINAL_VERT_OFFSET                    = -0.5d;
    private static final List<String> CARD_NAMES                        = new ArrayList<>();
    private static final List<HorizontalCoordinates> HORI_CARD_COORDS   = new ArrayList<>();
    
    private static final double HORIZON_LINE_WIDTH          = 2.0d;
    private static final HorizontalCoordinates HORIZON      = HorizontalCoordinates.of(0, 0);
    private static final ClosedInterval MAGNITUDE_INTERVAL  = ClosedInterval.of(-2.0, 5.0);
    
    /// BONUS 
    private static final double EARTH_TRANSPARENCY  = 0.6d;
    private static final double SMALL_ANGLE         = 0.0001;
    private static final Color SKY_COLOR            = Color.rgb(5, 5, 20);
    private static final Color EARTH_COLOUR         = Color.rgb(0, 0, 0, EARTH_TRANSPARENCY);
    
    private static final Color SELECTION_COLOUR      = Color.WHITE;
    private static final double SELECTION_LINE_WIDTH = 1.5d;
    private static final double SELECTION_RADIUS     = 10.0d;
    private static final double SELECTION_LENGTH     = SELECTION_RADIUS / 3.0d;

    private static final String FONT_AWESOME_NAME = "/Font Awesome 5 Free-Solid-900.otf";
    private static final double FONT_AWESOME_SIZE = 30d; 
    private static Font FONT_AWESOME;
    
    private static final String SUN_SYMBOL      = "\uf185";
    private static final String STAR_SYMBOL     = "\uf005";
    private static final String MOON_SYMBOL     = "\uf186";
    private static final String PLANET_SYMBOL   = "\uf7a2";

    private static final double INFO_TITLE_SCALE     = 1.6d;
    private static final double INFO_LEFT_MARGIN    = 20d;
    private static final double INFO_TOP_MARGIN     = 20d;
    private static final double INFO_HORI_SPACE     = 45d;
    private static final double INFO_VERT_SPACE     = 15d;
    
    
    private final Canvas canvas;
    private final GraphicsContext context;
    
    /**
     * Constructeur du SkyCanvasPainter. Construit un peintre d'étoiles 
     * étant donné le canevas {@code canvas} donné.
     * @param canvas
     */
    public SkyCanvasPainter(Canvas canvas) {
        this.canvas = canvas;
        this.context = canvas.getGraphicsContext2D();
        
        for (int i = 0; i < CARD_NUM; i++) {
            HORI_CARD_COORDS.add(HorizontalCoordinates.ofDeg(i * 45, CARDINAL_VERT_OFFSET));
            CARD_NAMES.add(HORI_CARD_COORDS.get(i).azOctantName("N", "E", "S", "O"));
        }
        

        try (InputStream fontStream = getClass().getResourceAsStream(FONT_AWESOME_NAME)) {
            FONT_AWESOME = Font.loadFont(fontStream, FONT_AWESOME_SIZE);
            fontStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Efface le canevas
     */
    public void clear() {
        context.setFill(SKY_COLOR);
        context.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }
    
    private double diameterFromMagnitude(double magnitude, StereographicProjection projection) {
        double clippedMagnitude = MAGNITUDE_INTERVAL.clip(magnitude);
        double sizeFactor = (99 - 17 * clippedMagnitude) / 140.0;
        double diameter = sizeFactor * projection.applyToAngle(Angle.ofDeg(0.5));
        
        return diameter;
    }
    
    /**
     * Dessine les astérismes et les étoiles du ciel observé {@code sky} sur le canevas étant donnés 
     * une projection stéréographique {@code projection} et une transformation du plan au canevas {@code planeToCanvas}.
     * @param sky
     *          le ciel observé contenant les astérismes et les étoiles
     * @param projection
     *          la projection stéréographique
     * @param planeToCanvas
     *          la transformation du plan 
     */
    public void drawStars(ObservedSky sky, StereographicProjection projection, Transform planeToCanvas) {
        
        /// TRANSFORMATION D'ETOILES ///
        
        // Diamètres d'étoiles
        List<Double> starDiameters = new ArrayList<Double>();
        for(Star star : sky.stars()) {
           starDiameters.add(
                   diameterFromMagnitude(
                           star.magnitude(), projection));
        }
        
        // Couleurs d'étoiles
        List<Color> starColors = new ArrayList<Color>();
        for(Star star : sky.stars()) {
            starColors.add(
                    BlackBodyColor.colorForTemperature(
                            star.colorTemperature()));  
        }

        // Transformer les positions des étoiles
        double[] starPositions = sky.starPositions();
        for(int i = 0, j = 1; j < starPositions.length; i += 2, j += 2)  {
            Point2D starTransPoint = planeToCanvas.transform(
                    starPositions[i],
                    starPositions[j]);
            starPositions[i] = starTransPoint.getX();
            starPositions[j] = starTransPoint.getY();
        }
        
        // Transformer les diamètres des étoiles
        for (int i = 0; i < starDiameters.size(); i++) {
            starDiameters.set(
                    i, planeToCanvas.deltaTransform(
                            starDiameters.get(i), 0).getX());
        }
        
        /// DESSIN ASTERISMES ///
        
        context.setStroke(Color.BLUE);
        context.setLineWidth(ASTERISM_LINE_WIDTH);
        
        // Pour chaque astérisme
        for (Asterism asterism : sky.asterisms()) {
            
            // Recommencer le chemin et présupposer l'existence d'un point précédent dans le chemin
            context.beginPath();
            Point2D oldStarPosition = OFFSCREEN_POINT;
            
            for (Integer index : sky.asterismIndices(asterism)) {
                
                // La prochaine position est cherchée
                Point2D newStarPosition = new Point2D(
                        starPositions[index * 2], 
                        starPositions[index * 2 + 1]);
                
                // Sauter au prochain point sans dessiner lorsqu'aucun des new/oldStarPosition ne sont visibles.
                if (!canvas.contains(newStarPosition) 
                        && !canvas.contains(oldStarPosition)) {
                    context.moveTo(
                            newStarPosition.getX(), 
                            newStarPosition.getY());
                } else {
                    context.lineTo(
                            newStarPosition.getX(), 
                            newStarPosition.getY()); 
                }
                
                oldStarPosition = newStarPosition;
            }

            context.stroke();
        }
        
        /// DESSIN D'ETOILES ///
        
        for (int i = 0; i < sky.stars().size(); ++i) {
            context.setFill(starColors.get(i));
            context.fillOval(
                    starPositions[2 * i]     - (starDiameters.get(i) / 2), 
                    starPositions[2 * i + 1] - (starDiameters.get(i) / 2), 
                    starDiameters.get(i), 
                    starDiameters.get(i));
        }
    }
    
    /**
     * Dessine les astérismes si {@code showAsterisms} et les étoiles du ciel observé {@code sky} si {@code showStars} sur le canevas étant donnés 
     * une projection stéréographique {@code projection} et une transformation du plan au canevas {@code planeToCanvas}.
     * @param sky
     *          le ciel observé contenant les astérismes et les étoiles
     * @param projection
     *          la projection stéréographique
     * @param planeToCanvas
     *          la transformation du plan 
     */
    public void drawStarsConditional(ObservedSky sky, StereographicProjection projection, Transform planeToCanvas,
            boolean showStars, boolean showAsterisms) {
        
        /// TRANSFORMATION D'ETOILES ///
        
        // Diamètres d'étoiles
        List<Double> starDiameters = new ArrayList<Double>();
        for(Star star : sky.stars()) {
           starDiameters.add(
                   diameterFromMagnitude(
                           star.magnitude(), projection));
        }
        
        // Couleurs d'étoiles
        List<Color> starColors = new ArrayList<Color>();
        for(Star star : sky.stars()) {
            starColors.add(
                    BlackBodyColor.colorForTemperature(
                            star.colorTemperature()));  
        }

        // Transformer les positions des étoiles
        double[] starPositions = sky.starPositions();
        for(int i = 0, j = 1; j < starPositions.length; i += 2, j += 2)  {
            Point2D starTransPoint = planeToCanvas.transform(
                    starPositions[i],
                    starPositions[j]);
            starPositions[i] = starTransPoint.getX();
            starPositions[j] = starTransPoint.getY();
        }
        
        // Transformer les diamètres des étoiles
        for (int i = 0; i < starDiameters.size(); i++) {
            starDiameters.set(
                    i, planeToCanvas.deltaTransform(
                            starDiameters.get(i), 0).getX());
        }
        
        /// DESSIN ASTERISMES ///
        
        context.setStroke(Color.BLUE);
        context.setLineWidth(ASTERISM_LINE_WIDTH);
        
        if (showAsterisms) {
            // Pour chaque astérisme
            for (Asterism asterism : sky.asterisms()) {
                
                // Recommencer le chemin et présupposer l'existence d'un point précédent dans le chemin
                context.beginPath();
                Point2D oldStarPosition = OFFSCREEN_POINT;
                
                for (Integer index : sky.asterismIndices(asterism)) {
                    
                    // La prochaine position est cherchée
                    Point2D newStarPosition = new Point2D(
                            starPositions[index * 2], 
                            starPositions[index * 2 + 1]);
                    
                    // Sauter au prochain point sans dessiner lorsqu'aucun des new/oldStarPosition ne sont visibles.
                    if (!canvas.contains(newStarPosition) 
                            && !canvas.contains(oldStarPosition)) {
                        context.moveTo(
                                newStarPosition.getX(), 
                                newStarPosition.getY());
                    } else {
                        context.lineTo(
                                newStarPosition.getX(), 
                                newStarPosition.getY()); 
                    }
                    
                    oldStarPosition = newStarPosition;
                }
    
                context.stroke();
            }
        }
        
        /// DESSIN D'ETOILES ///
        
        if (showStars) {
            for (int i = 0; i < sky.stars().size(); ++i) {
                context.setFill(starColors.get(i));
                context.fillOval(
                        starPositions[2 * i]     - (starDiameters.get(i) / 2), 
                        starPositions[2 * i + 1] - (starDiameters.get(i) / 2), 
                        starDiameters.get(i), 
                        starDiameters.get(i));
            }
        }
    }

    /**
     * Dessine les planètes du ciel observé {@code sky} sur le canevas étant donnés 
     * une projection stéréographique {@code projection} et une transformation du plan au canevas {@code planeToCanvas}.
     * @param sky
     *          le ciel observé contenant les planètes
     * @param projection
     *          la projection stéréographique
     * @param planeToCanvas
     *          la transformation du plan 
     */
    public void drawPlanets(ObservedSky sky, StereographicProjection projection, Transform planeToCanvas) {
        
        /// TRANSFORMATION DE PLANETES ///
        
        // Diamètres des planètes // 
        List<Double> planetDiameters = new ArrayList<Double>();
        for (Planet planet : sky.planets()) {
           planetDiameters.add(
                   diameterFromMagnitude(planet.magnitude(), projection));
        }
        
        // Transformer les positions des planètes
        double[] planetPositions = sky.planetPositions();
        for (int i = 0, j = 1; j < planetPositions.length; i += 2, j += 2)  {
            Point2D planetTransPoint = planeToCanvas.transform(
                    planetPositions[i], 
                    planetPositions[j]);
            planetPositions[i] = planetTransPoint.getX();
            planetPositions[j] = planetTransPoint.getY();
        }
         
        // Transformer les diamètres des étoiles
        for (int i = 0; i < planetDiameters.size(); i++) {
            planetDiameters.set(
                    i, planeToCanvas.deltaTransform(
                            planetDiameters.get(i), 0).getX());
        }
        
        /// DESSIN DES PLANETES ///
        
        context.setFill(Color.LIGHTGRAY);
        for (int i = 0; i < sky.planets().size(); ++i) {
            context.fillOval(
                    planetPositions[2 * i]     - (planetDiameters.get(i) / 2), 
                    planetPositions[2 * i + 1] - (planetDiameters.get(i) / 2), 
                    planetDiameters.get(i), 
                    planetDiameters.get(i));
        }
    }
    
    /**
     * Dessine le Soleil du ciel observé {@code sky} sur le canevas étant donnés 
     * une projection stéréographique {@code projection} et une transformation du plan au canevas {@code planeToCanvas}.
     * @param sky
     *          le ciel observé contenant le Soleil
     * @param projection
     *          la projection stéréographique
     * @param planeToCanvas
     *          la transformation du plan 
     */ 
    public void drawSun(ObservedSky sky, StereographicProjection projection, Transform planeToCanvas) {
        
        // Diamètre du Soleil
        double sunDiameter = projection.applyToAngle(
                Angle.ofDeg(0.5));
        
        // Transformer la position du Soleil
        Point2D sunTransPoint = planeToCanvas.transform(
                sky.sunPosition().x(),
                sky.sunPosition().y());
         
        // Transformer le diamètre du Soleil
        sunDiameter = planeToCanvas.deltaTransform(sunDiameter, 0).getX();
        

        /// DESSIN DU SOLEIL ///

        // Halo
        double haloDiameter = sunDiameter * HALO_SCALE_FACTOR; 
        
        context.setFill(HALO_COLOR);
        context.fillOval(
                sunTransPoint.getX() - haloDiameter / 2.0,
                sunTransPoint.getY() - haloDiameter / 2.0,
                haloDiameter,
                haloDiameter);
        
        // Surface
        double surfaceDiameter = sunDiameter + SURFACE_DILATION;
        
        context.fillOval(
                sunTransPoint.getX() - surfaceDiameter / 2.0,
                sunTransPoint.getY() - surfaceDiameter / 2.0,
                surfaceDiameter,
                surfaceDiameter);
        
        // Intérieur
        context.setFill(Color.WHITE);
        context.fillOval(
                sunTransPoint.getX() - sunDiameter / 2.0,
                sunTransPoint.getY() - sunDiameter / 2.0,
                sunDiameter,
                sunDiameter);
    }
        
    /**
     * Dessine le Soleil du ciel observé {@code sky} sur le canevas étant donnés 
     * une projection stéréographique {@code projection} et une transformation du plan au canevas {@code planeToCanvas}.
     * @param sky
     *          le ciel observé contenant le Soleil
     * @param projection
     *          la projection stéréographique
     * @param planeToCanvas
     *          la transformation du plan 
     */ 
    public void drawMoon(ObservedSky sky, StereographicProjection projection, Transform planeToCanvas) {
        // Diamètre de la Lune
        double moonDiameter = projection.applyToAngle(
                Angle.ofDeg(0.5));
        
        // Transformer la position de la Lune
        Point2D moonTransPoint = planeToCanvas.transform(
                sky.moonPosition().x(), 
                sky.moonPosition().y());
         
        // Transformer le diamètre de la Lune
        moonDiameter = planeToCanvas.deltaTransform(moonDiameter, 0).getX();
        
        /// DESSIN DE LA LUNE ///
        context.setFill(Color.WHITE);
        context.fillOval(
                moonTransPoint.getX() - moonDiameter / 2.0,
                moonTransPoint.getY() - moonDiameter / 2.0,
                moonDiameter,
                moonDiameter);
    }
    
    /** BONUS
     * Dessine l'horizon et les points cardinaux et intercardinaux.
     * @param projection
     *          la projection stéréographique
     * @param planeToCanvas
     *          la transformation du plan 
     */
    public void drawHorizon(StereographicProjection projection, Transform planeToCanvas) {
        
        // Rayon de l'horizon
        double horizonCircleDiameter = Math.abs(2 * 
                projection.circleRadiusForParallel(HORIZON));
        

        // Centre de l'horizon
        CartesianCoordinates horizonCircleCenter = 
                projection.circleCenterForParallel(HORIZON);
        
        // Centre transformée de l'horizon
        Point2D horizonCircleCenterTransPoint = 
                planeToCanvas.transform(
                        horizonCircleCenter.x(),
                        horizonCircleCenter.y());
        
        // Rayon transformé de l'horizon
        horizonCircleDiameter = planeToCanvas.deltaTransform(horizonCircleDiameter, 0).getX();
        
        /// DESSIN DE L'HORIZON ///
        
        context.setStroke(Color.RED);
        context.setLineWidth(HORIZON_LINE_WIDTH);
        
        double viewingAlt = projection.getProjectionCenter().alt();
        
        // Si on regarde tout droit, alors
        if (-SMALL_ANGLE <= viewingAlt && viewingAlt < SMALL_ANGLE) {
        
            context.strokeLine(
                    0, 
                    canvas.getHeight() / 2, 
                    canvas.getWidth(), 
                    canvas.getHeight() / 2);
            
        } else {
            context.strokeOval(
                    horizonCircleCenterTransPoint.getX() - horizonCircleDiameter / 2,
                    horizonCircleCenterTransPoint.getY() - horizonCircleDiameter / 2,
                    horizonCircleDiameter, 
                    horizonCircleDiameter);
        }
        
        // Calcul position des points cardinaux et intercardinaux
        context.setFill(Color.RED);
        context.setTextAlign(TextAlignment.CENTER);
        context.setTextBaseline(VPos.TOP);
        
        for (int i = 0; i < CARD_NUM; i++) {
            CartesianCoordinates cartCardCoords = projection.apply(
                    HORI_CARD_COORDS.get(i));
            
            Point2D cardinalTransPoint = planeToCanvas.transform(
                    cartCardCoords.x(),
                    cartCardCoords.y());
            
            context.fillText(
                    CARD_NAMES.get(i), 
                    cardinalTransPoint.getX(), 
                    cardinalTransPoint.getY());
        }
    }
    
    /** BONUS
     * Dessine la Terre sous l'horizon étant donnés
     * une projection stéréographique {@code projection} et une transformation du plan au canevas {@code planeToCanvas}.
     * @param projection
     *          la projection stéréographique
     * @param planeToCanvas
     *          la transformation du plan 
     */
    public void drawEarth(StereographicProjection projection, Transform planeToCanvas) {
     
        // Diamètre de la Terre
        double earthCircleDiameter = Math.abs(2 * 
                projection.circleRadiusForParallel(HORIZON));
        

        // Centre de la Terre
        CartesianCoordinates earthCircleCenter = 
                projection.circleCenterForParallel(HORIZON);
        
        // Centre transformée de la Terre
        Point2D earthCircleCenterTransPoint = 
                planeToCanvas.transform(
                        earthCircleCenter.x(),
                        earthCircleCenter.y());
        
        // Diamètre transformé de la Terre
        earthCircleDiameter = planeToCanvas.deltaTransform(earthCircleDiameter, 0).getX();
        
        /// DESSIN DE LA TERRE ///
        double viewingAlt = projection.getProjectionCenter().alt();
        
        // Si on regarde vers le bas, alors
        if (viewingAlt < -SMALL_ANGLE) {
            
            // La transparence de la Terre diminue plus on s'approche d'une hauteur d'observation de -90 degrés.
            double earthOpacityFactor = (1 - Math.abs(viewingAlt / (Angle.TAU / 4)));
            context.setFill(EARTH_COLOUR.deriveColor(0, 0, 0, earthOpacityFactor));
            
            // La Terre est un cercle ;)
            context.fillOval(
                    earthCircleCenterTransPoint.getX() - earthCircleDiameter / 2,
                    earthCircleCenterTransPoint.getY() - earthCircleDiameter / 2,
                    earthCircleDiameter, 
                    earthCircleDiameter);
             
        // Si on regarde droit, alors
        } else if (-SMALL_ANGLE <= viewingAlt && viewingAlt < SMALL_ANGLE) {
            context.setFill(EARTH_COLOUR);
            
            // La Terre est un rectangle ;)
            context.fillRect(
                    0, 
                    canvas.getHeight() / 2d, 
                    canvas.getWidth(), 
                    canvas.getHeight() / 2d);

        // Si on regarde vers le haut, alors
        } else {
            context.setFill(EARTH_COLOUR);
            
            // Le path "extérieur du cercle"
            context.beginPath();
            
            // On touche les quatres coins du canevas 
            context.moveTo(0, 0);
            context.lineTo(canvas.getWidth(), 0);
            context.lineTo(canvas.getWidth(), canvas.getHeight());
            context.lineTo(0, canvas.getHeight());
            context.lineTo(0, 0);
            
            // On se place sur un point du cercle
            context.lineTo(
                    earthCircleCenterTransPoint.getX() - earthCircleDiameter / 2, 
                    earthCircleCenterTransPoint.getY());
            
            // On trace le path du cercle
            context.arc(
                    earthCircleCenterTransPoint.getX(), 
                    earthCircleCenterTransPoint.getY(), 
                    earthCircleDiameter / 2, 
                    earthCircleDiameter / 2, 
                    180, 360);
            
            // On termine le path
            context.closePath();
            context.fill();
        }
    }
    
    /** BONUS
     * Dessine la sélection sur l'objet cliqué {@code clickedObject} du ciel observé {@code sky} sur le canevas étant donnés 
     * une projection stéréographique {@code projection} et une transformation du plan au canevas {@code planeToCanvas}.
     * @param sky
     *          le ciel observé
     * @param projection
     *          la projection stéréographique
     * @param planeToCanvas
     *          la transformation du plan 
     * @param clickedObject
     *          l'objet céleste cliqué
     */
    public void drawTracker(ObservedSky sky, StereographicProjection projection, Transform planeToCanvas, CelestialObject celestialObject) {
        
        /// TRANSFORMATION DE L'OBJET CLIQUE ///
        
        // Diamètre de l'objet cliqué
        double celestialObjectDiameter = 
                   diameterFromMagnitude(
                           celestialObject.magnitude(), projection);
        
        // Position de l'objet cliqué
        CartesianCoordinates celestialObjectPosition = sky.getPosition(celestialObject);

        // Transformer la position de l'objet cliqué
        Point2D clickedTransPoint = 
                planeToCanvas.transform(
                        celestialObjectPosition.x(),
                        celestialObjectPosition.y());
        
        // Transformer le diamètre de la sélection
        double selectionDiameter = 
                planeToCanvas.deltaTransform(
                            celestialObjectDiameter, 0).getX()
                                + SELECTION_RADIUS;
        
        
        /// DESSIN DE LA SELECTION ///
        
        context.setStroke(SELECTION_COLOUR);
        context.setLineWidth(SELECTION_LINE_WIDTH);
        
        // Commencer le chemin
        context.beginPath();
        
        // Les traits horizontaux
        context.moveTo(clickedTransPoint.getX() - selectionDiameter, clickedTransPoint.getY());
        context.lineTo(clickedTransPoint.getX() - selectionDiameter + SELECTION_LENGTH, clickedTransPoint.getY());
        
        context.moveTo(clickedTransPoint.getX() + selectionDiameter, clickedTransPoint.getY());
        context.lineTo(clickedTransPoint.getX() + selectionDiameter - SELECTION_LENGTH, clickedTransPoint.getY());
        
        // Les traits verticaux
        context.moveTo(clickedTransPoint.getX(), clickedTransPoint.getY() - selectionDiameter);
        context.lineTo(clickedTransPoint.getX(), clickedTransPoint.getY() - selectionDiameter + SELECTION_LENGTH);
        
        context.moveTo(clickedTransPoint.getX(), clickedTransPoint.getY() + selectionDiameter);
        context.lineTo(clickedTransPoint.getX(), clickedTransPoint.getY() + selectionDiameter - SELECTION_LENGTH);
        
        context.stroke();
    }
    
    /** BONUS
     * Dessine les informations sur l'objet cliqué {@code clickedObject} du ciel observé {@code sky} 
     * sur le canevas étant donné une projection stéréographique {@code projection}.
     * @param sky
     *          le ciel observé
     * @param projection
     *          la projection stéréographique
     * @param clickedObject
     *          l'objet céleste cliqué
     */
    public void drawInfo(ObservedSky sky, StereographicProjection projection, CelestialObject celestialObject) {
        
        
        // Measure the length of the box
        int vertScalar = 1;
        
        // Alignment
        context.setTextAlign(TextAlignment.LEFT);
        context.setTextBaseline(VPos.TOP);
        
        // Le symbole représentant l'objet
        context.setFill(Color.WHITE);
        context.setFont(FONT_AWESOME);
        
        String celestialObjectSymbol = new String();

        if (celestialObject.getClass() == Sun.class) {
            celestialObjectSymbol = SUN_SYMBOL;
        } else if (celestialObject.getClass() == Moon.class) {
            celestialObjectSymbol = MOON_SYMBOL;
        } else if (celestialObject.getClass() == Planet.class) {
            celestialObjectSymbol = PLANET_SYMBOL;
        } else if (celestialObject.getClass() == Star.class) {
            celestialObjectSymbol = STAR_SYMBOL;
        }

        context.fillText(celestialObjectSymbol, INFO_LEFT_MARGIN, INFO_TOP_MARGIN);
        
        // Le nom de l'objet
        context.setFont(Font.getDefault());
        context.scale(INFO_TITLE_SCALE, INFO_TITLE_SCALE);
        context.setFill(Color.WHITE);

        context.fillText(celestialObject.name(), 
                (INFO_LEFT_MARGIN + INFO_HORI_SPACE) / INFO_TITLE_SCALE, INFO_TOP_MARGIN / INFO_TITLE_SCALE);
        
        // Le type de l'objet
        context.setFill(Color.GREY);
        context.scale(1 / INFO_TITLE_SCALE, 1 / INFO_TITLE_SCALE);
        
        context.fillText(celestialObject.getClass().getSimpleName(),
                INFO_LEFT_MARGIN + INFO_HORI_SPACE, INFO_TOP_MARGIN + INFO_TITLE_SCALE * INFO_VERT_SPACE);
        
        // Informations générales aux objets célestes
        context.setFill(Color.WHITE);
        
        HorizontalCoordinates horizontalCoordinates = 
                projection.inverseApply(
                        sky.getPosition(celestialObject));
        vertScalar += 2;
        context.fillText(
                String.format("Magnitude\t%.2f\nRa/Dec\t\t%.2fh\t%.2f°\nAz/Hauteur\t%.2f°\t%.2f°\n", 
                        celestialObject.magnitude(), 
                        celestialObject.equatorialPos().raHr(), 
                        celestialObject.equatorialPos().decDeg(),
                        horizontalCoordinates.azDeg(),
                        horizontalCoordinates.altDeg()), 
                INFO_LEFT_MARGIN, INFO_TOP_MARGIN + INFO_VERT_SPACE * vertScalar);
        
        // Informations particulières aux sous-types d'objet céleste
        
        vertScalar += 3;
        if (celestialObject.name() == "Soleil") {
            context.fillText(
                    String.format("Mean Anom.\t%.2f\nType Spectral\t%s\nAge\t\t\t%s Milliards", 
                        ((Sun)celestialObject).meanAnomaly(),
                        ((Sun)celestialObject).spectralType(),
                        ((Sun)celestialObject).age()), 
                    INFO_LEFT_MARGIN, INFO_TOP_MARGIN + INFO_VERT_SPACE * vertScalar);
            
        } else if (celestialObject.name() == "Lune") {
            context.fillText(
                    String.format("Phase\t\t%.2f%%", 
                            ((Moon)celestialObject).phase() * 100), 
                    INFO_LEFT_MARGIN, INFO_TOP_MARGIN + INFO_VERT_SPACE * vertScalar);
            
        } else if (celestialObject.getClass() == Planet.class) {
            context.fillText(
                    String.format("Satellites\t\t%d", 
                            ((Planet)celestialObject).moons()), 
                    INFO_LEFT_MARGIN, INFO_TOP_MARGIN + INFO_VERT_SPACE * vertScalar);
            
        } else {
            context.fillText(
                    String.format("Hipparcos\t%d\nDistance\t\t%.2f parsec\nType Spectral\t%s", 
                        ((Star)celestialObject).hipparcosId(), 
                        ((Star)celestialObject).distance(),
                        ((Star)celestialObject).spectralType()), 
                    INFO_LEFT_MARGIN, INFO_TOP_MARGIN + INFO_VERT_SPACE * vertScalar);
        }
        
        // DESCRIPTIONS PARTICULIERES
        if (celestialObject.name() == "Soleil") {
            vertScalar += 4;
            context.fillText(((Sun)celestialObject).description(), INFO_LEFT_MARGIN, INFO_TOP_MARGIN + INFO_VERT_SPACE * vertScalar);            
        } else if (celestialObject.name() == "Lune") {
            vertScalar += 2;
            context.fillText(((Moon)celestialObject).description(), INFO_LEFT_MARGIN, INFO_TOP_MARGIN + INFO_VERT_SPACE * vertScalar);     
        } else if (celestialObject.getClass() == Planet.class) {
            vertScalar += 2;
            context.fillText(((Planet)celestialObject).description(), INFO_LEFT_MARGIN, INFO_TOP_MARGIN + INFO_VERT_SPACE * vertScalar);
        } 
    }
    
    /**
     * Efface le canevas, puis dessine les astérismes, les étoiles, les planètes, le soleil, la lune, et l'horizon
     * @param sky
     *          le ciel observé contenant le objets célestes
     * @param projection
     *          la projection stéréographique
     * @param planeToCanvas
     *          la transformation du plan 
     */
    public void drawAllConditional(ObservedSky sky, StereographicProjection projection, Transform planeToCanvas, CelestialObject celestialObject,
            boolean showStars, boolean showAsterisms, boolean showPlanets, boolean showSun, boolean showMoon, boolean showEarth, boolean showHorizon, boolean showInfo) {
        
        this.clear();
        
        this.drawStarsConditional(sky, projection, planeToCanvas, showStars, showAsterisms);
        
        if (showPlanets) this.drawPlanets(sky, projection, planeToCanvas);
        
        if (showSun) this.drawSun(sky, projection, planeToCanvas);
        
        if (showMoon) this.drawMoon(sky, projection, planeToCanvas);
        
        if (showEarth) this.drawEarth(projection, planeToCanvas);
        
        if (showHorizon) this.drawHorizon(projection, planeToCanvas);
        
        if (celestialObject != null) {
            this.drawTracker(sky, projection, planeToCanvas, celestialObject);
            
            if (showInfo) this.drawInfo(sky, projection, celestialObject);
        }
    }
    
}
