import java.awt.Color;
import java.awt.image.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class PixelizingFilter 
{
	private BufferedImage givenImg = null;
	private BufferedImage newImg = null;
	
	// Empty Constructor
	public PixelizingFilter() {}
	
	// Takes a given Image and returns the filtered Image, or null
	public BufferedImage filterImage(BufferedImage givenImg, int reductionFactor) {
		try {
		    this.givenImg = givenImg;
			int imgWidth = givenImg.getWidth();
			int imgHeight = givenImg.getHeight();
			// Create new image with same dimensions as the original
			newImg = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);
			// Go through the original Image, block by block and avg pixels within and paint avg on newImg
			int remainderX = imgWidth % reductionFactor;
			int remainderY = imgHeight % reductionFactor;
			// Process img
			pixelizeImg(0, 0, imgWidth-reductionFactor, imgHeight-reductionFactor, reductionFactor, reductionFactor);
			// If there is remaining space on the right side of the img, process the incomplete rects
			if (remainderX != 0)
				pixelizeImg(imgWidth-remainderX, 0, imgWidth-1, imgHeight-reductionFactor, remainderX, reductionFactor);
			// If there is remaining space on the bottom of the img, process the incomplete rects
			if (remainderY != 0)
				pixelizeImg(0, imgHeight-remainderY, imgWidth-reductionFactor, imgHeight-1, reductionFactor, remainderY);
			// If there was both remaining space on the right and on the bottom, process the remaining square(bottom-right)
			if (remainderX != 0 && remainderY != 0)	
				pixelizeImg(imgWidth-remainderX, imgHeight-remainderY, imgWidth-1, imgHeight-1, remainderX, remainderY);
			
			// Write Image to File
			ImageIO.write(newImg, "png", new File("PixelatedImage.png"));
		} 
		catch (IOException e) {
			System.out.println("Error Reading file");
		}
		return newImg;
	}
	
	// Processes the given dimensions of the image in the given block size
	private void pixelizeImg(int startX, int startY, int endX, int endY, int sizeX, int sizeY) {
		for (int r = startX; r <= endX; r += sizeX) {
			for(int c = startY; c <= endY; c += sizeY) {
				double red, blue, green; 
				red = blue = green = 0.0;
				int RGB = 0;
				for (int x = r; x < r + sizeX; x++) {
					for (int y = c; y < c + sizeY; y++) {
						Color pixel = new Color(givenImg.getRGB(x, y));
						red += pixel.getRed();
						green += pixel.getGreen();
						blue += pixel.getBlue();
					}
				}
				Color avgColor = new Color((int)(red/(sizeX * sizeY)), (int)(green/(sizeX * sizeY)), (int)(blue/(sizeX * sizeY)));
				RGB = avgColor.getRGB();
				for (int x = r; x < r + sizeX; x++) {
					for (int y = c; y < c + sizeY; y++) {
						newImg.setRGB(x,y,RGB);
					}
				}
			}
		}
	}
}