

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Class containing system messages. JavaFX Bean
 * @author Orfeas Liossatos
 *
 */
public final class MessageBean {
    
    private final StringProperty errorMessageProperty;
    private final StringProperty selectionMessageProperty;
    
    public MessageBean(String errorMessage, String selectionMessage) {
        errorMessageProperty = new SimpleStringProperty(errorMessage);
        selectionMessageProperty = new SimpleStringProperty(selectionMessage);
    }
    
    public MessageBean() {
        this("", "");
    }
    
    public StringProperty errorMessageProperty() { return errorMessageProperty; }
    public StringProperty selectionMessageProperty() { return selectionMessageProperty; }
    
    public String getErrorMessage() {return errorMessageProperty.getValue(); }
    public String getSelectionMessage() {return selectionMessageProperty.getValue(); }

    public void setErrorMessage(String errorMessage) { errorMessageProperty.set(errorMessage); }
    public void setSelectionMessage(String selectionMessage) { selectionMessageProperty.set(selectionMessage); }
}
