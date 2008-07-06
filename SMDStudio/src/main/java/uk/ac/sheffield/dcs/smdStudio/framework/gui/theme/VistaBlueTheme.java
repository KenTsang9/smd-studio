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

import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;

import com.pagosoft.plaf.PgsLookAndFeel;
import com.pagosoft.plaf.themes.VistaTheme;

/**
 * Implements Vista Blue theme
 * 
 * @author Alexandre de Pellegrin
 *
 */
public class VistaBlueTheme extends AbstractTheme
{


    /* (non-Javadoc)
     * @see uk.ac.sheffield.dcs.smdStudio.framework.gui.laf.AbstractLookAndFeel#getLookAndFeel()
     */
    protected String getLookAndFeelClassName()
    {
        VistaTheme vistaTheme = new VistaTheme()
        {
            public ColorUIResource getMenuBackground()
            {
                return new ColorUIResource(new Color(255, 255, 255));
            }

            public ColorUIResource getSecondary3()
            {
                return new ColorUIResource(new Color(224, 231, 242));
            }
        };
        PgsLookAndFeel.setCurrentTheme(vistaTheme);
        return PgsLookAndFeel.class.getName();
    }
    
    /* (non-Javadoc)
     * @see uk.ac.sheffield.dcs.smdStudio.framework.gui.theme.AbstractTheme#setup()
     */
    protected void setup()
    {
    }    

    public Color getWHITE_COLOR() {return Color.WHITE;}
    public Color getBLACK_COLOR() {return Color.BLACK;}

    public Color getGRID_COLOR() {return new Color(220, 220, 220);}
    
    public Color getBACKGROUND_COLOR() {return new Color(224, 231, 242);}

    public Font getMENUBAR_FONT() {return MetalLookAndFeel.getMenuTextFont();}
    public Color getMENUBAR_BACKGROUND_COLOR() {return new Color(73, 103, 145);}
    public Color getMENUBAR_FOREGROUND_COLOR() {return Color.WHITE;}
    
    public Color getROLLOVERBUTTON_DEFAULT_COLOR() {return getMENUBAR_BACKGROUND_COLOR();}
    public Color getROLLOVERBUTTON_ROLLOVER_BORDER_COLOR() {return getMENUBAR_FOREGROUND_COLOR();}
    public Color getROLLOVERBUTTON_ROLLOVER_COLOR() {return getMENUBAR_BACKGROUND_COLOR();}
    
    public Color getSIDEBAR_BACKGROUND_END_COLOR() {return new Color(125, 156, 201);}
    public Color getSIDEBAR_BACKGROUND_START_COLOR() {return getMENUBAR_BACKGROUND_COLOR();}
    public Color getSIDEBAR_BORDER_COLOR() {return getGRID_COLOR();}
    public Color getSIDEBAR_ELEMENT_BACKGROUND_COLOR() {return getBACKGROUND_COLOR();}
    public Color getSIDEBAR_ELEMENT_TITLE_BG_END_COLOR() {return getMENUBAR_BACKGROUND_COLOR();}
    public Color getSIDEBAR_ELEMENT_TITLE_BG_START_COLOR() {return getMENUBAR_BACKGROUND_COLOR().darker();}
    public Color getSIDEBAR_ELEMENT_TITLE_FOREGROUND_COLOR() {return getBACKGROUND_COLOR();}
    public Color getSIDEBAR_ELEMENT_TITLE_OVER_COLOR() {return getBACKGROUND_COLOR().brighter();}
    
    public Color getSTATUSBAR_BACKGROUND_COLOR() {return getMENUBAR_BACKGROUND_COLOR();}
    public Color getSTATUSBAR_BORDER_COLOR() {return getMENUBAR_BACKGROUND_COLOR();}
    
    public Font getTOGGLEBUTTON_FONT() {return MetalLookAndFeel.getMenuTextFont().deriveFont(Font.PLAIN);}
    public Color getTOGGLEBUTTON_SELECTED_BORDER_COLOR() {return new Color(247, 154, 24);}
    public Color getTOGGLEBUTTON_SELECTED_COLOR() {return new Color(255, 203, 107);}
    public Color getTOGGLEBUTTON_UNSELECTED_COLOR() {return getSIDEBAR_ELEMENT_BACKGROUND_COLOR();}
    
    public Font getWELCOME_BIG_FONT() {return MetalLookAndFeel.getWindowTitleFont().deriveFont((float) 28.0);}
    public Font getWELCOME_SMALL_FONT() {return MetalLookAndFeel.getWindowTitleFont().deriveFont((float) 12.0).deriveFont(Font.PLAIN);}
    public Color getWELCOME_BACKGROUND_END_COLOR() {return getSIDEBAR_BACKGROUND_START_COLOR();}
    public Color getWELCOME_BACKGROUND_START_COLOR() {return getSIDEBAR_BACKGROUND_END_COLOR().brighter();}
    public Color getWELCOME_BIG_FOREGROUND_COLOR() {return Color.WHITE;}
    public Color getWELCOME_BIG_ROLLOVER_FOREGROUND_COLOR() {return new Color(255, 203, 151);}

}
