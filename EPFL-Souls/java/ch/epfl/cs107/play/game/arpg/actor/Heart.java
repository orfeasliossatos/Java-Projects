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
 * Heart CollectableAreaEntity. Heals the player on collecting.
 */
public class Heart extends CollectableAreaEntity {
	
	// Sprites and animation
	private Sprite[] heartSprites;
	private Animation animation;
	private final static int ANIMATION_DURATION = 4;
	// How much the heart heals for
	private final static int healValue = 1;
	
	/**
	 *  Default Heart constructor.
	 * @param area (Area) Owner Area, not null
	 * @param orientation (Orientation) Initial orientation, not null
	 * @param position (DiscreteCoordinates) Initial position, not null
	 */
	public Heart(Area area, Orientation orientation, DiscreteCoordinates position) {	
		super(area, orientation, position);
		
		// Sprites
		heartSprites = new Sprite[4];
		for (int i = 0; i < 4; i++) {
			heartSprites[i] = new RPGSprite("zelda/heart", 1.f, 1.f, this, new RegionOfInterest(16 * i, 0, 16, 16), new Vector(0, 0), 100, -1000);			
		}
		
		// Animations
		animation = new Animation(ANIMATION_DURATION, heartSprites, true);
	}
	
	/// Heart implements Updatable
	
	@Override
	public void update(float deltaTime) {
		animation.update(deltaTime);
	}
	
	/// Heart extends Collectable
	
	public void collect(Player player) {	
		((ARPGPlayer)player).giveHitpoints(healValue);
		getOwnerArea().unregisterActor(this);
	}
		
	/// Heart implements Interactable

	@Override
	public void acceptInteraction(AreaInteractionVisitor v) {
		((ARPGInteractionVisitor)v).interactWith(this);
	}
	
	/// Heart implements Graphics
	
	@Override
	public void draw(Canvas canvas) {
		animation.draw(canvas);
	}

}

