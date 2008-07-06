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

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import uk.ac.sheffield.dcs.smdStudio.framework.action.FileAction;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.DiagramPanel;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.theme.ThemeManager;
import uk.ac.sheffield.dcs.smdStudio.framework.resources.ResourceFactory;
import uk.ac.sheffield.dcs.smdStudio.framework.swingextension.IconButtonUI;


// EDITED Remote file functions has been removed
@SuppressWarnings("serial")
public class SideShortcutOptionalPanel extends JPanel implements
		ISideShortcutOptionalPanel {

	/**
	 * Default contructor
	 */
	public SideShortcutOptionalPanel(DiagramPanel diagramPanel,
			ResourceBundle sideBarResourceBundle) {
		this.diagramPanel = diagramPanel;
		// this.helpAction = new HelpAction();
		this.fileAction = new FileAction();

		setBackground(ThemeManager.getInstance().getTheme()
				.getSIDEBAR_ELEMENT_BACKGROUND_COLOR());
		ResourceFactory factory = new ResourceFactory(sideBarResourceBundle);

		// JButton bHelp = getHelpButton(factory);
		// addButton(bHelp);

		JButton bPrint = getPrintButton(factory);
		addButton(bPrint);

		JButton bExportToClipboard = getExportToClipboardButton(factory);
		addButton(bExportToClipboard);

		this.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		add(getPanel());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.sheffield.dcs.smdStudio.framework.gui.sidebar.ISideShortcutOptionalPanel
	 * #addButton(javax.swing.JButton)
	 */
	public void addButton(JButton aButton) {
		aButton.setUI(new IconButtonUI());
		getPanel().add(aButton);
	}

	private JButton getExportToClipboardButton(ResourceFactory factory) {
		JButton bExportToClipboard = factory
				.createButton("export_to_clipboard");
		bExportToClipboard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				performExportToClipboard();
			}
		});
		return bExportToClipboard;
	}

	private JButton getPrintButton(ResourceFactory factory) {
		JButton bPrint = factory.createButton("print");
		bPrint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				performPrint();
			}
		});
		return bPrint;
	}

	@SuppressWarnings("unused")
	private JButton getHelpButton(ResourceFactory factory) {
		JButton bHelp = factory.createButton("help");
		bHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				performShowHelp();
			}
		});
		return bHelp;
	}

	private JPanel getPanel() {
		if (this.panel == null) {
			this.panel = new JPanel();
			this.panel.setOpaque(false);
			this.panel.setBorder(new EmptyBorder(0, 5, 0, 0));
			GridLayout layout = new GridLayout(0, 5, 15, 10);
			this.panel.setLayout(layout);
		}
		return this.panel;
	}

	private void performShowHelp() {
		// TODO : insert direct link to website
	}

	private void performPrint() {
		fileAction.print(diagramPanel);
	}

	private void performExportToClipboard() {
		fileAction.exportToClipboard(diagramPanel);
	}

	/**
	 * "File" action manager
	 */
	private FileAction fileAction;

	/**
	 * Current diagram panel
	 */
	private DiagramPanel diagramPanel;

	/**
	 * Component(s panel
	 */
	private JPanel panel;

}
