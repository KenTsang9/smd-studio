package uk.ac.sheffield.dcs.smdStudio.framework.diagram;

import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;

import uk.ac.sheffield.dcs.smdStudio.product.diagram.common.GraphProperties;


/**
 * Implement this interface and register it a Graph class instance to
 * be informed about modifications.
 * 
 * @author Cay Horstmann
 *
 */
public interface GraphModificationListener
{
    void nodeAdded(Graph g, Node n, Point2D location);
    void nodeRemoved(Graph g, Node n);
    void nodeMoved(Graph g, Node n, double dx, double dy);
    void childAttached(Graph g, int index, Node p, Node c);
    void childDetached(Graph g, int index, Node p, Node c);
    void edgeAdded(Graph g, Edge e, Point2D startPoint, Point2D endPoint);
    void edgeRemoved(Graph g, Edge e);
    void propertyChangedOnNodeOrEdge(Graph g, PropertyChangeEvent event);
    void graphPropertiesChanged(Graph g, GraphProperties properties);
    
}
