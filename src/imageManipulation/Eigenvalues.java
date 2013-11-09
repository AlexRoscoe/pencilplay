public class Eigenvalues extends Object
{
	doublePoint[][] gradient;
	int complexity;
	doublePoint[][] eigenvalues;
	doublePoint[][] dominantEigenvectors;
	double[][] dominantEigenvalues;

	Eigenvalues(doublePoint[][] g, int c)
	{
		super();

		gradient = g;
		complexity = c;
		computeEigenvalues();
	}

	private void computeEigenvalues()
	{
		eigenvalues = new doublePoint[gradient.length][gradient[0].length];
		dominantEigenvectors = new doublePoint[gradient.length][gradient[0].length];
		dominantEigenvalues = new double[gradient.length][gradient[0].length];
		for (int i=complexity;i<gradient.length-complexity;i++)
		{
			for (int j=complexity;j<gradient[0].length-complexity;j++)
			{
				doublePoint[][] m = getNeighbourhood(gradient,i,j,complexity);
				doublePoint ssums = ssum(m,complexity);
				double msum = msum(m,complexity);

				double sqrtD = Math.sqrt(Math.pow(ssums.x-ssums.y,2) + 4*(Math.pow(msum,2)));
				double base = ssums.x + ssums.y;
				eigenvalues[i][j] = new doublePoint((base+sqrtD)/2,(base-sqrtD)/2);
				double k1 = Math.max(eigenvalues[i][j].x,eigenvalues[i][j].y);
				dominantEigenvectors[i][j] = getEigenVector(k1,ssums,msum);
				dominantEigenvalues[i][j] = k1;
			}
			System.out.println(i);
		}
	}

	public static doublePoint getEigenVector(double l, doublePoint ssum, double msum)
	{
		double x = msum / (l-ssum.x);
		double y = msum * x / (l-ssum.y);
		return (new doublePoint(x,y));
	}

	public static doublePoint sum(doublePoint[][] m, int complexity)
	{
		doublePoint sum = new doublePoint(0,0);

		for (int i=0;i<m.length;i++)
		{
			for (int j=0;j<complexity;j++)
			{
				doublePoint dv = new doublePoint(m[i][j].x,m[i][j].y);
				sum.x += dv.x;
				sum.y += dv.y;
			}
		}
		
		return sum;
	}

	public static doublePoint ssum(doublePoint[][] m, boolean[][] map, int complexity)
	{
		doublePoint sum = new doublePoint(0,0);

		for (int i=0;i<complexity;i++)
		{
			for (int j=0;j<complexity;j++)
			{
				if (map[i][j])
				{
					doublePoint dv = new doublePoint(m[i][j].x,m[i][j].y);
					sum.x = sum.x + (dv.x*dv.x);
					sum.y = sum.y + (dv.y*dv.y);
				}
			}
		}
		
		return sum;
	}

	public static doublePoint ssum(doublePoint[][] m, int complexity)
	{
		doublePoint sum = new doublePoint(0,0);

		for (int i=0;i<complexity;i++)
		{
			for (int j=0;j<complexity;j++)
			{
				doublePoint dv = new doublePoint(m[i][j].x,m[i][j].y);
				sum.x = sum.x + (dv.x*dv.x);
				sum.y = sum.y + (dv.y*dv.y);
			}
		}
		
		return sum;
	}

	public static double msum(doublePoint[][] m, boolean[][] map, int complexity)
	{
		double sum = 0;

		for (int i=0;i<complexity;i++)
		{
			for (int j=0;j<complexity;j++)
			{
				if (map[i][j])
				{
					doublePoint dv = new doublePoint(m[i][j].x,m[i][j].y);
					sum = sum + (dv.x*dv.y);
				}
			}
		}
		
		return sum;
	}

	public static double msum(doublePoint[][] m, int complexity)
	{
		double sum = 0;

		for (int i=0;i<complexity;i++)
		{
			for (int j=0;j<complexity;j++)
			{
				doublePoint dv = new doublePoint(m[i][j].x,m[i][j].y);
				sum = sum + (dv.x*dv.y);
			}
		}
		
		return sum;
	}

	public static boolean[][] getNeighbourhood(boolean[][] m, int k, int l, int complexity)
	{
		boolean[][] nei = new boolean[complexity][complexity];
		int W = complexity/2;
		for(int i=k-W; i<=k+W; i++)
			for(int j=l-W; j<=l+W; j++)
			{
				nei[i-k+W][j-l+W] = m[i][j];
			}
		return nei;
	}

	public static double[][] getNeighbourhood(double[][] m, int k, int l, int complexity)
	{
		double[][] nei = new double[complexity][complexity];
		int W = complexity/2;
		for(int i=k-W; i<=k+W; i++)
			for(int j=l-W; j<=l+W; j++)
			{
				nei[i-k+W][j-l+W] = m[i][j];
			}
		return nei;
	}

	public static doublePoint[][] getNeighbourhood(doublePoint[][] m, int k, int l, int complexity)
	{
		doublePoint[][] nei = new doublePoint[complexity][complexity];
		int W = complexity/2;
		for(int i=k-W; i<=k+W; i++)
			for(int j=l-W; j<=l+W; j++)
			{
				nei[i-k+W][j-l+W] = new doublePoint(m[i][j].x, m[i][j].y);
			}
		return nei;
	}
}
