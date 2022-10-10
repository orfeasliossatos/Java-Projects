package ch.epfl.cs107.play.game.arpg;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.arpg.actor.ARPGPlayer;
import ch.epfl.cs107.play.game.arpg.area.Chateau;
import ch.epfl.cs107.play.game.arpg.area.Ferme;
import ch.epfl.cs107.play.game.arpg.area.Route;
import ch.epfl.cs107.play.game.arpg.area.RouteChateau;
import ch.epfl.cs107.play.game.arpg.area.RouteTemple;
import ch.epfl.cs107.play.game.arpg.area.Temple;
import ch.epfl.cs107.play.game.arpg.area.Village;
import ch.epfl.cs107.play.game.rpg.RPG;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Window;


/**
 * Action Roll Play Game is an RPG that concretizes inherited notions of Player and Area.
 */
public class ARPG extends RPG {

	/// How much of the current area is shown (zoom)
	public final static float CAMERA_SCALE_FACTOR = 10.f;
	/// Timestep
	public final static float STEP = 0.05f;
	/// Wait time before respawning
	private static float respawnTime = 0;
	private final static float MAX_RESPAWN_TIME = 3.f;

	/// Names of the available areas, and where the player spawns.
	private final String[] areas = {"zelda/Ferme", 
			"zelda/Village", 
			"zelda/Route", 
			"zelda/RouteChateau", 
			"zelda/Chateau", 
			"zelda/RouteTemple", 
			"zelda/Temple"};
	private final DiscreteCoordinates[] startingPositions = {
				new DiscreteCoordinates(6, 10),
				new DiscreteCoordinates(30, 15),
				new DiscreteCoordinates(5, 15),
				new DiscreteCoordinates(8, 10),
				new DiscreteCoordinates(6, 9),
				new DiscreteCoordinates(4, 4),
				new DiscreteCoordinates(2, 3),
				};

	private int areaIndex;
	
	/**
	 * Initialize all the areas.
	 */
	private void createAreas(){
		addArea(new Ferme());
		addArea(new Village());
		addArea(new Route());
		addArea(new RouteChateau());
		addArea(new Chateau());
		addArea(new RouteTemple());
		addArea(new Temple());
	}

	/// ARPG implements Playable.
	
	/**
	 * Initialize the ARPG : The Areas and the ARPGPlayer.
	 */
	@Override
	public boolean begin(Window window, FileSystem fileSystem) {
		if (super.begin(window, fileSystem)) {
			createAreas();
			areaIndex = 0;
			Area area = setCurrentArea(areas[areaIndex], true);
			initPlayer(new ARPGPlayer(area, Orientation.DOWN, startingPositions[areaIndex]));
			return true;
		}
		return false;
	}

	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		if(((ARPGPlayer)getPlayer()).isDead()) {
			
			/* DEATH MESSAGE */
			respawnTime += deltaTime;
			
			if (respawnTime >= MAX_RESPAWN_TIME) {
				respawnTime = 0;
				end();				
			}
		}
	}

	@Override
	public void end() {
		// Starting area
		Area area = setCurrentArea(areas[0], false);
		
		// Leave the Area and enter the starting one
		((ARPGPlayer)getPlayer()).leaveArea();
		((ARPGPlayer)getPlayer()).enterArea(area, startingPositions[0]);
		
		// Revive the player
		((ARPGPlayer)getPlayer()).revive();
	}

	@Override
	public String getTitle() {
		return "EPFL-Souls";
	}
}
