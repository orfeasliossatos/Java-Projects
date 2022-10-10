package ch.epfl.cs107.play.game.arpg.handler;

import ch.epfl.cs107.play.game.areagame.actor.CollectableAreaEntity;
import ch.epfl.cs107.play.game.arpg.ARPGBehavior.ARPGCell;
import ch.epfl.cs107.play.game.arpg.actor.ARPGPlayer;
import ch.epfl.cs107.play.game.arpg.actor.Bomb;
import ch.epfl.cs107.play.game.arpg.actor.CastleDoor;
import ch.epfl.cs107.play.game.arpg.actor.FireSpell;
import ch.epfl.cs107.play.game.arpg.actor.Grass;
import ch.epfl.cs107.play.game.arpg.actor.Monster;
import ch.epfl.cs107.play.game.rpg.handler.RPGInteractionVisitor;

public interface ARPGInteractionVisitor extends RPGInteractionVisitor {
   
	/**
     * Simulate an interaction between ARPG Interactor and an ARPGCell
     * @param arpgcell (ARPGCell), not null
     */
	default void interactWith(ARPGCell arpgCell) {
		// by default the interaction is empty
	}
	
	/**
     * Simulate an interaction between ARPG Interactor and an ARPGPlayer
     * @param arpgPlayer (ARPGPlayer), not null
     */
	default void interactWith(ARPGPlayer arpgPlayer) {
		// by default the interaction is empty
	}

    /**
     * Simulate an interaction between ARPG Interactor and Grass
     * @param door (Door), not null
     */
    default void interactWith(Grass grass) {
        // by default the interaction is empty
    }
    
    /**
     * Simulate an interaction between ARPG Interactor and a Bomb
     * @param door (Door), not null
     */
    default void interactWith(Bomb bomb) {
        // by default the interaction is empty
    }
    
    /**
     * Simulate an interaction between ARPG Interactor and a Grass
     * @param door (Door), not null
     */
    default void interactWith(Monster grass) {
        // by default the interaction is empty
    }
    
    /**
     * Simulate an interaction between ARPG Interactor and a FireSpell
     * @param fireSpell (FireSpell), not null
     */
    default void interactWith(FireSpell fireSpell) {
    	// by default the interaction is empty
    }
    
    /**
     * Simulate an interaction between ARPG Interactor and a CollectableAreaEntity
     * @param collectable (ColleactableAreaEntity), not null
     */
    default void interactWith(CollectableAreaEntity collectable) {
    	// by default the interaction is empty
    }

    /**
     * Simulate an interaction between ARPG Interactor and a CastleDoor
     * @param castleDoor (CastleDoor), not null
     */
    default void interactWith(CastleDoor castleDoor) {
    	// by default the interaction is empty
    }
    
}
