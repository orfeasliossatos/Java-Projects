package ch.epfl.cs107.play.game.arpg.actor;

/**
 * The finite set of ARPG damage types.
 */
public enum DamageType {
	
	/// Enumeration elements
	PHYSICAL("physical"),
	FIRE("fire"),
	MAGIC("magic");
	
	/// Name of the damage type
	private final String typeName;
	
	/**
	 * Default DamageType constructor
	 * @param typeName (String). Not null
	 */
	private DamageType(String typeName) {
		this.typeName = typeName;
	}
	
	/**
	 * Convert an int into a DamageType
	 * @param index the int representing the damage type
	 * @return the damage type
	 */
	public static DamageType fromInt(int index) {
		switch(index) {
			case 0: return DamageType.PHYSICAL;
			case 1: return DamageType.FIRE;
			case 3: return DamageType.MAGIC;
		}
		return null;
	}
	
	@Override
	public String toString() {
		return typeName;
	}
}
