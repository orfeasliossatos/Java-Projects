package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.CelestialObject;
import ch.epfl.rigel.astronomy.ObservedSky;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.canvas.Canvas;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;

/**
 * Classe représentant un gestionnaire de canevas sur lequel le ciel est dessiné.
 * <p>
 * Publique, immuable.
 * 
 * @author Orfeas Liossatos (310738)
 * @author Henrique Da Silva Gameiro (315689)
 */
public final class SkyCanvasManager {

    // Attributs statiques
    private static final int DEFAULT_WIDTH                          = 800;
    private static final int DEFAULT_HEIGHT                         = 600;
    private static final double CLOSEST_RANGE                       = 100d;
    private static final CartesianCoordinates INIT_MOUSE_POSITION   = CartesianCoordinates.of(0d, 0d);
    
    private static final int AZ_STEP    = 10;
    private static final int ALT_STEP   = 5;

    private static final RightOpenInterval AZ_DEG_INTERVAL  = RightOpenInterval.of(0, 360);
    private static final ClosedInterval ALT_DEG_INTERVAL    = ClosedInterval.of(-90, 90);
    private static final ClosedInterval FOV_DEG_INTERVAL    = ClosedInterval.of(30, 150);
    
    // Attributs privés
    private Canvas canvas;
    private SkyCanvasPainter painter;

    // Propriétés et liens externes 
    private DoubleBinding mouseAzDeg;
    private DoubleBinding mouseAltDeg;
    private ObjectBinding<CelestialObject> objectUnderMouse;
    private ObjectProperty<CelestialObject> clickedObject = new SimpleObjectProperty<>(); // BONUS
    
