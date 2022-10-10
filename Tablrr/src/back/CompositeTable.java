package back;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TreeSet;

import javafx.geometry.Point2D;

public final class CompositeTable implements ITable {

    private final Set<ITable> tables;

    public CompositeTable(List<Integer> numbers) {
        Set<ITable> tables = new TreeSet<>();
        for (Integer i : numbers) {
            tables.add(new SingleTable(i));
        }
        this.tables = Set.copyOf(tables);
    }
    
    public CompositeTable(Set<ITable> tables) {
        this.tables = Set.copyOf(tables);
    }
    
    public Set<SingleTable> getSingleTables() {
        Set<SingleTable> singleTables = new TreeSet<>();
        for (ITable table : tables) {
            if (table instanceof SingleTable) {
                singleTables.add((SingleTable)table);
            } else {
                singleTables.addAll(((CompositeTable)table).getSingleTables());
            }
        }
        return singleTables;
    }
    
    public int getNumberOfSingleTables() {
        int total = 0;
        for (ITable table : tables) {
            if (table instanceof SingleTable) {
                total++;
            } else {
                total += ((CompositeTable)table).getNumberOfSingleTables();
            }
        }
        return total;
    }
    
    @Override
    public TreeSet<Integer> getTableNumber() {
        TreeSet<Integer> tableNumbers = new TreeSet<>();
        tables.forEach(t -> tableNumbers.addAll(t.getTableNumber()));
        return tableNumbers;
    }
    
    @Override
    public String toString() {
        StringJoiner j = new StringJoiner("-");
        getSingleTables().forEach(t -> j.add(t.toString()));
        return j.toString();
    }
    
    @Override
    public String csvString() {
        StringJoiner j = new StringJoiner(",");
        tables.forEach(t -> j.add(t.csvString()));
        return j.toString();
    }

    @Override
    public int compareTo(ITable o) {
        return 0;
    }

    @Override
    public Point2D getCoordinates() {
        Point2D center = Point2D.ZERO;

        Set<SingleTable> singleTables = getSingleTables();
        for (SingleTable singleTable : singleTables) {
            center = center.add(singleTable.getCoordinates());
        }
        
        return center.multiply(1 / (double) singleTables.size());
    }

    @Override
    public int getNumberOfChairs() {
        int numberOfChairs = 0;
        
        for (ITable table : tables) {
            numberOfChairs += table.getNumberOfChairs();
        }
        
        return numberOfChairs;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof CompositeTable) {
            if (((CompositeTable)other).getTableNumber().equals(this.getTableNumber())) 
                return true;
        }
        return false;
    }
    
}
