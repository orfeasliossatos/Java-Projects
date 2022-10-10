package ch.epfl.cs107.play.game.areagame.actor;

import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.window.Canvas;

/**
 * AnimatedDecorations don't interact or accept interactions, and can be placed anywhere that a flyable entity can go.
 */
public class AnimatedDecoration extends AreaEntity {
	
	// Sprites and animation
	private Sprite[] sprites;		// An AnimatedDecoration has a single animation that is independent of orientation
	private Animation animation;	
	
	/**
	 * Default AnimatedDecoration constructor. The parameters are used to build the animation.
	 * @param (String)	name
	 * @param (int) 	nbFrames
	 * @param (int)		width
	 * @param (int)		height
	 * @param (int)		regionWidth
	 * @param (int)		regionHeight
	 * @param (int)		animationDuration
	 * @param (Area)	area
	 * @param (Orientation) orientation
	 * @param (DiscreteCoordinates) position
	 */
	public AnimatedDecoration(String name, int nbFrames, int width, int height, int regionWidth, int regionHeight, int animationDuration, Area area, Orientation orientation, DiscreteCoordinates position) {
		super(area, orientation, position);
		
		// Sprites and animation
		sprites = new Sprite[nbFrames];
	
		for (int i = 0; i < nbFrames; i++) {
			sprites[i] = new RPGSprite(name, width, height, this, new RegionOfInterest(regionWidth * i, 0, regionWidth, regionHeight));
		}
		
		animation = new Animation(animationDuration, sprites, true); // On repeat
	}
	
	/// AnimatedDecoration implements Updatable
	
	@Override 
	public void update(float deltaTime) {
		super.update(deltaTime);
		animation.update(deltaTime);
	}
	
	/// AnimatedDecoration extends AreaEntity
	
	@Override
	public List<DiscreteCoordinates> getCurrentCells() {
		return Collections.singletonList(getCurrentMainCellCoordinates());
	}

	@Override
	public boolean takeCellSpace() {
		return false;	// AnimatedDecorations don't take any space
	}

	@Override
	public boolean isCellInteractable() {
		return false;	// AnimatedDecorations are not interactable
	}

	@Override
	public boolean isViewInteractable() {
		return false;	// AnimatedDecorations are not interactable
	}

	@Override
	public void acceptInteraction(AreaInteractionVisitor v) {
		// Does nothing.
	}
	
	@Override
	public void draw(Canvas canvas) {
		animation.draw(canvas); // Simply draws the animation
	}

}
