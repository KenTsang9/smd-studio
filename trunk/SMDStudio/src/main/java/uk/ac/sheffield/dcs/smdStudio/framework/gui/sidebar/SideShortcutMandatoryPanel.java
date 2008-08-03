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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import uk.ac.sheffield.dcs.smdStudio.framework.action.EditAction;
import uk.ac.sheffield.dcs.smdStudio.framework.action.ViewAction;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.DiagramPanel;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.theme.ThemeManager;
import uk.ac.sheffield.dcs.smdStudio.framework.resources.ResourceFactory;
import uk.ac.sheffield.dcs.smdStudio.framework.swingextension.IconButtonUI;

@SuppressWarnings("serial")
public class SideShortcutMandatoryPanel extends JPanel {

	/**
	 * Default contructor
	 */
	public SideShortcutMandatoryPanel(final DiagramPanel diagramPanel,
			ResourceBundle sideBarResourceBundle, boolean isFullSize) {
		this.diagramPanel = diagramPanel;
		this.viewAction = new ViewAction();
		this.editAction = new EditAction();
		setBackground(ThemeManager.getInstance().getTheme()
				.getSIDEBAR_ELEMENT_BACKGROUND_COLOR());
		ResourceFactory factory = new ResourceFactory(sideBarResourceBundle);

		double scalingValue = FULLSIZE_SCALING_FACTOR;
		if (!isFullSize) {
			scalingValue = SMALLSIZE_SCALING_FACTOR;
		}

		JButton bZoomIn = factory.createButton("zoomin");
		bZoomIn.setUI(new IconButtonUI(scalingValue));
		bZoomIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				performZoomIn();
			}
		});
		JButton bZoomOut = factory.createButton("zoomout");
		bZoomOut.setUI(new IconButtonUI(scalingValue));
		bZoomOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				performZoomOut();
			}
		});
		JButton bUndo = factory.createButton("undo");
		bUndo.setUI(new IconButtonUI(scalingValue));
		bUndo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				performUndo();
			}
		});
		JButton bRedo = factory.createButton("redo");
		bRedo.setUI(new IconButtonUI(scalingValue));
		bRedo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				performRedo();
			}
		});

		JButton bDelete = factory.createButton("delete");
		bDelete.setUI(new IconButtonUI(scalingValue));
		bDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				performDelete();
			}
		});

		this.toolsPanel = new JPanel();
		this.toolsPanel.setOpaque(false);
		this.toolsPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		this.toolsPanel.add(bUndo);
		this.toolsPanel.add(bZoomIn);
		this.toolsPanel.add(bZoomOut);
		this.toolsPanel.add(bDelete);
		this.toolsPanel.add(bRedo);

		if (!isFullSize) {
			BoxLayout boxLayout = new BoxLayout(this.toolsPanel,
					BoxLayout.Y_AXIS);
			bUndo.setBorder(new EmptyBorder(5, 0, 5, 0));
			bRedo.setBorder(new EmptyBorder(5, 0, 5, 0));
			bZoomIn.setBorder(new EmptyBorder(5, 0, 5, 0));
			bZoomOut.setBorder(new EmptyBorder(5, 0, 5, 0));
			bDelete.setBorder(new EmptyBorder(5, 0, 5, 0));
			this.toolsPanel.setLayout(boxLayout);
		}

		if (isFullSize) {
			GridBagLayout layout = new GridBagLayout();
			this.toolsPanel.setLayout(layout);

			GridBagConstraints c1 = new GridBagConstraints();
			c1.anchor = GridBagConstraints.CENTER;
			c1.insets = new Insets(0, 0, 5, 15);
			c1.gridx = 0;
			c1.gridy = 0;
			layout.setConstraints(bUndo, c1);

			GridBagConstraints c2 = new GridBagConstraints();
			c2.anchor = GridBagConstraints.CENTER;
			c2.insets = new Insets(0, 0, 5, 15);
			c2.gridx = 1;
			c2.gridy = 0;
			layout.setConstraints(bZoomIn, c2);

			GridBagConstraints c3 = new GridBagConstraints();
			c3.anchor = GridBagConstraints.CENTER;
			c3.insets = new Insets(0, 0, 5, 15);
			c3.gridx = 2;
			c3.gridy = 0;
			layout.setConstraints(bZoomOut, c3);

			GridBagConstraints c4 = new GridBagConstraints();
			c4.anchor = GridBagConstraints.CENTER;
			c4.insets = new Insets(0, 0, 5, 15);
			c4.gridx = 3;
			c4.gridy = 0;
			layout.setConstraints(bDelete, c4);

			GridBagConstraints c5 = new GridBagConstraints();
			c5.anchor = GridBagConstraints.CENTER;
			c5.insets = new Insets(0, 0, 5, 0);
			c5.gridx = 4;
			c5.gridy = 0;
			layout.setConstraints(bRedo, c5);
		}

		setLayout(new FlowLayout(FlowLayout.CENTER));
		add(this.toolsPanel);
	}

	private void performZoomIn() {
		viewAction.zoomIn(diagramPanel);
	}

	private void performZoomOut() {
		viewAction.zoomOut(diagramPanel);
	}

	private void performUndo() {
		editAction.undo(diagramPanel);
	}

	private void performRedo() {
		editAction.redo(diagramPanel);
	}

	private void performDelete() {
		editAction.delete(diagramPanel);
	}

	/**
	 * Related diagram panel
	 */
	private DiagramPanel diagramPanel;

	/**
	 * "View" action manager
	 */
	private ViewAction viewAction;

	/**
	 * "Edit" action manager
	 */
	private EditAction editAction;

	/**
	 * Panel containing tools
	 */
	private JPanel toolsPanel;

	/**
	 * Full size icon scaling factor
	 */
	private static final double FULLSIZE_SCALING_FACTOR = 1;

	/**
	 * Small size icon scaling factor
	 */
	private static final double SMALLSIZE_SCALING_FACTOR = 0.8;

}
