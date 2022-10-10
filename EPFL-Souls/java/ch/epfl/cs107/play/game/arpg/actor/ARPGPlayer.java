package ch.epfl.cs107.play.game.arpg.actor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.CollectableAreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.ARPGBehavior.ARPGCell;
import ch.epfl.cs107.play.game.arpg.ARPGBehavior.ARPGCellType;
import ch.epfl.cs107.play.game.arpg.ARPGController;
import ch.epfl.cs107.play.game.arpg.ARPGController.ARPGControllerButton;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.Door;
import ch.epfl.cs107.play.game.rpg.actor.Player;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.game.rpg.inventory.Inventory;
import ch.epfl.cs107.play.game.rpg.inventory.InventoryItem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

/**
 * ARPGPlayer concretizes the notion of Player, and implements Mortal behavior.
 */
public class ARPGPlayer extends Player implements Mortal, SwimmableEntity, Inventory.Holder {
	
	// Sprites and animations
	private List<Sprite[][]> sprites;
	private List<Animation[]> animations;

	private Sprite lightHalo;	// We don't use the LightHalo class because that class assumes the player is center on the screen.
	
	// Interaction handler
	private final ARPGPlayerHandler handler;
	
	// Controls
	private ARPGController controller;
	
	// Stats
	private boolean isDead;
	private float hitPoints;
	private DamageType[] typeWeaknesses;
	private ARPGPlayerState currentState;
	private final static float MAX_HIT_POINTS = 3.f;
	private final static float PHYSICAL_POWER = 1.0f;
	
	// Invincibility  
	private int invincibilityFrames;
	private final static int MAX_INVINCIBILITY_FRAMES = 16;
	private final static int ROLL_INVINCIBILITY_FRAMES = 4;
	private boolean shouldDraw;
	
	// Inventory and UI
	private ARPGItem currentItem;
	private ARPGInventory inventory;
	private ARPGPlayerStatusGUI gui;
	
	/**
	 * Default ARPGPlayer constructor
	 * @param area (Area) Owner Area, not null
	 * @param orientation (Orientation) Initial orientation, not null
	 * @param position (DiscreteCoordinates) Initial position, not null
	 */
	public ARPGPlayer(Area area, Orientation orientation, DiscreteCoordinates coordinates) {
		super(area, orientation, coordinates);
		
		// Interaction handler
		handler = new ARPGPlayerHandler(); 		
		
		// Sprite and animations
		sprites = new ArrayList<Sprite[][]>();
		animations = new ArrayList<Animation[]>();
		
		sprites.add(RPGSprite.extractSprites(ARPGPlayerState.IDLE.spriteSheetName,				4, 1, 2, this, 16, 32, new Orientation[] {Orientation.DOWN, Orientation.RIGHT, Orientation.UP, Orientation.LEFT})); 
		sprites.add(RPGSprite.extractSprites(ARPGPlayerState.ROLLING.spriteSheetName,			4, 1, 2, this, 16, 32, new Orientation[] {Orientation.DOWN, Orientation.RIGHT, Orientation.UP, Orientation.LEFT}));
		sprites.add(RPGSprite.extractSprites(ARPGPlayerState.SWORDATTACKING.spriteSheetName,	4, 2, 2, this, 32, 32, new Orientation[] {Orientation.DOWN, Orientation.UP, Orientation.RIGHT, Orientation.LEFT}));
		sprites.add(RPGSprite.extractSprites(ARPGPlayerState.STAFFATTACKING.spriteSheetName,	4, 2, 2, this, 32, 32, new Orientation[] {Orientation.DOWN, Orientation.UP, Orientation.RIGHT, Orientation.LEFT}));
		sprites.add(RPGSprite.extractSprites(ARPGPlayerState.BOWATTACKING.spriteSheetName,		4, 2, 2, this, 32, 32, new Orientation[] {Orientation.DOWN, Orientation.UP, Orientation.RIGHT, Orientation.LEFT}));
		sprites.add(RPGSprite.extractSprites(ARPGPlayerState.SWIMMING.spriteSheetName,			2, 1, 2, this, 16, 32, new Orientation[] {Orientation.DOWN, Orientation.RIGHT, Orientation.UP, Orientation.LEFT}));
		
		lightHalo = new RPGSprite("lightHalo", 27, 27, this, new RegionOfInterest(0, 0, 500, 500), new Vector(-13.5f, -13.0f), 0.90f, 1050);
		
		for (int i = 2; i < 5; i++) {
			for (int j = 0; j < 4; j++) {
				for (int k = 0; k < 4; k++) {
					sprites.get(i)[j][k].setAnchor(new Vector(-0.5f, 0));
				}
			}
		}
		
		animations.add(RPGSprite.createAnimations(ARPGPlayerState.IDLE.frameDuration / 2, 		sprites.get(0), true));
		animations.add(RPGSprite.createAnimations(ARPGPlayerState.ROLLING.frameDuration / 2, 	sprites.get(1), false));
		animations.add(RPGSprite.createAnimations(ARPGPlayerState.SWORDATTACKING.frameDuration, sprites.get(2), false));
		animations.add(RPGSprite.createAnimations(ARPGPlayerState.STAFFATTACKING.frameDuration, sprites.get(3), false));
		animations.add(RPGSprite.createAnimations(ARPGPlayerState.BOWATTACKING.frameDuration, 	sprites.get(4), false));
		animations.add(RPGSprite.createAnimations(ARPGPlayerState.SWIMMING.frameDuration,		sprites.get(5), true));
		
		// Controls
		controller = new ARPGController();
		
		// Stats
		isDead = false;
		hitPoints = getMaxHitPoints();
		currentState = ARPGPlayerState.IDLE;
		typeWeaknesses = new DamageType[] { DamageType.PHYSICAL, DamageType.FIRE, DamageType.MAGIC };
	    
	    // Invincibility
		shouldDraw = true;
	    invincibilityFrames = 0;
	    
	    // Inventory : "hard-code" add items.
	    inventory = new ARPGInventory(0, 1000); 
	    inventory.addItem(ARPGItem.BOMB,	5); 
	    inventory.addItem(ARPGItem.ARROW,	20);
		inventory.addItem(ARPGItem.SWORD,	1);	
		inventory.addItem(ARPGItem.BOW,		1);
		
		currentItem = ARPGItem.SWORD;	// Begin by holding the sword
	    
		// User Interface
		gui = new ARPGPlayerStatusGUI(this);
		
	}

