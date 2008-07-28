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
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import org.jdom.Element;

import uk.ac.sheffield.dcs.smdStudio.framework.diagram.Edge;
import uk.ac.sheffield.dcs.smdStudio.framework.diagram.ExportableAsXML;
import uk.ac.sheffield.dcs.smdStudio.framework.diagram.Grid;
import uk.ac.sheffield.dcs.smdStudio.framework.diagram.MultiLineString;
import uk.ac.sheffield.dcs.smdStudio.framework.diagram.RectangularNode;
import uk.ac.sheffield.dcs.smdStudio.framework.resources.ResourceBundleConstant;
import uk.ac.sheffield.dcs.smdStudio.framework.resources.XMLResourceBoundle;

/**
 * A note node in a UML diagram.
 */
@SuppressWarnings("serial")
public class GraphProperties extends RectangularNode implements ExportableAsXML {

	private static Color DEFAULT_COLOR = new Color(151, 168, 220);

	private static int DEFAULT_HEIGHT = 100;

	private static int DEFAULT_WIDTH = 150;

	private static int FOLD_X = 8;

	private static int FOLD_Y = 8;

	private static int INFINITE_Z_LEVEL = 10000;

	private Color color;

	private ResourceBundle resourceBundle;

	private double teamQuality;

	private String teamQualityText = "";

	private MultiLineString text;

	private String totalCostText = "";

	private double trainingCost;

	private String trainingCostText = "";

	private static final XMLResourceBoundle RS = new XMLResourceBoundle(
			GraphProperties.class);

	/**
	 * Construct a note node with a default size and color
	 */
	public GraphProperties() {
		if (teamQuality == 0) {
			teamQuality = 1;
		}
		text = new MultiLineString();
		text.setJustification(MultiLineString.CENTER);
		text.setSize(MultiLineString.LARGE);
		updateText();
		color = DEFAULT_COLOR;
		setBounds(new Rectangle2D.Double(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT));
	}

	public String calculateTotalCost() {
		String notAvailable = resourceBundle
				.getString("graph.noCostAvailable.text");
		if (getGraph() == null) {
			return notAvailable;
		}
		double totalCost = getGraph().calculateTotalCost();
		if (totalCost == 0) {
			return notAvailable;
		}
		totalCost = totalCost * teamQuality + trainingCost;
		return new DecimalFormat("0.##").format(totalCost);
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
		updateText();
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

	private String formatCostText() {
		String label = getGeneralGraphResourceBundle().getString(
				"graph.totalCost.text");
		return "<font face=\"comic sans ms\" size=\"7\" color=\"red\">" + label
				+ ": " + calculateTotalCost() + "</font>";
	}

	/**
	 * Gets the value of the color property.
	 * 
	 * @return the background color of the note
	 */
	public Color getColor() {
		return color;
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

	private String getLabelValueFormatedText(String label, String value) {
		return "<font face=\"comic sans ms\">" + label + ": " + value
				+ "</font>";
	}

	@Override
	public Shape getShape() {
		updateText();
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

	public double getTeamQuality() {
		return teamQuality;
	}

	/**
	 * Gets the value of the text property.
	 * 
	 * @return the text inside the note
	 */
	public MultiLineString getText() {
		return text;
	}

	public double getTrainingCost() {
		return trainingCost;
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

	public void setTeamQuality(double teamQuality) {
		this.teamQuality = teamQuality;
		updateText();
	}

	public void setTrainingCost(double trainingCost) {
		this.trainingCost = trainingCost;
		updateText();
	}

	private void updateText() {
		boolean changed = false;
		String prefix = getGeneralGraphResourceBundle().getString(
				"graph.trainingCost.text");
		String sTrainingCost = getLabelValueFormatedText(prefix, String
				.valueOf(trainingCost));
		changed = changed ? true : trainingCostText.equals(sTrainingCost);
		trainingCostText = sTrainingCost;
		prefix = getGeneralGraphResourceBundle().getString(
				"graph.teamQuality.text");
		String sTeamQuality = getLabelValueFormatedText(prefix, String
				.valueOf(teamQuality));
		changed = changed ? true : teamQualityText.equals(sTeamQuality);
		teamQualityText = sTeamQuality;
		String sTotalCost = formatCostText();
		changed = changed ? true : totalCostText.equals(sTotalCost);
		totalCostText = sTotalCost;
		text.setText(sTotalCost + "<br/><br/>" + sTeamQuality + "<br/><br/>"
				+ sTrainingCost);
		if (changed && getGraph() != null) {
			getGraph().repaint();
		}
	}

	@Override
	public Element getAsXMLElement() {
		Element element = new Element(RS.getElementName("element"));
		Element eTrainingCost = new Element(RS.getElementName("trainingcost"));
		eTrainingCost.setText(String.valueOf(trainingCost));
		element.addContent(eTrainingCost);
		Element eTeamQuality = new Element(RS.getElementName("teamquality"));
		eTeamQuality.setText(String.valueOf(teamQuality));
		element.addContent(eTeamQuality);
		return element;
	}
}
