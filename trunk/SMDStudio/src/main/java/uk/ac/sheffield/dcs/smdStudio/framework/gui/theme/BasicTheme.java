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

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

/**
 * Currently supported JVM theme. This themes fakes swing composants to extract current look and feel theme colors
 * 
 * @author ALexandre de Pellegrin
 */
public class BasicTheme extends AbstractTheme
{

    /**
     * Default constructor
     * @param className
     */
    public BasicTheme(String className) {
        this.lookAndFeelClassName = className;
    }

    /* (non-Javadoc)
     * @see uk.ac.sheffield.dcs.smdStudio.framework.gui.theme.AbstractTheme#getLookAndFeel()
     */
    protected String getLookAndFeelClassName()
    {
        return this.lookAndFeelClassName;
    }
    
    /* (non-Javadoc)
     * @see uk.ac.sheffield.dcs.smdStudio.framework.gui.theme.AbstractTheme#setup()
     */
    protected void setup() 
    {
        this.defaultGridColor = new Color(220, 220, 220);
        
        JMenuBar menuBar = new JMenuBar();
        this.basicMenubarBackground = menuBar.getBackground();
        this.basicMenubarForeground = menuBar.getForeground();
        this.basicMenubarFont = menuBar.getFont();
        
        JMenu menu = new JMenu();
        this.basicMenuBackground = menu.getBackground();
        this.basicMenuForeground = menu.getForeground();
        this.basicMenuFont = menu.getFont();
        
        JTextField textArea = new JTextField();
        this.basicWhite = textArea.getBackground();
        this.basicBlack = textArea.getForeground();
        
        JFrame frame = new JFrame();
        this.basicFrameBackground = frame.getBackground();
        frame.dispose();
        
        JProgressBar progressBar = new JProgressBar();
        this.basicProgressbarForeground = progressBar.getForeground();
        
        JTabbedPane pane = new JTabbedPane();
        this.basicTabbedPaneBackground = pane.getBackground();
    }
    
    
    public Color getBLACK_COLOR() {return this.basicBlack;}
    public Color getWHITE_COLOR() {return this.basicWhite;}

    public Color getGRID_COLOR() {return this.defaultGridColor;}

    public Color getBACKGROUND_COLOR() {return this.basicMenuBackground;}
    
    public Font getMENUBAR_FONT() {return this.basicMenubarFont;}
    public Color getMENUBAR_BACKGROUND_COLOR() {return this.basicMenubarBackground;}
    public Color getMENUBAR_FOREGROUND_COLOR() {return this.basicMenubarForeground;}
    
    public Color getROLLOVERBUTTON_DEFAULT_COLOR() {return this.basicMenuBackground;}
    public Color getROLLOVERBUTTON_ROLLOVER_BORDER_COLOR() {return this.basicMenuForeground;}
    public Color getROLLOVERBUTTON_ROLLOVER_COLOR() {return this.basicMenuBackground;}

    public Color getSIDEBAR_BACKGROUND_END_COLOR() {return this.basicTabbedPaneBackground;}
    public Color getSIDEBAR_BACKGROUND_START_COLOR() {return this.basicTabbedPaneBackground;}
    public Color getSIDEBAR_BORDER_COLOR() {return this.basicMenuBackground;}
    public Color getSIDEBAR_ELEMENT_BACKGROUND_COLOR() {return this.basicMenuBackground;}
    public Color getSIDEBAR_ELEMENT_TITLE_BG_END_COLOR() {return this.basicMenuBackground;}
    public Color getSIDEBAR_ELEMENT_TITLE_BG_START_COLOR() {return this.basicMenuBackground.darker();}
    public Color getSIDEBAR_ELEMENT_TITLE_FOREGROUND_COLOR() {return this.basicFrameBackground;}
    public Color getSIDEBAR_ELEMENT_TITLE_OVER_COLOR() {return this.basicFrameBackground.brighter();}
    
    public Color getSTATUSBAR_BACKGROUND_COLOR() {return this.basicMenuBackground;}
    public Color getSTATUSBAR_BORDER_COLOR() {return this.basicMenuBackground;}
    
    public Font getTOGGLEBUTTON_FONT() {return this.basicMenuFont.deriveFont(Font.PLAIN);}
    public Color getTOGGLEBUTTON_SELECTED_BORDER_COLOR() {return this.basicTabbedPaneBackground.darker();}
    public Color getTOGGLEBUTTON_SELECTED_COLOR() {return this.basicTabbedPaneBackground;}
    public Color getTOGGLEBUTTON_UNSELECTED_COLOR() {return this.basicMenuBackground;}
    
    public Font getWELCOME_SMALL_FONT() {return this.basicMenuFont.deriveFont((float) 12.0).deriveFont(Font.PLAIN);}
    public Font getWELCOME_BIG_FONT() {return this.basicMenuFont.deriveFont((float) 28.0);}
    public Color getWELCOME_BACKGROUND_END_COLOR() {return this.basicMenuBackground;}
    public Color getWELCOME_BACKGROUND_START_COLOR() {return this.basicMenuBackground.brighter();}
    public Color getWELCOME_BIG_FOREGROUND_COLOR() {return this.basicProgressbarForeground;}
    public Color getWELCOME_BIG_ROLLOVER_FOREGROUND_COLOR() {return getWELCOME_BIG_FOREGROUND_COLOR().darker();}
    
    /** Look and feel class name */
    private String lookAndFeelClassName;
    
    /** Default grid color */
    private Color defaultGridColor;
    
    /** Menubar bg color */
    private Color basicMenubarBackground;
    
    /** Menubar fg color */
    private Color basicMenubarForeground;
    
    /** Menubar font */
    private Font basicMenubarFont;
    
    /** Menu bg color */
    private Color basicMenuBackground;
    
    /** Menu fg color */
    private Color basicMenuForeground;
    
    /** Menu font */
    private Font basicMenuFont;
    
    /** Default white */
    private Color basicWhite;
    
    /** Default black */
    private Color basicBlack;
    
    /** Frame bg color */
    private Color basicFrameBackground;
    
    /** Progress bar color */
    private Color basicProgressbarForeground;
    
    /** Tabbed pane background color */
    private Color basicTabbedPaneBackground;
    


}
