package ch.epfl.rigel.gui;


import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * Classe contenant les paramètres déterminant la portion du ciel visible sur l'image. Bean JavaFX.
 * <p>
 * Publique, finale.
 * 
 * @author Orfeas Liossatos (310738)
 * @author Henrique Da Silva Gameiro (315689)
 */
public final class MiscBean {
    
    /// Propriétés
    private BooleanProperty doTrack         = new SimpleBooleanProperty();
    private BooleanProperty showInfo        = new SimpleBooleanProperty();
    private BooleanProperty showAsterisms   = new SimpleBooleanProperty();
    private BooleanProperty showStars       = new SimpleBooleanProperty();
    private BooleanProperty showPlanets     = new SimpleBooleanProperty();
    private BooleanProperty showSun         = new SimpleBooleanProperty();
    private BooleanProperty showMoon        = new SimpleBooleanProperty();
    private BooleanProperty showEarth       = new SimpleBooleanProperty();
    private BooleanProperty showHorizon     = new SimpleBooleanProperty();


    /// Méthodes retournant les propriétés
    
    /**
     * Retourne la propriété booléenne de tracking
     * @return
     *          la propriété booléenne de tracking
     */
    public BooleanProperty doTrackProperty() {
        return this.doTrack;
    }
    
    /**
     * Retourne la propriété booléenne de montrer l'information
     * @return
     *          la propriété booléenne de montrer l'information
     */
    public BooleanProperty showInfoProperty() {
        return this.showInfo;
    }

    /**
     * Retourne la propriété booléenne de montrer l'astérisme
     * @return
     *          la propriété booléenne de montrer l'astérisme
     */
    public BooleanProperty showAsterismsProperty() {
        return this.showAsterisms;
    }
    
    /**
     * Retourne la propriété booléenne de montrer les étoiles
     * @return
     *          la propriété booléenne de montrer les étoiles
     */
    public BooleanProperty showStarsProperty() {
        return this.showStars;
    }
    
    /**
     * Retourne la propriété booléenne de montrer les planètes
     * @return
     *          la propriété booléenne de montrer les planètes
     */
    public BooleanProperty showPlanetsProperty() {
        return this.showPlanets;
    }

    /**
     * Retourne la propriété booléenne de montrer le Soleil
     * @return
     *          la propriété booléenne de montrer le Soleil
     */
    public BooleanProperty showSunProperty() {
        return this.showSun;
    }

    /**
     * Retourne la propriété booléenne de montrer la Lune
     * @return
     *          la propriété booléenne de montrer la Lune
     */
    public BooleanProperty showMoonProperty() {
        return this.showMoon;
    }
    
    /**
     * Retourne la valeur booléenne montrer la Terre
     * @return
     *          la valeur booléenne montrer la Terre
     */
    public BooleanProperty showEarthProperty() {
        return this.showEarth;
    }
    
    /**
     * Retourne la valeur booléenne montrer l'horizon
     * @return
     *          la valeur booléenne montrer l'horizon
     */
    public BooleanProperty showHorizonProperty() {
        return this.showHorizon;
    }
    
    
    /// Méthodes retournant le contenu des propriétés
    
    /**
     * Retourne la valeur booléenne de tracking
     * @return
     *          la valeur booléenne de tracking
     */
    public boolean getDoTrack() {
        return this.doTrack.get();
    }

    /**
     * Retourne la valeur booléenne montrer l'information
     * @return
     *          la valeur booléenne montrer l'information
     */
    public boolean getShowInfo() {
        return this.showInfo.get();
    }

    /**
     * Retourne la valeur booléenne montrer les astérismes
     * @return
     *          la valeur booléenne montrer les astérismes
     */
    public boolean getShowAsterisms() {
        return this.showAsterisms.get();
    }
    
    /**
     * Retourne la valeur booléenne montrer les étoiles
     * @return
     *          la valeur booléenne montrer les étoiles
     */
    public boolean getShowStars() {
        return this.showStars.get();
    }

