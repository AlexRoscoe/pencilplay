package server;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ImageProcessor {
	
	BufferedImage anImage;
	JLabel ImageLabel = new JLabel();
	
	public ImageProcessor(){
		doStuff();
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
}
