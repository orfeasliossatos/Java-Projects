package ch.epfl.cs107.play.game.areagame.actor;

import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.rpg.actor.Player;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

/**
 * Represents the abstract notion of AreaEntity which can be collected by a Player.
 */
public abstract class CollectableAreaEntity extends AreaEntity {
	
	/**
	 * Default CollectableAreaEntity constructor
	 * @param (Area) the current area, not null
	 * @param (Orientation) the initial orientation, not null
	 * @param (DiscreteCoordinates) the initial position, not null
	 */
	public CollectableAreaEntity(Area area, Orientation orientation, DiscreteCoordinates position) {
		super(area, orientation, position);
	}
	
	/// CollectableAreaEntity specific methods
	
	/**
	 * Collect the item. 
	 * @param (ARPGPlayer) player
	 */
	public abstract void collect(Player player); //Must be redefined.
	
	/// CollectableAreaEntity implements Graphics
	
	@Override
	public abstract void draw(Canvas canvas);

	/// CollectableAreaEntity implements Interactable
	
	@Override
	public List<DiscreteCoordinates> getCurrentCells() {
		return Collections.singletonList(getCurrentMainCellCoordinates());
	}
	
	@Override
	public boolean takeCellSpace() {
		return false;
	}

	@Override
	public boolean isCellInteractable() {
		return true;
	}

	@Override
	public boolean isViewInteractable() {
		return false;
	}

}
