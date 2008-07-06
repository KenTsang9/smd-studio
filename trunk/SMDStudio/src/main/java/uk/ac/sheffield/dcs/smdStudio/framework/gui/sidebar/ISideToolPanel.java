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

package uk.ac.sheffield.dcs.smdStudio.framework.gui.sidebar;

import uk.ac.sheffield.dcs.smdStudio.framework.diagram.Node;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.sidebar.SideToolPanel.Listener;

/**
 * Side tool panel interface
 * 
 * @author Alexandre de Pellegrin
 * 
 */
public interface ISideToolPanel
{

    /**
     * Registers a new node tool to this panel.<br>
     * <br>
     * Example :<br>
     * ImageIcon imageIcon = new ImageIcon(this.getClass().getResource("/icons/72x72/welcome_create.png"));<br>
     * final ImageNode imageNode = new ImageNode(imageIcon.getImage());<br>
     * addCustomTool(imageNode, "test");<br>
     * 
     * @param nodePrototype
     * @param title
     */
    public void addTool(Node nodePrototype, String title);

    /**
     * Select next button
     */
    public void selectNextButton();

    /**
     * Select previous button
     */
    public void selectPreviousButton();

    /**
     * Resets tool selection by selecting the first of the list
     */
    public void reset();

    /**
     * Declares a new listener
     * 
     * @param listener
     */
    public void addListener(Listener listener);

    /**
     * Removes a declared listener
     * 
     * @param listener
     */
    public void removeListener(Listener listener);

    /**
     * @return currently selected tool
     */
    public Tool getSelectedTool();

}