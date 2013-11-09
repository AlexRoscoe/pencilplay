import java.awt.*;
import java.awt.image.*;

public class Gradient extends Object 
{
	private doublePoint[][] grad = null;
	//private double[][] grad = null;
	private double[][] norm = null;

	double meanMagnitude = 0;

	Gradient()
	{
		super();
	}

	public doublePoint[][] setPixels(Dimension dim, int pixels[])
	{
		doublePoint[][] result = new doublePoint[dim.height][dim.width];

                for (int i=0;i<dim.height;i++)
                        for (int j=0;j<dim.width;j++)
                        {
				Point p = new Point(i,j);
				double cnvX = convoluteX(pixels,p,dim);
				double cnvY = convoluteY(pixels,p,dim);
				result[i][j] = new doublePoint(cnvX,cnvY);
			}

		grad = result;

		return result;
	}

	public double[][] getNorm()
	{
		double sum = 0;
		norm = new double[grad.length][grad[0].length];
		for (int i=0;i<grad.length;i++)
			for (int j=0;j<grad[i].length;j++)
			{
				double n =  Math.sqrt(Math.pow(grad[i][j].x,2) + Math.pow(grad[i][j].y,2));
				norm[i][j] = n;
				sum += n;
			}

		meanMagnitude = sum/((double) (grad.length*grad[0].length));
		return norm;
	}



	private long coords2index(Point p, Dimension d)
	{
		return p.y*d.height + p.x;
	}

	

	private double findValue(int[] pix, Dimension imageDimension, Point coord)
	{
		int imageVal = 0;
		int coordinate = -1;
		try
		{
			coordinate = getCoord(coord,imageDimension);
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
