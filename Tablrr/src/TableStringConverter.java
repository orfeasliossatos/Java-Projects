

import back.ITable;
import back.TableNumberFormatException;
import javafx.util.StringConverter;

public class TableStringConverter extends StringConverter<ITable> {

    @Override
    public String toString(ITable object) {
        return object.toString();
    }

    @Override
    public ITable fromString(String string) throws TableNumberFormatException {
        return ITable.parse(string);
    }

}
