package org.cytoscape.pcm.internal.logic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class Merger {
    
    public static Set<Complex> merge(List<Complex> clusters, CyNetwork network, double overlapValue, int minClustersInComponent) {
        Set<Complex> newClusters = new HashSet<Complex>();
        
        UndirectedGraph<ComplexWrapper, DefaultEdge> g = new SimpleGraph<ComplexWrapper, DefaultEdge>(DefaultEdge.class);
        List<ComplexWrapper> oldClusters = new ArrayList<ComplexWrapper>();
        
        int i=1;
        for(Complex cluster : clusters) {
            oldClusters.add(new ComplexWrapper(cluster, i));
            i++;
        }
        
        for(ComplexWrapper cw : oldClusters) {
            g.addVertex(cw);
        }
        
        Iterator<ComplexWrapper> outer = oldClusters.iterator();
        
        while(outer.hasNext()) {
            ComplexWrapper C1 = outer.next();
            Iterator<ComplexWrapper> inner = oldClusters.iterator();
            
            while(inner.hasNext()) {
                ComplexWrapper C2 = inner.next();
                
                if(C1.equals(C2)){
                    continue;
                }
                
                if(matchCoefficient(C1.getComplex(), C2.getComplex()) >= overlapValue) {
                    g.addEdge(C1, C2);
                }
            }
        }
        
        
        ConnectivityInspector<ComplexWrapper, DefaultEdge> inspector = new ConnectivityInspector<ComplexWrapper, DefaultEdge>(g);
        List<Set<ComplexWrapper>> connComponents = inspector.connectedSets();
        
        for(Set<ComplexWrapper> component : connComponents) {
            if(!component.isEmpty()){
                if(component.size() >= minClustersInComponent) {
                    newClusters.add(mergeComponent(component, network));
                }
            }
        }
        
        return newClusters;
    }
        
    
    public static double matchCoefficient(Complex C1, Complex C2) {
        double inter = intersection(C1.getNodes(), C2.getNodes()).size();
        double matchCoeff = (inter * inter)/(C1.getNodes().size() * C2.getNodes().size());
    
        return matchCoeff;
    }
    
    public static Complex mergeComponent(Set<ComplexWrapper> component, CyNetwork network) {
        Set<CyNode> nodesUnion = new HashSet<CyNode>();
        Set<CyEdge> edgesUnion = new HashSet<CyEdge>();
        CyRootNetwork root = ((CySubNetwork)network).getRootNetwork();
        
        for(ComplexWrapper C : component) {
            
            nodesUnion.addAll(C.getComplex().getNodes());
            edgesUnion.addAll(C.getComplex().getEdges());
            CyNetwork mergedNetwork = root.addSubNetwork(nodesUnion, edgesUnion);
            Complex mergedCluster = new Complex(mergedNetwork);
            return mergedCluster;
            
        }
        return null;
    }
    
    public static Set<CyNode> intersection(List<CyNode> setA, List<CyNode> setB) {
        Set<CyNode> tmp = new HashSet<CyNode>();
        for (CyNode x : setA) {
            if (setB.contains(x)) {
                tmp.add(x);
            }
        }
        return tmp;    
    }
}
