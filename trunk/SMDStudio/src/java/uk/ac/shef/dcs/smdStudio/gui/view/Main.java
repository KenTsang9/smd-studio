package uk.ac.shef.dcs.smdStudio.gui.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;

import uk.ac.shef.dcs.smdStudio.gui.controller.MainController;

/**
 * <p>
 * Title: Main
 * </p>
 * 
 * <p>
 * Description: The Main Programs window
 * </p>
 * 
 * @author <a href=mailto:a.dorri@genesys.shef.ac.uk>Aria Dorri</a>
 * @version 1.0.15
 */
@SuppressWarnings("serial")
public class Main extends JFrame {
	/**
	 * margins
	 */
	static final Insets margins = new Insets(0, 0, 0, 0);

	/**
	 * Handels the Events on the menu Items and toolbar buttons
	 */
	public MainController mainController;

	/**
	 * Th place that the Quiz frames will appear
	 */
	public JDesktopPane mainPane;

	/**
	 * Menu Bar
	 */
	JMenuBar menuBar;

	/**
	 * File Menu
	 */
	JMenu file;

	/**
	 * File -> New
	 */
	JMenuItem neew;

	/**
	 * File -> Open
	 */
	JMenuItem open;

	/**
	 * File -> Save
	 */
	JMenuItem save;

	/**
	 * File -> Save As
	 */
	JMenuItem saveAs;

	/**
	 * File -> Print
	 */
	JMenuItem print;

	/**
	 * File -> Print Preview
	 */
	JMenuItem printPreview;

	/**
	 * File -> Exit
	 */
	JMenuItem exit;

	/**
	 * Insert Menu
	 */
	JMenu insert;

	/**
	 * Insert -> Question
	 */
	JMenu question;

	/**
	 * Insert -> Question -> Multiple Choice
	 */
	JMenuItem multipleChoice;

	/**
	 * Help Menu
	 */
	JMenu help;

	/**
	 * Help -> About
	 */
	JMenuItem about;

	/**
	 * Tool Bar
	 */
	JToolBar toolBar;

	/**
	 * Tool Bar's New Button
	 */
	JButton newButton;

	/**
	 * Tool Bar's Open Button
	 */
	JButton openButton;

	/**
	 * Tool Bar's Save Button
	 */
	JButton saveButton;

	/**
	 * Tool Bar's Multiple Choice Button
	 */
	JButton insertMultipleChoice;

	/**
	 * Initializes an instance of the {@link Main} class and opens the Quiz in
	 * the path passed as a parameter
	 * 
	 * @param path
	 *            Path of the Quiz that should be opend
	 */
	public Main(String path) {
		setTitle("Quiz Studio");
		mainController = new MainController(this);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(mainController);

		mainPane = new JDesktopPane();
		mainPane.setBorder(BorderFactory.createLoweredBevelBorder());

		initMenu();
		initToolBar();

		JPanel mainPanel = initMainPanel();
		getContentPane().add(mainPanel, BorderLayout.CENTER);

		// setMinimumSize(new Dimension(500, 500));
		lockInMinSize();
		setExtendedState(Frame.MAXIMIZED_BOTH);
		if (path != null) {
			File file = new File(path);
			if (file.exists() && file.isFile() && file.canRead()) {
				open(file.getAbsolutePath());

			}

		}

	}

	/**
	 * Initializes and returns the main panel.
	 * 
	 * @return The main panel.
	 */
	private JPanel initMainPanel() {
		JPanel topPanel = new JPanel(new BorderLayout());
		JPanel toolPanel = new JPanel(new BorderLayout());
		topPanel.setBorder(BorderFactory.createLineBorder(Color.darkGray));
		toolPanel.setBorder(BorderFactory.createLineBorder(Color.darkGray));
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(new CompoundBorder(new EmptyBorder(2, 2, 2, 2),
				new SoftBevelBorder(SoftBevelBorder.LOWERED)));
		mainPanel.add(topPanel, BorderLayout.NORTH);
		mainPanel.add(toolPanel, BorderLayout.CENTER);
		topPanel.add(menuBar, BorderLayout.CENTER);
		topPanel.add(toolBar, BorderLayout.SOUTH);
		toolPanel.add(mainPane, BorderLayout.CENTER);
		return mainPanel;
	}

