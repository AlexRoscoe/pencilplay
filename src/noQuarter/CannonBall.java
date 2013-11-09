package com.example.noquarter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;

public class CannonBall {
	private static Bitmap image;
	private static int speed; 
	private float xPos, yPos; 
	private final int VELOCITY = 15; 
	private int direction; 
	
	
	public CannonBall(int startX, int startY, int direction) {
		xPos = startX;
		yPos = startY; 
		this.direction = direction;
	}
	
	public Point getHitPosition() {
		return new Point ((int)xPos + image.getWidth(), (int)yPos + image.getHeight()/2);
	}
	
	public int getTop() { return (int)yPos; } 
	public int getBottom() { return (int)yPos + image.getWidth(); } 
	public int getWidth() { return image.getWidth(); }
	public Rect getBoundingBox() { return new Rect((int)xPos, (int)yPos, (int)xPos + image.getWidth(), (int) (yPos + image.getHeight())); }
	
	public boolean isTravellingLeft() {
		return direction == -1; 
	}
	
	public void update() { 
		xPos += VELOCITY * direction; 
		yPos += speed; 
	}
	
	public static void setSpeed(int speedIn) {
		speed = speedIn/5; 
	}
	
	public static void setImage(Bitmap imageIn) { 
		image = imageIn; 
	}
	
	public void draw(Canvas canvas) {
		canvas.drawBitmap(image, xPos, yPos, null);
	}
}
