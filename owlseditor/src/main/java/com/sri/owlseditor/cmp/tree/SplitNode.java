/*****************************************************************************************
"The contents of this file are subject to the Mozilla Public License  Version 1.1 
(the "License"); you may not use this file except in compliance with the License.  
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, 
WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License for the specific 
language governing rights and limitations under the License.

The Original Code is OWL-S Editor for Protege.

The Initial Developer of the Original Code is SRI International. 
Portions created by the Initial Developer are Copyright (C) 2004 the Initial Developer.  
All Rights Reserved.
 ******************************************************************************************/
package com.sri.owlseditor.cmp.tree;

import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashSet;

import com.sri.owlseditor.cmp.graph.GraphNodeInfo;
import com.sri.owlseditor.cmp.graph.GraphProcessModel;
import com.sri.owlseditor.cmp.graph.UniqueName;
import com.sri.owlseditor.util.OWLSList;

public class SplitNode extends BagOrTreeNode {
	public SplitNode(OWLSTreeNodeInfo ni) {
		super(ni, OWLSList.CC_BAG);
	}

	public GraphNodeInfo graph(HashSet nameSet, PrintWriter pw,
			int clusterNumber, OWLSTreeNode selectedNode) {
		UniqueName controlConstructName = new UniqueName(getInstance()
				.getName(), nameSet);
		int newClusterNumber = clusterNumber;
		Integer clusterInt = new Integer(newClusterNumber++);

		pw.println("subgraph " + clusterInt.toString() + " {");

		// If this node is the selected one, 'highlight' it.
		if (this.equals(selectedNode)) {
			pw.println("node [fillcolor="
					+ GraphProcessModel.SELECTED_NODE_FILL_COLOR + ", color="
					+ GraphProcessModel.SELECTED_NODE_EDGE_COLOR + "];");
			pw.println("edge [color=" + GraphProcessModel.SELECTED_EDGE_COLOR
					+ "];");
		}

		// Create the split node
		UniqueName splitNodeName = new UniqueName("splitNode", nameSet);
		pw.println(splitNodeName.getUniqueName()
				+ " [shape=point, label=\"\"];");

		Enumeration e = children();
		if (!e.hasMoreElements()) {
			// nothing inside this construct
			pw.println("}");
			return new GraphNodeInfo(splitNodeName.getUniqueName(),
					splitNodeName.getUniqueName(), "", "", newClusterNumber);
		}

		GraphNodeInfo thisPair = null;
		while (e.hasMoreElements()) {
			OWLSTreeNode child = (OWLSTreeNode) e.nextElement();
			thisPair = child.graph(nameSet, pw, newClusterNumber++,
					selectedNode);
			newClusterNumber = thisPair.clusterNumber;
			// connect to split node
			pw.println(splitNodeName.getUniqueName() + "->"
					+ thisPair.firstNode + thisPair.createInEdgeAttr(""));
		}

		pw.println("}");
		return new GraphNodeInfo(splitNodeName.getUniqueName(),
				splitNodeName.getUniqueName(), "", "", newClusterNumber);
	}

	public String toString() {
		return "Split";
	}

}
