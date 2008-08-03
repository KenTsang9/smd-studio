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

package uk.ac.sheffield.dcs.smdStudio.framework.gui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;

import uk.ac.sheffield.dcs.smdStudio.framework.action.EditAction;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.DiagramPanel;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.EditorFrame;
import uk.ac.sheffield.dcs.smdStudio.framework.resources.ResourceFactory;

/**
 * Edit menu
 * 
 * @author Alexandre de Pellegrin
 * 
 */
@SuppressWarnings("serial")
public class EditMenu extends JMenu {

	/**
	 * Default constructor
	 * 
	 * @param editorFrame
	 *            where is attached this menu
	 * @param factory
	 *            for accessing to external resources
	 */
	public EditMenu(EditorFrame editorFrame, ResourceFactory factory) {
		this.tabbedPane = editorFrame.getTabbedPane();
		this.factory = factory;
		this.editAction = new EditAction();
		this.createMenu();
	}

	/**
	 * Initialises menu
	 */
	private void createMenu() {
		this.factory.configureMenu(this, "edit");

		JMenuItem undo = factory.createMenuItem("edit.undo");
		undo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				performUndo();
			}
		});
		this.add(undo);

		JMenuItem redo = factory.createMenuItem("edit.redo");
		redo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				performRedo();
			}
		});
		this.add(redo);

		JMenuItem properties = factory.createMenuItem("edit.properties");
		properties.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				performEditProperties();
			}
		});
		this.add(properties);

		JMenuItem delete = factory.createMenuItem("edit.delete");
		delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				performDelete();
			}
		});
		this.add(delete);

		JMenuItem selectNext = factory.createMenuItem("edit.select_next");
		selectNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				performSelectNext();
			}
		});
		this.add(selectNext);

		JMenuItem selectPrevious = factory
				.createMenuItem("edit.select_previous");
		selectPrevious.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				performSelectPrevious();
			}
		});
		this.add(selectPrevious);

	}

	/**
	 * Performs undo action
	 */
	private void performUndo() {
		DiagramPanel diagramPanel = (DiagramPanel) tabbedPane
				.getSelectedComponent();
		editAction.undo(diagramPanel);
	}

	/**
	 * Perfoems redo action
	 */
	private void performRedo() {
		DiagramPanel diagramPanel = (DiagramPanel) tabbedPane
				.getSelectedComponent();
		editAction.redo(diagramPanel);
	}

	/**
	 * Performs edit selected item action
	 */
	private void performEditProperties() {
		DiagramPanel diagramPanel = (DiagramPanel) tabbedPane
				.getSelectedComponent();
		editAction.edit(diagramPanel);
	}

	/**
	 * Performs delete selected item action
	 */
	private void performDelete() {
		DiagramPanel diagramPanel = (DiagramPanel) tabbedPane
				.getSelectedComponent();
		editAction.delete(diagramPanel);
	}

	/**
	 * Performs select next item on diagram panel
	 */
	private void performSelectNext() {
		DiagramPanel diagramPanel = (DiagramPanel) tabbedPane
				.getSelectedComponent();
		editAction.selectNext(diagramPanel);
	}

	/**
	 * Performs select previous item on diagram panel
	 */
	private void performSelectPrevious() {
		DiagramPanel diagramPanel = (DiagramPanel) tabbedPane
				.getSelectedComponent();
		editAction.selectPrevious(diagramPanel);
	}

	/**
	 * REeource factory to acces to external ressources such as icons, string
	 * labels...
	 */
	private ResourceFactory factory;

	/**
	 * Tabbed pane that contaisn all diagrams
	 */
	private JTabbedPane tabbedPane;

	/**
	 * Edit actions
	 */
	private EditAction editAction;

}
