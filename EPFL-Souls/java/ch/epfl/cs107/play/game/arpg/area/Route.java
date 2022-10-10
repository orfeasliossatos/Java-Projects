package ch.epfl.cs107.play.game.arpg.area;

import ch.epfl.cs107.play.game.areagame.actor.AnimatedDecoration;
import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.areagame.actor.Foreground;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.arpg.actor.Grass;
import ch.epfl.cs107.play.game.arpg.actor.LogMonster;
import ch.epfl.cs107.play.game.rpg.actor.Door;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.signal.logic.Logic;

public class Route extends ARPGArea {

	protected void createArea() {
		registerActor(new Background(this));
		registerActor(new Foreground(this));
		
		// Doors
		registerActor(new Door("zelda/Ferme", 			new DiscreteCoordinates(18, 15),	Logic.TRUE, this, Orientation.UP, 	new DiscreteCoordinates(0, 15), new DiscreteCoordinates(0, 16)));
		registerActor(new Door("zelda/Village", 		new DiscreteCoordinates(29, 18),	Logic.TRUE, this, Orientation.DOWN, new DiscreteCoordinates(9, 0), new DiscreteCoordinates(10, 0)));
		registerActor(new Door("zelda/RouteChateau", 	new DiscreteCoordinates(9, 1),		Logic.TRUE, this, Orientation.UP, 	new DiscreteCoordinates(9, 19), new DiscreteCoordinates(10, 19)));
		registerActor(new Door("zelda/RouteTemple",		new DiscreteCoordinates(1, 4),		Logic.TRUE, this, Orientation.RIGHT, new DiscreteCoordinates(19, 10), new DiscreteCoordinates(19, 11), new DiscreteCoordinates(19, 9), new DiscreteCoordinates(19, 8)));
		
		// Grass
		for (int i = 5; i <= 7; i++)
			for(int j = 6; j <= 11; j++)
			{
				registerActor(new Grass(this, Orientation.UP, new DiscreteCoordinates(i ,j)));
			}
		
		// Waterfall
		registerActor(new AnimatedDecoration("zelda/waterfall", 3, 4, 4, 64, 64, 4, this, Orientation.DOWN, new DiscreteCoordinates(15, 4)));
	
		// Enemies
		registerActor(new LogMonster(this, Orientation.DOWN, new DiscreteCoordinates(3, 10)));
		registerActor(new LogMonster(this, Orientation.DOWN, new DiscreteCoordinates(8, 11)));
	}
	
	@Override
	public String getTitle() {
		return "zelda/Route";
	}
	
}
