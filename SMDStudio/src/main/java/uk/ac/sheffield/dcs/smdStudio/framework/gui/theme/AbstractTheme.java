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

import java.awt.Frame;
import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import uk.ac.sheffield.dcs.smdStudio.UMLEditor;

import com.l2fprod.common.swing.plaf.LookAndFeelAddons;
import com.l2fprod.common.swing.plaf.windows.WindowsLookAndFeelAddons;

/**
 * Abstract GUI theme with commons operations
 * 
 * @author Alexandre de Pellegrin
 * 
 */
public abstract class AbstractTheme implements Theme
{

    /**
     * @return look and feel
     */
    protected abstract String getLookAndFeelClassName();

    /*
     * (non-Javadoc)
     * 
     * @see uk.ac.sheffield.dcs.smdStudio.framework.gui.laf.CustomLookAndFeel#activate()
     */
    public void activate()
    {
        try
        {
            this.lafClassName = getLookAndFeelClassName();
            UIManager.setLookAndFeel(this.lafClassName);

            Frame[] frames = Frame.getFrames();
            for (int i = 0; i < frames.length; i++)
            {
                SwingUtilities.updateComponentTreeUI(frames[i]);
            }

        }
        catch (UnsupportedLookAndFeelException e)
        {
            abortWithError(e);
        }
        catch (ClassNotFoundException e)
        {
            abortWithError(e);
        }
        catch (InstantiationException e)
        {
            abortWithError(e);
        }
        catch (IllegalAccessException e)
        {
            abortWithError(e);
        }
        setup();
        updateTaskPaneUI();
    }

    /**
     * Inits collapsible side panel look and feel
     */
    private void updateTaskPaneUI()
    {
        LookAndFeelAddons addons = new WindowsLookAndFeelAddons();
        LookAndFeelAddons.setAddon(addons);

        UIDefaults defaults = UIManager.getDefaults();
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("TaskPaneGroup.background", getSIDEBAR_ELEMENT_BACKGROUND_COLOR());
        m.put("TaskPaneGroup.titleBackgroundGradientStart", getSIDEBAR_ELEMENT_TITLE_BG_START_COLOR());
        m.put("TaskPaneGroup.titleBackgroundGradientEnd", getSIDEBAR_ELEMENT_TITLE_BG_END_COLOR());
        m.put("TaskPaneGroup.titleForeground", getSIDEBAR_ELEMENT_TITLE_FOREGROUND_COLOR());
        m.put("TaskPaneGroup.titleOver", getSIDEBAR_ELEMENT_TITLE_OVER_COLOR());
        m.put("TaskPaneGroup.borderColor", getSIDEBAR_ELEMENT_BACKGROUND_COLOR());
        m.put("TaskPane.useGradient", Boolean.TRUE);
        m.put("TaskPane.backgroundGradientStart", getSIDEBAR_BACKGROUND_START_COLOR());
        m.put("TaskPane.backgroundGradientEnd", getSIDEBAR_BACKGROUND_END_COLOR());

        defaults.putAll(m);
    }

    /**
     * Setup entry point after look and feel change
     */
    protected abstract void setup();

    /**
     * Informs on error and exits
     * 
     * @param e exception raised
     */
    private void abortWithError(Exception e)
    {
        System.err.println("Fatal error when loading look and feel! -> " + this.lafClassName);
        e.printStackTrace();
        System.err.println("Stopping...");
        UMLEditor.getInstance().exitWithErrors();
    }

    private String lafClassName;

}