	/**
	 * Initializes the tool bar.
	 * 
	 */
	private void initToolBar() {
		toolBar = new JToolBar(JToolBar.HORIZONTAL);

		initNewButton();
		initOpenButton();
		initSaveButton();
		initMultipleChoiceButton();

		toolBar.add(newButton);
		toolBar.add(openButton);
		toolBar.add(saveButton);
		toolBar.addSeparator();
		toolBar.add(insertMultipleChoice);
		toolBar.setBorder(BorderFactory.createCompoundBorder());
	}

	/**
	 * Initializes the insert multiple choice tool bar button.
	 * 
	 */
	private void initMultipleChoiceButton() {
		insertMultipleChoice = new JButton(new ImageIcon("data/choice.gif"));
		insertMultipleChoice.setToolTipText("Multiple Choice");
		insertMultipleChoice.addActionListener(mainController);
		insertMultipleChoice.setActionCommand(String
				.valueOf(MainController.MULTIPLE_CHOICE));
		insertMultipleChoice.setMargin(margins);
	}

	/**
	 * Initializes the save button.
	 * 
	 */
	private void initSaveButton() {
		saveButton = new JButton(new ImageIcon("data/save.gif"));
		saveButton.setMargin(margins);
		saveButton.setActionCommand(String.valueOf(MainController.SAVE));
		saveButton.setToolTipText("Save");
		saveButton.addActionListener(mainController);
	}

	/**
	 * initializes the Open button.
	 * 
	 */
	private void initOpenButton() {
		openButton = new JButton(new ImageIcon("data/open.gif"));
		openButton.setMargin(margins);
		openButton.setActionCommand(String.valueOf(MainController.OPEN));
		openButton.setToolTipText("Open");
		openButton.addActionListener(mainController);
	}

	/**
	 * Initializes the New button.
	 * 
	 */
	private void initNewButton() {
		newButton = new JButton(new ImageIcon("data/new.gif"));
		newButton.setMargin(margins);
		newButton.setToolTipText("New");
		newButton.setActionCommand(String.valueOf(MainController.NEW));
		newButton.addActionListener(mainController);
	}

	/**
	 * Initializes the menu for the window.
	 * 
	 */
	private void initMenu() {

		menuBar = new JMenuBar();

		initFile();

		insert = new JMenu("Insert");
		insert.setMnemonic('I');
		question = new JMenu("Question");
		multipleChoice = new JMenuItem("MultipleChoice", 'M');
		multipleChoice.setActionCommand(String
				.valueOf(MainController.MULTIPLE_CHOICE));
		multipleChoice.setIcon(new ImageIcon("data/choice16.gif"));
		multipleChoice.addActionListener(mainController);
		help = new JMenu("Help");
		about = new JMenuItem("About", 'A');
		about.setActionCommand(String.valueOf(MainController.ABOUT));
		about.addActionListener(mainController);
		menuBar.add(insert);
		insert.add(question);
		question.add(multipleChoice);
		menuBar.add(help);
		help.add(about);
	}

	/**
	 * Initializes the File menu.
	 * 
	 */
	private void initFile() {
		file = new JMenu("File");
		file.setMnemonic('F');

		initNewMenuItem();
		initOpenMenuItem();
		initSaveMenuItem();
		initSaveAsMenuItem();
		initPrintMeniItem();
		initPrintPreviewMenuItem();
		initExitMenuItem();

		menuBar.add(file);
		file.add(neew);
		file.add(open);
		file.add(save);
		file.add(saveAs);
		file.addSeparator();
		file.add(print);
		file.add(printPreview);
		file.addSeparator();
		file.add(exit);
	}

