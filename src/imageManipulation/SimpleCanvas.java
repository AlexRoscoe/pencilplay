import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

public class SimpleCanvas extends Canvas 
{
	Vector surfaces;

	SimpleCanvas(Dimension dimension)
	{
		super();
		surfaces = new Vector();
		setSize(dimension);
	}

	public void setSurfaces(Vector v)
	{
		surfaces = v;
		repaint();
	}

	public void paint(Graphics g)
	{
		//Enumeration enum = surfaces.elements();
		//while (enum.hasMoreElements())
		for (int i=surfaces.size()-1;i>=0;i--)
		{
			//Surface3D surf = (Surface3D) enum.nextElement();
			Surface3D surf = (Surface3D) surfaces.elementAt(i);
			g.setColor(new Color((int) (255*Math.random()),(int) (255*Math.random()),(int) (255*Math.random())));
			//g.drawPolygon(surf.getOutline());
			Polygon poly = surf.getOutline();
			//for (int j=0;j<poly.npoints;j++)
			Enumeration enum2 = surf.points();
			while (enum2.hasMoreElements())
			{
				TDPoint tdp = (TDPoint) enum2.nextElement();
				//int x = poly.xpoints[j];
				//int y = poly.ypoints[j];
				g.drawLine(2*tdp.y,2*tdp.x,2*tdp.y+1,2*tdp.x+1);
				//g.drawLine(2*x,2*y,2*x+1,2*y+1);
			}
		}
	}
}
