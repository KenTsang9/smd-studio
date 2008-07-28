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

package uk.ac.sheffield.dcs.smdStudio.framework.action;

import java.awt.Component;
import java.awt.Font;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.border.EmptyBorder;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import uk.ac.sheffield.dcs.smdStudio.UMLEditor;
import uk.ac.sheffield.dcs.smdStudio.framework.diagram.Graph;
import uk.ac.sheffield.dcs.smdStudio.framework.diagram.GraphService;
import uk.ac.sheffield.dcs.smdStudio.framework.file.ExtensionFilter;
import uk.ac.sheffield.dcs.smdStudio.framework.file.FileChooserService;
import uk.ac.sheffield.dcs.smdStudio.framework.file.FileOpenerHandler;
import uk.ac.sheffield.dcs.smdStudio.framework.file.FileSaverHandler;
import uk.ac.sheffield.dcs.smdStudio.framework.file.FileService;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.DiagramPanel;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.DialogFactory;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.EditorFrame;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.IDiagramPanel;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.PrintPanel;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.menu.FileMenu;
import uk.ac.sheffield.dcs.smdStudio.framework.resources.ResourceBundleConstant;
import uk.ac.sheffield.dcs.smdStudio.product.diagram.smd.SoftwareModulesDiagramGraph;

/**
 * This class concentrates actions that belong to the file menu
 * 
 * @author Alexandre de Pellegrin
 * 
 *         EDITED Remote file opening has been removed
 */
public class FileAction {

	/**
	 * Default constructor
	 */
	public FileAction() {
		menuResourceBundle = ResourceBundle.getBundle(
				ResourceBundleConstant.MENU_STRINGS, Locale.getDefault());
	}

