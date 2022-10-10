package ch.epfl.cs107.play.game.arpg.actor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RandomGenerator;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public class LogMonster extends Monster {
	
	private final static int VISION_RANGE = 8;
	private final static int MAX_INACTIVE_DURATION = 34;
	private final static int MIN_SLEEPING_DURATION = 100;
	private final static int MAX_SLEEPING_DURATION = 150;
	private final static float POWER = 4.f;
	
	private final int turnProbability;
	private final int inactionProbability;
	private final LogMonsterHandler handler; // lol

	private int inactionTimeSteps;
	private LogMonsterState currentState;
	private final static double PROBABILITY_TO_DROP_ITEM = 0.5;
	
	public LogMonster(Area area, Orientation orientation, DiscreteCoordinates position) {
		super(area, orientation, position, new DamageType[] {DamageType.PHYSICAL, DamageType.FIRE});
	
		turnProbability = RandomGenerator.getInstance().nextInt(20) + 40; 
		inactionProbability = RandomGenerator.getInstance().nextInt(20) + 60;
		handler = new LogMonsterHandler();

		inactionTimeSteps = 0;
		currentState = LogMonsterState.IDLE;
		
		// Sprites
		sprites.add(RPGSprite.extractSprites(LogMonsterState.IDLE.spriteSheetName, 4, 2, 2, this, 32, 32, new Orientation[] {Orientation.DOWN, Orientation.UP, Orientation.RIGHT, Orientation.LEFT}));
		sprites.add(new Sprite[1][3]);
		sprites.add(new Sprite[1][3]);
		sprites.add(new Sprite[1][4]);
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				sprites.get(0)[i][j].setAnchor(new Vector(-0.5f, 0));
			}
		}
		
		for (int i = 0; i < 3; i++) {
			sprites.get(1)[0][i] = new RPGSprite(LogMonsterState.WAKING.spriteSheetName, 2, 2, this, new RegionOfInterest(0, 32 * i, 32, 32));
			sprites.get(1)[0][i].setAnchor(new Vector(-0.5f, 0));
			sprites.get(2)[0][2 - i] = new RPGSprite(LogMonsterState.FALLINGASLEEP.spriteSheetName, 2, 2, this, new RegionOfInterest(0, 32 * i, 32, 32));
			sprites.get(2)[0][2 - i].setAnchor(new Vector(-0.5f, 0));
		}
		for (int i = 0; i < 4; i++) {
			sprites.get(3)[0][i] = new RPGSprite(LogMonsterState.SLEEPING.spriteSheetName, 2, 2, this, new RegionOfInterest(0, 32 * i, 32, 32));
			sprites.get(3)[0][i].setAnchor(new Vector(-0.5f, 0));
		}
		
		// Animations
		animations.add(RPGSprite.createAnimations(LogMonsterState.IDLE.frameDuration / 4, sprites.get(0), true));
		animations.add(RPGSprite.createAnimations(LogMonsterState.ATTACKING.frameDuration / 6, sprites.get(0), true));
		animations.add(new Animation[] {new Animation(LogMonsterState.FALLINGASLEEP.frameDuration, sprites.get(2)[0], false)});
		animations.add(new Animation[] {new Animation(LogMonsterState.WAKING.frameDuration, sprites.get(1)[0], false)});
		animations.add(new Animation[] {new Animation(LogMonsterState.SLEEPING.frameDuration, sprites.get(3)[0], true)});
	}

	/// Updatable
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		if(!isDead()) {
			if(inactionTimeSteps > 0) {
				inactionTimeSteps--;
			}
			
			// Animation logic
			if (currentState == LogMonsterState.IDLE || currentState == LogMonsterState.ATTACKING) {
				if (isDisplacementOccurs()) {
					animations.get(currentState.animationIndex)[getOrientation().ordinal()].update(deltaTime);						
				}
				else if (inactionTimeSteps > 0) {
					animations.get(currentState.animationIndex)[getOrientation().ordinal()].reset();					
				}
			} else {
				animations.get(currentState.animationIndex)[0].update(deltaTime);
			}
			
			// Behaviour logic
			if (inactionTimeSteps == 0) {
				switch(currentState) {
					case IDLE : {
						if (!isDisplacementOccurs()) {
							randomMove();
							if (RandomGenerator.getInstance().nextInt(100) < inactionProbability) {
								becomeInactive(RandomGenerator.getInstance().nextInt(MAX_INACTIVE_DURATION));
							}
						}
						break;
					}
					case ATTACKING : {
						if (!isDisplacementOccurs() && !getOwnerArea().canEnterAreaCells(this, getNextCurrentCells())) {
							currentState = LogMonsterState.FALLINGASLEEP;
						} else
							move(LogMonsterState.ATTACKING.frameDuration);
						break;
					}
					case FALLINGASLEEP : {
						if (animations.get(LogMonsterState.FALLINGASLEEP.animationIndex)[0].isCompleted()) {
							animations.get(LogMonsterState.FALLINGASLEEP.animationIndex)[0].reset();
							currentState = LogMonsterState.SLEEPING;
							becomeInactive(RandomGenerator.getInstance().nextInt(MAX_SLEEPING_DURATION - MIN_SLEEPING_DURATION) + MIN_SLEEPING_DURATION);
						}
						break;
					}
					case WAKING : {
						if (animations.get(LogMonsterState.WAKING.animationIndex)[0].isCompleted()) {
							animations.get(LogMonsterState.WAKING.animationIndex)[0].reset();
							currentState = LogMonsterState.IDLE;	
							move(LogMonsterState.IDLE.frameDuration);						
						}
						break;
					}
					case SLEEPING : {
						animations.get(LogMonsterState.FALLINGASLEEP.animationIndex)[0].reset();
						currentState = LogMonsterState.WAKING;
						break;
					}
				}
			}
		}
	}
	
	/// Specific
	private void randomMove() {
		if (RandomGenerator.getInstance().nextInt(100) < turnProbability) {
			Orientation nextOrientation = Orientation.fromInt(RandomGenerator.getInstance().nextInt(4));
			orientate(nextOrientation);
		}
		move(currentState.frameDuration);
	}
	
	private void becomeInactive(int inactionTimeSteps) {
		this.inactionTimeSteps = inactionTimeSteps;
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
	
	/// Mortal
	@Override
	public float getMaxHitPoints() {
		return 3.f;
	}
	
	/// Interactor
	@Override
	public List<DiscreteCoordinates> getFieldOfViewCells() {
		if (currentState.equals(LogMonsterState.ATTACKING)) {
			return Collections.singletonList(getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
		} else {
			List<DiscreteCoordinates> FOVCells = new ArrayList<DiscreteCoordinates>();
			for (int c = 1; c <= VISION_RANGE; c++) {
				FOVCells.add(getCurrentMainCellCoordinates().jump(getOrientation().toVector().mul(c)));
			}
			return FOVCells;
		}
	}
	
	@Override
	public void interactWith(Interactable other) {
		other.acceptInteraction(handler);
	}
	
	@Override
	public boolean wantsCellInteraction() {
		return false;
	}

	@Override
	public boolean wantsViewInteraction() {
		if (!isDead() && (currentState == LogMonsterState.IDLE || currentState == LogMonsterState.ATTACKING)) {
			return true;			
		} else {
			return false;
		}
	}
	
	/// Graphics
	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas); // Death animation
		
		// Specific animation
		if (!isDead() && shouldDraw()) {
			switch(currentState) {
				case IDLE:	 		{ animations.get(LogMonsterState.IDLE.animationIndex)[getOrientation().ordinal()].draw(canvas); break;}
				case ATTACKING: 	{ animations.get(LogMonsterState.ATTACKING.animationIndex)[getOrientation().ordinal()].draw(canvas); break;}
				case FALLINGASLEEP: { animations.get(LogMonsterState.FALLINGASLEEP.animationIndex)[0].draw(canvas); break;}
				case SLEEPING:	 	{ animations.get(LogMonsterState.SLEEPING.animationIndex)[0].draw(canvas); break;}
				case WAKING:		{ animations.get(LogMonsterState.WAKING.animationIndex)[0].draw(canvas); break;}
			}
		}
	}
	
	/// LogMonster states enum
	private enum LogMonsterState {
		
		IDLE(16, 0, "zelda/logMonster"),
		ATTACKING(12, 1, "zelda/logMonster"),
		FALLINGASLEEP(4, 2, "zelda/logMonster.wakingUP"), 
		WAKING(10, 3, "zelda/logMonster.wakingUp"),
		SLEEPING(16, 4, "zelda/logMonster.sleeping");

		private final int frameDuration;
		private final int animationIndex;
		private final String spriteSheetName;
		
		LogMonsterState(int frameDuration, int animationIndex, String spriteSheetName) {
			this.frameDuration = frameDuration;
			this.animationIndex = animationIndex;
			this.spriteSheetName = spriteSheetName;
		}
	}
	
	/// LogMonster interaction handler
	private class LogMonsterHandler implements ARPGInteractionVisitor {
		
		public void interactWith(ARPGPlayer arpgPlayer) {
			if (currentState == LogMonsterState.ATTACKING || DiscreteCoordinates.distanceBetween(LogMonster.this.getCurrentMainCellCoordinates(), arpgPlayer.getCurrentCells().get(0)) == 1) {
				arpgPlayer.takeDamage(DamageType.PHYSICAL, POWER);
			}
				
			if (currentState == LogMonsterState.IDLE)
				currentState = LogMonsterState.ATTACKING;
		}
		
	}
	
	
}