	/// ARPGPlayer implements Updatable
	
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		// Only update the ARPGPlayer if it's not dead.
		if (!isDead) {
			
			// Controls
			controller.update(getOwnerArea().getKeyboard());
			
			// Invincibility
			if (invincibilityFrames > 0) {
				invincibilityFrames--;
			}
			
			if (invincibilityFrames > ROLL_INVINCIBILITY_FRAMES) {
				shouldDraw = !shouldDraw;
			} else {
				shouldDraw = true;
			}
			
			// Inventory
			if (controller.isReady(ARPGControllerButton.SWITCH)) {
				controller.setCoolingDown(ARPGControllerButton.SWITCH);
				switchItem();
			}
			if (controller.isReady(ARPGControllerButton.ITEM)) {
				controller.setCoolingDown(ARPGControllerButton.ITEM);
				useEquipment();
			}
			
			// Rolling logic : You need to be able to roll in the middle of any situation, other than swimming or sliding
			if (currentState != ARPGPlayerState.SWIMMING && controller.isReady(ARPGControllerButton.ROLL)) {
				currentState = ARPGPlayerState.ROLLING;
			}
			
			// Movement logic
			switch(currentState) {
				case IDLE : {		// Simply move or orientate player
					moveOrientate();
					break;
				}
				case ROLLING : {
					roll();			// Roll once and return to IDLE state.
					if (animations.get(currentState.animationIndex)[getOrientation().ordinal()].isCompleted()) {
						animations.get(currentState.animationIndex)[getOrientation().ordinal()].reset();
						controller.setCoolingDown(ARPGControllerButton.ROLL);
						currentState = ARPGPlayerState.IDLE;
					}
					break;
				}
				case SWIMMING : {	// Simply move or orientate player
					moveOrientate();
					break;
				}
				default : {			// Do nothing
					break;
				}
			}
			
		    // Animations
			switch(currentState) {
				case IDLE: {	// Only run animations when moving
				    if (isDisplacementOccurs()) {
				    	animations.get(currentState.animationIndex)[getOrientation().ordinal()].update(deltaTime);
				    } else { 
				    	animations.get(currentState.animationIndex)[getOrientation().ordinal()].reset();
				    }
				    break;
				}
				case ROLLING: { // Only run animations when moving. When the animation is completed return to IDLE
				    if (isDisplacementOccurs()) {
				    	animations.get(currentState.animationIndex)[getOrientation().ordinal()].update(deltaTime);
				    } else { 
				    	animations.get(currentState.animationIndex)[getOrientation().ordinal()].reset();
				    	currentState = ARPGPlayerState.IDLE;	
				    }
				    break;
				}
				case SWORDATTACKING: {	// Animations are ran until the player releases the ITEM button, in which case the player returns to IDLE.
					animations.get(currentState.animationIndex)[getOrientation().ordinal()].update(deltaTime); 
					if (animations.get(currentState.animationIndex)[getOrientation().ordinal()].isCompleted()) {
						animations.get(currentState.animationIndex)[getOrientation().ordinal()].reset();
						
						if (controller.isReleased(ARPGControllerButton.ITEM)) {
							currentState = ARPGPlayerState.IDLE;
						}
					}
					break;
				}
				case SWIMMING: {// Run animations whether or not the player is moving
					animations.get(currentState.animationIndex)[getOrientation().ordinal()].update(deltaTime);
					break;
				}
				default: {		// Run animation once, then return to IDLE
					animations.get(currentState.animationIndex)[getOrientation().ordinal()].update(deltaTime); 
					if (animations.get(currentState.animationIndex)[getOrientation().ordinal()].isCompleted()) {
						animations.get(currentState.animationIndex)[getOrientation().ordinal()].reset();
						currentState = ARPGPlayerState.IDLE;
					}
					break;
				}
			}
			
			/// Death
			
			if (hitPoints == 0) {
				// Remove itself from its cells!
				if (getOwnerArea().leaveAreaCells(this, getCurrentCells())) {
					isDead = true;
					inventory.removeMoney(inventory.getMoney() / 2);	
				}
			}
		}
	}
	
	/// ARPGPlayer's specific methods
	
	/**
	 * Revive the player
	 */
	public void revive() {
		hitPoints = getMaxHitPoints();
		isDead = false;
	}
	
	/**
	 * Move in the direction the player is currently facing, or face a new direction.
	 * @param (Orientation) orientation
	 * @param (ARPGControllerButton) button
	 */
	private void moveOrientate() {
		Orientation orientation = getOrientation();
		ARPGControllerButton button;
		// Loop through Orientations and their associated ARPGControllerButton buttons (CLOCKWISE ORDERING)
		for (int o = Orientation.UP.ordinal(), b = ARPGControllerButton.MOVEUP.ordinal(); o < 4; o++, b++) {
			orientation = Orientation.fromInt(o);
			button = ARPGControllerButton.fromInt(b);
			
			if(controller.isReady(button)) {
				if(getOrientation() == orientation) { 
					move(currentState.frameDuration);				
				} else {
					orientate(orientation);
				}
				break;
			}
		}
		
    }
	
	/**
	 * Move in the direction the player is currently facing and acquire a small amount of invulnerability frames
	 */
	private void roll() {
		invincibilityFrames = ROLL_INVINCIBILITY_FRAMES;
		move(ARPGPlayerState.ROLLING.frameDuration);	
	}
	
	// NOTE TO CODER: Every time you make a getter, you chip away at encapsulation!
	
	/**
	 * Getter for current hitPoints
	 * @return (float) hitPoints
	 */
	public float getHitPoints() {
		return this.hitPoints;
	}
	
	/**
	 * Getter for current amount of money in the inventory
	 * @return (int) the money in the inventory 
	 */
	public int getMoney() {
		return this.inventory.getMoney();
	}
	
	/**
	 * Getter for current item selected by the player with the SWITCH button
	 * @return (ARPGItem) the current item selected
	 */
	public ARPGItem getCurrentItem() {
		return this.currentItem;
	}
	
	/**
	 * Heals the player by some healValue
	 * @param (float) healValue 
	 */
	public void giveHitpoints(float healValue) {
		hitPoints += Math.abs(healValue);
		if (hitPoints > getMaxHitPoints()) {
			hitPoints = getMaxHitPoints();
		}
	}

	/**
	 * Puts some amount of money in the player's inventory
	 * @param (int) money to be added to the player's inventory
	 */
	public void giveMoney(int money) {
		this.inventory.addMoney(money);
	}
	
	/**
	 * Puts a certain number of one type of item in the players inventory
	 * @param (ARPGItem) item to be placed in the inventory
	 * @param (int) quantity of such item to be placed in the inventory
	 */
	public void giveItem(ARPGItem item, int quantity) {
		this.inventory.addItem(item, quantity);
	}
	
	/**
	 * This method allows the player to "use" the currently equiped item. 
	 */
	private void useEquipment() {
		switch(currentItem) {
			case BOMB: {	// Places the bomb on the cell infront, if possible.
				if((inventory.containsItem(ARPGItem.BOMB)) && (getOwnerArea().canEnterAreaCells(new Bomb(getOwnerArea(), getOrientation(), getCurrentMainCellCoordinates().jump(getOrientation().toVector())), getFieldOfViewCells()))) {
					getOwnerArea().registerActor(new Bomb(getOwnerArea(), Orientation.UP, getCurrentMainCellCoordinates().jump(getOrientation().toVector())));
					inventory.removeItem(ARPGItem.BOMB, 1);
					
					if (!inventory.containsItem(ARPGItem.BOMB)) {
						switchItem();
					}
				}	
				break;
			}
			case SWORD: {	// Swings the sword by changing the player's state to sword-attacking
				if (currentState == ARPGPlayerState.IDLE) {
					currentState = ARPGPlayerState.SWORDATTACKING;
					controller.setIsReady(ARPGControllerButton.ITEM);
				}
				break;
			}
			case STAFF: {	// Casts a water spell if possible, and changes the player's state to staff-attacking.
				if (currentState == ARPGPlayerState.IDLE) {
					currentState = ARPGPlayerState.STAFFATTACKING;
					if(getOwnerArea().canEnterAreaCells(new MagicWaterProjectile(getOwnerArea(), getOrientation(), getCurrentMainCellCoordinates().jump(getOrientation().toVector())), getFieldOfViewCells())) {
						getOwnerArea().registerActor(new MagicWaterProjectile(getOwnerArea(), getOrientation(), getCurrentMainCellCoordinates().jump(getOrientation().toVector())));
					}
				}
				break; 
			}
			case BOW: {		// Shoots an arrow if possible, and changes the player's state to bow-attacking.
				if (currentState == ARPGPlayerState.IDLE) {
					currentState = ARPGPlayerState.BOWATTACKING;
					if((inventory.containsItem(ARPGItem.ARROW)) && (getOwnerArea().canEnterAreaCells(new Arrow(getOwnerArea(), getOrientation(), getCurrentMainCellCoordinates().jump(getOrientation().toVector())), getFieldOfViewCells()))) {
						getOwnerArea().registerActor(new Arrow(getOwnerArea(), getOrientation(), getCurrentMainCellCoordinates().jump(getOrientation().toVector())));
						inventory.removeItem(ARPGItem.ARROW, 1);
					}
				}
				break;
			}
			default: {		// If the item's usage is undefined, or the item isn't usable
				currentState = ARPGPlayerState.IDLE;
				break;
			}
		}
	}
	
	/**
	 * Cycles through the items in inventory. 
	 * @param (ARPGItem) currentItem
	 * @return (ARPGItem) the next item in the inventory
	 */
	private void switchItem() {
		
		List<InventoryItem> items = inventory.getItems();
		
		int index = 0;
		
		// Look for the current item in the list
		for (int i = 0; i < items.size(); ++i) {
			if (items.get(i) == (this.currentItem)) { // If the item is found
				if (i == items.size() - 1) { // Here, circular permutation of items
					index = 0;
				} else {
					index = i + 1; // Then the next item should be at index i + 1.
				}
			}
		}
		
		this.currentItem = (ARPGItem)items.get(index);
	}
	
	
	/// ARPGPlayer implements Mortal
	
	@Override
	public boolean isWeakToDamageType(DamageType damageType) {
		for (DamageType type : typeWeaknesses) {
			if (type.equals(damageType)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void takeDamage(DamageType damageType, float damagePoints)
	{
		if (isWeakToDamageType(damageType) && invincibilityFrames == 0) {
			hitPoints -= damagePoints;
			if (hitPoints < 0) {
				kill();
			}
			invincibilityFrames = MAX_INVINCIBILITY_FRAMES;
		}
	}
	
	@Override
	public float getMaxHitPoints() {
		return MAX_HIT_POINTS;
	}

	
	@Override
	public void kill() {
		hitPoints = 0;
	}

	@Override
	public boolean isDead() {
		return isDead;
	}
	
	/// ARPGPlayer implements Interactor
	
	@Override
	public List<DiscreteCoordinates> getCurrentCells() {
		return Collections.singletonList(getCurrentMainCellCoordinates());
	}

	@Override
	public List<DiscreteCoordinates> getFieldOfViewCells() {
		return Collections.singletonList(getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
	}

	@Override
	public boolean wantsCellInteraction() {
		return true; 
	}

	@Override
	public boolean wantsViewInteraction() {	
		if (controller.isReady(ARPGControllerButton.ACTION) || currentState == ARPGPlayerState.SWORDATTACKING) {
			return true;			
		} else {
			return false;
		}
	}

	@Override
	public void interactWith(Interactable other) {
		other.acceptInteraction(handler);
	}

	/// ARPGPlayer implements Interactable
	
	@Override
	public boolean takeCellSpace() {
		return true; 
	}

	@Override
	public boolean isCellInteractable() {
		return true;
	}

	@Override
	public boolean isViewInteractable() {
		return true;
	}

	@Override
	public void acceptInteraction(AreaInteractionVisitor v) {
		((ARPGInteractionVisitor)v).interactWith(this);
	}

	/// ARPGPlayer implements Inventory.Holder

	@Override
	public boolean possesses(InventoryItem item) {
		return inventory.containsItem(item);
	}
	
	
	/// ARPGPlayer extends Graphics
	
	@Override
	public void draw(Canvas canvas) {
		// Blinking when damaged
		if (shouldDraw) {
			animations.get(currentState.animationIndex)[getOrientation().ordinal()].draw(canvas);		
		}
		// Dark filter
		lightHalo.draw(canvas);	
		
		// Graphical user interface
		gui.draw(canvas);
	}

	// ARPGPlayer possible states as a private enum, because it is useful only to ARPGPlayer.
	private enum ARPGPlayerState {
		IDLE			(8,  0, "zelda/player"),
		ROLLING			(4,  1, "zelda/player.roll"),
		SWORDATTACKING	(2,  2, "zelda/player.sword"),
		STAFFATTACKING	(2,  3, "zelda/player.staff_water"),
		BOWATTACKING	(2,  4, "zelda/player.bow"),
		SWIMMING		(8,  5,	"zelda/player.swim");

		private final int frameDuration;
		private final int animationIndex;
		private final String spriteSheetName;
		
		ARPGPlayerState(int frameDuration, int animationIndex, String spriteSheetName) {
			this.frameDuration = frameDuration;
			this.animationIndex = animationIndex;
			this.spriteSheetName = spriteSheetName;
		}
	}
	
	// ARPGPlayer interaction handler private class
	private class ARPGPlayerHandler implements ARPGInteractionVisitor {
		
		// Start swimming if step on a WATER type ARPGCell
		// Take an unfair amount of damage if step on a SPIKES type ARPGCell
		public void interactWith(ARPGCell arpgCell) {
			
			if (arpgCell.isType(ARPGCellType.WATER)) {
				currentState = ARPGPlayerState.SWIMMING;
			} else {
				if (currentState == ARPGPlayerState.SWIMMING) {
					currentState = ARPGPlayerState.IDLE;
				}
			}
			
			if (arpgCell.isType(ARPGCellType.SPIKES)) {
				ARPGPlayer.this.takeDamage(DamageType.PHYSICAL, 999.0f);
			}
		}
		
		// Pass through the door
		public void interactWith(Door door) {
			if (currentState == ARPGPlayerState.IDLE || currentState == ARPGPlayerState.ROLLING) {
				setIsPassingADoor(door);
			}
		}
		
		// Attack the monster with physical damage on attacking with the sword
		public void interactWith(Monster monster) {
			if (currentState == ARPGPlayerState.SWORDATTACKING) {
				monster.takeDamage(DamageType.PHYSICAL, PHYSICAL_POWER);
			}
		}
		
		// Slice the grass if attacking with the sword
		public void interactWith(Grass grass) {
			if (currentState == ARPGPlayerState.SWORDATTACKING) {
				grass.slice();				
			}
		}
		
		// Explode the bomb on attacking with the sword
		public void interactWith(Bomb bomb) {
			if (currentState == ARPGPlayerState.SWORDATTACKING) {
				bomb.explode();
			}
		}
		
		// Collect the Collectable item.
		public void interactWith(CollectableAreaEntity collectable) {
			collectable.collect(ARPGPlayer.this);
		}
		
		// Open the CastleDoor
		public void interactWith(CastleDoor castleDoor) {
			if (castleDoor.isOpen()) {
				setIsPassingADoor(castleDoor);
				castleDoor.close();
			} else {
				if (inventory.containsItem(ARPGItem.CASTLEKEY) && !castleDoor.isOpen() && controller.isReady(ARPGControllerButton.ACTION)) {
					controller.setCoolingDown(ARPGControllerButton.ACTION);
					castleDoor.open();
				}
			}
		}
		
	}
	
}
