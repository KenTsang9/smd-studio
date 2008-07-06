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

import javax.swing.JLabel;

import uk.ac.sheffield.dcs.smdStudio.framework.diagram.Grid;
import uk.ac.sheffield.dcs.smdStudio.framework.diagram.MultiLineString;
import uk.ac.sheffield.dcs.smdStudio.framework.diagram.Node;
import uk.ac.sheffield.dcs.smdStudio.framework.diagram.RectangularNode;
import uk.ac.sheffield.dcs.smdStudio.framework.util.GeometryUtils;
import uk.ac.sheffield.dcs.smdStudio.product.diagram.common.PointNode;


/**
 * A class node in a class diagram.
 */
@SuppressWarnings("serial")
public class SimpleModuleNode extends RectangularNode {
	private static int DEFAULT_HEIGHT = 100;

	private static int DEFAULT_COMPARTMENT_HEIGHT = 20;

	private static int DEFAULT_TOP_WIDTH = 60;

	private static int DEFAULT_WIDTH = 100;

	private static JLabel label = new JLabel();

	private static final int NAME_GAP = 3;

	private MultiLineString description;

	private String name;

	private transient Rectangle2D mid;

	private transient Rectangle2D top;

	/**
	 * Construct a package node with a default size
	 */
	public SimpleModuleNode() {
		name = "";
		description = new MultiLineString();
		description.setJustification(MultiLineString.LEFT);
		setBounds(new Rectangle2D.Double(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT));
		top = new Rectangle2D.Double(0, 0, DEFAULT_TOP_WIDTH,
				DEFAULT_COMPARTMENT_HEIGHT);
		mid = new Rectangle2D.Double(0, DEFAULT_COMPARTMENT_HEIGHT,
				DEFAULT_WIDTH, DEFAULT_HEIGHT - DEFAULT_COMPARTMENT_HEIGHT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.Node#addNode(uk.ac.sheffield.dcs.smdStudio.framework
	 * .Node, java.awt.geom.Point2D)
	 */
	public boolean checkAddNode(Node n, Point2D p) {
		// TODO : where is it added?
		if (n instanceof PointNode) {
			return true;
		}
		return false;
	}

	public SimpleModuleNode clone() {
		SimpleModuleNode cloned = (SimpleModuleNode) super.clone();
		cloned.description = description.clone();
		top = (Rectangle2D) top.clone();
		mid = (Rectangle2D) mid.clone();
		return cloned;
	}

	public void draw(Graphics2D g2) {
		super.draw(g2);
		Rectangle2D bounds = getBounds();

		label.setText("<html><b>" + name + "</b></html>");
		label.setFont(g2.getFont());
		Dimension d = label.getPreferredSize();
		label.setBounds(0, 0, d.width, d.height);

		g2.draw(top);

		double textX = bounds.getX() + NAME_GAP;
		double textY = bounds.getY() + (top.getHeight() - d.getHeight()) / 2;

		g2.translate(textX, textY);
		label.paint(g2);
		g2.translate(-textX, -textY);

		g2.draw(mid);
		description.draw(g2, mid);
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
		return path;
	}

	public void layout(Graphics2D g2, Grid grid) {

		Rectangle2D descriptionBounds = description.getBounds(g2);
		label.setText("<html><b>" + name + "</b></html>");
		label.setFont(g2.getFont());
		Dimension d = label.getPreferredSize();
		double topWidth = Math.max(d.getWidth() + 2 * NAME_GAP,
				DEFAULT_TOP_WIDTH);
		double midHeight = descriptionBounds.getHeight() == 0 ? DEFAULT_HEIGHT
				- DEFAULT_COMPARTMENT_HEIGHT : Math.max(
				DEFAULT_COMPARTMENT_HEIGHT, descriptionBounds.getHeight());
		double topHeight = DEFAULT_COMPARTMENT_HEIGHT;

		snapBounds(grid, Math.max(topWidth + DEFAULT_WIDTH - DEFAULT_TOP_WIDTH,
				Math.max(DEFAULT_WIDTH, descriptionBounds.getWidth())),
				topHeight + midHeight);

		Rectangle2D b = getBounds();
		top = new Rectangle2D.Double(b.getX(), b.getY(), topWidth, topHeight);
		mid = new Rectangle2D.Double(b.getX(), b.getY() + topHeight, b
				.getWidth(), midHeight);
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
		super.translate(dx, dy);
	}

}
