package server;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class TestImage {

	public static void main(String[] args) throws IOException{
		File file = new File("C:/Dev/pencilplay/testData/images/Image.jpg");
		BufferedImage bI = ImageIO.read(file);
		ImageProcessor ip = new ImageProcessor();
		ip.processImage(bI);
		System.out.print("test"); 
	}
	
}
