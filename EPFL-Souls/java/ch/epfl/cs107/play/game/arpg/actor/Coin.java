package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.CollectableAreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.Player;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

/**
 * Coin CollectableAreaEntity. Gives the player money on collecting.
 */
public class Coin extends CollectableAreaEntity {

	// Sprites and animation
	private Sprite[] coinSprites;
	private Animation animation;
	private final static int ANIMATION_DURATION = 4;
	// How much money a coin is worth
	private final static int goldValue = 5;
	
	/**
	 * Default Coin constructor
	 * @param (Area) the current area, not null
	 * @param (Orientation) the initial orientation, not null
	 * @param (DiscreteCoordinates) the initial position, not null
	 */
	public Coin(Area area, Orientation orientation, DiscreteCoordinates position) {
		super(area, orientation, position);
		
		// Sprites
		coinSprites= new Sprite[4];
		for (int i = 0; i < 4; i++) {
			coinSprites[i] = new RPGSprite("zelda/coin",1.f,1.f,this,new RegionOfInterest(16*i,0,16,16), new Vector(0,0), 100, -1000);
		}
			
		// Animations
		animation = new Animation(ANIMATION_DURATION, coinSprites, true);
	}
	
	/// Coin implements Updatable
	
	public void update(float deltaTime) {
		animation.update(deltaTime);
	}
	
	/// Coin implements CollectableAreaEntity
	
	public void collect(Player player) {
		((ARPGPlayer)player).giveMoney(goldValue);
		getOwnerArea().unregisterActor(this);
	}
	
	/// Coin implements Interactable
	@Override
	public void acceptInteraction(AreaInteractionVisitor v) {
		((ARPGInteractionVisitor)v).interactWith(this);
	}
	
	/// Coin implements Graphics
	
	@Override
	public void draw(Canvas canvas) {
		animation.draw(canvas);
	}

}
