

import javafx.util.StringConverter;

public final class BooleanStringConverter extends StringConverter<Boolean> {

    @Override
    public String toString(Boolean object) {
        return object.toString();
    }

    @Override
    public Boolean fromString(String string) {
        return Boolean.parseBoolean(string);
    }

}
