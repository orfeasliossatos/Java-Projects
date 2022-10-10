package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.actor.Graphics;
import ch.epfl.cs107.play.game.actor.ImageGraphics;
import ch.epfl.cs107.play.game.areagame.io.ResourcePath;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

/**
 * This class is the graphical user interface for the player.
 */
public class ARPGPlayerStatusGUI implements Graphics {
	
	// It has its own arpgPlayer, whose attributes it can get and draw.
	// Therefore can be extended for multiplayer usage ;)
	ARPGPlayer arpgPlayer;
	private static final float DEPTH = 1100;
	
	private float canvasWidth;
	private float canvasHeight;
	private Vector anchor;
	
	/**
	 * ARPGPlayerStatusGUI default constructor. Protected to help ensure only the ARPGPlayer has a ARPGPlayerStatusGUI
	 * @param (ARPGPlayer) arpgPlayer
	 */
	protected ARPGPlayerStatusGUI(ARPGPlayer arpgPlayer) {
		this.arpgPlayer = arpgPlayer;
	}
	
	/// ARPGPlayerStatusGUI specific methods
	
	//.. You may ignore literals used in ImageGraphics instanciations! They are purely aesthetic and have no semantic meaning. //
	
	/**
	 * This method generates and draw the "YOU DIED" death message to the canvas
	 * @param (Canvas) canvas
	 */
	private void drawDeathMessage(Canvas canvas) {
		ImageGraphics deathMessage = new ImageGraphics(ResourcePath.getSprite("YouDied"), 16, 8);
		deathMessage.setAnchor(anchor.add(new Vector(-3, 1)));
		deathMessage.setDepth(DEPTH);
		deathMessage.draw(canvas);
	}
	
	
	/**
	 * This method generates and draws the "item bar" to the canvas. 
	 * @param (Canvas) canvas
	 * @param (ARPGItem) currentItem
	 */
	private void drawItemBar(Canvas canvas) {
		// The brown circle upon which items will be shown
		float gearDisplayScale = 1.25f;
		ImageGraphics gearDisplay = new ImageGraphics(ResourcePath.getSprite("zelda/gearDisplay"),
			gearDisplayScale, gearDisplayScale, new RegionOfInterest(0, 0, 32, 32), 
			anchor.add(new Vector(0.25f, canvasHeight - 1.5f)), 1, DEPTH);
		
		// The current item
		float currentItemDisplayScale = 0.7f;
		ImageGraphics currentItemDisplay = new ImageGraphics(ResourcePath.getSprite(arpgPlayer.getCurrentItem().getSpriteName()),
			currentItemDisplayScale, currentItemDisplayScale, getRegionOfInterest(arpgPlayer.getCurrentItem()),
			anchor.add(new Vector(0.55f, canvasHeight - 1.2f)), 1, DEPTH);
		
		// Draw
		gearDisplay.draw(canvas);
		currentItemDisplay.draw(canvas);
	}
	
	/**
	 * This method generates and draws the "hitPoints bar" to the canvas. 
	 * @param (Canvas) canvas
	 * @param (float) maxHp
	 * @param (float) hp
	 */
	private void drawHpBar(Canvas canvas) {
		// Displacement variables
		final float shift = 0.7f;
		final float heartDisplayScale = 0.7f;		

		// Heart graphics
		ImageGraphics fullHeart = new ImageGraphics(ResourcePath.getSprite("zelda/heartDisplay"), 
			heartDisplayScale, heartDisplayScale, new RegionOfInterest(32, 0, 16, 16),
			anchor.add(new Vector(0.95f, canvasHeight - 1.2f)), 1, DEPTH); 		
		
		ImageGraphics halfHeart = new ImageGraphics(ResourcePath.getSprite("zelda/heartDisplay"),
			heartDisplayScale, heartDisplayScale, new RegionOfInterest(16, 0, 16, 16),
			anchor.add(new Vector(0.95f, canvasHeight - 1.2f)), 1, DEPTH); 
		
		ImageGraphics greyHeart = new ImageGraphics(ResourcePath.getSprite("zelda/heartDisplay"),
				heartDisplayScale, heartDisplayScale, new RegionOfInterest(0, 0, 16, 16),
				anchor.add(new Vector(0.95f, canvasHeight - 1.2f)), 1, DEPTH);
		
		// Draw
		
		// Grey hearts are drawn first
		for (int i = 0; i < (int) Math.floor(arpgPlayer.getMaxHitPoints()); i++) {
			greyHeart.setAnchor(greyHeart.getAnchor().add(new Vector(shift, 0)));
			greyHeart.draw(canvas); 
		}
		
		if (arpgPlayer.getHitPoints() > 0) {
			// Full hearts
			for (int i = 0; i < (int) Math.floor(arpgPlayer.getHitPoints()); ++i) {
				fullHeart.setAnchor(fullHeart.getAnchor().add(new Vector(shift, 0)));
				fullHeart.draw(canvas);
			}
			// Half hearts
			if (Math.floor(arpgPlayer.getHitPoints()) != arpgPlayer.getHitPoints()) {
				halfHeart.setAnchor(halfHeart.getAnchor().add(new Vector(shift * ((int) Math.floor(arpgPlayer.getHitPoints()) + 1), 0)));
				halfHeart.draw(canvas);
			}
		}
	}
	
