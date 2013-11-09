package com.example.noquarter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Vibrator;

public class Ship {
	private Bitmap shipImage;
	private Bitmap cannonFireLeftImage, cannonFireRightImage;
	private int cannonFireLeftCounter, cannonFireRightCounter;
	private boolean firedLeft, firedRight;
	private Bitmap wakeImage1, wakeImage2, wakeImage3; 
	private int xPos, yPos; 
	private int screenWidth, screenHeight; 
	private ArrayList<CannonBall> shotsFired; 
	private boolean leftCollision, rightCollision; 
	private int startingHealth, health; 
	private int topCannonYPos, bottomCannonYPos; 
	private int counter = 0; 
	private int halfImage;
	private int width;
	private int height;
	private Paint collisionRed; 
	private int horizontalVelocity;
	private final int MAX_SPEED = 2; 
	
	
	public Ship(int screenWidth, int screenHeight, int startingHealth) {
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight; 
		xPos = screenWidth/2; 
		yPos = (int) (3.0*screenHeight/5.0);
		shotsFired = new ArrayList<CannonBall>(12);
		this.startingHealth = startingHealth; 
		this.health = startingHealth;
		collisionRed = new Paint();
		collisionRed.setColor(Color.RED);
		collisionRed.setAlpha(0);
	}
	
	public void setShipImage(Bitmap image) { 
		shipImage = Bitmap.createScaledBitmap(image, (int) ((80.0*screenHeight)/(5.0*200.0)), screenHeight/5, true); 
		xPos -= image.getWidth()/2;
		yPos -= image.getHeight()/2; 
		topCannonYPos = yPos + (int) (shipImage.getHeight() * (92.0f/200.0f));
		bottomCannonYPos = yPos + (int) (shipImage.getHeight() * (149.0f/200.0f));
	}
	
	public int getLeftSide() {return xPos; }
	public int getRightSide() { return xPos + shipImage.getWidth(); }
	public int getCollidableSideUpperBound() { return (int) (yPos + 130.0*shipImage.getHeight()/200.0); }
	public int getCollidableSideLowerBound() { return yPos + shipImage.getHeight()/4; }
	public ArrayList<CannonBall> getShotsFired() { return shotsFired; }
	public int getHealth() {return health;}
	public boolean isFiringLeft() {return firedLeft;}
	public boolean isFiringRight() {return firedRight;}
	public Rect getBoundingBox() {
		return new Rect(xPos, (int) (yPos + 35.0*shipImage.getHeight()/200), xPos + shipImage.getWidth(), yPos + shipImage.getHeight());
	}
	public int getWidth() { return shipImage.getWidth(); }
	public boolean justCollided() { return collisionRed.getAlpha() != 0;}
	
	
	public void setWakeImage(Bitmap image) {
		wakeImage1 = Bitmap.createScaledBitmap(image, (int) ((80.0*screenHeight)/(5.0*200.0)), screenWidth/10, true); 
	}
	
	public void setWakeImage2(Bitmap image) {
		wakeImage2 = Bitmap.createScaledBitmap(image, (int) ((80.0*screenHeight)/(5.0*200.0)), screenWidth/10, true); 
	}
	
	public void setWakeImage3(Bitmap image) {
		wakeImage3 = Bitmap.createScaledBitmap(image, (int) ((80.0*screenHeight)/(5.0*200.0)), screenWidth/10, true);
	}
	
	public void setCannonFireImage(Bitmap imageL, Bitmap imageR) {
		cannonFireLeftImage = Bitmap.createScaledBitmap(imageL, (int) ((80.0*screenHeight)/(5.0*200.0)), screenWidth/10, true);
		cannonFireRightImage = Bitmap.createScaledBitmap(imageR, (int) ((80.0*screenHeight)/(5.0*200.0)), screenWidth/10, true);
		halfImage = cannonFireRightImage.getHeight()/4;
		width = cannonFireRightImage.getWidth()/3;
		height = width; 
	}
	
