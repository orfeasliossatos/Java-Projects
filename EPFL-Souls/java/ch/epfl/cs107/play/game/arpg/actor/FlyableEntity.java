package ch.epfl.cs107.play.game.arpg.actor;

/**
 * Represents a FlyableEntity, which can enter "isFlyable" ARPGCells.
 */
public interface FlyableEntity {
	
	/**
	 * FlyableEntities can fly by default.
	 * @return whether or not the entity can fly
	 */
	default boolean canFly() { return true; }

}