    /**
     * Retourne la valeur booléenne montrer les planètes
     * @return
     *          la valeur booléenne montrer les planètes
     */
    public boolean getShowPlanets() {
        return this.showPlanets.get();
    }

    /**
     * Retourne la valeur booléenne montrer le Soleil
     * @return
     *          la valeur booléenne montrer le Soleil
     */
    public boolean getShowSun() {
        return this.showSun.get();
    }

    /**
     * Retourne la valeur booléenne montrer la Lune
     * @return
     *          la valeur booléenne montrer la Lune
     */
    public boolean getShowMoon() {
        return this.showMoon.get();
    }
    
    /**
     * Retourne la valeur booléenne montrer la Terre
     * @return
     *          la valeur booléenne montrer la Terre
     */
    public boolean getShowEarth() {
        return this.showEarth.get();
    }
    
    /**
     * Retourne la valeur booléenne montrer l'horizon
     * @return
     *          la valeur booléenne montrer l'horizon
     */
    public boolean getShowHorizon() {
        return this.showHorizon.get();
    }
    
    /// Méthodes permettant de modifier le contenu de la propriété
    
    /**
     * Modifie la valeur booléenne de tracking pour qu'elle soit égale à {@code center}
     * @param doTrack
     *          si oui ou non on suit l'étoile sélectionnée
     */
    public void setDoTrack(boolean doTrack) {
        this.doTrack.set(doTrack);
    }
    
    /**
     * Modifie la valeur booléenne de montrer l'information pour qu'elle soit égale à {@code showInfo}
     * @param showInfo
     *          si oui ou non on montre l'information relative à un objet céleste 
     */
    public void setShowInfo(boolean showInfo) {
        this.showInfo.set(showInfo);
    }
    
    /**
     * Modifie la valeur booléenne de montrer les astérismes pour qu'elle soit égale à {@code showAsterisms}
     * @param showAsterisms
     *          si oui ou non on montre les astérismes 
     */
    public void setShowAsterisms(boolean showAsterisms) {
        this.showAsterisms.set(showAsterisms);
    }
    
    /**
     * Modifie la valeur booléenne de montrer les étoiles pour qu'elle soit égale à {@code showStars}
     * @param showStars
     *          si oui ou non on montre les étoiles 
     */
    public void setShowStars(boolean showStars) {
        this.showStars.set(showStars);
    }

    /**
     * Modifie la valeur booléenne de montrer les planètes pour qu'elle soit égale à {@code showPlanets}
     * @param showPlanets
     *          si oui ou non on montre les planètes 
     */
    public void setShowPlanets(boolean showPlanets) {
        this.showPlanets.set(showPlanets);
    }

    /**
     * Modifie la valeur booléenne de montrer les planètes pour qu'elle soit égale à {@code showSun}
     * @param showSun
     *          si oui ou non on montre le Soleil 
     */
    public void setShowSun(boolean showSun) {
        this.showSun.set(showSun);
    }

    /**
     * Modifie la valeur booléenne de montrer les planètes pour qu'elle soit égale à {@code showMoon}
     * @param showMoon
     *          si oui ou non on montre la Lune 
     */
    public void setShowMoon(boolean showMoon) {
        this.showMoon.set(showMoon);
    }
    
    /**
     * Modifie la valeur booléenne de montrer les planètes pour qu'elle soit égale à {@code showEarth}
     * @param showEarth
     *          si oui ou non on montre la Terre 
     */
    public void setShowEarth(boolean showEarth) {
        this.showEarth.set(showEarth);
    }
    
    /**
     * Modifie la valeur booléenne de montrer les planètes pour qu'elle soit égale à {@code showHorizon}
     * @param showHorizon
     *          si oui ou non on montre l'horizon 
     */
    public void setShowHorizon(boolean showHorizon) {
        this.showHorizon.set(showHorizon);
    }

}

