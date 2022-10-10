package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.CelestialObject;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public final class StarMutable {
        
        //utilisé pour la barre de recherche de la partie bonus
        
        public SimpleStringProperty name;
        public SimpleStringProperty hipparcos;
        public ObjectProperty<CelestialObject> celestialObject;
     
        public StarMutable(String name, String hipparcos, CelestialObject c) {
            this.name = new SimpleStringProperty(name);
            this.hipparcos = new SimpleStringProperty(hipparcos);
            this.celestialObject = new SimpleObjectProperty<CelestialObject>(c);
        }
     
        public String name() {
            return this.name.getValue();
        }
        public void setName(String fName) {
            name.set(fName);
        }
        
        public String hipparcos() {
            return this.hipparcos.getValue();
        }
        public CelestialObject celestialObject() {
            return this.celestialObject.getValue();
        }
            

            
    
}
