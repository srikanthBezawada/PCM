package org.cytoscape.pcm.internal.logic.mclAlgo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.pcm.internal.logic.Complex;
import org.cytoscape.pcm.internal.logic.vault.CyColtMatrix;
import org.cytoscape.pcm.internal.logic.vault.CyMatrix;
import org.cytoscape.pcm.internal.logic.vault.NodeCluster;
import org.cytoscape.pcm.internal.logic.utils.CyNetworkUtil;
import org.cytoscape.pcm.internal.logic.utils.CyNodeUtil;


public class Mcl implements Callable<Set<Complex>> {
    CyNetwork network;
    
    public Mcl(CyNetwork network) {
        this.network = network;
    }
    
    @Override
    public Set<Complex> call() throws Exception {
        
        
        NodeCluster.init();
        
        CyMatrix matrix = makeLargeMatrix(network, true);
        RunMCL runMCL;
        // matrix, context.inflation_parameter, context.iterations, context.clusteringThresh, context.maxResidual, context.maxThreads
        double inflation_parameter = 2.5;
        double clusteringThresh = 1e-15;
        int iterations = 16;
        double maxResidual = 0.001;
        int maxThreads = 0;
        
        runMCL = new RunMCL(matrix, inflation_parameter, iterations, 
		                    clusteringThresh, maxResidual, maxThreads);
        List<NodeCluster> clusters = runMCL.run(network);
        
        Set<Complex> mclComplexes = new HashSet<Complex>();
        for(NodeCluster cluster : clusters) {
            //Get the root network here
            CyRootNetwork root = ((CySubNetwork)network).getRootNetwork();
            Complex C = new Complex(cluster.getSubNetwork(network, root));
            mclComplexes.add(C);
            
            // Removing very small complexes
            if(C.getEdges().size() < 3)
                mclComplexes.remove(C);
        }
        return mclComplexes;
   
    }
    
    public static CyMatrix makeLargeMatrix(CyNetwork network, boolean unDirected) {
		List<CyNode> nodes;
		List<CyEdge> edges;
		
                nodes = network.getNodeList();
                edges = network.getEdgeList();
		
		CyMatrix matrix = new CyColtMatrix(network, nodes.size(), nodes.size());
		matrix.setRowNodes(nodes);
		matrix.setColumnNodes(nodes);
		Map<CyNode, Integer> nodeMap = new HashMap<CyNode, Integer>(nodes.size());
		for (int row = 0; row < nodes.size(); row++) {
			CyNode node = nodes.get(row);
			nodeMap.put(node, row);
			matrix.setRowLabel(row, CyNodeUtil.getName(network, node));
			matrix.setColumnLabel(row, CyNodeUtil.getName(network, node));
		}

		matrix.setSymmetrical(unDirected);

		for (CyEdge edge: edges) {
			double value;
                        value = 1.0;

			int sourceIndex = nodeMap.get(edge.getSource());
			int targetIndex = nodeMap.get(edge.getTarget());
			matrix.setValue(targetIndex, sourceIndex, value);
			// TODO: should we consider maybe doing this on the getValue side?
			if (unDirected)
				matrix.setValue(sourceIndex, targetIndex, value);
		}
                matrix.adjustDiagonals();
		// System.out.println("distance matrix: "+matrix.printMatrix());
		return matrix;
	}
    
    
}
