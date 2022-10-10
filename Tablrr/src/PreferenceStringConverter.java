import back.Preference;
import javafx.util.StringConverter;

public final class PreferenceStringConverter extends StringConverter<Preference> {

    @Override
    public String toString(Preference object) {
        return object.toString();
    }

    @Override
    public Preference fromString(String string) {
        return Preference.fromString(string);
    }

}
