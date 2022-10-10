package ch.epfl.cs107.play.game.arpg.area;

import ch.epfl.cs107.play.game.areagame.actor.AnimatedDecoration;
import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.areagame.actor.Foreground;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.rpg.actor.Door;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.signal.logic.Logic;

public class Ferme extends ARPGArea {

	protected void createArea() {
		registerActor(new Background(this));
		registerActor(new Foreground(this));
		
		// Doors
		registerActor(new Door("zelda/Route", new DiscreteCoordinates(1, 15), Logic.TRUE, this, Orientation.RIGHT, new DiscreteCoordinates(19, 15), new DiscreteCoordinates(19, 16)));
		registerActor(new Door("zelda/Village", new DiscreteCoordinates(4, 18), Logic.TRUE, this, Orientation.DOWN, new DiscreteCoordinates(4, 0), new DiscreteCoordinates(5, 0)));
		registerActor(new Door("zelda/Village", new DiscreteCoordinates(14, 18), Logic.TRUE, this, Orientation.DOWN, new DiscreteCoordinates(13, 0), new DiscreteCoordinates(14, 0)));

		// Baby log monsters (tree shoots)
		registerActor(new AnimatedDecoration("zelda/babyLogMonster", 10, 1, 1, 12, 16, 7, this, Orientation.DOWN, new DiscreteCoordinates(14, 8)));
		registerActor(new AnimatedDecoration("zelda/babyLogMonster", 10, 1, 1, 12, 16, 8, this, Orientation.DOWN, new DiscreteCoordinates(12, 5)));
		registerActor(new AnimatedDecoration("zelda/babyLogMonster", 10, 1, 1, 12, 16, 10, this, Orientation.DOWN, new DiscreteCoordinates(11, 7)));
		registerActor(new AnimatedDecoration("zelda/babyLogMonster", 10, 1, 1, 12, 16, 9, this, Orientation.DOWN, new DiscreteCoordinates(12, 4)));
		registerActor(new AnimatedDecoration("zelda/babyLogMonster", 10, 1, 1, 12, 16, 5, this, Orientation.DOWN, new DiscreteCoordinates(10, 6)));
		registerActor(new AnimatedDecoration("zelda/babyLogMonster", 10, 1, 1, 12, 16, 6, this, Orientation.DOWN, new DiscreteCoordinates(14, 6)));
	
	}

	@Override
	public String getTitle() {
		return "zelda/Ferme";
	}
	
}
