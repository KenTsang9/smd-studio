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

package uk.ac.sheffield.dcs.smdStudio.framework.action;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import uk.ac.sheffield.dcs.smdStudio.framework.diagram.Graph;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.DiagramPanel;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.DialogFactory;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.EditorFrame;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.GraphPanel;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.theme.ThemeManager;


/**
 * Concentrates business logic related to the view menu
 * 
 * @author Alexandre de Pellegrin
 * 
 */
public class ViewAction
{

    /**
     * Zoom out on given panel
     * 
     * @param diagramPanel
     */
    public void zoomOut(DiagramPanel diagramPanel)
    {
        if (diagramPanel == null || diagramPanel.getGraphPanel() == null || diagramPanel.getGraphPanel().getGraph() == null) return;
        GraphPanel panel = diagramPanel.getGraphPanel();
        panel.changeZoom(-1);
    }

    /**
     * Zoom in on given panel
     * 
     * @param diagramPanel
     */
    public void zoomIn(DiagramPanel diagramPanel)
    {
        if (diagramPanel == null || diagramPanel.getGraphPanel() == null || diagramPanel.getGraphPanel().getGraph() == null) return;
        GraphPanel panel = diagramPanel.getGraphPanel();
        panel.changeZoom(1);
    }

    /**
     * Extends given panel drawing area
     * 
     * @param diagramPanel
     */
    public void growDrawingArea(DiagramPanel diagramPanel)
    {
        if (diagramPanel == null || diagramPanel.getGraphPanel() == null || diagramPanel.getGraphPanel().getGraph() == null) return;
        Graph g = diagramPanel.getGraphPanel().getGraph();
        Rectangle2D bounds = g.getBounds((Graphics2D) diagramPanel.getGraphics());
        bounds.add(diagramPanel.getGraphPanel().getBounds());
        g.setMinBounds(new Double(0, 0, ViewAction.GROW_SCALE_FACTOR * bounds.getWidth(), ViewAction.GROW_SCALE_FACTOR
                * bounds.getHeight()));
        diagramPanel.getGraphPanel().revalidate();
        diagramPanel.repaint();
    }

    /**
     * Clips given panel drawing area
     * 
     * @param diagramPanel
     */
    public void clipDrawingArea(DiagramPanel diagramPanel)
    {
        if (diagramPanel == null || diagramPanel.getGraphPanel() == null || diagramPanel.getGraphPanel().getGraph() == null) return;
        Graph g = diagramPanel.getGraphPanel().getGraph();
        g.setMinBounds(null);
        diagramPanel.getGraphPanel().revalidate();
        diagramPanel.repaint();
    }

    /**
     * Sets a smaller grid on the given panel
     * 
     * @param diagramPanel
     */
    public void displaySmallerGrid(DiagramPanel diagramPanel)
    {
        if (diagramPanel == null || diagramPanel.getGraphPanel() == null || diagramPanel.getGraphPanel().getGraph() == null) return;
        GraphPanel panel = diagramPanel.getGraphPanel();
        panel.changeGridSize(-1);
    }

    /**
     * Sets a larger grid on the given panel
     * 
     * @param diagramPanel
     */
    public void displayLargerGrid(DiagramPanel diagramPanel)
    {
        if (diagramPanel == null || diagramPanel.getGraphPanel() == null || diagramPanel.getGraphPanel().getGraph() == null) return;
        GraphPanel panel = diagramPanel.getGraphPanel();
        panel.changeGridSize(1);
    }

    /**
     * Shows/Hides grids on the given panel
     * 
     * @param diagramPanel
     * @param isHidden
     */
    public void hideGrid(DiagramPanel diagramPanel, boolean isHidden)
    {
        if (diagramPanel == null || diagramPanel.getGraphPanel() == null || diagramPanel.getGraphPanel().getGraph() == null) return;
        GraphPanel panel = diagramPanel.getGraphPanel();
        panel.setHideGrid(isHidden);
    }

    /**
     * Changes look and feel to given look and feel
     * 
     * @param look and feel or pgs theme class name
     */
    public void changeLookAndFeel(String className, EditorFrame editorFrame, ResourceBundle resourceBundle)
    {
        String message = resourceBundle.getString("dialog.change_laf.ok");
        String title = resourceBundle.getString("dialog.change_laf.title");
        ImageIcon icon = new ImageIcon(this.getClass().getResource(resourceBundle.getString("dialog.change_laf.icon")));
        JOptionPane optionPane = new JOptionPane();
        optionPane.setMessage(message);
        optionPane.setOptionType(JOptionPane.YES_NO_CANCEL_OPTION);
        optionPane.setIcon(icon);
        DialogFactory.getInstance().showDialog(optionPane, title, true);

        int result = JOptionPane.CANCEL_OPTION;
        if (!JOptionPane.UNINITIALIZED_VALUE.equals(optionPane.getValue()))
        {
            result = ((Integer) optionPane.getValue()).intValue();
        }

        if (result == JOptionPane.YES_OPTION)
        {
            ThemeManager.getInstance().setPreferedLookAndFeel(className);
            editorFrame.restart();
        }
        if (result == JOptionPane.NO_OPTION)
        {
            ThemeManager.getInstance().setPreferedLookAndFeel(className);
        }
    }

    /**
     * Scale factor used to grow drawing area
     */
    private static final double GROW_SCALE_FACTOR = Math.sqrt(2);
}
