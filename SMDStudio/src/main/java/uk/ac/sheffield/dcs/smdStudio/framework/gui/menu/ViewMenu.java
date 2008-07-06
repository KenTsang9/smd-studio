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
import java.util.ResourceBundle;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import uk.ac.sheffield.dcs.smdStudio.framework.action.ViewAction;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.DiagramPanel;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.EditorFrame;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.GraphPanel;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.theme.ThemeManager;
import uk.ac.sheffield.dcs.smdStudio.framework.resources.ResourceFactory;


/**
 * View menu
 * 
 * @author Alexandre de Pellegrin
 * 
 */
@SuppressWarnings("serial")
public class ViewMenu extends JMenu
{

    /**
     * Default constructor
     * 
     * @param editorFrame where this menu is attached
     * @param rBundle to access to external resources
     * @param rFactory to access to external ressources
     */
    public ViewMenu(EditorFrame editorFrame, ResourceBundle rBundle, ResourceFactory rFactory)
    {
        this.editorFrame = editorFrame;
        this.tabbedPane = editorFrame.getTabbedPane();
        this.bundle = rBundle;
        this.factory = rFactory;
        this.viewAction = new ViewAction();
        this.createMenu();
    }

    /**
     * Initializes menu
     */
    private void createMenu()
    {
        this.factory.configureMenu(this, "view");

        JMenuItem zoomOut = factory.createMenuItem("view.zoom_out");
        zoomOut.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                performZoomOut();
            }
        });
        this.add(zoomOut);

        JMenuItem zoomIn = factory.createMenuItem("view.zoom_in");
        zoomIn.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                performZoomIn();
            }
        });
        this.add(zoomIn);

        JMenuItem growDrawingArea = factory.createMenuItem("view.grow_drawing_area");
        growDrawingArea.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                performGrowDrawingArea();
            }
        });
        this.add(growDrawingArea);

        JMenuItem clipDrawingArea = factory.createMenuItem("view.clip_drawing_area");
        clipDrawingArea.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                performClipDrawingArea();
            }
        });
        this.add(clipDrawingArea);

        JMenuItem smallerGrid = factory.createMenuItem("view.smaller_grid");
        smallerGrid.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                performDisplaySmallerGrid();
            }
        });
        this.add(smallerGrid);

        JMenuItem largerGrid = factory.createMenuItem("view.larger_grid");
        largerGrid.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                performDisplayLargerGrid();
            }
        });
        this.add(largerGrid);

        final JCheckBoxMenuItem hideGridItem = (JCheckBoxMenuItem) factory.createCheckBoxMenuItem("view.hide_grid");
        hideGridItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                performHideGrid(event);
            }
        });

        this.addMenuListener(new MenuListener()
        {
            public void menuSelected(MenuEvent event)
            {
                DiagramPanel diagramPanel = (DiagramPanel) tabbedPane.getSelectedComponent();
                if (diagramPanel == null) return;
                GraphPanel panel = diagramPanel.getGraphPanel();
                hideGridItem.setSelected(panel.getHideGrid());
            }

            public void menuDeselected(MenuEvent event)
            {
            }

            public void menuCanceled(MenuEvent event)
            {
            }
        });

        JMenu changeLookAndFeelMenu = factory.createMenu("view.change_laf");
        ButtonGroup lookAndFeelButtonGroup = new ButtonGroup();
        String preferedLafName = ThemeManager.getInstance().getPreferedLookAndFeel();
        LookAndFeelInfo[] laf = ThemeManager.getInstance().getInstalledLookAndFeels();
        for (int i = 0; i < laf.length; i++)
        {
            String lafName = laf[i].getName();
            final String lafClassName = laf[i].getClassName();
            JMenuItem lafMenu = new JCheckBoxMenuItem(lafName);
            lafMenu.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    performChangeLookAndFeel(lafClassName);
                }
            });
            changeLookAndFeelMenu.add(lafMenu);
            lookAndFeelButtonGroup.add(lafMenu);
            if (lafClassName.equals(preferedLafName))
            {
                lafMenu.setSelected(true);
            }
        }
        this.add(changeLookAndFeelMenu);

    }

    /**
     * Performs zoom out action
     */
    private void performZoomOut()
    {
        DiagramPanel diagramPanel = (DiagramPanel) tabbedPane.getSelectedComponent();
        viewAction.zoomOut(diagramPanel);
    }

    /**
     * Performs zoom in action
     */
    private void performZoomIn()
    {
        DiagramPanel diagramPanel = (DiagramPanel) tabbedPane.getSelectedComponent();
        viewAction.zoomIn(diagramPanel);
    }

    /**
     * Performs gros drawing area action
     */
    private void performGrowDrawingArea()
    {
        DiagramPanel diagramPanel = (DiagramPanel) tabbedPane.getSelectedComponent();
        viewAction.growDrawingArea(diagramPanel);
    }

    /**
     * Performs clip drawing area action
     */
    private void performClipDrawingArea()
    {
        DiagramPanel diagramPanel = (DiagramPanel) tabbedPane.getSelectedComponent();
        viewAction.clipDrawingArea(diagramPanel);
    }

    /**
     * Performs display smaller grid action
     */
    private void performDisplaySmallerGrid()
    {
        DiagramPanel diagramPanel = (DiagramPanel) tabbedPane.getSelectedComponent();
        viewAction.displaySmallerGrid(diagramPanel);
    }

    /**
     * Performs display larger grid action
     */
    private void performDisplayLargerGrid()
    {
        DiagramPanel diagramPanel = (DiagramPanel) tabbedPane.getSelectedComponent();
        viewAction.displayLargerGrid(diagramPanel);
    }

    /**
     * Performs hist grid action
     * 
     * @param event that handle the checkbox menu item to know if the hide sould be shown or not
     */
    private void performHideGrid(ActionEvent event)
    {
        DiagramPanel diagramPanel = (DiagramPanel) tabbedPane.getSelectedComponent();
        JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem) event.getSource();
        boolean isHidden = menuItem.isSelected();
        viewAction.hideGrid(diagramPanel, isHidden);
    }

    /**
     * Performs look and feel change
     * 
     * @param className look and feel or pgs theme class name
     */
    private void performChangeLookAndFeel(String className)
    {
        viewAction.changeLookAndFeel(className, this.editorFrame, this.bundle);
    }

    /**
     * Current editor frame
     */
    private EditorFrame editorFrame;

    /**
     * To access to menu external properties strings
     */
    private ResourceBundle bundle;

    /**
     * To build this menu with external resources such as icons, texts
     */
    private ResourceFactory factory;

    /**
     * Containing diagram panels
     */
    private JTabbedPane tabbedPane;

    /**
     * Action performerF
     */
    private ViewAction viewAction;

}
