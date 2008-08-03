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

package uk.ac.sheffield.dcs.smdStudio.framework.diagram;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyDescriptor;
import java.beans.Statement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jdom.Element;

import uk.ac.sheffield.dcs.smdStudio.framework.resources.XMLResourceBoundle;
import uk.ac.sheffield.dcs.smdStudio.framework.util.PropertyUtils;
import uk.ac.sheffield.dcs.smdStudio.product.diagram.common.GraphProperties;
import uk.ac.sheffield.dcs.smdStudio.product.diagram.common.NoteNode;
import uk.ac.sheffield.dcs.smdStudio.product.diagram.smd.ComplexModuleNode;
import uk.ac.sheffield.dcs.smdStudio.product.diagram.smd.SoftwareModuleDiagramObject;

/**
 * A graph consisting of selectable nodes and edges.
 */
@SuppressWarnings("serial")
public abstract class AbstractGraph implements Serializable, Cloneable, Graph {
	private ArrayList<Edge> edges;

	private transient ArrayList<Edge> edgesToBeRemoved;

	private transient ArrayList<GraphModificationListener> listeners = new ArrayList<GraphModificationListener>();

	private transient Rectangle2D minBounds;

	private ArrayList<Node> nodes;

	private transient ArrayList<Node> nodesToBeRemoved;

	private GraphProperties properties;

	private transient int recursiveRemoves;

	private static final XMLResourceBoundle RS = new XMLResourceBoundle(
			Graph.class);

	/**
	 * Adds a persistence delegate to a given encoder that encodes the child
	 * nodes of this node.
	 * 
	 * @param encoder
	 *            the encoder to which to add the delegate
	 */
	public static void setPersistenceDelegate(Encoder encoder) {
		encoder.setPersistenceDelegate(AbstractGraph.class,
				new DefaultPersistenceDelegate() {
					@Override
					protected void initialize(Class<?> type,
							Object oldInstance, Object newInstance, Encoder out) {
						super.initialize(type, oldInstance, newInstance, out);
						AbstractGraph g = (AbstractGraph) oldInstance;

						for (int i = 0; i < g.nodes.size(); i++) {
							Node n = g.nodes.get(i);
							Point2D p = n.getLocation();
							out.writeStatement(new Statement(oldInstance,
									"addNode", new Object[] { n, p }));
						}
						for (int i = 0; i < g.edges.size(); i++) {
							Edge e = g.edges.get(i);
							out.writeStatement(new Statement(oldInstance,
									"connect", new Object[] { e, e.getStart(),
											e.getEnd() }));
						}
					}
				});
	}

