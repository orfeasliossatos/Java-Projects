package ch.epfl.cs107.play.game.rpg.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Generic notion of an Inventory which can store Items.
 */
public abstract class Inventory {
	
	// Has a maximum weight.
	private float maxWeight;
	// Inventory HAS A map of items. It IS NOT A map of items.
	private Map<InventoryItem, Integer> items;
	
	/**
	 * Default constructor of an Inventory.
	 * @param (float) maxWeight the maximum weight the inventory can carry
	 */
	public Inventory(float maxWeight) {
		this.maxWeight = maxWeight;
		this.items = new HashMap<>();
	}
	 
	/**
	 * This method calculates and returns the inventory's current weight
	 * @return (float) the inventory's current weight
	 */
	protected float getInventoryWeight() {
		float weight = 0;
		
		for (Entry<InventoryItem, Integer> pair : items.entrySet()) {
			weight += pair.getValue()*(pair.getKey().getWeight());
		}
		
		return weight;
	}
	
	/**
	 * This method allows adding items to the inventory.
	 * @param (InventoryItem) the item to be added
	 * @param (int) the quantity of said item to be added
	 * @return (boolean) whether or not the item was correctly added
	 */
	protected boolean addItem(InventoryItem item, int quantity) {
		boolean didAddItem;
		float addedWeight = item.getWeight() * quantity;
		
		// If the items to be added aren't too heavy, put them in the inventory
		if (getInventoryWeight() + addedWeight < maxWeight) {
			items.put(item, getQuantity(item) + quantity);
			didAddItem = true;
		} else {
			didAddItem = false;
		}
		
		return didAddItem;
	}
	 
	/**
	 * This method allows for the removal of items from the inventory
	 * @param (InventoryItem) the item to be removed
	 * @param (int) the quantity of said item to be removed
	 * @return (boolean) whether or not the item was correctly removed
	 */
	protected boolean removeItem(InventoryItem item, int quantity) {
		boolean didRemove;
		
		// Inquire whether the item exists, AND whether there are enough of them to be removed.
		if (items.get(item) >= quantity && containsItem(item)) {
			int currentQuantity = items.get(item);
			items.put(item, currentQuantity - quantity);
			didRemove = true;
			
			// Furthermore, if that item's quantity is 0, remove it from the inventory completely
			items.remove(item, 0);
			
		} else {
			didRemove = false;
		}
		
		return didRemove;
	}
	
	/**
	 * This method allows inquiry into whether or not the inventory contains a certain item
	 * There must be at least one of such item for the inventory to "contain" it.
	 * @param (InventoryItem) the item to be looked for
	 * @return (boolean) whether or not the item was found
	 */
	public boolean containsItem(InventoryItem item) {
		boolean didFind = false;
		
		// Loop through the available items.
		for (InventoryItem anItem : items.keySet()) {
			if (anItem == item && items.get(anItem) > 0) {
				didFind = true;
			}
		} 
		
		return didFind;
	}
	 
	/**
	 * This method allows inquiry into the quantity any given item in the inventory.
	 * @param (InventoryItem) the item whose quantity to be known
	 * @return (int) the quantity of said item
	 */
	protected int getQuantity(InventoryItem item) {
		if (containsItem(item)) {
			return items.get(item);			
		} else {
			return 0;
		}
	}
	
	/**
	 * This methods returns the inventory's current items in the form of an ArrayList, rather than a Map
	 * @return (List<InventoryItem>) a list of all items in the invenoty
	 */
	protected List<InventoryItem> getItems() {
		List<InventoryItem> itemList = new ArrayList<InventoryItem>();
		for (InventoryItem key : items.keySet()) {
			itemList.add(key);
		}
		return itemList;
	}
	
	/**
	 * Every Inventory also has a Holder
	 */
	public interface Holder {
		
		/**
		 * It can be inquired of all Holders whether or not they posses an item.
		 * @param (InventoryItem) the item.
		 * @return (boolean) whether or not the item is in the inventory
		 */
		boolean possesses(InventoryItem item);
		
	}
}
