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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import uk.ac.sheffield.dcs.smdStudio.framework.diagram.Edge;
import uk.ac.sheffield.dcs.smdStudio.framework.diagram.Graph;
import uk.ac.sheffield.dcs.smdStudio.framework.diagram.GraphModificationListener;
import uk.ac.sheffield.dcs.smdStudio.framework.diagram.Id;
import uk.ac.sheffield.dcs.smdStudio.framework.diagram.Node;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.sidebar.ISideBar;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.sidebar.LargeSideBar;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.sidebar.SmallSideBar;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.theme.ThemeManager;
import uk.ac.sheffield.dcs.smdStudio.framework.preference.PreferencesConstant;
import uk.ac.sheffield.dcs.smdStudio.framework.preference.PreferencesService;
import uk.ac.sheffield.dcs.smdStudio.framework.preference.PreferencesServiceFactory;
import uk.ac.sheffield.dcs.smdStudio.framework.resources.ResourceBundleConstant;
import uk.ac.sheffield.dcs.smdStudio.framework.swingextension.TinyScrollBarUI;
import uk.ac.sheffield.dcs.smdStudio.product.diagram.common.GraphProperties;
import uk.ac.sheffield.dcs.smdStudio.product.diagram.smd.SoftwareModulesDiagramGraph;

/**
 * A panel for showing a graphical diagram. It is a kind of package composed by
 * a diagram put in a scroll panel, a side bar for tools and a status bar. This
 * the class to use when you want to work with diagrams outside from Violet (in
 * Eclipse or NetBeans for example)
 * 
 * @author Alexandre de Pellegrin
 */
@SuppressWarnings("serial")
public class DiagramPanel extends JPanel implements IDiagramPanel {
	private String filePath;

	private Graph graph;

	private GraphPanel graphPanel;

	private Id id;

	private boolean isSaveNeeded = false;

	private Vector<DiagramPanelListener> listeners = new Vector<DiagramPanelListener>();

	private JScrollPane scrollableGraphPanel;

	private JScrollPane scrollableSideBar;

	private JScrollPane scrollableStatusBar;

	private ISideBar sideBar;

	private StatusBar statusBar;

	private String title;

	/**
	 * Constructs a diagram panel with the specified graph
	 * 
	 * @param aGraph
	 *            the initial graph
	 */
	public DiagramPanel(Graph aGraph) {
		setGraph(aGraph);
	}

