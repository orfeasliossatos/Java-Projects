// Placing ARPGInventory in this package allows arpg actors to use its protected methods!
package ch.epfl.cs107.play.game.arpg.actor; 


import java.util.List;

import ch.epfl.cs107.play.game.rpg.inventory.Inventory;
import ch.epfl.cs107.play.game.rpg.inventory.InventoryItem;

/**
 * ARPGInventory is an Inventory that further adds a money count.
 */
public class ARPGInventory extends Inventory {
	
	private int money;	 // Money is the amount of "cash" contained in the inventory
	private int fortune; // Fortune is the value of all the inventory's items. Is recalculated when needed.
	
	private final static int MAX_MONEY = 999;
	private final static int DEFAULT_MONEY = 0;
	private final static int DEFAULT_MAX_WEIGHT = 1337;
	
	/**
	 * ARPGInventory repeated constructor. 
	 */
	protected ARPGInventory() {
		this(DEFAULT_MONEY, DEFAULT_MAX_WEIGHT);
	}
	
	/**
	 * ARPGInventory default constructor.
	 * @param (int) starting money count
	 * @param (float) starting weight
	 */
	protected ARPGInventory(int money, float weight) {
		super(weight);

		if (money >= MAX_MONEY) {
			this.money = MAX_MONEY;
		} else {
			this.money = money;			
		}
		
	}
	
	/// ARPGInventory specific methods
	
	/**
	 * This methods calculates the total fortune of the ARPGInventory.
	 * Fortune is calculated by summing up the total price of the ARPGInventory's contents, 
	 * plus the ARPGInventory's money total. 
	 * @return (int) the fortune of the inventory.
	 */
	private int calculateFortune() {
		int fortune = 0;
		
		fortune += this.money;
		// Multiply the value of an item by its quantity in the inventory
		for (InventoryItem item : getItems()) {
			fortune += ((ARPGItem)item).getPrice() * getQuantity(item);
		}
		
		return fortune;
	}
	
	/**
	 * Calculates and returns the current fortune.
	 * @return (int) fortune
	 */
	public int getFortune() {
		fortune = calculateFortune();
		return this.fortune;
	}
	
	/**
	 * Returns total money (cash) amount
	 * @return (int) money
	 */
	public int getMoney() {
		return this.money;
	}
	
	/**
	 * Adds (forced positive amount) to the money total
	 * @param (int) added money
	 */
	protected void addMoney(int money) {
		this.money += Math.abs(money);
		
		// You cannot add more money than the maximum amount.
		if (this.money > MAX_MONEY) {
			this.money = MAX_MONEY;
		}
	}
	
	/**
	 * Removes (forces negative amount) to the money total
	 * @param (int) removed money
	 */
	protected void removeMoney(int money) {
		this.money -= Math.abs(money);
		
		// The player cannot go into debt.
		if (this.money < 0) {
			this.money = 0;
		}
	}
	
	/// ARPGInventory extends Inventory
	
	@Override
	protected boolean addItem(InventoryItem item, int quantity) {
		return super.addItem(item, quantity);
	}

	@Override
	protected boolean removeItem(InventoryItem item,int quantity) {
		return super.removeItem(item, quantity);
	}
	
	@Override
	protected List<InventoryItem> getItems() {
		return super.getItems();
	}
	
}
