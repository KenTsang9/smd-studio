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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.PersistenceDelegate;
import java.beans.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.UIManager;

/**
 * A class that supplies convenience implementations for a number of methods in
 * the Node interface
 * 
 * @author Cay Horstmann
 */
@SuppressWarnings("serial")
public abstract class AbstractNode implements Node {
	/**
	 * Constructs a node with no parents or children at location (0, 0).
	 */
	public AbstractNode() {
		children = new ArrayList<Node>();
		parent = null;
		this.id = new Id();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.sheffield.dcs.smdStudio.framework.diagram.Node#getLocation()
	 */
	public Point2D getLocation() {
		// Legacy grief--some versions of the XML encoder wrote calls to
		// setBounds
		// We use the location set by setBounds until the first call to
		// translate.
		if (location == null)
			return new Point2D.Double(getBounds().getX(), getBounds().getY());
		return location;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.sheffield.dcs.smdStudio.framework.diagram.Node#getId()
	 */
	public Id getId() {
		return this.id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.diagram.Node#setId(uk.ac.sheffield.dcs.smdStudio
	 * .framework.diagram.Id)
	 */
	public void setId(Id id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.sheffield.dcs.smdStudio.framework.diagram.Node#getRevision()
	 */
	public Integer getRevision() {
		return this.revision;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.diagram.Node#setRevision(java.lang.Integer
	 * )
	 */
	public void setRevision(Integer newRevisionNumber) {
		this.revision = newRevisionNumber;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.sheffield.dcs.smdStudio.framework.diagram.Node#incrementRevision()
	 */
	public void incrementRevision() {
		int i = this.revision.intValue();
		i++;
		this.revision = new Integer(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.sheffield.dcs.smdStudio.framework.Node#translate(double, double) Note
	 * that we don't translate the children since that is not always appropriate
	 * and hard to un-inherit.
	 */
	public void translate(double dx, double dy) {
		// Legacy grief--some versions of the XML encoder wrote calls to
		// setBounds
		// We use the location set by setBounds until the first call to
		// translate.
		if (location == null)
			location = (Point2D.Double) getLocation();

		location.x += dx;
		location.y += dy;

		if (graph != null && graph.getNodes().contains(this))
			graph.fireNodeMoved(this, dx, dy);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.Node#addEdge(uk.ac.sheffield.dcs.smdStudio.framework
	 * .Edge, java.awt.geom.Point2D, java.awt.geom.Point2D)
	 */
	public boolean checkAddEdge(Edge e, Point2D p1, Point2D p2) {
		return e.getEnd() != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.Node#removeEdge(uk.ac.sheffield.dcs.smdStudio.framework
	 * .Graph, uk.ac.sheffield.dcs.smdStudio.framework.Edge)
	 */
	public void checkRemoveEdge(Edge e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.Node#removeNode(uk.ac.sheffield.dcs.smdStudio.framework
	 * .Graph, uk.ac.sheffield.dcs.smdStudio.framework.Node)
	 */
	public void checkRemoveNode(Node e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.Node#layout(uk.ac.sheffield.dcs.smdStudio.framework
	 * .Graph, java.awt.Graphics2D, uk.ac.sheffield.dcs.smdStudio.framework.Grid)
	 */
	public void layout(Graphics2D g2, Grid grid) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.Node#addNode(uk.ac.sheffield.dcs.smdStudio.framework
	 * .Node, java.awt.geom.Point2D)
	 */
	public boolean checkAddNode(Node n, Point2D p) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.sheffield.dcs.smdStudio.framework.Node#getAncestors()
	 */
	public List<Node> getAncestors() {
		List<Node> result = new ArrayList<Node>();
		Node parent = this.getParent();
		while (parent != null) {
			result.add(parent);
			parent = parent.getParent();
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.sheffield.dcs.smdStudio.framework.Node#getParent()
	 */
	public Node getParent() {
		return parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.Node#setParent(uk.ac.sheffield.dcs.smdStudio.framework
	 * .Node)
	 */
	public void setParent(Node node) {
		parent = node;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.sheffield.dcs.smdStudio.framework.diagram.Node#getChildren()
	 */
	public List<Node> getChildren() {
		return children;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.diagram.Node#setChildren(java.util.List)
	 */
	public void setChildren(List<Node> children) {
		this.children = new ArrayList<Node>(children);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.sheffield.dcs.smdStudio.framework.Node#addChild(int,
	 * uk.ac.sheffield.dcs.smdStudio.framework.Node)
	 */
	public void addChild(int index, Node node) {
		Node oldParent = node.getParent();
		if (oldParent != null)
			oldParent.removeChild(node);
		children.add(index, node);
		if (node instanceof AbstractNode)
			((AbstractNode) node).setParent(this);
		if (graph != null)
			graph.fireChildAttached(index, this, node);
	}

	/**
	 * Called from decoder
	 * 
	 * @param node
	 */
	public void addChild(Node node) {
		addChild(children.size(), node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.Node#removeChild(uk.ac.sheffield.dcs.smdStudio.
	 * framework.Node)
	 */
	public void removeChild(Node node) {
		if (node.getParent() != this)
			return;
		int i = children.indexOf(node);
		if (i >= 0) {
			children.remove(i);
			if (node instanceof AbstractNode)
				((AbstractNode) node).setParent(null);
			if (graph != null)
				graph.fireChildDetached(i, this, node);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.sheffield.dcs.smdStudio.framework.Node#draw(java.awt.Graphics2D)
	 */
	public void draw(Graphics2D g2) {
		Shape shape = getShape();
		if (shape == null)
			return;
		/*
		 * Area shadow = new Area(shape);
		 * shadow.transform(AffineTransform.getTranslateInstance(SHADOW_GAP,
		 * SHADOW_GAP)); shadow.subtract(new Area(shape));
		 */
		Color oldColor = g2.getColor();
		g2.translate(SHADOW_GAP, SHADOW_GAP);
		g2.setColor(SHADOW_COLOR);
		g2.fill(shape);
		g2.translate(-SHADOW_GAP, -SHADOW_GAP);
		g2.setColor(BACKGROUND_COLOR);
		g2.fill(shape);
		g2.setColor(oldColor);
	}

	/**
	 * @return the shape to be used for computing the drop shadow
	 */
	public Shape getShape() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.diagram.Node#checkPasteChildren(java.util
	 * .Collection)
	 */
	public boolean checkPasteChildren(Collection<Node> children) {
		return false;
	}

	/**
	 * Adds a persistence delegate to a given encoder that encodes the child
	 * nodes of this node.
	 * 
	 * @param encoder
	 *            the encoder to which to add the delegate
	 * @param writeId
	 *            indicates that the edge's id should be included in the
	 *            persistance process
	 */
	public static void setPersistenceDelegate(Encoder encoder,
			final boolean writeId) {
		PersistenceDelegate oldInstance = encoder
				.getPersistenceDelegate(AbstractNode.class);
		encoder.remove(oldInstance);
		encoder.setPersistenceDelegate(AbstractNode.class,
				new DefaultPersistenceDelegate() {
					protected void initialize(Class<?> type,
							Object oldInstance, Object newInstance, Encoder out) {
						super.initialize(type, oldInstance, newInstance, out);
						Node n = (Node) oldInstance;
						List<Node> children = n.getChildren();
						for (int i = 0; i < children.size(); i++) {
							Node c = (Node) children.get(i);
							out.writeStatement(new Statement(oldInstance,
									"addChild", new Object[] { c }));
						}
						if (writeId) {
							Id id = n.getId();
							out.writeStatement(new Statement(oldInstance,
									"setId", new Object[] { id }));
						}
					}
				});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	public AbstractNode clone() {
		try {
			AbstractNode cloned = (AbstractNode) super.clone();
			cloned.id = new Id();
			cloned.children = new ArrayList<Node>();
			cloned.location = (Point2D.Double) getLocation().clone();
			/*
			 * for (Node child : children) { Node clonedChild = child.clone();
			 * cloned.children.add(clonedChild); clonedChild.setParent(cloned);
			 * }
			 */
			cloned.graph = null;
			return cloned;
		} catch (CloneNotSupportedException exception) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.diagram.Node#setGraph(uk.ac.sheffield.dcs.smdStudio
	 * .framework.diagram.Graph)
	 */
	public void setGraph(Graph g) {
		graph = g;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.sheffield.dcs.smdStudio.framework.diagram.Node#getGraph()
	 */
	public Graph getGraph() {
		return graph;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.sheffield.dcs.smdStudio.framework.diagram.Node#getZ()
	 */
	public int getZ() {
		return z;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.sheffield.dcs.smdStudio.framework.diagram.Node#setZ(int)
	 */
	public void setZ(int z) {
		this.z = z;
	}

	private static final Color SHADOW_COLOR = Color.LIGHT_GRAY;
	protected static Color BACKGROUND_COLOR = UIManager
			.getColor("TextPane.background");
	public static final int SHADOW_GAP = 4;

	private ArrayList<Node> children;
	private Node parent;
	private Graph graph;
	private Point2D.Double location;
	private transient int z;

	/** Node's current id (unique in all the graph) */
	protected Id id;

	/** Node's current revision */
	protected Integer revision = new Integer(0);
}
