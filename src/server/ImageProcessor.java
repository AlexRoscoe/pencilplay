package server;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ImageProcessor {
	
	private BufferedImage anImage;
	private JLabel ImageLabel = new JLabel();
	private int imageWidth, imageHeight;
	
	public ImageProcessor(){}
	
	public void processImage(BufferedImage img) {
		this.anImage = img;
		// Convert to grayScale image
		doStuff(); 
		// Get x gradient
		
		// Get y gradient
		
		// combine
		
		// clean up (average into one line)
		
		// Edge creep to find floor
		
		// remove floor
		
		// find player
	}
	
	public void doStuff(){
		
		JFrame jf = new JFrame();
		JPanel jp = new JPanel();
		//Container pane = jf.getContentPane();
		
		File file = new File("C:/Dev/pencilplay/testData/images/Image.jpg");
		try {
			anImage = ImageIO.read(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		BufferedImage gray = new BufferedImage(anImage.getWidth(),anImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		ColorConvertOp op = new ColorConvertOp(anImage.getColorModel().getColorSpace(), gray.getColorModel().getColorSpace(), null);
		
		anImage = op.filter(anImage, gray);
		Graphics2D graphics = anImage.createGraphics();	
		graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics.drawImage(anImage, 0, 0, anImage.getWidth(), anImage.getHeight(), null);
		graphics.dispose();
		
		ImageLabel.setIcon(new ImageIcon(anImage));
		ImageLabel.setHorizontalAlignment(JLabel.CENTER);
		jf.setLayout(new BorderLayout());
		jf.add(jp, BorderLayout.SOUTH);
		jp.add(ImageLabel);
		jf.pack();
		jf.setVisible(true);
		
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
