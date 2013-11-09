public class EigenDetector extends Object
{
	Eigenvalues eigenvalues;

	public EigenDetector(int scale, doublePoint[][] gr)
	{
		super();
		eigenvalues = new Eigenvalues(gr,scale);
	}

	public boolean[][] getNOStructure(double thresh)
	{
		boolean[][] result = new boolean[eigenvalues.eigenvalues.length][eigenvalues.eigenvalues[0].length];
		for (int i=eigenvalues.complexity;i<eigenvalues.eigenvalues.length-eigenvalues.complexity;i++)
		{
			for (int j=eigenvalues.complexity;j<eigenvalues.eigenvalues[0].length-eigenvalues.complexity;j++)
			{
				double k1 = Math.max(eigenvalues.eigenvalues[i][j].x,eigenvalues.eigenvalues[i][j].y);
				double k2 = Math.min(eigenvalues.eigenvalues[i][j].x,eigenvalues.eigenvalues[i][j].y);
				if (k2 < thresh)
					result[i][j] = true;
				else
					result[i][j] = false;
			}
		}

		return result;
	}

	public boolean[][] get1DStructure(double hithresh, double lowthresh)
	{
		boolean[][] result = new boolean[eigenvalues.eigenvalues.length][eigenvalues.eigenvalues[0].length];
		for (int i=eigenvalues.complexity;i<eigenvalues.eigenvalues.length-eigenvalues.complexity;i++)
		{
			for (int j=eigenvalues.complexity;j<eigenvalues.eigenvalues[0].length-eigenvalues.complexity;j++)
			{
				double k1 = Math.max(eigenvalues.eigenvalues[i][j].x,eigenvalues.eigenvalues[i][j].y);
				double k2 = Math.min(eigenvalues.eigenvalues[i][j].x,eigenvalues.eigenvalues[i][j].y);
				if ((k1 > hithresh) && (k2 < lowthresh))
					result[i][j] = true;
				else
					result[i][j] = false;
			}
		}

		return result;
	}

	public double[][] get2DStructure(double thresh)
	{
		double[][] result = new double[eigenvalues.eigenvalues.length][eigenvalues.eigenvalues[0].length];
		for (int i=eigenvalues.complexity;i<eigenvalues.eigenvalues.length-eigenvalues.complexity;i++)
		{
			for (int j=eigenvalues.complexity;j<eigenvalues.eigenvalues[0].length-eigenvalues.complexity;j++)
			{
				double k1 = Math.max(eigenvalues.eigenvalues[i][j].x,eigenvalues.eigenvalues[i][j].y);
				double k2 = Math.min(eigenvalues.eigenvalues[i][j].x,eigenvalues.eigenvalues[i][j].y);

				if ((k2 > thresh) && (k1 < 2*k2))
					result[i][j] = (k1/k2);
			}
		}
		return result;
	}

	/*public boolean[][] get2DStructure(double thresh)
	{
		boolean[][] result = new boolean[eigenvalues.eigenvalues.length][eigenvalues.eigenvalues[0].length];
		for (int i=eigenvalues.complexity;i<eigenvalues.eigenvalues.length-eigenvalues.complexity;i++)
		{
			for (int j=eigenvalues.complexity;j<eigenvalues.eigenvalues[0].length-eigenvalues.complexity;j++)
			{
				double k1 = Math.max(eigenvalues.eigenvalues[i][j].x,eigenvalues.eigenvalues[i][j].y);
				double k2 = Math.min(eigenvalues.eigenvalues[i][j].x,eigenvalues.eigenvalues[i][j].y);

				result[i][j] = ((k2 > thresh) && (k1 < 2*k2));
			}
		}
		return result;
	}*/

	/*public boolean[][] get1DFlow(double thresh2d, double threshNd)
	{
		boolean[][] s2d = get2DStructure(thresh2d);
		boolean[][] se = getEnergies(eigenvalues.complexity);
		boolean[][] sNd = getNOStructure(threshNd);

		boolean[][] result = new boolean[se.length][se[0].length];
		for (int i=0;i<se.length;i++)
			for (int j=0;j<se[0].length;j++)
				result[i][j] = se[i][j] & (!s2d[i][j]) & (!sNd[i][j]);

		return result;
	}*/

	private boolean[][] getEnergies(int scale)
	{
		boolean[][] result = new boolean[eigenvalues.gradient.length][eigenvalues.gradient[0].length];

		doublePoint[][] e1 = new doublePoint[eigenvalues.gradient.length][eigenvalues.gradient[0].length];
		doublePoint[][] e2 = new doublePoint[eigenvalues.gradient.length][eigenvalues.gradient[0].length];
		boolean[][] mapA = getAcuteAngleMap(scale);
		boolean[][] mapO = getObscuteAngleMap(scale);
		for (int i=scale;i<eigenvalues.gradient.length-scale;i++)
		{
			System.out.println(i+"/"+(eigenvalues.gradient.length-scale));
			for (int j=scale;j<eigenvalues.gradient[0].length-scale;j++)
			{
				doublePoint[][] nei = Eigenvalues.getNeighbourhood(eigenvalues.gradient,i,j,scale);
				boolean[][] mei = Eigenvalues.getNeighbourhood(mapA,i,j,scale);
				e1[i][j] = energy(nei,mei,scale);
				mei = Eigenvalues.getNeighbourhood(mapO,i,j,scale);
				e2[i][j] = energy(nei,mei,scale);

				double s1 = e1[i][j].x + e1[i][j].y;
				double s2 = e2[i][j].x + e2[i][j].y;
				result[i][j] = (Math.max(s1,s2) < 2*Math.min(s1,s2));
			}
		}
		return result;
	}

	private doublePoint energy(doublePoint[][] m, boolean[][] map, int scale)
	{
		doublePoint ssums = Eigenvalues.ssum(m,map,scale);
		double msum = Eigenvalues.msum(m,map,scale);

		double sqrtD = Math.sqrt(Math.pow(ssums.x-ssums.y,2) + 4*(Math.pow(msum,2)));
		double base = ssums.x + ssums.y;

		return (new doublePoint((base+sqrtD)/2,(base-sqrtD)/2));
	}

	public boolean[][] getObscuteAngleMap(int complexity)
	{
		boolean[][] result = new boolean[eigenvalues.gradient.length][eigenvalues.gradient[0].length];
		for (int i=complexity;i<eigenvalues.gradient.length-complexity;i++)
			for (int j=complexity;j<eigenvalues.gradient[0].length-complexity;j++)
			{
				doublePoint v = eigenvalues.dominantEigenvectors[i][j];
				double tmp = v.x * eigenvalues.gradient[i][j].x + v.y * eigenvalues.gradient[i][j].y;
				result[i][j] = (tmp < 0);
			}

		return result;
	}

	public boolean[][] getAcuteAngleMap(int complexity)
	{
		boolean[][] result = new boolean[eigenvalues.gradient.length][eigenvalues.gradient[0].length];
		for (int i=complexity;i<eigenvalues.gradient.length-complexity;i++)
			for (int j=complexity;j<eigenvalues.gradient[0].length-complexity;j++)
			{
				doublePoint v = eigenvalues.dominantEigenvectors[i][j];
				double tmp = v.x * eigenvalues.gradient[i][j].x + v.y * eigenvalues.gradient[i][j].y;
				result[i][j] = (tmp > 0);
			}

		return result;
	}

}
