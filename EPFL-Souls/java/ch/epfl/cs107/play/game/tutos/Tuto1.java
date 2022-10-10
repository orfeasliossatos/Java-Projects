package ch.epfl.cs107.play.game.tutos;

import ch.epfl.cs107.play.game.areagame.AreaGame;
import ch.epfl.cs107.play.game.tutos.actor.SimpleGhost;
import ch.epfl.cs107.play.game.tutos.area.tuto1.Ferme;
import ch.epfl.cs107.play.game.tutos.area.tuto1.Village;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Button;
import ch.epfl.cs107.play.window.Keyboard;
import ch.epfl.cs107.play.window.Window;

public class Tuto1 extends AreaGame {
	
	private SimpleGhost player;
	
	private void createAreas()
	{
		super.addArea(new Ferme());
		super.addArea(new Village());
	}
	
	private void switchArea()
	{
		player.strengthen();
		
		super.getCurrentArea().unregisterActor(player);
		
		if (super.getCurrentArea().getTitle().equals("zelda/Ferme"))
		{
			super.setCurrentArea("zelda/Village", true);
		}
		else if (super.getCurrentArea().getTitle().equals("zelda/Village"))
		{
			super.setCurrentArea("zelda/Ferme", true);
		}
		
		super.getCurrentArea().registerActor(player);
		
		super.getCurrentArea().setViewCandidate(player);
	}
	
	@Override
	public String getTitle() {
		return "Tuto1";
	}
	
	@Override
	public void end() {
		// by default does nothing
    	// can save the game states if wanted
	}
	
	@Override
	public void update(float deltaTime) {
		
		if(player.isWeak())
		{
			switchArea();
		}
		
		super.update(deltaTime);
		
		Keyboard keyboard = getWindow().getKeyboard();
		
		Button key = keyboard.get(Keyboard.UP);
		if (key.isDown())
		{
			player.moveUp(player.getVitesse() * deltaTime);
		}
		
		key = keyboard.get(Keyboard.DOWN);
		if (key.isDown())
		{
			player.moveDown(player.getVitesse() * deltaTime);
		}
		
		key = keyboard.get(Keyboard.LEFT);
		if (key.isDown())
		{
			player.moveLeft(player.getVitesse() * deltaTime);
		}

		key = keyboard.get(Keyboard.RIGHT);
		if (key.isDown())
		{
			player.moveRight(player.getVitesse() * deltaTime);
		}
		
	}

	@Override
	public boolean begin(Window window, FileSystem fileSystem) {
		if (super.begin(window, fileSystem)) {
			
			player = new SimpleGhost(new Vector(18, 7), "ghost.1");
			
			createAreas();
			
			super.setCurrentArea("zelda/Ferme", true);
	
			super.getCurrentArea().registerActor(player);
			
			super.getCurrentArea().setViewCandidate(player);
			
			return true;
		}
		else return false;
	}
	
}
