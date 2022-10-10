package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.CollectableAreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.Player;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

/**
 * Collectable Key used for opening the Castle Door.
 */
public class Key extends CollectableAreaEntity {
	
	// Sprite
	private Sprite keySprite;

	/**
	 * Default Key constructor.
	 * @param area (Area) Owner Area, not null
	 * @param orientation (Orientation) Initial orientation, not null
	 * @param position (DiscreteCoordinates) Initial position, not null
	 */
	public Key(Area area, Orientation orientation, DiscreteCoordinates position) {
		super(area, orientation, position);
		keySprite = new Sprite("zelda/key", 1.f, 1.f, this, new RegionOfInterest(0, 0, 64, 64), new Vector(0,0), 100, -1000);
	}
	
	/// Key extends CollectableAreaEntity
	
	public void collect(Player player) {
		((ARPGPlayer)player).giveItem(ARPGItem.CASTLEKEY, 1);
		super.getOwnerArea().unregisterActor(this);
	}
	
	/// Key implements Interactable
	
	public void acceptInteraction(AreaInteractionVisitor v) {
		((ARPGInteractionVisitor)v).interactWith(this);
	}
	
	/// Key implements Graphics
	
	@Override
	public void draw(Canvas canvas) {
		keySprite.draw(canvas);
	}

}
