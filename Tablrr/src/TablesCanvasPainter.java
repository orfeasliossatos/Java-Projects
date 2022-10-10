import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import back.CompositeTable;
import back.ITable;
import back.SingleTable;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * Class representing an object which can draw the table grid onto the canvas.
 * @author Orfeas Liossatos
 *
 */
public final class TablesCanvasPainter {

    // COLORS
    private static final Color BACKGROUND_COLOR = Color.rgb(220, 220, 220);
    private static final Color GRID_LINE_COLOR = Color.rgb(180, 180, 180);
    private static final int COLOR_HUE_PHASE = 250;
    private static final double COLOR_HUE_ANGULAR_VELOCITY = 12.8d;
    private static final double COLOR_SATURATION = 0.6d;
    private static final double COLOR_BRIGHTNESS = 1d;
    
    // VALUES
    private static final double GRID_LINE_WIDTH = 2.0d;
    private static final double OUTLINE_THICKNESS = 2d;
    private static final double CHAIR_DIAMETER = 4d;
    private static final double CHAIR_TABLE_DISTANCE = 8d;
    
    // FONT
    private static Font FONT_AWESOME;
    private static final String FONT_AWESOME_NAME = "/Font Awesome 5 Free-Solid-900.otf";
    private static final double FONT_AWESOME_SIZE = 22d; 
    
    private final Canvas canvas;
    private final GraphicsContext context;