	/**
	 * Constructs a graph with no nodes or edges.
	 */
	public AbstractGraph() {
		nodes = new ArrayList<Node>();
		edges = new ArrayList<Edge>();
		nodesToBeRemoved = new ArrayList<Node>();
		edgesToBeRemoved = new ArrayList<Edge>();
		if (properties == null) {
			properties = new GraphProperties();
			addNode(properties, new Point2D.Double(0, 0));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.diagram.Graph#addEdgeAtPoints
	 * (uk.ac.sheffield.dcs.smdStudio.framework.diagram.Edge,
	 * java.awt.geom.Point2D, java.awt.geom.Point2D)
	 */
	public boolean addEdgeAtPoints(Edge e, Point2D p1, Point2D p2) {
		Node n1 = findNode(p1);
		Node n2 = findNode(p2);
		if (n1 != null) {
			e.connect(n1, n2);
			if (n1.checkAddEdge(e, p1, p2) && e.getEnd() != null) {
				if (!nodes.contains(e.getEnd())) {
					addNode(e.getEnd(), e.getEnd().getLocation());
				}
				edges.add(e);
				fireEdgeAdded(e, p1, p2);
				return true;
			}
		}
		return false;
	}

	@Override
	public Element getAsXMLElement() {
		Element element = new Element(RS.getElementName("element"));
		for (Node node : nodes) {
			if (node.getParent() == null && node instanceof ExportableAsXML) {
				ExportableAsXML xmlNode = (ExportableAsXML) node;
				element.addContent(xmlNode.getAsXMLElement());
			}
		}
		for (Edge edge : edges) {
			if (edge.getParent() == null && edge instanceof ExportableAsXML) {
				ExportableAsXML xmlEdge = (ExportableAsXML) edge;
				element.addContent(xmlEdge.getAsXMLElement());
			}
		}
		return element;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeuk.ac.sheffield.dcs.smdStudio.framework.diagram.Graph#
	 * addGraphModificationListener
	 * (uk.ac.sheffield.dcs.smdStudio.framework.diagram
	 * .GraphModificationListener)
	 */
	public void addGraphModificationListener(GraphModificationListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeuk.ac.sheffield.dcs.smdStudio.framework.diagram.Graph#
	 * addGraphModificationListener (java.util.List)
	 */
	public void addGraphModificationListener(List<GraphModificationListener> l) {
		synchronized (listeners) {
			listeners.addAll(l);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.diagram.IGraph#addNode(uk.ac.
	 * sheffield.dcs.smdStudio .framework.diagram.Node, java.awt.geom.Point2D)
	 */
	public boolean addNode(Node newNode, Point2D p) {

		// adding the properties node
		if (newNode instanceof GraphProperties) {
			if (properties != null) {
				nodes.remove(properties);
				newNode.setGraph(null);
			}
			properties = (GraphProperties) newNode;
		} else if (properties.contains(p)) {
			return false;
		}

		newNode.setGraph(this);
		newNode.translate(p.getX() - newNode.getLocation().getX(), p.getY()
				- newNode.getLocation().getY());

		if (newNode instanceof NoteNode) {
			nodes.add(newNode);
			fireNodeAdded(newNode, p);
			return true;
		}

		boolean accepted = false;
		boolean insideANode = false;
		int maxZ = 0;
		for (Node n : nodes) {
			if (n.getZ() > maxZ)
				maxZ = n.getZ();
		}
		for (int z = maxZ; !accepted && z >= 0; z--) {
			for (int i = 0; !accepted && i < nodes.size(); i++) {
				Node n = nodes.get(i);
				if (!n.equals(newNode) && n.getZ() == z && n.contains(p)) {
					insideANode = true;
					accepted = n.checkAddNode(newNode, p);
				}
			}
		}
		if (insideANode && !accepted)
			return false;
		nodes.add(newNode);
		fireNodeAdded(newNode, p);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.diagram.IGraph#attachChildNode
	 * (int, uk.ac.sheffield.dcs.smdStudio.framework.diagram.Node,
	 * uk.ac.sheffield.dcs.smdStudio.framework.diagram.Node)
	 */
	public void attachChildNode(int index, Node parentNode, Node childNode) {
		parentNode.addChild(index, childNode);
		fireChildAttached(index, parentNode, childNode);
	}

	@Override
	public double calculateTotalCost() {
		double cost = 0;
		for (Node node : nodes) {
			if (node instanceof SoftwareModuleDiagramObject
					&& !(node instanceof ComplexModuleNode)) {
				SoftwareModuleDiagramObject smd = (SoftwareModuleDiagramObject) node;
				cost += smd.getCost();
			}
		}
		for (Edge edge : edges) {
			if (edge instanceof SoftwareModuleDiagramObject) {
				SoftwareModuleDiagramObject smd = (SoftwareModuleDiagramObject) edge;
				cost += smd.getCost();
			}
		}
		return cost;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeuk.ac.sheffield.dcs.smdStudio.framework.diagram.IGraph#
	 * changeNodeOrEdgeProperty (java.beans.PropertyChangeEvent)
	 */
	public void changeNodeOrEdgeProperty(PropertyChangeEvent e) {
		PropertyUtils.setProperty(e.getSource(), e.getPropertyName(), e
				.getNewValue());
		firePropertyChangeOnNodeOrEdge(e);
	}

	public void changeTeamQuality(double teamQuality) {
		this.properties.setTeamQuality(teamQuality);
		fireGraphPropertiesChanged();
	}

	public void changeTrainingCost(double trainingCost) {
		this.properties.setTrainingCost(trainingCost);
		fireGraphPropertiesChanged();
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

	@SuppressWarnings("unchecked")
	private List<GraphModificationListener> cloneListeners() {
		synchronized (listeners) {
			return (List<GraphModificationListener>) listeners.clone();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.diagram.IGraph#connect(uk.ac.
	 * sheffield.dcs.smdStudio .framework.diagram.Edge,
	 * uk.ac.sheffield.dcs.smdStudio.framework.diagram.Node,
	 * uk.ac.sheffield.dcs.smdStudio.framework.diagram.Node)
	 */
	public void connect(Edge e, Node start, Node end) {
		// Re-attaches nodes to list
		if (!nodes.contains(start))
			addNode(start, start.getLocation());
		if (!nodes.contains(end))
			addNode(end, start.getLocation());
		// Registers nodes to edge
		e.connect(start, end);
		edges.add(e);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.diagram.IGraph#connect(uk.ac.
	 * sheffield.dcs.smdStudio .framework.diagram.Edge,
	 * uk.ac.sheffield.dcs.smdStudio.framework.diagram.Node,
	 * java.awt.geom.Point2D,
	 * uk.ac.sheffield.dcs.smdStudio.framework.diagram.Node,
	 * java.awt.geom.Point2D)
	 */
	public void connect(Edge e, Node aStart, Point2D sPoint, Node anEnd,
			Point2D ePoint) {
		connect(e, aStart, anEnd);
	}

	/**
	 * @see uk.ac.sheffield.dcs.smdStudio.framework.diagram.Graph#detachChildNode(int,
	 *      uk.ac.sheffield.dcs.smdStudio.framework.diagram.Node,
	 *      uk.ac.sheffield.dcs.smdStudio.framework.diagram.Node)
	 */
	public void detachChildNode(int index, Node parentNode, Node childNode) {
		parentNode.removeChild(childNode);
		fireChildDetached(index, parentNode, childNode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.diagram.IGraph#draw(java.awt.
	 * Graphics2D, uk.ac.sheffield.dcs.smdStudio.framework.diagram.Grid)
	 */
	public void draw(Graphics2D g2, Grid g) {
		List<Node> specialNodes = new ArrayList<Node>();

		int count = 0;
		int z = 0;
		while (count < nodes.size()) {
			for (Node n : nodes) {

				if (n.getZ() == z) {
					if (n instanceof NoteNode) {
						specialNodes.add(n);
					} else {
						n.draw(g2);
					}
					count++;
				}
			}
			z++;
		}

		for (int i = 0; i < edges.size(); i++) {
			Edge e = edges.get(i);
			e.draw(g2);
		}
		// Special nodes are always drawn upon other elements
		for (Node n : specialNodes) {
			n.draw(g2);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.diagram.Graph#findEdge(uk.ac.
	 * sheffield.dcs.smdStudio .framework.diagram.Id)
	 */
	public Edge findEdge(Id id) {
		for (Edge e : edges) {
			if (e.getId().equals(id))
				return e;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.diagram.IGraph#findEdge(java.
	 * awt.geom. Point2D)
	 */
	public Edge findEdge(Point2D p) {
		for (Edge e : edges) {
			if (e.contains(p))
				return e;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.diagram.Graph#findNode(uk.ac.
	 * sheffield.dcs.smdStudio .framework.diagram.Id)
	 */
	public Node findNode(Id id) {
		for (Node n : nodes) {
			if (n.getId().equals(id))
				return n;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.diagram.IGraph#findNode(java.
	 * awt.geom. Point2D)
	 */
	public Node findNode(Point2D p) {
		int maxZ = 0;
		for (Node n : nodes) {
			if (n.getZ() > maxZ)
				maxZ = n.getZ();
		}
		for (int z = maxZ; z >= 0; z--) {
			for (Node n : nodes) {
				if (n.getZ() == z && n.contains(p))
					return n;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.diagram.IGraph#fireChildAttached
	 * (int, uk.ac.sheffield.dcs.smdStudio.framework.diagram.Node,
	 * uk.ac.sheffield.dcs.smdStudio.framework.diagram.Node)
	 */
	public void fireChildAttached(int index, Node p, Node c) {
		for (GraphModificationListener listener : cloneListeners())
			listener.childAttached(this, index, p, c);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.diagram.IGraph#fireChildDetached
	 * (int, uk.ac.sheffield.dcs.smdStudio.framework.diagram.Node,
	 * uk.ac.sheffield.dcs.smdStudio.framework.diagram.Node)
	 */
	public void fireChildDetached(int index, Node p, Node c) {
		for (GraphModificationListener listener : cloneListeners())
			listener.childDetached(this, index, p, c);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.diagram.Graph#fireEdgeAdded(com
	 * .horstmann .violet.framework.diagram.Edge, java.awt.geom.Point2D,
	 * java.awt.geom.Point2D)
	 */
	public void fireEdgeAdded(Edge e, Point2D startPoint, Point2D endPoint) {
		for (GraphModificationListener listener : cloneListeners())
			listener.edgeAdded(this, e, startPoint, endPoint);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.diagram.IGraph#fireEdgeRemoved
	 * (com.horstmann .violet.framework.diagram.Edge)
	 */
	public void fireEdgeRemoved(Edge e) {
		for (GraphModificationListener listener : cloneListeners())
			listener.edgeRemoved(this, e);
	}

	public void fireGraphPropertiesChanged() {
		for (GraphModificationListener listener : cloneListeners())
			listener.graphPropertiesChanged(this, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.diagram.IGraph#fireNodeAdded(
	 * com.horstmann .violet.framework.diagram.Node)
	 */
	public void fireNodeAdded(Node n, Point2D location) {
		for (GraphModificationListener listener : cloneListeners())
			listener.nodeAdded(this, n, location);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.diagram.IGraph#fireNodeMoved(
	 * com.horstmann .violet.framework.diagram.Node, double, double)
	 */
	public void fireNodeMoved(Node node, double dx, double dy) {
		if (dx == 0 && dy == 0)
			return;
		for (GraphModificationListener listener : cloneListeners())
			listener.nodeMoved(this, node, dx, dy);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.diagram.IGraph#fireNodeRemoved
	 * (com.horstmann .violet.framework.diagram.Node)
	 */
	public void fireNodeRemoved(Node n) {
		for (GraphModificationListener listener : cloneListeners())
			listener.nodeRemoved(this, n);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeuk.ac.sheffield.dcs.smdStudio.framework.diagram.IGraph#
	 * firePropertyChangeOnNodeOrEdge (java.beans.PropertyChangeEvent)
	 */
	public void firePropertyChangeOnNodeOrEdge(PropertyChangeEvent event) {
		if (event.getOldValue() == event.getNewValue())
			return;
		for (GraphModificationListener listener : cloneListeners())
			listener.propertyChangedOnNodeOrEdge(this, event);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.diagram.IGraph#getBounds(java
	 * .awt.Graphics2D )
	 */
	public Rectangle2D getBounds(Graphics2D g2) {
		Rectangle2D r = minBounds;
		for (Node n : nodes) {
			Rectangle2D b = n.getBounds();
			if (r == null)
				r = b;
			else
				r.add(b);
		}
		for (Edge e : edges) {
			r.add(e.getBounds(g2));
		}
		return r == null ? new Rectangle2D.Double() : new Rectangle2D.Double(r
				.getX(), r.getY(), r.getWidth() + AbstractNode.SHADOW_GAP, r
				.getHeight()
				+ AbstractNode.SHADOW_GAP);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.diagram.IGraph#getEdgePrototypes
	 * ()
	 */
	public abstract Edge[] getEdgePrototypes();

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.sheffield.dcs.smdStudio.framework.diagram.IGraph#getEdges()
	 */
	public Collection<Edge> getEdges() {
		return Collections.unmodifiableCollection(edges);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeuk.ac.sheffield.dcs.smdStudio.framework.diagram.Graph#
	 * getGraphModificationListener ()
	 */
	public List<GraphModificationListener> getGraphModificationListener() {
		synchronized (listeners) {
			return listeners;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.diagram.IGraph#getNodePrototypes
	 * ()
	 */
	public abstract Node[] getNodePrototypes();

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.sheffield.dcs.smdStudio.framework.diagram.IGraph#getNodes()
	 */
	public Collection<Node> getNodes() {
		return Collections.unmodifiableCollection(nodes);
	}

	public GraphProperties getProperties() {
		return properties;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.diagram.IGraph#layout(java.awt
	 * .Graphics2D, uk.ac.sheffield.dcs.smdStudio.framework.diagram.Grid)
	 */
	public void layout(Graphics2D g2, Grid gr) {
		for (Node n : nodes) {
			if (n.getParent() == null) // parents lay out their children
				n.layout(g2, gr);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.diagram.Graph#moveNode(uk.ac.
	 * sheffield.dcs.smdStudio .framework.diagram.Node, java.awt.geom.Point2D)
	 */
	public void moveNode(Node existingNode, Point2D dest) {
		existingNode.translate(dest.getX(), dest.getY());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.diagram.IGraph#removeEdge(com
	 * .horstmann .violet.framework.diagram.Edge)
	 */
	public void removeEdge(Edge e) {
		edges.remove(e);
		fireEdgeRemoved(e);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeuk.ac.sheffield.dcs.smdStudio.framework.diagram.IGraph#
	 * removeGraphModificationListener
	 * (uk.ac.sheffield.dcs.smdStudio.framework.diagram
	 * .GraphModificationListener)
	 */
	public synchronized void removeGraphModificationListener(
			GraphModificationListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.diagram.IGraph#removeNode(com
	 * .horstmann .violet.framework.diagram.Node)
	 */
	public void removeNode(Node n) {
		// graph properties should not be removed!
		if (!(n instanceof GraphProperties)) {
			Node p = n.getParent();
			if (p != null)
				p.removeChild(n);
			nodes.remove(n);
			n.setGraph(null);
			fireNodeRemoved(n);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.diagram.IGraph#removeNodesAndEdges
	 * (java .util.Collection, java.util.Collection)
	 */
	public void removeNodesAndEdges(Collection<? extends Node> nodesToRemove,
			Collection<? extends Edge> edgesToRemove) {
		recursiveRemoves++;

		// Notify all nodes of removals. This might trigger recursive
		// invocations.

		if (nodesToRemove != null) {
			for (Node n : nodesToRemove) {
				if (!nodesToBeRemoved.contains(n)) {
					for (Node n2 : nodes) {
						n2.checkRemoveNode(n);
					}
					nodesToBeRemoved.add(n);
				}
			}
		}
		if (edgesToRemove != null) {
			for (Edge e : edgesToRemove) {
				if (!edgesToBeRemoved.contains(e)) {
					for (Node n1 : nodes) {
						n1.checkRemoveEdge(e);
					}
					edgesToBeRemoved.add(e);
				}
			}
		}

		recursiveRemoves--;
		if (recursiveRemoves > 0)
			return;

		for (Edge e : edges) {
			if (!edgesToBeRemoved.contains(e)
					&& (nodesToBeRemoved.contains(e.getStart()) || nodesToBeRemoved
							.contains(e.getEnd())))
				edgesToBeRemoved.add(e);
		}
		// edges.removeAll(edgesToBeRemoved);
		// for (Edge e : edgesToBeRemoved)
		// fireEdgeRemoved(e);
		for (Edge e : edgesToBeRemoved)
			removeEdge(e);
		edgesToBeRemoved.clear();

		// Traverse all nodes other than the ones to be removed and make sure
		// that none
		// of their node-valued properties fall into the set of removed nodes.
		// (Null out if necessary.)

		for (Node n : nodes) {
			if (!nodesToBeRemoved.contains(n)) {
				try {
					for (PropertyDescriptor descriptor : Introspector
							.getBeanInfo(n.getClass()).getPropertyDescriptors()) {
						if (Node.class.isAssignableFrom(descriptor
								.getPropertyType())) {
							Node value = (Node) descriptor.getReadMethod()
									.invoke(n);
							if (nodesToBeRemoved.contains(value))
								descriptor.getWriteMethod().invoke(n,
										(Object) null);
							PropertyChangeEvent event = new PropertyChangeEvent(
									n, descriptor.getName(), value, null);
							firePropertyChangeOnNodeOrEdge(event);
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}

		for (Node n : nodesToBeRemoved) {
			removeNode(n);
		}
		// for (Node n : nodesToBeRemoved) {
		// for (int i = n.getChildren().size() - 1; i >= 0; i--)
		// n.removeChild(n.getChildren().get(i));
		// }
		// for (Node n : nodesToBeRemoved) {
		// if (n.getParent() != null)
		// n.getParent().removeChild(n);
		// n.setGraph(null);
		// }
		// nodes.removeAll(nodesToBeRemoved);
		// for (Node n : nodesToBeRemoved) {
		// fireNodeRemoved(n);
		// }
		nodesToBeRemoved.clear();
	}

	@Override
	public void repaint() {
		for (GraphModificationListener listener : cloneListeners())
			listener.repaintGraph();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.diagram.IGraph#setMinBounds(java
	 * .awt.geom .Rectangle2D)
	 */
	public void setMinBounds(Rectangle2D newValue) {
		minBounds = newValue;
	}

}
