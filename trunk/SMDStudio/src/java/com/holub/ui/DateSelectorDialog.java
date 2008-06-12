package com.holub.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

/**
 * The DateSelectorDialog, shown below, combines a {@link DateSelector} and a
 * {@link PopupDialog} to provide a standalone, popup dialog for choosing dates.
 * The dialog is a free-floating top-level window. You can drag it around by the
 * title bar and close it by clicking on the "close" icon.
 * <p>
 * The class does implement the {@link DateSelector} interface, but bear in mind
 * that the window closes when the user selects a date. Unlike the
 * {@link TitledDateSelector} wrapper class, both of the action events are sent
 * to listeners, however. Create one the hard way like this:
 * 
 * <pre>
 * DateSelector calendar = new DateSelectorPanel(selector);
 * calendar = new NavigableDateSelector(calendar); // add navigation
 * DateSelectorDialog chooser = new DateSelectorDialog(parent_frame, calendar);
 * //...
 * Date d = chooser.select(); // Pops up chooser; returns selected Date.
 * </pre>
 * 
 * You can leave out the navigation bar by omitting the second line of the
 * previous moduleple. The following convenience constructor has exactly the same
 * effect as the earlier code:
 * 
 * <pre>
 *  DateSelectorDialog chooser = new DateSelectorDialog(parent_frame);
 * &lt;pre&gt;
 * You can also pop up the dialog like this:
 * &lt;pre&gt;
 *  chooser.setVisible(true);		// blocks until dialog closed
 *  Date d = chooser.getSelectedDate();
 * &lt;/pre&gt;
 * This class is a stand-alone dialog. For a version
 * that you can embed into another window, see {@link DateSelectorPanel}.
 * 
 * @see DateSelector
 * @see DateSelectorPanel
 * @see NavigableDateSelector
 * @see TitledDateSelector
 * @see PopupDialog
 * 
 */

@SuppressWarnings("serial")
public class DateSelectorDialog extends PopupDialog implements DateSelector {
	private DateSelector selector = new DateSelectorPanel();

	/**
	 * Creates a dialog box with the indicated parent that holds a standard
	 * {@link DateSelectorPanel DateSelectorPanel} (as created using the
	 * no-arg constructor).
	 * 
	 * @param parent
	 *            Frame
	 */
	public DateSelectorDialog(Frame parent) {
		super(parent);
		selector = new NavigableDateSelector(new DateSelectorPanel());
		init(parent);
	}

	/*
	 * Like {@link #DateSelectorDialog(Frame), but for a {@link Dialog} parent.
	 */
	public DateSelectorDialog(Dialog parent) {
		super(parent);
		selector = new NavigableDateSelector(new DateSelectorPanel());
		init(parent);
	}

	/**
	 * Creates a dialog box with the indicated parent that holds the indicated
	 * DateSelector. Note that the current month and year is displayed in the
	 * dialog-box title bar, so there's no need to display it in the selector
	 * too.
	 * 
	 * @param parent
	 *            Frame
	 * @param to_wrap
	 *            DateSelector
	 */
	public DateSelectorDialog(Frame parent, DateSelector to_wrap) {
		super(parent);
		selector = to_wrap;
		init(parent);
	}

	/*
	 * Like {@link #DateSelectorDialog(Frame,DateSelector), but for a
	 * {@link Dialog} parent.
	 */

	public DateSelectorDialog(Dialog parent, DateSelector to_wrap) {
		super(parent);
		selector = to_wrap;
		init(parent);
	}

	/**
	 * Code comon to all constructors
	 * 
	 * @param parent
	 *            Component
	 */
	private void init(Component parent) {

		getContentPane().add((Container) selector, BorderLayout.CENTER);
		selector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (event.getID() == DateSelector.CHANGE_ACTION) {
					setTitle(event.getActionCommand());
				} else {
					setVisible(false);
					dispose();
				}
			}
		});
		setLocationRelativeTo(parent);
		((Container) selector).setVisible(true);
		pack();
	}

	/**
	 * For use when you pop up a dialog using <code>setVisible(true)</code>
	 * rather than {@link #select}.
	 * 
	 * @return the selected date or null if the dialog was closed without
	 *         selecting anything.
	 */
	public Date getSelectedDate() {
		return selector.getSelectedDate();
	}

	/**
	 * Get the current date. The dialog stays in existance until the user closes
	 * it or selects a date, so this method can be used to see what month the
	 * user has scrolled to.
	 * 
	 * @return the date currently displayed on the calendar.
	 */
	public Date getCurrentDate() {
		return selector.getCurrentDate();
	}

	/**
	 * Add an action listner for both {@link DateSelector#CHANGE_ACTION} and
	 * {@link DateSelector#SELECT_ACTION} action events.
	 * 
	 * @param l
	 *            ActionListener
	 */
	public void addActionListener(ActionListener l) {
		selector.addActionListener(l);
	}

	/**
	 * Remove a previously-added listener
	 * 
	 * @param l
	 *            ActionListener
	 */
	public void removeActionListener(ActionListener l) {
		selector.removeActionListener(l);
	}

	/**
	 * Pops up the chooser and blocks until the user selects a date.
	 * 
	 * @return the selected date or null if the dialog was closed without
	 *         selecting anything.
	 */
	public Date select() {
		setVisible(true);
		return selector.getSelectedDate();
	}

	public void roll(int f, boolean up) {
		selector.roll(f, up);
	}

	public int get(int f) {
		return selector.get(f);
	}
}
