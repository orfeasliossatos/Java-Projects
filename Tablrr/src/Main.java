import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;

import back.ITable;
import back.Preference;
import back.Reservation;
import back.ReservationBean;
import back.ReservationBeansCatalogue;
import back.SingleTable;
import back.TableNumberFormatException;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.LocalDateTimeStringConverter;

/**
 * Class containing the main program
 * @author Orfeas Liossatos
 * 
 */
public final class Main extends Application {
    
    /// PDF PARAMS
    private static final float CANVAS_SIZE_FACTOR = 0.65f;
    
    /// TABLE GRID PARAMS
    private static final int MAX_ROWS = 10;
    private static final int MAX_COLS = 10;
    private static final int DEFAULT_ROWS = 7;
    private static final int DEFAULT_COLS = 7;
    
    /// DEFAULT FILE NAMES
    private static final String FONT_AWESOME_NAME   = "/Font Awesome 5 Free-Solid-900.otf";
    private static final String RES_PDF_DESTINATION = "C:/Users/oilio/eclipse-workspace/Tables/resources/reservations_pdf.pdf";
    private static final String RES_CSV_DESTINATION = "C:/Users/oilio/eclipse-workspace/Tables/resources/reservations.csv";
    private static final String TAB_PDF_DESTINATION = "C:/Users/oilio/eclipse-workspace/Tables/resources/tables_pdf.pdf";
    private static final String TAB_CSV_DESTINATION = "C:/Users/oilio/eclipse-workspace/Tables/resources/tables.csv";
    private static final String TAB_IMG_DESTINATION = "C:/Users/oilio/eclipse-workspace/Tables/resources/tablesCanvas.png";
    
    /// UNICODE SYMBOLS
    private static final String FLOPPY_SYMBOL           = "\uf0c7"; 
    private static final String PERSON_PLUS_SYMBOL      = "\uf234"; 
    private static final String PDF_SYMBOL              = "\uf1c1"; 
    private static final String BORDER_NONE_SYMBOL      = "\uf850"; 
    private static final String BORDER_ALL_SYMBOL       = "\uf84c"; 
    
    /// DEFAULT VALUES
    private static final ZonedDateTime DEFAULT_ZDT = ZonedDateTime.now();
    
    /// WINDOWS PARAMETERS
    private static final String WINDOW_TITLE        = "Tables";
    private static final double WINDOW_MIN_WIDTH    = 1200;
    private static final double WINDOW_MIN_HEIGHT   = 800;
    private static final double WINDOW_POS_X        = 10;
    private static final double WINDOW_POS_Y        = 10;
    
    /// ICON
    private static javafx.scene.image.Image ICON;
    
    /// FONT 
    private static Font FONT_AWESOME;
    private static final double BUTTON_FONT_SIZE    = 15d;
    private static final double PDF_TITLE_FONT_SIZE = 18d; 
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        // LOAD RESERVATIONS
        ReservationBeansCatalogue reservationsCatalogue;
        try (InputStream reservationsInputStream = new FileInputStream(RES_CSV_DESTINATION)) {
            reservationsCatalogue = new ReservationBeansCatalogue.Builder()
                    .loadFrom(reservationsInputStream, ReservationsLoader.INSTANCE)
                    .build();
        }
        
        // LOAD TABLES
        TableBeansCatalogue tableBeansCatalogue;
        try (InputStream tableBeansInputStream = new FileInputStream(TAB_CSV_DESTINATION)) {
            tableBeansCatalogue = new TableBeansCatalogue.Builder()
                    .loadFrom(tableBeansInputStream, TableBeansLoader.INSTANCE)
                    .build();
        }
        
        // LOAD FONT
        try (InputStream fontStream = resourceStream(FONT_AWESOME_NAME)) {
            FONT_AWESOME = Font.loadFont(fontStream, BUTTON_FONT_SIZE);
        }
        
        // LOAD ICON
        try (InputStream iconStream = resourceStream("/TablesIcon.png")) {
            ICON = new javafx.scene.image.Image(iconStream);
        }
        
