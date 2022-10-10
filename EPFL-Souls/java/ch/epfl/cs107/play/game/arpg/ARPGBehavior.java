package ch.epfl.cs107.play.game.arpg;

import ch.epfl.cs107.play.game.areagame.AreaBehavior;
import ch.epfl.cs107.play.game.areagame.actor.AnimatedDecoration;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.actor.FlyableEntity;
import ch.epfl.cs107.play.game.arpg.actor.SwimmableEntity;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Window;

/**
 * ARPGBehavior concretizes the notion of AreaBehavior, which is essentially a grid made of cells.
 */
public class ARPGBehavior extends AreaBehavior {
	
	/**
	 *  ARPGCellType contains (1) information about its associated colour in a behaviour image,
	 *  (2) whether a MovableEntity can traverse the cell, 
	 *  (3) and whether a FlyableEntity can traverse the cell.
	 */
	public enum ARPGCellType {
		NULL		(0,				false,	false),
		WALL		(-16777216,		false,	false),
		IMPASSABLE	(-8750470,		false,	true),
		INTERACT	(-256,			true,	true),
		DOOR		(-195580,		true,	true),
		WALKABLE	(-1,			true,	true),
		WATER		(-16776961,		false,	true),
		SPIKES		(-16711936,		true,	true);

		final int type;
		final boolean isWalkable;
		final boolean isFlyable;

		ARPGCellType(int type, boolean isWalkable, boolean isFlyable){
			this.type = type;
			this.isWalkable = isWalkable;
			this.isFlyable = isFlyable;
		}

		public static ARPGCellType toType(int type){
			for(ARPGCellType ict : ARPGCellType.values()){
				if(ict.type == type)
					return ict;
			}
			// When you add a new color, you can print the int value here before assign it to a type
			System.out.println(type);
			return NULL;
		}
	}
	
	/**
	 * Default ARPGBehavior Constructor
	 * @param window (Window), not null
	 * @param name (String): Name of the Behavior, not null
	 */
	public ARPGBehavior(Window window, String name){
		super(window, name);
		int height = getHeight();
		int width = getWidth();
		for(int y = 0; y < height; y++) {
			for (int x = 0; x < width ; x++) {
				ARPGCellType color = ARPGCellType.toType(getRGB(height-1-y, x));
				setCell(x,y, new ARPGCell(x,y,color));
			}
		}
	}
	
	public boolean isDoor(DiscreteCoordinates coord) {
		return (((ARPGCell)getCell(coord.x, coord.y)).isType(ARPGCellType.DOOR));
	}
	
	/**
	 * Inner class ARPGCell: Cell adapted to the ARPG game
	 */
	public class ARPGCell extends AreaBehavior.Cell {
		/// Type of the cell following the enum
		private final ARPGCellType type;
		
		/**
		 * Default ARPGCell Constructor
		 * @param x (int): x coordinate of the cell
		 * @param y (int): y coordinate of the cell
		 * @param type (EnigmeCellType), not null
		 */
		public ARPGCell(int x, int y, ARPGCellType type) {
			super(x, y);
			this.type = type;
		}
		
		/**
		 * Determines whether the ARPGCell is of a given type.
		 * @param (ARPGCellType) the type to be checked
		 * @return (boolean) whether or not the ARPGCell is of said type
		 */
		public boolean isType(ARPGCellType type) {
			return (this.type == type);	
		}
		
		/// ARPGCell extends AreaBehavior.Cell 
		
		@Override
		protected boolean canLeave(Interactable entity) {
			return true;
		}

		@Override
		protected boolean canEnter(Interactable entity) {
			// For AnimatedDecoration
			if (entity instanceof AnimatedDecoration) {
				return true;
			}
			
			// For FlyableEntities
			if (entity instanceof FlyableEntity) {
				if (((FlyableEntity)entity).canFly()) {
					return type.isFlyable;									
				}
			} else
			
			// For SwimmableEntites
			if (entity instanceof SwimmableEntity && type.equals(ARPGCellType.WATER)) {
				return true;
			} else
			
			if (super.hasNonTraversableContent()) {	// For other Interactables
				return false;
			} else {
				return type.isWalkable;				
			} 
			
			return false;
	    }

		/// ARPGCell implements Interactable
	    
		@Override
		public boolean isCellInteractable() {
			return true;
		}

		@Override
		public boolean isViewInteractable() {
			return false;
		}

		@Override
		public void acceptInteraction(AreaInteractionVisitor v) {
			((ARPGInteractionVisitor)v).interactWith(this);
		}

	}
}
