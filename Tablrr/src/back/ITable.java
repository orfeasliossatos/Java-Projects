package back;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import javafx.geometry.Point2D;

public interface ITable extends Comparable<ITable> {

    public Point2D getCoordinates();
    
    public TreeSet<Integer> getTableNumber();
    
    public int getNumberOfChairs();
    
    public String csvString();
    
    public static ITable parse(String s) throws TableNumberFormatException {
        try {
            String[] numberStrings = s.split("-");
            if (numberStrings.length <= 0) {
                throw new TableNumberFormatException();
            } else if (numberStrings.length == 1) {
                if (numberStrings[0].isBlank())
                    return SingleTable.chairBehind();
                else 
                    return new SingleTable(Integer.parseInt(numberStrings[0]));
            } else {
                List<Integer> tableNumbers = new ArrayList<>();
                for (String numberString : numberStrings) {
                    if (numberString.isBlank())
                        tableNumbers.add(0);
                    else
                        tableNumbers.add(Integer.parseInt(numberString));
                }
                return new CompositeTable(tableNumbers);
            }
        } catch (NumberFormatException e) {
            throw new TableNumberFormatException();
        }
    };
    
}
