package org.cytoscape.pcm.internal.logic.cmcAlgo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.pcm.internal.logic.Complex;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.BronKerboschCliqueFinder;
import org.jgrapht.graph.SimpleGraph;

public class Cmc implements Callable<Set<Complex>>{
    CyNetwork network;
    
    public Cmc(CyNetwork network) {
        this.network = network;
    }
    
    @Override
    public Set<Complex> call() throws Exception {
        Set<Complex> cmcComplexes = new HashSet<Complex>();
        CyRootNetwork root = ((CySubNetwork)network).getRootNetwork();
        
        UndirectedGraph<CyNode, CyEdge> g = new SimpleGraph<CyNode, CyEdge>(CyEdge.class);
        List<CyNode> nodeList = network.getNodeList();
        List<CyEdge> edgeList = network.getEdgeList();
        for(CyNode n : nodeList){
            g.addVertex(n);
        }
        for(CyEdge e : edgeList){
            if(e.getSource().equals(e.getTarget())){
                continue; // removing self-loops
            }
            g.addEdge(e.getSource(), e.getTarget(),e);
        }
        BronKerboschCliqueFinder bcfinder = new BronKerboschCliqueFinder(g);
        
        List<Set<CyNode>> requiredNodeSetsList = (List<Set<CyNode>>) bcfinder.getAllMaximalCliques();
        
        for(Set<CyNode> requiredNodes : requiredNodeSetsList) {
            List<CyEdge> requiredEdges = new ArrayList<CyEdge>();
            for(CyEdge e : edgeList){
                if(requiredNodes.contains(e.getSource()) && requiredNodes.contains(e.getTarget())){
                    requiredEdges.add(e);
                }
            }
            // Removing very small complexes
            if(requiredEdges.size() >= 3) {
                CyNetwork subNet = root.addSubNetwork(requiredNodes, requiredEdges);
                cmcComplexes.add(new Complex(subNet));
            }
        }
        return cmcComplexes;
    }
    
}
