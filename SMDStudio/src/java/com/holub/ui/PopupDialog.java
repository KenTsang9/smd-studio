package com.holub.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

// Test this class by instantiating DateSelector
// TODO: Draw the close box in a paint() override using lines so that
// 		 the dependancy on the image file goes away and so that
// 		 it can change colors to match the foreground color.

/**
 * A PopupDialog is a clean, lightweight, "modal" window intended for simple
 * pop-up user-interface widgets. The frame, as shown at right, is a
 * single-pixel-wide line; the title bar holds only the title text and a small
 * "close-window" icon. The dialog box can be dragged around on the screen by
 * grabbing the title bar (and closed by clicking on the icon), but the user
 * can't resize it, minimize it, etc. (Your program can do so, of course).
 * <p>
 * The "close" icon in the current implementation is an image loaded as a
 * "resource" from the CLASSPATH. If the class can't find the image file, it
 * uses the character "X" instead. The main problem with this approach is that
 * you can't change the color of the close icon to math the title-bar colors.
 * Future versions of this class will fix the problem by rendering the image
 * internally.
 */

@SuppressWarnings("serial")
public class PopupDialog extends JDialog {
	private Color TITLE_BAR_COLOR = Colors.LIGHT_YELLOW;

	private JLabel title = new JLabel("xxxxxxxxxxxxxx");
	{
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setOpaque(false);
		title.setFont(title.getFont().deriveFont(Font.BOLD));
	}

	private JPanel header = new JPanel();
	{
		header.setBackground(TITLE_BAR_COLOR);
		header.setLayout(new BorderLayout());
		header.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		header.add(title, BorderLayout.CENTER);
		// header.add( createCloseButton() , BorderLayout.EAST );
	}

	private JPanel contentPane = new JPanel();
	{
		contentPane.setLayout(new BorderLayout());
	}

	public PopupDialog(Frame owner) {
		super(owner);
		setModal(true);
	}

	public PopupDialog(Dialog owner) {
		super(owner);
		setModal(true);
	}

	/* code common to all constructors */
	{
		initDragable();

		setUndecorated(true);
		JPanel contents = new JPanel();
		contents.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		contents.setLayout(new BorderLayout());
		contents.add(header, BorderLayout.NORTH);
		contents.add(contentPane, BorderLayout.CENTER);
		contents.setBackground(Color.WHITE);

		setContentPane(contents); // , BorderLayout.CENTER );
		setLocation(100, 100);
	}

	/** Set the dialog title to the indicated text */
	public void setTitle(String text) {
		title.setText(text);
	}

	// ----------------------------------------------------------------------
	// Drag support.
	//
	private Point referencePosition = new Point(0, 0);

	private MouseMotionListener movement_handler;

	private MouseListener click_handler;

	private void initDragable() {
		movement_handler = new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) { // The reference
				// posistion is the
				// (window relative)
				// cursor postion when the click occured. The
				// current_mouse-position is mouse position
				// now, and the deltas represent the disance
				// moved.

				Point current_mousePosition = e.getPoint();
				Point current_window_location = getLocation();

				int delta_x = current_mousePosition.x - referencePosition.x;
				int delta_y = current_mousePosition.y - referencePosition.y;

				// Move the window over by the computed delta. This move
				// effectivly shifts the window-relative current-mouse
				// position back to the original reference position.

				current_window_location.translate(delta_x, delta_y);
				setLocation(current_window_location);
			}
		};

		click_handler = new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				referencePosition = e.getPoint(); // start of the drag
			}
		};

		setDragable(true);
	}

	/**
	 * Turn dragability on or off.
	 */
	public void setDragable(boolean on) {
		if (on) {
			title.addMouseMotionListener(movement_handler);
			title.addMouseListener(click_handler);
		} else {
			title.removeMouseMotionListener(movement_handler);
			title.removeMouseListener(click_handler);
		}
	}

	/**
	 * Add your widgets to the window returned by this method, in a manner
	 * similar to a JFrame. Do not modify the PoupDialog itself. The returned
	 * container is a {@link JPanel JPanel} with a preinstalled
	 * {@link BorderLayout}. By default, it's colored colored dialog-box gray.
	 * 
	 * @return the content pane.
	 */
	public Container getContentPane() {
		return contentPane;
	}

	/**
	 * Change the color of the text and background in the title bar. The "close"
	 * icon is always {@linkplain Colors#DARK_RED dark red} so it
	 * will be hard to see if the background color is also a dark red).
	 * 
	 * @param foreground
	 *            the text color
	 * @param background
	 *            the background color
	 */
	public void change_titlebarColors(Color foreground, Color background) {
		title.setForeground(foreground);
		header.setBackground(background);
	}
}
