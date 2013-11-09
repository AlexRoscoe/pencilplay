import java.util.*;
import java.awt.*;

public class ConnectedComponent extends Object
{
	public static int SURF_SIZE_THRESH = 5;
	public static int BORDER = 0;

	private int[] image;
	private boolean[] labels;
	private int rows,columns;

	ConnectedComponent(int rows, int columns, int[] image)
	{
		super();

		this.rows = rows;
		this.columns = columns;
		this.image = image;

		labels = new boolean[rows*columns];
		for (int i=0;i<labels.length;i++)
			labels[i] = false;
	}

	public Vector getBlobs()
	{
		Vector surfs = new Vector();

		for (int j=BORDER;j<rows-BORDER;j++)
		{
			for (int i=BORDER;i<columns-BORDER;i++)
			{
				boolean past = labels[getIndex(j,i)];
				labels[getIndex(j,i)] = true;
				if (!past)
				{
					Blob surf = get3DSurface(new Point(j,i));
					if (surf.points.size() > SURF_SIZE_THRESH)
					{
						surfs.addElement(surf);
					}
				}
			}
		}

		return surfs;
	}

	private Vector addPixels(Point p, Vector intermediateResult)
	{
		intermediateResult.addElement(p);
		labels[getIndex(p.x,p.y)] = true;

		for (int j=-1;j<=1;j++)
		{
			for (int i=-1;i<=1;i++)
			{
				Point tp = new Point(p.x+j,p.y+i);
				if (!((i==0) && (j==0)))
				{
					if (pointIsSuitable(p.x+j,p.y+i))
					{
						intermediateResult = addPixels(tp,intermediateResult);
					}
				}
			}
		}

		return intermediateResult;
	}

	private boolean pointIsSuitable(int x, int y)
	{
		if ((x < BORDER) || (x >= rows-BORDER) ||
			(y < BORDER) || (y >= columns-BORDER))
				return false;
		if (labels[getIndex(x,y)] == true)
			return false;
		if (image[getIndex(x,y)] != Color.white.getRGB())
			return false;
		return true;
	}

	private double distance(Point p0, Point p1)
	{
		return (Math.sqrt(Math.pow(p0.x-p1.x,2) + Math.pow(p0.y-p1.y,2)));
	}

	private int getIndex(int x, int y)
	{
		return x*columns+y;
	}

	private Blob get3DSurface(Point seed)
	{
		Vector surfPixels = new Vector();

		try
		{
			surfPixels = addPixels(seed,surfPixels);
		}
		catch (StackOverflowError stErr)
		{
			System.out.println("Error: Out of Memory !!! "+stErr);
		}

		return (new Blob(surfPixels));
	}
}
