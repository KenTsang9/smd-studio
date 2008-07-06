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

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JPanel;

import uk.ac.sheffield.dcs.smdStudio.framework.diagram.Edge;
import uk.ac.sheffield.dcs.smdStudio.framework.diagram.Graph;
import uk.ac.sheffield.dcs.smdStudio.framework.diagram.Node;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.theme.Theme;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.theme.ThemeManager;
import uk.ac.sheffield.dcs.smdStudio.framework.resources.ResourceBundleUtils;
import uk.ac.sheffield.dcs.smdStudio.framework.swingextension.CustomToggleButton;


/**
 * A tool bar that contains node and edge prototype icons. Exactly one icon is selected at any time.
 */
@SuppressWarnings("serial")
public class SideToolPanel extends JPanel implements ISideToolPanel
{
    /**
     * Constructs a tool bar with no icons.
     */
    public SideToolPanel(Graph graph)
    {
        nodeTools = getStandardNodeTools(graph);
        edgeTools = getStandardEdgeTools(graph);
        nodeButtons = getToggleButtons(nodeTools);
        edgeButtons = getToggleButtons(edgeTools);
        setBackground(ThemeManager.getInstance().getTheme().getSIDEBAR_ELEMENT_BACKGROUND_COLOR());
        fillPanel();
        reset();
    }

    /**
     * Fills current panel with toggle buttons
     */
    private void fillPanel()
    {
        removeAll();
        JPanel nodeButtonsPanel = getButtonPanel(nodeButtons);
        JPanel edgeButtonsPanel = getButtonPanel(edgeButtons);
        setLayout(new BorderLayout());
        add(nodeButtonsPanel, BorderLayout.NORTH);
        add(edgeButtonsPanel, BorderLayout.SOUTH);
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.ac.sheffield.dcs.smdStudio.framework.gui.sidebar.ISideToolPanel#addCustomTool(uk.ac.sheffield.dcs.smdStudio.framework.diagram.Node,
     *      java.lang.String)
     */
    public void addTool(Node nodePrototype, String title)
    {
        Tool newTool = new Tool(nodePrototype, title);
        nodeTools.add(newTool);
        nodeButtons = getToggleButtons(nodeTools);
        fillPanel();
    }

    /**
     * Returns standard node tools associated to a graph
     * 
     * @param graph
     * @return tools collection
     */
    private List<Tool> getStandardNodeTools(Graph graph)
    {
        Node[] nodeTypes = graph.getNodePrototypes();
        List<Tool> tools = new ArrayList<Tool>();
        Tool firstTool = getSelectionTool();
        tools.add(firstTool);
        if (nodeTypes.length == 0)
        {
            return tools;
        }
        ResourceBundle graphResources = ResourceBundleUtils.getStringsResourceBundleForObject(graph);
        for (int i = 0; i < nodeTypes.length; i++)
        {
            String label = graphResources.getString("node" + (i + 1) + ".tooltip");
            Tool aTool = new Tool(nodeTypes[i], label);
            tools.add(aTool);
        }
        return tools;
    }

    /**
     * Returns standard edge tools associated to a graph
     * 
     * @param graph
     * @return tools collection
     */
    private List<Tool> getStandardEdgeTools(Graph graph)
    {
        Edge[] edgeTypes = graph.getEdgePrototypes();
        List<Tool> tools = new ArrayList<Tool>();
        if (edgeTypes.length == 0)
        {
            return tools;
        }
        ResourceBundle graphResources = ResourceBundleUtils.getStringsResourceBundleForObject(graph);
        for (int i = 0; i < edgeTypes.length; i++)
        {
            String label = graphResources.getString("edge" + (i + 1) + ".tooltip");
            Tool aTool = new Tool(edgeTypes[i], label);
            tools.add(aTool);
        }
        return tools;
    }

    /**
     * @return selection tool (which refers to a special constructor
     */
    private Tool getSelectionTool()
    {
        return new Tool();
    }