	/**
	 * This method generates and draws the "money bar" to the canvas. 
	 * @param (Canvas) canvas
	 * @param (int) money
	 */
	private void drawMoneyBar(Canvas canvas) {
		// The strip of paper on which the money count will be written
		float moneyDisplayScale = 1.45f;
		ImageGraphics moneyDisplay = new ImageGraphics(ResourcePath.getSprite("zelda/coinsDisplay"), 
			moneyDisplayScale * 2, moneyDisplayScale, new RegionOfInterest(0, 0, 64, 64), 
			anchor.add(new Vector(0.3f, 0.3f)), 1, DEPTH);
		
		moneyDisplay.draw(canvas);
		
		
		// Iterate upon the digits
		String numberString = Integer.toString(arpgPlayer.getMoney());
		float shift = 0.45f;
		for (int i = 0; i < numberString.length(); i++){      
		    RegionOfInterest numberRegion;
		    switch(numberString.charAt(i)) {
			    case '0':	{numberRegion = new RegionOfInterest(16, 32, 16, 16);	break;}
			    case '1':	{numberRegion = new RegionOfInterest(0, 0, 16, 16);		break;}
		    	case '2':	{numberRegion = new RegionOfInterest(16, 0, 16, 16);	break;} 
		    	case '3':	{numberRegion = new RegionOfInterest(32, 0, 16, 16);	break;} 
		    	case '4':	{numberRegion = new RegionOfInterest(48, 0, 16, 16);	break;} 
		    	case '5':	{numberRegion = new RegionOfInterest(0, 16, 16, 16);	break;} 
		    	case '6':	{numberRegion = new RegionOfInterest(16, 16, 16, 16);	break;}
		    	case '7':	{numberRegion = new RegionOfInterest(32, 16, 16, 16);	break;}
		    	case '8':	{numberRegion = new RegionOfInterest(48, 16, 16, 16);	break;}
		    	case '9':	{numberRegion = new RegionOfInterest(0, 32, 16, 16);	break;} 
		    	default:	{numberRegion = null; 									break;}
		    }
		    
		    float digitDisplayScale = 0.65f;
		    ImageGraphics digitDisplay = new ImageGraphics(ResourcePath.getSprite("zelda/digits"), 
		    	digitDisplayScale, digitDisplayScale, numberRegion,
		    	anchor.add(new Vector(1.4f + shift * i, 0.75f )), 1, DEPTH);
		
			digitDisplay.draw(canvas);
		}
	}
	
	/**
	 * This method returns the relevant regions of interest for each possible ARPGItem
	 * @param (ARPGItem) item whose region of interest interests us
	 */
	private RegionOfInterest getRegionOfInterest(ARPGItem item) {
		int firstCoordinate = 0, secondCoordinate = 0, thirdCoordinate = 64, fourthCoordinate = 64;
		switch(item) {
			case BOMB:	{thirdCoordinate = 16;	fourthCoordinate = 16;	break;}
			case STAFF:	{thirdCoordinate = 16;	fourthCoordinate = 16;	break;}
			default:
				break;
		}
		
		return new RegionOfInterest(firstCoordinate, secondCoordinate, thirdCoordinate, fourthCoordinate);
	}

	/// ARPGPlayerStatusGUI implements Graphics
	
	@Override
	public void draw(Canvas canvas) {
		
		canvasWidth = canvas.getScaledWidth();
		canvasHeight = canvas.getScaledHeight();
		anchor = canvas.getTransform().getOrigin().sub(new Vector(canvasWidth / 2, canvasHeight / 2));
		
		// GUI
		this.drawHpBar(canvas);
		this.drawItemBar(canvas);
		this.drawMoneyBar(canvas);
		
		// Death message
		if (arpgPlayer.isDead()) {
			this.drawDeathMessage(canvas);			
		}
	}
}

	
	
	
	
	
	
	
	
	
	
	
	
	
	