        // Information
        ObservableList<ReservationBean> reservationBeans = FXCollections.observableArrayList(reservationsCatalogue.reservations());
        ReservationBean selectedReservationBean = ReservationBean.ofReservation(Reservation.emptyReservation());
        MessageBean messageBean = new MessageBean("", "Contrôles tables - Composer : ALT (tenir et cliquer), Décomposer : D, Instancier : N, Supprimer : DELETE, Déplacer : SHIFT (tenir et cliquer) ");
        DateBean dateBean = new DateBean(LocalDate.now());
        TableGridParamsBean tableGridParamsBean = new TableGridParamsBean(DEFAULT_ROWS, DEFAULT_COLS, false);
        ObservableList<TableBean> tableBeans = FXCollections.observableArrayList(tableBeansCatalogue.tableBeans());
        
        /// Front-end
        TablesCanvasManager tablesCanvasManager = new TablesCanvasManager(tableGridParamsBean, tableBeans, 
                dateBean, messageBean, reservationBeans, selectedReservationBean);
        Canvas tablesCanvas = tablesCanvasManager.canvas();
        
        // Tables
        Pane tablesPane = new Pane(tablesCanvas);
        tablesPane.setPrefHeight(tablesCanvas.getHeight());
        tablesPane.setPrefWidth(tablesCanvas.getWidth());
        tablesCanvasManager.paneHeightProperty().set(tablesCanvas.getWidth());
        tablesCanvasManager.paneWidthProperty().set(tablesCanvas.getHeight());

        VBox tablesBox = new VBox(tablesPane);
        
        // Reservation box
        VBox reservationBox = createReservationBox(reservationBeans, dateBean, messageBean, selectedReservationBean);
        
        // Message box
        BorderPane messageBox = createMessageBox(messageBean);
        
        // Control box
        HBox controlBox = createControlBox(reservationBeans, dateBean, messageBean,
                tableGridParamsBean, tableBeans, tablesCanvasManager, tablesCanvas);
        
        // Tables & Reservations
        HBox reserveTablesBox = new HBox(reservationBox, tablesBox);
        reserveTablesBox.setSpacing(10);
        reserveTablesBox.setPadding(new Insets(10, 0, 10, 0));
        
        // Root
        VBox root = new VBox(controlBox, reserveTablesBox, messageBox);
        root.setPadding(new Insets(10, 10, 10, 10));
        
        // Windows parameters
        primaryStage.getIcons().add(new javafx.scene.image.Image("/TablesIcon.png"));
        primaryStage.setTitle(WINDOW_TITLE);
        
        primaryStage.setMinWidth(WINDOW_MIN_WIDTH);
        primaryStage.setMinHeight(WINDOW_MIN_HEIGHT);
        
