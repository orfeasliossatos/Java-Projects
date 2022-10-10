package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

/**
 * The result of a magic spell, does damage and flies in a straight line.
 */
public class MagicWaterProjectile extends Projectile {
	
	// MagicWaterProjectile attributes
	Sprite[][] sprites;
	Animation animation;
	MagicWaterProjectileHandler handler;
	private final static float POWER = 1.f;
	// Default values
	private final static int DEFAULT_SPEED = 2;
	private final static float DEFAULT_MAX_DISTANCE = 8.f;

	/**
	 * MagicWaterProjectile constructor
	 * @param area
	 * @param orientation
	 * @param position
	 * @param speed
	 * @param maxDistance
	 */
	public MagicWaterProjectile(Area area, Orientation orientation, DiscreteCoordinates position, int speed, float maxDistance) {
		super(area, orientation, position, speed, maxDistance);
		
		// Sprites and animation
		sprites = RPGSprite.extractSprites("zelda/magicWaterProjectile", 4, 1, 1, this, 32, 32, new Orientation[] {Orientation.UP, Orientation.RIGHT, Orientation.DOWN, Orientation.LEFT});
		animation = new Animation(2, sprites[0], true);
		
		// Handler
		handler = new MagicWaterProjectileHandler();
	}
	
	/**
	 * MagicWaterProjectile default constructor
	 * @param area
	 * @param orientation
	 * @param position
	 */
	public MagicWaterProjectile(Area area, Orientation orientation, DiscreteCoordinates position) {
		this(area, orientation, position, DEFAULT_SPEED, DEFAULT_MAX_DISTANCE);
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		animation.update(deltaTime);
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
		animation.draw(canvas);
	}
	
	// MagicWaterProjectile handler class for interactions
	private class MagicWaterProjectileHandler implements ARPGInteractionVisitor {
		
		// Damage monster with Magic damage.
		public void interactWith(Monster monster) {
			monster.takeDamage(DamageType.MAGIC, POWER);
			endTrajectory();
		}
		
		// Extinguish fires.
		public void interactWith(FireSpell fireSpell) {
			fireSpell.extinguish();
		}
		
	}

}
