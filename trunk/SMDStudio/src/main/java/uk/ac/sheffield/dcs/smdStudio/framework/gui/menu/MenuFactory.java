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

import java.util.Locale;
import java.util.ResourceBundle;

import uk.ac.sheffield.dcs.smdStudio.framework.gui.EditorFrame;
import uk.ac.sheffield.dcs.smdStudio.framework.resources.ResourceBundleConstant;
import uk.ac.sheffield.dcs.smdStudio.framework.resources.ResourceFactory;


/**
 * Menu factory
 * 
 * Be carefull, it is not a singleton
 * 
 * @author Alexandre de Pellegrin
 * 
 */
public class MenuFactory
{

    /**
     * @param editorFrame
     * @return edit menu
     */
    public EditMenu getEditMenu(EditorFrame editorFrame)
    {
        if (this.editMenu == null)
        {
            this.editMenu = new EditMenu(editorFrame, this.getResourceFactory());
        }
        return this.editMenu;
    }

    /**
     * @param editorFrame
     * @return file menu
     */
    public FileMenu getFileMenu(EditorFrame editorFrame)
    {
        if (this.fileMenu == null)
        {
            this.fileMenu = new FileMenu(editorFrame, this.getResourceFactory());
        }
        return this.fileMenu;
    }

    /**
     * @param editorFrame
     * @return help menu
     */
    public HelpMenu getHelpMenu(EditorFrame editorFrame)
    {
        if (this.helpMenu == null)
        {
            this.helpMenu = new HelpMenu(editorFrame, this.getResourceFactory());
        }
        return this.helpMenu;
    }

    /**
     * @param editorFrame
     * @return view menu
     */
    public ViewMenu getViewMenu(EditorFrame editorFrame)
    {
        if (this.viewMenu == null)
        {
            this.viewMenu = new ViewMenu(editorFrame, this.getResourceBundle(), this.getResourceFactory());
        }
        return this.viewMenu;
    }

    /**
     * @return resource factory used to create swing components the used external resources as parameter (icons, texts...)
     */
    private ResourceFactory getResourceFactory()
    {
        if (this.resourceFactory == null)
        {
            ResourceBundle rs = this.getResourceBundle();
            this.resourceFactory = new ResourceFactory(rs);
        }
        return this.resourceFactory;
    }

    /**
     * @return resource bundle linked with menus
     */
    private ResourceBundle getResourceBundle()
    {
        if (this.resourceBundle == null)
        {
            this.resourceBundle = ResourceBundle.getBundle(ResourceBundleConstant.MENU_STRINGS, Locale.getDefault());
        }
        return this.resourceBundle;
    }

    private ResourceFactory resourceFactory;
    private ResourceBundle resourceBundle;
    private EditMenu editMenu;
    private FileMenu fileMenu;
    private HelpMenu helpMenu;
    private ViewMenu viewMenu;

}
