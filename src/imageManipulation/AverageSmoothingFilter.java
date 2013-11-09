import java.awt.Color;
import java.awt.image.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class AverageSmoothingFilter 
{
	private BufferedImage givenImg = null;
	private BufferedImage newImg = null;
	
	// Empty Constructor
	public AverageSmoothingFilter() {}
	
	public BufferedImage filterImage(BufferedImage givenImg, int kernalSize) {
		// Read Image 
		try {
		    this.givenImg = givenImg;
			int imgWidth = givenImg.getWidth();
			int imgHeight = givenImg.getHeight();
			int offset = (kernalSize - 1) / 2;
				
			// Create new image with same dimensions as the original
			newImg = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);
			// Go through the original Image, block by block and avg pixels within and paint avg on gaussianImg
			for (int r = offset; r < imgWidth - offset; r ++) {
				for(int c = offset; c < imgHeight - offset; c ++) {
					int avgRed, avgGreen, avgBlue, RGB;
					avgRed = avgGreen = avgBlue = RGB = 0;
					for (int x = r - offset; x <= r + offset; x++) {
						for (int y = c - offset; y <= c + offset; y++) {
							Color pixel = new Color(givenImg.getRGB(x, y));
							avgRed += pixel.getRed();
							avgGreen += pixel.getGreen();
							avgBlue += pixel.getBlue();
						}
					}
					int square = kernalSize*kernalSize;
					Color avgColor = new Color(avgRed/square, avgGreen/square, avgBlue/square);
					RGB = avgColor.getRGB();
					newImg.setRGB(r,c,RGB);
				}
			}
			// Write Image to File
			ImageIO.write(newImg, "png", new File("AverageSmoothedImage.png"));
		} 
		catch (IOException e) {
			System.out.println("Error Reading file");
		}
		return newImg;
	}
	
	// Calculates the combination(a, b)
	private int C(int a, int b) {
		int result = 0;
		result = f(a) / (f(b) * f(a-b));
		
		return result;
	}
	
	// Calculates the factorial x!
	private int f(int x) {
		if (x == 1 || x == 0) {
			return 1;
		}
		else {
			return x * f((x-1));
		}
	}
}