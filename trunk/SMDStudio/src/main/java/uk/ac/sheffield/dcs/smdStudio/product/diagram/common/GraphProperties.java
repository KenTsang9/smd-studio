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

package uk.ac.sheffield.dcs.smdStudio.product.diagram.common;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Locale;
import java.util.ResourceBundle;

import uk.ac.sheffield.dcs.smdStudio.framework.diagram.Edge;
import uk.ac.sheffield.dcs.smdStudio.framework.diagram.Grid;
import uk.ac.sheffield.dcs.smdStudio.framework.diagram.MultiLineString;
import uk.ac.sheffield.dcs.smdStudio.framework.diagram.RectangularNode;
import uk.ac.sheffield.dcs.smdStudio.framework.resources.ResourceBundleConstant;

/**
 * A note node in a UML diagram.
 */
@SuppressWarnings("serial")
public class GraphProperties extends RectangularNode {
	private static Color DEFAULT_COLOR = new Color(151, 168, 220);

	private static int DEFAULT_HEIGHT = 100;

	private static int DEFAULT_WIDTH = 100;

	private static int FOLD_X = 8;

	private static int FOLD_Y = 8;

	private static int INFINITE_Z_LEVEL = 10000;

	private Color color;

	private double trainingCost;

	private double teamQuality = 1;

	public double getTeamQuality() {
		return teamQuality;
	}

	public void setTeamQuality(double teamQuality) {
		this.teamQuality = teamQuality;
		updateText();
	}

	public double getTrainingCost() {
		return trainingCost;
	}

	private MultiLineString text;

	private ResourceBundle resourceBundle;

	/**
	 * Construct a note node with a default size and color
	 */
	public GraphProperties() {
		setBounds(new Rectangle2D.Double(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT));
		text = new MultiLineString();
		text.setJustification(MultiLineString.LEFT);
		updateText();
		color = DEFAULT_COLOR;
	}

	private void updateText() {
		String prefix = getGeneralGraphResourceBundle().getString(
				"graph.trainingCost.text");
		String sTrainingCost = getLabelValueFormatedText(prefix, String
				.valueOf(trainingCost));
		prefix = getGeneralGraphResourceBundle().getString(
				"graph.teamQuality.text");
		String sTeamQuality = getLabelValueFormatedText(prefix, String
				.valueOf(teamQuality));
		text.setText(sTeamQuality + "<br/><br/>" + sTrainingCost);
	}

	private String getLabelValueFormatedText(String label, String value) {
		return "<font face=\"comic sans ms\" size=\"10\">" + label + ": "
				+ value + "</font>";
	}

	@Override
	public boolean checkAddEdge(Edge e, Point2D p1, Point2D p2) {
		return false;
	}

	@Override
	public GraphProperties clone() {
		GraphProperties cloned = (GraphProperties) super.clone();
		cloned.text = text.clone();
		return cloned;
	}

	@Override
	public void draw(Graphics2D g2) {
		super.draw(g2);
		Color oldColor = g2.getColor();
		g2.setColor(color);

		Shape path = getShape();
		g2.fill(path);
		g2.setColor(oldColor);
		g2.draw(path);

		Rectangle2D bounds = getBounds();
		GeneralPath fold = new GeneralPath();
		fold.moveTo((float) (bounds.getMaxX() - FOLD_X), (float) bounds.getY());
		fold.lineTo((float) bounds.getMaxX() - FOLD_X, (float) bounds.getY()
				+ FOLD_X);
		fold.lineTo((float) bounds.getMaxX(), (float) (bounds.getY() + FOLD_Y));
		fold.closePath();
		oldColor = g2.getColor();
		g2.setColor(g2.getBackground());
		g2.fill(fold);
		g2.setColor(oldColor);
		g2.draw(fold);

		text.draw(g2, getBounds());
	}

	/**
	 * Gets the value of the color property.
	 * 
	 * @return the background color of the note
	 */
	public Color getColor() {
		return color;
	}

	@Override
	public Shape getShape() {
		Rectangle2D bounds = getBounds();
		GeneralPath path = new GeneralPath();
		path.moveTo((float) bounds.getX(), (float) bounds.getY());
		path.lineTo((float) (bounds.getMaxX() - FOLD_X), (float) bounds.getY());
		path.lineTo((float) bounds.getMaxX(), (float) (bounds.getY() + FOLD_Y));
		path.lineTo((float) bounds.getMaxX(), (float) bounds.getMaxY());
		path.lineTo((float) bounds.getX(), (float) bounds.getMaxY());
		path.closePath();
		return path;
	}

	/**
	 * Gets the value of the text property.
	 * 
	 * @return the text inside the note
	 */
	public MultiLineString getText() {
		return text;
	}

	@Override
	public int getZ() {
		// Ensures that this kind of nodes is always on top
		return INFINITE_Z_LEVEL;
	}

	@Override
	public void layout(Graphics2D g2, Grid grid) {
		Rectangle2D b = text.getBounds(g2);
		snapBounds(grid, Math.max(b.getWidth(), DEFAULT_WIDTH), Math.max(b
				.getHeight(), DEFAULT_HEIGHT));
	}

	/**
	 * Sets the value of the color property.
	 * 
	 * @param newValue
	 *            the background color of the note
	 */
	public void setColor(Color newValue) {
		color = newValue;
	}

	public void setTrainingCost(double trainingCost) {
		this.trainingCost = trainingCost;
		updateText();
	}

	/**
	 * @return resource bundle
	 */
	private ResourceBundle getGeneralGraphResourceBundle() {
		if (this.resourceBundle == null) {
			this.resourceBundle = ResourceBundle.getBundle(
					ResourceBundleConstant.GENERAL_GRAPH_STRINGS, Locale
							.getDefault());
		}
		return this.resourceBundle;
	}
}
