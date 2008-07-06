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

package uk.ac.sheffield.dcs.smdStudio.framework.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import uk.ac.sheffield.dcs.smdStudio.framework.diagram.Graph;
import uk.ac.sheffield.dcs.smdStudio.framework.preference.PreferencesConstant;
import uk.ac.sheffield.dcs.smdStudio.framework.preference.PreferencesService;
import uk.ac.sheffield.dcs.smdStudio.framework.preference.PreferencesServiceFactory;
import uk.ac.sheffield.dcs.smdStudio.framework.resources.ResourceBundleConstant;
import uk.ac.sheffield.dcs.smdStudio.product.diagram.smd.SoftwareModulesDiagramGraph;
import uk.ac.sheffield.dcs.smdStudio.product.diagram.state.StateDiagramGraph;


/**
 * This file provides common file services
 * 
 * @author Alexandre de Pellegrin
 * 
 */
public abstract class FileService {

	/**
	 * Edits the file path so that it ends in the desired extension.
	 * 
	 * @param original
	 *            the file to use as a starting point
	 * @param toBeRemoved
	 *            the extension that is to be removed before adding the desired
	 *            extension. Use null if nothing needs to be removed.
	 * @param desired
	 *            the desired extension (e.g. ".png"), or a | separated list of
	 *            extensions
	 * @return original if it already has the desired extension, or a new file
	 *         with the edited file path
	 */
	public static String editExtension(String original, String toBeRemoved,
			String desired) {
		if (original == null)
			return null;
		int n = desired.indexOf('|');
		if (n >= 0)
			desired = desired.substring(0, n);
		String path = original;
		if (!path.toLowerCase().endsWith(desired.toLowerCase())) {
			if (toBeRemoved != null
					&& path.toLowerCase().endsWith(toBeRemoved.toLowerCase()))
				path = path.substring(0, path.length() - toBeRemoved.length());
			path = path + desired;
		}
		return path;
	}

	/**
	 * @return a map of extension filter per diagram type
	 */
	private static Map<Class<? extends Graph>, ExtensionFilter> getExtensionFilters() {
		ResourceBundle appResources = ResourceBundle.getBundle(
				ResourceBundleConstant.FILE_STRINGS, Locale.getDefault());
		Map<Class<? extends Graph>, ExtensionFilter> result = new LinkedHashMap<Class<? extends Graph>, ExtensionFilter>();
		// Set files extensions
		String defaultFilesExt = appResources
				.getString("files.global.extension");
		String defaultFilesExtName = appResources
				.getString("files.global.name");
		String classFilesExt = appResources.getString("files.smd.extension");
		String classFilesExtName = appResources.getString("files.smd.name");
		String stateFilesExt = appResources.getString("files.state.extension");
		String stateFilesExtName = appResources.getString("files.state.name");

		ExtensionFilter fileFilter0 = new ExtensionFilter(defaultFilesExtName,
				new String[] { defaultFilesExt });
		ExtensionFilter fileFilter1 = new ExtensionFilter(classFilesExtName,
				new String[] { classFilesExt });
		ExtensionFilter fileFilter2 = new ExtensionFilter(stateFilesExtName,
				new String[] { stateFilesExt });

		result.put(SoftwareModulesDiagramGraph.class, fileFilter1);
		result.put(StateDiagramGraph.class, fileFilter2);
		result.put(Graph.class, fileFilter0);

		return result;
	}

	/**
	 * @return all kind of extension file filters
	 */
	public static ExtensionFilter[] getFileFilters() {
		Map<Class<? extends Graph>, ExtensionFilter> filters = FileService
				.getExtensionFilters();
		Collection<ExtensionFilter> values = filters.values();
		return (ExtensionFilter[]) values.toArray(new ExtensionFilter[values
				.size()]);
	}

	/**
	 * @param graph
	 * @return the file filter specific to a graph type
	 */
	public static ExtensionFilter getExtensionFilter(Graph graph) {
		Map<Class<? extends Graph>, ExtensionFilter> filters = FileService
				.getExtensionFilters();
		ExtensionFilter filter = filters.get(graph.getClass());
		if (filter != null) {
			return filter;
		}
		return filters.get(Graph.class);
	}

	/**
	 * @return the extension filter for image export
	 */
	public static ExtensionFilter getImageExtensionFilter() {
		return FileService.getExtensionFilter("image");
	}

	/**
	 * @return the string reprensentation of file extension for image export
	 */
	public static String getImageFileExtension() {
		return FileService.getFileExtension("image");
	}

	/**
	 * @return the extension filter for image export
	 */
	public static ExtensionFilter getXMIExtensionFilter() {
		return FileService.getExtensionFilter("xmi");
	}

	/**
	 * @return the string representation of file extension for xmi export
	 */
	public static String getXMIFileExtension() {
		return FileService.getFileExtension("xmi");
	}

	/**
	 * @return the extension filter for type
	 * @param type
	 *            to search in properties file. For example, for
	 *            "files.image.extension", key should be "image".
	 */
	private static ExtensionFilter getExtensionFilter(String type) {
		ResourceBundle appResources = ResourceBundle.getBundle(
				ResourceBundleConstant.FILE_STRINGS, Locale.getDefault());
		String filterName = appResources.getString("files." + type + ".name");
		String fileExtension = appResources.getString("files." + type
				+ ".extension");
		return new ExtensionFilter(filterName, fileExtension);
	}

	/**
	 * @return the string representation of file extension for a type
	 * @param type
	 *            to search in properties file. For example, for
	 *            "files.image.extension", key should be "image".
	 */
	private static String getFileExtension(String type) {
		ResourceBundle appResources = ResourceBundle.getBundle(
				ResourceBundleConstant.FILE_STRINGS, Locale.getDefault());
		return appResources.getString("files." + type + ".extension");
	}

