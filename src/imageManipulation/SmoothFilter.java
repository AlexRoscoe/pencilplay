import java.awt.image.*;
import java.awt.*;

public class SmoothFilter extends Object
{
	Dimension dimension;
	int[] raster;
	int[] mask = {1,4,6,4,1};

	SmoothFilter()
	{
		super();
	}

	public int[] setPixels(int[] pixels, Dimension dim)
	{
		int[] result0 = new int[pixels.length];
		int[] result1 = new int[pixels.length];

		for (int i=0;i<dim.height;i++)
			for (int j=0;j<dim.width;j++)
			{
				Point p = new Point(j,i);
				int cl = convoluteX(pixels,p,dim);
				result0[getCoord(p,dim)] = (new Color(cl,cl,cl)).getRGB();
			}

		for (int i=0;i<dim.width;i++)
			for (int j=0;j<dim.height;j++)
			{
				Point p = new Point(i,j);
				int cl = convoluteY(result0,p,dim);
				result1[getCoord(p,dim)] = (new Color(cl,cl,cl)).getRGB();
			}

		return result1;
	}

	private int convoluteY(int[] pix, Point p, Dimension d)
	{
		int sum = 0;

		int[] neighbourhood = new int[mask.length];
		int W = mask.length/2;

		for (int i=-W;i<=W;i++)
			neighbourhood[i+W] = findValue(pix,d,new Point(p.x,p.y+i));

		for (int i=0;i<mask.length;i++)
			sum += ((neighbourhood[i]*mask[i])/16);

		return sum;
	}

	private int convoluteX(int[] pix, Point p, Dimension d)
	{
		int sum = 0;

		int[] neighbourhood = new int[mask.length];
		int W = mask.length/2;

		for (int i=-W;i<=W;i++)
			neighbourhood[i+W] = findValue(pix,d,new Point(p.x+i,p.y));

		for (int i=0;i<mask.length;i++)
			sum += ((neighbourhood[i]*mask[i])/16);

		return sum;
	}

	private int findValue(int[] pix, Dimension imageDimension, Point trgCoord)
	{
		int imageVal = 0;
		int coordinate = -1;
		try
		{
			coordinate = getCoord(trgCoord,imageDimension);
			imageVal = pix[coordinate];
		}
		catch (ArrayIndexOutOfBoundsException ex)
		{
			return 0;
		}
		return (new Color(imageVal)).getBlue();
	}

	private int getCoord(Point p, Dimension d) throws ArrayIndexOutOfBoundsException
	{
		if ((p.x < 0) || (p.x == d.width) || (p.y < 0) || (p.y == d.height))
			throw (new ArrayIndexOutOfBoundsException());
		return (d.width*p.y+p.x);
	}
}
