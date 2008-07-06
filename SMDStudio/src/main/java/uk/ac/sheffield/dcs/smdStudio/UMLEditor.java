/*
 Violet - A program for editing UML diagrams.

 Copyright (C) 2002 Cay S. Horstmann (http://horstmann.com)

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

package uk.ac.sheffield.dcs.smdStudio;

import java.awt.HeadlessException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.ServiceLoader;

import javax.swing.JApplet;
import javax.swing.JFrame;

import uk.ac.sheffield.dcs.smdStudio.framework.action.FileAction;
import uk.ac.sheffield.dcs.smdStudio.framework.diagram.Graph;
import uk.ac.sheffield.dcs.smdStudio.framework.file.FileService;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.Clipboard;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.EditorFrame;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.SplashScreen;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.theme.ThemeManager;
import uk.ac.sheffield.dcs.smdStudio.framework.preference.PreferencesServiceFactory;
import uk.ac.sheffield.dcs.smdStudio.framework.resources.ResourceFactory;
import uk.ac.sheffield.dcs.smdStudio.framework.util.VersionChecker;


/**
 * A program for editing UML diagrams.
 */
@SuppressWarnings("serial")
public class UMLEditor extends JApplet {

	/**
	 * Default constructor (warn : extends default applet construtor)
	 * 
	 * @throws HeadlessException
	 *             (from applet constructor inheritance)
	 */
	public UMLEditor() throws HeadlessException {
		super();
		UMLEditor.setInstance(this);
	}

