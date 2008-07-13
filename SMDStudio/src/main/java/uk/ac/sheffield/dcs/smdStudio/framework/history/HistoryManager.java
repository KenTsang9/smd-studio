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

package uk.ac.sheffield.dcs.smdStudio.framework.history;

import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

import uk.ac.sheffield.dcs.smdStudio.framework.diagram.Edge;
import uk.ac.sheffield.dcs.smdStudio.framework.diagram.Graph;
import uk.ac.sheffield.dcs.smdStudio.framework.diagram.GraphModificationListener;
import uk.ac.sheffield.dcs.smdStudio.framework.diagram.Node;
import uk.ac.sheffield.dcs.smdStudio.product.diagram.common.GraphProperties;

/**
 * Manages action history happened on graph
 * 
 * @author Cay S. Horstmann / Alexandre de Pellegrin
 * 
 */
public class HistoryManager {

	/**
	 * Default constructor
	 * 
	 * @param graph
	 */
	public HistoryManager(Graph graph) {
		GraphModificationListener graphModListener = new GraphModificationListener() {
			public void childAttached(final Graph g, final int index,
					final Node p, final Node c) {
				CompoundEdit capturedEdit = getCurrentCapturedEdit();
				if (capturedEdit == null)
					return;
				UndoableEdit edit = new AbstractUndoableEdit() {

					private static final long serialVersionUID = 1L;

					@Override
					public void undo() throws CannotUndoException {
						p.removeChild(c);
						super.undo();
					}

					@Override
					public void redo() throws CannotRedoException {
						super.redo();
						p.addChild(index, c);
					}
				};
				capturedEdit.addEdit(edit);
			}

			public void childDetached(final Graph g, final int index,
					final Node p, final Node c) {
				CompoundEdit capturedEdit = getCurrentCapturedEdit();
				if (capturedEdit == null)
					return;
				UndoableEdit edit = new AbstractUndoableEdit() {

					private static final long serialVersionUID = 1L;

					@Override
					public void undo() throws CannotUndoException {
						p.addChild(index, c);
						super.undo();
					}

					@Override
					public void redo() throws CannotRedoException {
						super.redo();
						p.removeChild(c);
					}
				};
				capturedEdit.addEdit(edit);
			}

			public void edgeAdded(final Graph g, final Edge e,
					Point2D startPoint, Point2D endPiint) {
				CompoundEdit capturedEdit = getCurrentCapturedEdit();
				if (capturedEdit == null)
					return;
				UndoableEdit edit = new AbstractUndoableEdit() {

					private static final long serialVersionUID = 1L;

					@Override
					public void undo() throws CannotUndoException {
						g.removeEdge(e);
						super.undo();
					}

					@Override
					public void redo() throws CannotRedoException {
						super.redo();
						g.connect(e, e.getStart(), e.getEnd());
					}
				};
				capturedEdit.addEdit(edit);
			}

			public void edgeRemoved(final Graph g, final Edge e) {
				CompoundEdit capturedEdit = getCurrentCapturedEdit();
				if (capturedEdit == null)
					return;
				UndoableEdit edit = new AbstractUndoableEdit() {

					private static final long serialVersionUID = 1L;

					@Override
					public void undo() throws CannotUndoException {
						g.connect(e, e.getStart(), e.getEnd());
						super.undo();
					}

					@Override
					public void redo() throws CannotRedoException {
						super.redo();
						g.removeEdge(e);
					}
				};
				capturedEdit.addEdit(edit);
			}

			public void nodeAdded(final Graph g, final Node n,
					final Point2D location) {
				CompoundEdit capturedEdit = getCurrentCapturedEdit();
				if (capturedEdit == null)
					return;
				UndoableEdit edit = new AbstractUndoableEdit() {

					private static final long serialVersionUID = 1L;

					@Override
					public void undo() throws CannotUndoException {
						g.removeNode(n);
						super.undo();
					}

					@Override
					public void redo() throws CannotRedoException {
						super.redo();
						g.addNode(n, location);
					}
				};
				capturedEdit.addEdit(edit);
			}

			public void nodeRemoved(final Graph g, final Node n) {
				CompoundEdit capturedEdit = getCurrentCapturedEdit();
				if (capturedEdit == null)
					return;
				UndoableEdit edit = new AbstractUndoableEdit() {

					private static final long serialVersionUID = 1L;

					@Override
					public void undo() throws CannotUndoException {
						g.addNode(n, n.getLocation());
						super.undo();
					}

					@Override
					public void redo() throws CannotRedoException {
						super.redo();
						g.removeNode(n);
					}
				};
				capturedEdit.addEdit(edit);
			}

			public void nodeMoved(Graph g, final Node n, final double dx,
					final double dy) {
				CompoundEdit capturedEdit = getCurrentCapturedEdit();
				if (capturedEdit == null)
					return;
				UndoableEdit edit = new AbstractUndoableEdit() {

					private static final long serialVersionUID = 1L;

					@Override
					public void undo() throws CannotUndoException {
						n.translate(-dx, -dy);
						super.undo();
					}

					@Override
					public void redo() throws CannotRedoException {
						super.redo();
						n.translate(dx, dy);
					}
				};
				capturedEdit.addEdit(edit);
			}

			public void propertyChangedOnNodeOrEdge(final Graph g,
					final PropertyChangeEvent event) {
				CompoundEdit capturedEdit = getCurrentCapturedEdit();
				if (capturedEdit == null)
					return;
				UndoableEdit edit = new AbstractUndoableEdit() {

					private static final long serialVersionUID = 1L;

					@Override
					public void undo() throws CannotUndoException {
						PropertyChangeEvent invertedEvent = new PropertyChangeEvent(
								event.getSource(), event.getPropertyName(),
								event.getNewValue(), event.getOldValue());
						g.changeNodeOrEdgeProperty(invertedEvent);
						super.undo();
					}

					@Override
					public void redo() throws CannotRedoException {
						super.redo();
						g.changeNodeOrEdgeProperty(event);
					}
				};
				capturedEdit.addEdit(edit);
			}

			@Override
			public void graphPropertiesChanged(final Graph g,
					final GraphProperties properties) {
				CompoundEdit capturedEdit = getCurrentCapturedEdit();
				if (capturedEdit == null)
					return;
				UndoableEdit edit = new AbstractUndoableEdit() {

					private static final long serialVersionUID = 1L;

					@Override
					public void undo() throws CannotUndoException {
						g.changeTrainingCost(properties.getTrainingCost());
						g.changeTeamQuality(properties.getTeamQuality());
						super.undo();
					}

					@Override
					public void redo() throws CannotRedoException {
						super.redo();
						g.changeTrainingCost(properties.getTrainingCost());
						g.changeTeamQuality(properties.getTeamQuality());
					}
				};
				capturedEdit.addEdit(edit);
			}

			@Override
			public void repaintGraph() {
				// Nothing to be done here
			}
		};
		graph.addGraphModificationListener(graphModListener);
		this.undoManager.setLimit(HISTORY_SIZE);
	}

