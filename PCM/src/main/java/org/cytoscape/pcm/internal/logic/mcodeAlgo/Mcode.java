package org.cytoscape.pcm.internal.logic.mcodeAlgo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.model.SavePolicy;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.pcm.internal.logic.Complex;
import org.cytoscape.pcm.internal.logic.algorithm.NodeCluster;

public class Mcode implements Callable<Set<Complex>>{
    CyNetwork network;
    
    public Mcode(CyNetwork network) {
        this.network = network;
    }
    
    @Override
    public Set<Complex> call() throws Exception {
        
        NodeCluster.init();
        MCODEParameterSet currentParamsCopy = MCODECurrentParameters.getInstance().getParamsCopy(null);
        currentParamsCopy.setDefaultParams();
        
        RunMCODE runMCODE = new RunMCODE(1, 1, currentParamsCopy, network);
        List<NodeCluster> clusters = runMCODE.run();
        
                
        Set<Complex> mcodeComplexes = new HashSet<Complex>();
        for(NodeCluster cluster : clusters) {
            //Get the root network here
            CyRootNetwork root = ((CySubNetwork)network).getRootNetwork();
            Complex C = new Complex(cluster.getSubNetwork(network, root));
            mcodeComplexes.add(C);
        }
        return mcodeComplexes;
    }
    
    
}