	/**
	 * initializes the Ecit menu item.
	 * 
	 */
	private void initExitMenuItem() {
		exit = new JMenuItem("Exit", 'X');
		exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
				InputEvent.CTRL_MASK));
		exit.setActionCommand(String.valueOf(MainController.EXIT));
		exit.addActionListener(mainController);
	}

	/**
	 * Initializes the Print Preview menu item.
	 * 
	 */
	private void initPrintPreviewMenuItem() {
		printPreview = new JMenuItem("Print Preview", 'V');
		printPreview.setIcon(new ImageIcon("data/printPreview.gif"));
		printPreview.setActionCommand(String
				.valueOf(MainController.PRINT_PREVIEW));
		printPreview.addActionListener(mainController);
	}

	/**
	 * Initializes the print menu item.
	 * 
	 */
	private void initPrintMeniItem() {
		print = new JMenuItem("Print", 'P');
		print.setIcon(new ImageIcon("data/print16.gif"));
		print.setActionCommand(String.valueOf(MainController.PRINT));
		print.addActionListener(mainController);
		print.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,
				InputEvent.CTRL_MASK));
	}

	/**
	 * Initializes the saveAs menu item.
	 * 
	 */
	private void initSaveAsMenuItem() {
		saveAs = new JMenuItem("Save As", 'A');
		saveAs.setIcon(new ImageIcon("data/SaveAs16.gif"));
		saveAs.setActionCommand(String.valueOf(MainController.SAVEAS));
		saveAs.addActionListener(mainController);
	}

	/**
	 * initializes the save menu item.
	 * 
	 */
	private void initSaveMenuItem() {
		save = new JMenuItem("Save", 'S');
		save.setIcon(new ImageIcon("data/save16.gif"));
		save.setActionCommand(String.valueOf(MainController.SAVE));
		save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				InputEvent.CTRL_MASK));
		save.addActionListener(mainController);
	}

	/**
	 * Initializes the Open menu item.
	 * 
	 */
	private void initOpenMenuItem() {
		open = new JMenuItem("Open", 'O');
		open.setIcon(new ImageIcon("data/open16.gif"));
		open.setActionCommand(String.valueOf(MainController.OPEN));
		open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				InputEvent.CTRL_MASK));
		open.addActionListener(mainController);
	}

	/**
	 * Initializes the new menu item.
	 * 
	 */
	private void initNewMenuItem() {
		neew = new JMenuItem("New", 'N');
		neew.setIcon(new ImageIcon("data/new16.gif"));
		neew.addActionListener(mainController);
		neew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
				InputEvent.CTRL_MASK));
		neew.setActionCommand(String.valueOf(MainController.NEW));
	}

	private void lockInMinSize() {
		// Ensures user cannot resize frame to be smaller than frame is right
		// now.
		final int origX = 640;
		final int origY = 480;
		this.addComponentListener(new java.awt.event.ComponentAdapter() {
			public void componentResized(ComponentEvent event) {
				Main.this.setSize((Main.this.getWidth() < origX) ? origX
						: Main.this.getWidth(),
						(Main.this.getHeight() < origY) ? origY : Main.this
								.getHeight());
			}
		});
	}

	/**
	 * The main method that runs the application
	 * 
	 * @param args
	 *            String[]
	 */
	public static void main(String[] args) {
		Splash splash = new Splash();
		try {
			// UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			Main main;
			if (args.length > 0) {
				main = new Main(args[0]);
			} else {
				main = new Main(null);
			}
			splash.dispose();
			main.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,
					"An un expected error occured, the program terminates!",
					"Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}

	/**
	 * Method that opens the file in the path that has been passed as parameter
	 * 
	 * @param path
	 *            Path of the file
	 */
	void open(String path) {
		Project project = new Project(path, this);
		project.setQuiz(path);
		mainPane.add(project);
		try {
			project.pack();
			project.setMaximum(true);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		project.setVisible(true);
		project.moveToFront();
	}

}
