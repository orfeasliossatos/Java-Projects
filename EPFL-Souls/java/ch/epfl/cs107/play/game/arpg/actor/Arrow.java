package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

/**
 * Arrow concretizes the notion of Projectile, and has certain additional interactions.
 */
public class Arrow extends Projectile {

	private Sprite[][] sprites;
	private final ArrowHandler handler;
	private final static float POWER = 0.5f;
	private final static int DEFAULT_SPEED = 1;
	private final static float DEFAULT_MAX_DISTANCE = 12.f;
	
	/**
	 * Default Arrow constructor.
	 * @param (Area) the current area, not null
	 * @param (Orientation) the initial orientation, not null
	 * @param (DiscreteCoordinates) the initial position, not null
	 * @param (int) the speed of the projectile, 
	 * @param (float) the maximum distance the arrow can fly
	 */
	public Arrow(Area area, Orientation orientation, DiscreteCoordinates position, int speed, float maxDistance) {
		super(area, orientation, position, speed, maxDistance);

		sprites = RPGSprite.extractSprites("zelda/arrow", 4, 1, 1, this, 32, 32, new Orientation[] {Orientation.UP, Orientation.RIGHT, Orientation.DOWN, Orientation.LEFT});
		handler = new ArrowHandler();
	}
	
	/**
	 * Repeated Arrow constructor. 
	 * @param (Area) the current area, not null
	 * @param (Orientation) the initial orientation, not null
	 * @param (DiscreteCoordinates) the initial position, not null
	 */
	public Arrow(Area area, Orientation orientation, DiscreteCoordinates position) {
		this(area, orientation, position, DEFAULT_SPEED, DEFAULT_MAX_DISTANCE);
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		if (!isDisplacementOccurs()) {
			endTrajectory();
		}
	}

	@Override
	public void interactWith(Interactable other) {
		other.acceptInteraction(handler);
	}

	@Override
	public void draw(Canvas canvas) {
		sprites[0][getOrientation().ordinal()].draw(canvas);
	}

	// Arrow interaction handler private class
	private class ArrowHandler implements ARPGInteractionVisitor {
		
		// Attacks the monster with physical damage, and stops flying
		public void interactWith(Monster monster) {
			monster.takeDamage(DamageType.PHYSICAL, POWER);
			endTrajectory();
		}
		
		// Slices the grass, and stops flying
		public void interactWith(Grass grass) {
			grass.slice();
			endTrajectory();
		}
		
		// Extinguishes the fire, and continues flying
		public void interactWith(FireSpell fireSpell) {
			fireSpell.extinguish();
		}
		
		// Explodes the bomb, and stops flying.
		public void interactWith(Bomb bomb) {
			bomb.explode();
			endTrajectory();
		}
		
	}
	
}
