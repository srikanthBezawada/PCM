package org.cytoscape.pcm.internal;

public class Parameters {
    public double mergeThreshold;
    public int similarityType;
    public int clusterThreshold;
    public boolean isCmc;
    public boolean isPewcc;
    public boolean isMcode;
    public boolean isMcl;
    public boolean isCone;
   
    
    public Parameters(double mergeThreshold, int similarityType, int clusterThreshold,boolean isPewcc, boolean isCmc, boolean isMcode, boolean isMcl, boolean isCone) {
        this.mergeThreshold = mergeThreshold;
        this.similarityType = similarityType;
        this.clusterThreshold = clusterThreshold;
        this.isCmc = isCmc;
        this.isPewcc = isPewcc;
        this.isMcode = isMcode;
        this.isMcl = isMcl;
        this.isCone = isCone;
    }
}
