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

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JLabel;

import org.jdom.Element;

import uk.ac.sheffield.dcs.smdStudio.framework.diagram.Edge;
import uk.ac.sheffield.dcs.smdStudio.framework.diagram.ExportableAsXML;
import uk.ac.sheffield.dcs.smdStudio.framework.diagram.Grid;
import uk.ac.sheffield.dcs.smdStudio.framework.diagram.MultiLineString;
import uk.ac.sheffield.dcs.smdStudio.framework.diagram.Node;
import uk.ac.sheffield.dcs.smdStudio.framework.diagram.RectangularNode;
import uk.ac.sheffield.dcs.smdStudio.framework.resources.XMLResourceBoundle;
import uk.ac.sheffield.dcs.smdStudio.framework.util.GeometryUtils;
import uk.ac.sheffield.dcs.smdStudio.product.diagram.common.NoteNode;

/**
 * A package node in a UML diagram.
 */
@SuppressWarnings("serial")
public class ComplexModuleNode extends RectangularNode implements
		SoftwareModuleDiagramObject, ExportableAsXML {

	private static final XMLResourceBoundle RS = new XMLResourceBoundle(
			ComplexModuleNode.class);

	private static int DEFAULT_HEIGHT = 100;

	private static int DEFAULT_COMPARTMENT_HEIGHT = 20;

	private static int DEFAULT_TOP_WIDTH = 60;

	private static int DEFAULT_WIDTH = 100;

	private static final int DEFAULT_XGAP = 50;

	private static final int DEFAULT_YGAP = 50;

	private static JLabel nameLabel = new JLabel();

	private static JLabel costLabel = new JLabel();

	private static final int NAME_GAP = 3;

	private transient Rectangle2D bot;

	private MultiLineString description;

	private String name;

	private transient Rectangle2D mid;

	private transient Rectangle2D top;

	private Set<ModuleTransitionEdge> edges;

	private int smdId;

	public int getSMDId() {
		return smdId;
	}

	public void setSMDId(int id) {
		smdId = id;
	}

	/**
	 * Construct a package node with a default size
	 */
	public ComplexModuleNode() {
		edges = new HashSet<ModuleTransitionEdge>();
		name = "";
		description = new MultiLineString();
		description.setJustification(MultiLineString.LEFT);
		setBounds(new Rectangle2D.Double(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT));
		top = new Rectangle2D.Double(0, 0, DEFAULT_TOP_WIDTH,
				DEFAULT_COMPARTMENT_HEIGHT);
		mid = new Rectangle2D.Double(0, DEFAULT_COMPARTMENT_HEIGHT,
				DEFAULT_WIDTH, DEFAULT_COMPARTMENT_HEIGHT);
		bot = new Rectangle2D.Double(0, DEFAULT_COMPARTMENT_HEIGHT * 2,
				DEFAULT_WIDTH, DEFAULT_HEIGHT - 2 * DEFAULT_COMPARTMENT_HEIGHT);
	}

	public boolean checkAddNode(Node n, Point2D p) {
		if (n instanceof SimpleModuleNode || n instanceof ComplexModuleNode) {
			addChild(getChildren().size(), n);
			return true;
		} else
			return n instanceof NoteNode;
	}

	@Override
	public boolean checkPasteChildren(Collection<Node> children) {
		for (Node n : children) {
			if (!(n instanceof SimpleModuleNode || n instanceof ComplexModuleNode))
				return false;
		}
		for (Node n : children) {
			final int GAP = 6;
			n.translate(bot.getX() + GAP, bot.getY() + GAP);
			addChild(getChildren().size(), n);
		}
		return true;
	}

	public ComplexModuleNode clone() {
		ComplexModuleNode cloned = (ComplexModuleNode) super.clone();
		cloned.description = description.clone();
		cloned.top = (Rectangle2D) top.clone();
		cloned.mid = (Rectangle2D) mid.clone();
		cloned.bot = (Rectangle2D) bot.clone();
		cloned.edges = new HashSet<ModuleTransitionEdge>();
		cloned.edges.addAll(edges);
		return cloned;
	}

	public void draw(Graphics2D g2) {
		super.draw(g2);
		Rectangle2D bounds = getBounds();

		nameLabel.setText("<html><b>" + name + "</b></html>");
		nameLabel.setFont(g2.getFont());
		Dimension d = nameLabel.getPreferredSize();
		nameLabel.setBounds(0, 0, d.width, d.height);

		g2.draw(top);

		double textX = bounds.getX() + NAME_GAP;
		double textY = bounds.getY() + (top.getHeight() - d.getHeight()) / 2;

		g2.translate(textX, textY);
		nameLabel.paint(g2);
		g2.translate(-textX, -textY);

		g2.draw(mid);
		description.draw(g2, mid);

		g2.draw(bot);

		costLabel.setText("<html><b>" + getCost() + "</b></html>");
		costLabel.setFont(g2.getFont());
		Dimension costD = costLabel.getPreferredSize();
		costLabel.setBounds(Math.max(DEFAULT_WIDTH, (int) bot.getWidth())
				- costD.width - NAME_GAP, 0, costD.width, costD.height);

		g2.draw(top);

		textX = bounds.getX() + Math.max(DEFAULT_WIDTH, (int) bot.getWidth())
				- costD.width - NAME_GAP;
		textY = bounds.getY() + (top.getHeight() - costD.getHeight()) / 2;

		g2.translate(textX, textY);
		costLabel.paint(g2);
		g2.translate(-textX, -textY);
	}

	/**
	 * Gets the description property value.
	 * 
	 * @return the description of this class
	 */
	public MultiLineString getDescription() {
		return description;
	}

	public Point2D getLocation() {
		if (getChildren().size() > 0)
			return new Point2D.Double(getBounds().getX(), getBounds().getY());
		else
			return super.getLocation();
	}

	/**
	 * Gets the name property value.
	 * 
	 * @return the class name
	 */
	public String getName() {
		return name;
	}

	public Shape getShape() {
		GeneralPath path = new GeneralPath();
		path.append(top, false);
		path.append(mid, false);
		path.append(bot, false);
		return path;
	}

	public void layout(Graphics2D g2, Grid grid) {

		double xgap = Math.max(DEFAULT_XGAP, grid.getX());
		double ygap = Math.max(DEFAULT_YGAP, grid.getY());

		Rectangle2D childBounds = null;
		List<Node> children = getChildren();
		for (Node child : children) {
			child.setZ(getZ() + 1);
			child.layout(g2, grid);
			if (childBounds == null)
				childBounds = child.getBounds();
			else
				childBounds.add(child.getBounds());
		}

		Rectangle2D descriptionBounds = description.getBounds(g2);
		nameLabel.setText("<html><b>" + name + "</b></html>");
		nameLabel.setFont(g2.getFont());
		Dimension d = nameLabel.getPreferredSize();
		double topWidth = Math.max(d.getWidth() + 2 * NAME_GAP,
				DEFAULT_TOP_WIDTH);
		double midHeight = Math.max(d.getHeight(), descriptionBounds
				.getHeight());
		double midwidth = Math.max(Math.max(d.getWidth(), descriptionBounds
				.getWidth()), topWidth);
		double topHeight = DEFAULT_COMPARTMENT_HEIGHT;

		if (childBounds == null) // no children; leave (x,y) as is and place
		// default rect below
		{
			snapBounds(grid, Math.max(topWidth + DEFAULT_WIDTH
					- DEFAULT_TOP_WIDTH, Math.max(DEFAULT_WIDTH,
					descriptionBounds.getWidth())), topHeight
					+ Math.max(DEFAULT_HEIGHT - DEFAULT_COMPARTMENT_HEIGHT,
							descriptionBounds.getHeight()));
		} else {
			costLabel.setText("<html><b>" + getCost() + "</b></html>");
			costLabel.setFont(g2.getFont());
			setBounds(new Rectangle2D.Double(childBounds.getX() - xgap,
					childBounds.getY() - topHeight - midHeight - ygap, Math
							.max(topWidth + DEFAULT_WIDTH, Math.max(midwidth,
									childBounds.getWidth() + 2 * xgap)),
					topHeight + midHeight + childBounds.getHeight() + 2 * ygap));
		}

		Rectangle2D b = getBounds();
		top = new Rectangle2D.Double(b.getX(), b.getY(), topWidth, topHeight);
		mid = new Rectangle2D.Double(b.getX(), b.getY() + topHeight, b
				.getWidth(), midHeight);
		bot = new Rectangle2D.Double(b.getX(),
				b.getY() + topHeight + midHeight, b.getWidth(), b.getHeight()
						- topHeight - descriptionBounds.getHeight());
	}

	/**
	 * Sets the description property value.
	 * 
	 * @param newValue
	 *            the description of this class
	 */
	public void setDescription(MultiLineString newValue) {
		description = newValue;
	}

	/**
	 * Sets the name property value.
	 * 
	 * @param newValue
	 *            the class name
	 */
	public void setName(String newValue) {
		name = newValue;
	}

	@Override
	public void translate(double dx, double dy) {
		GeometryUtils.translate(top, dx, dy);
		GeometryUtils.translate(mid, dx, dy);
		GeometryUtils.translate(bot, dx, dy);
		if (getChildren().size() == 0)
			super.translate(dx, dy);
		else
			for (Node childNode : getChildren())
				childNode.translate(dx, dy);
	}

	public double getCost() {
		try {
			double cost = 0;
			List<Node> children = getChildren();
			for (Node child : children) {
				if (child instanceof SoftwareModuleDiagramObject) {
					cost += ((SoftwareModuleDiagramObject) child).getCost();
				}
			}
			for (Edge edge : edges) {
				if (edge instanceof SoftwareModuleDiagramObject) {
					cost += ((SoftwareModuleDiagramObject) edge).getCost();
				}
			}
			return cost;
		} catch (Exception e) {
			return 0;
		}
	}

	public void addEdge(ModuleTransitionEdge edge) {
		edges.add(edge);
	}

	public void removeEdge(ModuleTransitionEdge edge) {
		edges.remove(edge);
	}

	@Override
	public Element getAsXMLElement() {
		Element element = new Element(RS.getElementName("element"));
		Element eId = new Element(RS.getName("node.id"));
		eId.setText(String.valueOf(smdId));
		element.addContent(eId);
		Element eName = new Element(RS.getElementName("name"));
		eName.setText(name);
		element.addContent(eName);
		Element eDescription = new Element(RS.getElementName("description"));
		eDescription.setText(description.getText());
		element.addContent(eDescription);
		for (Node node : getChildren()) {
			if (node instanceof ExportableAsXML) {
				ExportableAsXML xmlNode = (ExportableAsXML) node;
				element.addContent(xmlNode.getAsXMLElement());
			}
		}
		for (Edge edge : edges) {
			if (edge instanceof ExportableAsXML) {
				ExportableAsXML xmlEdge = (ExportableAsXML) edge;
				element.addContent(xmlEdge.getAsXMLElement());
			}
		}
		return element;
	}

	public boolean contains(Node n) {
		if (this.getChildren().contains(n)) {
			return true;
		}
		for (Node child : this.getChildren()) {
			if (child instanceof ComplexModuleNode) {
				return ((ComplexModuleNode) child).contains(n);
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return this.getName();
	}
}
