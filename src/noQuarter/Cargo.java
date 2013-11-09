package com.example.noquarter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Cargo {
	private int xPos, yPos, shipVelocity;
	private static Bitmap cargoImage; 
	private static int screenWidth, screenHeight; 
	
	public Cargo (int xPos, int yPos, int velocity) {
		this.xPos = xPos; 
		this.yPos = yPos; 
		this.shipVelocity = velocity; 
	}
	
	public int getTop() {return yPos;}
	public Rect getBoundingBox() {return new Rect(xPos, yPos, xPos + cargoImage.getWidth(), yPos + cargoImage.getHeight());}
	
	public static void setImage(Bitmap image, int screenWidth, int screenHeight) {
		Cargo.screenHeight = screenHeight; 
		Cargo.screenWidth = screenWidth;
		cargoImage = Bitmap.createScaledBitmap(image, (int) ((60.0*screenHeight)/(5.0*200.0)), (int) ((60.0*screenHeight)/(5.0*200.0)), true); 
	}
	
	public static int getWidth() {return cargoImage.getWidth();}
	
	public void update() {
		yPos += shipVelocity; 
	}
	
	public void draw(Canvas canvas) {
		canvas.drawBitmap(cargoImage, xPos, yPos, null);
	}
	
	
}