	/**
	 * @return the list of lastest opened files (as path strings)
	 */
	public static List<String> getRecentFiles() {
		List<String> recentFiles = new ArrayList<String>();
		PreferencesService preferences = PreferencesServiceFactory
				.getInstance();
		String recent = preferences.get(PreferencesConstant.RECENT_FILES, "")
				.trim();
		if (recent.length() > 0) {
			recentFiles.addAll(Arrays.asList(recent.split("[|]")));
		}
		return recentFiles;
	}

	/**
	 * Saves recently opened files into user preferences
	 * 
	 * @param recentFiles
	 */
	public static void updateRecentFiles(List<String> recentFiles) {
		PreferencesService preferences = PreferencesServiceFactory
				.getInstance();
		String recent = "";
		for (int i = 0; i < Math.min(recentFiles.size(),
				FileService.DEFAULT_MAX_RECENT_FILES); i++) {
			if (recent.length() > 0)
				recent += "|";
			recent += recentFiles.get(i);
		}
		preferences.put(PreferencesConstant.RECENT_FILES, recent);
	}

	/**
	 * Saves newly opened file path into user preferences
	 * 
	 * @param path
	 *            file path (should be relative or absolute)
	 */
	public static void addOpenedFile(String path) {
		boolean isPathFound = false;
		PreferencesService preferences = PreferencesServiceFactory
				.getInstance();
		String list = preferences.get(
				PreferencesConstant.OPENED_FILES_ON_WORKSPACE, "");
		StringTokenizer tokenizer = new StringTokenizer(list,
				PreferencesConstant.OPENED_FILES_SEPARATOR.toString());
		while (tokenizer.hasMoreTokens()) {
			String aPath = tokenizer.nextToken();
			if (aPath.equals(path)) {
				isPathFound = true;
				break;
			}
		}
		if (!isPathFound) {
			list = list + PreferencesConstant.OPENED_FILES_SEPARATOR + path;
			preferences
					.put(PreferencesConstant.OPENED_FILES_ON_WORKSPACE, list);
		}
	}

	/**
	 * Removes newly closed file from user preferences
	 * 
	 * @param path
	 *            file path (could be relative or absolute)
	 */
	public static void removeOpenedFile(String path) {
		boolean isPathFound = false;
		PreferencesService preferences = PreferencesServiceFactory
				.getInstance();
		String list = preferences.get(
				PreferencesConstant.OPENED_FILES_ON_WORKSPACE, "");
		StringTokenizer tokenizer = new StringTokenizer(list,
				PreferencesConstant.OPENED_FILES_SEPARATOR.toString());
		List<String> pathList = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()) {
			String aPath = tokenizer.nextToken();
			if (aPath.equals(path)) {
				isPathFound = true;
			}
			pathList.add(aPath);
		}
		if (isPathFound) {
			pathList.remove(path);
			Iterator<String> iter = pathList.iterator();
			list = "";
			while (iter.hasNext()) {
				String aPath = (String) iter.next();
				list = list + PreferencesConstant.OPENED_FILES_SEPARATOR
						+ aPath;
			}
			preferences
					.put(PreferencesConstant.OPENED_FILES_ON_WORKSPACE, list);
		}
	}

	/**
	 * Gets opened files on last session. Used to restore workspace after
	 * restart
	 * 
	 * @return file path list (relative or absolute, as saved...)
	 */
	public static String[] getOpenedFilesDuringLastSession() {
		PreferencesService preferences = PreferencesServiceFactory
				.getInstance();
		String list = preferences.get(
				PreferencesConstant.OPENED_FILES_ON_WORKSPACE, "");
		StringTokenizer tokenizer = new StringTokenizer(list,
				PreferencesConstant.OPENED_FILES_SEPARATOR.toString());
		List<String> realPathList = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()) {
			String aPath = tokenizer.nextToken();
			File aFile = new File(aPath);
			if (aFile.exists()) {
				realPathList.add(aPath);
			}
		}
		// Re-generate file list to evict deleted files
		Iterator<String> iter = realPathList.iterator();
		list = "";
		while (iter.hasNext()) {
			String aPath = (String) iter.next();
			list = list + PreferencesConstant.OPENED_FILES_SEPARATOR + aPath;
		}
		preferences.put(PreferencesConstant.OPENED_FILES_ON_WORKSPACE, list);
		return (String[]) realPathList.toArray(new String[realPathList.size()]);
	}

	/**
	 * Indicates which diagram is currently focused on workspace and saves it
	 * into user preferences
	 * 
	 * @param path
	 *            file path (could be relative or absolute)
	 */
	public static void setActiveDiagramFile(String path) {
		if (path != null) {
			PreferencesService pService = PreferencesServiceFactory
					.getInstance();
			pService.put(PreferencesConstant.ACTIVE_FILE, path);
		}
	}

	/**
	 * Gets from user preferences which diagram was setted as focused
	 * 
	 * @return file path (could be relative or absolute). Returns null by
	 *         default.
	 */
	public static String getActiveDiagramFile() {
		PreferencesService pService = PreferencesServiceFactory.getInstance();
		String path = pService.get(PreferencesConstant.ACTIVE_FILE, null);
		return path;
	}

	/**
	 * @return the last opened file directory or the current directory if no one
	 *         is found
	 */
	public static File getLastDir() {
		List<String> recentFiles = FileService.getRecentFiles();
		File lastDir = new File(".");
		if (recentFiles.size() > 0) {
			lastDir = new File(recentFiles.get(0)).getParentFile();
		}
		return lastDir;
	}

	/**
	 * Recent opened files list capacity
	 */
	private static final int DEFAULT_MAX_RECENT_FILES = 5;
}
