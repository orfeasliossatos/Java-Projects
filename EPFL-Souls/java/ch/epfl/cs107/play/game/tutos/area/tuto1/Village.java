package ch.epfl.cs107.play.game.tutos.area.tuto1;

import ch.epfl.cs107.play.game.actor.Actor;
import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.tutos.actor.SimpleGhost;
import ch.epfl.cs107.play.game.tutos.area.SimpleArea;
import ch.epfl.cs107.play.math.Vector;

public class Village extends SimpleArea {
	
	protected void createArea() {
		Actor ghost = new SimpleGhost(new Vector(18, 7), "ghost.2");
	
		registerActor(ghost);
		
		registerActor(new Background(this));
	}

	@Override
	public String getTitle() {
		return "zelda/Village";
	}
}
