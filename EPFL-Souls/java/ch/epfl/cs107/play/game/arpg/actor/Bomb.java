package ch.epfl.cs107.play.game.arpg.actor;

import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Interactor;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

/**
 * Bomb is an AreaEntity that cannot move and is both Interactable and Interactor
 */
public class Bomb extends AreaEntity implements Interactor {

	private final BombHandler handler; // Bomb interaction handler
	
	// Sprites and animations
	private Sprite[] bombSprites;
	private Sprite[] explosionSprites;
	
	private int animationIndex;
	private Animation[] animations;
	
	// Constants
	private final static int BOMB_ANIMATION_DURATION = 4;
	private final static int EXPLOSION_ANIMATION_DURATION = 2;
	private final static float POWER = 3.5f;
	
	// Properties
	private int fuseLength;
	private boolean isExploding;
	
	// Other
	private int oneFrameCounter; // Counts a single frame
	private boolean didInteract; // The bomb should only interact with interactables on the FIRST FRAME 
	
	/**
	 * Default Bomb constructor
	 * @param (Area) the current area, not null
	 * @param (Orientation) the initial orientation, not null
	 * @param (DiscreteCoordinates) the initial position, not null
	 */
	public Bomb(Area area, Orientation orientation, DiscreteCoordinates position)
	{
		super(area, orientation, position);
		
		// Interaction handler
		handler = new BombHandler();
		
		// Sprites
		bombSprites = new Sprite[2];
		for (int i = 0; i < 2; i++) {
			bombSprites[i] = new RPGSprite("zelda/bomb", 1.f, 1.f, this, new RegionOfInterest(16*i, 0, 16, 16));
		}
		explosionSprites = new Sprite[7];
		for (int i = 0; i < 7; i++) {
			explosionSprites[i] = new RPGSprite("explosion-1", 2.5f, 2.5f, this, new RegionOfInterest(32*i, 0, 32, 32));
			explosionSprites[i].setAnchor(new Vector(-0.75f, -0.75f));
		}
		
		// Animations
		animationIndex = 0;
		animations = new Animation[2];
		animations[0] = new Animation(BOMB_ANIMATION_DURATION, bombSprites, true);
		animations[1] = new Animation(EXPLOSION_ANIMATION_DURATION, explosionSprites, false);
		
		// Properties
		fuseLength = 40;
		isExploding = false;
		
		// Other
		oneFrameCounter = 0;
		didInteract = false;
	}

	/// Bomb specific methods
	
	/**
	 * Explodes the bomb. 
	 */
	public void explode() {
		fuseLength = 0;
		isExploding = true;
		animationIndex = 1;
	}
	
	/// Bomb implements Updatable
	
	@Override
	public void update(float deltaTime)
	{
		// The bomb is unregistered when its animation is completed and is currently exploding.
		if (animations[animationIndex].isCompleted() && isExploding) {
			getOwnerArea().unregisterActor(this);
		}
		
		// Animations
		animations[animationIndex].update(deltaTime);
		
		// Time count down before exploding
		if (fuseLength > 0)
			fuseLength--;
		
		// Explode
		if (fuseLength <= 0)
		{
			isExploding = true;
			animationIndex = 1;
		}
		
		// Only allow one interaction!
		if (isExploding && !didInteract) {
			if (oneFrameCounter == 1) {
				didInteract = true;
			} else {
				++oneFrameCounter;
			}
		}
	}
	
	/// Bomb implements Interactable
	
	@Override
	public boolean takeCellSpace() {
		if (isExploding) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public boolean isCellInteractable() {
		return true; 
	}

	@Override
	public boolean isViewInteractable() {
		return true;
	}

	@Override
	public void acceptInteraction(AreaInteractionVisitor v) {
		((ARPGInteractionVisitor)v).interactWith(this);
	}

	/// Bomb implements Graphics
	
	@Override
	public void draw(Canvas canvas) {
		if(!animations[animationIndex].isCompleted())
			animations[animationIndex].draw(canvas);
	}

	/// Bomb implements Interactor
	
	@Override
	public List<DiscreteCoordinates> getCurrentCells() {
		return Collections.singletonList(getCurrentMainCellCoordinates());
	}

	@Override
	public List<DiscreteCoordinates> getFieldOfViewCells() {
		return getCurrentMainCellCoordinates().getNeighbours(); // The four cells around it
	}

	@Override
	public boolean wantsCellInteraction() {
		if (isExploding) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean wantsViewInteraction() {
		if (isExploding) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void interactWith(Interactable other) {
		if (!didInteract) {	// Only interact once!
			other.acceptInteraction(handler);			
		}
	}
	
	// Bomb interaction handler private class
	private class BombHandler implements ARPGInteractionVisitor {
		
		// Damage the player with physical damage
		public void interactWith(ARPGPlayer player) {
			player.takeDamage(DamageType.PHYSICAL, POWER);	
		}
		
		// Damage monsters with physical damage
		public void interactWith(Monster monster) {
			monster.takeDamage(DamageType.PHYSICAL, POWER);
		}
		
		// Explodes other bombs
		public void interactWith(Bomb bomb) {
			bomb.explode();
		}
		
		// Extreme lawn mowing
		public void interactWith(Grass grass) {
			grass.slice();
		}
		
	}
	
}
