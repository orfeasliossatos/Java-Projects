package ch.epfl.cs107.play.game.tutos.area.tuto2;

import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.tutos.area.Tuto2Area;

public class Ferme extends Tuto2Area {

	protected void createArea() {
		registerActor(new Background(this));
		
		//registerActor(new Foreground(this));
	}

	@Override
	public String getTitle() {
		return "zelda/Ferme";
	}
}
