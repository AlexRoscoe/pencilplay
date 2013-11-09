import java.awt.Color;
import java.awt.image.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Point;
import java.util.*;

public class BlobbingFilter 
{
	private BufferedImage givenImg = null;
	private BufferedImage newImg = null;
	
	private int imgWidth, imgHeight;
	private int desiredRGB;
	private int[][] visitedPoints;
	private List<List<Point>> blobs;
	
	// Empty constructor
	public BlobbingFilter() {}
	
	// Takes a given Image and returns the filtered Image, or null
	public BufferedImage filterImage(BufferedImage givenImg, int x, int y) {
		this.givenImg = givenImg;
		imgWidth = givenImg.getWidth();
		imgHeight = givenImg.getHeight();
		// Create new image with same dimensions as the original
		newImg = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);
		desiredRGB = givenImg.getRGB(x,y);
		// Instansiate the two lists
		visitedPoints = new int[imgWidth][imgHeight];
		blobs = new ArrayList<List<Point>>();
		// Search the img for matching color blobs
		for (int r = 0; r < imgWidth; r++) {
			for (int c = 0; c < imgHeight; c++) {
				// If the point has not been visited
				if (visitedPoints[r][c] == 0) {
					// Check if the point matches the desired color
					if (desiredRGB == givenImg.getRGB(r,c)) {
						// Create a new Blob and run method to add all associated points to it
						ArrayList<Point> blob = new ArrayList<Point>();
						grabBlob(r,c, blob);
						blobs.add(blob);
					}
					else {
						// Mark Point as visited in case it wasn't part of a blob
						visitedPoints[r][c] = 1;
					}
				}
			}
		}
		for (List<Point> blob : blobs) {
			for (Point p : blob) {
				newImg.setRGB(p.x, p.y, givenImg.getRGB(p.x, p.y));
			}
		}
		return newImg;
	}
	
	private void grabBlob(int x, int y, ArrayList<Point> blob) {
		// Return immediately if point has already been visited
		if (x < 0 || y < 0 || x >= imgWidth || y >= imgHeight || visitedPoints[x][y] == 1)
			return;
		else {
			// Mark as visited
			visitedPoints[x][y] = 1;
			if (desiredRGB == givenImg.getRGB(x,y)) {
				// Add to blob
				blob.add(new Point(x,y));
				// Check Neighbours
				for (int i = x-1; i <= x+1; i++) {
					for (int j = y-1; j <= y+1; j++) {
						grabBlob(i, j, blob);
					}
				}
			}
		}
	}
	
	public int getNumBlobs() {
		return blobs.size();
	}
	
	public String getResults() {
		if (blobs.size() == 0)
			return "No blobs have been detected";
		else {
			int counter = 0;
			String result = blobs.size() + " Blob(s) have been detected. \n";
			for (List<Point> blob : blobs) {
				counter ++;
				result += "\t Blob " + counter + " contains: " + blob.size() + " points. \n";
			}
			return result;
		}
	}
}