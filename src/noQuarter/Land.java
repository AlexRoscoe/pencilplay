package com.example.noquarter;

import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Land {
	private int[] landL, landR;
	private Paint landColor;
	private Random r = new Random();
	private int screenWidth, screenHeight; 
	private int LAND_LIMIT = 0; 
	private int velocity;
	private int mapRemaining; 

	public Land(int screenWidth, int screenHeight, int velocity, int mapLength) {
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.velocity = velocity; 
		this.mapRemaining = mapLength; 
		LAND_LIMIT = screenWidth/4;
		landL = new int[screenHeight];
		landR = new int[screenHeight];
		generateLand(landL);
		generateLand(landR);
		landColor = new Paint(); 
		landColor.setAlpha(255);
		landColor.setColor(Color.GREEN);
	}
	
	public void setShipSpeed(int velocity) {this.velocity = velocity;}
	public int get_L_DockLocation() {return landL[0];}
	public int get_R_DockLocation() {return screenWidth - landR[0];}
	public int get_L_at(int i) {return landL[i];}
	public int get_R_at(int i) {return landR[i];}
	public int getLimit() {return LAND_LIMIT;}
	public int getMapRemaining() {return mapRemaining;}
	public int getVelocity() {return velocity;}
		

	public void generateLand(int[] array) {
		int dist = r.nextInt(LAND_LIMIT);
		for (int i = 0; i < landL.length; i++) { 
			int add = r.nextInt(2); // 0 - 5
			if (r.nextInt(2) == 1) { 
				add *= -1; 
			}
			dist += add; 
			if (dist >= LAND_LIMIT) { 
				dist = LAND_LIMIT; 
			}
			else if (dist <= 0) {
				dist = 0;
			}
			array[i] = dist; 
		}
	}
	
	public void upDateLand(int[] array) {
		int dist = array[0];
		for (int i = array.length-1; i >= velocity; i--) {
			array[i] = array[i-velocity];
		}
		for (int i = 0; i < velocity; i++) {
			int add = r.nextInt(2); // 0 - 5
			if (r.nextInt(2) == 1) { 
				add *= -1; 
			}
			dist += add; 
			if (dist >= LAND_LIMIT) { 
				dist = LAND_LIMIT; 
			}
			else if (dist <= 0) {
				dist = 0;
			}
			array[i] = dist;
		}
	}
	
	public void shotLeft(int lowerBound, int upperBound, int objectWidth) {
		for (int i = lowerBound; i <= upperBound; i++) {
			landL[i] -= objectWidth; 
		}
	}
	
	public void shotRight(int lowerBound, int upperBound, int objectWidth) {
		for (int i = lowerBound; i <= upperBound; i++) {
			landR[i] -= objectWidth; 
		}
	}
	
	public int checkLeftCollision(int lowerBound, int upperBound, int leftSideOfObject) {
		int pointHit = -1; 
		for (int i = lowerBound; i <= upperBound; i++) {
			//collision
			if (landL[i] >= leftSideOfObject) {
				if (landL[i] > pointHit) {
					pointHit = landL[i]; 
				}
			}
		}
		return pointHit; 
	}
	
	public int checkRightCollision(int lowerBound, int upperBound, int rightSideOfObject) {
		int pointHit = screenWidth; 
		for (int i = lowerBound; i <= upperBound; i++) {
			//collision
			if (screenWidth - landR[i] <= rightSideOfObject) {
				if (landR[i] < pointHit) {
					pointHit = screenWidth - landR[i]; 
				}
			}
		}
		return pointHit; 
	}
	
	public void update() {
		upDateLand(landL);
		upDateLand(landR);
		mapRemaining -= 1;
	}
	
	public void draw(Canvas canvas) {
		if (canvas != null) {
			for (int i = 0; i < landL.length; i++) { 
				canvas.drawLine(0, i, landL[i], i, landColor);
				canvas.drawLine(canvas.getWidth(), i, canvas.getWidth() - landR[i], i, landColor);
			}
		}
	}
}
