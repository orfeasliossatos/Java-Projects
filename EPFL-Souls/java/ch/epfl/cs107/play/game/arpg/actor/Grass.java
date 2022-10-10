package ch.epfl.cs107.play.game.arpg.actor;

import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RandomGenerator;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

/**
 * Grass AreaEntity. Drops items on slicing.
 */
public class Grass extends AreaEntity {

	// Sprites and animations
	private static Sprite[] grassSprites;
	private static Sprite[] slicedSprites;
	private int animationIndex;
	private Animation[] animations;
	private final static int GRASS_ANIMATION_DURATION = 1;
	private final static int SLICED_ANIMATION_DURATION = 4;
	
	// Boolean
	private boolean isSliced;
	
	// Item drop 
	private boolean didAttemptDrop;
	private final static double PROBABILITY_TO_DROP_ITEM = 0.2;		// This is EPFL-Souls.
	private final static double PROBABILITY_TO_DROP_HEART = 0.1;	// Haha. Good luck. 
	
	/**
	 * Default Grass constructor
	 * @param area (Area) Owner Area of entity, not null
	 * @param orientation (Orientation) Initial orientation of the entity, not null
	 * @param position (DiscreteCoordinates) Initial position of the entity, not null
	 */
	public Grass(Area area, Orientation orientation, DiscreteCoordinates position) {
		super(area, orientation, position);

		// Sprites	
		grassSprites = new Sprite[] {new RPGSprite("zelda/grass", 1.f, 1.f, this, new RegionOfInterest(0, 0, 16, 16))};
		slicedSprites = new Sprite[4];
		for (int i = 0; i < 4; i++)
		{
			slicedSprites[i] = new RPGSprite("zelda/grass.sliced", 2.f, 2.f, this, new RegionOfInterest(32*i, 0, 32, 32));
			slicedSprites[i].setAnchor(new Vector(-0.5f, 0.f));
		}
			
		// Animations
		animationIndex = 0;
		animations = new Animation[2];
		animations[0] = new Animation(GRASS_ANIMATION_DURATION, grassSprites, true);
		animations[1] = new Animation(SLICED_ANIMATION_DURATION, slicedSprites, false);
		
		isSliced = false;
		didAttemptDrop = false;
	}
	
	/// Grass specific methods
	
	/**
	 * Drops a coin or a heart with a given probability.
	 */
	private void dropItem() {
		double probItem = RandomGenerator.getInstance().nextDouble();
		if (probItem < PROBABILITY_TO_DROP_ITEM) {
			
			double probHeart = RandomGenerator.getInstance().nextDouble();
			if (probHeart < PROBABILITY_TO_DROP_HEART) {
				getOwnerArea().registerActor(new Heart(getOwnerArea(), Orientation.UP, getCurrentMainCellCoordinates()));
			} else {
				getOwnerArea().registerActor(new Coin(getOwnerArea(), Orientation.UP, getCurrentMainCellCoordinates()));
			}
		}
	}
	
	/**
	 * Slices the grass.
	 */
	public void slice() {
		isSliced = true;
		animationIndex = 1;
	}
	
	/// Updateable
	@Override
	public void update(float deltaTime) {
		if (animations[animationIndex].isCompleted() && isSliced) {
			getOwnerArea().unregisterActor(this);
		}

		if (isSliced && !didAttemptDrop) {
			didAttemptDrop = true;
			dropItem();
		}
		animations[animationIndex].update(deltaTime);
		
	}
	
	@Override
	public List<DiscreteCoordinates> getCurrentCells() {
		return Collections.singletonList(getCurrentMainCellCoordinates());
	}

	/// Grass implements Interactable
	
	@Override
	public boolean takeCellSpace() {
		if (isSliced) {
			return false;			
		} else {
			return true;			
		}
	}

	@Override
	public boolean isCellInteractable() {
		if (isSliced) {
			return false;			
		} else {
			return true;			
		}
	}

	@Override
	public boolean isViewInteractable() {
		if (isSliced) {
			return false;			
		} else {
			return true;			
		}
	}

	@Override
	public void acceptInteraction(AreaInteractionVisitor v) {
		((ARPGInteractionVisitor)v).interactWith(this);
	}

	/// Graphs implements Graphics
	
	@Override
	public void draw(Canvas canvas) {
		if(!animations[animationIndex].isCompleted())
			animations[animationIndex].draw(canvas);
	}


}
