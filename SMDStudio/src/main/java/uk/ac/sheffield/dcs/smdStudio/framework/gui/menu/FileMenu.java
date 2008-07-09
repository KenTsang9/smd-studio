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
import java.io.File;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import uk.ac.sheffield.dcs.smdStudio.framework.action.FileAction;
import uk.ac.sheffield.dcs.smdStudio.framework.diagram.Graph;
import uk.ac.sheffield.dcs.smdStudio.framework.file.FileChooserService;
import uk.ac.sheffield.dcs.smdStudio.framework.file.FileChooserServiceFactory;
import uk.ac.sheffield.dcs.smdStudio.framework.file.FileService;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.DiagramPanel;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.EditorFrame;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.IDiagramPanel;
import uk.ac.sheffield.dcs.smdStudio.framework.resources.ResourceFactory;
import uk.ac.sheffield.dcs.smdStudio.product.diagram.smd.SoftwareModulesDiagramGraph;

/**
 * Represents the file menu on the editor frame
 * 
 * @author Alexandre de Pellegrin
 * 
 * 
 *         EDITED Remote file opening has been removed
 */
@SuppressWarnings("serial")
public class FileMenu extends JMenu {

	/**
	 * Default constructor
	 * 
	 * @param editorFrame
	 * @param factory
	 *            to access to menu external resources such as string labels and
	 *            icons paths
	 */
	public FileMenu(EditorFrame editorFrame, ResourceFactory factory) {
		this.menuFactory = factory;
		this.editorFrame = editorFrame;
		this.fileChooserService = FileChooserServiceFactory.getInstance();
		this.recentFiles = FileService.getRecentFiles();
		this.createMenu();
	}

