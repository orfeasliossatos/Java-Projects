/**
 * The conditions that must be satisfied before a method is called.
 * @author Orfeas Liossatos
 *
 */
public final class Preconditions {

    private Preconditions() {}
    
    /**
     * Check that the argument is true, or throw IllegalArgumentException.
     * @param arg
     */
    public static void checkArgument(boolean arg) {
        if (!arg) {
            throw new IllegalArgumentException("The argument is false");
        }
    }
    
}
