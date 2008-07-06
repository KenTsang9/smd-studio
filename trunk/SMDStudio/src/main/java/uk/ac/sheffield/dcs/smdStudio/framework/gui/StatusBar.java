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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import uk.ac.sheffield.dcs.smdStudio.framework.gui.sidebar.ISideBar;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.sidebar.ISideToolPanel;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.sidebar.SideToolPanel;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.sidebar.Tool;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.theme.Theme;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.theme.ThemeManager;
import uk.ac.sheffield.dcs.smdStudio.framework.resources.ResourceBundleConstant;
import uk.ac.sheffield.dcs.smdStudio.framework.resources.ResourceFactory;
import uk.ac.sheffield.dcs.smdStudio.framework.swingextension.CustomToggleButton;
import uk.ac.sheffield.dcs.smdStudio.framework.swingextension.LinkButtonUI;


@SuppressWarnings("serial")
public class StatusBar extends JPanel {

	/**
	 * Default constructor
	 * 
	 * @param diagram
	 *            panel embedding this status bar
	 */
	public StatusBar(final IDiagramPanel diagramPanel) {
		ISideBar sideBar = diagramPanel.getSideBar();
		setLayout(new GridBagLayout());
		JPanel toolViewer = getDiagramToolViewer((SideToolPanel) sideBar
				.getSideToolPanel());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(2, 4, 3, 5);
		c.weightx = 1;
		c.gridx = 0;
		c.gridy = 0;
		add(toolViewer, c);
		JButton sideBarLink = getSideBarLink(diagramPanel);
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(2, 5, 3, 5);
		c.weightx = 1;
		c.gridx = 1;
		c.gridy = 0;
		add(sideBarLink, c);
		Theme cLAF = ThemeManager.getInstance().getTheme();
		setBackground(cLAF.getSTATUSBAR_BACKGROUND_COLOR());
		setBorder(new EmptyBorder(0, 0, 0, 0));
	}

	/**
	 * Create side bar link to show / hide it
	 * 
	 * @param diagram
	 *            panel embedding this status bar and responsible to change the
	 *            sidebar type
	 * @return link label
	 */
	private JButton getSideBarLink(final IDiagramPanel diagramPanel) {
		JButton link = getResourceFactory().createButton("sidebarlink");
		link.setUI(new LinkButtonUI());
		link.setForeground(ThemeManager.getInstance().getTheme()
				.getMENUBAR_FOREGROUND_COLOR());
		link.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				diagramPanel.maximizeMinimizeSideBar();
			}
		});
		return link;
	}

	private JPanel getDiagramToolViewer(final ISideToolPanel sideToolPanel) {
		Tool selectedTool = sideToolPanel.getSelectedTool();
		Theme cLAF = ThemeManager.getInstance().getTheme();
		final CustomToggleButton button = new CustomToggleButton(selectedTool
				.getLabel(), selectedTool.getIcon(), cLAF
				.getTOGGLEBUTTON_SELECTED_COLOR(), cLAF
				.getTOGGLEBUTTON_SELECTED_BORDER_COLOR(), cLAF
				.getTOGGLEBUTTON_UNSELECTED_COLOR());
		button.setSelected(true);
		button.setPreferredSize(new Dimension(DIAGRAMTOOL_VIEWER_WIDTH,
				(int) button.getPreferredSize().getHeight()));
		sideToolPanel.addListener(new SideToolPanel.Listener() {
			public void toolSelectionChanged(Tool tool) {
				final String text = tool.getLabel();
				final Icon icon = tool.getIcon();
				button.setText(text);
				button.setIcon(icon);
				button.setToolTipText(text);
				button.repaint();
			}
		});
		return button;
	}

	/**
	 * @return resource factory associated to statusString.properties file
	 */
	private ResourceFactory getResourceFactory() {
		if (this.resourceFactory == null) {
			ResourceBundle resourceBundle = ResourceBundle.getBundle(
					ResourceBundleConstant.STATUSBAR_STRINGS, Locale
							.getDefault());
			this.resourceFactory = new ResourceFactory(resourceBundle);
		}
		return this.resourceFactory;
	}

	/**
	 * Resource factory instance
	 */
	private ResourceFactory resourceFactory;

	private static final int DIAGRAMTOOL_VIEWER_WIDTH = 350;

}
