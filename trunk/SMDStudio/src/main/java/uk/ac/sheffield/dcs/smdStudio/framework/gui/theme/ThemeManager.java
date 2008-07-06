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

package uk.ac.sheffield.dcs.smdStudio.framework.gui.theme;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.plaf.metal.MetalLookAndFeel;

import uk.ac.sheffield.dcs.smdStudio.framework.preference.PreferencesConstant;
import uk.ac.sheffield.dcs.smdStudio.framework.preference.PreferencesService;
import uk.ac.sheffield.dcs.smdStudio.framework.preference.PreferencesServiceFactory;

import com.pagosoft.plaf.PgsLookAndFeel;

/**
 * Manages GUI themes
 * 
 * @author Alexandre de Pellegrin
 * 
 */
public class ThemeManager {

	/**
	 * Private constructor for singleton pattern
	 */
	private ThemeManager() {
	}

	/**
	 * @return a single private instance
	 */
	public static ThemeManager getInstance() {
		if (ThemeManager.instance == null) {
			ThemeManager.instance = new ThemeManager();
		}
		return ThemeManager.instance;
	}

	public LookAndFeelInfo[] getInstalledLookAndFeels() {
		UIManager.LookAndFeelInfo[] infos = UIManager
				.getInstalledLookAndFeels();
		PgsLookAndFeel pgsLaf = new PgsLookAndFeel();
		boolean isPgsSupported = pgsLaf.isSupportedLookAndFeel();
		if (isPgsSupported) {
			// Hacks look and feel infos by adding Pgs themes
			LookAndFeelInfo vistaBlueInfo = new UIManager.LookAndFeelInfo(
					ThemeConstant.VISTA_BLUE_NAME, VistaBlueTheme.class
							.getName());
			LookAndFeelInfo[] newInfos = new LookAndFeelInfo[infos.length + 1];
			System.arraycopy(infos, 0, newInfos, 1, infos.length);
			newInfos[0] = vistaBlueInfo;
			infos = newInfos;
		}
		return infos;
	}

	/**
	 * Applies prefered theme. Only for standalone mode (not Eclipse plugin mode
	 * for example)
	 * 
	 */
	public void applyPreferedTheme() {
		String className = getPreferedLookAndFeel();
		if (VistaBlueTheme.class.getName().equals(className)) {
			switchToVistaBlueTheme();
			return;
		}

		if (MetalLookAndFeel.class.getName().equals(className)) {
			switchToClassicMetalTheme();
			return;
		}

		// Then, uses basic theme
		switchToBasicTheme(className);
	}

	/**
	 * @return prefered theme
	 */
	public int getPreferedTheme() {
		PreferencesService pService = PreferencesServiceFactory.getInstance();
		String preferedLAF = pService.get(PreferencesConstant.LOOK_AND_FEEL, ""
				+ ThemeConstant.VISTA_BLUE);
		return Integer.parseInt(preferedLAF);
	}

	public void setPreferedLookAndFeel(String className) {
		PreferencesService pService = PreferencesServiceFactory.getInstance();
		pService.put(PreferencesConstant.LOOK_AND_FEEL, className);
	}

	public String getPreferedLookAndFeel() {
		PreferencesService pService = PreferencesServiceFactory.getInstance();
		String preferedLAF = pService.get(PreferencesConstant.LOOK_AND_FEEL,
				VistaBlueTheme.class.getName());
		return preferedLAF;
	}

	/**
	 * Switch to Vista Blue graphical theme
	 */
	public void switchToVistaBlueTheme() {

		currentTheme = new VistaBlueTheme();
		currentTheme.activate();
	}

	/**
	 * Switch to Java classic metal theme
	 */
	public void switchToClassicMetalTheme() {
		currentTheme = new ClassicMetalTheme();
		currentTheme.activate();
	}

	/**
	 * Switch to Java currently installed basic theme
	 */
	public void switchToBasicTheme(String className) {
		currentTheme = new BasicTheme(className);
		currentTheme.activate();
	}

	/**
	 * @return current theme
	 */
	public Theme getTheme() {
		return currentTheme;
	}

	/**
	 * Single instance
	 */
	private static ThemeManager instance;

	/**
	 * Current graphical theme
	 */
	private Theme currentTheme;

}