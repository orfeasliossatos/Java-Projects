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
 * Staff as a CollectableAreaEntity
 */
public class Staff extends CollectableAreaEntity {

	// Sprites and animation
	private Sprite[] staffSprites; 
	private Animation animation;
	private final static int ANIMATION_DURATION = 4;
	
	/**
	 * Default Staff constructor
	 * @param area (Area) Owner Area, not null
	 * @param orientation (Orientation) Initial orientation, not null
	 * @param position (DiscreteCoordinates) Initial position, not null
	 */
	public Staff(Area area, Orientation orientation, DiscreteCoordinates position) {
		super(area, orientation, position);
		
		// Sprites
		staffSprites = new Sprite[8];
		for (int i = 0; i < 8; i++) {
			staffSprites[i] = new RPGSprite("zelda/staff", 1.f, 1.f, this, new RegionOfInterest(32 * i, 0, 32, 32), new Vector(0, 0), 100, -1000);
			staffSprites[i].setAnchor(new Vector(0.f, 0.4f));
		}
		
		// Animations
		animation = new Animation(ANIMATION_DURATION, staffSprites, true);
	}

	/// Staff implements Updatable
	
	@Override
	public void update(float deltaTime) {
		animation.update(deltaTime);
	}
	
	/// Staff implements Interactable
	
	@Override
	public void acceptInteraction(AreaInteractionVisitor v) {
		((ARPGInteractionVisitor)v).interactWith(this);
	}

	/// Staff implements Collectable
	
	@Override
	public void collect(Player player) {
		((ARPGPlayer)player).giveItem(ARPGItem.STAFF, 1);
		getOwnerArea().unregisterActor(this);
	}

	@Override
	public void draw(Canvas canvas) {
		animation.draw(canvas);
	}

}
