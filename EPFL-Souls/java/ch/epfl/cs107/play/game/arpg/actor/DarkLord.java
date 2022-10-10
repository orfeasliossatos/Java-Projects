package ch.epfl.cs107.play.game.arpg.actor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RandomGenerator;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

/**
 * DarkLord is a Monster.
 */
public class DarkLord extends Monster {

	// Constants shared by all DarkLords
	private final static int VISION_RANGE = 3;
	private final static int MAX_INACTIVE_DURATION = 44;
	private final static int MIN_SPELL_WAIT_DURATION = 100;
	private final static int MAX_SPELL_WAIT_DURATION = 120;
	private final static int ATTACK_PROBABILITY = 20;
	private final static int FLAME_STRENGTH = 8;
	private final static int MIN_TELEPORTATION_RADIUS = 5;
	private final static int MAX_TELEPORTATION_RADIUS = 50;
	private final static int MAX_TELEPORTATION_ATTEMPTS = 50;
	private final static double PROBABILITY_TO_DROP_ITEM = 1.0;
	
	// Constants
	private final int turnProbability;
	private final int inactionProbability;
	// Interaction handler
	private final DarkLordHandler handler;
	
	// Properties
	private int internalTimer;
	private int spellWaitDuration;
	private boolean isStrategized;
	private int inactionTimeSteps;
	private DarkLordState currentState;
	
