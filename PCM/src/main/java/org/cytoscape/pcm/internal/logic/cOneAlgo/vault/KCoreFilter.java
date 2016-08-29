package org.cytoscape.pcm.internal.logic.cOneAlgo.vault;

import com.sosnoski.util.array.IntArray;
import org.cytoscape.pcm.internal.results.standardgraph.Directedness;
import org.cytoscape.pcm.internal.results.standardgraph.Graph;



/**
 * Contracts a nodeset to one of its k-cores and accepts the nodeset
 * if there is a k-core in the nodeset, otherwise rejects it.
 * 
 * A k-core of a nodeset is a subset of the nodeset such that every
 * vertex has at least k connections to other vertices in the core.
 * E.g., the 2-core consists of the subset without nodes of degree 1.
 * 
 * k must be specified as a parameter of this filter.
 * 
 * @author tamas
 * @todo There is a much more efficient version to calculating coreness
 *       than the one we have here.
 */
public class KCoreFilter implements NodeSetFilter {
	protected int k = 2;
	
	public KCoreFilter() {
		this(2);
	}
	
	/**
	 * Constructs a k-core filter with the given k value.
	 * 
	 * @param k  the coreness threshold
	 */
	public KCoreFilter(int k) {
		setK(k);
	}
	
	/**
	 * Contracts the given nodeset to its k-core and accepts it if there is a
	 * k-core in the nodeset.
	 */
	public boolean filter(MutableNodeSet nodeSet) {
		boolean finished = false;
		Graph graph = nodeSet.getGraph();
		MutableNodeSet nodeSetCopy = new MutableNodeSet(nodeSet);
		
		if (k <= 0)
			return true;

		while (!finished) {
			IntArray toRemove = new IntArray();
			
			for (int i: nodeSetCopy) {
				int[] neis = graph.getAdjacentNodeIndicesArray(i, Directedness.ALL);
				int numNeis = 0;
				
				for (int nei: neis) {
					if (nodeSetCopy.contains(nei))
						numNeis++;
				}
				
				if (numNeis < k)
					toRemove.add(i);
			}
			
			nodeSetCopy.remove(toRemove.toArray());
			finished = (toRemove.size() == 0);
		}
		
		return !nodeSetCopy.isEmpty();
	}

	/**
	 * Returns the coreness threshold of the filter
	 * 
	 * @return  the coreness threshold
	 */
	public int getK() {
		return this.k;
	}
	
	/**
	 * Sets the coreness threshold of the filter
	 * 
	 * @param  k  the new coreness threshold
	 */
	public void setK(int k) {
		this.k = k;
	}
}