    // Propriétés et liens internes
    private ObjectBinding<StereographicProjection> projection; 
    private DoubleBinding dilationFactor;
    private ObjectBinding<Transform> planeToCanvas;
    private ObjectBinding<ObservedSky> observedSky;
    private ObjectProperty<CartesianCoordinates> mousePosition = new SimpleObjectProperty<>(INIT_MOUSE_POSITION);
    private ObjectBinding<HorizontalCoordinates> mouseHorizontalPosition;
    
    
    /**
     * Constructeur de SkyCanvasManager. Construit un gestionnaire de canevas étant donné
     * le catalogue d'étoiles {@code catalogue}, l'instant d'observation {@code dateTimeB},
     * la position de l'observateur {@code observerLocationB}, et les paramètres d'observation
     * {@code viewingParametersB} donnés.
     * @param catalogue
     *          le catalogue d'étoiles
     * @param dateTimeB
     *          l'instant d'observation
     * @param observerLocationB
     *          la position de l'observateur
     * @param viewingParametersB
     *          les paramètres d'observation
     */
    public SkyCanvasManager(StarCatalogue catalogue, DateTimeBean dateTimeB,
            ObserverLocationBean observerLocationB, ViewingParametersBean viewingParametersB, MiscBean miscB) {
        
        /*
         * Le canevas
         */
        canvas = new Canvas(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        
        
        /*
         * Le peintre du canevas
         */
        painter = new SkyCanvasPainter(canvas);
        
        
        /*
         * Un lien contenant la projection stéréographique à utiliser
         */
        projection = Bindings.createObjectBinding(
                () -> new StereographicProjection(
                        viewingParametersB.getCenter()),
                viewingParametersB.centerProperty());
        
        
        /*
         * Un lien contenant la transformation correspondant au passage
         * du repère de la projection stéréographique au repère du canevas
         */
        dilationFactor = Bindings.createDoubleBinding(
                () -> canvas.getWidth() / projection.getValue().applyToAngle(
                        Angle.ofDeg(viewingParametersB.getFieldOfViewDeg())),
                projection, 
                canvas.widthProperty(),
                viewingParametersB.fieldOfViewDegProperty());
        
        planeToCanvas = Bindings.createObjectBinding(
                () -> Transform.affine(
                        dilationFactor.get(), 0, 
                        0, -dilationFactor.get(),
                        canvas.getWidth() / 2, canvas.getHeight() / 2),
                dilationFactor,
                canvas.widthProperty(),
                canvas.heightProperty());
        
        
        /*
         *  Un lien contenant le ciel observé
         */
        observedSky = Bindings.createObjectBinding(
                () -> new ObservedSky(
                        dateTimeB.getZonedDateTime(),
                        observerLocationB.getCoordinates(),
                        projection.getValue(),
                        catalogue), 
                observerLocationB.coordinatesProperty(),
                dateTimeB.dateProperty(),
                dateTimeB.timeProperty(),
                dateTimeB.zoneProperty(),
                projection);
        
        
        /*
         * Un auditeur pour être informé des mouvements
         * du curseur de la souris, et stocker sa position dans une propriété
         */
        canvas.setOnMouseMoved(event -> {
            mousePosition.setValue(
                    CartesianCoordinates.of(event.getX(), event.getY()));
            event.consume();
        });
        
        /*
         * Un lien contenant la position du curseur de la souris
         * dans le système de coordonnées horizontal
         */
        
            mouseHorizontalPosition = Bindings.createObjectBinding(
                    () -> projection.getValue().inverseApply(
                            CartesianCoordinates.ofPoint2D(
                                    planeToCanvas.getValue().inverseTransform(
                                            mousePosition.getValue().x(), mousePosition.getValue().y()))),
                    mousePosition, 
                    projection, 
                    planeToCanvas);
        
        
        
        /*
         * L'azimut et la hauteur, en degrés, de la position du curseur de la souris
         */
        mouseAzDeg = Bindings.createDoubleBinding(
                () -> mouseHorizontalPosition.getValue().azDeg(), 
                mouseHorizontalPosition);
        
        mouseAltDeg = Bindings.createDoubleBinding(
                () -> mouseHorizontalPosition.getValue().altDeg(),
                mouseHorizontalPosition);
        
        
        objectUnderMouse = Bindings.createObjectBinding(
                () -> observedSky.getValue().objectClosestTo(
                        CartesianCoordinates.ofPoint2D(
                                planeToCanvas.getValue().inverseTransform(
                                        mousePosition.getValue().x(), mousePosition.getValue().y())), 
                        planeToCanvas.getValue().inverseDeltaTransform(CLOSEST_RANGE, 0).getX()).orElse(null),
                observedSky, 
                mousePosition, 
                planeToCanvas);
        
        /*
         * Un auditeur pour détecter les clics de la souris
         * sur le canevas et en faire alors le destinataire des événements clavier
         */
        canvas.setOnMousePressed(event ->  {
            if (event.isPrimaryButtonDown()) {
                canvas.requestFocus();
                
                // BONUS : Select a celestial object                   
                clickedObject.setValue(objectUnderMouse.getValue());
                event.consume();
            }
        });
        
        
        
        /*
         * Un auditeur pour réagir aux mouvement de la molette de la souris
         * et/ou du trackpad et changer le champ de vue en fonction
         */
        canvas.setOnScroll(event -> {
            viewingParametersB.setFieldOfViewDeg(
                    FOV_DEG_INTERVAL.clip(
                            viewingParametersB.getFieldOfViewDeg()
                                - (Math.abs(event.getDeltaX()) > Math.abs(event.getDeltaY()) ? 
                                        event.getDeltaX() : event.getDeltaY())));
            event.consume();
        });
        
        
        /*
         * Un auditeur pour réagir aux pression sur les touches du curseur 
         * et changer le centre de projection en fonction
         */
        canvas.setOnKeyPressed(event -> {
            switch(event.getCode()) {
            case LEFT : {
                viewingParametersB.setCenter(
                        HorizontalCoordinates.ofDeg(
                                AZ_DEG_INTERVAL.reduce(
                                        Angle.toDeg(viewingParametersB.getCenter().az() - Angle.ofDeg(AZ_STEP))),
                                viewingParametersB.getCenter().altDeg()));
                break; } 
            case RIGHT : {  
                viewingParametersB.setCenter(
                        HorizontalCoordinates.ofDeg(
                                AZ_DEG_INTERVAL.reduce(
                                        Angle.toDeg(viewingParametersB.getCenter().az() + Angle.ofDeg(AZ_STEP))),
                                viewingParametersB.getCenter().altDeg()));
                break; } 
            case UP : {  
                viewingParametersB.setCenter(
                        HorizontalCoordinates.ofDeg(
                                viewingParametersB.getCenter().azDeg(),
                                ALT_DEG_INTERVAL.clip(
                                        viewingParametersB.getCenter().altDeg() + ALT_STEP)));
                break; } 
            case DOWN : {
                viewingParametersB.setCenter(
                        HorizontalCoordinates.ofDeg(
                                viewingParametersB.getCenter().azDeg(),
                                ALT_DEG_INTERVAL.clip(
                                        viewingParametersB.getCenter().altDeg() - ALT_STEP)));
                break; } 
            default : { 
                break; }
            }
            event.consume();
        });
        
        
        /*
         * Des auditeurs pour être informé des changements 
         * des liens et propriétés ayant un impact sur le dessin du ciel
         */
        ChangeListener<Object> painterListener = (o, oV, nV) 
                -> {
                    painter.drawAllConditional(
                        observedSky.getValue(),
                        projection.getValue(),
                        planeToCanvas.getValue(),
                        clickedObject.getValue(),
                        miscB.getShowStars(),
                        miscB.getShowAsterisms(),
                        miscB.getShowPlanets(),
                        miscB.getShowSun(),
                        miscB.getShowMoon(),
                        miscB.getShowEarth(),
                        miscB.getShowHorizon(),
                        miscB.getShowInfo());
                };
        
        observedSky.addListener(painterListener);
        planeToCanvas.addListener(painterListener);
        clickedObject.addListener(painterListener); 
        miscB.showInfoProperty().addListener(painterListener);
        miscB.showStarsProperty().addListener(painterListener);
        miscB.showAsterismsProperty().addListener(painterListener);
        miscB.showPlanetsProperty().addListener(painterListener);
        miscB.showSunProperty().addListener(painterListener);
        miscB.showMoonProperty().addListener(painterListener);
        miscB.showEarthProperty().addListener(painterListener);
        miscB.showHorizonProperty().addListener(painterListener);
                
        // BONUS : STAR TRACKING
       
        ChangeListener<Object> trackListener = (o, oV, nV)
                 -> {
 
                     if (clickedObject.isNotNull().get()) {
                         clickedObject.setValue(observedSky.getValue().getCurrent(clickedObject.getValue()));                        
                    
                         if (miscB.getDoTrack()) {
                             viewingParametersB.setCenter(
                                 projection.getValue().inverseApply(
                                         observedSky.getValue().getPosition(
                                                 clickedObject.getValue())));
                         }
                     }
                 };

        clickedObject.addListener(trackListener);
        miscB.doTrackProperty().addListener(trackListener);
        observerLocationB.lonDegProperty().addListener(trackListener);
        observerLocationB.latDegProperty().addListener(trackListener);
        dateTimeB.zoneProperty().addListener(trackListener);
        dateTimeB.dateProperty().addListener(trackListener);
        dateTimeB.timeProperty().addListener(trackListener);
        
        
                
    }
    
    /**
     * Retourne le canevas du ciel
     * @return
     *          le canevas du ciel.
     */
    public Canvas canvas() {
        return canvas;
    }

    /**
     * Retourne la propriété de l'azimut, en degrés, de la position du curseur de la souris
     * @return
     *          la propriété de l'azimut, en degrés, de la position du curseur de la souris
     */
    public DoubleBinding mouseAzDegProperty() {
        return mouseAzDeg;
    }

    /**
     * Retourne la propriété de la hauteur, en degrés, de la position du curseur de la souris
     * @return
     *          la propriété de la hauteur, en degrés, de la position du curseur de la souris
     */
    public DoubleBinding mouseAltDegProperty() {
        return mouseAltDeg;
    }
    
    /**
     * Retourne la propriété de l'objet céleste le plus proche de la souris
     * @return
     *          la propriété de l'objet céleste le plus proche de la souris
     */
    public ObjectBinding<CelestialObject> objectUnderMouseProperty() {
        return objectUnderMouse;
    }
    // BONUS
    public ObjectProperty<CelestialObject> clickedObjectProperty(){
        return clickedObject;
    }
    public ObjectBinding<StereographicProjection> projectionProperty(){
        return projection;
    }
    public ObjectBinding<ObservedSky> observedSkyProperty(){
        return observedSky;
    }
}
