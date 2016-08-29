package org.cytoscape.pcm.internal;

public class Parameters {
    public boolean isCmc;
    public boolean isPewcc;
    public boolean isMcode;
    public boolean isMcl;
    public boolean isCone;
    
    public Parameters(boolean isPewcc, boolean isCmc, boolean isMcode, boolean isMcl, boolean isCone) {
        this.isCmc = isCmc;
        this.isPewcc = isPewcc;
        this.isMcode = isMcode;
        this.isMcl = isMcl;
        this.isCone = isCone;
    }
}
