package ch.epfl.cs107.play.game.arpg.actor;

import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactor;
import ch.epfl.cs107.play.game.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

/**
 * Projectile notion. Can move on a grid, behaves like an interactor, and can fly.
 */
public abstract class Projectile extends MovableAreaEntity implements Interactor, FlyableEntity {

	// Procectile attributes
	private int speed;
	private float maxDistance;
	private DiscreteCoordinates initialposition;
	private float distanceTravelled;
	private boolean finishedTrajectory;
	
	/**
	 * Default Projectile constructor
	 * @param area
	 * @param orientation
	 * @param position
	 * @param speed
	 * @param maxDistance
	 */
	public Projectile(Area area, Orientation orientation, DiscreteCoordinates position, int speed, float maxDistance) {
		super(area, orientation, position);
		
		this.speed = speed;
		this.maxDistance = maxDistance;
		initialposition = position;
		distanceTravelled = 0;
		finishedTrajectory = false;
	}
	
	/// Projectile specific methods
	
	/**
	 * When this method is called, the projectile is unregistered from the area (if possible)
	 */
	protected void endTrajectory() {
		if (getOwnerArea().leaveAreaCells(this, getCurrentCells())) {
			getOwnerArea().unregisterActor(this);			
		}
	}
	
	/// Updatable
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		distanceTravelled = getPosition().sub(initialposition.toVector()).getLength();
		if (distanceTravelled > maxDistance) {
			finishedTrajectory = true;
		}
		
		if (finishedTrajectory) {
			endTrajectory();
		}
		
		move(speed);
	}

	/// Projectile implements Interactable
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
		return false;
	}

	@Override
	public boolean isViewInteractable() {
		return false;
	}

	@Override
	public void acceptInteraction(AreaInteractionVisitor v) {
		((ARPGInteractionVisitor)v).interactWith(this);
	}

	/// Projectile implements Interactor
	@Override
	public List<DiscreteCoordinates> getFieldOfViewCells() {
		return null;
	}

	@Override
	public boolean wantsCellInteraction() {
		if (!finishedTrajectory) {
			return true;			
		} else {
			return false;
		}
	}

	@Override
	public boolean wantsViewInteraction() {
		return false;
	}

}
