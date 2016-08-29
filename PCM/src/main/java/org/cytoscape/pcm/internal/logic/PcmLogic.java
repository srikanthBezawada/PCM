package org.cytoscape.pcm.internal.logic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.pcm.internal.Parameters;
import org.cytoscape.pcm.internal.PcmGui;
import org.cytoscape.pcm.internal.logic.cOneAlgo.ClOne;
import org.cytoscape.pcm.internal.logic.cmcAlgo.Cmc;
import org.cytoscape.pcm.internal.logic.mclAlgo.Mcl;
import org.cytoscape.pcm.internal.logic.mcodeAlgo.Mcode;
import org.cytoscape.pcm.internal.logic.pewccAlgo.Pewcc;
import org.cytoscape.view.model.CyNetworkView;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class PcmLogic extends Thread{
    private boolean stop;
    
    private PcmGui panel;
    private CyNetwork network;
    private CyNetworkView networkView;
    private Parameters parameters;
    
    public PcmLogic(PcmGui panel, CyNetwork network, CyNetworkView networkView, Parameters parameters) {
        this.panel = panel;
        this.network = network;
        this.networkView = networkView;
        this.parameters = parameters;
    }
    
    public void run() {
        stop = false;
        panel.startComputation();
        long startTime = System.currentTimeMillis();
        
        Set<Complex> finalResult = new HashSet<Complex>();
        
        final int cores = Runtime.getRuntime().availableProcessors();
	final ExecutorService service = Executors.newFixedThreadPool(cores);
        List<Future<Set<Complex>>> allAlgosResult = new ArrayList<Future<Set<Complex>>>();
        
        if(parameters.isPewcc) {
            //Future<Set<Complex>> pewccRes = service.submit(new Pewcc(network));
            Callable<Set<Complex>> callable = new Pewcc(network);
            Future<Set<Complex>> pewccRes = service.submit(callable);
            allAlgosResult.add(pewccRes);
        }
        
        if(parameters.isCmc) {
            //Future<Set<Complex>> cmcRes = service.submit(new Cmc(network));
            Callable<Set<Complex>> callable = new Cmc(network);
            Future<Set<Complex>> cmcRes = service.submit(callable);
            allAlgosResult.add(cmcRes);
        } 
        
        if(parameters.isMcode) {
            Callable<Set<Complex>> callable = new Mcode(network);
            Future<Set<Complex>> mcodeRes = service.submit(callable);
            allAlgosResult.add(mcodeRes);
        }
        
        if(parameters.isMcl) {
            Callable<Set<Complex>> callable = new Mcl(network);
            Future<Set<Complex>> mclRes = service.submit(callable);
            allAlgosResult.add(mclRes);
        }
        
        if(parameters.isCone) {
            try {
                
                Callable<Set<Complex>> callable = new ClOne(network);
                Future<Set<Complex>> cOneRes = service.submit(callable);
                allAlgosResult.add(cOneRes);
            } catch (Exception ex) {
                Logger.getLogger(PcmLogic.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
        for(Future<Set<Complex>> eachAlgo : allAlgosResult) {
            try {
                Set<Complex> eachAlgoResult = eachAlgo.get();
                finalResult.addAll(eachAlgoResult);
            } catch (InterruptedException ex) {
                Logger.getLogger(PcmLogic.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(PcmLogic.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
        
        service.shutdown();
        
        Set<Complex> finalResultMerged = merge(finalResult);
        if(finalResultMerged == null) {
            return;
        }
        if(stop) {
            return;
        }
        
        panel.resultsCalculated(finalResult, network);
        
        while (true) {
            try {
                    service.awaitTermination(1, TimeUnit.DAYS);
                    break;
            } catch (InterruptedException ignored) {
            }
        }
        
        
        
        
        
        long endTime = System.currentTimeMillis();
        long difference = endTime - startTime;
        System.out.println("Execution time for PCM " + difference +" milli seconds");
        panel.endComputation();
    }
    
    public void end() {
        stop = true;
    }
    
    
    public Set<Complex> merge(Set<Complex> clusters) {
        
        
        double overlapValue = 0.8;
        Set<Complex> newClusters = new HashSet<Complex>();
        List<Complex> unmergedLists = new ArrayList<Complex>();
        unmergedLists.addAll(clusters);
        
        UndirectedGraph<Complex, DefaultEdge> g = new SimpleGraph<Complex, DefaultEdge>(DefaultEdge.class);
        for(Complex cluster : clusters) {
            g.addVertex(cluster);
        }
        
        Iterator<Complex> outer = unmergedLists.iterator();
        if(stop) {
            return null;
        }
        while(outer.hasNext()) {
            if(stop) {
                return null;
            }
            Complex C1 = outer.next();
            Iterator<Complex> inner = unmergedLists.iterator();
            
            while(inner.hasNext()) {
                if(stop) {
                    return null;
                }
                Complex C2 = inner.next();
                
                if(C1.equals(C2)){
                    continue;
                }
                
                if(matchCoefficient(C1, C2) >= overlapValue) {
                    g.addEdge(C1, C2);
                }
            }
        }
        
        
        
        ConnectivityInspector<Complex, DefaultEdge> inspector = new ConnectivityInspector<Complex, DefaultEdge>(g);
        List<Set<Complex>> connComponents = inspector.connectedSets();
        for(Set<Complex> component : connComponents) {
            if(component.isEmpty() == false)
            newClusters.add(mergeComponent(component));
        }
        
        return newClusters;
    }
        
    
    public double matchCoefficient(Complex C1, Complex C2) {
        double inter = intersection(C1.getNodes(), C2.getNodes()).size();
        double matchCoeff = (inter * inter)/(C1.getNodes().size() * C2.getNodes().size());
    
        return matchCoeff;
    }
    
    public Complex mergeComponent(Set<Complex> component) {
        Set<CyNode> nodesUnion = new HashSet<CyNode>();
        Set<CyEdge> edgesUnion = new HashSet<CyEdge>();
        CyRootNetwork root = ((CySubNetwork)network).getRootNetwork();
        if(component.isEmpty()) {
            return null;
        }
        for(Complex C : component) {
            if(component.size() == 1) {
                return C;
            } else{
                nodesUnion.addAll(C.getNodes());
                edgesUnion.addAll(C.getEdges());
                CyNetwork mergedNetwork = root.addSubNetwork(nodesUnion, edgesUnion);
                Complex mergedCluster = new Complex(mergedNetwork);
                return mergedCluster;
            }
        }
        return null;
    }
    
    public Set<CyNode> intersection(List<CyNode> setA, List<CyNode> setB) {
        Set<CyNode> tmp = new HashSet<CyNode>();
        for (CyNode x : setA) {
            if (setB.contains(x)) {
                tmp.add(x);
            }
        }
        return tmp;    
    }
    
    
    
    
   
    
    
    
}
