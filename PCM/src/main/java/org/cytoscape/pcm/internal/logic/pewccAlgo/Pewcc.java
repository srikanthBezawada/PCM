package org.cytoscape.pcm.internal.logic.pewccAlgo;

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

public class Pewcc implements Callable<Set<Complex>>{
    CyNetwork network;
    
    public Pewcc(CyNetwork network) {
        this.network = network;
    }

    
    @Override
    public Set<Complex> call() throws Exception {
        CyRootNetwork root = ((CySubNetwork)network).getRootNetwork();
        CyNetwork subNet, subNetTemp;
        List<CyNode> nodeList = network.getNodeList();
        List<CyNode> remnodeList = new ArrayList<CyNode>();
        List<CyNode> rejoinList = new ArrayList<CyNode>();
        List<CyEdge> edgeList = network.getEdgeList();
        List<CyNode> neighbourNodeList;
        List<CyEdge> neightbourEdgeList;
        int counter = 0;
        
        double joinPValue = 0.5;
        
        Set<Complex> clusters = new HashSet<Complex>();
        
        for(CyNode cprotein : nodeList) {
            neighbourNodeList = network.getNeighborList(cprotein, CyEdge.Type.ANY);
            neighbourNodeList.add(cprotein);
            if(neighbourNodeList.size() > 3) {
                neightbourEdgeList =  findNeighbourEdges(edgeList, neighbourNodeList);
                subNet = root.addSubNetwork(neighbourNodeList, neightbourEdgeList);
                subNetTemp = root.addSubNetwork(neighbourNodeList, neightbourEdgeList);
                
                while(subNetTemp.getNodeCount() > 3) {
                    remnodeList.add(findNodeToRemove(subNetTemp, cprotein));
                    subNetTemp.removeNodes(remnodeList);
                    remnodeList.clear();
                }
                
                for(CyNode n : neighbourNodeList) {
                    counter = 0;
                    if(subNetTemp.containsNode(n)) {
                        continue;
                    }
                    for(CyNode x : subNetTemp.getNodeList()) {
                        if(subNet.containsEdge(x, n) || subNet.containsEdge(x, n)) {
                            counter++;
                        }
                    }

                    if(counter/(double)(subNetTemp.getNodeCount()) > joinPValue){
                        rejoinList.add(n);
                    }
                    
                }
                
                for(CyNode n: neighbourNodeList) {
                    if(subNetTemp.containsNode(n)) {
                        continue;
                    }
                    if(rejoinList.contains(n)){
                        continue;
                    }
                    Set<CyNode> coll = new HashSet<CyNode>();
                    coll.add(n);
                    subNet.removeNodes(coll); 
                    coll.clear();
                }
     
                Complex C = new Complex(subNet);
                // Removing very small complexes
                if(subNet.getEdgeList().size() >= 3)
                clusters.add(C);
                
                neighbourNodeList.clear();
                neightbourEdgeList.clear();
                rejoinList.clear();
                remnodeList.clear();
                subNetTemp.dispose();
            } 
            

        }
        
        return clusters;
    }
    
    
    public List<CyEdge> findNeighbourEdges(List<CyEdge> edgeList, List<CyNode> neightbourNodes) {
        List<CyEdge> neighbourEges = new ArrayList<CyEdge>();
        for(CyEdge e : edgeList) {
            if(neightbourNodes.contains(e.getSource()) && neightbourNodes.contains(e.getTarget())) 
                neighbourEges.add(e);
        }
        
        return neighbourEges;
    }
    
    public CyNode findNodeToRemove(CyNetwork tempNet, CyNode cp){
        CyNode toRemove = tempNet.getNodeList().get(1);
        for(CyNode n:tempNet.getNodeList()) {
            if(n.equals(cp))
                continue; // verify this
            if(tempNet.getNeighborList(n, CyEdge.Type.ANY).size() < tempNet.getNeighborList(toRemove, CyEdge.Type.ANY).size()) {
                toRemove = n;
            }
        }
        return toRemove;
    }
}