	/**
	 * Constructs a diagram panel with the specified graph and a specified id
	 * 
	 * @param aGraph
	 *            the initial graph
	 * @param id
	 *            unique id
	 */
	public DiagramPanel(Graph aGraph, Id id) {
		this.id = id;
		setGraph(aGraph);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.gui.IDiagramPanel#addListener
	 * (com.horstmann .violet.framework.gui.DiagramPanelListener)
	 */
	public synchronized void addListener(DiagramPanelListener l) {
		if (!this.listeners.contains(l)) {
			this.listeners.addElement(l);
		}
	}

	@SuppressWarnings("unchecked")
	private synchronized Vector<DiagramPanelListener> cloneListeners() {
		return (Vector<DiagramPanelListener>) this.listeners.clone();
	}

	/**
	 * Fire an event to all listeners by calling
	 */
	public void fireMustOpenFile(URL url) {
		Vector<DiagramPanelListener> tl = cloneListeners();
		int size = tl.size();
		if (size == 0)
			return;
		for (int i = 0; i < size; ++i) {
			DiagramPanelListener l = (DiagramPanelListener) tl.elementAt(i);
			l.mustOpenfile(url);
		}
	}

	/**
	 * Fire an event to all listeners by calling
	 */
	private void fireSaveNeeded() {
		Vector<DiagramPanelListener> tl = cloneListeners();
		int size = tl.size();
		if (size == 0)
			return;
		for (int i = 0; i < size; ++i) {
			DiagramPanelListener l = (DiagramPanelListener) tl.elementAt(i);
			l.graphCouldBeSaved();
		}
	}

	/**
	 * Fires a event to indicate that the title has been changed
	 * 
	 * @param newTitle
	 */
	private void fireTitleChanged(String newTitle) {
		Vector<DiagramPanelListener> tl = cloneListeners();
		int size = tl.size();
		if (size == 0)
			return;

		for (int i = 0; i < size; ++i) {
			DiagramPanelListener aListener = (DiagramPanelListener) tl
					.elementAt(i);
			aListener.titleChanged(newTitle);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.gui.IDiagramPanel#getFilePath()
	 */
	public String getFilePath() {
		return filePath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.sheffield.dcs.smdStudio.framework.gui.IDiagramPanel#getGraph()
	 */
	public Graph getGraph() {
		return this.getGraphPanel().getGraph();
	}

	/**
	 * @return the graph panel associated to this diagram panel
	 */
	public GraphPanel getGraphPanel() {
		if (this.graphPanel == null) {
			this.graphPanel = new GraphPanel(this.graph);
		}
		return this.graphPanel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.sheffield.dcs.smdStudio.framework.gui.IDiagramPanel#getId()
	 */
	public Id getId() {
		if (this.id == null) {
			this.id = new Id();
		}
		return this.id;
	}

	/**
	 * @return the scrollable panel containing the graph
	 */
	public JScrollPane getScrollableGraphPanel() {
		if (this.scrollableGraphPanel == null) {
			GraphPanel panel = this.getGraphPanel();
			panel.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON3) {
						getSideBar().getSideToolPanel().reset();
					}
				}
			});
			panel.addMouseWheelListener(new MouseWheelListener() {
				public void mouseWheelMoved(MouseWheelEvent e) {
					int scroll = e.getUnitsToScroll();
					if (scroll > 0) {
						getSideBar().getSideToolPanel().selectNextButton();
					}
					if (scroll < 0) {
						getSideBar().getSideToolPanel().selectPreviousButton();
					}
				}
			});
			panel.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						getSideBar().getSideToolPanel().reset();
					}
				}
			});

			this.scrollableGraphPanel = new JScrollPane(panel);
			this.scrollableGraphPanel.setBackground(ThemeManager.getInstance()
					.getTheme().getWHITE_COLOR());
			this.scrollableGraphPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		}
		return this.scrollableGraphPanel;
	}

	/**
	 * @return scrollpane containing sidebar
	 */
	private JScrollPane getScrollableSideBar() {
		if (this.scrollableSideBar == null) {
			this.scrollableSideBar = new JScrollPane(getSideBar()
					.getAWTComponent());
			this.scrollableSideBar.setAlignmentY(Component.TOP_ALIGNMENT);
			this.scrollableSideBar.getHorizontalScrollBar().setUI(
					new TinyScrollBarUI());
			this.scrollableSideBar.getVerticalScrollBar().setUI(
					new TinyScrollBarUI());
			this.scrollableSideBar.setBorder(new MatteBorder(0, 1, 0, 0,
					ThemeManager.getInstance().getTheme()
							.getSIDEBAR_BORDER_COLOR()));
			this.scrollableSideBar
					.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			this.scrollableSideBar
					.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		}
		return this.scrollableSideBar;
	}

	/**
	 * @return scrollpane containing status bar
	 */
	private JScrollPane getScrollableStatusBar() {
		if (this.scrollableStatusBar == null) {
			StatusBar statBar = getStatusBar();
			this.scrollableStatusBar = new JScrollPane(statBar);
			this.scrollableStatusBar.getHorizontalScrollBar().setUI(
					new TinyScrollBarUI());
			this.scrollableStatusBar.getVerticalScrollBar().setUI(
					new TinyScrollBarUI());
			this.scrollableStatusBar.setBorder(new MatteBorder(1, 0, 0, 0,
					ThemeManager.getInstance().getTheme()
							.getSTATUSBAR_BORDER_COLOR()));
			this.scrollableStatusBar
					.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			this.scrollableStatusBar
					.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		}
		return this.scrollableStatusBar;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.gui.IDiagramPanel#getSideBar()
	 */
	public ISideBar getSideBar() {
		if (this.sideBar == null) {
			PreferencesService preferences = PreferencesServiceFactory
					.getInstance();
			Boolean isSmallSideBarPreferred = new Boolean(preferences.get(
					PreferencesConstant.SMALL_SIDEBAR_PREFERRED, Boolean.FALSE
							.toString()));
			if (!isSmallSideBarPreferred) {
				this.sideBar = new LargeSideBar(this);
			}
			if (isSmallSideBarPreferred) {
				this.sideBar = new SmallSideBar(this);
			}
			this.sideBar.getSideToolPanel().addListener(
					getGraphPanel().getToolListener());
		}
		return this.sideBar;
	}

	/**
	 * @return the current status bar
	 */
	private StatusBar getStatusBar() {
		if (this.statusBar == null) {
			this.statusBar = new StatusBar(this);
		}
		return this.statusBar;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.sheffield.dcs.smdStudio.framework.gui.IDiagramPanel#getTitle()
	 */
	public String getTitle() {
		return title;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.gui.IDiagramPanel#isSaveNeeded()
	 */
	public boolean isSaveNeeded() {
		return this.isSaveNeeded;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeuk.ac.sheffield.dcs.smdStudio.framework.gui.IDiagramPanel#
	 * enlargeReduceSideBar()
	 */
	public void maximizeMinimizeSideBar() {
		PreferencesService preferences = PreferencesServiceFactory
				.getInstance();
		Boolean isSmallSideBarPreferred = new Boolean(preferences.get(
				PreferencesConstant.SMALL_SIDEBAR_PREFERRED, Boolean.FALSE
						.toString()));
		String invertedValue = Boolean.toString(!isSmallSideBarPreferred
				.booleanValue());
		preferences.put(PreferencesConstant.SMALL_SIDEBAR_PREFERRED,
				invertedValue);

		this.sideBar = null;
		ISideBar newSideBar = this.getSideBar();
		getScrollableSideBar().setViewportView(newSideBar.getAWTComponent());
		getScrollableSideBar().setSize(newSideBar.getAWTComponent().getSize());
		newSideBar.getSideToolPanel().addListener(
				getGraphPanel().getToolListener());

		this.statusBar = null;
		StatusBar newStatusBar = this.getStatusBar();
		getScrollableStatusBar().setViewportView(newStatusBar);

		this.refreshDisplay();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.gui.IDiagramPanel#refreshDisplay
	 * ()
	 */
	public void refreshDisplay() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				revalidate();
				doLayout();
				repaint();
			}
		});
	}

	/**
	 * Resets all objet references to prepare this panel to welcome another
	 * diagram.
	 */
	private void reset() {
		this.removeAll();
		this.graph = null;
		this.graphPanel = null;
		this.sideBar = null;
		this.scrollableGraphPanel = null;
		this.statusBar = null;
		this.filePath = null;
		this.title = null;
		this.listeners = new Vector<DiagramPanelListener>();
	}

	/**
	 * Sets a default title
	 * 
	 * @param graph
	 */
	private void setDefaultTitle(Graph graph) {
		ResourceBundle rb = ResourceBundle.getBundle(
				ResourceBundleConstant.MENU_STRINGS, Locale.getDefault());
		if (graph instanceof SoftwareModulesDiagramGraph)
			title = rb.getString("file.new.smd_diagram.text");
		else
			title = "Unknown";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.gui.IDiagramPanel#setFilePath
	 * (java.lang .String)
	 */
	public void setFilePath(String path) {
		filePath = path;
		File file = new File(path);
		setTitle(file.getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.gui.IDiagramPanel#setGraph(com
	 * .horstmann .violet.framework.diagram.Graph)
	 */
	public void setGraph(Graph aGraph) {
		List<GraphModificationListener> backupedGraphListeners = new ArrayList<GraphModificationListener>();
		if (this.graph != null) {
			backupedGraphListeners.addAll(this.graph
					.getGraphModificationListener());
		}
		reset();
		this.graph = aGraph;
		this.graph.addGraphModificationListener(backupedGraphListeners);

		setDefaultTitle(this.graph);
		LayoutManager layout = new BorderLayout();
		setLayout(layout);

		JScrollPane scrollGPanel = getScrollableGraphPanel();
		add(scrollGPanel, BorderLayout.CENTER);
		JScrollPane scrollSideBarPanel = getScrollableSideBar();
		add(scrollSideBarPanel, BorderLayout.EAST);
		JScrollPane scrollStatusBarPanel = getScrollableStatusBar();
		add(scrollStatusBarPanel, BorderLayout.SOUTH);

		// Update title when graph dirty state changes
		graph.addGraphModificationListener(new GraphModificationListener() {
			public void childAttached(Graph g, int index, Node p, Node c) {
				process();
			}

			public void childDetached(Graph g, int index, Node p, Node c) {
				process();
			}

			public void edgeAdded(Graph g, Edge e, Point2D startPoint,
					Point2D endPoint) {
				process();
			}

			public void edgeRemoved(Graph g, Edge e) {
				process();
			}

			@Override
			public void graphPropertiesChanged(Graph g,
					GraphProperties properties) {
				((LargeSideBar) sideBar).updateGraphProperties(properties);
				refreshDisplay();
				process();
			}

			public void nodeAdded(Graph g, Node n, Point2D location) {
				process();
			}

			public void nodeMoved(Graph g, Node n, double dx, double dy) {
				process();
			}

			public void nodeRemoved(Graph g, Node n) {
				process();
			}

			private void process() {
				setSaveNeeded(true);
			}

			public void propertyChangedOnNodeOrEdge(Graph g,
					PropertyChangeEvent event) {
				getGraphPanel().doLayout();
				process();
			}

			@Override
			public void repaintGraph() {
				getGraphPanel().doLayout();
			}
		});
		((LargeSideBar) this.sideBar).updateGraphProperties(aGraph
				.getProperties());
		setSaveNeeded(false);
		refreshDisplay();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.gui.IDiagramPanel#setSaveNeeded
	 * (boolean)
	 */
	public void setSaveNeeded(boolean isSaveNeeded) {
		this.isSaveNeeded = isSaveNeeded;
		if (isSaveNeeded) {
			if (!title.endsWith("*")) {
				setTitle(title + "*");
			}
			fireSaveNeeded();
		}
		if (!isSaveNeeded) {
			if (title.endsWith("*")) {
				setTitle(title.substring(0, title.length() - 1));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.gui.IDiagramPanel#setTitle(java
	 * .lang.String )
	 */
	public void setTitle(String newValue) {
		title = newValue;
		fireTitleChanged(newValue);
	}

}
