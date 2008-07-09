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

package uk.ac.sheffield.dcs.smdStudio.framework.gui.sidebar;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

import uk.ac.sheffield.dcs.smdStudio.framework.gui.DiagramPanel;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.theme.ThemeManager;
import uk.ac.sheffield.dcs.smdStudio.framework.resources.ResourceBundleConstant;
import uk.ac.sheffield.dcs.smdStudio.product.diagram.common.GraphProperties;

import com.l2fprod.common.swing.JTaskPane;
import com.l2fprod.common.swing.JTaskPaneGroup;

@SuppressWarnings("serial")
public class LargeSideBar extends JPanel implements ISideBar {

	private DiagramPanel diagramPanel;

	private ResourceBundle resourceBundle;

	private SideShortcutMandatoryPanel sideShortcutMandatoryPanel;

	private SideShortcutOptionalPanel sideShortcutOptionalPanel;

	private SideSMDPropertiesPanel sideSMDPropertiesPanel;

	private SideToolPanel sideToolPanel;

	private JTaskPane taskPane;

	public LargeSideBar(DiagramPanel diagramPanel) {
		super();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.diagramPanel = diagramPanel;
		taskPane = new JTaskPane();
		add(taskPane);
		this.addElement(this.getSideShortcutMandatoryPanel(), this
				.getSideBarResourceBundle().getString("title.standardbuttons"));
		this.addElement(this.getSideToolPanel(), this
				.getSideBarResourceBundle().getString("title.diagramtools"));
		this.addElement((Component) this.getSideShortcutOptionalPanel(), this
				.getSideBarResourceBundle()
				.getString("title.extendedfunctions"));
		this.addElement((Component) this.getSideSMDPropertiesPanel(), this
				.getSideBarResourceBundle().getString("title.smdProperties"));
		setBorder(new MatteBorder(0, 1, 0, 0, ThemeManager.getInstance()
				.getTheme().getSIDEBAR_BORDER_COLOR()));
		fixWidth();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.plugin.VisitableSideBar#addElement
	 * (java.awt.Component, java.lang.String)
	 */
	public void addElement(final Component c, String title) {
		JTaskPaneGroup group = new JTaskPaneGroup();
		group.setFont(group.getFont().deriveFont(Font.PLAIN));
		group.setTitle(title);
		group.setLayout(new BorderLayout());
		group.add(c, BorderLayout.CENTER);
		taskPane.add(group);
	}

	private void fixWidth() {
		JLabel sizer = new JLabel();
		sizer.setPreferredSize(new Dimension(215, 1));
		taskPane.add(sizer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.gui.sidebar.ISideBar#getAWTComponent
	 * ()
	 */
	public Component getAWTComponent() {
		return this;
	}

	/**
	 * @return resource bundle
	 */
	private ResourceBundle getSideBarResourceBundle() {
		if (this.resourceBundle == null) {
			this.resourceBundle = ResourceBundle
					.getBundle(ResourceBundleConstant.SIDEBAR_STRINGS, Locale
							.getDefault());
		}
		return this.resourceBundle;
	}

	private SideShortcutMandatoryPanel getSideShortcutMandatoryPanel() {
		if (this.sideShortcutMandatoryPanel == null) {
			this.sideShortcutMandatoryPanel = new SideShortcutMandatoryPanel(
					this.diagramPanel, this.getSideBarResourceBundle(), true);
		}
		return this.sideShortcutMandatoryPanel;
	}

	private SideShortcutOptionalPanel getSideShortcutOptionalPanel() {
		if (this.sideShortcutOptionalPanel == null) {
			this.sideShortcutOptionalPanel = new SideShortcutOptionalPanel(
					this.diagramPanel, this.getSideBarResourceBundle());
		}
		return this.sideShortcutOptionalPanel;
	}

	private SideSMDPropertiesPanel getSideSMDPropertiesPanel() {
		if (this.sideSMDPropertiesPanel == null) {
			this.sideSMDPropertiesPanel = new SideSMDPropertiesPanel(
					this.diagramPanel, this.getSideBarResourceBundle());
		}
		return this.sideSMDPropertiesPanel;
	}

	public SideToolPanel getSideToolPanel() {
		if (this.sideToolPanel == null) {
			this.sideToolPanel = new SideToolPanel(this.diagramPanel
					.getGraphPanel().getGraph());
		}
		return this.sideToolPanel;
	}

	public void updateGraphProperties(GraphProperties properties) {
		sideSMDPropertiesPanel.setTeamQuality(properties.getTeamQuality());
		sideSMDPropertiesPanel.setTrainingCost(properties.getTrainingCost());
	}

}
