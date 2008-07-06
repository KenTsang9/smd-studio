package uk.ac.sheffield.dcs.smdStudio.framework.gui;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.sheffield.dcs.smdStudio.framework.diagram.AbstractGraph;
import uk.ac.sheffield.dcs.smdStudio.framework.diagram.Edge;
import uk.ac.sheffield.dcs.smdStudio.framework.diagram.Graph;
import uk.ac.sheffield.dcs.smdStudio.framework.diagram.Node;


@SuppressWarnings("serial")
public class Clipboard extends AbstractGraph {
	public void copyIn(Graph g, List<Node> selectedNodes) {
		if (selectedNodes.size() == 0)
			return;

		Rectangle2D bounds = null;
		for (Node n : selectedNodes) {
			if (bounds == null)
				bounds = n.getBounds();
			else
				bounds.add(n.getBounds());
		}

		// form transitive closure over nodes, including children and
		// node-valued properties
		Set<Node> includedNodes = new HashSet<Node>();
		for (Node n : selectedNodes)
			addDependents(n, includedNodes);

		// empty old contents
		List<Edge> edges = new ArrayList<Edge>(getEdges());
		for (Edge e : edges)
			removeEdge(e);
		List<Node> nodes = new ArrayList<Node>(getNodes());
		for (Node n : nodes)
			removeNode(n);

		copyStructure(g, includedNodes, this, null, -bounds.getX(), -bounds
				.getY());
	}

	private static void addDependents(Node n, Set<Node> dependents) {
		if (dependents.contains(n))
			return;
		dependents.add(n);
		for (Node c : n.getChildren())
			addDependents(c, dependents);
		try {
			/*
			 * Enumerate all Node-valued properties
			 */
			for (PropertyDescriptor descriptor : Introspector.getBeanInfo(
					n.getClass()).getPropertyDescriptors()) {
				if (Node.class.isAssignableFrom(descriptor.getPropertyType())) {
					Node value = (Node) descriptor.getReadMethod().invoke(n);
					addDependents(value, dependents);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public Collection<Node> pasteOut(Graph g, Node selectedNode) {
		return copyStructure(this, getNodes(), g, selectedNode, 0, 0);
	}

	private static Collection<Node> copyStructure(Graph graphIn,
			Collection<Node> selectedIn, Graph graphOut, Node selectedOut,
			double dx, double dy) {
		/*
		 * Make sure target graph can receive node types.
		 */
		Node[] nodeProtos = graphOut.getNodePrototypes();
		if (nodeProtos != null) {
			Set<Class<? extends Node>> nodeClasses = new HashSet<Class<? extends Node>>();
			for (Node n : nodeProtos)
				nodeClasses.add(n.getClass());
			for (Node n : selectedIn) {
				if (!nodeClasses.contains(n.getClass()))
					return null;
			}
		}

		/*
		 * Clone all nodes and remember the original-cloned correspondence
		 */
		Map<Node, Node> originalAndClonedNodes = new LinkedHashMap<Node, Node>();
		for (Node n : selectedIn) {
			Node n2 = n.clone();
			originalAndClonedNodes.put(n, n2);
		}

		/*
		 * Clone all edges that join copied nodes
		 */
		List<Edge> newEdges = new ArrayList<Edge>();
		for (Edge e : graphIn.getEdges()) {
			Node start = null;
			Node end = null;
			if ((start = originalAndClonedNodes.get(e.getStart())) != null
					&& (end = originalAndClonedNodes.get(e.getEnd())) != null) {
				Edge e2 = e.clone();
				e2.connect(start, end);
				newEdges.add(e2);
			}
		}

		/*
		 * Make sure target graph can receive edge types.
		 */
		Edge[] edgeProtos = graphOut.getEdgePrototypes();
		if (edgeProtos != null) {
			Set<Class<? extends Edge>> edgeClasses = new HashSet<Class<? extends Edge>>();
			for (Edge e : edgeProtos)
				edgeClasses.add(e.getClass());
			for (Edge e : newEdges) {
				if (!edgeClasses.contains(e.getClass()))
					return null;
			}
		}

		/*
		 * Add nodes to target.
		 */
		for (Node n : originalAndClonedNodes.values()) {
			Point2D location = n.getLocation();
			Point2D p = new Point2D.Double(location.getX() + dx, location
					.getY()
					+ dy);
			graphOut.addNode(n, p);
		}

		/*
		 * Add edges to target.
		 */
		for (Edge e : newEdges)
			graphOut.connect(e, e.getStart(), e.getEnd());

		if (selectedOut != null)
			selectedOut.checkPasteChildren(originalAndClonedNodes.values());

		for (Node n : selectedIn) {
			for (Node child : n.getChildren()) {
				Node child2 = originalAndClonedNodes.get(child);
				if (child2 != null) {
					Node n2 = originalAndClonedNodes.get(n);
					n2.addChild(n2.getChildren().size(), child2);
				}
			}
			try {
				/*
				 * Establish all Node-valued properties on clone
				 */
				for (PropertyDescriptor descriptor : Introspector.getBeanInfo(
						n.getClass()).getPropertyDescriptors()) {
					if (Node.class.isAssignableFrom(descriptor
							.getPropertyType())) {
						Node value = (Node) descriptor.getReadMethod()
								.invoke(n);
						Node value2 = originalAndClonedNodes.get(value);
						if (value2 != null) {
							Node n2 = originalAndClonedNodes.get(n);
							descriptor.getWriteMethod().invoke(n2, value2);
						}
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return originalAndClonedNodes.values();
	}

	@Override
	public Node[] getNodePrototypes() {
		return null;
	}

	@Override
	public Edge[] getEdgePrototypes() {
		return null;
	}
}
