import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import java.io.*;

public class PointCanvas extends Canvas implements ImageObserver, MouseListener
{
	Vector lines;
	Dimension dim;
	Image image = null;

	PointCanvas(Dimension dimension)
	{
		super();
		lines = new Vector();
		dim = dimension;
		setSize(dimension);
		addMouseListener(this);
		setBackground(Color.black);
	}

	public void setImage(MemoryImageSource is)
	{
		image = createImage(is);
	}

	public void setLines(Vector v)
	{
		lines = v;
		repaint();
	}

	public void paint(Graphics g)
	{
		if (image != null)
                	g.drawImage(image,0,0,dim.width,dim.height,this);
		Enumeration enuml = lines.elements();
			
		while (enuml.hasMoreElements())
		{
			//g.setColor(Color.red);
			g.setColor(new Color((int) (255*Math.random()),
				(int) (255*Math.random()), (int) (255*Math.random())));

			Vector ls = (Vector) enuml.nextElement();
			for (int i=0;i<ls.size();i++)
			{
				Point p = (Point) ls.elementAt(i);
				g.drawLine(p.x,p.y,p.x,p.y);
			}
		}
        }

	public void mouseClicked(MouseEvent e)
	{
		System.out.println(e.getX()+" "+e.getY());
	}

	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
}
