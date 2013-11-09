package com.example.noquarter;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class DockedShip {
	private static Bitmap leftShipImage, rightShipImage, damageLeft, damageRight; 
	private int xPos, yPos; 
	private ArrayList<CannonBall> shotsFired; 
	private int[] hits = new int[10]; 
	private int startingHealth, health; 
	private static int screenWidth, screenHeight;
	private boolean left, hasBeenHit;
	
	public DockedShip( int startingX, int startingHealth) {
		xPos = startingX; 
		yPos = 0;
		shotsFired = new ArrayList<CannonBall>(12);
		this.startingHealth = startingHealth; 
		this.health = startingHealth; 
		if (startingX < screenWidth/2) {
			left = true; 
		}
		if (!left) {
			xPos -= 3*leftShipImage.getWidth()/4;
		}
		else {
			xPos -= leftShipImage.getWidth()/4;
		}
		yPos -= leftShipImage.getHeight(); 
	}
	
	public boolean isLeft() {return left;}
	public boolean hasBeenHit() {return hasBeenHit;}
	public Rect getBoundingBox() {
		Rect bb = null; 
		if (left) {
			bb = new Rect((int) (xPos + 40.0*leftShipImage.getWidth()/200.0), yPos + (int)(30.0*leftShipImage.getHeight()/200.0), (int) (xPos + leftShipImage.getWidth()), yPos + leftShipImage.getHeight());
		}
		else {
			bb = new Rect(xPos, yPos + (int)(30.0*leftShipImage.getHeight()/200.0), (int) (xPos + 110.0*leftShipImage.getWidth()/160.0), yPos + leftShipImage.getHeight());
		}
			
		return bb; 
	}
	
	public static void setImages(int screenWidth, int screenHeight, Bitmap leftShipImage, Bitmap rightShipImage, Bitmap dmgLeft, Bitmap dmgRight) {
		DockedShip.screenWidth = screenWidth;
		DockedShip.screenHeight = screenHeight; 
		DockedShip.leftShipImage = Bitmap.createScaledBitmap(leftShipImage, (int) ((160.0*screenHeight)/(1000.0)), screenHeight/5, true);
		DockedShip.rightShipImage = Bitmap.createScaledBitmap(rightShipImage, (int) ((160.0*screenHeight)/(1000.0)), screenHeight/5, true);
		DockedShip.damageLeft = Bitmap.createScaledBitmap(dmgLeft, (int) (leftShipImage.getHeight()/10.0), (int) (leftShipImage.getWidth()/10.0), true);
		DockedShip.damageRight = Bitmap.createScaledBitmap(dmgRight, (int) (rightShipImage.getHeight()/10.0), (int) (rightShipImage.getWidth()/10.0), true);
	}
	
	public int getLeftSide() {return xPos; }
	public int getRightSide() { return xPos + leftShipImage.getWidth(); }
	public int getCollidableSideUpperBound() { return (int) (yPos + leftShipImage.getHeight()*0.85); }
	public int getCollidableSideLowerBound() { return (int) (yPos + leftShipImage.getHeight()*0.35); }
	public ArrayList<CannonBall> getShotsFired() { return shotsFired; }
	public int getTop() {return yPos;}
	
	public void update(int velocity) {
		yPos += velocity; 
		for (CannonBall c : shotsFired) { 
			c.update(); 
		}
		for (int i = 0; i < hits.length; i++) {
			if (hits[i] > 0) {
				hits[i] += velocity; 
			}
		}
	}
	
	public void removeShot(CannonBall cb) {
		shotsFired.remove(cb); 
	}
	
	public void fireLeft() { 
		CannonBall top = new CannonBall(xPos, yPos+92, -1);
		CannonBall bottom = new CannonBall(xPos, yPos+149, -1); 
		shotsFired.add(top); 
		shotsFired.add(bottom);
	}
	
	public void fireRight() { 
		CannonBall top = new CannonBall(xPos+80, yPos+92, 1);
		CannonBall bottom = new CannonBall(xPos+80, yPos+149, 1); 
		shotsFired.add(top); 
		shotsFired.add(bottom);
	}
	
	public void handleLeftCollison(int x) {
		xPos = x; 
		//leftCollision = true; 
		handleCollision(); 
	}
	
	public void handleRightCollison(int x) {
		xPos = x-leftShipImage.getWidth();
		//rightCollision = true; 
		handleCollision(); 
	}
	
	public void handleCollision() {
		if (health > 0 ) {health--; 
		 	health--;
		}
	}
	
	public void shot(int location) {
		for (int i = 0; i < hits.length; i++) {
			if (hits[i] == 0) {
				hits[i] = location - damageLeft.getHeight()/2;
				i = hits.length; // EXIT LOOP
			}
		}
		hasBeenHit = true;
	}
	
	public int getHealthDeduction() { 
		return (health < 0) ? startingHealth : (startingHealth - health);
	}
	
	public void clearCollisions() {
		//leftCollision = false; 
	//	rightCollision = false; 
	}
	
	public void draw(Canvas canvas) {		
		for (CannonBall c : shotsFired) { 
			c.draw(canvas);
		} 
		if (left) { 
			canvas.drawBitmap(leftShipImage, xPos, yPos, null);
			for (int i = 0; i < hits.length; i++) {
				if (hits[i] > 0) {
					canvas.drawBitmap(damageLeft, xPos + leftShipImage.getWidth() - damageLeft.getWidth(), hits[i], null);
				}
			}
		}
		else {
			canvas.drawBitmap(rightShipImage, xPos, yPos, null);
			for (int i = 0; i < hits.length; i++) {
				if (hits[i] > 0) {
					canvas.drawBitmap(damageRight, xPos, hits[i], null);
				}
			}
		}
		
	}
}
