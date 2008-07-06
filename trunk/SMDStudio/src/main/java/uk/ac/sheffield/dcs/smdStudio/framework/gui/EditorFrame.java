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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Toolkit;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.beans.BeanInfo;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import uk.ac.sheffield.dcs.smdStudio.framework.action.FileAction;
import uk.ac.sheffield.dcs.smdStudio.framework.diagram.AbstractNode;
import uk.ac.sheffield.dcs.smdStudio.framework.diagram.ArrowHead;
import uk.ac.sheffield.dcs.smdStudio.framework.diagram.BentStyle;
import uk.ac.sheffield.dcs.smdStudio.framework.diagram.Graph;
import uk.ac.sheffield.dcs.smdStudio.framework.diagram.LineStyle;
import uk.ac.sheffield.dcs.smdStudio.framework.file.FileChooserServiceFactory;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.menu.MenuFactory;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.theme.Theme;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.theme.ThemeManager;
import uk.ac.sheffield.dcs.smdStudio.framework.resources.ResourceBundleConstant;
import uk.ac.sheffield.dcs.smdStudio.framework.resources.ResourceFactory;


/**
 * This desktop frame contains panes that show graphs.
 */
@SuppressWarnings("serial")
public class EditorFrame extends JFrame {
	/**
	 * Constructs a blank frame with a desktop pane but no graph windows.
	 * 
	 * @param appClassName
	 *            the fully qualified app class name. It is expected that the
	 *            resources are appClassName + "Strings" and appClassName +
	 *            "Version" (the latter for version-specific resources)
	 */
	public EditorFrame() {

		this.editorFrame = this;

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		DialogFactory.getInstance().setDialogOwner(this);

		ResourceBundle resources = ResourceBundle.getBundle(
				ResourceBundleConstant.ABOUT_STRINGS, Locale.getDefault());
		setTitle(resources.getString("app.name"));
		ResourceFactory resourceFactory = new ResourceFactory(resources);
		setIconImage(resourceFactory.createImage("app.icon"));

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidth = (int) screenSize.getWidth();
		int screenHeight = (int) screenSize.getHeight();
		setBounds(screenWidth / 16, screenHeight / 16, screenWidth * 7 / 8,
				screenHeight * 7 / 8);
		// For screenshots only -> setBounds(50, 50, 850, 650);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				performExit();
			}

		});

		// set up menus
		JMenuBar menuBar = new JMenuBar();
		// TODO : Font change has not effect
		menuBar
				.setFont(ThemeManager.getInstance().getTheme()
						.getMENUBAR_FONT());
		MenuFactory menuFactory = getMenuFactory();
		menuBar.add(menuFactory.getFileMenu(this));
		menuBar.add(menuFactory.getEditMenu(this));
		menuBar.add(menuFactory.getViewMenu(this));
		menuBar.add(menuFactory.getHelpMenu(this));
		setJMenuBar(menuBar);

		getContentPane().add(this.getWelcomePanel());

	}

	/**
	 * Perform exit action when window closing is requested
	 */
	private void performExit() {
		FileAction action = new FileAction();
		action.exit(getMenuFactory().getFileMenu(this), this,
				FileChooserServiceFactory.getInstance());
	}

	/**
	 * Perform restart action
	 */
	public void restart() {
		FileAction action = new FileAction();
		action.restart(getMenuFactory().getFileMenu(this), this,
				FileChooserServiceFactory.getInstance());
	}

	/**
	 * Adds a tabbed pane (only if not already added)
	 * 
	 * @param c
	 *            the component to display in the internal frame
	 */
	public void addTabbedPane(final DiagramPanel diagramPanel) {
		replaceWelcomePanelByTabbedPane();

		Component[] diagramPanels = this.getTabbedPane().getComponents();
		boolean isAlreadyAdded = false;
		for (int i = 0; i < diagramPanels.length; i++) {
			if (diagramPanels[i] instanceof DiagramPanel) {
				IDiagramPanel aDiagramPanel = (IDiagramPanel) diagramPanels[i];
				if (aDiagramPanel.getFilePath() != null
						&& aDiagramPanel.getFilePath().equals(
								diagramPanel.getFilePath())) {
					isAlreadyAdded = true;
					return;
				}
			}
		}
		if (!isAlreadyAdded) {
			this.getTabbedPane().add(diagramPanel.getTitle(), diagramPanel);
			diagramPanel.addListener(new DiagramPanelListener() {
				public void titleChanged(String newTitle) {
					int pos = getTabbedPane().indexOfComponent(diagramPanel);
					getTabbedPane().setTitleAt(pos, newTitle);
				}

				public void graphCouldBeSaved() {
					// nothing to do here
				}

				public void mustOpenfile(URL url) {
					FileAction action = new FileAction();
					action.openURL(url, editorFrame);
				}
			});
		}
		// Use invokeLater to prevent exception
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				getTabbedPane().setSelectedComponent(diagramPanel);
				diagramPanel.getGraphPanel().requestFocus();
			}
		});
	}

	private void replaceWelcomePanelByTabbedPane() {
		WelcomePanel welcomePanel = this.getWelcomePanel();
		JTabbedPane tabbedPane = getTabbedPane();
		getContentPane().remove(welcomePanel);
		getContentPane().add(tabbedPane);
		repaint();
	}

	private void replaceTabbedPaneByWelcomePanel() {
		this.welcomePanel = null;
		WelcomePanel welcomePanel = this.getWelcomePanel();
		JTabbedPane tabbedPane = getTabbedPane();
		getContentPane().remove(tabbedPane);
		getContentPane().add(welcomePanel);
		repaint();
	}

	/**
	 * @return the tabbed pane that contains diagram panels
	 */
	public JTabbedPane getTabbedPane() {
		if (this.tabbedPane == null) {
			this.tabbedPane = new JTabbedPane() {
				public void paint(Graphics g) {
					Graphics2D g2 = (Graphics2D) g;
					Paint currentPaint = g2.getPaint();
					Theme LAF = ThemeManager.getInstance().getTheme();
					GradientPaint paint = new GradientPaint(getWidth() / 2,
							-getHeight() / 4, LAF
									.getWELCOME_BACKGROUND_START_COLOR(),
							getWidth() / 2, getHeight() + getHeight() / 4, LAF
									.getWELCOME_BACKGROUND_END_COLOR());
					g2.setPaint(paint);
					g2.fillRect(0, 0, getWidth(), getHeight());
					g2.setPaint(currentPaint);
					super.paint(g);
				}
			};
			this.tabbedPane.setOpaque(false);
			MouseWheelListener[] mouseWheelListeners = this.tabbedPane
					.getMouseWheelListeners();
			for (int i = 0; i < mouseWheelListeners.length; i++) {
				this.tabbedPane
						.removeMouseWheelListener(mouseWheelListeners[i]);
			}
		}
		return this.tabbedPane;
	}

	/**
	 * Removes a diagram panel from this editor frame
	 * 
	 * @param diagramPanel
	 */
	public void removeDiagramPanel(DiagramPanel diagramPanel) {
		JTabbedPane tp = getTabbedPane();
		tp.remove(diagramPanel);
		if (tp.getTabCount() == 0) {
			replaceTabbedPaneByWelcomePanel();
		}
	}

	/**
	 * Looks for an opened diagram from its file path and focus it
	 * 
	 * @param diagramFilePath
	 *            diagram file path
	 */
	public void setActiveDiagramPanel(String diagramFilePath) {
		if (diagramFilePath == null)
			return;
		JTabbedPane tp = getTabbedPane();
		Component[] components = tp.getComponents();
		for (int i = 0; i < components.length; i++) {
			if (components[i] instanceof DiagramPanel) {
				DiagramPanel dPanel = (DiagramPanel) components[i];
				String filePath = dPanel.getFilePath();
				if (diagramFilePath.equals(filePath)) {
					tp.setSelectedComponent(dPanel);
					return;
				}
			}
		}
	}

	/**
	 * @return selected diagram file path (or null if not one is selected; that
	 *         should never happen)
	 */
	public IDiagramPanel getActiveDiagramPanel() {
		JTabbedPane tp = getTabbedPane();
		Component c = tp.getSelectedComponent();
		if (c instanceof DiagramPanel) {
			IDiagramPanel dPanel = (IDiagramPanel) c;
			return dPanel;
		}
		// Should never happen
		return null;
	}

	private WelcomePanel getWelcomePanel() {
		if (this.welcomePanel == null) {
			this.welcomePanel = new WelcomePanel(this.getMenuFactory()
					.getFileMenu(this));
		}
		return this.welcomePanel;
	}

	/**
	 * @return the menu factory instance
	 */
	public MenuFactory getMenuFactory() {
		if (this.menuFactory == null) {
			menuFactory = new MenuFactory();
		}
		return this.menuFactory;
	}

	/**
	 * Tabbed pane instance
	 */
	private JTabbedPane tabbedPane;

	/**
	 * Panel added is not diagram is opened
	 */
	private WelcomePanel welcomePanel;

	/**
	 * Menu factory instance
	 */
	private MenuFactory menuFactory;

	/**
	 * Self reference used for listeners
	 */
	private EditorFrame editorFrame;

	// workaround for bug #4646747 in J2SE SDK 1.4.0
	private static java.util.HashMap<Class<?>, BeanInfo> beanInfos;
	static {
		beanInfos = new java.util.HashMap<Class<?>, BeanInfo>();
		Class<?>[] cls = new Class<?>[] { Point2D.Double.class,
				BentStyle.class, ArrowHead.class, LineStyle.class, Graph.class,
				AbstractNode.class, };
		for (int i = 0; i < cls.length; i++) {
			try {
				beanInfos.put(cls[i], java.beans.Introspector
						.getBeanInfo(cls[i]));
			} catch (java.beans.IntrospectionException ex) {
			}
		}
	}
}
