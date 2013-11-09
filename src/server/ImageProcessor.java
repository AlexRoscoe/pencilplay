package server;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ImageProcessor {
	
	private BufferedImage anImage;
	private JLabel ImageLabel = new JLabel();
	private JFrame frame = new JFrame();
	private JPanel jp = new JPanel();
	private int imageWidth, imageHeight;
	private int[][] imageArray, combination, convX, convY;
	
	
	public ImageProcessor(){}
	
	
	public void processImage(BufferedImage img) {
		
		imageArray = getGrayscale(img);
		
		// Get x gradient
		convX = convoluteX();
		
		// Get y gradient, first need to get column
		convY = convoluteY();
		
		// combine 
		combination = combine(convX, convY);
		
		// clean up (average into one line)
		for(int i = 0; i < combination.length; i++){
			for(int j = 0; j < combination.length; j++){
				anImage.setRGB(i, j, combination[i][j]);
			}
		}
		ImageLabel.setIcon(new ImageIcon(anImage));
		ImageLabel.setHorizontalAlignment(JLabel.CENTER);
		frame.add(jp);
		jp.add(ImageLabel);
		jp.setVisible(true);
		
		
		// Edge creep to find floor
		
		// remove floor
		
		// find player
	}
	
	// Convert to grayScale image
	public int[][] getGrayscale(BufferedImage image){
	
		int[][] imageArray = new int[image.getWidth()][image.getHeight()];
		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				int rgb = image.getRGB(i, j);
				int r = (rgb >> 16) & 0xFF;
				int g = (rgb >> 8) & 0xFF;
				int b = (rgb & 0xFF);
				int gray = (r + g + b) / 3;
				imageArray[i][j] = gray;
			}
		}
		return imageArray;
	}
	
	//get x gradient
	private int[][] convoluteX(){
		int max =0 ;
		int[][] xArray = new int[imageArray.length][imageArray[0].length];
		for(int i = 1; i < imageArray.length-1; i++){
			for(int j = 1; j < imageArray[0].length-1; j++){
				
				int x1 = imageArray[i-1][j];
				int x2 = imageArray[i+1][j];
				
				double val = Math.abs(x1-x2);
				xArray[i][j] = (int) val;
				if(max < (int)val){
					max = (int) val;
				}
			}
		}
		System.out.println(max);
		return xArray;
	}
	
	//get y gradient
	private int[][] convoluteY()
	{
		int max =0 ;
		int[][] yArray = new int[imageArray.length][imageArray[0].length];
		for(int i = 1; i < imageArray.length-1; i++){
			for(int j = 1; j < imageArray[0].length-1; j++){
				
				int y1 = imageArray[i][j-1];
				int y2 = imageArray[i][j+1];
				
				double val = Math.abs(y1-y2);
				yArray[i][j] = (int) val;
				if(max < (int)val){
					max = (int) val;
				}
				
			}
		}
		
		System.out.println(max);
		return yArray;
	}
	
	private int[][] combine(int[][] convX, int[][] convY){
		int[][] arr = new int[convX.length][convY[0].length];
		for(int i =0; i < convX.length; i++){
			for(int j =0; i < convY[0].length; i++){
				if(convX[i][j] == 1 || convY[i][j] == 1){
					arr[i][j] = 1;  
				 } else {
					 arr[i][j] = 0; 
				 }
				
			}
		}
		return arr;
	}
	
	public void findFloor(int[][] img) {
		ArrayList<Point> leftSide = new ArrayList<Point>(); 
		// Check left side
		for (int i = 0; i < imageWidth/4; i++) {
			for (int j = 0; j < imageHeight; j++) {
				if (img[i][j] == 1) {
					leftSide.add(new Point(i, j)); 
				}
				if (leftSide.size() >= 20) { // We have enough points to try. 
					break; 
				}
			}
		}
		ArrayList<Point> rightSide = new ArrayList<Point>(); 
		// Check right side
		for (int i = imageWidth-1; i > imageWidth - imageWidth/4; i--) {
			for (int j = 0; j < imageHeight; j++) {
				if (img[i][j] == 1) {
					rightSide.add(new Point(i, j)); 
				}
				if (rightSide.size() >= 20) { // We have enough points to try. 
					break; 
				}
			}
		}
		ArrayList<Point> bottom = new ArrayList<Point>(); 
		// Check bottom
		for (int i = 0; i < imageWidth; i++) {
			for (int j = imageHeight-1; j > imageHeight - imageHeight/4; j--) {
				if (img[i][j] == 1) {
					bottom.add(new Point(i, j)); 
				}
				if (bottom.size() >= 20) { // We have enough points to try. 
					break; 
				}
			}
		}
		
	}
	
	// Returns the starting position of the player.
	public Point getStartPosition() {
		return new Point(0, 0); 
	}
	
	// Returns a screen containing only the pixels corresponding to the floor. 
	public int[][] getFloor() {
		return new int[6][9];
	}
}
