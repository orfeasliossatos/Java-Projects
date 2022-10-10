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
import ch.epfl.cs107.play.math.RandomGenerator;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

/**
 * FireSpell AreaEntity that is an Interactor, and Flyable.
 */
public class FireSpell extends AreaEntity implements Interactor, FlyableEntity {

	// Lifetime
	private int lifeTime;
	private final static int MIN_LIFE_TIME = 120;
	private final static int MAX_LIFE_TIME = 240;
	
	// Propagation
	private final FireSpellHandler handler;
	private final static float POWER = 2.5f;
	private int strength;
	private int propagationTimer;
	private final static int PROPAGATION_TIME_FIRE = 4;
	
	// Animations
	private int animationIndex;
	private Sprite[] fireSprites;
	private Sprite[] extinguishSprites; 
	private Animation[] animations;
	private final static int FIRE_ANIMATION_DURATION = 3;
	private final static int EXTINGUISH_ANIMATION_DURATION = 3;
	
	// Booleans
	private boolean isExtinguished;
	private boolean didPropagate;
	
	/**
	 * Default FireSpell constructor.
	 * @param (Area) the current area, not null
	 * @param (Orientation) the initial orientation, not null
	 * @param (DiscreteCoordinates) the initial position, not null
	 * @param (int) strength of the flames (how many much more it will propagate), not null
	 */
	public FireSpell(Area area, Orientation orientation, DiscreteCoordinates position, int strength) {
		super(area, orientation, position);
		
		lifeTime = RandomGenerator.getInstance().nextInt(MAX_LIFE_TIME - MIN_LIFE_TIME) + MIN_LIFE_TIME;
		
		handler = new FireSpellHandler();
		this.strength = strength;
		propagationTimer = 0;
		
		isExtinguished = false;
		didPropagate = false;
		
		// Sprites
		fireSprites = new Sprite[7];
		for (int i = 0; i < 7; i++) {
			fireSprites[i] = new RPGSprite("zelda/fire", 1, 1, this, new RegionOfInterest(16 * i, 0, 16, 16));
		}
		extinguishSprites = new Sprite[7];
		for (int i = 0; i < 7; i++) {
			extinguishSprites[i] = new RPGSprite("zelda/vanish", 2.f, 2.f, this, new RegionOfInterest(32 * i, 0, 32, 32));
			extinguishSprites[i].setAnchor(new Vector(-0.5f, 0.0f));
		}
		
		// Animations
		animationIndex = 0;
		animations = new Animation[2];
		animations[0] = new Animation(FIRE_ANIMATION_DURATION, fireSprites, true);
		animations[1] = new Animation(EXTINGUISH_ANIMATION_DURATION, extinguishSprites, false);
	}

	/// FireSpell specific methods
	/**
	 * Extinguishes the FireSpell
	 */
	public void extinguish() {
		isExtinguished = true;
		animationIndex = 1;
	}
	
	/// FireSpell implements Updateable
	@Override
	public void update(float deltaTime) {
		// When does the fire get removed
		if (animations[animationIndex].isCompleted() && isExtinguished) {
			getOwnerArea().unregisterActor(this);
		}
		
		// Animations
		animations[animationIndex].update(deltaTime);
		
		// Behaviour
		if (!isExtinguished) {
			if (lifeTime > 0) {
				lifeTime--;				
			}
			if (lifeTime <= 0) {
				extinguish();
			}
			
			// Fire propagation
			if (!didPropagate) {
				propagationTimer++;
				if (propagationTimer == PROPAGATION_TIME_FIRE) {
					
					if (strength > 0 && getOwnerArea().canEnterAreaCells(this, Collections.singletonList(getCurrentMainCellCoordinates().jump(getOrientation().toVector())))) {
						getOwnerArea().registerActor(new FireSpell(getOwnerArea(), getOrientation(), getCurrentMainCellCoordinates().jump(getOrientation().toVector()), strength - 1));
					}
					
					didPropagate = true;
					propagationTimer = 0;
				}
			}
			
		}
	}
	
	/// FireSpell implements Interactable
	@Override
	public boolean takeCellSpace() {
		return false;
	}

	@Override
	public boolean isCellInteractable() {
		if (!isExtinguished) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean isViewInteractable() {
		return false;
	}

	@Override
	public void acceptInteraction(AreaInteractionVisitor v) {
		((ARPGInteractionVisitor)v).interactWith(this);
	}

	/// FireSpell implements Graphics
	
	@Override
	public void draw(Canvas canvas) {
		if(!animations[animationIndex].isCompleted())
			animations[animationIndex].draw(canvas);
	}

	/// FireSpell implements Interactor
	
	@Override
	public List<DiscreteCoordinates> getCurrentCells() {
		return Collections.singletonList(getCurrentMainCellCoordinates());
	}

	@Override
	public List<DiscreteCoordinates> getFieldOfViewCells() {
		return null;
	}

	@Override
	public boolean wantsCellInteraction() {
		if (!isExtinguished) {
			return true;			
		} else {
			return false;
		}
	}

	@Override
	public boolean wantsViewInteraction() {
		return false;
	}

	@Override
	public void interactWith(Interactable other) {
		other.acceptInteraction(handler);
	}
	
	// FireSpell interaction handler
	private class FireSpellHandler implements ARPGInteractionVisitor {
		
		// Monster takes Fire damage
		public void interactWith(Monster monster) {
			monster.takeDamage(DamageType.FIRE, POWER);
		}

		// Player takes Fire damage
		public void interactWith(ARPGPlayer arpgPlayer) {
			arpgPlayer.takeDamage(DamageType.FIRE, POWER);
		}
		
		// Ultra X-treme lawn mowing
		public void interactWith(Grass grass) {
			grass.slice();
		}
		
		// Explodes bombs
		public void interactWith(Bomb bomb) {
			bomb.explode();
		}
		
	}
	

}