	/**
	 * Starts capturing actions on graph
	 */
	public void startCaptureAction() {
		this.currentCapturedEdit = new CompoundEdit();
	}

	/**
	 * Stops capturing actions on graph and adds an entry to history
	 */
	public void stopCaptureAction() {
		if (this.currentCapturedEdit == null)
			return;
		this.currentCapturedEdit.end();
		this.undoManager.addEdit(this.currentCapturedEdit);
		this.currentCapturedEdit = null;
	}

	/**
	 * @return current composed undoable edit
	 */
	private CompoundEdit getCurrentCapturedEdit() {
		return this.currentCapturedEdit;
	}

	/**
	 * @return true if a actions capture is in progress
	 */
	public boolean hasCaptureInProgress() {
		if (this.currentCapturedEdit != null) {
			return true;
		}
		return false;
	}

	/**
	 * Restores previous graph action from the history cursor location
	 */
	public void undo() {
		if (undoManager.canUndo()) {
			undoManager.undo();
		}
	}

	/**
	 * Restores next graph action from the history cursor location
	 */
	public void redo() {
		if (undoManager.canRedo()) {
			undoManager.redo();
		}
	}

	/**
	 * Current composed undoable edit
	 */
	private CompoundEdit currentCapturedEdit;

	/**
	 * Undo/redo manager
	 */
	private UndoManager undoManager = new UndoManager();

	/**
	 * History size limit
	 */
	private static final int HISTORY_SIZE = 50;

}
