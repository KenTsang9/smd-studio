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

/**
 * GUI theme. It includes look and feel and colors
 * 
 * @author Alexandre de Pellegrin
 *
 */
public interface Theme
{

    /**
     * Apply look and feel theme
     */
    public void activate();
    
    public abstract Color getWHITE_COLOR();
    public abstract Color getBLACK_COLOR();

    public abstract Color getGRID_COLOR();
    
    public abstract Color getBACKGROUND_COLOR();
    
    public abstract Font getWELCOME_BIG_FONT();
    public abstract Font getWELCOME_SMALL_FONT();
    public abstract Color getWELCOME_BACKGROUND_START_COLOR();
    public abstract Color getWELCOME_BACKGROUND_END_COLOR();
    public abstract Color getWELCOME_BIG_FOREGROUND_COLOR();
    public abstract Color getWELCOME_BIG_ROLLOVER_FOREGROUND_COLOR();
    
    public abstract Font getMENUBAR_FONT();
    public abstract Color getMENUBAR_BACKGROUND_COLOR();
    public abstract Color getMENUBAR_FOREGROUND_COLOR();

    public abstract Color getSIDEBAR_BACKGROUND_START_COLOR();
    public abstract Color getSIDEBAR_BACKGROUND_END_COLOR();
    public abstract Color getSIDEBAR_ELEMENT_BACKGROUND_COLOR();
    public abstract Color getSIDEBAR_ELEMENT_TITLE_BG_START_COLOR();
    public abstract Color getSIDEBAR_ELEMENT_TITLE_BG_END_COLOR();
    public abstract Color getSIDEBAR_ELEMENT_TITLE_FOREGROUND_COLOR();
    public abstract Color getSIDEBAR_ELEMENT_TITLE_OVER_COLOR();
    public abstract Color getSIDEBAR_BORDER_COLOR();

    public abstract Color getSTATUSBAR_BACKGROUND_COLOR();
    public abstract Color getSTATUSBAR_BORDER_COLOR();
    
    public abstract Color getTOGGLEBUTTON_SELECTED_COLOR();
    public abstract Color getTOGGLEBUTTON_SELECTED_BORDER_COLOR();
    public abstract Color getTOGGLEBUTTON_UNSELECTED_COLOR();
    public abstract Font getTOGGLEBUTTON_FONT();
    public abstract Color getROLLOVERBUTTON_DEFAULT_COLOR();
    public abstract Color getROLLOVERBUTTON_ROLLOVER_COLOR();
    public abstract Color getROLLOVERBUTTON_ROLLOVER_BORDER_COLOR();

}