import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import back.CompositeTable;
import back.ITable;
import back.ReservationBean;
import back.SingleTable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.input.KeyCode;

/**
 * Class representing a canvas manager on which the table grid is drawn.
 * @author Orfeas Liossatos
 *
 */
public final class TablesCanvasManager {

    private static final double LARGE_NUMBER = Double.MAX_VALUE;
    
    private static final int DEFAULT_WIDTH  = 660;
    private static final int DEFAULT_HEIGHT = 660;
    
    private ObjectBinding<TableGrid> tableGrid;
    private ObjectBinding<Set<ITable>> tables;
    
    private DoubleProperty paneWidthProperty = new SimpleDoubleProperty();
    private DoubleProperty paneHeightProperty = new SimpleDoubleProperty();
    
    private ObjectBinding<ITable> tableUnderMouse;
    private ObjectProperty<ITable> clickedTable = new SimpleObjectProperty<>();
    
    private ObjectProperty<SpinnerValueFactory<Integer>> clickedTableSizeSpinnerValueFactoryProperty = new SimpleObjectProperty<>(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100));
    private ObjectProperty<SpinnerValueFactory<Integer>> clickedTableChairsSpinnerValueFactoryProperty = new SimpleObjectProperty<>(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100));
    
    private ObjectProperty<Point2D> mousePosition = new SimpleObjectProperty<>(Point2D.ZERO);
    private ObjectBinding<Point2D> movePosition;
    
    private BooleanProperty showMovedTable = new SimpleBooleanProperty(false);
    private BooleanProperty showComposeLine = new SimpleBooleanProperty(false);
    
    private Canvas canvas;
    private TablesCanvasPainter painter;
    
    public TablesCanvasManager(TableGridParamsBean tableGridParamsBean, ObservableList<TableBean> tableBeans, 
            DateBean dateBean, MessageBean messageBean, ObservableList<ReservationBean> reservationBeans, ReservationBean selectedReservationBean) {
        
        /**
         * The canvas
         */
        canvas = new Canvas(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        
        /**
         * The painter
         */
        painter = new TablesCanvasPainter(canvas);
        
        /**
         * Table grid
         */
        tableGrid = Bindings.createObjectBinding(() -> new TableGrid(tableGridParamsBean.getNumRows(), tableGridParamsBean.getNumCols()), 
                tableGridParamsBean.numRowsProperty(),
                tableGridParamsBean.numColsProperty()); 

        /*
         * On mouse move
         */
        canvas.setOnMouseMoved(event -> {
            mousePosition.setValue(
                    new Point2D(event.getX(), event.getY()));
            event.consume();
        });
        
        /*
         * Move position
         */
        movePosition = Bindings.createObjectBinding(() -> 
        !tableGridParamsBean.getSnapToGrid() ? mousePosition.getValue()
                : new Point2D(
                        tableGridParamsBean.getNumCols() == 0 ? mousePosition.getValue().getX()
                                : roundToClosestMultiple(
                                        mousePosition.getValue().getX(), 
                                        (double) DEFAULT_WIDTH / (tableGridParamsBean.getNumCols() + 1)),
                    
                        tableGridParamsBean.getNumRows() == 0 ? mousePosition.getValue().getY()  
                                : roundToClosestMultiple(
                                        mousePosition.getValue().getY(),
                                        (double) DEFAULT_HEIGHT / (tableGridParamsBean.getNumRows() + 1))), 
                mousePosition,
                tableGridParamsBean.numColsProperty(), 
                tableGridParamsBean.numRowsProperty(), 
                tableGridParamsBean.snapToGridProperty());
        
        /*
         * On click
         */
        canvas.setOnMousePressed(event ->  {
            if (event.isPrimaryButtonDown()) {
                // Request focus
                canvas.requestFocus();
                
                if (!event.isShiftDown() && !event.isAltDown()) {
                    // Select a table       
                    clickedTable.setValue(tableUnderMouse.getValue());
                    messageBean.setSelectionMessage("Table sélectionnée : " + (clickedTable.isNotNull().get() ? clickedTable.getValue().toString() : "aucune" ));
                
                    // Change spinners
                    if (clickedTable.isNull().get() || clickedTable.getValue() instanceof CompositeTable) {
                        clickedTableSizeSpinnerValueFactoryProperty.set(null);  
                        clickedTableChairsSpinnerValueFactoryProperty.set(null);  
                    } else if (clickedTable.getValue() instanceof SingleTable) {
                        clickedTableSizeSpinnerValueFactoryProperty.set(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, clickedTable.isNotNull().get() ? ((SingleTable) clickedTable.getValue()).getSize() : 0));
                        clickedTableChairsSpinnerValueFactoryProperty.set(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, clickedTable.isNotNull().get() ? ((SingleTable) clickedTable.getValue()).getNumberOfChairs() : 0));
                    }
                }
                
                // Move a table
                if (event.isShiftDown() && clickedTable.isNotNull().get()) {
                    filterDate(tableBeans, dateBean).forEach(t -> {
                        if (t.getTable().equals(clickedTable.getValue())) {
                            
                            
                            if (clickedTable.getValue() instanceof SingleTable) {
                                t.setTable(((SingleTable)t.getTable()).withCoordinates(movePosition.getValue()));
                            } else {
                                Point2D moveDifference = movePosition.getValue().subtract(t.getTable().getCoordinates());
                                Set<ITable> movedSingleTables = new HashSet<>();
                                for (SingleTable singleTable : ((CompositeTable)t.getTable()).getSingleTables()) 
                                    movedSingleTables.add(singleTable.withCoordinates(singleTable.getCoordinates().add(moveDifference)));
                                
                                t.setTable(new CompositeTable(movedSingleTables));
                            }
                            
                            clickedTable.setValue(t.getTable());
                        }
                        messageBean.setSelectionMessage("Table déplacée : " + clickedTable.getValue());
                    });
                }
                
                // Compose two tables
                if (event.isAltDown() && clickedTable.isNotNull().get() && tableUnderMouse.isNotNull().get()
                        && clickedTable.isNotEqualTo(tableUnderMouse).get()) {
                    ITable firstTable = clickedTable.getValue();
                    ITable secondTable = tableUnderMouse.getValue();
                    messageBean.setSelectionMessage("Tables composées : " + firstTable.toString() + " et " + secondTable.toString());
                    
                    // Remove two single tables
                    TableBean firstTableBean = null, secondTableBean = null;
                    for (TableBean tableBean : filterDate(tableBeans, dateBean)) {
                        if (tableBean.getTable().equals(firstTable))
                                firstTableBean = tableBean;
                        if (tableBean.getTable().equals(secondTable))
                                secondTableBean = tableBean;
                    }
                    tableBeans.removeAll(firstTableBean, secondTableBean);
                            
                    // Add them as a composed table
                    tableBeans.add(new TableBean(
                            new CompositeTable(Set.of(firstTable, secondTable)), 
                            dateBean.getDate()));
                    
                    clickedTable.set(null);
                }
            }

            // Update canvas painting
            painter.drawAll(tableGrid.getValue(), 
                    tableGridParamsBean.getSnapToGrid(),
                    paneWidthProperty.getValue(), 
                    paneHeightProperty.getValue(),
                    tables.getValue(),
                    clickedTable.getValue(),
                    tableUnderMouse.getValue(),
                    mousePosition.getValue(),
                    movePosition.getValue(),
                    showMovedTable.getValue(),
                    showComposeLine.getValue());
            
            event.consume();
        });
        
        /*
         * De-select listener
         */
        ChangeListener<Object> deselectListener = (o, oV, nV) -> clickedTable.setValue(null);

        dateBean.dateProperty().addListener(deselectListener);
        
        /**
         * On key press
         */
        canvas.setOnKeyPressed(event -> {
            switch(event.getCode()) {
            
            // MOVE TABLE
            case SHIFT : 
                showMovedTable.set(true);
                break;
                
            // SET A RESERVATION'S TABLE 
            case C :
                if (clickedTable.isNotNull().get()) {
                    if (clickedTable.getValue().getNumberOfChairs() < selectedReservationBean.getNumberOfPeople()) {
                        messageBean.setErrorMessage("Pas assez de chaises!");
                        
                    } else {
                        for (ReservationBean reservationBean : reservationBeans) {
                            if (reservationBean.getReservation().equals(selectedReservationBean.getReservation())) {
                                reservationBean.setITable(clickedTable.get());
                                messageBean.setErrorMessage("Table changée!");
                                break;
                            }
                        }
                    }
                }
                break;
                
            // DELETE TABLE
            case DELETE :
                if (clickedTable.isNotNull().get())
                    for (TableBean tableBean : filterDate(tableBeans, dateBean)) {
                        if (tableBean.getTable().equals(clickedTable.get())) {
                            tableBeans.remove(tableBean);
                            clickedTable.set(null);
                            messageBean.setSelectionMessage("Table supprimée : " + tableBean.getTable().toString());
                            break;
                        }
                    }
                break;
            
            // SHOW COMPOSITION LINE
            case ALT :
                showComposeLine.set(true);
                break;
                
            // DECOMPOSE TABLES
            case D :
                if (clickedTable.isNotNull().get() && clickedTable.getValue() instanceof CompositeTable) {
                    TableBean removeTableBean = null;
                    for (TableBean tableBean : filterDate(tableBeans, dateBean)) {
                        ITable table = tableBean.getTable();
                        
                        if (table instanceof CompositeTable) 
                            if (table.equals(clickedTable.getValue())) {
                                removeTableBean = tableBean;
                                break;
                            }
                    }
                    
                    // Remove the composite table
                    tableBeans.remove(removeTableBean);
                    
                    // Add them back as single tables
                    Set<SingleTable> singleTables = ((CompositeTable)removeTableBean.getTable()).getSingleTables();
                    ObservableList<TableBean> singleTableBeans = FXCollections.observableArrayList();
                    singleTables.forEach(t -> singleTableBeans.add(new TableBean(t, dateBean.getDate())));
                    tableBeans.addAll(singleTableBeans);
                    
                    clickedTable.set(null);
                }
                break;
                
            // ADD A NEW TABLE
            case N : 
                Set<SingleTable> singleTables = new TreeSet<>();
                filterDate(tableBeans, dateBean).forEach(t -> {
                    ITable tableBeanTable = t.getTable();
                    if (tableBeanTable instanceof SingleTable) 
                        singleTables.add((SingleTable) tableBeanTable);
                    else
                        singleTables.addAll(((CompositeTable)tableBeanTable).getSingleTables());
                });

                int i = 1; boolean didFill = false;
                for (SingleTable singleTable : singleTables) {
                    if (singleTable.getTableNumber().first() != i) {
                        tableBeans.add(new TableBean(new SingleTable(i, movePosition.get(), 5), dateBean.getDate()));
                        didFill = true;
                        break;
                    }
                    i++;
                }
                if (!didFill)
                    tableBeans.add(new TableBean(new SingleTable(i, movePosition.get(), 5), dateBean.getDate()));
                
                clickedTable.set(tableBeans.get(tableBeans.size() - 1).getTable());
                messageBean.setSelectionMessage("Table créée : " + clickedTable.getValue().toString());
                break;
            default:
                break;
            }
                
        });
        
        /**
         * On key release
         */
        canvas.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.SHIFT) {
                showMovedTable.set(false);
            }
            
            if (event.getCode() == KeyCode.ALT) {
                showComposeLine.set(false);
            }
        });
        
        /**
         * The set of tables
         */
        tables = Bindings.createObjectBinding(() -> {
            Set<ITable> tableSet = new HashSet<>();

            for (TableBean tableBean : tableBeans) 
                if (tableBean.getConcertDate().equals(dateBean.getDate()))
                    tableSet.add(tableBean.getTable());
            
            return tableSet; 
            },
                clickedTable,
                tableBeans,
                dateBean.dateProperty());

        /**
         * The table under the mouse
         */
        tableUnderMouse = Bindings.createObjectBinding(() -> {
            double closestDistance = LARGE_NUMBER;
            ITable closestTable = null;
            for (TableBean tableBean : filterDate(tableBeans, dateBean)) {
                ITable table = tableBean.getTable();
                if (table instanceof SingleTable) {

                    double distanceFromTable = mouseDistanceFromSingleTable((SingleTable)table);
                    
                    if (distanceFromTable < closestDistance 
                            && distanceFromTable < Math.pow(
                                    ((SingleTable) table).getSize(), 2)) {
                        closestDistance = distanceFromTable;
                        closestTable = table;
                    }
                } else {
                    for (SingleTable singleTable : ((CompositeTable)table).getSingleTables()) {
                        double distanceFromTable = mouseDistanceFromSingleTable(singleTable);
                        
                        if (distanceFromTable < closestDistance
                                && distanceFromTable < Math.pow(
                                        singleTable.getSize(), 2)) {
                            closestDistance = distanceFromTable;
                            closestTable = table;
                        }
                    }
                }
                
            }
            
            return closestTable;
            },
                tableBeans,
                mousePosition);

        /**
         * Painter listener
         */
        ChangeListener<Object> painterListener = (o, oV, nV) -> {
            painter.drawAll(tableGrid.getValue(), 
                    tableGridParamsBean.getSnapToGrid(),
                    paneWidthProperty.getValue(), 
                    paneHeightProperty.getValue(),
                    tables.getValue(),
                    clickedTable.getValue(),
                    tableUnderMouse.getValue(),
                    mousePosition.getValue(),
                    movePosition.getValue(),
                    showMovedTable.getValue(),
                    showComposeLine.getValue());
        };
        
        // On which property changes should the painter draw the canvas?
        clickedTable.addListener(painterListener);
        tableGridParamsBean.numRowsProperty().addListener(painterListener);
        tableGridParamsBean.numColsProperty().addListener(painterListener);
        tableGridParamsBean.snapToGridProperty().addListener(painterListener);
        dateBean.dateProperty().addListener(painterListener);
        tableBeans.forEach(t -> {
            t.concertDateProperty().addListener(painterListener);
            t.tableProperty().addListener(painterListener);
        });
        mousePosition.addListener(painterListener);
        showMovedTable.addListener(painterListener);
        showComposeLine.addListener(painterListener);
        tables.addListener(painterListener);

        /**
         * Add or remove a table, listener
         */
        tableBeans.addListener((ListChangeListener<? super TableBean>) (s) -> {
            painter.drawAll(tableGrid.getValue(), 
                    tableGridParamsBean.getSnapToGrid(),
                    paneWidthProperty.getValue(), 
                    paneHeightProperty.getValue(),
                    tables.getValue(),
                    clickedTable.getValue(),
                    tableUnderMouse.getValue(),
                    mousePosition.getValue(),
                    movePosition.getValue(),
                    showMovedTable.getValue(),
                    showComposeLine.getValue());
        });
        
        /*
         * Draw once
         */
        painter.drawAll(tableGrid.getValue(),
                tableGridParamsBean.getSnapToGrid(),
                paneWidthProperty.getValue(), 
                paneHeightProperty.getValue(),
                tables.getValue(),
                clickedTable.getValue(),
                tableUnderMouse.getValue(),
                mousePosition.getValue(),
                movePosition.getValue(),
                showMovedTable.getValue(),
                showComposeLine.getValue());
    }

    public void changeSize(int size, ObservableList<TableBean> tableBeans, DateBean dateBean) {
        ITable table = clickedTable.getValue();
        if (table instanceof SingleTable) {
            for (TableBean tableBean : filterDate(tableBeans, dateBean))
                if (table.equals(tableBean.getTable())) {
                    tableBean.setTable(((SingleTable)tableBean.getTable()).withSize(size));
                    clickedTable.setValue(tableBean.getTable());
                    break;
                }
        }
    }
    
    public void changeNumberOfChairs(int numberOfChairs, ObservableList<TableBean> tableBeans,
            DateBean dateBean) {

        ITable table = clickedTable.getValue();
        if (table instanceof SingleTable) {
            for (TableBean tableBean : filterDate(tableBeans, dateBean)) 
                if (table.equals(tableBean.getTable())) {
                    tableBean.setTable(((SingleTable)tableBean.getTable()).withNumberOfChairs(numberOfChairs));
                    clickedTable.setValue(tableBean.getTable());
                    break;
                }   
            
        }
    }
    
    private double roundToClosestMultiple(double x, double y) {
        return Math.round(x / y) * y;
    }
    
    private FilteredList<TableBean> filterDate(ObservableList<TableBean> tableBeans, DateBean dateBean) {
        return tableBeans.filtered(t -> t.getConcertDate().equals(dateBean.getDate()));
    }
    
    private double mouseDistanceFromSingleTable(SingleTable table) {
        return Math.pow(mousePosition.getValue().getX() - table.getCoordinates().getX(), 2)
                    + Math.pow(mousePosition.getValue().getY() - table.getCoordinates().getY(), 2);
    }
    
    public DoubleProperty paneWidthProperty() { return paneWidthProperty; }
    public DoubleProperty paneHeightProperty() { return paneHeightProperty; }

    public ObjectProperty<SpinnerValueFactory<Integer>> clickedTableSizeSpinnerValueFactoryProperty() { return clickedTableSizeSpinnerValueFactoryProperty; }
    public ObjectProperty<SpinnerValueFactory<Integer>> clickedTableChairsSpinnerValueFactoryProperty() { return clickedTableChairsSpinnerValueFactoryProperty; }
    
    public Canvas canvas() { return canvas; }

    public ITable tableUnderMouse() { return tableUnderMouse.getValue(); }
    
}
