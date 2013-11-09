package server;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;

public class Server {
	private Firebase db;
	private String baseURL = "https://pencilplay.firebaseio.com/";
	
	public void pushImage() {
		db = new Firebase(baseURL);
		int[] a = {2, 3, 4, 1, 12, 23};
		db.child("image").setValue(a);
	}

	public Server() {}
	
	public void run() {
		db = new Firebase(baseURL + "/image");
		db.addValueEventListener(new ValueEventListener() {

			@Override
			public void onCancelled() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onDataChange(DataSnapshot snap) {
				Object jsonImage = snap.getValue(); 
				if (jsonImage != null) {
					// Deserialize the image
					BufferedImage img = extractImage(jsonImage); 
					// Processes the image
					ImageProcessor processor = new ImageProcessor();
					// Extract necessary values
					processor.processImage(img);
					int startPosX = processor.getStartPosition().x;
					int startPosY = processor.getStartPosition().y; 
					int[][] floor = processor.getFloor(); 
					// Update Firebase
					Firebase level = new Firebase(baseURL + "/level");
					level.child("startPosX").setValue(startPosX);
					level.child("startPosY").setValue(startPosY);
					level.child("floor").setValue(floor);
				}
			}
		});
	}
	
	public BufferedImage extractImage(Object jsonImg) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(ImageIO.createImageInputStream(jsonImg));
		} catch (IOException e) {
			System.out.println("Error reading image from Firebase.");
			e.printStackTrace();
		}
		return img; 
	}
	
	public static void main(String[] args) {
		Server server = new Server(); 
		//server.run();
		server.pushImage();
	}
}