	/**
	 * Initialize the menu
	 */
	private void createMenu() {
		this.menuFactory.configureMenu(this, "file");
		fileNewMenu = this.menuFactory.createMenu("file.new");
		createGraphTypeMenuItem("file.new.smd_diagram",
				SoftwareModulesDiagramGraph.class, this.menuFactory,
				this.editorFrame);
		this.add(this.fileNewMenu);

		JMenuItem fileOpenItem = this.menuFactory.createMenuItem("file.open");
		fileOpenItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				performOpen();
			}
		});
		this.add(fileOpenItem);

		JMenuItem fileCloseItem = this.menuFactory.createMenuItem("file.close");
		fileCloseItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				performClose();
			}
		});
		this.add(fileCloseItem);

		this.fileRecentMenu = this.menuFactory.createMenu("file.recent");
		buildRecentFilesMenu();
		this.add(this.fileRecentMenu);

		JMenuItem fileSaveItem = this.menuFactory.createMenuItem("file.save");
		fileSaveItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				performSave();
			}

		});
		this.add(fileSaveItem);

		JMenuItem fileSaveAsItem = this.menuFactory
				.createMenuItem("file.save_as");
		fileSaveAsItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				performSaveAs();
			}
		});
		this.add(fileSaveAsItem);

		JMenuItem fileExportItem = this.menuFactory
				.createMenuItem("file.export_to_image");
		fileExportItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				performExportToImage();
			}
		});

		JMenuItem fileExportToClipBoard = this.menuFactory
				.createMenuItem("file.export_to_clipboard");
		fileExportToClipBoard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				performExportToClipBoard();
			}
		});

		JMenuItem fileExportToXMI = this.menuFactory
				.createMenuItem("file.export_to_xmi");
		fileExportToXMI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				performExportToXMI();
			}
		});

		JMenuItem fileExportToJava = this.menuFactory
				.createMenuItem("file.export_to_java");
		fileExportToJava.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				performExportToJava();
			}
		});

		JMenuItem fileExportToPython = this.menuFactory
				.createMenuItem("file.export_to_python");
		fileExportToPython.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				performExportToPython();
			}
		});

		JMenu fileExportMenu = this.menuFactory.createMenu("file.export");
		fileExportMenu.add(fileExportItem);
		fileExportMenu.add(fileExportToClipBoard);
		// fileExportMenu.add(fileExportToXMI);
		// fileExportMenu.add(fileExportToJava);
		// fileExportMenu.add(fileExportToPython);
		this.add(fileExportMenu);

		JMenuItem filePrintItem = this.menuFactory.createMenuItem("file.print");
		filePrintItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				performPrint();
			}
		});
		this.add(filePrintItem);

		JMenuItem fileExitItem = this.menuFactory.createMenuItem("file.exit");
		fileExitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				performExit();
			}
		});
		this.add(fileExitItem);

		if (this.fileChooserService == null) {
			fileOpenItem.setEnabled(false);
			fileSaveAsItem.setEnabled(false);
			fileExportItem.setEnabled(false);
			fileExportToClipBoard.setEnabled(false);
			filePrintItem.setEnabled(false);
			fileExitItem.setEnabled(false);
		}

		if (this.fileChooserService == null
				|| this.fileChooserService.isWebStart()) {
			this.fileRecentMenu.setEnabled(false);
			fileSaveItem.setEnabled(false);
		}
	}

	/**
	 * Creates a graph type menuitem to attach to the File->New menu.
	 * 
	 * @param resourceName
	 *            the name of the menu item resource
	 * @param graphClass
	 *            the class object for the graph
	 * @param factory
	 *            resource factory
	 * @param editorFrame
	 *            use in action
	 */
	public void createGraphTypeMenuItem(String resourceName,
			final Class<? extends Graph> graphClass, ResourceFactory factory,
			final EditorFrame editorFrame) {
		JMenuItem item = factory.createMenuItem(resourceName);
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				performNew(graphClass);
			}
		});
		fileNewMenu.add(item);
	}

	/**
	 * Adds a file name to the "recent files" list and rebuilds the
	 * "recent files" menu.
	 * 
	 * @param newFile
	 *            the file name to add
	 */
	public void addRecentFile(String newFile) {
		recentFiles.remove(newFile);
		if (newFile == null || newFile.equals(""))
			return;
		recentFiles.add(0, newFile);
		for (int i = 5; i < recentFiles.size(); i++) {
			recentFiles.remove(i);
		}
		buildRecentFilesMenu();
	}

	/**
	 * Returns the list of recently opened files
	 * 
	 * @return String list
	 */
	public List<String> getRecentFiles() {
		return this.recentFiles;
	}

	/**
	 * @return "new file" menu
	 */
	public JMenu getFileNewMenu() {
		return this.fileNewMenu;
	}

	/**
	 * 
	 * @return "recent file" menu
	 */
	public JMenu getFileRecentMenu() {
		return this.fileRecentMenu;
	}

	/**
	 * Rebuilds the "recent files" menu.
	 */
	private void buildRecentFilesMenu() {
		fileRecentMenu.removeAll();
		for (int i = 0; i < recentFiles.size(); i++) {
			final String file = (String) recentFiles.get(i);
			String name = new File(file).getName();
			// if (i < 10) name = i + " " + name;
			// else if (i == 10) name = "0 " + name;
			JMenuItem item = new JMenuItem(name);
			fileRecentMenu.add(item);
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					fileAction.open(file, editorFrame);
				}
			});
		}
	}

	/**
	 * Performs open action
	 */
	private void performOpen() {
		fileAction.open(this, fileChooserService, editorFrame);
	}

	/**
	 * Performs open action
	 */
	private void performClose() {
		DiagramPanel diagramPanel = (DiagramPanel) editorFrame.getTabbedPane()
				.getSelectedComponent();
		if (diagramPanel != null) {
			fileAction.close(this, editorFrame, diagramPanel,
					fileChooserService);
		}
	}

	/**
	 * Performs save action
	 */
	private void performSave() {
		DiagramPanel diagramPanel = (DiagramPanel) editorFrame.getTabbedPane()
				.getSelectedComponent();
		if (diagramPanel != null) {
			fileAction.save(this, diagramPanel, fileChooserService);
		}
	}

	/**
	 * Performs save as action
	 */
	private void performSaveAs() {
		DiagramPanel diagramPanel = (DiagramPanel) editorFrame.getTabbedPane()
				.getSelectedComponent();
		if (diagramPanel != null) {
			fileAction.saveAs(this, diagramPanel, fileChooserService);
		}
	}

	/**
	 * Performs export to image action
	 */
	private void performExportToImage() {
		DiagramPanel diagramPanel = (DiagramPanel) editorFrame.getTabbedPane()
				.getSelectedComponent();
		if (diagramPanel != null) {
			fileAction.exportImage(diagramPanel, fileChooserService);
		}
	}

	/**
	 * Performs export to clipboard action
	 */
	private void performExportToClipBoard() {
		DiagramPanel diagramPanel = (DiagramPanel) editorFrame.getTabbedPane()
				.getSelectedComponent();
		if (diagramPanel != null) {
			fileAction.exportToClipboard(diagramPanel);
		}
	}

	/**
	 * Performs export to XMI file
	 */
	private void performExportToXMI() {
		DiagramPanel diagramPanel = (DiagramPanel) editorFrame.getTabbedPane()
				.getSelectedComponent();
		if (diagramPanel != null) {
			fileAction.exportToXMI(diagramPanel, fileChooserService);
		}
	}

	/**
	 * Performs export to Java code
	 */
	private void performExportToJava() {
		IDiagramPanel diagramPanel = (IDiagramPanel) editorFrame
				.getTabbedPane().getSelectedComponent();
		if (diagramPanel != null) {
		}
	}

	/**
	 * Performs export to Python code
	 */
	private void performExportToPython() {
		IDiagramPanel diagramPanel = (IDiagramPanel) editorFrame
				.getTabbedPane().getSelectedComponent();
		if (diagramPanel != null) {
		}
	}

	/**
	 * Performs print action
	 */
	private void performPrint() {
		DiagramPanel diagramPanel = (DiagramPanel) editorFrame.getTabbedPane()
				.getSelectedComponent();
		if (diagramPanel != null) {
			fileAction.print(diagramPanel);
		}
	}

	/**
	 * Performs print action
	 */
	private void performExit() {
		fileAction.exit(this, editorFrame, fileChooserService);
	}

	/**
	 * Perform new action
	 * 
	 * @param graphClass
	 *            graph type to create
	 */
	private void performNew(Class<? extends Graph> graphClass) {
		fileAction.createDiagram(graphClass, editorFrame);
	}

	/**
	 * The file chooser to use with with menu
	 */
	private FileChooserService fileChooserService;

	/**
	 * The editor frame where the menu is attached
	 */
	private EditorFrame editorFrame;

	/**
	 * Recently opened file list
	 */
	private List<String> recentFiles;

	/**
	 * "New file" sub menu
	 */
	private JMenu fileNewMenu;

	/**
	 * Recently opened file list menu
	 */
	private JMenu fileRecentMenu;

	/**
	 * External resource factory (to create menuitem with label, icon...)
	 */
	private ResourceFactory menuFactory;

	/**
	 * File action used to perform... actions. Because it is not the
	 * responsability of this class to perform business code (sometimes, I am
	 * dream that, one day, all codes will respect the Single Reponsability
	 * Principle). Alexandre de Pellegrin
	 */
	private FileAction fileAction = new FileAction();

}
