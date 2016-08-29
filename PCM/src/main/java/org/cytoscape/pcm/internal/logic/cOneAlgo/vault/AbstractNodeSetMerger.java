package org.cytoscape.pcm.internal.logic.cOneAlgo.vault;



/**
 * Abstract node set merger from which concrete node set mergers will derive.
 * 
 * @author tamas
 */
public abstract class AbstractNodeSetMerger implements NodeSetMerger {
	
	
	/**
	 * Constructs a nodeset merger from a string specification.
	 */
	public static AbstractNodeSetMerger fromString(String spec)
	throws InstantiationException {
		if (spec != null) {
			if (spec.equals("single"))
				return new SinglePassNodeSetMerger();
		}
		throw new InstantiationException("unknown nodeset merger: " + spec);
	}
}