	/**
	 * Standalone application entry point
	 * 
	 * @param args
	 *            (could contains file to open)
	 */
	public static void main(String[] args) {
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if ("-reset".equals(arg)) {
				PreferencesServiceFactory.getInstance().reset();
				System.out.println("User preferences reset done.");
			}
			if ("-english".equals(arg)) {
				Locale.setDefault(Locale.ENGLISH);
				System.out.println("Language forced to english.");
			}
			if ("-help".equals(arg) || "-?".equals(arg)) {
				System.out
						.println("Violet UML Editor command line help. Options are :");
				System.out.println("-reset to reset user preferences,");
				System.out.println("-english to force language to english.");
				return;
			}
		}
		UMLEditor editor = new UMLEditor();
		editor.startingMode = CMDLINE_STARTING_MODE;
		String startedWithWebStart = System.getProperty("startedWithWebStart",
				"false");
		if ("true".equals(startedWithWebStart)) {
			editor.startingMode = WEBSTART_STARTING_MODE;
		}
		editor.createWorkspace();
	}

	/*
	 * Applet entry point (non-Javadoc)
	 * 
	 * @see java.applet.Applet#init()
	 */
	public void init() {
		this.startingMode = APPLET_STARTING_MODE;
		this.createWorkspace();
	}

	private void createWorkspace() {
		try {
			switch (this.startingMode) {
			case APPLET_STARTING_MODE: {
				// No splash screen
				// No JVM checking
				// With launging argments to open diagram
				ThemeManager.getInstance().applyPreferedTheme();
				this.editorFrame = new EditorFrame();
				this.editorFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
				setContentPane(editorFrame.getContentPane());
				setJMenuBar(editorFrame.getJMenuBar());
				String url = getParameter("diagram");
				if (url != null)
					try {
						FileAction fileAction = new FileAction();
						fileAction.openURL(new URL(getDocumentBase(), url),
								editorFrame);
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				editorFrame.addWindowListener(new WindowAdapter() {
					public void windowClosed(WindowEvent e) {
						System.out.println("editor closed");
					}
				});
			}
				break;

			case WEBSTART_STARTING_MODE: {
				// No splash screen
				// No JVM checking
				// No diagram to open in argument
				ThemeManager.getInstance().applyPreferedTheme();
				this.editorFrame = new EditorFrame();
				this.editorFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
				this.editorFrame.setVisible(true);
			}
				break;

			case CMDLINE_STARTING_MODE: {
				// With splash screen
				// Check JVM
				// With cmd line arguments
				// Restore last opened workspace
				ThemeManager.getInstance().applyPreferedTheme();
				SplashScreen splashScreen = new SplashScreen();
				splashScreen.setVisible(true);
				VersionChecker checker = new VersionChecker();
				checker.check(JAVA_VERSION);
				// try
				// {
				// System.setProperty("apple.laf.useScreenMenuBar", "true");
				// }
				// catch (SecurityException ex)
				// {
				// // well, we tried...
				// }
				this.editorFrame = new EditorFrame();
				this.editorFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
				FileAction fileAction = new FileAction();
				SplashScreen.displayOverEditor(editorFrame, 1000);
				if (this.args != null && this.args.length != 0) {
					for (int i = 0; i < this.args.length; i++) {
						fileAction.open(this.args[i], editorFrame);
					}
				}
				String[] filePaths = FileService
						.getOpenedFilesDuringLastSession();
				fileAction.open(filePaths, editorFrame);
				String activeDiagramFile = FileService.getActiveDiagramFile();
				editorFrame.setActiveDiagramPanel(activeDiagramFile);
				editorFrame.setVisible(true);
				splashScreen.setVisible(false);
				splashScreen.dispose();

				installGraphPlugins();
			}
				break;

			default:
				System.err
						.println("Could not establish if software has been launched as an applet, via webstart or with command line. Stopping...");
				System.exit(-1);
				break;
			}
		} catch (ExceptionInInitializerError e) {
			System.out
					.println("Error while initializing workspace. User preferences has been reset for safety reasons.");
			PreferencesServiceFactory.getInstance().reset();
			throw e;
		}

	}

	public void installGraphPlugins() {
		ClassLoader pcl = getPluginClassLoader();
		if (pcl == null)
			return;
		ServiceLoader<Graph> graphServices = ServiceLoader.load(Graph.class,
				pcl);
		for (Graph graph : graphServices) {
			ResourceBundle bundle = ResourceBundle.getBundle(graph.getClass()
					.getName()
					+ "Strings", Locale.getDefault(), pluginClassLoader);
			ResourceFactory factory = new ResourceFactory(bundle);
			editorFrame.getMenuFactory().getFileMenu(editorFrame)
					.createGraphTypeMenuItem("file.new", graph.getClass(),
							factory, editorFrame);
		}
	}

	/**
	 * Exit
	 */
	public void exit() {
		System.exit(0);
	}

	/**
	 * Exit with error status ad, before, reset preferences
	 */
	public void exitWithErrors() {
		PreferencesServiceFactory.getInstance().reset();
		System.exit(-1);
	}

	/**
	 * Restarts editor by replacing current by a new one
	 */
	public void restart() {
		// Disposes frame
		this.editorFrame.dispose();
		// Changes LAF
		ThemeManager.getInstance().applyPreferedTheme();
		// Creates a new one
		createWorkspace();
	}

	public ClassLoader getPluginClassLoader() {
		if (pluginClassLoader == null) {
			String pluginDirName = System.getProperty("violet.plugin.dir");
			if (pluginDirName == null)
				return null;
			File pluginDir = new File(pluginDirName);
			File[] pluginJars = pluginDir.listFiles(new FileFilter() {
				public boolean accept(File pathname) {
					return pathname.toString().endsWith(".jar");
				}
			});

			URL[] pluginJarUrls = new URL[pluginJars.length];
			for (int i = 0; i < pluginJars.length; i++)
				try {
					pluginJarUrls[i] = pluginJars[i].toURI().toURL();
				} catch (MalformedURLException ex) {
					ex.printStackTrace();
				}

			pluginClassLoader = new URLClassLoader(pluginJarUrls);
		}
		return pluginClassLoader;
	}

	public Clipboard getClipboard() {
		return clipboard;
	}

	/**
	 * Sets unique editor instance
	 * 
	 * @param instance
	 */
	private static void setInstance(UMLEditor instance) {
		UMLEditor.instance = instance;
	}

	/**
	 * @return singleton
	 */
	public static UMLEditor getInstance() {
		if (UMLEditor.instance == null) {
			setInstance(new UMLEditor());
			/*
			 * throw new RuntimeException(
			 * "Error while accessing to UMLEditor unique instance which should have been created on editor startup."
			 * );
			 */
		}
		return UMLEditor.instance;
	}

	/** Minimum compliant Java version */
	private static final String JAVA_VERSION = "1.6";

	/** Application started as an applet */
	private static final int APPLET_STARTING_MODE = 1;

	/** Application started via web start */
	private static final int WEBSTART_STARTING_MODE = 2;

	/** Application started using command line */
	private static final int CMDLINE_STARTING_MODE = 3;

	/** Constant to use with System.exit. Indicates to finalize application */
	public static final int EXIT_SOFTWARE = 0;

	/**
	 * Constant to use with System.exit. Indicates to restart workspace (new
	 * EditorFrame instance)
	 */
	public static final int RESTART_WORKSPACE = 222;

	/** Starting mode */
	private int startingMode = 0;

	/** Command line arguments */
	private String[] args;

	/** Main frame */
	private EditorFrame editorFrame;

	/** UML Editor instance */
	private static UMLEditor instance;

	private ClassLoader pluginClassLoader;

	/** The clipboard that is shared among all diagrams */
	private Clipboard clipboard = new Clipboard();
}