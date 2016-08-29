package org.cytoscape.pcm.internal.logic.mclAlgo;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.work.TaskMonitor;


import cern.colt.function.tdouble.IntIntDoubleFunction;
import cern.colt.matrix.tdouble.DoubleMatrix2D;
import org.cytoscape.pcm.internal.logic.vault.CyMatrix;
import org.cytoscape.pcm.internal.logic.vault.Matrix;
import org.cytoscape.pcm.internal.logic.vault.NodeCluster;


public class RunMCL {

	private double inflationParameter; //density parameter 
	private int number_iterations; //number of inflation/expansion cycles
	private double clusteringThresh; //Threshold used to remove weak edges between distinct clusters
	private double maxResidual; //The maximum residual to look for
	private boolean canceled = false;
	protected int clusterCount = 0;
	private boolean createMetaNodes = false;
	private CyMatrix distanceMatrix = null;
	private CyMatrix matrix = null;
	private List<CyNode> nodes = null;
	private boolean debug = true;
	private int nThreads = Runtime.getRuntime().availableProcessors()-1;
	
	public RunMCL(CyMatrix dMat, double inflationParameter, int num_iterations, 
            double clusteringThresh, double maxResidual, int maxThreads)
	{
			
		this.distanceMatrix = dMat;
		this.matrix = dMat.copy();
		this.inflationParameter = inflationParameter;
		this.number_iterations = num_iterations;
		this.clusteringThresh = clusteringThresh;
		this.maxResidual = maxResidual;
		nodes = distanceMatrix.getRowNodes();
		if (maxThreads > 0)
			nThreads = maxThreads;
		else
			nThreads = Runtime.getRuntime().availableProcessors()-1;

	}
	
	public List<NodeCluster> run(CyNetwork network)
	{
		Long networkID = network.getSUID();		//String networkID = network.getIdentifier();

		long startTime = System.currentTimeMillis();

		// Matrix matrix;
		double numClusters;


		// Normalize
		normalize(matrix.getColtMatrix(), clusteringThresh, false);


		double residual = 1.0;
		IntIntDoubleFunction myPow = new MatrixPow(inflationParameter);
		
		for (int i=0; (i<number_iterations)&&(residual>maxResidual); i++)
		{
			// Expand
			{
				long t = System.currentTimeMillis();
				Matrix multiMatrix = matrix.multiplyMatrix(matrix);
				matrix = matrix.copy(multiMatrix);

				// Normalize
				normalize(matrix.getColtMatrix(), clusteringThresh, false);
			}


			// Inflate
			DoubleMatrix2D m = matrix.getColtMatrix();
			{
				long t = System.currentTimeMillis();
				m.forEachNonZero(myPow);

				// Normalize
				normalize(matrix.getColtMatrix(), clusteringThresh, true);
			}

			m.trimToSize();
			residual = calculateResiduals(m);

			
		}


		clusterCount = 0;
		HashMap<Integer, NodeCluster> clusterMap = new HashMap<Integer, NodeCluster>();
		matrix.getColtMatrix().forEachNonZero(new ClusterMatrix(clusterMap));

		int clusterNumber = 1;
		HashMap<NodeCluster,NodeCluster> cMap = new HashMap<NodeCluster, NodeCluster>();
		for (NodeCluster cluster: NodeCluster.sortMap(clusterMap)) {

			if (cMap.containsKey(cluster))
				continue;

			cMap.put(cluster,cluster);

			cluster.setClusterNumber(clusterNumber);
			clusterNumber++;
		}

		Set<NodeCluster>clusters = cMap.keySet();
		return new ArrayList<NodeCluster>(clusters);
	}
	
	
	/**
	 * This method does threshold and normalization.  First, we get rid of
	 * any cells that have a value beneath our threshold and in the same pass
	 * calculate all of the column sums.  Then we use the column sums to normalize
	 * each column such that all of the cells in the column sum to 1.
	 *
	 * @param matrix the (sparse) data matrix we're operating on
	 * @param clusteringThresh the maximum value that we will take as a "zero" value
	 * @param prune if 'false', don't prune this pass
	 */
	private void normalize(DoubleMatrix2D matrix, double clusteringThresh, boolean prune)
	{
		// Remove any really low values and create the sums array
		double [] sums = new double[matrix.columns()];
		matrix.forEachNonZero(new MatrixZeroAndSum(prune, clusteringThresh, sums));

		// Finally, adjust the values
		matrix.forEachNonZero(new MatrixNormalize(sums));

		// Last step -- find any columns that summed to zero and set the diagonal to 1
		for (int col = 0; col < sums.length; col++) {
			if (sums[col] == 0.0) {
				// debugln("Column "+col+" sums to 0");
				matrix.set(col,col,1.0);
			}
		}
	}
	