	/**
	 * Default DarkLord Constructor
	 * @param (Area) the current area, not null
	 * @param (Orientation) the initial orientation, not null
	 * @param (DiscreteCoordinates) the initial position, not null
	 */
	public DarkLord(Area area, Orientation orientation, DiscreteCoordinates position) {
		// DarkLords are weak to Magic ONLY.
		super(area, orientation, position, new DamageType[] { DamageType.MAGIC });
		
		// Constants
		turnProbability = RandomGenerator.getInstance().nextInt(20) + 40; 
		inactionProbability = RandomGenerator.getInstance().nextInt(20) + 70;
		handler = new DarkLordHandler();
		
		// Properties
		internalTimer = 0;
		spellWaitDuration = RandomGenerator.getInstance().nextInt(MAX_SPELL_WAIT_DURATION - MIN_SPELL_WAIT_DURATION) + MIN_SPELL_WAIT_DURATION; 
		isStrategized = false;
		inactionTimeSteps = 0;
		currentState = DarkLordState.IDLE;
		
		// Sprites
		sprites.add(RPGSprite.extractSprites(DarkLordState.IDLE.spriteSheetName, 3, 2, 2, this, 32, 32, new Orientation[] {Orientation.UP, Orientation.LEFT, Orientation.DOWN, Orientation.RIGHT}));
		sprites.add(RPGSprite.extractSprites(DarkLordState.SUMMONING.spriteSheetName, 3, 2, 2, this, 32, 32, new Orientation[] {Orientation.UP, Orientation.LEFT, Orientation.DOWN, Orientation.RIGHT}));
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 3; j++) {
				sprites.get(0)[i][j].setAnchor(new Vector(-0.5f, 0));
				sprites.get(1)[i][j].setAnchor(new Vector(-0.5f, 0));
			}
		}
		
		// Animations
		animations.add(RPGSprite.createAnimations(DarkLordState.IDLE.frameDuration / 3, sprites.get(0), true));
		animations.add(RPGSprite.createAnimations(DarkLordState.ATTACKING.frameDuration / 6, sprites.get(1), false));
		animations.add(RPGSprite.createAnimations(DarkLordState.SUMMONING.frameDuration / 6, sprites.get(1), false));
		animations.add(RPGSprite.createAnimations(DarkLordState.CASTINGTELEPORT.frameDuration / 6, sprites.get(1), false));
		animations.add(RPGSprite.createAnimations(DarkLordState.TELEPORTING.frameDuration / 9, sprites.get(0), false));
	}

	/// DarkLord extends Monster
	
	@Override
	public void dropItem() {
		double probItem = RandomGenerator.getInstance().nextDouble();
		if (probItem < PROBABILITY_TO_DROP_ITEM) {
			if (getOwnerArea().canEnterAreaCells(new Key(getOwnerArea(), Orientation.UP, getCurrentMainCellCoordinates()), Collections.singletonList(getCurrentMainCellCoordinates()))) {
				getOwnerArea().registerActor(new Key(getOwnerArea(), Orientation.UP, getCurrentMainCellCoordinates()));				
			}
		}
	}
	
	/// DarkLord implements Updatable
	
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		// If if DarkLord is alive
		if(!isDead()) {
			
			// Timers
			if(inactionTimeSteps > 0) {
				inactionTimeSteps--;
			}
			
			// Animation logic : Depends on currentState
			if(currentState == DarkLordState.IDLE) {
				if (isDisplacementOccurs()) {
					animations.get(currentState.animationIndex)[getOrientation().ordinal()].update(deltaTime);						
				} else {
					animations.get(currentState.animationIndex)[getOrientation().ordinal()].reset();										
				}
			} else {
				animations.get(currentState.animationIndex)[getOrientation().ordinal()].update(deltaTime);						
			}
			
			// Strategy logic
			internalTimer++;
			if (internalTimer == spellWaitDuration) {
				internalTimer = 0;
				spellWaitDuration = RandomGenerator.getInstance().nextInt(MAX_SPELL_WAIT_DURATION - MIN_SPELL_WAIT_DURATION) + MIN_SPELL_WAIT_DURATION; 
				
				// Cast FireSpell or summon a FlameSkull?
				if (RandomGenerator.getInstance().nextInt(100) < ATTACK_PROBABILITY) {
					currentState = DarkLordState.ATTACKING;
				} else {
					currentState = DarkLordState.SUMMONING;
				}
				
				// Turn to face a direction where it could place a FireSpell
				ArrayList<Orientation> possibleOrientations = new ArrayList<Orientation>();
				for (Orientation orientation : Orientation.values()) {
					if (getOwnerArea().canEnterAreaCells(new FireSpell(getOwnerArea(), getOrientation(), getCurrentMainCellCoordinates().jump(getOrientation().toVector()), 0), Collections.singletonList(getCurrentMainCellCoordinates().jump(orientation.toVector())))) {
						possibleOrientations.add(orientation);
					}
				}
				
				if (possibleOrientations.size() != 0) {
					orientate(possibleOrientations.get(RandomGenerator.getInstance().nextInt(possibleOrientations.size())));
				}
				
				isStrategized = true;
			}
			
			// Behaviour logic
			if(!isDisplacementOccurs())
				switch(currentState) {
					case IDLE: {		// Move in a random direction, then become inactive.
						if (inactionTimeSteps == 0) {
							randomMove();
							if (RandomGenerator.getInstance().nextInt(100) < inactionProbability) {
								becomeInactive(RandomGenerator.getInstance().nextInt(MAX_INACTIVE_DURATION));
							}
						}
						break;
					}
					case ATTACKING: {	// If possible, cast FireSpell in front of it, then become IDLE
						if (isStrategized) {
							if (getOwnerArea().canEnterAreaCells(new FireSpell(getOwnerArea(), getOrientation(), getCurrentMainCellCoordinates().jump(getOrientation().toVector()), 0), Collections.singletonList(getCurrentMainCellCoordinates().jump(getOrientation().toVector())))) {
								if (animations.get(DarkLordState.ATTACKING.animationIndex)[getOrientation().ordinal()].isCompleted()) {
									animations.get(DarkLordState.ATTACKING.animationIndex)[getOrientation().ordinal()].reset();

									getOwnerArea().registerActor(new FireSpell(getOwnerArea(), getOrientation(), getCurrentMainCellCoordinates().jump(getOrientation().toVector()), FLAME_STRENGTH));
									currentState = DarkLordState.IDLE;
									isStrategized = false;
								}
							}
						}
						break;
					}
					case SUMMONING: {	// If possible, summon a FlameSkull in front of it, then become IDLE
						if (isStrategized) {
							if (getOwnerArea().canEnterAreaCells(new FlameSkull(getOwnerArea(), getOrientation(), getCurrentMainCellCoordinates().jump(getOrientation().toVector())), Collections.singletonList(getCurrentMainCellCoordinates().jump(getOrientation().toVector())))) {	
								if (animations.get(DarkLordState.SUMMONING.animationIndex)[getOrientation().ordinal()].isCompleted()) {
									animations.get(DarkLordState.SUMMONING.animationIndex)[getOrientation().ordinal()].reset();
									getOwnerArea().registerActor(new FlameSkull(getOwnerArea(), getOrientation(), getCurrentMainCellCoordinates().jump(getOrientation().toVector())));
									currentState = DarkLordState.IDLE;
									isStrategized = false;
								}
							}
						}
						break;
					}
					
					case CASTINGTELEPORT: {	// This bit stops the teleport casting animation.
						if (animations.get(DarkLordState.CASTINGTELEPORT.animationIndex)[getOrientation().ordinal()].isCompleted()) {
							animations.get(DarkLordState.CASTINGTELEPORT.animationIndex)[getOrientation().ordinal()].reset();
							currentState = DarkLordState.TELEPORTING;
						}
						break;
					}
					
					case TELEPORTING: {		// Teleport if not currently moving.
						if(!isDisplacementOccurs() || isTargetReached()) {
							// Try teleporting a few times, in case the first few leads into a non-walkable ARPGCell type
							for (int i = 0; i < MAX_TELEPORTATION_ATTEMPTS; i++) {
								// Pick some distance in a donut shape around the DarkLord
								int dx = ((RandomGenerator.getInstance().nextInt(2) % 2 == 1)? -1 : 1) * RandomGenerator.getInstance().nextInt(MAX_TELEPORTATION_RADIUS - MIN_TELEPORTATION_RADIUS) + MIN_TELEPORTATION_RADIUS;
								int dy = ((RandomGenerator.getInstance().nextInt(2) % 2 == 1)? -1 : 1) * RandomGenerator.getInstance().nextInt(MAX_TELEPORTATION_RADIUS - MIN_TELEPORTATION_RADIUS) + MIN_TELEPORTATION_RADIUS;
								
								if (dx == 0 && dy == 0) {
									continue;
								}
								
								// Next current cells
								List<DiscreteCoordinates> nextCells = new ArrayList<>();
								for (DiscreteCoordinates coord : getCurrentCells()) {
									nextCells.add(coord.jump(dx, dy));
								}
									
								// Left cells
								Set<DiscreteCoordinates> leavingCells = new HashSet<>(getCurrentCells());
								leavingCells.removeAll(nextCells);
										
								// Entering cells
								Set<DiscreteCoordinates> enteringCells = new HashSet<>(nextCells);
								enteringCells.removeAll(getCurrentCells());
									
								if (getOwnerArea().enterAreaCells(this, new ArrayList<>(enteringCells)) && getOwnerArea().leaveAreaCells(this, new ArrayList<>(leavingCells))) {
								   	setCurrentPosition(getPosition().add(dx, dy));
								   	currentState = DarkLordState.IDLE;
								   	break;
								}
							}
						}
						break;
					}
				}
			}
		
	}
	
	/// DarkLord Specific methods
	
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
	
	
	/// DarkLord implements Interactor
	
	@Override
	public List<DiscreteCoordinates> getFieldOfViewCells() {	
		List<DiscreteCoordinates> FOVCells = new ArrayList<DiscreteCoordinates>();
	
		for (int dx = -VISION_RANGE; dx <= VISION_RANGE; dx++) {
			for (int dy = -VISION_RANGE; dy <= VISION_RANGE; dy++) {
				if (!(dy == 0 && dx == 0)) { // If it is not the case that both dx and dy are equal to 0
					FOVCells.add(getCurrentMainCellCoordinates().jump(dx, dy));					
				}
			}
		}
		return FOVCells; 
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
		if (!isDead()) {
			return true;			
		} else {
			return false;
		}
	}

	/// DarkLord implements Mortal
	
	@Override
	public float getMaxHitPoints() {
		return 10.0f;
	}
	
	/// DarkLord implements Graphics
	
	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas); // Death animation
		
		// Specific animation
		if(!isDead() && shouldDraw()) {
			animations.get(currentState.animationIndex)[getOrientation().ordinal()].draw(canvas);
		}
	}
	
	/// DarkLord states enum
	private enum DarkLordState {
		
		IDLE(18, 0, "zelda/darkLord"),
		ATTACKING(18, 1, "zelda/darkLord.spell"),
		SUMMONING(18, 2, "zelda/darkLord.spell"),
		CASTINGTELEPORT(18, 3, "zelda/darkLord.spell"),
		TELEPORTING(18, 4, "zelda/darkLord");

		private final int frameDuration;
		private final int animationIndex;
		private final String spriteSheetName;
		
		DarkLordState(int frameDuration, int animationIndex, String spriteSheetName) {
			this.frameDuration = frameDuration;
			this.animationIndex = animationIndex;
			this.spriteSheetName = spriteSheetName;
		}
	}
	
	/// DarkLord interaction handler private class
	private class DarkLordHandler implements ARPGInteractionVisitor {
	
		// Cast teleport if the player gets too close!
		public void interactWith(ARPGPlayer arpgPlayer) {
			if (currentState != DarkLordState.TELEPORTING)
				currentState = DarkLordState.CASTINGTELEPORT;
		}
	}

}
