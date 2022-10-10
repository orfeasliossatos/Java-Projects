package ch.epfl.rigel.gui;


import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Classe contenant les paramètres déterminant la portion du ciel visible sur l'image. Bean JavaFX.
 * <p>
 * Publique, finale.
 * 
 * @author Orfeas Liossatos (310738)
 * @author Henrique Da Silva Gameiro (315689)
 */
public final class ViewingParametersBean {
    
    /// Propriétés
    private DoubleProperty fieldOfViewDeg                   = new SimpleDoubleProperty();
    private ObjectProperty<HorizontalCoordinates> center    = new SimpleObjectProperty<>(null);

    /// Méthodes retournant les propriétés
    
    /**
     * Retourne la propriété du champ de vue
     * @return
     *          la propriété du champ de vue
     */
    public DoubleProperty fieldOfViewDegProperty() {
        return this.fieldOfViewDeg;
    }
    
    /**
     * Retourne la propriété des coordonnées du centre de projection
     * @return
     *          la propriété des coordonnées du centre de projection
     */
    public ObjectProperty<HorizontalCoordinates> centerProperty() {
        return this.center;
    }
    
    /// Méthodes retournant le contenu des propriétés
    
    /**
     * Retourne le champ de vue en degrés
     * @return
     *          le champ de vue en degrés
     */
    public double getFieldOfViewDeg() {
        return this.fieldOfViewDeg.get();
    }
    
    /**
     * Retourne le centre de projection
     * @return
     *          le centre de projection
     */
    public HorizontalCoordinates getCenter() {
        return this.center.getValue();
    }
    
    /// Méthodes permettant de modifier le contenu de la propriété
    
    /**
     * Modifie le champ de vue pour qu'il soit égal à {@code fieldOfViewDeg} degrés
     * @param fieldOfViewDeg
     *          le nouveau champ de vue (en degrés)
     */
    public void setFieldOfViewDeg(double fieldOfViewDeg) {
        this.fieldOfViewDeg.set(fieldOfViewDeg);
    }
    
    /**
     * Modifie le centre d'observation pour qu'il soit égal à {@code center}
     * @param center
     *          le nouveau centre d'observation
     */
    public void setCenter(HorizontalCoordinates center) {
        this.center.setValue(center);
    }

}

