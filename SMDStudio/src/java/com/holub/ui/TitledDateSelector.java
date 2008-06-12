package com.holub.ui;

import java.awt.AWTEventMulticaster;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/*******************************************************************************
 * This class is a GoF "Decorator" that augements the "raw" </code>DateSelectorPanel</code>
 * with a title that displays the month name and year. The title updates
 * automatically as the user navigates. Create a titled date selector like this:
 * 
 * <pre>
 * DateSelector selector = new DateSelectorPanel(); // or other constructor.
 * selector = new TitledDateSelector(selector);
 * </pre>
 * 
 * This wrapper absorbs the {@link DateSelector#CHANGE_ACTION} events: listeners
 * that you register on the wrapper will be sent only
 * {@link DateSelector#SELECT_ACTION} events. (Listeners that are registered on
 * the wrapped <code>DateSelector</code> object will be notified of all
 * events, however.
 * 
 * @see DateSelector
 * @see DateSelectorPanel
 * @see DateSelectorDialog
 * @see NavigableDateSelector
 */
@SuppressWarnings("serial")
public class TitledDateSelector extends JPanel implements DateSelector {
	private DateSelector selector;

	private final JLabel title = new JLabel("XXXX");

	/**
	 * Wrap an existing DateSelector to add a title bar showing the displayed
	 * month and year. The title changes as the user navigates.
	 */

	public TitledDateSelector(DateSelector selector) {
		this.selector = selector;

		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setOpaque(true);
		title.setBackground(Colors.LIGHT_YELLOW);
		title.setFont(title.getFont().deriveFont(Font.BOLD));

		selector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getID() == DateSelectorPanel.CHANGE_ACTION)
					title.setText(e.getActionCommand());
				else
					mySubscribers.actionPerformed(e);
			}
		});

		setOpaque(false);
		setLayout(new BorderLayout());
		add(title, BorderLayout.NORTH);
		add((JPanel) selector, BorderLayout.CENTER);
	}

	/**
	 * This constructor lets you specify the background color of the title strip
	 * that holds the month name and year (the default is light yellow).
	 * 
	 * @param labelBackgroundColor
	 *            the color of the title bar, or null to make it transparent.
	 */
	public TitledDateSelector(DateSelector selector, Color labelBackgroundColor) {
		this(selector);
		if (labelBackgroundColor == null)
			title.setOpaque(false);
		else
			title.setBackground(labelBackgroundColor);
	}

	private ActionListener mySubscribers = null;

	public synchronized void addActionListener(ActionListener l) {
		mySubscribers = AWTEventMulticaster.add(mySubscribers, l);
	}

	public synchronized void removeActionListener(ActionListener l) {
		mySubscribers = AWTEventMulticaster.remove(mySubscribers, l);
	}

	public Date getSelectedDate() {
		return selector.getSelectedDate();
	}

	public Date getCurrentDate() {
		return selector.getCurrentDate();
	}

	public void roll(int f, boolean up) {
		selector.roll(f, up);
	}

	public int get(int f) {
		return selector.get(f);
	}
}
