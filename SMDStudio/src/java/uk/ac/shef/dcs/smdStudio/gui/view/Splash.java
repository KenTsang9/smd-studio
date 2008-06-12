package uk.ac.shef.dcs.smdStudio.gui.view;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * <p>
 * Title: Splash Screen
 * </p>
 * 
 * <p>
 * Description: Showing the splash screen
 * </p>
 * 
 * 
 * @author <a href=mailto:a.dorri@genesys.shef.ac.uk>Aria Dorri</a>
 * @version 1.0
 */
@SuppressWarnings("serial")
public class Splash extends JDialog {
	/**
	 * Initializes the {@link Splash} objects that is shown while the program is
	 * loading
	 */
	public Splash() {
		JPanel panel = new JPanel(new BorderLayout());
		setUndecorated(true);
		JLabel label = new JLabel(new ImageIcon("data/splash.jpg"));
		panel.setBorder(BorderFactory.createLoweredBevelBorder());
		panel.add(label);
		getContentPane().add(panel);
		setResizable(false);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
}
