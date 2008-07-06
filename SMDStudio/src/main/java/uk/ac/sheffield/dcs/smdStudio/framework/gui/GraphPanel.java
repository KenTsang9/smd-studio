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

package uk.ac.sheffield.dcs.smdStudio.framework.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import uk.ac.sheffield.dcs.smdStudio.UMLEditor;
import uk.ac.sheffield.dcs.smdStudio.framework.diagram.DiagramLink;
import uk.ac.sheffield.dcs.smdStudio.framework.diagram.Edge;
import uk.ac.sheffield.dcs.smdStudio.framework.diagram.Graph;
import uk.ac.sheffield.dcs.smdStudio.framework.diagram.Grid;
import uk.ac.sheffield.dcs.smdStudio.framework.diagram.Node;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.sidebar.SideToolPanel;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.sidebar.Tool;
import uk.ac.sheffield.dcs.smdStudio.framework.history.HistoryManager;
import uk.ac.sheffield.dcs.smdStudio.framework.resources.ResourceBundleConstant;
import uk.ac.sheffield.dcs.smdStudio.product.diagram.common.DiagramLinkNode;


/**
 * A panel to draw a graph
 */
@SuppressWarnings("serial")
public class GraphPanel extends JPanel {

	/**
	 * Constructs a graph.
	 * 
	 * @param aToolBar
	 *            the tool bar with the node and edge tools
	 */
	public GraphPanel(Graph aGraph) {
		this.graph = aGraph;
		this.historyManager = new HistoryManager(aGraph);
		this.zoom = 1;
		setBackground(Color.WHITE);

		addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent event) {
				requestFocus();
				final Point2D mousePoint = new Point2D.Double(event.getX()
						/ zoom, event.getY() / zoom);
				boolean isCtrl = (event.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0;
				Node n = graph.findNode(mousePoint);
				Edge e = graph.findEdge(mousePoint);
				if (event.getClickCount() > 1
						|| (event.getModifiers() & InputEvent.BUTTON1_MASK) == 0) {
					if (e != null) {
						selectionHandler.setSelectedElement(e);
						editSelected();
					} else if (n != null) {
						selectionHandler.setSelectedElement(n);
						editSelected();
					}
				} else if (selectedTool == null) // select
				{
					if (e != null) {
						selectionHandler.setSelectedElement(e);
					} else if (n != null) {
						if (isCtrl)
							selectionHandler.addSelectedElement(n);
						else if (!selectionHandler.isElementAlreadySelected(n))
							selectionHandler.setSelectedElement(n);
						dragMode = DRAG_MOVE;
					} else {
						if (!isCtrl)
							selectionHandler.clearSelection();
						dragMode = DRAG_LASSO;
					}
				} else if (selectedTool instanceof Node) {
					Node prototype = (Node) selectedTool;
					Node newNode = (Node) prototype.clone(); // GraphService.
																// cloneNode
																// (prototype);
					boolean added = addNodeAtPoint(newNode, mousePoint);
					if (added) {
						selectionHandler.setSelectedElement(newNode);
						dragMode = DRAG_MOVE;
					} else if (n != null) {
						if (isCtrl)
							selectionHandler.addSelectedElement(n);
						else if (!selectionHandler.isElementAlreadySelected(n))
							selectionHandler.setSelectedElement(n);
						dragMode = DRAG_MOVE;
					}
				} else if (selectedTool instanceof Edge) {
					if (n != null)
						dragMode = DRAG_RUBBERBAND;
				}

				lastMousePoint = mousePoint;
				mouseDownPoint = mousePoint;
				repaint();
			}

			public void mouseReleased(MouseEvent event) {
				Point2D mousePoint = new Point2D.Double(event.getX() / zoom,
						event.getY() / zoom);
				if (dragMode == DRAG_RUBBERBAND) {
					Edge prototype = (Edge) selectedTool;
					Edge newEdge = (Edge) prototype.clone(); // GraphService.
																// cloneEdge
																// (prototype);
					boolean added = addEdgeAtPoints(newEdge, mouseDownPoint,
							mousePoint);
					if (added) {
						selectionHandler.setSelectedElement(newEdge);
					}
				} else if (dragMode == DRAG_MOVE) {
					if (historyManager.hasCaptureInProgress()) {
						graph.layout((Graphics2D) getGraphics(), grid);
						historyManager.stopCaptureAction();
					}
				}
				dragMode = DRAG_NONE;

				revalidate();
				repaint();
			}
		});

		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent event) {
				Point2D mousePoint = new Point2D.Double(event.getX() / zoom,
						event.getY() / zoom);
				if (dragMode == DRAG_MOVE
						&& selectionHandler.isNodeSelectedAtLeast()) {
					if (!historyManager.hasCaptureInProgress()) {
						historyManager.startCaptureAction();
					}

					Node lastNode = selectionHandler.getLastSelectedNode();
					Rectangle2D bounds = lastNode.getBounds();
					double dx = mousePoint.getX() - lastMousePoint.getX();
					double dy = mousePoint.getY() - lastMousePoint.getY();

					// we don't want to drag nodes into negative coordinates
					// particularly with multiple selection, we might never be
					// able to get them back.
					List<Node> selectedNodes = selectionHandler
							.getSelectedNodes();
					for (Node n : selectedNodes)
						bounds.add(n.getBounds());
					dx = Math.max(dx, -bounds.getX());
					dy = Math.max(dy, -bounds.getY());

					for (Node n : selectedNodes) {
						if (!selectedNodes.contains(n.getParent())) // parents
																	// are
																	// responsible
																	// for
																	// translating
																	// their
																	// children
							n.translate(dx, dy);
					}
				} else if (dragMode == DRAG_LASSO) {
					boolean isCtrl = (event.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0;
					double x1 = mouseDownPoint.getX();
					double y1 = mouseDownPoint.getY();
					double x2 = mousePoint.getX();
					double y2 = mousePoint.getY();
					Rectangle2D.Double lasso = new Rectangle2D.Double(Math.min(
							x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math
							.abs(y1 - y2));
					Iterator<Node> iter = graph.getNodes().iterator();
					while (iter.hasNext()) {
						Node n = (Node) iter.next();
						Rectangle2D bounds = n.getBounds();
						if (!isCtrl && !lasso.contains(bounds)) {
							selectionHandler.removeElementFromSelection(n);
						} else if (lasso.contains(bounds)) {
							selectionHandler.addSelectedElement(n);
						}
					}
				}

				lastMousePoint = mousePoint;
				repaint();
			}
		});
	}

	/**
	 * Create and/or return the (mandatory) tool panel listener
	 * 
	 * @return listener
	 */
	public SideToolPanel.Listener getToolListener() {
		if (this.toolListener == null) {
			this.toolListener = new SideToolPanel.Listener() {
				public void toolSelectionChanged(Tool tool) {
					selectedTool = tool.getNodeOrEdge();
				}
			};
		}
		return this.toolListener;
	}

	/**
	 * Edits the properties of the selected graph element.
	 */
	public void editSelected() {
		Object edited = null;
		if (selectionHandler.isNodeSelectedAtLeast()) {
			edited = selectionHandler.getLastSelectedNode();
		}
		if (selectionHandler.isEdgeSelectedAtLeast()) {
			edited = selectionHandler.getLastSelectedEdge();
		}
		if (edited == null) {
			return;
		}
		final PropertySheet sheet = new PropertySheet(edited);

		sheet.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(final PropertyChangeEvent event) {
				if (event.getSource() instanceof DiagramLinkNode) {
					DiagramLinkNode ln = (DiagramLinkNode) event.getSource();
					DiagramLink dl = ln.getDiagramLink();
					if (dl != null && dl.getOpenFlag().booleanValue()) {
						diagramPanel.fireMustOpenFile(dl.getURL());
						dl.setOpenFlag(new Boolean(false));
					}
				}

				graph.changeNodeOrEdgeProperty(event);
				graph.layout((Graphics2D) getGraphics(), grid);
				repaint();
			}
		});

		JOptionPane optionPane = new JOptionPane();
		optionPane.setOpaque(false);
		optionPane.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if ((event.getPropertyName().equals(JOptionPane.VALUE_PROPERTY))
						&& event.getNewValue() != null
						&& event.getNewValue() != JOptionPane.UNINITIALIZED_VALUE) {
					if (sheet.isEditable()) {
						// This manages optionPane submits through a property
						// listener because, as dialog display could be
						// delegated
						// (to Eclipse for example), host system can work in
						// other threads
						historyManager.stopCaptureAction();
					}
				}
			}
		});

		if (sheet.isEditable()) {
			historyManager.startCaptureAction();
			optionPane.setMessage(sheet.getComponent());
		}
		if (!sheet.isEditable()) {
			String message = this.resourceBundle
					.getString("dialog.properties.empty_bean_message");
			JLabel label = new JLabel(message);
			label.setFont(label.getFont().deriveFont(Font.PLAIN));
			optionPane.setMessage(label);
		}
		DialogFactory.getInstance().showDialog(optionPane,
				this.resourceBundle.getString("dialog.properties.title"), true);

	}

	private boolean addNodeAtPoint(Node newNode, Point2D location) {
		boolean isAdded = false;
		historyManager.startCaptureAction();
		try {
			if (graph.addNode(newNode, location)) {
				newNode.incrementRevision();
				graph.layout((Graphics2D) getGraphics(), grid);
			}
		} finally {
			historyManager.stopCaptureAction();
		}
		return isAdded;
	}

	private boolean addEdgeAtPoints(Edge newEdge, Point2D startPoint,
			Point2D endPoint) {
		boolean isAdded = false;
		if (startPoint.distance(endPoint) > CONNECT_THRESHOLD) {
			historyManager.startCaptureAction();
			try {
				if (graph.addEdgeAtPoints(newEdge, startPoint, endPoint)) {
					newEdge.incrementRevision();
					graph.layout((Graphics2D) getGraphics(), grid);
				}
			} finally {
				historyManager.stopCaptureAction();
			}
		}
		return isAdded;
	}

	/**
	 * Removes the selected nodes or edges.
	 */
	public void removeSelected() {
		historyManager.startCaptureAction();
		try {
			graph.removeNodesAndEdges(selectionHandler.getSelectedNodes(),
					selectionHandler.getSelectedEdges());
			graph.layout((Graphics2D) getGraphics(), grid);
		} finally {
			historyManager.stopCaptureAction();
		}

		selectionHandler.clearSelection();
		repaint();
	}

	public void cut() {
		copy();
		removeSelected();
	}

	public void copy() {
		UMLEditor.getInstance().getClipboard().copyIn(graph,
				selectionHandler.getSelectedNodes());
	}

	public void paste() {
		historyManager.startCaptureAction();
		try {
			Collection<Node> pastedNodes = UMLEditor.getInstance()
					.getClipboard().pasteOut(graph,
							selectionHandler.getLastSelectedNode());
			if (pastedNodes != null) {
				selectionHandler.clearSelection();
				for (Node n : pastedNodes)
					selectionHandler.addSelectedElement(n);
				graph.layout((Graphics2D) getGraphics(), grid);
			}
		} finally {
			historyManager.stopCaptureAction();
		}
		repaint();
	}

	/**
	 * Returns the graph handlede by the panel
	 */
	public Graph getGraph() {
		return this.graph;
	}

	@Override
	public void doLayout() {
		if (graph != null && grid != null) {
			graph.layout((Graphics2D) getGraphics(), grid);
		}
		super.doLayout();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (grid == null) // first time this is painted
		{
			grid = new Grid();
			grid.setGrid((int) gridSize, (int) gridSize);
			graph.layout((Graphics2D) getGraphics(), grid);
		}

		Graphics2D g2 = (Graphics2D) g;
		g2.scale(zoom, zoom);
		Rectangle2D bounds = getBounds();
		Rectangle2D graphBounds = graph.getBounds(g2);
		if (!hideGrid)
			grid.draw(g2, new Rectangle2D.Double(0, 0, Math.max(bounds
					.getMaxX()
					/ zoom, graphBounds.getMaxX()), Math.max(bounds.getMaxY()
					/ zoom, graphBounds.getMaxY())));
		graph.draw(g2, grid);
		List<Node> nodes = selectionHandler.getSelectedNodes();
		for (Node n : nodes) {
			if (graph.getNodes().contains(n)) {
				Rectangle2D grabberBounds = n.getBounds();
				drawGrabber(g2, grabberBounds.getMinX(), grabberBounds
						.getMinY());
				drawGrabber(g2, grabberBounds.getMinX(), grabberBounds
						.getMaxY());
				drawGrabber(g2, grabberBounds.getMaxX(), grabberBounds
						.getMinY());
				drawGrabber(g2, grabberBounds.getMaxX(), grabberBounds
						.getMaxY());
			}
		}
		List<Edge> edges = selectionHandler.getSelectedEdges();
		for (Edge e : edges) {
			if (graph.getEdges().contains(e)) {
				Line2D line = e.getConnectionPoints();
				drawGrabber(g2, line.getX1(), line.getY1());
				drawGrabber(g2, line.getX2(), line.getY2());
			}
		}

		if (dragMode == DRAG_RUBBERBAND) {
			Color oldColor = g2.getColor();
			g2.setColor(PURPLE);
			g2.draw(new Line2D.Double(mouseDownPoint, lastMousePoint));
			g2.setColor(oldColor);
		} else if (dragMode == DRAG_LASSO) {
			Color oldColor = g2.getColor();
			g2.setColor(PURPLE);
			double x1 = mouseDownPoint.getX();
			double y1 = mouseDownPoint.getY();
			double x2 = lastMousePoint.getX();
			double y2 = lastMousePoint.getY();
			Rectangle2D.Double lasso = new Rectangle2D.Double(Math.min(x1, x2),
					Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1 - y2));
			g2.draw(lasso);
			g2.setColor(oldColor);
		}
	}

	/**
	 * Draws a single "grabber", a filled square
	 * 
	 * @param g2
	 *            the graphics context
	 * @param x
	 *            the x coordinate of the center of the grabber
	 * @param y
	 *            the y coordinate of the center of the grabber
	 */
	public static void drawGrabber(Graphics2D g2, double x, double y) {
		final int SIZE = 5;
		Color oldColor = g2.getColor();
		g2.setColor(PURPLE);
		g2.fill(new Rectangle2D.Double(x - SIZE / 2, y - SIZE / 2, SIZE, SIZE));
		g2.setColor(oldColor);
	}

	public Dimension getPreferredSize() {
		Rectangle2D bounds = graph.getBounds((Graphics2D) getGraphics());
		return new Dimension((int) (zoom * bounds.getMaxX()),
				(int) (zoom * bounds.getMaxY()));
	}

	/**
	 * Changes the zoom of this panel. The zoom is 1 by default and is
	 * multiplied by sqrt(2) for each positive stem or divided by sqrt(2) for
	 * each negative step.
	 * 
	 * @param steps
	 *            the number of steps by which to change the zoom. A positive
	 *            value zooms in, a negative value zooms out.
	 */
	public void changeZoom(int steps) {
		final double FACTOR = Math.sqrt(Math.sqrt(2));
		for (int i = 1; i <= steps; i++)
			zoom *= FACTOR;
		for (int i = 1; i <= -steps; i++)
			zoom /= FACTOR;
		revalidate();
		repaint();
	}

	/**
	 * Changes the grid size of this panel. The zoom is 10 by default and is
	 * multiplied by sqrt(2) for each positive stem or divided by sqrt(2) for
	 * each negative step.
	 * 
	 * @param steps
	 *            the number of steps by which to change the zoom. A positive
	 *            value zooms in, a negative value zooms out.
	 */
	public void changeGridSize(int steps) {
		final double FACTOR = Math.sqrt(Math.sqrt(2));
		for (int i = 1; i <= steps; i++)
			gridSize *= FACTOR;
		for (int i = 1; i <= -steps; i++)
			gridSize /= FACTOR;
		grid.setGrid((int) gridSize, (int) gridSize);
		graph.layout((Graphics2D) getGraphics(), grid);
		repaint();
	}

	public void setSelectedElement(Node node) {
		selectionHandler.setSelectedElement(node);
	}

	public void selectNext(int n) {
		ArrayList<Object> selectables = new ArrayList<Object>();
		selectables.addAll(graph.getNodes());
		selectables.addAll(graph.getEdges());
		if (selectables.size() == 0)
			return;
		java.util.Collections.sort(selectables,
				new java.util.Comparator<Object>() {
					public int compare(Object obj1, Object obj2) {
						double x1;
						double y1;
						if (obj1 instanceof Node) {
							Rectangle2D bounds = ((Node) obj1).getBounds();
							x1 = bounds.getX();
							y1 = bounds.getY();
						} else {
							Point2D start = ((Edge) obj1).getConnectionPoints()
									.getP1();
							x1 = start.getX();
							y1 = start.getY();
						}
						double x2;
						double y2;
						if (obj2 instanceof Node) {
							Rectangle2D bounds = ((Node) obj2).getBounds();
							x2 = bounds.getX();
							y2 = bounds.getY();
						} else {
							Point2D start = ((Edge) obj2).getConnectionPoints()
									.getP1();
							x2 = start.getX();
							y2 = start.getY();
						}
						if (y1 < y2)
							return -1;
						if (y1 > y2)
							return 1;
						if (x1 < x2)
							return -1;
						if (x1 > x2)
							return 1;
						return 0;
					}
				});
		int index;
		Object lastSelected = null;
		if (selectionHandler.isNodeSelectedAtLeast()) {
			lastSelected = selectionHandler.getLastSelectedNode();
		}
		if (selectionHandler.isEdgeSelectedAtLeast()) {
			lastSelected = selectionHandler.getLastSelectedEdge();
		}
		if (lastSelected == null)
			index = 0;
		else
			index = selectables.indexOf(lastSelected) + n;
		while (index < 0)
			index += selectables.size();
		index %= selectables.size();
		Object toSelect = selectables.get(index);
		if (toSelect instanceof Node) {
			selectionHandler.setSelectedElement((Node) toSelect);
		}
		if (toSelect instanceof Edge) {
			selectionHandler.setSelectedElement((Edge) toSelect);
		}
		repaint();
	}

	/**
	 * Sets the value of the hideGrid property
	 * 
	 * @param newValue
	 *            true if the grid is being hidden
	 */
	public void setHideGrid(boolean newValue) {
		hideGrid = newValue;
		repaint();
	}

	/**
	 * Gets the value of the hideGrid property
	 * 
	 * @return true if the grid is being hidden
	 */
	public boolean getHideGrid() {
		return hideGrid;
	}

	/**
	 * Restore the previous graph from the history cursor location
	 */
	public void undo() {
		historyManager.undo();
		graph.layout((Graphics2D) getGraphics(), grid);
		repaint();
	}

	/**
	 * Restore the next graph from the history cursor location
	 */
	public void redo() {
		historyManager.redo();
		graph.layout((Graphics2D) getGraphics(), grid);
		repaint();
	}

	private ResourceBundle resourceBundle = ResourceBundle.getBundle(
			ResourceBundleConstant.OTHER_STRINGS, Locale.getDefault());

	private SideToolPanel.Listener toolListener;

	private Graph graph;

	private Grid grid;

	private DiagramPanel diagramPanel;

	private double zoom;

	private double gridSize = GRID_SIZE;

	private boolean hideGrid;

	private Object selectedTool;

	private Point2D lastMousePoint;

	private Point2D mouseDownPoint;

	private int dragMode;

	private GraphPanelSelectionHandler selectionHandler = new GraphPanelSelectionHandler();

	private HistoryManager historyManager;

	private static final int DRAG_NONE = 0;

	private static final int DRAG_MOVE = 1;

	private static final int DRAG_RUBBERBAND = 2;

	private static final int DRAG_LASSO = 3;

	private static final int GRID_SIZE = 10;

	private static final int CONNECT_THRESHOLD = 8;

	private static final Color PURPLE = new Color(0.7f, 0.4f, 0.7f);

}