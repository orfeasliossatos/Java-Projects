package ch.epfl.rigel.gui;

import java.awt.Event;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.UnaryOperator;

import ch.epfl.rigel.astronomy.AsterismLoader;
import ch.epfl.rigel.astronomy.CelestialObject;
import ch.epfl.rigel.astronomy.HygDatabaseLoader;
import ch.epfl.rigel.astronomy.MoonModel;
import ch.epfl.rigel.astronomy.PlanetModel;
import ch.epfl.rigel.astronomy.Star;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.astronomy.SunModel;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.math.Interval;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.PopupWindow.AnchorLocation;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.converter.LocalTimeStringConverter;
import javafx.util.converter.NumberStringConverter;

/**
 * Classe contenant le programme principal du projet.
 * 
 * @author Orfeas Liossatos (310738)
 * @author Henrique Da Silva Gameiro (315689)
 */
public final class Main extends Application {

    /// Noms des fichiers
    private static final String HYG_CATALOGUE_NAME      = "/hygdata_v3.csv";
    private static final String ASTERISM_CATALOGUE_NAME = "/asterisms.txt";
    private static final String FONT_AWESOME_NAME       = "/Font Awesome 5 Free-Solid-900.otf";
    
    /// Valeurs par défaut
    private static final double DEFAULT_FOV                                 = 100d;
    private static final ZonedDateTime DEFAULT_ZDT                          = ZonedDateTime.now();
    private static final NamedTimeAccelerator DEFAULT_NAMED_ACCELERATOR     = NamedTimeAccelerator.TIMES_300;
    private static final HorizontalCoordinates DEFAULT_CENTER               = HorizontalCoordinates.ofDeg(180.000000000001d, 15d);
    private static final GeographicCoordinates DEFAULT_OBSERVER_POSITION    = GeographicCoordinates.ofDeg(6.57d, 46.52d);
    
    /// Styles
    private static final String CONTROL_BAR_STYLE                   = "-fx-spacing: 4; -fx-padding: 4;";
    private static final String OBSERVER_POSITION_BOX_STYLE         = "-fx-spacing: inherit;-fx-alignment: baseline-left;";
    private static final String OBSERVER_POSITION_TEXTFIELD_STYLE   = "-fx-pref-width: 60;-fx-alignment: baseline-right;";
    private static final String OBSERVER_TIME_BOX_STYLE             = "-fx-spacing: inherit; -fx-alignment: baseline-left;";
    private static final String OBSERVER_DATEPICKER_STYLE           = "-fx-pref-width: 120;";
    private static final String OBSERVER_HOUR_TEXTFIELD_STYLE       = "-fx-pref-width: 75; -fx-alignment: baseline-right;";
    private static final String OBSERVER_ZONE_COMBOBOX_STYLE        = "-fx-pref-width: 180;";
    private static final String TIME_ANIMATION_BOX_STYLE            = "-fx-spacing: inherit;";
    private static final String INFO_BAR_STYLE                      = "-fx-padding: 4; -fx-background-color: white;";
    
    /// Valeurs boutons
    private static final String RESET_SYMBOL = "\uf0e2";
    private static final String PLAY_SYMBOL  = "\uf04b";
    private static final String PAUSE_SYMBOL = "\uf04c";
    private static final double FONT_SIZE    = 15d;
    
    /// Paramètres fenêtre
    private static final double WINDOW_MIN_WIDTH    = 800d;
    private static final double WINDOW_MIN_HEIGHT   = 600d;
    private static final double WINDOW_POS_Y        = 300d;
    private static final String WINDOW_TITLE        = "Rigel";
    