    public TablesCanvasPainter(Canvas canvas) {
        this.canvas = canvas;
        this.context = canvas.getGraphicsContext2D();
        
        try (InputStream fontStream = getClass().getResourceAsStream(FONT_AWESOME_NAME)) {
            FONT_AWESOME = Font.loadFont(fontStream, FONT_AWESOME_SIZE);
            fontStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        context.setTextAlign(TextAlignment.CENTER);
        context.setTextBaseline(VPos.CENTER);
        context.setFont(FONT_AWESOME);
    }
    
    /**
     * Efface le canevas
     */
    public void clear() {
        context.setFill(BACKGROUND_COLOR);
        context.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }
    
    /**
     * Draws the grid lines
     * @param tableGrid
     */
    public void drawGridLines(TableGrid tableGrid, double paneWidth, double paneHeight) {
        
        context.setStroke(GRID_LINE_COLOR);
        context.setLineWidth(GRID_LINE_WIDTH);
        
        int numRows = tableGrid.getNumRows();
        for (int i = 0; i < numRows; i++) {
            double linePos = (i + 1) * paneHeight / (double) (numRows + 1);
            context.strokeLine(0, linePos, paneWidth, linePos);
        }
        
        int numCols = tableGrid.getNumCols();
        for (int j = 0; j < numCols; j++) {
            double linePos = (j + 1) * paneWidth / (double) (numCols + 1);
            context.strokeLine(linePos, 0, linePos, paneHeight);
        }
    }
    
    public void drawSelection(ITable table) {

        context.setStroke(Color.BLACK);
        context.setLineWidth(OUTLINE_THICKNESS);
        
        if (table instanceof SingleTable) {
            double radius = ((SingleTable) table).getSize();
    
            context.strokeArc(
                    table.getCoordinates().getX() - radius,
                    table.getCoordinates().getY() - radius, 
                    radius * 2, radius * 2, 
                    0, 360, 
                    ArcType.OPEN);
        } else {
            for (SingleTable singleTable : ((CompositeTable)table).getSingleTables()) {
                double radius = singleTable.getSize();
                
                context.strokeArc(
                        singleTable.getCoordinates().getX() - radius,
                        singleTable.getCoordinates().getY() - radius, 
                        radius * 2, radius * 2, 
                        0, 360, 
                        ArcType.OPEN);
            }
        }
    }
    
    public void drawTables(Set<ITable> tables) {
        for (ITable table : tables) {
            if (table instanceof SingleTable) {
                drawSingleTable((SingleTable)table);
            } else {
                // Extract set of single tables
                Set<SingleTable> singleTables = ((CompositeTable)table).getSingleTables();
                
                // Composition lines
                for (SingleTable thisTable : singleTables) 
                    for (SingleTable otherTable : singleTables) 
                        if (!otherTable.equals(thisTable))
                            drawComposedLine(thisTable, otherTable);
                    
                // The single tables
                for (SingleTable singleTable : singleTables) 
                    drawSingleTable(singleTable);
            }
        }
    }
    
    private void drawSingleTable(SingleTable table) {
        // Draw table
        double radius = table.getSize();
        
        context.setFill(getColorForSize(table.getSize()));
        context.fillOval(
                table.getCoordinates().getX() - radius, 
                table.getCoordinates().getY() - radius, 
                radius * 2, radius * 2);
        
        // Draw table number
        context.setFill(Color.BLACK);
        
        double verticalAdjustment = 1;
        context.fillText(
                String.valueOf(table.getTableNumber().first()), 
                table.getCoordinates().getX(), 
                table.getCoordinates().getY() + verticalAdjustment);
        
        // Draw chairs
        double chairRadius = table.getSize() + CHAIR_TABLE_DISTANCE;
        for (int i = 0; i < table.getNumberOfChairs(); i++) {
            double angle = Math.PI / 2 - Math.PI / 20 - Math.PI / 10 * (i - (double) table.getNumberOfChairs() / 2);
            context.fillOval(
                    table.getCoordinates().getX() + Math.cos(angle) * chairRadius - CHAIR_DIAMETER / 2, 
                    table.getCoordinates().getY() - Math.sin(angle) * chairRadius - CHAIR_DIAMETER / 2,
                    CHAIR_DIAMETER, CHAIR_DIAMETER);
        }
        
    }
    
    private Color getColorForSize(int size) {
        return Color.hsb(COLOR_HUE_PHASE + -(double) size * COLOR_HUE_ANGULAR_VELOCITY, COLOR_SATURATION, COLOR_BRIGHTNESS);
    }
    
    public void drawMovedTable(Point2D movePosition, ITable clickedTable) {

        if (clickedTable instanceof SingleTable) {
            double radius = ((SingleTable) clickedTable).getSize();
            
            context.setFill(Color.rgb(200, 200, 200, 0.5));
            context.fillOval(
                    movePosition.getX() - radius,
                    movePosition.getY() - radius, 
                    radius * 2, radius * 2);
    
            context.setStroke(Color.BLACK);
            context.strokeArc(
                    movePosition.getX() - radius,
                    movePosition.getY() - radius, 
                    radius * 2, radius * 2, 
                    0, 360, 
                    ArcType.OPEN);
        } else {

            Set<SingleTable> singleTables = ((CompositeTable)clickedTable).getSingleTables();
            for (SingleTable singleTable : singleTables) {
                Point2D moveDifference = movePosition.subtract(clickedTable.getCoordinates());
                double radius = singleTable.getSize();
                
                context.setFill(Color.rgb(200, 200, 200, 0.5));
                context.fillOval(
                        singleTable.getCoordinates().getX() + moveDifference.getX() - radius,
                        singleTable.getCoordinates().getY() + moveDifference.getY() - radius, 
                        radius * 2, radius * 2);
        
                context.setStroke(Color.BLACK);
                context.strokeArc(
                        singleTable.getCoordinates().getX() + moveDifference.getX() - radius,
                        singleTable.getCoordinates().getY() + moveDifference.getY() - radius, 
                        radius * 2, radius * 2, 
                        0, 360, 
                        ArcType.OPEN);
            }
        }
        
    }
    
    
    private void drawComposedLine(ITable table1, ITable table2) {
        Point2D table1Pos = table1.getCoordinates();
        Point2D table2Pos = table2.getCoordinates();
        
        context.setStroke(Color.BLACK);
        context.setLineWidth(OUTLINE_THICKNESS);
        context.strokeLine(
                table1Pos.getX(), 
                table1Pos.getY(), 
                table2Pos.getX(), 
                table2Pos.getY());
    }
    
    public void drawComposeLine(ITable clickedTable, Point2D mousePosition) {
        Point2D tablePosition = clickedTable.getCoordinates();
        
        context.setStroke(Color.BLACK);
        context.strokeLine(
                tablePosition.getX(), 
                tablePosition.getY(), 
                mousePosition.getX(), 
                mousePosition.getY());
        
    }
    
    public void drawHover(ITable table) {
        context.setStroke(Color.BLACK);
        context.setLineWidth(OUTLINE_THICKNESS);
        context.setLineDashes(5);
        
        if (table instanceof SingleTable) {
            double radius = ((SingleTable) table).getSize();
            
            context.strokeArc(
                    table.getCoordinates().getX() - radius,
                    table.getCoordinates().getY() - radius, 
                    radius * 2, radius * 2, 
                    0, 360, 
                    ArcType.OPEN);
            
        } else {
            for (SingleTable singleTable : ((CompositeTable)table).getSingleTables()) {
                double radius = singleTable.getSize();
                
                context.strokeArc(
                        singleTable.getCoordinates().getX() - radius,
                        singleTable.getCoordinates().getY() - radius, 
                        radius * 2, radius * 2, 
                        0, 360, 
                        ArcType.OPEN);
            }
        }
        
        context.setLineDashes(0);
    }
    
    public void drawChairsAndStage() {
        context.setFill(GRID_LINE_COLOR);
        double margin = 50d;
        double thickness = 30d;
        context.fillRect(margin, margin, canvas.getWidth() - 2 * margin, thickness);
        context.fillRect(margin, canvas.getHeight() - (margin + thickness), canvas.getWidth() - 2 * margin, thickness);
        
        context.setFill(Color.BLACK);
        
        context.fillText("Chaises", canvas.getWidth() / 2, margin + thickness / 2);
        context.fillText("Scene", canvas.getWidth() / 2, canvas.getHeight() - (margin + thickness / 2));
        
    }
    
    /**
     * Efface le canevas, puis dessine les tables
     */
    public void drawAll(TableGrid tableGrid, boolean snapToGrid, double paneWidth, double paneHeight, Set<ITable> tables,
            ITable clickedTable, ITable tableUnderMouse, Point2D mousePosition, Point2D movePosition, 
            boolean showMovedTable, boolean showComposeLine) {
        
        this.clear();
        
        
        if (snapToGrid)
            drawGridLines(tableGrid, paneWidth, paneHeight);

        //drawChairsAndStage();
        
        if (clickedTable != null && showComposeLine)
            drawComposeLine(clickedTable, mousePosition);
        
        if (tables.size() != 0)
            drawTables(tables);
        
        if (clickedTable != null && showMovedTable) 
            drawMovedTable(movePosition, clickedTable);
        
        if (clickedTable != null) 
            drawSelection(clickedTable);
        
        if (tableUnderMouse != null && !tableUnderMouse.equals(clickedTable))
            drawHover(tableUnderMouse);
    }
    
}
