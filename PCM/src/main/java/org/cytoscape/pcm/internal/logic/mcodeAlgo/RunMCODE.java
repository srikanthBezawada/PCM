package org.cytoscape.pcm.internal.logic.mcodeAlgo;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.TaskMonitor;


import java.util.List;
import org.cytoscape.pcm.internal.logic.algorithm.NodeCluster;
import org.cytoscape.pcm.internal.logic.utils.CyNetworkUtil;

/**
 * Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center
 * *
 * * Code written by: Gary Bader
 * * Authors: Gary Bader, Ethan Cerami, Chris Sander
 * *
 * * This library is free software; you can redistribute it and/or modify it
 * * under the terms of the GNU Lesser General Public License as published
 * * by the Free Software Foundation; either version 2.1 of the License, or
 * * any later version.
 * *
 * * This library is distributed in the hope that it will be useful, but
 * * WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 * * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 * * documentation provided hereunder is on an "as is" basis, and
 * * Memorial Sloan-Kettering Cancer Center
 * * has no obligations to provide maintenance, support,
 * * updates, enhancements or modifications.  In no event shall the
 * * Memorial Sloan-Kettering Cancer Center
 * * be liable to any party for direct, indirect, special,
 * * incidental or consequential damages, including lost profits, arising
 * * out of the use of this software and its documentation, even if
 * * Memorial Sloan-Kettering Cancer Center
 * * has been advised of the possibility of such damage.  See
 * * the GNU Lesser General Public License for more details.
 * *
 * * You should have received a copy of the GNU Lesser General Public License
 * * along with this library; if not, write to the Free Software Foundation,
 * * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 * *
 * * User: GaryBader
 * * Date: Jan 25, 2005
 * * Time: 8:41:53 PM
 * * Description: MCODE Score network and find cluster task
 */

/**
 * MCODE Score network and find cluster task.
 */
public class RunMCODE {
	private TaskMonitor taskMonitor = null;
	private boolean interrupted = false;
	private CyNetwork network = null;
	private MCODEAlgorithm alg = null;
	private boolean completedSuccessfully = false;
	private int analyze;
	private int resultId;

	/**
	 * Scores and finds clusters in a given network
	 *
	 * @param network The network to cluster
	 * @param analyze Tells the task if we need to rescore and/or refind
	 * @param resultId Identifier of the current result set
	 * @param alg reference to the algorithm for this network
	 */
	public RunMCODE(int analyze, int resultId, MCODEParameterSet params, CyNetwork network) {
		this.analyze = analyze;
		this.resultId = resultId;
		this.network = network;
		this.alg = new MCODEAlgorithm(CyNetworkUtil.getNetworkName(network), params);
	}

	/**
	 * Run MCODE (Both score and find steps)
	 */
	public List<NodeCluster> run() {
		
		if (analyze == 1) {
			alg.scoreGraph(network, resultId);
			if (interrupted) {
				return null;
			}
		}
		
		List<NodeCluster> clusters = alg.findClusters(network, resultId);

		if (interrupted) {
			return null;
		}
		return clusters;
	}

	/**
	 * Non-blocking call to interrupt the task.
	 */
	public void cancel() {
		this.interrupted = true;
		alg.setCancelled(true);
	}

	/**
	 * Gets the Task Title.
	 *
	 * @return human readable task title.
	 */
	public String getTitle() {
		return new String("MCODE Network Cluster Detection");
	}
	
	public MCODEAlgorithm getAlg() {
		return alg;
	}
}
