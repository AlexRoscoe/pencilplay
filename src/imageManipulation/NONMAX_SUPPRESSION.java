import java.awt.*;
import java.util.*;

public class NONMAX_SUPPRESSION extends Object
{
	double[][] orientation;
	double[][][] gradient;

	NONMAX_SUPPRESSION(double[][] o, double[][] image, Dimension dim)
	{
		super();

		orientation = o;
		gradient = computeGradient(image,dim);
	}

	private double[][][] computeGradient(double[][] image, Dimension dim)
	{
		double[][][] result = new double[dim.height][dim.width][2];
		for (int i=0;i<dim.height;i++)
			for (int j=0;j<dim.height;j++)
			{
				result[i][j][0] = convoluteX(image,new Point(i,j));
				result[i][j][1] = convoluteY(image,new Point(i,j));
			}
		return result;
	}

	private double convoluteY(double[][] pix, Point p)
	{
		double valm1 = findValue(pix, new Point(p.x,p.y-1));
		double valp1 = (-1) * findValue(pix, new Point(p.x,p.y+1));
		double val = valp1 + valm1;

		return val;
	}

	private double convoluteX(double[][] pix, Point p)
	{
		double valm1 = findValue(pix, new Point(p.x-1,p.y));
		double valp1 = (-1) * findValue(pix, new Point(p.x+1,p.y));
		double val = valp1 + valm1;

		return val;
	}

	public Vector setPixels(Enumeration enum)
	{
		Vector result = new Vector();
		while (enum.hasMoreElements())
		{
			Point p = (Point) enum.nextElement();
			//System.out.println("pixel : "+p);

			double direction = orientation[p.x][p.y] / 255.0;
			//System.out.println("dir : "+direction);
			double ndirection = direction + 0.5;
			if (ndirection > 1)
				ndirection = ndirection-1;
			//System.out.println("ndir : "+ndirection);

			double ux = gradient[p.x][p.y][0];
			double uy = gradient[p.x][p.y][1];
			double gc = hypot(ux,uy);
			double t = gc * 20.0;

			//derivative_mag[p.x][p.y] = (t<256 ? t : 255);
			double gn = hypot(gradient[p.x][p.y-1][0],gradient[p.x][p.y-1][1]);
			double gs = hypot(gradient[p.x][p.y+1][0],gradient[p.x][p.y+1][1]);
			double gw = hypot(gradient[p.x-1][p.y][0],gradient[p.x-1][p.y][1]);
			double ge = hypot(gradient[p.x+1][p.y][0],gradient[p.x+1][p.y][1]);
			double gne = hypot(gradient[p.x+1][p.y-1][0],gradient[p.x+1][p.y-1][1]);
			double gse = hypot(gradient[p.x+1][p.y+1][0],gradient[p.x+1][p.y+1][1]);
			double gsw = hypot(gradient[p.x-1][p.y+1][0],gradient[p.x-1][p.y+1][1]);
			double gnw = hypot(gradient[p.x-1][p.y-1][0],gradient[p.x-1][p.y-1][1]);

			if (ux*uy>0)
			{
				if(Math.abs(ux)<Math.abs(uy))
				{
					double g0 = Math.abs(uy*gc);
					if(g0 < Math.abs(ux*gse+(uy-ux)*gs) ||
						g0<=Math.abs(ux*gnw+(uy-ux)*gn))
						continue;
				}
				else
				{
					double g0 = Math.abs(ux*gc);
					if(g0 < Math.abs(uy*gse+(ux-uy)*ge) ||
						g0<=Math.abs(uy*gnw+(ux-uy)*gw))
						continue;
				}
			}
			else
			{
				if(Math.abs(ux)<Math.abs(uy))
				{
					double g0 = Math.abs(uy*gc);
					if(g0 < Math.abs(ux*gne-(uy+ux)*gn) ||
						g0<=Math.abs(ux*gsw-(uy+ux)*gs))
						continue;
				}
				else
				{
					double g0 = Math.abs(ux*gc);
					if(g0 < Math.abs(uy*gne-(ux+uy)*ge) ||
						g0<=Math.abs(uy*gsw-(ux+uy)*gw))
						continue;
				}
			}
			result.addElement(p);
		}

		return result;
	}

	private double hypot(double x, double y)
	{
		return Math.sqrt(x*x + y*y);
	}

	private double findValue(double[][] grad, Point coord)
	{
		try
		{
			return grad[coord.x][coord.y];
		}
		catch (ArrayIndexOutOfBoundsException ex)
		{
			return 0;
		}
	}
}
