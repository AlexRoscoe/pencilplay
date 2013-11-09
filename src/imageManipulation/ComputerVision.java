import java.util.Scanner;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.awt.image.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/**
 * @author: Alex Roscoe
 * GUI class that allows the user to use and play with the different 
 * filters and image manipulation tasks that were part of Assignment 1 
 * in computer vision. 
**/
public class ComputerVision implements ActionListener, MouseListener
{
	private JFrame mainFrame;
	private JPanel imageSelectionPanel, imageFilteringPanel;
	private JLabel selectedImageLabel, orgImgLabel, filteredImageLabel;
	private JButton chooseImageButton, pixelizeImageButton, avgSmoothImageButton, 
					gaussianSmoothImageButton, magicWandButton;
	private JTextField kernelTextField;
	// Images
	private BufferedImage originalImage, filteredImage;	
	// Filters
	private PixelizingFilter pixelize;
	private AverageSmoothingFilter avgSmooth;
	private GaussianSmoothingFilter gaussianSmooth;
	private BlobbingFilter blobbing;
	
	private int IMG_WIDTH;
	private int IMG_HEIGHT;
	private boolean wandMode;
	
	// Constructor 
	public ComputerVision () { init(); }
	
	// Initializes all the GUI components and the associated filter classes.	
	private void init() {
		// Create the main Frame for the program.
		mainFrame = new JFrame("Image Manipulator");
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		IMG_WIDTH = (dim.width - 50) / 2;
		IMG_HEIGHT = IMG_WIDTH;
		int width = dim.width;
		int height = 200 + IMG_WIDTH;
		mainFrame.setSize(width, height);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
		// Get the size of the screen, determine the center, and reposition the JFrame
		
		int x = (dim.width-width)/2;
		int y = 0;
		mainFrame.setLocation(x,y);
	
		GridBagConstraints gc = new GridBagConstraints();
	    gc.fill = GridBagConstraints.BOTH;
		mainFrame.setLayout(new GridBagLayout());
		
		// Create the JPanel for selecting the image to be manipulated
		imageSelectionPanel = new JPanel();
		selectedImageLabel = new JLabel("Select an image to be manipulated... ");
		chooseImageButton = new JButton("Choose Image");
			chooseImageButton.addActionListener(this);
		imageSelectionPanel.add(selectedImageLabel);
		imageSelectionPanel.add(chooseImageButton);
		gc.gridx = 0;
		gc.gridy = 0;
		gc.gridwidth = 2;	
		mainFrame.add(imageSelectionPanel, gc);
		
		imageFilteringPanel = new JPanel();
		JLabel kernelText = new JLabel("Kernel Size: ");
		imageFilteringPanel.add(kernelText);
		kernelTextField = new JTextField("3");
		kernelTextField.setColumns(3);
		imageFilteringPanel.add(kernelTextField);		
		pixelizeImageButton = new JButton("Pixelize Image");
		pixelizeImageButton.addActionListener(this);
		imageFilteringPanel.add(pixelizeImageButton);
		avgSmoothImageButton = new JButton("Smooth Image (Average)");
		avgSmoothImageButton.addActionListener(this);
		imageFilteringPanel.add(avgSmoothImageButton);		
		gaussianSmoothImageButton = new JButton("Smooth Image (Gaussian)");
		gaussianSmoothImageButton.addActionListener(this);
		imageFilteringPanel.add(gaussianSmoothImageButton);
		magicWandButton = new JButton("Start Magic Wand..");
		magicWandButton.addActionListener(this);
		imageFilteringPanel.add(magicWandButton);
		kernelTextField.setEnabled(false);
		pixelizeImageButton.setEnabled(false);
		avgSmoothImageButton.setEnabled(false);
		gaussianSmoothImageButton.setEnabled(false);
		magicWandButton.setEnabled(true);
		gc.gridx = 0;
		gc.gridy = 1;
		gc.gridwidth = 2;	
		mainFrame.add(imageFilteringPanel, gc);
		
		// Create the area for displaying the selected, original, image
		JLabel orgImgText = new JLabel("Original Image: ");
		orgImgLabel = new JLabel();
		orgImgLabel.setPreferredSize(new Dimension(IMG_WIDTH, IMG_HEIGHT));
		orgImgLabel.setBorder(BorderFactory.createLineBorder(Color.black, 1));
		orgImgLabel.addMouseListener(this);
		gc.gridx = 0;
		gc.gridy = 2;
		gc.gridwidth = 1;	
		mainFrame.add(orgImgText, gc);
		gc.gridx = 0;
		gc.gridy = 3;
		gc.gridwidth = 1;	
		mainFrame.add(orgImgLabel, gc);
		
		// Create the area for displaying the filtered version of the original image
		JLabel filteredImgText = new JLabel("Filtered Image: ");
		filteredImageLabel = new JLabel();
		filteredImageLabel.setPreferredSize(new Dimension(IMG_WIDTH, IMG_HEIGHT));
		filteredImageLabel.setBorder(BorderFactory.createLineBorder(Color.blue, 1));
		gc.gridx = 1;
		gc.gridy = 2;
		gc.gridwidth = 1;	
		mainFrame.add(filteredImgText, gc);
		gc.gridx = 1;
		gc.gridy = 3;
		gc.gridwidth = 1;	
		mainFrame.add(filteredImageLabel, gc);
		mainFrame.setVisible(true);
		
		// Create the filter objects to be used by the program.
	    pixelize = new PixelizingFilter();
		avgSmooth = new AverageSmoothingFilter();
		gaussianSmooth = new GaussianSmoothingFilter();
		blobbing = new BlobbingFilter();
		
		// Start the program outside wand mode
		wandMode = false;
	}
	
