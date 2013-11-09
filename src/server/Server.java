package server;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;

public class Server {
	private Firebase db;

	public Server() {
		
	}
	
	public void initialize() {
		db = new Firebase("https://pencilplay.firebaseio.com/");
		db.addValueEventListener(new ValueEventListener() {

			@Override
			public void onCancelled() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onDataChange(DataSnapshot snap) {
				BufferedImage img = extractImage(); 
				// Processes the image
				ImageProcessor processor = new ImageProcessor();
				// Extract necessary values
				processor.processImage(img);
				int startPosX = processor.getStartPosition().x;
				int startPosY = processor.getStartPosition().y; 
				int[][] floor = processor.getFloor(); 
			}
		});
	}
	
	public BufferedImage extractImage() {
		BufferedImage img;
		try {
			img = ImageIO.read(ImageIO.createImageInputStream(snap.getValue()));
		} catch (IOException e) {
			System.out.println("Error reading image from Firebase.");
			e.printStackTrace();
		}
		return img; 
	}
	
}
