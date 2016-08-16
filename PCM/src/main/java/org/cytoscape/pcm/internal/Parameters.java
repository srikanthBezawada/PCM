package org.cytoscape.pcm.internal;

public class Parameters {
    public boolean isCmc;
    public boolean isPewcc;
    public boolean isMcode;
    
    public Parameters(boolean isPewcc, boolean isCmc, boolean isMcode) {
        this.isCmc = isCmc;
        this.isPewcc = isPewcc;
        this.isMcode = isMcode;
    }
}
