/*
 Violet - A program for editing UML diagrams.

 Copyright (C) 2007 Cay S. Horstmann (http://horstmann.com)
 Alexandre de Pellegrin (http://alexdp.free.fr);

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package uk.ac.sheffield.dcs.smdStudio.product.diagram.state;

import uk.ac.sheffield.dcs.smdStudio.framework.diagram.AbstractGraph;
import uk.ac.sheffield.dcs.smdStudio.framework.diagram.Edge;
import uk.ac.sheffield.dcs.smdStudio.framework.diagram.Node;
import uk.ac.sheffield.dcs.smdStudio.product.diagram.common.DiagramLinkNode;
import uk.ac.sheffield.dcs.smdStudio.product.diagram.common.NoteEdge;
import uk.ac.sheffield.dcs.smdStudio.product.diagram.common.NoteNode;

/**
 * An UML state diagram.
 */
@SuppressWarnings("serial")
public class StateDiagramGraph extends AbstractGraph {
	public Node[] getNodePrototypes() {
		return NODE_PROTOTYPES;
	}

	public Edge[] getEdgePrototypes() {
		return EDGE_PROTOTYPES;
	}

	private static final Node[] NODE_PROTOTYPES = new Node[5];

	private static final Edge[] EDGE_PROTOTYPES = new Edge[2];

	static {
		NODE_PROTOTYPES[0] = new StateNode();
		NODE_PROTOTYPES[1] = new CircularInitialStateNode();
		NODE_PROTOTYPES[2] = new CircularFinalStateNode();
		NODE_PROTOTYPES[3] = new NoteNode();
		NODE_PROTOTYPES[4] = new DiagramLinkNode();
		EDGE_PROTOTYPES[0] = new StateTransitionEdge();
		EDGE_PROTOTYPES[1] = new NoteEdge();
	}

}