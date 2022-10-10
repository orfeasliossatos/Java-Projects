package ch.epfl.cs107.play.game.arpg.actor;


import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.Door;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;

public class CastleDoor extends Door {
	
	// Sprites
    private Sprite doorOpenSprite;
    private Sprite doorCloseSprite;
    
    /**
     * Default Door constructor
     * @param destination        (String): Name of the destination area, not null
     * @param otherSideCoordinates (DiscreteCoordinate):Coordinates of the other side, not null
     * @param signal (Logic): LogicGate signal opening the door, may be null
     * @param area        (Area): Owner area, not null
     * @param orientation (Orientation): Initial orientation of the entity, not null
     * @param position    (DiscreteCoordinate): Initial position of the entity, not null
     */
    public CastleDoor(String destination, DiscreteCoordinates otherSideCoordinates, Logic signal, Area area, Orientation orientation, DiscreteCoordinates position) {
    	super( destination, otherSideCoordinates,signal,area, orientation, position);
		
        doorOpenSprite  = new Sprite("zelda/castleDoor.open", 2.f, 2.f, this, new RegionOfInterest(0, 0, 64, 64),new Vector(0,0), 100, -1000);
        doorCloseSprite = new Sprite("zelda/castleDoor.close", 2.f, 2.f, this, new RegionOfInterest(0, 0, 64, 64),new Vector(0,0),100, -1000);
    }

    /**
     * Complementary Door constructor
     * @param destination        (String): Name of the destination area, not null
     * @param otherSideCoordinates (DiscreteCoordinate):Coordinates of the other side, not null
     * @param signal (Logic): LogicGate signal opening the door, may be null
     * @param area        (Area): Owner area, not null
     * @param position    (DiscreteCoordinate): Initial position of the entity, not null
     * @param orientation (Orientation): Initial orientation of the entity, not null
     * @param otherCells (DiscreteCoordinates...): Other cells occupied by the AreaEntity if any. Assume absolute coordinates, not null
     */
    public CastleDoor(String destination, DiscreteCoordinates otherSideCoordinates, Logic signal, Area area, Orientation orientation, DiscreteCoordinates position, DiscreteCoordinates... otherCells) {
    	super(destination, otherSideCoordinates,signal,area, orientation, position,otherCells);
    
        doorOpenSprite  = new Sprite("zelda/castleDoor.open", 2.f, 2.f, this, new RegionOfInterest(0, 0, 64, 64),new Vector(0,0), 100,-1000);
        doorCloseSprite = new Sprite("zelda/castleDoor.close", 2.f, 2.f, this, new RegionOfInterest(0, 0, 64, 64),new Vector(0,0),100,-1000);
    }
    
    /// CastleDoor implements Graphics

    @Override
    public void draw(Canvas canvas) {
        if(isOpen()) {
        	doorOpenSprite.draw(canvas);
        } else {
        	doorCloseSprite.draw(canvas);
    		doorCloseSprite.setDepth(-1000); // Close the door infront of the player : EXTREME GHETTO SOLUTION
        }
    }
    
    /// CastleDoor specific methods
    
    public void open() {
    	if (!isOpen()) {
    		setSignal(Logic.TRUE);
    	}
    }
    
    public void close() {
    	if (isOpen()) {
    		setSignal(Logic.FALSE);
    		doorCloseSprite.setDepth(1000); // Close the door behind the player WARNING : EXTREME GHETTO SOLUTION
    	}
    }
    
    /// CastleDoor implements Interactable

    @Override
    public boolean takeCellSpace() {
    	if(!isOpen()) {
    		return true;
    	} else {
    		return false;
    	}
    }

    @Override
    public boolean isCellInteractable() {
        return isOpen();
    }
    
    @Override
    public boolean isViewInteractable(){
        return !isOpen();
    }
    
    @Override
    public void acceptInteraction(AreaInteractionVisitor v) {
        ((ARPGInteractionVisitor)v).interactWith(this);
    }


}
