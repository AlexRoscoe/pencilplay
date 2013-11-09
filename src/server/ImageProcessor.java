package server;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ImageProcessor {
	
	BufferedImage anImage;
	JLabel ImageLabel = new JLabel();
	JFrame frame = new JFrame();
	JPanel jp = new JPanel();
	Container pane = frame.getContentPane();
	
	public ImageProcessor(){}
	int[][] imageArray;
	int[][] combination;
	int[][] convX;
	int[][] convY;
	
	
	public void processImage(BufferedImage img) {
		
		anImage = img;
		ImageLabel.setIcon(new ImageIcon(anImage));
		ImageLabel.setHorizontalAlignment(JLabel.CENTER);
		
		frame.add(jp);
		jp.setLayout(new BorderLayout());
		jp.add(ImageLabel , BorderLayout.SOUTH);
		frame.pack();
		frame.setVisible(true);
		
		imageArray = getGrayscale(anImage);
		
		// Get x gradient
		convX = convoluteX();
		
		// Get y gradient, first need to get column
		convY = convoluteY();
		
		// combine 
		combination = combine(convX, convY);
		for(int i = 1; i < combination.length-1; i++){
			for(int j = 1; j < combination[0].length-1; j++){
				anImage.setRGB(i, j, combination[i][j]);
				if(combination[i][j] == 1){
					System.out.print(combination[i][j]);
				}
			}
		}
		
		
		// clean up (average into one line)
		
		
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
		int[][] xArray = new int[imageArray.length][imageArray[0].length];
		for(int i = 1; i < imageArray.length-1; i++){
			for(int j = 1; j < imageArray[0].length-1; j++){
				
				int x1 = imageArray[i-1][j];
				int x2 = imageArray[i+1][j];
				
				double val = Math.abs(x1-x2);
				xArray[i][j] = (int) val;
			}
		}
		return xArray;
	}
	
	//get y gradient
	private int[][] convoluteY(){
		int[][] yArray = new int[imageArray.length][imageArray[0].length];
		for(int i = 1; i < imageArray.length-1; i++){
			for(int j = 1; j < imageArray[0].length-1; j++){
				
				int y1 = imageArray[i][j-1];
				int y2 = imageArray[i][j+1];
				
				int val = Math.abs(y1-y2);
				yArray[i][j] =  val;
			}
		}
		return yArray;
	}
	
	private int[][] combine(int[][] convX, int[][] convY){
		int[][] arr = new int[convX.length][convY[0].length];
		for(int i =0; i < convX.length; i++){
			for(int j =0; i < convY[0].length; i++){
				if(convX[i][j] == 1 || convY[i][j] == 1){
					arr[i][j] = 0;  
				 } else {
					 arr[i][j] = 1; 
				 }
				
			}
		}
		return arr;
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