    /// BONUS
    private static final String TRACK_SYMBOL    = "\uf08d";
    private static final String INFO_SYMBOL     = "\uf05a";
    private static final String SUN_SYMBOL      = "\uf185";
    private static final String ASTERISM_SYMBOL = "\uf542";
    private static final String STAR_SYMBOL     = "\uf005";
    private static final String MOON_SYMBOL     = "\uf186";
    private static final String PLANET_SYMBOL   = "\uf7a2";
    private static final String EARTH_SYMBOL    = "\uf453";
    private static final String HORIZON_SYMBOL  = "\uf14e";
    private static final String SEARCH_SYMBOL   = "\uf002";
    
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try (InputStream hygStream = resourceStream(HYG_CATALOGUE_NAME);
                InputStream astStream = resourceStream(ASTERISM_CATALOGUE_NAME)) {

            // Initialisation du modèle
            StarCatalogue catalogue = new StarCatalogue.Builder()
                    .loadFrom(hygStream, HygDatabaseLoader.INSTANCE)
                    .loadFrom(astStream, AsterismLoader.INSTANCE)
                    .build();
        
            DateTimeBean dateTimeB = new DateTimeBean();
            dateTimeB.setZonedDateTime(DEFAULT_ZDT);
            
            ObserverLocationBean observerLocationB = new ObserverLocationBean();
            observerLocationB.setCoordinates(DEFAULT_OBSERVER_POSITION);
            
            ViewingParametersBean viewingParametersB = new ViewingParametersBean();
            viewingParametersB.setCenter(DEFAULT_CENTER);
            viewingParametersB.setFieldOfViewDeg(DEFAULT_FOV);
            
            MiscBean miscB = new MiscBean();
            miscB.setDoTrack(false);
            miscB.setShowInfo(false);
            miscB.setShowAsterisms(true);
            miscB.setShowStars(true);
            miscB.setShowPlanets(true);
            miscB.setShowSun(true);
            miscB.setShowMoon(true);
            miscB.setShowEarth(true);
            miscB.setShowHorizon(true);

            TimeAnimator timeAnimator = new TimeAnimator(dateTimeB);
            timeAnimator.setAccelerator(DEFAULT_NAMED_ACCELERATOR.getAccelerator());
            
            // L'affichage du ciel
            SkyCanvasManager canvasManager = new SkyCanvasManager(catalogue,
                    dateTimeB,
                    observerLocationB,
                    viewingParametersB,
                    miscB);
             
            Canvas skyCanvas = canvasManager.canvas();
            Pane skyPane = new Pane(skyCanvas);
            
            // Barre d'information
            BorderPane infoBar = createInfoBar(viewingParametersB, canvasManager);


            //Barre de recherche(bonus) composée d'un TextField est d'une TableView
            TextField starSearchBar = new TextField();
            
            TableView<StarMutable> starTable = new TableView<StarMutable>();
            starTable.setMaxWidth(300);
            TableColumn<StarMutable,String>firstColumn = new TableColumn<StarMutable,String>("Stars") ;
            TableColumn<StarMutable,String >secondColumn = new TableColumn<StarMutable,String>("Hipparcos ID");
            List<Star> all = new ArrayList<Star>();
            all = catalogue.stars();
            List<String> starNames = new ArrayList<String>();
            List<String> starHipparcos = new ArrayList<String>();
            String hipparcos = "0";
            for(Star star : all) {
                starNames.add(star.toString());
                hipparcos = String.valueOf(star.hipparcosId());
                starHipparcos.add(hipparcos);
            }
            List<StarMutable> stars = new ArrayList<StarMutable>();
            for(int i = 0; i < starNames.size(); ++i) {
                stars.add(new StarMutable(starNames.get(i), starHipparcos.get(i), all.get(i)));
            }
            

            for( CelestialObject p : canvasManager.observedSkyProperty().getValue().planets()) {
                stars.add(new StarMutable(p.name(), "none", p));
            }
            CelestialObject moon = canvasManager.observedSkyProperty().getValue().moon();
            stars.add(new StarMutable("Moon", "none", moon));
            CelestialObject sun = canvasManager.observedSkyProperty().getValue().sun();
            stars.add(new StarMutable("Sun", "none", sun));

            ObservableList<StarMutable> data = FXCollections.observableArrayList(stars);
            firstColumn.setCellValueFactory(cellData -> cellData.getValue().name);
            secondColumn.setCellValueFactory(cellData -> cellData.getValue().hipparcos);
            FilteredList<StarMutable> filteredData = new FilteredList<>(data, p -> true);
            starTable.getColumns().addAll(firstColumn, secondColumn);
           
            //permet d'actualiser la TableView selon ce qu'écrit l'utilisateur
            starSearchBar.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate( celestialObject -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }

                    String lowerCaseFilter = newValue.toLowerCase();
                    
                    if (celestialObject.name().toLowerCase().contains(lowerCaseFilter)) {
                        return true; 
                    } else if(celestialObject.hipparcos().toLowerCase().contains(lowerCaseFilter)){
                            return true;
                        }
                    return false; 
                
                });
            });
            
            
            SortedList<StarMutable> sortedData = new SortedList<StarMutable>(filteredData);
            sortedData.comparatorProperty().bind(starTable.comparatorProperty());
            starTable.setItems(sortedData);
            
            //lorsqu'on appuye sur enter on cible l'object séléctionné dans TableView et on centre l'affichage sur cet objet
            starTable.setOnKeyPressed(event ->  {
                try {
                if (event.getCode().equals(KeyCode.ENTER) ) {
                    skyCanvas.requestFocus();
                    canvasManager.clickedObjectProperty().setValue(starTable.getSelectionModel().getSelectedItem().celestialObject());
                    viewingParametersB.setCenter(canvasManager.projectionProperty().getValue().inverseApply
                    (canvasManager.observedSkyProperty().getValue().getPosition(canvasManager.clickedObjectProperty().getValue())));
                }        
                } catch(NullPointerException e){
                }
                
                if(event.getCode().equals(KeyCode.BACK_SPACE)) {
                    starSearchBar.deletePreviousChar();
                }
                event.consume();
            });
            Popup tableView = new Popup();
            tableView.getContent().addAll(starTable);
            Popup searchBox= new Popup();
            searchBox.getContent().addAll(starSearchBar);
            
            //bouton "loupe" permettant d'ouvir la TableView(popup)
            Button show = new Button(SEARCH_SYMBOL);
            try (InputStream fontStream = resourceStream(FONT_AWESOME_NAME)) {
                Font fontAwesome = Font.loadFont(fontStream, FONT_SIZE);
            show.setFont(fontAwesome);
            show.setOnAction(new EventHandler<ActionEvent>() {
                boolean pressed = false;
                public void handle(ActionEvent event) {
                    if(!pressed) {
                    tableView.setX(primaryStage.getX() - 7 + primaryStage.getWidth() - 300);
                    tableView.setY(primaryStage.getY() + 93);
                    searchBox.setX(primaryStage.getX() -7 + primaryStage.getWidth() - 300);
                    searchBox.setY(primaryStage.getY() + 67);
                    tableView.show(primaryStage);
                    searchBox.show(primaryStage);
                    pressed = true;
                    } else {
                        tableView.hide();
                        searchBox.hide();
                        pressed = false;
                    } 
                }
            });

            }
            // La dimension du TableView s'adapte aussi lorsqu'on change la taille de la fenêtre principale
            primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
                tableView.setX(primaryStage.getX() - 7 + primaryStage.getWidth() - 300);
                tableView.setY(primaryStage.getY() + 93);
                searchBox.setX(primaryStage.getX() -7 + primaryStage.getWidth() - 300);
                searchBox.setY(primaryStage.getY() + 67);
           });

           primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> {
               tableView.setX(primaryStage.getX() - 7 + primaryStage.getWidth() - 300);
               tableView.setY(primaryStage.getY() + 93);
               searchBox.setX(primaryStage.getX() -7 + primaryStage.getWidth() - 300);
               searchBox.setY(primaryStage.getY() + 67);
           });
           //création de la bar de contrôle
            HBox controlBar = createControlBar(dateTimeB, observerLocationB, miscB, timeAnimator,show);
            BorderPane root = new BorderPane(null, controlBar, skyPane, infoBar, null);
            
            // Les dimensions du canevas sont liées à la BorderPane racine.
            skyCanvas.widthProperty().bind(root.widthProperty());
            skyCanvas.heightProperty().bind(root.heightProperty());
       
            // Paramètres fenêtre
            primaryStage.setTitle(WINDOW_TITLE);
            
            primaryStage.setMinWidth(WINDOW_MIN_WIDTH);
            primaryStage.setMinHeight(WINDOW_MIN_HEIGHT);
            
            primaryStage.setY(WINDOW_POS_Y);
            
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
            
            // Le canevas devient destinataire des événements claviers
            skyCanvas.requestFocus();
            
        }
    }
    
    private HBox createControlBar(DateTimeBean dateTimeB, ObserverLocationBean observerLocationB, 
            MiscBean miscB, TimeAnimator timeAnimator, Button show) {

        // Panneau "position d'observation"
        HBox observerPositionBox = createObserverPositionBox(observerLocationB);
        
        // Séparateur
        Separator seperator1 = new Separator(Orientation.VERTICAL);
         
        // Panneau "instant d'observation"
        HBox observerTimeBox = createObserverTimeBox(dateTimeB, timeAnimator);
         
        // Séparateur
        Separator seperator2 = new Separator(Orientation.VERTICAL);
        
        // Panneau "écoulement du temps"
        HBox timeAnimationBox = createTimeAnimationBox(dateTimeB, timeAnimator);

        // Séparateur
        Separator seperator3 = new Separator(Orientation.VERTICAL);
        
        // Panneau "BONUS"
        HBox bonusBox = createBonusBox(miscB);

        // Séparateur
        Separator seperator4 = new Separator(Orientation.VERTICAL);
        
        // View Menu

        MenuBar menuBar = new MenuBar();

        menuBar.getMenus().add(createViewMenu(miscB));
        
        // Barre de contrôle
        HBox controlBar = new HBox(observerPositionBox, seperator1, observerTimeBox, seperator2, timeAnimationBox, seperator3, bonusBox, seperator4, menuBar, show);
        controlBar.setStyle(CONTROL_BAR_STYLE);
        
        return controlBar;
    }
    
    private HBox createObserverPositionBox(ObserverLocationBean observerLocationB) {
     
        // Etiquette et champ de text : Longitude
        Label lonLabel = new Label("Longitude (°) :");
        
        TextFormatter<Number> lonTextFormatter = 
                createTextFormatterForInterval(GeographicCoordinates.LONGITUDE_INTERVAL);
        lonTextFormatter.setValue(DEFAULT_OBSERVER_POSITION.lonDeg());
        
        TextField lonTextField = new TextField();
        lonTextField.setTextFormatter(lonTextFormatter);
        lonTextField.setStyle(OBSERVER_POSITION_TEXTFIELD_STYLE);

        observerLocationB.lonDegProperty().bind(lonTextFormatter.valueProperty());
        
         
        // Etiquette et champ de texte : Latitude 
        Label latLabel = new Label("Latitude (°) :");
        
        TextFormatter<Number> latTextFormatter = 
                createTextFormatterForInterval(GeographicCoordinates.LATITUDE_INTERVAL);
        latTextFormatter.setValue(DEFAULT_OBSERVER_POSITION.latDeg());
        
        TextField latTextField = new TextField();
        latTextField.setTextFormatter(latTextFormatter);
        latTextField.setStyle(OBSERVER_POSITION_TEXTFIELD_STYLE);
         
        observerLocationB.latDegProperty().bind(latTextFormatter.valueProperty());

        
        // Panneau "position d'observation" 
        HBox observerPositionBox = new HBox(lonLabel, lonTextField, latLabel, latTextField);
        observerPositionBox.setStyle(OBSERVER_POSITION_BOX_STYLE);
        
        return observerPositionBox;
    }
    
    private HBox createObserverTimeBox(DateTimeBean dateTimeB, TimeAnimator timeAnimator) {

        // Etiquette et sélecteur : Date
        Label dateLabel = new Label("Date :");
        dateLabel.disableProperty().bind(timeAnimator.getRunning());
        
        DatePicker datePicker = new DatePicker();
        datePicker.setValue(DEFAULT_ZDT.toLocalDate());
        datePicker.setStyle(OBSERVER_DATEPICKER_STYLE);
        
        datePicker.disableProperty().bind(timeAnimator.getRunning());
        dateTimeB.dateProperty().bindBidirectional(datePicker.valueProperty());
         
        // Etiquette et champ de texte : Heure
        Label hourLabel = new Label("Heure :");
        hourLabel.disableProperty().bind(timeAnimator.getRunning());
        
        DateTimeFormatter hmsFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTimeStringConverter stringConverter = new LocalTimeStringConverter(hmsFormatter, hmsFormatter);
        TextFormatter<LocalTime> timeFormatter = new TextFormatter<>(stringConverter);
        timeFormatter.setValue(DEFAULT_ZDT.toLocalTime());

        TextField hourTextField = new TextField();
        hourTextField.setTextFormatter(timeFormatter);     
        hourTextField.setStyle(OBSERVER_HOUR_TEXTFIELD_STYLE);
        hourTextField.disableProperty().bind(timeAnimator.getRunning()); 
        

        timeFormatter.valueProperty().bind(dateTimeB.timeProperty());

        //dateTimeB.timeProperty().bindBidirectional(timeFormatter.valueProperty()); 

        // Menu déroulant : Zone
        List<String> zoneIdStrings = new ArrayList<>(ZoneId.getAvailableZoneIds());
        Collections.sort(zoneIdStrings);
        
        List<ZoneId> sortedZones = new ArrayList<>();
        for (String zoneIdString : zoneIdStrings) {
            sortedZones.add(ZoneId.of(zoneIdString));
        }
        
        ComboBox<ZoneId> zoneComboBox = new ComboBox<ZoneId>(FXCollections.observableArrayList(sortedZones));
        zoneComboBox.setValue(DEFAULT_ZDT.getZone());
        zoneComboBox.setStyle(OBSERVER_ZONE_COMBOBOX_STYLE);
        zoneComboBox.disableProperty().bind(timeAnimator.getRunning());
        
        dateTimeB.zoneProperty().bindBidirectional(zoneComboBox.valueProperty());
        
        // Panneau "instant d'observation"
        HBox observerTimeBox = new HBox(dateLabel, datePicker, hourLabel, hourTextField, zoneComboBox);
        observerTimeBox.setStyle(OBSERVER_TIME_BOX_STYLE);
        
        return observerTimeBox;
    }
    
    private HBox createTimeAnimationBox(DateTimeBean dateTimeB, TimeAnimator timeAnimator) {
        // Sélecteur : Accélérateur du temps
        List<NamedTimeAccelerator> acceleratorNames = 
                new ArrayList<>(
                        Arrays.asList(
                                NamedTimeAccelerator.values()));
        
        ChoiceBox<NamedTimeAccelerator> acceleratorChoiceBox = 
                new ChoiceBox<>(
                        FXCollections.observableArrayList(
                                acceleratorNames)); 

        acceleratorChoiceBox.setValue(DEFAULT_NAMED_ACCELERATOR);
        acceleratorChoiceBox.disableProperty().bind(timeAnimator.getRunning());
        
        timeAnimator.acceleratorProperty().bind(
                Bindings.select(
                        acceleratorChoiceBox.valueProperty(),
                        "accelerator"));

        // Boutons de réinitialisation et d'arret/démarrage
        Button resetButton = new Button(RESET_SYMBOL);
        Button pausePlayButton = new Button();
        
        try (InputStream fontStream = resourceStream(FONT_AWESOME_NAME)) {
            Font fontAwesome = Font.loadFont(fontStream, FONT_SIZE);

            // Bouton de réinitialisation
            resetButton.setFont(fontAwesome);
            resetButton.setOnMouseClicked(event -> {
                dateTimeB.setZonedDateTime(DEFAULT_ZDT);
                timeAnimator.stop();
                event.consume();
            });       
            resetButton.disableProperty().bind(timeAnimator.getRunning());
            
            // Bouton d'arret/démarrage
            pausePlayButton.setFont(fontAwesome);
            pausePlayButton.setOnMouseClicked(event -> {
                if (timeAnimator.getRunning().get()) { timeAnimator.stop(); } 
                else { timeAnimator.start(); }
                event.consume();
            });
            pausePlayButton.textProperty().bind(
                    Bindings.when(timeAnimator.getRunning())
                    .then(PAUSE_SYMBOL)
                    .otherwise(PLAY_SYMBOL));
            
            // Fermeture du flot
            fontStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Panneau "écoulement du temps"
        HBox timeAnimationBox = new HBox(acceleratorChoiceBox, resetButton, pausePlayButton);
        timeAnimationBox.setStyle(TIME_ANIMATION_BOX_STYLE);
        
        return timeAnimationBox;
    }
    
    private HBox createBonusBox(MiscBean miscB) {
        ToggleButton trackButton = new ToggleButton(TRACK_SYMBOL);
        ToggleButton infoButton = new ToggleButton(INFO_SYMBOL);
        
        try (InputStream fontStream = resourceStream(FONT_AWESOME_NAME)) {
            Font fontAwesome = Font.loadFont(fontStream, FONT_SIZE);
         
            trackButton.setFont(fontAwesome);
            trackButton.setOnMouseClicked(event -> {
                miscB.setDoTrack(!miscB.getDoTrack());
                event.consume();
            });
            
            infoButton.setFont(fontAwesome);
            infoButton.setOnMouseClicked(event -> {
                miscB.setShowInfo(!miscB.getShowInfo());
                event.consume();
            });
            
            // Fermeture du flot
            fontStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        HBox bonusBox = new HBox(trackButton, infoButton);
        bonusBox.setStyle("-fx-spacing: inherit;");
        return bonusBox;
    }
    
    private Menu createViewMenu(MiscBean miscB) {

        Text starSymbol     = new Text(STAR_SYMBOL);
        Text planetSymbol   = new Text(PLANET_SYMBOL);
        Text sunSymbol      = new Text(SUN_SYMBOL);
        Text moonSymbol     = new Text(MOON_SYMBOL);
        Text earthSymbol    = new Text(EARTH_SYMBOL);
        Text horizonSymbol  = new Text(HORIZON_SYMBOL);
        Text asterismSymbol = new Text(ASTERISM_SYMBOL);
        
        try (InputStream fontStream = resourceStream(FONT_AWESOME_NAME)) {
            Font fontAwesome = Font.loadFont(fontStream, FONT_SIZE);
            
            starSymbol.setFont(fontAwesome);
            planetSymbol.setFont(fontAwesome);
            sunSymbol.setFont(fontAwesome);
            moonSymbol.setFont(fontAwesome);
            earthSymbol.setFont(fontAwesome);
            horizonSymbol.setFont(fontAwesome);
            asterismSymbol.setFont(fontAwesome);
            
            // Fermeture du flot
            fontStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        CheckMenuItem asterismView = new CheckMenuItem("", asterismSymbol);
        asterismView.setSelected(true);
        asterismView.selectedProperty().addListener((o, oV, nV) -> miscB.setShowAsterisms(!miscB.getShowAsterisms()));
        
        CheckMenuItem starView = new CheckMenuItem("", starSymbol);
        starView.setSelected(true);
        starView.selectedProperty().addListener((o, oV, nV) -> miscB.setShowStars(!miscB.getShowStars()));
        
        CheckMenuItem planetView = new CheckMenuItem("", planetSymbol);
        planetView.setSelected(true);
        planetView.selectedProperty().addListener((o, oV, nV) -> miscB.setShowPlanets(!miscB.getShowPlanets()));

        CheckMenuItem sunView = new CheckMenuItem("", sunSymbol);
        sunView.setSelected(true);
        sunView.selectedProperty().addListener((o, oV, nV) -> miscB.setShowSun(!miscB.getShowSun()));

        CheckMenuItem moonView = new CheckMenuItem("", moonSymbol);
        moonView.setSelected(true);
        moonView.selectedProperty().addListener((o, oV, nV) -> miscB.setShowMoon(!miscB.getShowMoon()));

        CheckMenuItem earthView = new CheckMenuItem("", earthSymbol);
        earthView.setSelected(true);
        earthView.selectedProperty().addListener((o, oV, nV) -> miscB.setShowEarth(!miscB.getShowEarth()));

        CheckMenuItem horizonView = new CheckMenuItem("", horizonSymbol);
        horizonView.setSelected(true);
        horizonView.selectedProperty().addListener((o, oV, nV) -> miscB.setShowHorizon(!miscB.getShowHorizon()));

        Menu viewMenu = new Menu("Filtre");
        
        viewMenu.getItems().addAll(asterismView, starView, planetView, sunView, moonView, earthView, horizonView);
        
        return viewMenu;
    }
    
    private BorderPane createInfoBar(ViewingParametersBean viewingParametersB, SkyCanvasManager canvasManager) {
        // Texte : champ de vue
        Text fovText = new Text();
        fovText.textProperty().bind(
                Bindings.format(
                        "Champ de vue : %.1f",
                        viewingParametersB.fieldOfViewDegProperty()));
        
        // Texte : l'objet céleste le plus proche du curseur
        Text closestObjectText = new Text();
        closestObjectText.textProperty().bind(
                canvasManager.objectUnderMouseProperty().asString());
        
        // Texte : La position, en coordonnées horizontales, du curseur
        Text mousePositionText = new Text();
        mousePositionText.textProperty().bind(
                Bindings.format(
                        "Azimut : %.2f°, Hauteur : %.2f°", 
                        canvasManager.mouseAzDegProperty(),
                        canvasManager.mouseAltDegProperty()));
         
        // Barre d'information 
        BorderPane infoBar = new BorderPane(closestObjectText, null, mousePositionText, null, fovText);
        infoBar.setStyle(INFO_BAR_STYLE);
        
        return infoBar;
    }
    
    private TextFormatter<Number> createTextFormatterForInterval(Interval interval) {
        NumberStringConverter stringConverter = new NumberStringConverter("#0.00");
        UnaryOperator<TextFormatter.Change> filter = (change -> { 
            try {
                String newText = change.getControlNewText();
                double newValue = stringConverter.fromString(newText).doubleValue();
                return interval.contains(newValue)
                        ? change : null;
                } catch (Exception e) {
                    return null;
                }
            });

        return new TextFormatter<>(stringConverter, 0, filter);
    }
    
    private InputStream resourceStream(String resourceName) {
        return getClass().getResourceAsStream(resourceName);
    }
    

}