	/**
	 * This method calculates the residuals.  Calculate the sum and
	 * sum of squares for each row, then return the maximum residual.
	 *
	 * @param matrix the (sparse) data matrix we're operating on
	 * @return residual value
	 */
	private double calculateResiduals(DoubleMatrix2D matrix) {
		// Calculate and return the residuals
		double[] sums = new double[matrix.columns()];
		double [] sumSquares = new double[matrix.columns()];
		matrix.forEachNonZero(new MatrixSumAndSumSq(sums, sumSquares));
		double residual = 0.0;
		for (int i = 0; i < sums.length; i++) {
			residual = Math.max(residual, sums[i] - sumSquares[i]);
		}
		return residual;
	}

	private static DecimalFormat scFormat = new DecimalFormat("0.###E0");
	private static DecimalFormat format = new DecimalFormat("0.###");
	

	
	/**
	 * The MatrixPow class raises the value of each non-zero cell of the matrix
	 * to the power passed in it's constructor.
	 */
	private class MatrixPow implements IntIntDoubleFunction {
		double pow;

		public MatrixPow(double power) {
			this.pow = power;
		}
		
		public double apply(int row, int column, double value) {
			if (canceled) { return 0.0; }
			return Math.pow(value,pow);
		}
		
	}

	/**
	 * The MatrixZeroAndSum looks through all non-zero cells in a matrix
	 * and if the value of the cell is beneath "threshold" it is set to
	 * zero.  All non-zero cells in a column are added together to return
	 * the sum of each column.
	 */
	private class MatrixZeroAndSum implements IntIntDoubleFunction {
		double threshold;
		double [] colSums;
		boolean prune;

		public MatrixZeroAndSum (boolean prune, double threshold, double[] colSums) {
			this.threshold = threshold;
			this.colSums = colSums;
			this.prune = prune;
		}

		public double apply(int row, int column, double value) {
			if (prune && (value < threshold))
				return 0.0;
			colSums[column] += value;
			return value;
		}
	}
	
	/**
	 * The MatrixSumAndSumSq looks through all non-zero cells in a matrix
	 * and calculates the sums and sum of squares for each column.
	 */
	private class MatrixSumAndSumSq implements IntIntDoubleFunction {
		double [] sumSquares;
		double [] colSums;

		public MatrixSumAndSumSq (double[] colSums, double[] sumSquares) {
			this.sumSquares = sumSquares;
			this.colSums = colSums;
		}
		public double apply(int row, int column, double value) {
			colSums[column] += value;
			sumSquares[column] += value*value;
			return value;
		}
	}
	
	/**
	 * The MatrixNormalize class takes as input an array of sums for
	 * each column in the matrix and uses that to normalize the sum of the
	 * column to 1.  If the sum of the column is 0, the diagonal is set to 1.
	 */
	private class MatrixNormalize implements IntIntDoubleFunction {
		double [] colSums;

		public MatrixNormalize(double[] colSums) {
			this.colSums = colSums;
		}

		public double apply(int row, int column, double value) {
			if (canceled) { return 0.0; }
			return value/colSums[column];
		}
	}
	
	private class ClusterMatrix implements IntIntDoubleFunction {
		Map<Integer, NodeCluster> clusterMap;

		public ClusterMatrix(Map<Integer,NodeCluster> clusterMap) {
			this.clusterMap = clusterMap;
		}

		public double apply(int row, int column, double value) {
			if (canceled) { return 0.0; }

			if (row == column) 
				return value;

			if (clusterMap.containsKey(column)) {
				// Already seen "column" -- get the cluster and add column
				NodeCluster columnCluster;
                            columnCluster = clusterMap.get(column);
				if (clusterMap.containsKey(row)) {
					// We've already seen row also -- join them
					NodeCluster rowCluster = clusterMap.get(row);
					if (rowCluster == columnCluster) 
						return value;
					clusterCount--;
					// logger.debug("Joining cluster "+columnCluster.getClusterNumber()+" and "+rowCluster.getClusterNumber());
					// logger.debug("clusterCount = "+clusterCount);
					columnCluster.addAll(rowCluster);
				} else {
					// logger.debug("Adding "+row+" to "+columnCluster.getClusterNumber());
					// logger.debug("clusterCount = "+clusterCount);
					columnCluster.add(distanceMatrix.getRowNode(row));
				}
				updateClusters(columnCluster);
			} else {
				NodeCluster rowCluster;
				// First time we've seen "column" -- have we already seen "row"
				if (clusterMap.containsKey(row)) {
					// Yes, just add column to row's cluster
					rowCluster = clusterMap.get(row);
					// logger.debug("Adding "+column+" to "+rowCluster.getClusterNumber());
					// logger.debug("clusterCount = "+clusterCount);
					rowCluster.add(distanceMatrix.getColumnNode(column));
				} else {
					clusterCount++;
					rowCluster = new NodeCluster();
					// logger.debug("Created new cluster "+rowCluster.getClusterNumber()+" with "+row+" and "+column);
					// logger.debug("clusterCount = "+clusterCount);
					rowCluster.add(distanceMatrix.getColumnNode(column));
					rowCluster.add(distanceMatrix.getRowNode(row));
				}
				updateClusters(rowCluster);
			}
			return value;
		}

		private void updateClusters(NodeCluster cl) {
			for (CyNode node: cl) {
				clusterMap.put(nodes.indexOf(node), cl);
			}
		}
	}
}
