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

import java.awt.Color;
import java.awt.Font;

import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;

/**
 * Java Metal based theme
 * 
 * @author ALexandre de Pellegrin
 *
 */
public class ClassicMetalTheme extends AbstractTheme
{



    /* (non-Javadoc)
     * @see uk.ac.sheffield.dcs.smdStudio.framework.gui.theme.AbstractTheme#getLookAndFeel()
     */
    protected String getLookAndFeelClassName()
    {
        MetalTheme defaultMetalTheme = new DefaultMetalTheme(); 
        MetalLookAndFeel.setCurrentTheme(defaultMetalTheme);
        return MetalLookAndFeel.class.getName();
    }
    
    /* (non-Javadoc)
     * @see uk.ac.sheffield.dcs.smdStudio.framework.gui.theme.AbstractTheme#setup()
     */
    protected void setup()
    {
    }

    public Color getBLACK_COLOR() {return MetalLookAndFeel.getBlack();}
    public Color getWHITE_COLOR() {return MetalLookAndFeel.getWhite();}

    public Color getGRID_COLOR() {return new Color(220, 220, 220);}

    public Color getBACKGROUND_COLOR() {return MetalLookAndFeel.getMenuBackground();}
    
    public Font getMENUBAR_FONT() {return MetalLookAndFeel.getMenuTextFont();}
    public Color getMENUBAR_BACKGROUND_COLOR() {return MetalLookAndFeel.getMenuBackground();}
    public Color getMENUBAR_FOREGROUND_COLOR() {return MetalLookAndFeel.getMenuForeground();}
    
    public Color getROLLOVERBUTTON_DEFAULT_COLOR() {return MetalLookAndFeel.getMenuBackground();}
    public Color getROLLOVERBUTTON_ROLLOVER_BORDER_COLOR() {return MetalLookAndFeel.getMenuForeground();}
    public Color getROLLOVERBUTTON_ROLLOVER_COLOR() {return MetalLookAndFeel.getMenuBackground();}

    public Color getSIDEBAR_BACKGROUND_END_COLOR() {return MetalLookAndFeel.getMenuSelectedBackground();}
    public Color getSIDEBAR_BACKGROUND_START_COLOR() {return MetalLookAndFeel.getMenuSelectedBackground();}
    public Color getSIDEBAR_BORDER_COLOR() {return MetalLookAndFeel.getMenuBackground();}
    public Color getSIDEBAR_ELEMENT_BACKGROUND_COLOR() {return MetalLookAndFeel.getMenuBackground();}
    public Color getSIDEBAR_ELEMENT_TITLE_BG_END_COLOR() {return MetalLookAndFeel.getMenuBackground();}
    public Color getSIDEBAR_ELEMENT_TITLE_BG_START_COLOR() {return MetalLookAndFeel.getMenuBackground().darker();}
    public Color getSIDEBAR_ELEMENT_TITLE_FOREGROUND_COLOR() {return MetalLookAndFeel.getWindowBackground();}
    public Color getSIDEBAR_ELEMENT_TITLE_OVER_COLOR() {return MetalLookAndFeel.getWindowBackground().brighter();}
    
    public Color getSTATUSBAR_BACKGROUND_COLOR() {return MetalLookAndFeel.getMenuBackground();}
    public Color getSTATUSBAR_BORDER_COLOR() {return MetalLookAndFeel.getMenuBackground();}
    
    public Font getTOGGLEBUTTON_FONT() {return MetalLookAndFeel.getMenuTextFont().deriveFont(Font.PLAIN);}
    public Color getTOGGLEBUTTON_SELECTED_BORDER_COLOR() {return MetalLookAndFeel.getMenuSelectedBackground();}
    public Color getTOGGLEBUTTON_SELECTED_COLOR() {return MetalLookAndFeel.getMenuSelectedBackground();}
    public Color getTOGGLEBUTTON_UNSELECTED_COLOR() {return MetalLookAndFeel.getMenuBackground();}
    
    public Font getWELCOME_SMALL_FONT() {return MetalLookAndFeel.getWindowTitleFont().deriveFont((float) 12.0).deriveFont(Font.PLAIN);}
    public Font getWELCOME_BIG_FONT() {return MetalLookAndFeel.getWindowTitleFont().deriveFont((float) 28.0);}
    public Color getWELCOME_BACKGROUND_END_COLOR() {return MetalLookAndFeel.getMenuBackground();}
    public Color getWELCOME_BACKGROUND_START_COLOR() {return MetalLookAndFeel.getMenuBackground().brighter();}
    public Color getWELCOME_BIG_FOREGROUND_COLOR() {return MetalLookAndFeel.getMenuSelectedBackground();}
    public Color getWELCOME_BIG_ROLLOVER_FOREGROUND_COLOR() {return getWELCOME_BIG_FOREGROUND_COLOR().darker();}



}