	public void moveLeft() {
		if (!leftCollision) {
			horizontalVelocity--;
			if (horizontalVelocity < -MAX_SPEED) {
				horizontalVelocity = -MAX_SPEED;
			}
		}
		else {
			handleCollision(1); 
		}
	}
	
	public void moveRight() {
		if (!rightCollision) {
			horizontalVelocity++;
			if (horizontalVelocity > MAX_SPEED) {
				horizontalVelocity = MAX_SPEED;
			} 
		}
		else {
			handleCollision(1); 
		}
	}
	
	public void stopShip() {
		horizontalVelocity = 0;
	}
	
	public void shiftLeft() {
		if (!leftCollision) {
			xPos -= 1;
		}
		else {
			handleCollision(1); 
		} 
	}
	

	public void shiftRight() {
		if (!rightCollision) {
			xPos += 1; 
		}
		else {
			handleCollision(1); 
		}
	}
	
	public void update() {
		xPos += horizontalVelocity;
		for (CannonBall c : shotsFired) { 
			c.update(); 
		}
	}
	
	public void removeShot(CannonBall cb) {
		shotsFired.remove(cb); 
	}
	
	public void fireLeft() { 
		if (!firedLeft) {
			CannonBall top = new CannonBall(xPos, topCannonYPos, -1);
			CannonBall bottom = new CannonBall(xPos, bottomCannonYPos, -1); 
			shotsFired.add(top); 
			shotsFired.add(bottom);
			firedLeft = true; 
		}
	}
	
	public void animateCannonFireLeft(Canvas canvas) {
		cannonFireLeftCounter++;
		if (cannonFireLeftCounter < 3) {																			//	new Rect(left, top, right, bottom)	
			canvas.drawBitmap(cannonFireLeftImage, new Rect(2*width, 0, 3*width, height), new Rect(xPos - width, topCannonYPos - halfImage, xPos, topCannonYPos + halfImage), null);
			canvas.drawBitmap(cannonFireLeftImage, new Rect(2*width, 0, 3*width, height), new Rect(xPos - width, bottomCannonYPos - halfImage, xPos, bottomCannonYPos + halfImage), null);
		}
		else if (cannonFireLeftCounter < 5){
			canvas.drawBitmap(cannonFireLeftImage, new Rect(width, 0, 2*width, height), new Rect(xPos - width, topCannonYPos - halfImage, xPos, topCannonYPos + halfImage), null);
			canvas.drawBitmap(cannonFireLeftImage, new Rect(width, 0, 2*width, height), new Rect(xPos - width, bottomCannonYPos - halfImage, xPos, bottomCannonYPos + halfImage), null);
		}
		else {
			canvas.drawBitmap(cannonFireLeftImage, new Rect(0, 0, width, height), new Rect(xPos - width, topCannonYPos - halfImage, xPos, topCannonYPos + halfImage), null);
			canvas.drawBitmap(cannonFireLeftImage, new Rect(0, 0, width, height), new Rect(xPos - width, bottomCannonYPos - halfImage, xPos, bottomCannonYPos + halfImage), null);
			if (cannonFireLeftCounter == 7) {
				cannonFireLeftCounter = 0; 
				firedLeft = false; 
			}
		}
	}
	
	public void fireRight() { 
		if (!firedRight) {
			CannonBall top = new CannonBall(xPos + shipImage.getWidth(), topCannonYPos, 1);
			CannonBall bottom = new CannonBall(xPos + shipImage.getWidth(), bottomCannonYPos, 1); 
			shotsFired.add(top); 
			shotsFired.add(bottom);
			firedRight = true;
		}
	}
	
