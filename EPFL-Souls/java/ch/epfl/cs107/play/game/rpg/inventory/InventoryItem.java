package ch.epfl.cs107.play.game.rpg.inventory;

/**
 * Generic notion of an InventoryItem : represents the "idea" of an article one can put in an inventory.
 */
public interface InventoryItem {

	// These methods must be redefined.
	
	/**
	 * InventoryItem name getter
	 * @return (String) the name of the item
	 */
	String getName();
	
	/**
	 * InventoryItem weight getter
	 * @return (float) the weight of the item
	 */
	float getWeight();
	
	/**
	 * InvenotryItem price getter
	 * @return (int) the price of the item
	 */
	int getPrice();
}
