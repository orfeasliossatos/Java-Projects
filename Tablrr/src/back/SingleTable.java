package back;
import java.util.StringJoiner;
import java.util.TreeSet;

import javafx.geometry.Point2D;

public final class SingleTable implements ITable {


    private static final int DEFAULT_TABLE_RADIUS     = 30;  
    private static final int DEFAULT_NUMBER_OF_CHAIRS = 5;
    
    private final int tableNumber;
    private final Point2D coordinates;
    private final int numberOfChairs;
    private final int size;
    
    public SingleTable(int tableNumber, Point2D coordinates, int numberOfChairs, int size) {
        this.tableNumber = tableNumber >= 1 ? tableNumber : 0;
        this.coordinates = coordinates;
        this.numberOfChairs = numberOfChairs;
        this.size = size;
    }
    
    public SingleTable(int tableNumber, Point2D coordinates, int numberOfChairs) {
        this(tableNumber, coordinates, numberOfChairs, DEFAULT_TABLE_RADIUS);
    }
    
    public SingleTable(int tableNumber, Point2D coordinates) {
        this(tableNumber, coordinates, DEFAULT_NUMBER_OF_CHAIRS, DEFAULT_TABLE_RADIUS);
    }
    
    public SingleTable(int tableNumber) {
        this(tableNumber, Point2D.ZERO);
    }
    
    public static SingleTable chairBehind() {
        return new SingleTable(0, Point2D.ZERO);
    }
    
    public SingleTable withCoordinates(Point2D coordinates) {
        return new SingleTable(this.tableNumber, coordinates, this.numberOfChairs, this.size);
    }
    
    public SingleTable withNumberOfChairs(int numberOfChairs) {
        return new SingleTable(this.tableNumber, this.coordinates, numberOfChairs, this.size);
    }
    
    public SingleTable withSize(int size) {
        return new SingleTable(this.tableNumber, this.coordinates, this.numberOfChairs, size);
    }
    
    public int getSize() { 
        return this.size; 
    }
    
    @Override
    public TreeSet<Integer> getTableNumber() {
        TreeSet<Integer> singleton = new TreeSet<>();
        singleton.add(tableNumber);
        return singleton;
    }
    
    @Override
    public String toString() {
        return tableNumber >= 1 ? String.valueOf(tableNumber) : "-";
    }
    
    @Override
    public String csvString() {
        StringJoiner j = new StringJoiner(",");
        j.add(String.valueOf(tableNumber))
            .add(String.valueOf(coordinates.getX()))
            .add(String.valueOf(coordinates.getY()))
            .add(String.valueOf(numberOfChairs))
            .add(String.valueOf(size));
        return j.toString();
    }

    @Override
    public int compareTo(ITable o) {
        if (o instanceof SingleTable)
            return Integer.compare(tableNumber, ((SingleTable) o).tableNumber);
        else {
            return 0;
        }
    }

    @Override
    public Point2D getCoordinates() {
        return this.coordinates;
    }

    @Override
    public int getNumberOfChairs() {
        return numberOfChairs;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof SingleTable) {
            if (((SingleTable)other).getTableNumber().equals(this.getTableNumber())) 
                return true;
        } 
        return false;
    }

}
