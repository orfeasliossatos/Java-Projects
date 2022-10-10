package ch.epfl.cs107.play.game.tutos.actor;

import java.awt.Color;

import ch.epfl.cs107.play.game.actor.Entity;
import ch.epfl.cs107.play.game.actor.TextGraphics;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public class SimpleGhost extends Entity {
	
	private Sprite sprite;
	
	private float hp;
	
	private float vitesse;
	
	private TextGraphics hpText;
	
	public SimpleGhost(Vector position, String spriteName)
	{
		super(position);
		
		sprite = new Sprite(spriteName, 1, 1, this);
	
		hp = 10;
	
		vitesse = 3;
		
		hpText = new TextGraphics(Integer.toString((int)hp), 0.4f, Color.BLUE);
	
		hpText.setParent(this);
		
		this.hpText.setAnchor(new Vector(-0.3f, 0.1f));
	}
	
	public boolean isWeak()
	{
		if (hp <= 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public void strengthen()
	{
		hp = 10;
	}

	public void moveUp(float delta)
	{
		setCurrentPosition(getPosition().add(0.f, delta));
	}
	
	public void moveDown(float delta)
	{
		setCurrentPosition(getPosition().add(0.f, -delta));
	}
	
	public void moveLeft(float delta)
	{
		setCurrentPosition(getPosition().add(-delta, 0.f));
	}
	
	public void moveRight(float delta)
	{
		setCurrentPosition(getPosition().add(delta, 0.f));
	}
	
	@Override
	public void draw(Canvas canvas) {
		sprite.draw(canvas);
		hpText.draw(canvas);
	}
	
	public void update(float deltaTime)
	{
		if (hp > 0)
		{
			hp = hp - deltaTime;
		}
		else if (hp < 0)
		{
			hp = 0;
		}
		hpText.setText(Integer.toString((int)hp));
	}
	
	public float getVitesse()
	{
		return vitesse;
	}
	
}