	/**
	 * Method to respond to button clicks.  
	**/
	public void actionPerformed(ActionEvent e) {
		// Brings up the fileChooser and loads the user selected image.
		if (e.getSource() == chooseImageButton) {
			final JFileChooser fc = new JFileChooser();
			int returnVal = fc.showOpenDialog(mainFrame);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				selectedImageLabel.setText("Selected Image:  " + file.getName());
				try {
					originalImage = ImageIO.read(file);
					int orgHeight = originalImage.getHeight();
					int orgWidth = originalImage.getWidth();
					// Check if the image to be displayed is too large, and if so scale it down.
					if (orgHeight > IMG_HEIGHT || orgWidth > IMG_WIDTH) {
						double scalingFactor = 1.0;
						// If the image is taller than it is wide
						if (orgHeight > orgWidth) 
							scalingFactor = orgHeight / (double)IMG_HEIGHT;
						// Else the image is wider than it is tall
						else
							scalingFactor = orgWidth / (double)IMG_WIDTH;
						int newWidth = (int)(orgWidth/scalingFactor);
						int newHeight = (int)(orgHeight/scalingFactor);
						BufferedImage tempImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
						Graphics2D g = tempImg.createGraphics();  
				        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);  
				        g.drawImage(originalImage, 0, 0, newWidth, newHeight, 0, 0, orgWidth, orgHeight, null);  
				        g.dispose();
						originalImage = tempImg;
					}
					orgImgLabel.setIcon(new ImageIcon(originalImage));
					orgImgLabel.setHorizontalAlignment(JLabel.CENTER);
					kernelTextField.setEnabled(true);					
					pixelizeImageButton.setEnabled(true);
					avgSmoothImageButton.setEnabled(true);
					gaussianSmoothImageButton.setEnabled(true);
				}
				catch (IOException exception) {
					System.out.println("Error Reading file: " + file.getName());
				}
			}
		}
		// Pixelizes the loaded image and displays the filtered image.
		else if (e.getSource() == pixelizeImageButton) {
			int kernelSize = readKernelSize();
			filteredImage = pixelize.filterImage(originalImage, kernelSize);
			filteredImageLabel.setIcon(new ImageIcon(filteredImage));
			filteredImageLabel.setHorizontalAlignment(JLabel.CENTER);
		}
		// Smoothes (Average) the loaded image and displays the filtered image.
		else if (e.getSource() == avgSmoothImageButton) {
			int kernelSize = readKernelSize();
			filteredImage = avgSmooth.filterImage(originalImage, kernelSize);
			filteredImageLabel.setIcon(new ImageIcon(filteredImage));
			filteredImageLabel.setHorizontalAlignment(JLabel.CENTER);
		}
		// Smoothes (Gaussian) the loaded image and displays the filtered image.
		else if (e.getSource() == gaussianSmoothImageButton) {
			int kernelSize = readKernelSize();
			filteredImage = gaussianSmooth.filterImage(originalImage, kernelSize);
			filteredImageLabel.setIcon(new ImageIcon(filteredImage));
			filteredImageLabel.setHorizontalAlignment(JLabel.CENTER);
		}
		// Turns on the "Magic Wand" and allows the user to click on the loaded image
		// And learn the amount of 'blobs' matching that color and their size. 
		// Also displays a new image only containing those blobs.
		else if (e.getSource() == magicWandButton) {	
			// If deactivating wandMode
			if (wandMode) {
				// Turn off wandMode 
				wandMode = false;
				kernelTextField.setEnabled(true);
				pixelizeImageButton.setEnabled(true);
				avgSmoothImageButton.setEnabled(true);
				gaussianSmoothImageButton.setEnabled(true);
				magicWandButton.setText("Start Magic Wand..");
			}
			// else activating wandmode
			else {
				// Turn on wandMode
				wandMode = true;
				// Deactivate all other buttons/textfields
				kernelTextField.setEnabled(false);
				pixelizeImageButton.setEnabled(false);
				avgSmoothImageButton.setEnabled(false);
				gaussianSmoothImageButton.setEnabled(false);
				magicWandButton.setText("DeActivate Wand Mode..");				
				
				// Load the image used for magic wand: 
				try {
					originalImage = ImageIO.read(new File("color.png"));
					orgImgLabel.setIcon(new ImageIcon(originalImage));
					orgImgLabel.setHorizontalAlignment(JLabel.CENTER);
				}
				catch (IOException ioe) {}
			}
		}
	}
	
	// Reads the user-desired kernel size.
	private int readKernelSize() {
		int kernelSize = 0;
		try {
			kernelSize = Integer.parseInt(kernelTextField.getText());
		}
		catch (Exception e) {
			// TODO: Add a pop up to get actual number here.
		}
		return kernelSize;
	}
	
	// Handles the mouse click for the 'magic wand' tool.
	public void mouseClicked(MouseEvent e) {
		// Check to see if the user is in wandMode
		if (wandMode) {
			// Translate Mouse coordinates from Label's frame to the Image's.
			int x = e.getX() - ((orgImgLabel.getWidth()/2) - (originalImage.getWidth()/2));
			int y = e.getY() - ((orgImgLabel.getHeight()/2) - (originalImage.getHeight()/2));
			filteredImage = blobbing.filterImage(originalImage, x, y);
			filteredImageLabel.setIcon(new ImageIcon(filteredImage));
			filteredImageLabel.setHorizontalAlignment(JLabel.CENTER);
			JOptionPane.showMessageDialog(null, blobbing.getResults(), "Blobs Detected!", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

	// Main method to start the GUI and leave it running.
	public static void main(String[] args) {
		ComputerVision assignment = new ComputerVision();
	}
}