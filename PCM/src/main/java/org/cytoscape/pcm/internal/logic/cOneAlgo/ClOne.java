package org.cytoscape.pcm.internal.logic.cOneAlgo;

import org.cytoscape.pcm.internal.logic.cOneAlgo.vault.ClusterONE;
import org.cytoscape.pcm.internal.logic.cOneAlgo.vault.UniqueIDGenerator;
import org.cytoscape.pcm.internal.logic.cOneAlgo.vault.NodeSet;
import org.cytoscape.pcm.internal.logic.cOneAlgo.vault.ValuedNodeSet;
import org.cytoscape.pcm.internal.logic.cOneAlgo.vault.ClusterONEAlgorithmParameters;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.pcm.internal.logic.Complex;
import org.cytoscape.pcm.internal.logic.utils.CyNetworkUtil;
import org.cytoscape.pcm.internal.results.Graph;

public class ClOne implements Callable<Set<Complex>>{
    CyNetwork network;
    
    public ClOne(CyNetwork network) {
        this.network = network;
    }
    
    @Override
    public Set<Complex> call() throws Exception {
        ClusterONE runCl1 = new ClusterONE(new ClusterONEAlgorithmParameters());
        Graph graph = convertCyNetworkToGraph(network, null);
        runCl1.setGraph(graph);
        runCl1.run();
        List<ValuedNodeSet> cl1Result = runCl1.getResults();
        List<CyNode> nodeMapping = ((Graph)runCl1.getGraph()).getNodeMapping();
        
        List<CyEdge> edgeList = network.getEdgeList();
        
        List<CyNode> nodes = new ArrayList<CyNode>();
        List<CyEdge> edges = new ArrayList<CyEdge>();
        Set<Complex> clusterOneComplexes = new HashSet<Complex>();
        
        for(NodeSet cluster : cl1Result) {
            for (int idx: cluster) {
                nodes.add(nodeMapping.get(idx));
            }
            
            for(CyEdge e : edgeList){
                if(nodes.contains(e.getSource()) && nodes.contains(e.getTarget())){
                    edges.add(e);
                }
            }
            
            CyRootNetwork root = ((CySubNetwork)network).getRootNetwork();
            Complex C = new Complex(root.addSubNetwork(nodes, edges));
            clusterOneComplexes.add(C);
            
            // Removing very small complexes
            if(C.getEdges().size() < 3)
                clusterOneComplexes.remove(C);
            
            nodes.clear();
            edges.clear();
        }
        return clusterOneComplexes;
    }
    
    public Graph convertCyNetworkToGraph(CyNetwork network, String weightAttr) {
		org.cytoscape.pcm.internal.results.Graph graph = new Graph(network);
		UniqueIDGenerator<CyNode> nodeIdGen = new UniqueIDGenerator<CyNode>(graph);
		Double weight;
		
		/* Import all the edges into our graph */
		
                for (CyEdge edge: network.getEdgeList()) {
                        int src = nodeIdGen.get(edge.getSource());
                        int dest = nodeIdGen.get(edge.getTarget());
                        if (src == dest)
                                continue;

                        if (weightAttr == null) {
                                weight = null;
                        } else {
                                CyRow row = network.getRow(edge);
                                weight = row.get(weightAttr, Double.class, 1.0);
                        }
                        if (weight == null)
                                weight = 1.0;

                        graph.createEdge(src, dest, weight);
                }
		
		
		graph.setNodeMapping(nodeIdGen.getReversedList());
		
		
		return graph;
	}
    
        
    
}