	/**
	 * Open a diagram file chosen with the file chooser service.
	 * 
	 * @param menu
	 *            for adding selected file to recent files
	 * @param fileChooserService
	 * @param editorFrame
	 *            to add a new Diagram Panel
	 */
	public void open(FileMenu menu, FileChooserService fileChooserService,
			EditorFrame editorFrame) {
		try {
			FileOpenerHandler open = fileChooserService.open(null, null,
					FileService.getFileFilters());
			InputStream in = open.getInputStream();
			if (in != null) {
				Graph graph = GraphService.readGraph(in);
				DiagramPanel frame = new DiagramPanel(graph);
				editorFrame.addTabbedPane(frame);
				frame.setFilePath(open.getName());
				menu.addRecentFile(open.getName());
				FileService.addOpenedFile(open.getName());
			}
		} catch (IOException e) {
			DialogFactory.getInstance().showErrorDialog(e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * Opens a file from an URL--used by applet and Eclipse plugin
	 * 
	 * @param url
	 *            the URL
	 * @param editorFrame
	 */
	public void openURL(URL url, EditorFrame editorFrame) {
		try {
			InputStream in = url.openStream();
			if (in != null) {
				Graph graph = GraphService.readGraph(in);
				DiagramPanel frame = new DiagramPanel(graph);
				editorFrame.addTabbedPane(frame);
			}
		} catch (IOException e) {
			DialogFactory.getInstance().showErrorDialog(e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * Opens a file with the given name, or switches to the frame if it is
	 * already open.
	 * 
	 * @param path
	 *            the file path (relative or absolute)
	 * @param editorFrame
	 */
	public void open(String path, EditorFrame editorFrame) {
		try {
			Graph graph = GraphService.readGraph(new FileInputStream(path));
			if (graph == null)
				return;
			DiagramPanel frame = new DiagramPanel(graph);
			editorFrame.addTabbedPane(frame);
			frame.setFilePath(path);
			FileService.addOpenedFile(path);
		} catch (Exception e) {
			DialogFactory.getInstance().showErrorDialog(e.getMessage());
			// throw new RuntimeException(e);
		}
	}

	/**
	 * Opens a list of files
	 * 
	 * @param filePaths
	 *            the file paths
	 * @param editorFrame
	 */
	public void open(String[] filePaths, EditorFrame editorFrame) {
		for (int i = 0; i < filePaths.length; i++) {
			String aPath = filePaths[i];
			this.open(aPath, editorFrame);
		}
	}

	/**
	 * Closes an edited diagram
	 * 
	 * @param menu
	 * @param editorFrame
	 * @param diagramPanel
	 * @param fileChooserService
	 */
	public void close(FileMenu menu, EditorFrame editorFrame,
			DiagramPanel diagramPanel, FileChooserService fileChooserService) {
		if (diagramPanel.isSaveNeeded()) {
			String message = menuResourceBundle.getString("dialog.close.ok");
			String title = menuResourceBundle.getString("dialog.close.title");
			ImageIcon icon = new ImageIcon(this.getClass().getResource(
					menuResourceBundle.getString("dialog.close.icon")));
			JOptionPane optionPane = new JOptionPane();
			optionPane.setMessage(message);
			optionPane.setOptionType(JOptionPane.YES_NO_CANCEL_OPTION);
			optionPane.setIcon(icon);
			DialogFactory.getInstance().showDialog(optionPane, title, true);

			int result = JOptionPane.CANCEL_OPTION;
			if (!JOptionPane.UNINITIALIZED_VALUE.equals(optionPane.getValue())) {
				result = ((Integer) optionPane.getValue()).intValue();
			}

			if (result == JOptionPane.YES_OPTION) {
				save(menu, diagramPanel, fileChooserService);
				editorFrame.removeDiagramPanel(diagramPanel);
				FileService.removeOpenedFile(diagramPanel.getFilePath());
			}
			if (result == JOptionPane.NO_OPTION) {
				editorFrame.removeDiagramPanel(diagramPanel);
				FileService.removeOpenedFile(diagramPanel.getFilePath());
			}
		}
		if (!diagramPanel.isSaveNeeded()) {
			editorFrame.removeDiagramPanel(diagramPanel);
			FileService.removeOpenedFile(diagramPanel.getFilePath());
		}

	}

	/**
	 * SAves a diagram. If it hasn't a file yet, the file chooser service is
	 * invoked
	 * 
	 * @param diagramPanel
	 * @param fileChooserService
	 */
	public void save(FileMenu menu, DiagramPanel diagramPanel,
			FileChooserService fileChooserService) {
		if (diagramPanel == null)
			return;
		String fileName = diagramPanel.getFilePath();
		if (fileName == null) {
			this.saveAs(menu, diagramPanel, fileChooserService);
			return;
		}
		try {
			Graph g = diagramPanel.getGraphPanel().getGraph();
			GraphService.write(g, new FileOutputStream(fileName));
			diagramPanel.setSaveNeeded(false);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Saves the current graph as a new file.
	 * 
	 */
	public void saveAs(FileMenu menu, DiagramPanel diagramPanel,
			FileChooserService fileChooserService) {
		if (diagramPanel == null || diagramPanel.getGraphPanel() == null
				|| diagramPanel.getGraphPanel().getGraph() == null)
			return;
		try {
			String currentFilePath = diagramPanel.getFilePath();
			if (currentFilePath != null) {
				FileService.removeOpenedFile(currentFilePath);
			}
			ExtensionFilter extensionFilter = FileService
					.getExtensionFilter(diagramPanel.getGraphPanel().getGraph());
			FileSaverHandler save = fileChooserService.save(null,
					currentFilePath, extensionFilter, null, extensionFilter
							.getExtensions()[0]);
			OutputStream out = save.getOutputStream();
			if (out != null) {
				try {
					Graph g = diagramPanel.getGraphPanel().getGraph();
					GraphService.write(g, out);
					diagramPanel.setSaveNeeded(false);
				} finally {
					out.close();
				}
				diagramPanel.setFilePath(save.getName());
				menu.addRecentFile(save.getName());
				FileService.addOpenedFile(save.getName());
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Exports the current graph to an image file.
	 * 
	 * @param diagramPanel
	 *            TODO
	 * @param fileChooserService
	 *            TODO
	 */
	public void exportImage(DiagramPanel diagramPanel,
			FileChooserService fileChooserService) {
		if (diagramPanel == null || diagramPanel.getGraphPanel() == null
				|| diagramPanel.getGraphPanel().getGraph() == null)
			return;

		try {
			String imageExtensions = FileService.getImageFileExtension();
			ExtensionFilter extensionFilter = FileService
					.getExtensionFilter(diagramPanel.getGraphPanel().getGraph());
			ExtensionFilter exportFilter = FileService
					.getImageExtensionFilter();
			FileSaverHandler save = fileChooserService.save(null, diagramPanel
					.getFilePath(), exportFilter, extensionFilter
					.getExtensions()[0], imageExtensions);
			OutputStream out = save.getOutputStream();
			if (out != null) {
				String format;
				String fileName = save.getName();
				if (fileName == null) {
					int n = imageExtensions.indexOf("|");
					if (n < 0)
						n = imageExtensions.length();
					format = imageExtensions.substring(1, n);
				} else
					format = fileName.substring(fileName.lastIndexOf(".") + 1);
				if (!ImageIO.getImageWritersByFormatName(format).hasNext()) {
					MessageFormat formatter = new MessageFormat(
							menuResourceBundle
									.getString("dialog.error.unsupported_image"));
					String message = formatter.format(new Object[] { format });
					String title = menuResourceBundle
							.getString("dialog.error.title");
					JOptionPane optionPane = new JOptionPane();
					optionPane.setMessage(message);
					DialogFactory.getInstance().showDialog(optionPane, title,
							true);
					return;
				}

				Graph graph = diagramPanel.getGraphPanel().getGraph();
				try {
					ImageIO.write(GraphService.getImage(graph), format, out);
				} finally {
					out.close();
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Exports the given diagram as an image into the clipboard and shows a
	 * dialog box on the parent frame when it is done.
	 * 
	 * @param diagramPanel
	 */
	public void exportToClipboard(DiagramPanel diagramPanel) {
		if (diagramPanel == null || diagramPanel.getGraphPanel() == null
				|| diagramPanel.getGraphPanel().getGraph() == null)
			return;
		Graph graph = diagramPanel.getGraphPanel().getGraph();
		GraphService.exportToclipBoard(graph);
		ImageIcon icon = new ImageIcon(this.getClass()
				.getResource(
						menuResourceBundle
								.getString("dialog.export_to_clipboard.icon")));
		String message = menuResourceBundle
				.getString("dialog.export_to_clipboard.ok");
		String title = menuResourceBundle
				.getString("dialog.export_to_clipboard.title");
		JLabel label = new JLabel(message);
		label.setFont(label.getFont().deriveFont(Font.PLAIN));
		JOptionPane optionPane = new JOptionPane();
		optionPane.setIcon(icon);
		optionPane.setMessage(label);
		optionPane.setName(title);
		DialogFactory.getInstance().showDialog(optionPane, title, true);
	}

	/**
	 * Exports the given diagram as an XML File and shows a dialog box on the
	 * parent frame when it is done.
	 * 
	 * @param diagramPanel
	 * @param fileChooserService
	 */
	public void exportAsXML(DiagramPanel diagramPanel,
			FileChooserService fileChooserService) {
		if (diagramPanel == null || diagramPanel.getGraphPanel() == null
				|| diagramPanel.getGraphPanel().getGraph() == null)
			return;

		try {
			String xmlExtensions = FileService.getXMLFileExtension();
			ExtensionFilter extensionFilter = FileService
					.getExtensionFilter(diagramPanel.getGraphPanel().getGraph());
			ExtensionFilter exportFilter = FileService.getXMLExtensionFilter();
			FileSaverHandler save = fileChooserService.save(null, diagramPanel
					.getFilePath(), exportFilter, extensionFilter
					.getExtensions()[0], xmlExtensions);
			OutputStream out = save.getOutputStream();
			if (out != null) {
				try {
					Element diagram = diagramPanel.getGraphPanel().getGraph()
							.getAsXMLElement();
					Document doc = new Document(diagram);
					XMLOutputter xmlOut = new XMLOutputter();
					xmlOut.output(doc, out);
				} finally {
					out.close();
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Exports given diagram to xmi format.
	 * 
	 * @param diagramPanel
	 * @param fileChooserService
	 */
	public void exportToXMI(DiagramPanel diagramPanel,
			FileChooserService fileChooserService) {
		if (diagramPanel == null || diagramPanel.getGraphPanel() == null
				|| diagramPanel.getGraphPanel().getGraph() == null)
			return;
		Graph graph = diagramPanel.getGraphPanel().getGraph();
		if (graph instanceof SoftwareModulesDiagramGraph) {
			DialogFactory.getInstance().showErrorDialog(
					this.menuResourceBundle
							.getString("dialog.export_to_xmi.error"));
			return;
		}
		try {
			String xmiExtension = FileService.getXMIFileExtension();
			ExtensionFilter extensionFilter = FileService
					.getExtensionFilter(diagramPanel.getGraphPanel().getGraph());
			ExtensionFilter exportFilter = FileService.getXMIExtensionFilter();
			FileSaverHandler save = fileChooserService.save(null, diagramPanel
					.getFilePath(), exportFilter, extensionFilter
					.getExtensions()[0], xmiExtension);
			OutputStream out = save.getOutputStream();
			if (out != null) {
				GraphService.exportToXMI(graph, out);
				out.close();
			}
		} catch (Exception e) {
			// Well, we tried...
			e.printStackTrace();
		}

	}

	/**
	 * Prints the given diagram
	 * 
	 * @param diagramPanel
	 */
	public void print(DiagramPanel diagramPanel) {
		if (diagramPanel == null || diagramPanel.getGraphPanel() == null
				|| diagramPanel.getGraphPanel().getGraph() == null)
			return;
		PrintPanel printPanel = new PrintPanel(diagramPanel.getGraphPanel()
				.getGraph());
		JOptionPane optionPane = new JOptionPane();
		ResourceBundle printBundle = ResourceBundle.getBundle(
				ResourceBundleConstant.PRINT_STRINGS, Locale.getDefault());
		String printCloseText = printBundle
				.getString("dialog.print.cancel.text");
		optionPane.setOptions(new String[] { printCloseText });
		optionPane.setMessage(printPanel);
		optionPane.setBorder(new EmptyBorder(0, 0, 10, 0));
		DialogFactory.getInstance().showDialog(optionPane, "Print", true);
	}

	/**
	 * Exits the program if no graphs have been modified or if the user agrees
	 * to abandon modified graphs or save its.
	 */
	public void exit(FileMenu menu, EditorFrame editorFrame,
			FileChooserService fileChooserService) {
		boolean ok = isItReadyToExit(menu, editorFrame, fileChooserService);
		if (ok) {
			UMLEditor.getInstance().exit();
		}
	}

	/**
	 * Restarts the editor if no graphs have been modified or if the user agrees
	 * to abandon modified graphs or save its.
	 */
	public void restart(FileMenu menu, EditorFrame editorFrame,
			FileChooserService fileChooserService) {
		boolean ok = isItReadyToExit(menu, editorFrame, fileChooserService);
		if (ok) {
			UMLEditor.getInstance().restart();
		}
	}

	/**
	 * Asks user to save changes before exit.
	 * 
	 * @return true is all is saved either false
	 */
	private boolean isItReadyToExit(FileMenu menu, EditorFrame editorFrame,
			FileChooserService fileChooserService) {
		List<DiagramPanel> unsavedDiagram = new ArrayList<DiagramPanel>();
		Component[] diagramPanels = editorFrame.getTabbedPane().getComponents();
		for (int i = 0; i < diagramPanels.length; i++) {
			if (diagramPanels[i] instanceof DiagramPanel) {
				DiagramPanel aDiagramPanel = (DiagramPanel) diagramPanels[i];
				if (aDiagramPanel.isSaveNeeded()) {
					unsavedDiagram.add(aDiagramPanel);
				}
			}
		}
		int unsavedCount = unsavedDiagram.size();
		IDiagramPanel activeDiagramPanel = editorFrame.getActiveDiagramPanel();
		if (unsavedCount > 0) {
			// ask user if it is ok to close
			ImageIcon icon = new ImageIcon(this.getClass().getResource(
					menuResourceBundle.getString("dialog.exit.icon")));
			String message = MessageFormat.format(menuResourceBundle
					.getString("dialog.exit.ok"), new Object[] { new Integer(
					unsavedCount) });
			String title = menuResourceBundle.getString("dialog.exit.title");
			JOptionPane optionPane = new JOptionPane(message,
					JOptionPane.CLOSED_OPTION,
					JOptionPane.YES_NO_CANCEL_OPTION, icon);
			DialogFactory.getInstance().showDialog(optionPane, title, true);

			int result = JOptionPane.YES_OPTION;
			if (!JOptionPane.UNINITIALIZED_VALUE.equals(optionPane.getValue())) {
				result = ((Integer) optionPane.getValue()).intValue();
			}

			if (result == JOptionPane.CANCEL_OPTION) {
				return false;
			}
			if (result == JOptionPane.YES_OPTION) {
				for (Iterator<DiagramPanel> iter = unsavedDiagram.iterator(); iter
						.hasNext();) {
					DiagramPanel anUnsavedDiagram = iter.next();
					save(menu, anUnsavedDiagram, fileChooserService);
				}
				FileService.updateRecentFiles(menu.getRecentFiles());
				FileService.setActiveDiagramFile(activeDiagramPanel
						.getFilePath());
				return true;
			}
			if (result == JOptionPane.NO_OPTION) {
				FileService.updateRecentFiles(menu.getRecentFiles());
				FileService.setActiveDiagramFile(activeDiagramPanel
						.getFilePath());
				return true;
			}
		}
		if (unsavedCount == 0) {
			FileService.updateRecentFiles(menu.getRecentFiles());
			if (activeDiagramPanel != null) {
				FileService.setActiveDiagramFile(activeDiagramPanel
						.getFilePath());
			}
			return true;
		}
		return false;
	}

	/**
	 * @param graphClass
	 * @param editorFrame
	 */
	public void createDiagram(Class<? extends Graph> graphClass,
			EditorFrame editorFrame) {
		try {
			DiagramPanel frame = new DiagramPanel(graphClass.newInstance());
			editorFrame.addTabbedPane(frame);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * Menu external resources.
	 */
	private ResourceBundle menuResourceBundle;

}
