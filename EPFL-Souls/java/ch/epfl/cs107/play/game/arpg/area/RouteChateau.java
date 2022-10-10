package ch.epfl.cs107.play.game.arpg.area;

import java.util.Collections;

import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.areagame.actor.Foreground;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.arpg.actor.Bomb;
import ch.epfl.cs107.play.game.arpg.actor.CastleDoor;
import ch.epfl.cs107.play.game.arpg.actor.DarkLord;
import ch.epfl.cs107.play.game.arpg.actor.FlameSkull;
import ch.epfl.cs107.play.game.arpg.actor.LogMonster;
import ch.epfl.cs107.play.game.rpg.actor.Door;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Keyboard;

public class RouteChateau extends ARPGArea{
	protected void createArea() {
		registerActor(new Background(this));
		registerActor(new Foreground(this));

		// Doors
		registerActor(new Door("zelda/Route", new DiscreteCoordinates(9, 18), Logic.TRUE, this, Orientation.DOWN, new DiscreteCoordinates(9, 0), new DiscreteCoordinates(10, 0)));
		registerActor(new CastleDoor("zelda/Chateau", new DiscreteCoordinates(7, 1), Logic.FALSE, this, Orientation.UP, new DiscreteCoordinates(9, 13), new DiscreteCoordinates(10, 13)));
	
		// Enemies
		registerActor(new DarkLord(this, Orientation.DOWN, new DiscreteCoordinates(5, 5)));
	}

	@Override
	public void update(float deltaTime) {
		
		// Spawn creatures for testing
		
		super.update(deltaTime);
		if (getKeyboard().get(Keyboard.L).isPressed()) {
			if (canEnterAreaCells(new LogMonster(this, Orientation.DOWN, new DiscreteCoordinates(9, 9)), Collections.singletonList(new DiscreteCoordinates(9, 9)))) {
				registerActor(new LogMonster(this, Orientation.DOWN, new DiscreteCoordinates(9, 9)));				
			}
		}
		
		if (getKeyboard().get(Keyboard.S).isPressed()) {
			if (canEnterAreaCells(new FlameSkull(this, Orientation.DOWN, new DiscreteCoordinates(8, 10)), Collections.singletonList(new DiscreteCoordinates(8, 10)))) {
				registerActor(new FlameSkull(this, Orientation.DOWN, new DiscreteCoordinates(8, 10)));				
			}
		}
		if (getKeyboard().get(Keyboard.B).isPressed()) {
			if (canEnterAreaCells(new Bomb(this, Orientation.DOWN, new DiscreteCoordinates(7, 10)), Collections.singletonList(new DiscreteCoordinates(7, 10)))) {
				registerActor(new Bomb(this, Orientation.DOWN, new DiscreteCoordinates(7, 10)));				
			}
		}
		
	}
	
	@Override
	public String getTitle() {
		return "zelda/RouteChateau";
	}
}
