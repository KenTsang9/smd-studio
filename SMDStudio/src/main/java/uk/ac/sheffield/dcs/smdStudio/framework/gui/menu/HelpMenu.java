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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import uk.ac.sheffield.dcs.smdStudio.framework.action.HelpAction;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.EditorFrame;
import uk.ac.sheffield.dcs.smdStudio.framework.resources.ResourceFactory;


/**
 * Help menu
 * 
 * @author Alexandre de Pellegrin
 * 
 */
@SuppressWarnings("serial")
public class HelpMenu extends JMenu
{

    /**
     * Default constructor
     * 
     * @param editorFrame where this menu is atatched
     * @param factory to access to external resources such as texts, icons
     */
    public HelpMenu(EditorFrame editorFrame, ResourceFactory factory)
    {
        this.editorFrame = editorFrame;
        this.menuFactory = factory;
        this.helpAction = new HelpAction();
        this.createMenu();
    }

    /**
     * Initializes menu
     */
    private void createMenu()
    {
        this.menuFactory.configureMenu(this, "help");

        JMenuItem userGuideItem = menuFactory.createMenuItem("help.userguide");
        userGuideItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                performOpenUserGuide();
            }

        });
        this.add(userGuideItem);

        JMenuItem homepageItem = menuFactory.createMenuItem("help.homepage");
        homepageItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                performOpenHomePage();
            }

        });
        this.add(homepageItem);

        JMenuItem aboutItem = menuFactory.createMenuItem("help.about");
        aboutItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                performShowAboutDialog();
            }

        });
        this.add(aboutItem);

    }

    /**
     * Displays the About dialog box.
     */
    private void performShowAboutDialog()
    {
        helpAction.showAboutDialog(editorFrame);
    }

    /**
     * Opens user guide
     */
    private void performOpenUserGuide()
    {
        helpAction.openUserGuide();
    }

    /**
     * Opens homepage
     */
    private void performOpenHomePage()
    {
        helpAction.openHomepage();
    }

    /**
     * Editor frame where this menu is attached to
     */
    private JFrame editorFrame;

    /**
     * Resource factory to access esternal resources
     */
    private ResourceFactory menuFactory;

    /**
     * Busines logic action
     */
    private HelpAction helpAction;

}
