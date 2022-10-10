package ch.epfl.cs107.play.game.arpg;

import ch.epfl.cs107.play.window.Keyboard;

/**
 * ARPGController class represents a controller. It modularises the Keyboard interface.
 * It does not implement the Keyboard interface, because we can imagine plugging in an external controller.
 */
public class ARPGController {
	
	// A button's state is stored in the index corresponding to its ordinal as an enum.
	private int[] controllerScheme;			// What Keyboard.(KEY)s are mapped to what index
	private int[] framesSinceLastAction;	// How many frames since that button was last put on cooldown
	private boolean[] isReleased;			// Is the button currently released
	private boolean[] isPressed;			// Is the button currently pressed
	
	/**
	 * Default ARPGController constructor. Initializes the controller scheme.
	 */
	public ARPGController() {		
	
		controllerScheme = new int[] { Keyboard.E, Keyboard.TAB, Keyboard.SPACE, Keyboard.UP, Keyboard.RIGHT, Keyboard.DOWN, Keyboard.LEFT, Keyboard.R};
		framesSinceLastAction = new int[controllerScheme.length];
		isReleased = new boolean[controllerScheme.length];
		isPressed = new boolean[controllerScheme.length];
	}
	
	/* Alternative overloaded update methods for different controllers go here... */
	
	/**
	 * The controller retrieves essential information from the keyboard and manages buttons.
	 * @param (Keyboard) keyboard
	 */
	public void update(Keyboard keyboard) {
		// Count frames since last time the action associated with a button occured.
		for (int i = 0; i < controllerScheme.length; i++) {
			if (framesSinceLastAction[i] < ARPGControllerButton.fromInt(i).cooldownTime) {				
				framesSinceLastAction[i]++;
			}
		}
		
		// Check to see if a key was released
		for (int i = 0; i < controllerScheme.length; i++) {
			if (keyboard.get(controllerScheme[i]).isUp()) {
				isReleased[i] = true;
			}
			if (keyboard.get(controllerScheme[i]).isDown()) {
				isPressed[i] = true;
			} else {
				isPressed[i] = false;
			}
		}
		
	}
	
	/**
	 * Returns true if the button is "ready" to be used, and false otherwise. 
	 * "Ready", at the least, means that the button is currently being pressed. 
	 * However, for buttons like "ACTION" (mapped to E by default), the button has to be released and then wait a certain cooldown time.
	 * @param controllerButton (ARPGControllerButton)
	 * @return (boolean) whether or not the button is ready.
	 */
	public boolean isReady(ARPGControllerButton controllerButton) {
		
		// What it means for a button to be 'ready' depends on the button!
		switch(controllerButton) {
			// For these buttons, it only matters whether or not the button is pressed.
			case MOVEUP: 	{if(!isPressed[controllerButton.ordinal()]) {return false;} else {return true; }}
			case MOVERIGHT: {if(!isPressed[controllerButton.ordinal()]) {return false;} else {return true; }}
			case MOVEDOWN:  {if(!isPressed[controllerButton.ordinal()]) {return false;} else {return true; }}
			case MOVELEFT:  {if(!isPressed[controllerButton.ordinal()]) {return false;} else {return true; }}
			
			// For the ACTION button, we further ask whether the cooldownTime has been reached (to prevent multiple action presses)
			case ACTION:	{
				if(!isPressed[controllerButton.ordinal()]) {
					return false;
				} else if (framesSinceLastAction[controllerButton.ordinal()] == controllerButton.cooldownTime) {
					return true; 
				}
			}
			
			// For the other buttons, calling isReady(ARPGControllerButton) also informs the controller that the buttons is no longer released.
			default : {
				if (!isPressed[controllerButton.ordinal()]) {
					return false;
				} else {
					if ((isReleased[controllerButton.ordinal()]) && (framesSinceLastAction[controllerButton.ordinal()] == controllerButton.cooldownTime)) {
						isReleased[controllerButton.ordinal()] = false;
						return true;
					} else {
						return false;
					}
				}
			}
		}
	}
	
	/**
	 * This methods skips the cooldown time and assumes a button to be released.
	 * @param (ARPGControllerButton) controllerButton
	 */
	public void setIsReady(ARPGControllerButton controllerButton) {
		isReleased[controllerButton.ordinal()] = true;
		framesSinceLastAction[controllerButton.ordinal()] = controllerButton.cooldownTime;
	}
	
	/**
	 * Returns true if the button is released.
	 * @param (ARPGControllerButton) controllerButton
	 * @return (boolean) 
	 */
	public boolean isReleased(ARPGControllerButton controllerButton) {
		return isReleased[controllerButton.ordinal()];
	}
	
	/**
	 * Set the number of frames since the button was last activated to 0.
	 * @param (ARPGControllerButton) controllerButton
	 */
	public void setCoolingDown(ARPGControllerButton controllerButton) {
		framesSinceLastAction[controllerButton.ordinal()] = 0;
	}

	// Inner enum because it makes sense for buttons be "a part" of a controller.
	// Yet public because the buttons should be visible outside of the class.	 
	public enum ARPGControllerButton {
		// enum elements
		ACTION(4),
		SWITCH(2),
		ITEM(12),
		MOVEUP(0),		
		MOVERIGHT(0),	
		MOVEDOWN(0),	
		MOVELEFT(0),	
		ROLL(12),;
		
		// The number of frames between consecutive button presses
		private int cooldownTime;
		
		ARPGControllerButton(int cooldownTime) {
			this.cooldownTime = cooldownTime;
		}
		
		/**
		 * Convert an int into an ARPGControllerButton
		 * @param (int) index : the int representing the ARPGControllerButton
		 * @return (ARPGControllerButton) the ARPGControllerButton
		 */
		public static ARPGControllerButton fromInt(int index) {
			switch(index) {
			case 0 : { return ACTION; }
			case 1 : { return SWITCH; }
			case 2 : { return ITEM; }
			case 3 : { return MOVEUP; }
			case 4 : { return MOVERIGHT; }
			case 5 : { return MOVEDOWN; }
			case 6 : { return MOVELEFT; }
			case 7 : { return ROLL; }
			}
			return null;
		}
		
	}
	
}
