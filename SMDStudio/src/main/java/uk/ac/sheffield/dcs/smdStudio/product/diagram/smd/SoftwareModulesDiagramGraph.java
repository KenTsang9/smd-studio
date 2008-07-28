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

package uk.ac.sheffield.dcs.smdStudio.product.diagram.smd;

import java.awt.geom.Point2D;

import uk.ac.sheffield.dcs.smdStudio.framework.diagram.AbstractGraph;
import uk.ac.sheffield.dcs.smdStudio.framework.diagram.Edge;
import uk.ac.sheffield.dcs.smdStudio.framework.diagram.Node;
import uk.ac.sheffield.dcs.smdStudio.product.diagram.common.NoteEdge;
import uk.ac.sheffield.dcs.smdStudio.product.diagram.common.NoteNode;

/**
 * A UML class diagram.
 */
@SuppressWarnings("serial")
public class SoftwareModulesDiagramGraph extends AbstractGraph {

	private static final Edge[] EDGE_PROTOTYPES = new Edge[2];

	private static final Node[] NODE_PROTOTYPES = new Node[3];

	static {
		NODE_PROTOTYPES[0] = new SimpleModuleNode();
		NODE_PROTOTYPES[1] = new ComplexModuleNode();
		NODE_PROTOTYPES[2] = new NoteNode();

		EDGE_PROTOTYPES[0] = new ModuleTransitionEdge();
		EDGE_PROTOTYPES[1] = new NoteEdge();
	}

	@Override
	public boolean addEdgeAtPoints(Edge e, Point2D p1, Point2D p2) {
		Node n1 = findNode(p1);
		Node n2 = findNode(p2);
		if (n1 == n2 || n1 == null || n2 == null) {
			return false;
		}
		if (e instanceof NoteEdge) {
			if (!(n1 instanceof NoteNode || n2 instanceof NoteNode)) {
				return false;
			}
		} else {
			if (n1.getParent() != n2.getParent()) {
				return false;
			}
			if (n1 instanceof ComplexModuleNode) {
				ComplexModuleNode node = (ComplexModuleNode) n1;
				if (node.getChildren().contains(n2)) {
					return false;
				}
			}
			if (n2 instanceof ComplexModuleNode) {
				ComplexModuleNode node = (ComplexModuleNode) n2;
				if (node.getChildren().contains(n1)) {
					return false;
				}
			}
		}
		return super.addEdgeAtPoints(e, p1, p2);
	}

	@Override
	public Edge[] getEdgePrototypes() {
		return EDGE_PROTOTYPES;
	}

	@Override
	public Node[] getNodePrototypes() {
		return NODE_PROTOTYPES;
	}

	@Override
	public void removeEdge(Edge e) {
		super.removeEdge(e);
		if (e instanceof ModuleTransitionEdge) {
			ModuleTransitionEdge edge = (ModuleTransitionEdge) e;
			edge.remove();
		}
	}

}
