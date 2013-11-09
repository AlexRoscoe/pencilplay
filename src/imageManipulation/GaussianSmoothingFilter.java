import java.awt.Color;
import java.awt.image.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class GaussianSmoothingFilter 
{
	private BufferedImage givenImg = null;
	private BufferedImage newImg = null;
	
	// Empty Constructor
	public GaussianSmoothingFilter() {}
	
	public BufferedImage filterImage(BufferedImage givenImg, int kernelSize) {
		// Read Image 
		try {
		    this.givenImg = givenImg;
			int imgWidth = givenImg.getWidth();
			int imgHeight = givenImg.getHeight();
			int[] oneDKernel = new int[kernelSize];
			for (int i=0; i<kernelSize; i++) {
				oneDKernel[i] = C(kernelSize-1, i);
			}
			int kernelSum = 0;
			int[][] gaussianKernel = new int[kernelSize][kernelSize];
			for (int i=0; i<kernelSize; i++) {
				for (int j=0; j<kernelSize; j++) {
					gaussianKernel[i][j] = oneDKernel[i]*oneDKernel[j];
					kernelSum += gaussianKernel[i][j];
				}
			}
			int offset = (kernelSize - 1) / 2;
			System.out.println("KernelSum : " + kernelSum);
				
			// Create new image with same dimensions as the original
			newImg = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);
			// Go through the original Image, block by block and avg pixels within and paint avg on gaussianImg
			for (int r = offset; r < imgWidth - offset; r ++) {
				for(int c = offset; c < imgHeight - offset; c ++) {
					int gaussianRed, gaussianGreen, gaussianBlue, RGB, a, b;
					gaussianRed = gaussianGreen = gaussianBlue = RGB = a = b = 0;
					for (int x = r - offset; x <= r + offset; x++) {
						for (int y = c - offset; y <= c + offset; y++) {
							Color pixel = new Color(givenImg.getRGB(x, y));
							gaussianRed += pixel.getRed() * gaussianKernel[a][b];
							gaussianGreen += pixel.getGreen() * gaussianKernel[a][b];
							gaussianBlue += pixel.getBlue() * gaussianKernel[a][b];
							b = (b + 1) % kernelSize;
						}
						a++;
					}
					Color gaussianColor = new Color(gaussianRed/kernelSum, gaussianGreen/kernelSum, gaussianBlue/kernelSum);
					RGB = gaussianColor.getRGB();
					newImg.setRGB(r,c,RGB);
				}
			}
			// Write Image to File
			ImageIO.write(newImg, "png", new File("GaussianSmoothedImage.png"));
		} 
		catch (IOException e) {
			System.out.println("Error Reading file");
		}
		return newImg;
	}
	
	// Calculates the combination(a, b)
	private int C(int a, int b) { return f(a) / (f(b) * f(a-b)); }
	
	// Calculates the factorial x!
	private int f(int x) {
		if (x == 1 || x == 0)
			return 1;
		else
			return x * f((x-1));
	}
}