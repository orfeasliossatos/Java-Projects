package ch.epfl.cs107.play.game.arpg.actor;

/**
 * Represents a SwimmableEntity, which can enter WATER type ARPGCells
 */
public interface SwimmableEntity {

	/**
	 * SwimmableEntities can swim by default.
	 * @return whether or not the entity can swim
	 */
	default boolean canSwim() { return true; }
	
}