    /**
     * @return curretly selected button
     */
    private CustomToggleButton getSelectedButton()
    {
        for (CustomToggleButton button : this.nodeButtons)
        {
            if (button.isSelected())
            {
                return button;
            }
        }
        for (CustomToggleButton button : this.edgeButtons)
        {
            if (button.isSelected())
            {
                return button;
            }
        }
        reset();
        return this.nodeButtons.get(0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.ac.sheffield.dcs.smdStudio.framework.gui.sidebar.ISideToolPanel#getSelectedTool()
     */
    public Tool getSelectedTool()
    {
        CustomToggleButton button = getSelectedButton();
        int posForNodes = this.nodeButtons.indexOf(button);
        if (posForNodes >= 0)
        {
            return this.nodeTools.get(posForNodes);
        }
        int posForEdges = this.edgeButtons.indexOf(button);
        if (posForEdges >= 0)
        {
            return this.edgeTools.get(posForEdges);
        }
        reset();
        return this.nodeTools.get(0);
    }

    /**
     * @param diagram tools
     * @return buttons representing tools
     */
    private List<CustomToggleButton> getToggleButtons(List<Tool> tools)
    {
        List<CustomToggleButton> buttons = new ArrayList<CustomToggleButton>();
        for (Tool aTool : tools)
        {
            Theme cLAF = ThemeManager.getInstance().getTheme();
            final CustomToggleButton button = new CustomToggleButton(aTool.getLabel(), aTool.getIcon(), cLAF
                    .getTOGGLEBUTTON_SELECTED_COLOR(), cLAF.getTOGGLEBUTTON_SELECTED_BORDER_COLOR(), cLAF
                    .getTOGGLEBUTTON_UNSELECTED_COLOR());

            buttons.add(button);
        }
        return buttons;
    }

    /**
     * Shows/Hides tools titles
     * 
     * @param isVisible
     */
    public void setToolsTitlesVisible(boolean isVisible)
    {
        for (CustomToggleButton button : this.edgeButtons)
        {
            button.setTextVisible(isVisible);
        }
        for (CustomToggleButton button : this.nodeButtons)
        {
            button.setTextVisible(isVisible);
        }
    }

    /**
     * Performs button select
     * 
     * @param selectedButton to be considered as selected
     */
    private void setSelectedButton(CustomToggleButton selectedButton)
    {
        for (CustomToggleButton button : this.nodeButtons)
        {
            if (button != selectedButton)
            {
                button.setSelected(false);
            }
            if (button == selectedButton)
            {
                button.setSelected(true);
                int pos = this.nodeButtons.indexOf(button);
                fireToolChangeEvent(nodeTools.get(pos));
            }
        }
        for (CustomToggleButton button : this.edgeButtons)
        {
            if (button != selectedButton)
            {
                button.setSelected(false);
            }
            if (button == selectedButton)
            {
                button.setSelected(true);
                int pos = this.edgeButtons.indexOf(button);
                fireToolChangeEvent(edgeTools.get(pos));
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.ac.sheffield.dcs.smdStudio.framework.gui.sidebar.ISideToolPanel#selectNextButton()
     */
    public void selectNextButton()
    {
        int nextPos = 0;
        CustomToggleButton selectedButton = getSelectedButton();
        int posForNodes = this.nodeButtons.indexOf(selectedButton);
        if (posForNodes >= 0)
        {
            nextPos = posForNodes + 1;
            if (nextPos < this.nodeButtons.size())
            {
                setSelectedButton(this.nodeButtons.get(nextPos));
            }
            if (nextPos >= this.nodeButtons.size() && this.edgeButtons.size() > 0)
            {
                setSelectedButton(this.edgeButtons.get(0));
            }
            return;
        }
        int posForEdges = this.edgeButtons.indexOf(selectedButton);
        if (posForEdges >= 0)
        {
            nextPos = posForEdges + 1;
            if (nextPos < this.edgeButtons.size())
            {
                setSelectedButton(this.edgeButtons.get(nextPos));
            }
            return;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.ac.sheffield.dcs.smdStudio.framework.gui.sidebar.ISideToolPanel#selectPreviousButton()
     */
    public void selectPreviousButton()
    {
        int previousPos = 0;
        CustomToggleButton selectedButton = getSelectedButton();
        int posForNodes = this.nodeButtons.indexOf(selectedButton);
        if (posForNodes >= 0)
        {
            previousPos = posForNodes - 1;
            if (previousPos >= 0)
            {
                setSelectedButton(this.nodeButtons.get(previousPos));
            }
            return;
        }
        int posForEdges = this.edgeButtons.indexOf(selectedButton);
        if (posForEdges >= 0)
        {
            previousPos = posForEdges - 1;
            if (previousPos >= 0)
            {
                setSelectedButton(this.edgeButtons.get(previousPos));
            }
            if (previousPos < 0 && this.nodeButtons.size() > 0)
            {
                setSelectedButton(this.nodeButtons.get(this.nodeButtons.size() - 1));
            }
            return;
        }
    }

    /**
     * Creates a panel that contains custom toggle buttons. Also sets mouse listeners.
     * 
     * @param buttons to be added to this panel
     * @return JPanel
     */
    private JPanel getButtonPanel(List<CustomToggleButton> buttons)
    {
        JPanel buttonPanel = new JPanel();
        for (final CustomToggleButton button : buttons)
        {
            button.addMouseListener(new MouseAdapter()
            {
                public void mouseClicked(MouseEvent arg0)
                {
                    setSelectedButton(button);
                }
            });
            buttonPanel.add(button);
        }

        buttonPanel.setLayout(new GridLayout(0, 1));
        buttonPanel.addMouseWheelListener(new MouseWheelListener()
        {

            public void mouseWheelMoved(MouseWheelEvent e)
            {
                int scroll = e.getUnitsToScroll();
                if (scroll > 0)
                {
                    selectNextButton();
                }
                if (scroll < 0)
                {
                    selectPreviousButton();
                }
            }

        });
        return buttonPanel;
    }

    /**
     * Remenbers selected tool and informs all listeners about this change
     * 
     * @param nodeOrEdge
     */
    private void fireToolChangeEvent(Tool tool)
    {
        Iterator<Listener> it = this.listeners.iterator();
        while (it.hasNext())
        {
            Listener listener = it.next();
            listener.toolSelectionChanged(tool);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.ac.sheffield.dcs.smdStudio.framework.gui.sidebar.ISideToolPanel#reset()
     */
    public void reset()
    {
        if (this.nodeButtons.size() > 0)
        {
            setSelectedButton(this.nodeButtons.get(0));
        }
    }

    /**
     * Listener to be implmenented and registered by each class that needs to know toolbar actions
     * 
     * @author Alexandre de Pellegrin
     * 
     */
    public interface Listener
    {

        /**
         * Invoked when a tool is selected
         * 
         * @param selectedNodeOrEdge the selected tool
         */
        void toolSelectionChanged(Tool selectedTool);

    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.ac.sheffield.dcs.smdStudio.framework.gui.sidebar.ISideToolPanel#addListener(uk.ac.sheffield.dcs.smdStudio.framework.gui.sidebar.SideToolPanel.Listener)
     */
    public void addListener(Listener listener)
    {
        this.listeners.add(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.ac.sheffield.dcs.smdStudio.framework.gui.sidebar.ISideToolPanel#removeListener(uk.ac.sheffield.dcs.smdStudio.framework.gui.sidebar.SideToolPanel.Listener)
     */
    public void removeListener(Listener listener)
    {
        this.listeners.remove(listener);
    }

    private List<Listener> listeners = new ArrayList<Listener>();
    private List<Tool> nodeTools;
    private List<Tool> edgeTools;
    private List<CustomToggleButton> nodeButtons;
    private List<CustomToggleButton> edgeButtons;

}