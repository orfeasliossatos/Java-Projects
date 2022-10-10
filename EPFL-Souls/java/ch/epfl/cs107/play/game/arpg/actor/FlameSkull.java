package ch.epfl.cs107.play.game.arpg.actor;

import java.util.Collections;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RandomGenerator;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public class FlameSkull extends Monster implements FlyableEntity {

	private FlameSkullState currentState;
	
	private int lifeTime;
	private final static int MIN_LIFE_TIME = 120;
	private final static int MAX_LIFE_TIME = 240;
	private final static float MAX_HIT_POINTS = 1.f;
	private final static float POWER = 1.f;
	
	private final int turnProbability;
	private final FlameSkullHandler handler;
	private final static double PROBABILITY_TO_DROP_ITEM = 0.5;
	
	public FlameSkull(Area area, Orientation orientation, DiscreteCoordinates position) {
		super(area, orientation, position, new DamageType[] {DamageType.PHYSICAL, DamageType.MAGIC});
				
		Sprite[][] flameSkullSprites = RPGSprite.extractSprites(FlameSkullState.IDLE.spriteSheetName, 3, 2, 2, this, 32, 32, new Orientation[] {Orientation.UP, Orientation.LEFT, Orientation.DOWN, Orientation.RIGHT}); 
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 3; j++) {
				flameSkullSprites[i][j].setAnchor(new Vector(-0.5f, 0));
			}
		}
		sprites.add(flameSkullSprites); 
		animations.add(RPGSprite.createAnimations(FlameSkullState.IDLE.frameDuration / 5, sprites.get(FlameSkullState.IDLE.animationIndex), true));
		
		handler = new FlameSkullHandler();
		lifeTime = RandomGenerator.getInstance().nextInt(MAX_LIFE_TIME - MIN_LIFE_TIME) + MIN_LIFE_TIME;
		currentState = FlameSkullState.IDLE;
		turnProbability = RandomGenerator.getInstance().nextInt(5) + 20; 
	}
	
	/// Monster

	@Override
	public void dropItem() {
		double probItem = RandomGenerator.getInstance().nextDouble();
		if (probItem < PROBABILITY_TO_DROP_ITEM) {
			if (getOwnerArea().canEnterAreaCells(new Coin(getOwnerArea(), Orientation.UP, getCurrentMainCellCoordinates()), Collections.singletonList(getCurrentMainCellCoordinates()))) {
				getOwnerArea().registerActor(new Coin(getOwnerArea(), Orientation.UP, getCurrentMainCellCoordinates()));				
			}
		}
	}
	
	/// Updateable
	@Override 
	public void update(float deltaTime) {
		super.update(deltaTime);

		animations.get(currentState.animationIndex)[getOrientation().ordinal()].update(deltaTime);
		
		if(!isDead()) {
			switch(currentState) {
				case IDLE: {
					if (!isDisplacementOccurs()) {
						randomMove();
					}
					
					if (lifeTime > 0) {
						lifeTime--;						
					}
					if (lifeTime <= 0) {
						super.kill();
					}
					
					break;
				}
			}
		}
	}
	
	// Specific
	private void randomMove() {
		if(!isDisplacementOccurs()) {
			if (RandomGenerator.getInstance().nextInt(100) < turnProbability) {
				Orientation nextOrientation = Orientation.fromInt(RandomGenerator.getInstance().nextInt(4));
				orientate(nextOrientation);
			}
			move(currentState.frameDuration);
		}
	}
	
	/// Interactor
	@Override
	public boolean wantsViewInteraction() {
		return false;
	}

	@Override
	public void interactWith(Interactable other) {
		other.acceptInteraction(handler);
	}

	/// Mortal
	@Override
	public float getMaxHitPoints() {
		return MAX_HIT_POINTS;
	}
	
	/// Interactable
	@Override
	public boolean takeCellSpace() {
		return false;
	}
	
	/// Graphics
	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas); // Draw death animation
		
		if (!isDead() && shouldDraw()) {
			switch(currentState) {
			case IDLE : { animations.get(FlameSkullState.IDLE.animationIndex)[getOrientation().ordinal()].draw(canvas);}
			}
		}
	}
	
	/// FlameSkull states enum
	private enum FlameSkullState {
		
		IDLE(10, 0, "zelda/flameSkull");

		private final int frameDuration;
		private final int animationIndex;
		private final String spriteSheetName;
		
		FlameSkullState(int frameDuration, int animationIndex, String spriteSheetName) {
			this.frameDuration = frameDuration;
			this.animationIndex = animationIndex;
			this.spriteSheetName = spriteSheetName;
		}
	}

	/// FlameSkull interaction handler
	private class FlameSkullHandler implements ARPGInteractionVisitor {
		
		public void interactWith(Monster monster) {
			monster.takeDamage(DamageType.FIRE, POWER);
		}
		
		public void interactWith(ARPGPlayer arpgPlayer) {
			arpgPlayer.takeDamage(DamageType.FIRE, POWER);
		}
		
		public void interactWith(Grass grass) {
			grass.slice();
		}
		
		public void interactWith(Bomb bomb) {
			bomb.explode();
		}
		
	}

	
}
