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

package uk.ac.sheffield.dcs.smdStudio.framework.swingextension;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * MODIFIED VERSION OF JCollapsiblePane FROM SWINGLABS PROJECT (Apache License,
 * Version 2.0)
 * 
 * <code>JCollapsiblePane</code> provides a component which can collapse or
 * expand its content area with animation and fade in/fade out effects. It also
 * acts as a standard container for other Swing components.
 * 
 * <p>
 * In this example, the <code>JCollapsiblePane</code> is used to build a Search
 * pane which can be shown and hidden on demand.
 * 
 * <pre>
 * &lt;code&gt;
 * JCollapsiblePane cp = new JCollapsiblePane();
 * 
 * // JCollapsiblePane can be used like any other container
 * cp.setLayout(new BorderLayout());
 * 
 * // the Controls panel with a textfield to filter the tree
 * JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
 * controls.add(new JLabel(&quot;Search:&quot;));
 * controls.add(new JTextField(10));
 * controls.add(new JButton(&quot;Refresh&quot;));
 * controls.setBorder(new TitledBorder(&quot;Filters&quot;));
 * cp.add(&quot;Center&quot;, controls);
 * 
 * JFrame frame = new JFrame();
 * frame.setLayout(new BorderLayout());
 * 
 * // Put the &quot;Controls&quot; first
 * frame.add(&quot;North&quot;, cp);
 * 
 * // Then the tree - we assume the Controls would somehow filter the tree
 * JScrollPane scroll = new JScrollPane(new JTree());
 * frame.add(&quot;Center&quot;, scroll);
 * 
 * // Show/hide the &quot;Controls&quot;
 * JButton toggle = new JButton(cp.getActionMap().get(JCollapsiblePane.TOGGLE_ACTION));
 * toggle.setText(&quot;Show/Hide Search Panel&quot;);
 * frame.add(&quot;South&quot;, toggle);
 * 
 * frame.pack();
 * frame.setVisible(true);
 * &lt;/code&gt;
 * </pre>
 * 
 * <p>
 * Note: <code>JCollapsiblePane</code> requires its parent container to have a
 * {@link java.awt.LayoutManager} using {@link #getPreferredSize()} when
 * calculating its layout (example
 * {@link com.l2fprod.common.swing.PercentLayout}, {@link java.awt.BorderLayout}
 * ).
 * 
 * @javabean.attribute name="isContainer" value="Boolean.TRUE" rtexpr="true"
 * 
 * @javabean.attribute name="containerDelegate" value="getContentPane"
 * 
 * @javabean.class name="JCollapsiblePane"
 *                 shortDescription="A pane which hides its content with an animation."
 *                 stopClass="java.awt.Component"
 * 
 * @author rbair (from the JDNC project)
 * @author <a href="mailto:fred@L2FProd.com">Frederic Lavigne</a>
 */
@SuppressWarnings("serial")
public class CollapsiblePane extends JPanel {

	/**
	 * Used when generating PropertyChangeEvents for the "animationState"
	 * property
	 */
	public final static String ANIMATION_STATE_KEY = "animationState";

	/**
	 * The icon used by the "toggle" action when the JCollapsiblePane is
	 * expanded, i.e the icon which indicates the pane can be collapsed.
	 */
	public final static String COLLAPSE_ICON = "collapseIcon";

	/**
	 * The icon used by the "toggle" action when the JCollapsiblePane is
	 * collapsed, i.e the icon which indicates the pane can be expanded.
	 */
	public final static String EXPAND_ICON = "expandIcon";

	/**
	 * Indicates whether the component is collapsed or expanded
	 */
	private boolean collapsed = false;

	/**
	 * Timer used for doing the transparency animation (fade-in)
	 */
	private Timer animateTimer;
	private AnimationListener animator;
	private int currentWidth = -1;
	private WrapperContainer wrapper;
	private boolean useAnimation = true;
	private AnimationParams animationParams;

	/**
	 * Constructs a new JCollapsiblePane with a {@link JPanel} as content pane
	 * and a vertical {@link PercentLayout} with a gap of 2 pixels as layout
	 * manager.
	 */
	public CollapsiblePane() {
		super.setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new PercentLayout(PercentLayout.HORIZONTAL, 2));
		setContentPane(panel);
		setDoubleBuffered(true);
		animator = new AnimationListener();

	}

	public void setBackground(Color bg) {
		super.setBackground(bg);
	}

	/**
	 * Sets the content pane of this JCollapsiblePane. Components must be added
	 * to this content pane, not to the JCollapsiblePane.
	 * 
	 * @param contentPanel
	 * @throws IllegalArgumentException
	 *             if contentPanel is null
	 */
	public void setContentPane(Container contentPanel) {
		if (contentPanel == null) {
			throw new IllegalArgumentException("Content pane can't be null");
		}

		if (wrapper != null) {
			super.remove(wrapper);
		}
		wrapper = new WrapperContainer(contentPanel);
		super.addImpl(wrapper, BorderLayout.CENTER, -1);
	}

	/**
	 * @return the content pane
	 */
	public Container getContentPane() {
		return wrapper.c;
	}

	/**
	 * Overriden to redirect call to the content pane.
	 */
	public void setLayout(LayoutManager mgr) {
		// wrapper can be null when setLayout is called by "super()" constructor
		if (wrapper != null) {
			getContentPane().setLayout(mgr);
		}
	}

	/**
	 * Overriden to redirect call to the content pane.
	 */
	protected void addImpl(Component comp, Object constraints, int index) {
		getContentPane().add(comp, constraints, index);
	}

	/**
	 * Overriden to redirect call to the content pane
	 */
	public void remove(Component comp) {
		getContentPane().remove(comp);
	}

	/**
	 * Overriden to redirect call to the content pane.
	 */
	public void remove(int index) {
		getContentPane().remove(index);
	}

	/**
	 * Overriden to redirect call to the content pane.
	 */
	public void removeAll() {
		getContentPane().removeAll();
	}

	/**
	 * If true, enables the animation when pane is collapsed/expanded. If false,
	 * animation is turned off.
	 * 
	 * <p>
	 * When animated, the <code>JCollapsiblePane</code> will progressively
	 * reduce (when collapsing) or enlarge (when expanding) the height of its
	 * content area until it becomes 0 or until it reaches the preferred height
	 * of the components it contains. The transparency of the content area will
	 * also change during the animation.
	 * 
	 * <p>
	 * If not animated, the <code>JCollapsiblePane</code> will simply hide
	 * (collapsing) or show (expanding) its content area.
	 * 
	 * @param animated
	 * @javabean.property bound="true" preferred="true"
	 */
	public void setAnimated(boolean animated) {
		if (animated != useAnimation) {
			useAnimation = animated;
			firePropertyChange("animated", !useAnimation, useAnimation);
		}
	}

	/**
	 * @return true if the pane is animated, false otherwise
	 * @see #setAnimated(boolean)
	 */
	public boolean isAnimated() {
		return useAnimation;
	}

	/**
	 * @return true if the pane is collapsed, false if expanded
	 */
	public boolean isCollapsed() {
		return collapsed;
	}

	/**
	 * Expands or collapses this <code>JCollapsiblePane</code>.
	 * 
	 * <p>
	 * If the component is collapsed and <code>val</code> is false, then this
	 * call expands the JCollapsiblePane, such that the entire JCollapsiblePane
	 * will be visible. If {@link #isAnimated()} returns true, the expansion
	 * will be accompanied by an animation.
	 * 
	 * <p>
	 * However, if the component is expanded and <code>val</code> is true, then
	 * this call collapses the JCollapsiblePane, such that the entire
	 * JCollapsiblePane will be invisible. If {@link #isAnimated()} returns
	 * true, the collapse will be accompanied by an animation.
	 * 
	 * @see #isAnimated()
	 * @see #setAnimated(boolean)
	 * @javabean.property bound="true" preferred="true"
	 */
	public void setCollapsed() {
		collapsed = !collapsed;
		if (isAnimated()) {
			if (collapsed) {
				setAnimationParams(new AnimationParams(24, Math.max(8, wrapper
						.getWidth() / 24), 1.0f, 0.01f));
				animator.reinit(wrapper.getWidth(), 0);
				animateTimer.start();
			} else {
				setAnimationParams(new AnimationParams(24, Math.max(8,
						getContentPane().getPreferredSize().width / 24), 0.01f,
						1.0f));
				animator.reinit(wrapper.getWidth(), getContentPane()
						.getPreferredSize().width);
				animateTimer.start();
			}
		} else {
			wrapper.c.setVisible(!collapsed);
			invalidate();
			doLayout();
		}
		repaint();
		firePropertyChange("collapsed", !collapsed, collapsed);
	}

	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	/**
	 * The critical part of the animation of this <code>JCollapsiblePane</code>
	 * relies on the calculation of its preferred size. During the animation,
	 * its preferred size (specially its height) will change, when expanding,
	 * from 0 to the preferred size of the content pane, and the reverse when
	 * collapsing.
	 * 
	 * @return this component preferred size
	 */
	public Dimension getPreferredSize() {
		/*
		 * The preferred size is calculated based on the current position of the
		 * component in its animation sequence. If the Component is expanded,
		 * then the preferred size will be the preferred size of the top
		 * component plus the preferred size of the embedded content container.
		 * <p>However, if the scroll up is in any state of animation, the height
		 * component of the preferred size will be the current height of the
		 * component (as contained in the currentHeight variable)
		 */
		Dimension dim;
		if (!isAnimated()) {
			if (getContentPane().isVisible()) {
				dim = getContentPane().getPreferredSize();
			} else {
				dim = super.getPreferredSize();
			}
		} else {
			dim = new Dimension(getContentPane().getPreferredSize());
			if (!getContentPane().isVisible() && currentWidth != -1) {
				dim.width = currentWidth;
			}
		}
		return dim;
	}

	/**
	 * Sets the parameters controlling the animation
	 * 
	 * @param params
	 * @throws IllegalArgumentException
	 *             if params is null
	 */
	private void setAnimationParams(AnimationParams params) {
		if (params == null) {
			throw new IllegalArgumentException("params can't be null");
		}
		if (animateTimer != null) {
			animateTimer.stop();
		}
		animationParams = params;
		animateTimer = new Timer(animationParams.waitTime, animator);
		animateTimer.setInitialDelay(0);
	}

	/**
	 * Tagging interface for containers in a JCollapsiblePane hierarchy who
	 * needs to be revalidated (invalidate/validate/repaint) when the pane is
	 * expanding or collapsing. Usually validating only the parent of the
	 * JCollapsiblePane is enough but there might be cases where the parent
	 * parent must be validated.
	 */
	public static interface JCollapsiblePaneContainer {
		Container getValidatingContainer();
	}

	/**
	 * Parameters controlling the animations
	 */
	private static class AnimationParams {
		final int waitTime;
		final int deltaX;
		final float alphaStart;
		final float alphaEnd;

		/**
		 * @param waitTime
		 *            the amount of time in milliseconds to wait between calls
		 *            to the animation thread
		 * @param deltaX
		 *            the delta in the Y direction to inc/dec the size of the
		 *            scroll up by
		 * @param alphaStart
		 *            the starting alpha transparency level
		 * @param alphaEnd
		 *            the ending alpha transparency level
		 */
		public AnimationParams(int waitTime, int deltaX, float alphaStart,
				float alphaEnd) {
			this.waitTime = waitTime;
			this.deltaX = deltaX;
			this.alphaStart = alphaStart;
			this.alphaEnd = alphaEnd;
		}
	}

	/**
	 * This class actual provides the animation support for scrolling up/down
	 * this component. This listener is called whenever the animateTimer fires
	 * off. It fires off in response to scroll up/down requests. This listener
	 * is responsible for modifying the size of the content container and
	 * causing it to be repainted.
	 * 
	 * @author Richard Bair
	 */
	private final class AnimationListener implements ActionListener {
		/**
		 * Mutex used to ensure that the startHeight/finalHeight are not changed
		 * during a repaint operation.
		 */
		private final Object ANIMATION_MUTEX = "Animation Synchronization Mutex";
		/**
		 * This is the starting width when animating. If > finalWidth, then the
		 * animation is going to be to scroll up the component. If it is < then
		 * finalWidth, then the animation will scroll down the component.
		 */
		private int startWidth = 0;
		/**
		 * This is the final height that the content container is going to be
		 * when scrolling is finished.
		 */
		private int finalWidth = 0;
		/**
		 * The current alpha setting used during "animation" (fade-in/fade-out)
		 */
		private float animateAlpha = 1.0f;

		public void actionPerformed(ActionEvent e) {
			/*
			 * Pre-1) If startHeight == finalHeight, then we're done so stop the
			 * timer 1) Calculate whether we're contracting or expanding. 2)
			 * Calculate the delta (which is either positive or negative,
			 * depending on the results of (1)) 3) Calculate the alpha value 4)
			 * Resize the ContentContainer 5) Revalidate/Repaint the content
			 * container
			 */
			synchronized (ANIMATION_MUTEX) {
				if (startWidth == finalWidth) {
					animateTimer.stop();
					animateAlpha = animationParams.alphaEnd;
					// keep the content pane hidden when it is collapsed, other
					// it may
					// still receive focus.
					if (finalWidth > 0) {
						wrapper.showContent();
						validate();
						CollapsiblePane.this.firePropertyChange(
								ANIMATION_STATE_KEY, null, "expanded");
						return;
					}
				}

				final boolean contracting = startWidth > finalWidth;
				final int delta_x = contracting ? -1 * animationParams.deltaX
						: animationParams.deltaX;
				int newWidth = wrapper.getWidth() + delta_x;
				if (contracting) {
					if (newWidth < finalWidth) {
						newWidth = finalWidth;
					}
				} else {
					if (newWidth > finalWidth) {
						newWidth = finalWidth;
					}
				}
				animateAlpha = (float) newWidth
						/ (float) wrapper.c.getPreferredSize().width;

				Rectangle bounds = wrapper.getBounds();
				int oldWidth = bounds.width;
				bounds.width = newWidth;
				wrapper.setBounds(bounds);
				bounds = getBounds();
				bounds.width = (bounds.width - oldWidth) + newWidth;
				currentWidth = bounds.width;
				setBounds(bounds);
				startWidth = newWidth;

				// it happens the animateAlpha goes over the alphaStart/alphaEnd
				// range
				// this code ensures it stays in bounds. This behavior is seen
				// when
				// component such as JTextComponents are used in the container.
				if (contracting) {
					// alphaStart > animateAlpha > alphaEnd
					if (animateAlpha < animationParams.alphaEnd) {
						animateAlpha = animationParams.alphaEnd;
					}
					if (animateAlpha > animationParams.alphaStart) {
						animateAlpha = animationParams.alphaStart;
					}
				} else {
					// alphaStart < animateAlpha < alphaEnd
					if (animateAlpha > animationParams.alphaEnd) {
						animateAlpha = animationParams.alphaEnd;
					}
					if (animateAlpha < animationParams.alphaStart) {
						animateAlpha = animationParams.alphaStart;
					}
				}
				wrapper.alpha = animateAlpha;

				validate();
			}
		}

		void validate() {
			Container parent = SwingUtilities.getAncestorOfClass(
					JCollapsiblePaneContainer.class, CollapsiblePane.this);
			if (parent != null) {
				parent = ((JCollapsiblePaneContainer) parent)
						.getValidatingContainer();
			} else {
				parent = getParent();
			}

			if (parent != null) {
				if (parent instanceof JComponent) {
					((JComponent) parent).revalidate();
				} else {
					parent.invalidate();
				}
				parent.doLayout();
				parent.repaint();
			}
		}

		/**
		 * Reinitializes the timer for scrolling up/down the component. This
		 * method is properly synchronized, so you may make this call regardless
		 * of whether the timer is currently executing or not.
		 * 
		 * @param startWidth
		 * @param stopWidth
		 */
		public void reinit(int startWidth, int stopWidth) {
			synchronized (ANIMATION_MUTEX) {
				CollapsiblePane.this.firePropertyChange(ANIMATION_STATE_KEY,
						null, "reinit");
				this.startWidth = startWidth;
				this.finalWidth = stopWidth;
				animateAlpha = animationParams.alphaStart;
				currentWidth = -1;
				wrapper.showImage();
			}
		}
	}

	private final class WrapperContainer extends JPanel {
		private BufferedImage img;
		private Container c;
		float alpha = 1.0f;

		public WrapperContainer(Container c) {
			super(new BorderLayout());
			this.c = c;
			add(c, BorderLayout.CENTER);

			// we must ensure the container is opaque. It is not opaque it
			// introduces
			// painting glitches specially on Linux with JDK 1.5 and GTK look
			// and feel.
			// GTK look and feel calls setOpaque(false)
			if (c instanceof JComponent && !((JComponent) c).isOpaque()) {
				((JComponent) c).setOpaque(true);
			}
			setOpaque(false);
			setDoubleBuffered(true);
		}

		public void showImage() {
			// render c into the img
			makeImage();
			c.setVisible(false);
		}

		public void showContent() {
			currentWidth = -1;
			c.setVisible(true);
		}

		void makeImage() {
			// if we have no image or if the image has changed
			if (getGraphicsConfiguration() != null && getWidth() > 0) {
				Dimension dim = c.getPreferredSize();
				// width and height must be > 0 to be able to create an image
				if (dim.width > 0) {
					img = getGraphicsConfiguration().createCompatibleImage(
							dim.width, getHeight());

					c.setSize(dim.width, getHeight());
					c.paint(img.getGraphics());
				} else {
					img = null;
				}
			}
		}

		public void paintComponent(Graphics g) {
			if (!useAnimation || c.isVisible()) {
				super.paintComponent(g);
			} else {
				// within netbeans, it happens we arrive here and the image has
				// not been
				// created yet. We ensure it is.
				if (img == null) {
					makeImage();
				}
				// and we paint it only if it has been created and only if we
				// have a
				// valid graphics
				if (g != null && img != null && img.getWidth() >= getWidth()
						&& img.getHeight() >= getHeight()) {
					// draw the image with y being width - imageWidth
					g.drawImage(img, 0, 0, null);
				}
			}
		}

	}
}
