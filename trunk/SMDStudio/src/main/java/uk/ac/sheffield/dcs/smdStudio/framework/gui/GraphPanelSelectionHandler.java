/*
 * Projet     : 
 * Package    : uk.ac.sheffield.dcs.smdStudio.framework.gui
 * Auteur     : a.depellegrin
 * Cr�� le    : 8 mars 2007
 */
package uk.ac.sheffield.dcs.smdStudio.framework.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import uk.ac.sheffield.dcs.smdStudio.framework.diagram.Edge;
import uk.ac.sheffield.dcs.smdStudio.framework.diagram.Node;


public class GraphPanelSelectionHandler
{

    public void setSelectedElement(Node node)
    {
        this.selectedNodes.clear();
        this.selectedEdges.clear();
        addSelectedElement(node);
    }

    public void setSelectedElement(Edge edge)
    {
        this.selectedNodes.clear();
        this.selectedEdges.clear();
        addSelectedElement(edge);
    }
    
    public void updateSelectedElements(Node[] nodes) {
        for (int i = 0; i < nodes.length; i++) {
            if (isElementAlreadySelected(nodes[i])) {
                addSelectedElement(nodes[i]);
            }
        }
    }

    public void updateSelectedElements(Edge[] edges) {
        for (int i = 0; i < edges.length; i++) {
            if (isElementAlreadySelected(edges[i])) {
                addSelectedElement(edges[i]);
            }
        }
    }

    public void addSelectedElement(Node node)
    {
        if (this.selectedNodes.contains(node))
        {
            this.removeElementFromSelection(node);
        }
        this.selectedNodes.add(node);
        this.isNodeSelectedAtLeast = true;
        this.isEdgeSelectedAtLeast = false;
    }

    public void addSelectedElement(Edge edge)
    {
        if (this.selectedEdges.contains(edge))
        {
            this.removeElementFromSelection(edge);
        }
        this.selectedNodes.clear();
        this.selectedEdges.add(edge);
        this.isNodeSelectedAtLeast = false;
        this.isEdgeSelectedAtLeast = true;
    }

    public void removeElementFromSelection(Node node)
    {
        if (this.selectedNodes.contains(node))
        {
            int i = this.selectedNodes.indexOf(node);
            this.selectedNodes.remove(i);
        }
    }

    public void removeElementFromSelection(Edge edge)
    {
        if (this.selectedEdges.contains(edge))
        {
            int i = this.selectedEdges.indexOf(edge);
            this.selectedEdges.remove(i);
        }
    }

    public boolean isElementAlreadySelected(Node node)
    {
        if (this.selectedNodes.contains(node)) return true;
        return false;
    }

    public boolean isElementAlreadySelected(Edge edge)
    {
        if (this.selectedEdges.contains(edge)) return true;
        return false;
    }

    public void clearSelection()
    {
        this.selectedNodes.clear();
        this.selectedEdges.clear();
        this.isNodeSelectedAtLeast = false;
        this.isEdgeSelectedAtLeast = false;
    }

    public Node getLastSelectedNode()
    {
        return getLastElement(this.selectedNodes);
    }

    public Edge getLastSelectedEdge()
    {
        return getLastElement(this.selectedEdges);
    }

    public boolean isNodeSelectedAtLeast()
    {
        return this.isNodeSelectedAtLeast;
    }

    public boolean isEdgeSelectedAtLeast()
    {
        return this.isEdgeSelectedAtLeast;
    }

    public List<Node> getSelectedNodes()
    {
        return Collections.unmodifiableList(selectedNodes);
    }

    public List<Edge> getSelectedEdges()
    {
        return Collections.unmodifiableList(selectedEdges);
    }

    private <T> T getLastElement(List<T> list)
    {
        int size = list.size();
        if (size <= 0)
        {
            return null;
        }
        return list.get(size - 1);
    }

    private List<Node> selectedNodes = new ArrayList<Node>();

    private List<Edge> selectedEdges = new ArrayList<Edge>();

    private boolean isNodeSelectedAtLeast = false;
    private boolean isEdgeSelectedAtLeast = false;

}
