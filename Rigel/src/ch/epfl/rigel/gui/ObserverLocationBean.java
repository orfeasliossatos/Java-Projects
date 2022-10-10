package ch.epfl.rigel.gui;

import ch.epfl.rigel.coordinates.GeographicCoordinates;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * Classe contenant la position de l'observateur, en degrés. Bean JavaFX.
 * <p>
 * Publique, finale.
 * 
 * @author Orfeas Liossatos (310738)
 * @author Henrique Da Silva Gameiro (315689)
 */
public final class ObserverLocationBean {
    
    /// Propriétés
    private DoubleProperty lonDeg = new SimpleDoubleProperty();
    private DoubleProperty latDeg = new SimpleDoubleProperty();
    private ObjectBinding<GeographicCoordinates> coordinates =
                Bindings.createObjectBinding(
                        ()-> GeographicCoordinates.ofDeg(lonDeg.get(), latDeg.get()),
                        lonDeg, latDeg);
    
    
    /// Méthodes retournant les propriétés
    
    /**
     * Retourne la propriété de la longitude de la position de l'observateur, en degrés
     * @return
     *          la propriété de la longitude de la position de l'observateur, en degrés
     */
    public DoubleProperty lonDegProperty() {
        return this.lonDeg;
    }
    
    /**
     * Retourne la propriété de la latitude de la position de l'observateur, en degrés
     * @return
     *          la propriété de la latitude de la position de l'observateur, en degrés
     */
    public DoubleProperty latDegProperty() {
        return this.latDeg;
    }

    /**
     * Retourne le lien des coordonnées géographiques de l'observateur
     * @return
     *          le lien des coordonnées géographiques de l'observateur
     */
    public ObjectBinding<GeographicCoordinates> coordinatesProperty() {
        return this.coordinates;
    }
    
    /// Méthodes retournant le contenu des propriétés
    
    /**
     * Retourne la longitude de la position de l'observateur, en degrés
     * @return
     *          la longitude de la position de l'observateur, en degrés
     */
    public double getLonDeg() {
        return this.lonDeg.get();
    }

    /**
     * Retourne la latitude de la position de l'observateur, en degrés
     * @return
     *          la latitude de la position de l'observateur, en degrés
     */
    public double getLatDeg() {
        return this.latDeg.get();
    }
    
    /**
     * Retourne les coordonnées géographiques de l'observateur
     * @return
     *          les coordonnées géographiques de l'observateur
     */
    public GeographicCoordinates getCoordinates() {
        return this.coordinates.getValue();
    }
    
    /// Méthodes permettant de modifier le contenu de la propriété
    
    /**
     * Modifie la longitude de la position de l'observateur
     * pour qu'elle soit égale à {@code lonDeg} degrés
     * @param lonDeg
     *          la nouvelle longitude de la position de l'observateur (en degrés)
     */
    public void setLonDeg(double lonDeg) {
        this.lonDeg.set(lonDeg);
    }
    
    /**
     * Modifie la latitude de la position de l'observateur
     * pour qu'elle soit égale à {@code latDeg} degrés
     * @param latDeg
     *          la nouvelle latitude de la position de l'observateur (en degrés)
     */
    public void setLatDeg(double latDeg) {
        this.latDeg.set(latDeg);
    }
    
    /**
     * Modifie les coordonnées géographiques de l'observateur
     * pour qu'elle soit égale à {@code coordinates}
     * @param coordinates
     *          les nouvelles coordonnées géographiques de l'observateur (en degrés)
     */
    public void setCoordinates(GeographicCoordinates coordinates) {
        setLonDeg(coordinates.lonDeg());
        setLatDeg(coordinates.latDeg());
    }
   
}
