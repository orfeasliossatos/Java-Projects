package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.rpg.inventory.InventoryItem;

/*
 * Items found in game : ARPG
 */
public enum ARPGItem implements InventoryItem {
	
	// Enumeration of elements
	BOW			(0, 50,	"Bow",		"zelda/bow.icon"),
	STAFF		(0,	100,"Staff",	"zelda/staff_water.icon"),
	SWORD		(0, 70,	"Sword",	"zelda/sword.icon"),
	BOMB		(0,	10,	"Bomb", 	"zelda/bomb"),
	ARROW		(0,	5,	"Arrow",	"zelda/arrow.icon"),
	CASTLEKEY	(0, 10,	"CastleKey","zelda/key");
	
	// Attributes
	private float weight;
	private int price;
	private String name;
	private String spriteName;
	
	/**
	 * Default ARPGItem constructor.
	 * @param (float) weight
	 * @param (int) price
	 * @param (String) name
	 * @param (String) spriteName
	 */
	private ARPGItem(float weight, int price, String name, String spriteName) {
		this.weight = weight;
		this.price = price;
		this.name = name;
		this.spriteName = spriteName;
	}

	/// ARPGItem specific methods
	
	public String getSpriteName() {
		return this.spriteName;
	}

	/// ARPGItem implements InventoryItem
	
	@Override
	public float getWeight() {
		return this.weight;
	}

	@Override
	public int getPrice(){
		return this.price;
	}

	@Override
	public String getName() {
		return this.name;
	}
	
}
