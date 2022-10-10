package back;

/**
 * Reservation preferences. Determines automatic seating order.
 * @author Orfeas Liossatos
 *
 */
public enum Preference {

    LEFT,
    RIGHT,
    BACK,
    FRONT,
    MIDDLE,
    NONE;
    
    /**
     * Converts a string into a Preference
     * @param s the string
     * @return the corresponding Preference
     */
    public static Preference fromString(String s) {
        switch(s.toLowerCase()) {
        case "gauche" : return LEFT;
        case "droite" : return RIGHT;
        case "derrière" : return BACK;
        case "devant" : return FRONT;
        case "milieu" : return MIDDLE;
        case "centre" : return MIDDLE;
        case "aucune" : return NONE;
        default : return NONE;
        }
    }
    
    @Override
    public String toString() {
        switch(this) {
        case LEFT   : return "Gauche"; 
        case RIGHT  : return "Droite";
        case BACK   : return "Derrière";
        case FRONT  : return "Devant"; 
        case MIDDLE : return "Centre";
        case NONE   : return "Aucune"; 
        default     :  return ""; 
        }
    }
    
}
