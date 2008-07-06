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
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.PersistenceDelegate;
import java.beans.Statement;

/**
 * A class that supplies convenience implementations for a number of methods in
 * the Edge interface
 */
@SuppressWarnings("serial")
public abstract class AbstractEdge implements Edge {
	public AbstractEdge() {
		this.id = new Id();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.Edge#connect(uk.ac.sheffield.dcs.smdStudio.framework
	 * .Node, uk.ac.sheffield.dcs.smdStudio.framework.Node)
	 */
	public final void connect(Node s, Node e) {
		start = s;
		end = e;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.sheffield.dcs.smdStudio.framework.Edge#getStart()
	 */
	public Node getStart() {
		return start;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.sheffield.dcs.smdStudio.framework.Edge#getEnd()
	 */
	public Node getEnd() {
		return end;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.sheffield.dcs.smdStudio.framework.Edge#getBounds(java.awt.Graphics2D)
	 */
	public Rectangle2D getBounds(Graphics2D g2) {
		Line2D conn = getConnectionPoints();
		Rectangle2D r = new Rectangle2D.Double();
		r.setFrameFromDiagonal(conn.getX1(), conn.getY1(), conn.getX2(), conn
				.getY2());
		return r;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.sheffield.dcs.smdStudio.framework.Edge#getConnectionPoints()
	 */
	public Line2D getConnectionPoints() {
		Rectangle2D startBounds = start.getBounds();
		Rectangle2D endBounds = end.getBounds();
		Point2D startCenter = new Point2D.Double(startBounds.getCenterX(),
				startBounds.getCenterY());
		Point2D endCenter = new Point2D.Double(endBounds.getCenterX(),
				endBounds.getCenterY());
		Direction toEnd = new Direction(startCenter, endCenter);
		return new Line2D.Double(start.getConnectionPoint(toEnd), end
				.getConnectionPoint(toEnd.turn(180)));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.sheffield.dcs.smdStudio.framework.diagram.Edge#getId()
	 */
	public Id getId() {
		return this.id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.diagram.Edge#setId(uk.ac.sheffield.dcs.smdStudio
	 * .framework.diagram.Id)
	 */
	public void setId(Id id) {
		this.id = id;
	}

	/**
	 * Adds a persistence delegate to a given encoder that encodes edges.
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
				.getPersistenceDelegate(AbstractEdge.class);
		encoder.remove(oldInstance);
		encoder.setPersistenceDelegate(AbstractEdge.class,
				new DefaultPersistenceDelegate() {
					protected void initialize(Class<?> type,
							Object oldInstance, Object newInstance, Encoder out) {
						super.initialize(type, oldInstance, newInstance, out);
						Edge e = (Edge) oldInstance;
						out.writeStatement(new Statement(oldInstance,
								"connect", new Object[] { e.getStart(),
										e.getEnd() }));
						if (writeId) {
							Id id = e.getId();
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
	public AbstractEdge clone() {
		try {
			AbstractEdge cloned = (AbstractEdge) super.clone();
			cloned.id = new Id();
			return cloned;
		} catch (CloneNotSupportedException ex) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.sheffield.dcs.smdStudio.framework.diagram.Edge#getRevision()
	 */
	public Integer getRevision() {
		return this.revision;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.diagram.Edge#setRevision(java.lang.Integer
	 * )
	 */
	public void setRevision(Integer newRevisionNumber) {
		this.revision = newRevisionNumber;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.sheffield.dcs.smdStudio.framework.diagram.Edge#incrementRevision()
	 */
	public void incrementRevision() {
		int i = this.revision.intValue();
		i++;
		this.revision = new Integer(i);
	}

	/** The node where the edge starts */
	private Node start;

	/** The node where the edge ends */
	private Node end;

	/** Edge's current id (unique in all the graph) */
	private Id id;

	/** Edge's current revision */
	private Integer revision = new Integer(0);
}