        primaryStage.setX(WINDOW_POS_X);
        primaryStage.setY(WINDOW_POS_Y);
        
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        
        primaryStage.setResizable(false);
        
        
        tablesCanvas.requestFocus();
    }
    
    private HBox createAddReservationBox(ObservableList<ReservationBean> reservationBeans, DateBean dateBean, MessageBean messageBean,
            TableColumn<ReservationBean, String> nameColumn, TableColumn<ReservationBean, Preference> preferenceColumn,
            TableColumn<ReservationBean, Integer> numberOfPeopleColumn, TableColumn<ReservationBean, ITable> tableNumberColumn,
            TableColumn<ReservationBean, LocalDateTime> reservationDateTimeColumn, TableColumn<ReservationBean, String> commentColumn,
            DateTimeFormatter dateTimeFormatter, TableColumn<ReservationBean, Boolean> blockColumn) {

        // ADD RESERVATION TEXTFIELDS

        TextField addTable = new TextField();
        addTable.setPromptText("Table");
        addTable.setMaxWidth(tableNumberColumn.getPrefWidth());
        TextField addName = new TextField();
        addName.setPromptText("Nom");
        addName.setMaxWidth(nameColumn.getPrefWidth());
        TextField addNumberOfPeople= new TextField();
        addNumberOfPeople.setPromptText("Nombre");
        addNumberOfPeople.setMaxWidth(numberOfPeopleColumn.getPrefWidth());
        TextField addReservationDateTime = new TextField();
        addReservationDateTime.setPromptText("Date de rés");
        addReservationDateTime.setMaxWidth(reservationDateTimeColumn.getPrefWidth());
        TextField addPreference = new TextField();
        addPreference.setPromptText("Préférence");
        addPreference.setMaxWidth(preferenceColumn.getPrefWidth());
        TextField addComment = new TextField();
        addComment.setPromptText("Remarque");
        addComment.setMaxWidth(commentColumn.getPrefWidth());
        TextField addBlock = new TextField();
        addBlock.setPromptText("Bloquée");
        addBlock.setMaxWidth(blockColumn.getPrefWidth());
        
        // ADD RESERVATION BUTTON
        Button addReservationButton = new Button(PERSON_PLUS_SYMBOL);

        addReservationButton.setFont(FONT_AWESOME);
        addReservationButton.setOnAction(event -> {
            try {
                Reservation defaultReservation = Reservation.emptyReservation();
                reservationBeans.add(new ReservationBean(
                        addName.getText().isBlank()             ? defaultReservation.name()             
                                : addName.getText(), 
                        addPreference.getText().isBlank()       ? defaultReservation.preference()       
                                : Preference.fromString(addPreference.getText()), 
                        addNumberOfPeople.getText().isBlank()   ? defaultReservation.numberOfPeople()   
                                : Integer.parseInt(addNumberOfPeople.getText()), 
                        addTable.getText().isBlank()      ? defaultReservation.iTable()           
                                : ITable.parse(addTable.getText()), 
                        addReservationDateTime.getText().isBlank()  ? defaultReservation.reservationDateTime() 
                                : LocalDateTime.parse(addReservationDateTime.getText(), dateTimeFormatter),
                        addComment.getText().isBlank()          ? defaultReservation.comment()
                                : addComment.getText(),
                        dateBean.getDate(),
                        addBlock.getText().isBlank()            ? defaultReservation.block()
                                : Boolean.parseBoolean(addBlock.getText())));
                
                addName.clear();
                addPreference.clear();
                addNumberOfPeople.clear();
                addTable.clear();
                addReservationDateTime.clear();
                addComment.clear();
                addBlock.clear();
            } catch (DateTimeParseException e) {
                messageBean.setErrorMessage("Format de date invalide. Essayez JJ-MM-AAAA à HH:MM:SS. Par exemple, 18-08-2020 à 19:30:00");
            } catch (NumberFormatException e) {
                messageBean.setErrorMessage("Chaîne de caractères invalide, ne correspond pas à un nombre entier.");
            } catch (TableNumberFormatException e) {
                messageBean.setErrorMessage("Table invalide. X pour la table X.  X-Y pour les tables X et Y. Si X vide, nul, ou \"-\" alors aucune table.");
            }
        });
        
        HBox addReservationBox = new HBox(addTable, addName, addNumberOfPeople, addReservationDateTime, addPreference, addComment, addBlock, addReservationButton);
        
        return addReservationBox;
    }
    
    @SuppressWarnings("unchecked")
    private VBox createReservationBox(ObservableList<ReservationBean> reservationBeans, DateBean dateBean,
            MessageBean messageBean, ReservationBean selectedReservationBean) {

        /// TABLE COLUMNS
        
        // Table number column
        TableColumn<ReservationBean, ITable> tableNumberColumn = new TableColumn<>("Table");
        tableNumberColumn.setCellValueFactory(cellData -> cellData.getValue().iTableProperty());
        tableNumberColumn.setCellFactory(TextFieldTableCell.forTableColumn(new TableStringConverter()));
        tableNumberColumn.setOnEditCommit(t -> t.getTableView().getItems().get(t.getTablePosition().getRow()).setITable(t.getNewValue()));
        tableNumberColumn.setPrefWidth(50);
        tableNumberColumn.setStyle("-fx-alignment : center;");

        // Name column
        TableColumn<ReservationBean, String> nameColumn = new TableColumn<>("Nom");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setOnEditCommit(t -> t.getTableView().getItems().get(t.getTablePosition().getRow()).setName(t.getNewValue()));
        nameColumn.setPrefWidth(120);
        
        // Number of people column
        TableColumn<ReservationBean, Integer> numberOfPeopleColumn = new TableColumn<>("Nombre");
        numberOfPeopleColumn.setCellValueFactory(cellData -> cellData.getValue().numberOfPeopleProperty());
        numberOfPeopleColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        numberOfPeopleColumn.setOnEditCommit(t -> t.getTableView().getItems().get(t.getTablePosition().getRow()).setNumberOfPeople(t.getNewValue()));
        numberOfPeopleColumn.setPrefWidth(60);
        numberOfPeopleColumn.setStyle("-fx-alignment : center;");
        
        // Reservation DateTime column
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-uuuu à HH:mm:ss");
        LocalDateTimeStringConverter localDateTimeConverter = new LocalDateTimeStringConverter(dateTimeFormatter, dateTimeFormatter);
        
        TableColumn<ReservationBean, LocalDateTime> reservationDateTimeColumn = new TableColumn<>("Date de rés.");
        reservationDateTimeColumn.setCellValueFactory(cellData -> cellData.getValue().reservationDateTimeProperty());
        reservationDateTimeColumn.setCellFactory(TextFieldTableCell.forTableColumn(localDateTimeConverter));
        reservationDateTimeColumn.setOnEditCommit(t -> t.getTableView().getItems().get(t.getTablePosition().getRow()).setReservationDateTime(t.getNewValue()));
        reservationDateTimeColumn.setPrefWidth(140);
        reservationDateTimeColumn.setStyle("-fx-alignment : center");
        
        // Preference column
        TableColumn<ReservationBean, Preference> preferenceColumn = new TableColumn<>("Préférence");
        preferenceColumn.setCellValueFactory(cellData -> cellData.getValue().preferenceProperty());
        preferenceColumn.setCellFactory(TextFieldTableCell.forTableColumn(new PreferenceStringConverter()));
        preferenceColumn.setOnEditCommit(t -> t.getTableView().getItems().get(t.getTablePosition().getRow()).setPreference(t.getNewValue()));
        preferenceColumn.setPrefWidth(80);
        preferenceColumn.setStyle("-fx-alignment : center");
        
        // Comment column
        TableColumn<ReservationBean, String> commentColumn = new TableColumn<>("Remarque");
        commentColumn.setCellValueFactory(cellData -> cellData.getValue().commentProperty());
        commentColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        commentColumn.setOnEditCommit(t -> t.getTableView().getItems().get(t.getTablePosition().getRow()).setComment(t.getNewValue()));
        
        // Block column
        TableColumn<ReservationBean, Boolean> blockColumn = new TableColumn<>("Bloquer");
        blockColumn.setCellValueFactory(cellData -> cellData.getValue().blockProperty());
        blockColumn.setCellFactory(TextFieldTableCell.forTableColumn(new BooleanStringConverter()));
        blockColumn.setOnEditCommit(t -> t.getTableView().getItems().get(t.getTablePosition().getRow()).setBlock(t.getNewValue()));
        blockColumn.setPrefWidth(50);
        
        // RESERVATION TABLE
        TableView<ReservationBean> reservationTable = new TableView<>();
        reservationTable.getColumns().addAll(
                tableNumberColumn,
                nameColumn, 
                numberOfPeopleColumn,
                reservationDateTimeColumn,
                preferenceColumn,
                commentColumn,
                blockColumn);  
        reservationTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        reservationTable.setEditable(true);
        reservationTable.setManaged(true);
        reservationTable.setTableMenuButtonVisible(true);
        
        // Add reservation for given concert date
        reservationTable.setItems(reservationBeans.filtered(r -> 
                r.getConcertDate().equals(dateBean.getDate())
        ));
        
        // Key presses 
        reservationTable.setOnKeyPressed(event -> {
            // Delete selected reservation
            if (event.getCode().equals(KeyCode.DELETE)) {
                reservationBeans.remove(reservationTable.getSelectionModel().getSelectedItem());
                messageBean.setErrorMessage("Réservation supprimée");
            // Select a reservation
            } else if (event.getCode().equals(KeyCode.C)) {
                selectedReservationBean.setReservation(reservationTable.getSelectionModel().getSelectedItem().getReservation());
                messageBean.setErrorMessage("Réservation copiée");
            // Block a reservation (auto placing no longer affects it!)            
            } else if (event.getCode().equals(KeyCode.X)) {
                boolean isBlocked = reservationTable.getSelectionModel().getSelectedItem().getBlock();
                reservationTable.getSelectionModel().getSelectedItem().setBlock(!isBlocked);
                messageBean.setErrorMessage("Réservation" + ((isBlocked) ? " plus " : "") + "bloquée");
            }
            event.consume();
        });

        // The table is updated when the date changes
        dateBean.dateProperty().addListener((o, oV, nV) -> 
            reservationTable.setItems(reservationBeans.filtered(r -> 
                r.getConcertDate().equals(dateBean.getDate())))              
        );
        
        // PANE CONTAINING TABLE
        StackPane stackPane = new StackPane(reservationTable);
        stackPane.setMinHeight(650);
        
        // ADD A NEW RESERVATION
        HBox addReservationBox = createAddReservationBox(reservationBeans, dateBean, messageBean,
                nameColumn, preferenceColumn, numberOfPeopleColumn, tableNumberColumn,
                reservationDateTimeColumn, commentColumn, dateTimeFormatter, blockColumn);
        
        // RESERVATION BOX
        VBox reservationBox = new VBox(stackPane, addReservationBox);
        reservationBox.setSpacing(10);
        
        return reservationBox;
    }
    
    private BorderPane createMessageBox(MessageBean messageBean) {
        // ERROR MESSAGE
        Text errorMessage = new Text();
        errorMessage.textProperty().bind(messageBean.errorMessageProperty());
        
        // SELECTION MESSAGE
        Text selectionMessage = new Text();
        selectionMessage.textProperty().bind(messageBean.selectionMessageProperty());
        selectionMessage.setTextAlignment(javafx.scene.text.TextAlignment.RIGHT);
        
        // MESSAGE BOX
        BorderPane messageBox = new BorderPane(null, null, selectionMessage, null, errorMessage);
        messageBox.setStyle("-fx-background-color: none;");
        
        
        return messageBox;
    }
    
    private HBox createControlBox(ObservableList<ReservationBean> reservationBeans, DateBean dateBean, 
            MessageBean messageBean, TableGridParamsBean tableGridParamsBean, ObservableList<TableBean> tableBeans,
            TablesCanvasManager tablesCanvasManager, Canvas canvas) {

        // CONCERT DATE
        DatePicker reservationDatePicker = new DatePicker();
        reservationDatePicker.setValue(DEFAULT_ZDT.toLocalDate());

        // The date is linked to the datePicker
        dateBean.dateProperty().bindBidirectional(reservationDatePicker.valueProperty());
        
        // BUTTONS
        
        /// EXPORT CSV
        Button saveButton = new Button(FLOPPY_SYMBOL);
        
        saveButton.setOnMouseClicked(event -> {
            // Write reservations to a CSV file
            try (Writer reservationsOutputStream = new PrintWriter(RES_CSV_DESTINATION)) {
                reservationsOutputStream.write("Nom, Preference, Nombre, Table, Date de res., Commentaire, Date de concert\n");
                reservationBeans.forEach(r -> {
                    try {
                        reservationsOutputStream.write(r.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                messageBean.setErrorMessage("Saved.");
            } catch (FileNotFoundException e1) {
                messageBean.setErrorMessage("FileNotFoundException for reservations.csv while saving");
                e1.printStackTrace();
            } catch (IOException e1) {
                messageBean.setErrorMessage("IOException for reservations.csv while saving");
                e1.printStackTrace();
            }
            
            // Write tables to a CSV file
            try (Writer tablesOutputStream = new PrintWriter(TAB_CSV_DESTINATION)) {
                tableBeans.forEach(t -> {
                    try {
                        tablesOutputStream.write(t.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                
            } catch (FileNotFoundException e) {
                messageBean.setErrorMessage("FileNotFoundException for tables.csv while saving");
                e.printStackTrace();
            } catch (IOException e) {
                messageBean.setErrorMessage("IOException for tables.csv while saving");
                e.printStackTrace();
            }
        });
        
        /// EXPORT PDF
        Button pdfButton = new Button(PDF_SYMBOL);

        pdfButton.setOnMouseClicked(event -> {
            // Print canvas lmao
            try (PdfWriter writer = new PdfWriter(TAB_PDF_DESTINATION);
                    ImageOutputStream imageOutputStream = new FileImageOutputStream(new File(TAB_IMG_DESTINATION))) {
                PdfDocument pdfDoc = new PdfDocument(writer);
                pdfDoc.addNewPage();
               
                // Document
                Document document = new Document(pdfDoc);
                
                // Title
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-uuuu");
                document.add(new Paragraph("Tables du " + dateBean.getDate().format(dateFormatter))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontSize((float) PDF_TITLE_FONT_SIZE));
                
                // Canvas image
                SnapshotParameters params = new SnapshotParameters();
                WritableImage writableImage = new WritableImage(660, 660);
                WritableImage snapshot = canvas.snapshot(params, writableImage); 
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(snapshot, null);
                
                try {
                    ImageIO.write(bufferedImage, "png", imageOutputStream);
                } catch (IOException e) {
                    document.close();
                    throw new RuntimeException(e);
                }
                
                ImageData imageData = ImageDataFactory.create(TAB_IMG_DESTINATION);
                Image image = new Image(imageData);
                image.setBorder(new SolidBorder(2f));
                image.scale(CANVAS_SIZE_FACTOR, CANVAS_SIZE_FACTOR);
                image.setHorizontalAlignment(HorizontalAlignment.CENTER);
                
                document.add(image);
                document.close();

                messageBean.setSelectionMessage("PDF des Tables créé");
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            
            // Write reservations to a PDF file
            try (PdfWriter writer = new PdfWriter(RES_PDF_DESTINATION)) {
                PdfDocument pdfDoc = new PdfDocument(writer);
                pdfDoc.addNewPage();               
                
                // Document
                Document document = new Document(pdfDoc);               

                // Title
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-uuuu");
                document.add(new Paragraph("Réservations du " + dateBean.getDate().format(dateFormatter))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontSize((float) PDF_TITLE_FONT_SIZE));
                
                // Table
                float[] pointColumnWidths = {175f, 100f, 100f, 175f};
                Table table = new Table(pointColumnWidths);
                table.setBorder(new SolidBorder(2f));
                table.setSplitCharacters(new CustomSplitCharacters());
                
                Cell nameCell = new Cell();
                nameCell.add(new Paragraph("Nom"));
                nameCell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
                nameCell.setTextAlignment(TextAlignment.CENTER);
                
                Cell numberOfPeopleCell = new Cell();
                numberOfPeopleCell.add(new Paragraph("Nombre"));
                numberOfPeopleCell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
                numberOfPeopleCell.setTextAlignment(TextAlignment.CENTER);
                
                Cell tableCell = new Cell();
                tableCell.add(new Paragraph("Table"));
                tableCell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
                tableCell.setTextAlignment(TextAlignment.CENTER);
                
                Cell commentCell = new Cell();
                commentCell.add(new Paragraph("Remarque"));
                commentCell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
                commentCell.setTextAlignment(TextAlignment.CENTER);
                
                table.addCell(tableCell).addCell(nameCell).addCell(numberOfPeopleCell).addCell(commentCell);
                
                reservationBeans.filtered(r -> r.getConcertDate().equals(dateBean.getDate()))
                    .forEach(r -> table.addCell(new Cell().add(new Paragraph(r.getITable().toString())).setTextAlignment(TextAlignment.CENTER))
                            .addCell(r.getName())
                            .addCell(new Cell().add(new Paragraph(r.getNumberOfPeople().toString())).setTextAlignment(TextAlignment.CENTER))
                            .addCell(r.getComment()));
                
                document.add(table);
                
                // Fill in the document
                document.close();       
                
                messageBean.setErrorMessage("PDF des Réservations créé");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                messageBean.setErrorMessage("Erreur, fichier non-trouvable. Essayez de fermer le PDF s'il est déjà ouvert.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Control grid
        Label gridRowLabel = new Label("\uf7a4");
        gridRowLabel.setFont(FONT_AWESOME);
        gridRowLabel.setPadding(new Insets(6, 0, 0, 0));
        Spinner<Integer> gridRowSpinner = new Spinner<>();
        SpinnerValueFactory<Integer> gridRowSpinnerValueFactory = 
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, MAX_ROWS, DEFAULT_ROWS);
        gridRowSpinner.setValueFactory(gridRowSpinnerValueFactory);
        gridRowSpinner.setPrefWidth(60);
        tableGridParamsBean.numRowsProperty().bind(gridRowSpinner.valueProperty());
        
        Label gridColLabel = new Label("\uf7a5");
        gridColLabel.setFont(FONT_AWESOME);
        gridColLabel.setPadding(new Insets(6, 0, 0, 0));
        Spinner<Integer> gridColSpinner = new Spinner<>();
        SpinnerValueFactory<Integer> gridColSpinnerValueFactory = 
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, MAX_COLS, DEFAULT_COLS);
        gridColSpinner.setValueFactory(gridColSpinnerValueFactory);
        gridColSpinner.setPrefWidth(60);
        tableGridParamsBean.numColsProperty().bind(gridColSpinner.valueProperty());
        
        // Modify table
        Label changeTableSizeLabel = new Label("Taille");
        //changeTableSizeLabel.setFont(FONT_AWESOME);
        changeTableSizeLabel.setPadding(new Insets(4, 0, 0, 0));
        Spinner<Integer> tableSizeSpinner = new Spinner<>();
        tableSizeSpinner.valueFactoryProperty().bind(tablesCanvasManager.clickedTableSizeSpinnerValueFactoryProperty());
        tableSizeSpinner.valueProperty().addListener((o, oV, nV) -> tablesCanvasManager.changeSize(nV, tableBeans, dateBean));
        tableSizeSpinner.setPrefWidth(60);
        
        Label changeTableChairsLabel = new Label("Chaises");
        //changeTableChairsLabel.setFont(FONT_AWESOME);
        changeTableChairsLabel.setPadding(new Insets(4, 0, 0, 0));
        Spinner<Integer> tableChairsSpinner = new Spinner<>();
        tableChairsSpinner.valueFactoryProperty().bind(tablesCanvasManager.clickedTableChairsSpinnerValueFactoryProperty());
        tableChairsSpinner.valueProperty().addListener((o, oV, nV) -> tablesCanvasManager.changeNumberOfChairs(nV, tableBeans, dateBean));
        tableChairsSpinner.setPrefWidth(60);
                
        ToggleButton snapToGridButton = new ToggleButton(BORDER_NONE_SYMBOL);
        snapToGridButton.setFont(FONT_AWESOME);
        snapToGridButton.setOnMouseClicked(event -> {
            tableGridParamsBean.setSnapToGrid(!tableGridParamsBean.getSnapToGrid());
            if (tableGridParamsBean.getSnapToGrid())
                snapToGridButton.setText(BORDER_ALL_SYMBOL);
            else
                snapToGridButton.setText(BORDER_NONE_SYMBOL);
        });
        
        
        Button generateDefaultTablesButton = new Button("Tables par défault");
        generateDefaultTablesButton.setOnMouseClicked(event -> {
            tableBeans.removeIf(t -> t.getConcertDate().equals(dateBean.getDate()));
            for (int i = 1; i <= 4; i++) 
                for (int j = 1; j <= 7; j++) {
                    int tableNumber = 34 - (j - 1 + (i - 1) * 7);
                    Point2D position = new Point2D(j * (canvas.getWidth() / 8), (i + 1) * (canvas.getHeight() / 8));
                    int numberOfChairs = 5;
                    int size = tableNumber == 10 ? 38 : ((tableNumber == 17 || tableNumber == 22) ? 22 : 30); 
                    tableBeans.add(new TableBean(new SingleTable(tableNumber, position, numberOfChairs, size), dateBean.getDate()));
                }
            for (int i = 6; i >= 1; i--) {
                tableBeans.add(new TableBean(new SingleTable(i, new Point2D((7 - i) * (canvas.getWidth() / 8) + (canvas.getWidth() / 16), canvas.getHeight() / 8 * 6), 3, 30), dateBean.getDate()));
            }
        });
        
        Button autoPlaceButton = new Button("Placement");
        autoPlaceButton.setOnMouseClicked(event -> {
            FilteredList<ReservationBean> filteredReservationBeans = reservationBeans.filtered(r -> r.getConcertDate().equals(dateBean.getDate()) && !r.getBlock());
            SortedList<ReservationBean> sortedReservationBeans = filteredReservationBeans.sorted((x, y) -> x.getReservationDateTime().compareTo(y.getReservationDateTime()));
            
            FilteredList<TableBean> filteredTableBeans = tableBeans.filtered(t -> t.getConcertDate().equals(dateBean.getDate()));
            
            Point2D frontMiddle = new Point2D(330, 500);
            Point2D backMiddle = new Point2D(330, 0);
            Point2D frontLeft = new Point2D(0, 600);
            Point2D frontRight = new Point2D(660, 600);

            Comparator<TableBean> frontMiddleComparator = (x, y) -> Double.compare(x.getTable().getCoordinates().distance(frontMiddle), y.getTable().getCoordinates().distance(frontMiddle));
            Comparator<TableBean> backMiddleComparator = (x, y) -> Double.compare(x.getTable().getCoordinates().distance(backMiddle), y.getTable().getCoordinates().distance(backMiddle));
            Comparator<TableBean> frontLeftComparator = (x, y) -> Double.compare(x.getTable().getCoordinates().distance(frontLeft), y.getTable().getCoordinates().distance(frontLeft));
            Comparator<TableBean> frontRightComparator = (x, y) -> Double.compare(x.getTable().getCoordinates().distance(frontRight), y.getTable().getCoordinates().distance(frontRight));
            
            
            Set<TreeSet<Integer>> takenTableNumbers = new HashSet<>();
            for (ReservationBean rBean : sortedReservationBeans) {
                rBean.setITable(SingleTable.chairBehind());
                
                FilteredList<TableBean> remainingTableBeans = filteredTableBeans.filtered(t -> !takenTableNumbers.contains(t.getTable().getTableNumber()));
                SortedList<TableBean> sortedTableBeans;
                switch(rBean.getPreference()) {
                case LEFT :
                    sortedTableBeans = remainingTableBeans.sorted(frontLeftComparator); break;
                case RIGHT :
                    sortedTableBeans = remainingTableBeans.sorted(frontRightComparator); break;
                case BACK :
                    sortedTableBeans = remainingTableBeans.sorted(backMiddleComparator); break;
                default :
                    sortedTableBeans = remainingTableBeans.sorted(frontMiddleComparator); break;
                }
                
                for (TableBean tBean : sortedTableBeans) {
                    ITable table = tBean.getTable();
                    if (rBean.getNumberOfPeople() <= table.getNumberOfChairs()) {
                        rBean.setITable(table);
                        takenTableNumbers.add(table.getTableNumber());
                        break;
                    }
                }
            }
            
        });
        
        // BUTTON FONTS
        saveButton.setFont(FONT_AWESOME);
        pdfButton.setFont(FONT_AWESOME);
        
        Separator separator1 = new Separator(Orientation.VERTICAL);
        Separator separator2 = new Separator(Orientation.VERTICAL);
        Separator separator3 = new Separator(Orientation.VERTICAL);
        Separator separator4 = new Separator(Orientation.VERTICAL);
        Separator separator5 = new Separator(Orientation.VERTICAL);

        /// CONTROL BOX
        HBox controlBox = new HBox(pdfButton, saveButton, separator1, 
                reservationDatePicker, separator2,
                gridRowLabel, gridRowSpinner, gridColLabel, gridColSpinner, snapToGridButton, separator3, 
                changeTableSizeLabel, tableSizeSpinner, changeTableChairsLabel, tableChairsSpinner, separator4,
                generateDefaultTablesButton, separator5,
                autoPlaceButton);
        
        controlBox.setSpacing(5);
        
        return controlBox;
    }
    
    
    
    private InputStream resourceStream(String resourceName) {
        return getClass().getResourceAsStream(resourceName);
    }
    
}
