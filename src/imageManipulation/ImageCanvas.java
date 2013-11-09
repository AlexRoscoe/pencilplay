import java.io.*;
import java.awt.*;
import java.util.*;
import java.awt.image.*;

public class ImageCanvas extends Canvas implements ImageObserver
{
	Image image;
	Vector tokens;
	Dimension dimension;
	ImagePanel parent;
	Group group = null;

	ImageCanvas()
	{
		this(null,null,new Dimension(256,256));
	}

	ImageCanvas(ImagePanel p)
	{
		this(p,null,new Dimension(256,256));
	}

	ImageCanvas(MemoryImageSource is, Dimension dim)
	{
		this(null,is,dim);
	}

	ImageCanvas(ImagePanel p, MemoryImageSource is, Dimension dim)
	{
		super();

		parent = p;
		image = createImage(is);
		dimension = dim;
		tokens = new Vector();

		setSize(dimension);
	}

	public void setGroup(Group g)
	{
		group = g;
		repaint();
	}

	public void setParent(ImagePanel p)
	{
		parent = p;
	}

	public void setTokens(Vector v)
	{
		tokens = v;
		repaint();
	}

	public Dimension getPreferredSize()
	{
		return dimension;
	}

	public void paint(Graphics g)
	{
		int dx = 0;
		int dy = 0;
		if (parent != null)
		{
			dx = parent.xscroll.getValue();
			dy = parent.yscroll.getValue();
		}

		if (image != null)
			g.drawImage(image,-dx,-dy,this);

		if (group == null)
		{
			Enumeration enum = tokens.elements();
			while (enum.hasMoreElements())
			{
				Object o = enum.nextElement();
				if (o instanceof LineSegment)
				{
					LineSegment l = (LineSegment) o;

					g.setColor(Color.red);
					if (l.isDominant)
						g.setColor(Color.magenta.darker());
					g.drawLine(l.x1-dx,l.y1-dy,l.x2-dx,l.y2-dy);
					g.fillOval(l.x1-1-dx,l.y1-1-dy,3,3);
					g.fillOval(l.x2-1-dx,l.y2-1-dy,3,3);
				}
			}
		}
		else
		{
			drawGroup(g,group,dx,dy);
		}
	}

	private void drawGroup(Graphics g, Group group, int dx, int dy)
	{
			Group pg = group;
			Vector v = pg.tokens;

			doublePoint np = pg.orientationPoint();
			//if (np!=null)
				//GraphicsUtil.drawLine(g,(int) np.x-dx,(int) np.y-dy,(int) pg.vanishingPoint.x-dy,(int) pg.vanishingPoint.y-dy,7,Color.green);
			//else
			//{
				//g.setColor(Color.green);
				//Dimension d = getSize();
				//g.drawString("Vanishing point resides at infinity.",d.width-195,d.height-4);
			//}

			//g.setXORMode(Color.black);
			Enumeration enum = v.elements();
			while (enum.hasMoreElements())
			{
				LineSegment ls = (LineSegment) enum.nextElement();
				GraphicsUtil.drawLine(g,ls.x1-dx,ls.y1-dy,ls.x2-dx,ls.y2-dy,5,Color.white);
			}
			//g.setPaintMode();

			//if (pg.prototype0 != null)
				//GraphicsUtil.drawLine(g,pg.prototype0.x1-dx,pg.prototype0.y1-dy,pg.prototype0.x2-dx,pg.prototype0.y2-dy,5,Color.pink);
			//if (pg.prototype1 != null)
				//GraphicsUtil.drawLine(g,pg.prototype1.x1-dx,pg.prototype1.y1-dy,pg.prototype1.x2-dx,pg.prototype1.y2-dy,5,Color.pink);
	}
}
