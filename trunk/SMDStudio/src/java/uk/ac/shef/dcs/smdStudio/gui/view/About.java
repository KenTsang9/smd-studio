package uk.ac.shef.dcs.smdStudio.gui.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * <p>
 * Title: About
 * </p>
 * 
 * <p>
 * Description: About Page
 * </p>
 * 
 * @author <a href=mailto:a.dorri@genesys.shef.ac.uk>Aria Dorri</a>
 * @version 1.0
 */
@SuppressWarnings("serial")
public class About extends JDialog {
	/**
	 * Initializes the about dialog
	 * 
	 * @param parent
	 *            Main
	 */
	public About(Main parent) {
		super(parent, true);
		setUndecorated(true);
		add(new AboutPanel());
		setSize(600, 400);
		center();
		setVisible(true);
	}

	/**
	 * Sets the position of the dialog to the center of parent frame
	 */
	public void center() {
		Dimension parentDim = ((JFrame) getParent()).getSize();
		setLocation(
				(int) ((parentDim.width - (int) (getSize().getWidth())) / 2 + ((JFrame) getParent())
						.getLocation().getX()),
				(int) ((parentDim.height - (int) (getSize().getHeight())) / 2 + ((JFrame) getParent())
						.getLocation().getY()));
	}

	/**
	 * <p>
	 * Title: About Panel
	 * </p>
	 * 
	 * <p>
	 * Description: Panel which shows information about the prgram
	 * </p>
	 * 
	 * 
	 * 
	 * @author <a href=mailto:a.dorri@genesys.shef.ac.uk>Aria Dorri</a>
	 * @version 1.0.2
	 */
	class AboutPanel extends JPanel {
		/**
		 * Initioalizes the {@link AboutPanel}
		 */
		AboutPanel() {
			addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					dispose();
				}
			});
		}

		/**
		 * Prints the required information on the panel about the program
		 * 
		 * @param g
		 *            Graphics
		 */
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g
					.drawImage(new ImageIcon("data/splash.jpg").getImage(), 0,
							0, this);
			g.setColor(Color.BLACK);
			g.setFont(new Font("Sans Sarif", Font.BOLD + Font.ITALIC, 9));
			g.drawString(
					"This program contains Allen Holub's Zip-archive utility. "
							+ "(c) 2003 Allen I. Holub. All Rights Reserved.",
					10, 380);
			g.setFont(new Font("Sans Sarif", Font.BOLD + Font.ITALIC, 12));
			g.drawString("Version 1.0", 10, 360);
			g.drawString("Copyright 2007 Genesys Solutions", 350, 360);
		}
	}

}