	public void animateCannonFireRight(Canvas canvas) {
		cannonFireRightCounter++;
		if (cannonFireRightCounter < 3) {				//	new Rect(left, top, right, bottom)
			canvas.drawBitmap(cannonFireRightImage, new Rect(0, 0, width, height), new Rect(xPos + shipImage.getWidth(), topCannonYPos - halfImage, xPos + shipImage.getWidth() + width, topCannonYPos + halfImage), null);
			canvas.drawBitmap(cannonFireRightImage, new Rect(0, 0, width, height), new Rect(xPos + shipImage.getWidth(), bottomCannonYPos - halfImage, xPos + shipImage.getWidth() + width, bottomCannonYPos + halfImage), null);
		}
		else if (cannonFireRightCounter < 5){
			canvas.drawBitmap(cannonFireRightImage, new Rect(width, 0, 2*width, height), new Rect(xPos + shipImage.getWidth(), topCannonYPos - halfImage, xPos + shipImage.getWidth() + width, topCannonYPos + halfImage), null);
			canvas.drawBitmap(cannonFireRightImage, new Rect(width, 0, 2*width, height), new Rect(xPos + shipImage.getWidth(), bottomCannonYPos - halfImage, xPos + shipImage.getWidth() + width, bottomCannonYPos + halfImage), null);
		}
		else {
			canvas.drawBitmap(cannonFireRightImage, new Rect(2*width, 0, 3*width, height), new Rect(xPos + shipImage.getWidth(), topCannonYPos - halfImage, xPos + shipImage.getWidth() + width, topCannonYPos + halfImage), null);
			canvas.drawBitmap(cannonFireRightImage, new Rect(2*width, 0, 3*width, height), new Rect(xPos + shipImage.getWidth(), bottomCannonYPos - halfImage, xPos + shipImage.getWidth() + width, bottomCannonYPos + halfImage), null);
			if (cannonFireRightCounter == 7) {
				cannonFireRightCounter = 0; 
				firedRight = false; 
			}
		}
	}
	
	public void animateWake(Canvas canvas) {
		double factor = 0.85;
		if (counter <2) {
			canvas.drawBitmap(wakeImage3, xPos, (float) (yPos + shipImage.getHeight() * factor), null);
		}
		else if (counter < 4){
			canvas.drawBitmap(wakeImage1, xPos, (float) (yPos + shipImage.getHeight() * factor), null);
		}
		else {
			canvas.drawBitmap(wakeImage2, xPos, (float) (yPos + shipImage.getHeight() * factor), null);
			if (counter == 6) {
				counter = 0; 
			}
		}
		counter++;
	}
	
	public void handleLeftCollison(int x) {
		xPos = x; 
		leftCollision = true; 
		handleCollision(1); 
	}
	
	public void handleRightCollison(int x) {
		xPos = x-shipImage.getWidth();
		rightCollision = true; 
		handleCollision(1); 
	}
	
	private void handleCollision(int deduction) {
		if (!justCollided()) {
			if (health > 0 ) { 
			 	health-= deduction;
			}
			collisionRed.setAlpha(200);
		}
	}
	
	public void collideWithDockedShip(int location) {
		handleCollision(startingHealth/5); 
		xPos = location; 
	}
	
	public void collideWithCargo() {
		handleCollision(startingHealth/10);
	}
	
	public int getHealthDeduction() { 
		return (health < 0) ? startingHealth : (startingHealth - health);
	}
	
	public void clearCollisions() {
		leftCollision = false; 
		rightCollision = false; 
	}
	
	public void replenishHealth() {
		health += startingHealth/20; 
		if (health > startingHealth) {
			health = startingHealth;
		}
	}
	
	public void draw(Canvas canvas) {
		if (collisionRed.getAlpha() != 0) {
			canvas.drawPaint(collisionRed);
			collisionRed.setAlpha(collisionRed.getAlpha() - 50);
		}
		else {
			clearCollisions();
		}
		// Animate Wake
		animateWake(canvas);
		// Draw Ship
		canvas.drawBitmap(shipImage, xPos, yPos, null);
		// Draw Firing Animations
		if (firedRight) { animateCannonFireRight(canvas);}
		if (firedLeft) { animateCannonFireLeft(canvas);}
		// Draw Cannon balls fired by ship
		for (CannonBall c : shotsFired) { 
			c.draw(canvas);
		}
	}
}
