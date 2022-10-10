package ch.epfl.cs107.play.game.arpg.actor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Interactor;
import ch.epfl.cs107.play.game.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public abstract class Monster extends MovableAreaEntity implements Interactor, Mortal {
	
	// All monsters have hitpoints, some current state, weaknesses, and can drop items
	private boolean isDead;
	private float hitPoints;	
	private DamageType[] typeWeaknesses;
	private boolean didAttemptDrop;

	// Invincibility  
	private int invincibilityFrames;
	private final static int MAX_INVINCIBILITY_FRAMES = 6;
	private boolean shouldDraw;

	// All monsters have a list of sprites[][] as well as a list of animations[]
	protected List<Sprite[][]> sprites;
	protected List<Animation[]> animations;
	
	// All monsters have some common deathsprites and a common death animation
	private Sprite[] deathSprites;
	private Animation deathAnimation;
	private static final int DEATH_ANIMATION_DURATION = 3;
	
	/**
	 * Default Monster constructor
	 * @param area
	 * @param orientation
	 * @param position
	 * @param typeWeaknesses
	 */
	public Monster(Area area, Orientation orientation, DiscreteCoordinates position, DamageType[] typeWeaknesses) {
		super(area, orientation, position);
		
		hitPoints = getMaxHitPoints();
		this.typeWeaknesses = typeWeaknesses;
		invincibilityFrames = 0;
		didAttemptDrop = false;
		
		// Sprites and animations
		sprites = new ArrayList<Sprite[][]>();
		animations = new ArrayList<Animation[]>();

		// Death sprites and animation
		deathSprites = new Sprite[7];
		for (int i = 0; i < 7; i++) {
			deathSprites[i] = new RPGSprite("zelda/vanish", 2.f, 2.f, this, new RegionOfInterest(32 * i, 0, 32, 32));
			deathSprites[i].setAnchor(new Vector(-0.5f, 0.0f));
		}
		deathAnimation = new Animation(DEATH_ANIMATION_DURATION, deathSprites, false);
		
	}
	
	/// Specific
	protected boolean shouldDraw() {
		return shouldDraw;
	}
	
	abstract void dropItem();
	
	
	/// Mortal
	@Override
	public boolean isWeakToDamageType(DamageType damageType) {
		for (DamageType type : typeWeaknesses) {
			if (type.equals(damageType)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void takeDamage(DamageType damageType, float damagePoints)
	{
		if (isWeakToDamageType(damageType) && invincibilityFrames == 0) {
			hitPoints -= damagePoints;			
			invincibilityFrames = MAX_INVINCIBILITY_FRAMES;
		}
	}
	
	@Override
	public boolean isDead() {
		return isDead;
	}
	
	@Override
	public void kill() {
		isDead = true;
	}
	
	/// Updatable
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		if (invincibilityFrames > 0) {
			invincibilityFrames--;
		} 

		if (invincibilityFrames > 0) {
			shouldDraw = !shouldDraw;
		} else {
			shouldDraw = true;
		}
		
		if (hitPoints <= 0) {
			this.kill();
		}
		
		if (isDead) {
			deathAnimation.update(deltaTime);
		}
		
		if (isDead && !didAttemptDrop) {
			dropItem();
			didAttemptDrop = true;
		}
		
		if (isDead && deathAnimation.isCompleted()) {
			getOwnerArea().unregisterActor(this);
		}
	}

	/// Interactable
	@Override
	public boolean takeCellSpace() {
		if (isDead) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public boolean isCellInteractable() {
		if (isDead) {
			return false;
		} else {
			return true;
		} 
	}
	
	@Override
	public boolean isViewInteractable() {
		if (isDead) {
			return false;
		} else {
			return true;
		}
	}
	
	@Override
	public void acceptInteraction(AreaInteractionVisitor v) {
		((ARPGInteractionVisitor)v).interactWith(this);
	}
	
	/// Interactor
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
		if (isDead) return false;
		else return true;
	}

	@Override
	public boolean wantsViewInteraction() {
		if (isDead) return false;
		else return true;
	}
	
	/// Graphics
	@Override
	public void draw(Canvas canvas) {
		if (isDead && !deathAnimation.isCompleted()) {
			deathAnimation.draw(canvas);
		}
	}
	
}
