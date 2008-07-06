package uk.ac.sheffield.dcs.smdStudio.framework.gui;

import uk.ac.sheffield.dcs.smdStudio.framework.diagram.Graph;
import uk.ac.sheffield.dcs.smdStudio.framework.diagram.Id;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.sidebar.ISideBar;

public interface IDiagramPanel
{

    /**
     * Sets a graph to this panel
     * 
     * @param aGraph
     */
    public void setGraph(Graph aGraph);

    /**
     * @return current graph
     */
    public Graph getGraph();

    /**
     * @return current side bar
     */
    public ISideBar getSideBar();
    
    /**
     * Switch from small side bar to large one or from small to large
     */
    public void maximizeMinimizeSideBar();

    /**
     * @return current diagram's title
     */
    public String getTitle();

    /**
     * Sets new title
     * 
     * @param newValue t
     */
    public void setTitle(String newValue);

    /**
     * Gets the fileName property.
     * 
     * @return the file path
     */
    public String getFilePath();

    /**
     * Sets the fileName property.
     * 
     * @param path the file path
     */
    public void setFilePath(String path);

    /**
     * @return true if contained graph has been modified and needs to be saved
     */
    public boolean isSaveNeeded();

    /**
     * Indicates if the current graph needs to be saved
     * 
     * @param isSaveNeeded
     */
    public void setSaveNeeded(boolean isSaveNeeded);

    /**
     * Registers a listener on this diagram panel to capture events
     * 
     * @param l
     */
    public void addListener(DiagramPanelListener l);

    /**
     * @return unique id
     */
    public Id getId();
    
    /**
     * refresh current display by repainting components
     */
    public void refreshDisplay();

}