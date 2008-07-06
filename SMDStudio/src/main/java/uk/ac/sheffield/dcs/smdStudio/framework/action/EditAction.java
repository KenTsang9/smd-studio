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

import uk.ac.sheffield.dcs.smdStudio.framework.gui.DiagramPanel;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.GraphPanel;

/**
 * Concentrates actions that belongs to edit menu
 * 
 * @author Alexandre de Pellegrin
 * 
 */
public class EditAction
{

    /**
     * Undo last diagram change
     * 
     * @param diagramPanel
     */
    public void undo(DiagramPanel diagramPanel)
    {
        if (diagramPanel == null || diagramPanel.getGraphPanel() == null || diagramPanel.getGraphPanel().getGraph() == null) return;
        GraphPanel panel = diagramPanel.getGraphPanel();
        panel.undo();
    }

    /**
     * Redo undone diagram changes
     * 
     * @param diagramPanel
     */
    public void redo(DiagramPanel diagramPanel)
    {
        if (diagramPanel == null || diagramPanel.getGraphPanel() == null || diagramPanel.getGraphPanel().getGraph() == null) return;
        GraphPanel panel = diagramPanel.getGraphPanel();
        panel.redo();
    }

    /**
     * Edits selected item
     * 
     * @param diagramPanel
     */
    public void edit(DiagramPanel diagramPanel)
    {
        if (diagramPanel == null || diagramPanel.getGraphPanel() == null || diagramPanel.getGraphPanel().getGraph() == null) return;
        GraphPanel panel = diagramPanel.getGraphPanel();
        panel.editSelected();
    }

    /**
     * Cuts selected item
     * 
     * @param diagramPanel
     */
    public void cut(DiagramPanel diagramPanel)
    {
        if (diagramPanel == null || diagramPanel.getGraphPanel() == null || diagramPanel.getGraphPanel().getGraph() == null) return;
        GraphPanel panel = diagramPanel.getGraphPanel();
        panel.cut();        
    }    

    /**
     * Copies selected item
     * 
     * @param diagramPanel
     */
    public void copy(DiagramPanel diagramPanel)
    {
        if (diagramPanel == null || diagramPanel.getGraphPanel() == null || diagramPanel.getGraphPanel().getGraph() == null) return;
        GraphPanel panel = diagramPanel.getGraphPanel();
        panel.copy();        
    }    
    
    /**
     * Pastes selected item
     * 
     * @param diagramPanel
     */
    public void paste(DiagramPanel diagramPanel)
    {
        if (diagramPanel == null || diagramPanel.getGraphPanel() == null || diagramPanel.getGraphPanel().getGraph() == null) return;
        GraphPanel panel = diagramPanel.getGraphPanel();
        panel.paste();        
    }    
    
    /**
     * Deletes selected item
     * 
     * @param diagramPanel
     */
    public void delete(DiagramPanel diagramPanel)
    {
        if (diagramPanel == null || diagramPanel.getGraphPanel() == null || diagramPanel.getGraphPanel().getGraph() == null) return;
        GraphPanel panel = diagramPanel.getGraphPanel();
        panel.removeSelected();
    }

    /**
     * Selects next item
     * 
     * @param diagramPanel
     */
    public void selectNext(DiagramPanel diagramPanel)
    {
        if (diagramPanel == null || diagramPanel.getGraphPanel() == null || diagramPanel.getGraphPanel().getGraph() == null) return;
        GraphPanel panel = diagramPanel.getGraphPanel();
        panel.selectNext(1);
    }

    /**
     * Selects previous item
     * 
     * @param diagramPanel
     */
    public void selectPrevious(DiagramPanel diagramPanel)
    {
        if (diagramPanel == null || diagramPanel.getGraphPanel() == null || diagramPanel.getGraphPanel().getGraph() == null) return;
        GraphPanel panel = diagramPanel.getGraphPanel();
        panel.selectNext(-1);
    }
}
