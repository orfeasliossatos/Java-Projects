package ch.epfl.cs107.play.game.arpg.actor;

/**
 * The notion of Mortal behaviour
 */
public interface Mortal {	

	/**
	 * Check whether the Mortal is weak to a certain DamageType
	 * Note : must be redefined
	 * @param damageType
	 * @return (boolean) whether or not the Mortal is weak said damageType 
	 */
	boolean isWeakToDamageType(DamageType damageType);
	
	/**
	 * Remove hitPoints from Mortal if it is weak to the given damage type.
	 * Note : must be redefined
	 * @param damageType
	 * @return (float) remove damagePoints hitpoints from Mortal 
	 */
	void takeDamage(DamageType damageType, float damagePoints);

	/**
	 * Max hit points getter
	 * Note : must be redefined
	 * @return (float) the maximum amount of hit points
	 */
	float getMaxHitPoints();
	
	/**
	 * Ends the life of a Mortal, should not unregister the Mortal from its OwnerArea yet.
	 * Note : must be redefined
	 */
	void kill();
	
	/**
	 * Inquires whether or not the Mortal is dead.
	 * Note : must be redefined
	 * @return (boolean) whether or not the Mortal is dead.
	 */
	boolean isDead();

}
