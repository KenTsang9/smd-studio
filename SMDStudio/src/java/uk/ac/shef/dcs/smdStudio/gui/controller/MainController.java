package uk.ac.shef.dcs.smdStudio.gui.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import uk.ac.shef.dcs.smdStudio.gui.view.About;
import uk.ac.shef.dcs.smdStudio.gui.view.Main;
import uk.ac.shef.dcs.smdStudio.gui.view.NewMultiple;
import uk.ac.shef.dcs.smdStudio.gui.view.NewQuiz;
import uk.ac.shef.dcs.smdStudio.gui.view.PrintPreview;
import uk.ac.shef.dcs.smdStudio.gui.view.Project;
import uk.ac.shef.dcs.smdStudio.util.FileFilterEx;

/**
 * <p>
 * Title: Menu Listener
 * </p>
 * 
 * <p>
 * Description: Handles events generated in the main program window
 * </p>
 * 
 * @author <a href=mailto:a.dorri@genesys.shef.ac.uk>Aria Dorri</a>
 * @version 1.0.23
 */
public class MainController extends WindowAdapter implements ActionListener {
	/**
	 * actionCommand for New button and menu item
	 */
	public final static int NEW = 0;

	/**
	 * actionCommand for Open button and menu item
	 */
	public final static int OPEN = 2;

	/**
	 * actionCommand for Save button and menu item
	 */
	public final static int SAVE = 3;

	/**
	 * actionCommand for Save As menu item
	 */
	public final static int SAVEAS = 4;

	/**
	 * actionCommand for Print menu item
	 */
	public final static int PRINT = 5;

	/**
	 * actionCommand for Exit menu item
	 */
	public final static int EXIT = 6;

	/**
	 * actionCommand for Multiple Choice button and menu item
	 */
	public final static int MULTIPLE_CHOICE = 9;

	/**
	 * actionCommand for About menu item
	 */
	public final static int ABOUT = 11;

	/**
	 * actionCommand for Print Preview menu item
	 */
	public final static int PRINT_PREVIEW = 12;

	/**
	 * A refrence to the Main program window
	 */
	public Main frame;

	/**
	 * panel
	 */
	public JPanel panel;

	/**
	 * chooser
	 */
	public JFileChooser chooser;

	public MainController(Main frame) {
		this.frame = frame;
		initFileChooser();
	}

	/**
	 * This method initializes the file chooser that is used within the program.
	 * 
	 */
	private void initFileChooser() {
		chooser = new JFileChooser();
		FileFilterEx filter = new FileFilterEx();
		filter.addExtension("exs");
		filter.setDescription("Online Quiz");
		chooser.setFileFilter(filter);
	}

	public void actionPerformed(ActionEvent evt) {
		switch (Integer.parseInt(evt.getActionCommand())) {
		case NEW:
			doNew();
			break;
		case OPEN:
			doOpen();
			break;
		case SAVE:
			getSelectedProject().doSave();
			break;
		case SAVEAS:
			getSelectedProject().doSaveAs();
			break;
		case PRINT:
			doPrint();
			break;
		case PRINT_PREVIEW:
			doPrintPreview();
			break;
		case EXIT:
			doExit();
			break;
		case MULTIPLE_CHOICE:
			doMultipleChoice();
			break;
		case ABOUT: {
			new About(frame);
		}
			break;
		}
	}

	/**
	 * Returns the selected Project
	 * 
	 * @return the selected project.
	 */
	private Project getSelectedProject() {
		return (Project) (((Main) frame).mainPane.getSelectedFrame());
	}

	/**
	 * Confirms the Exit and savings, and terminates the application.
	 * 
	 */
	private void doExit() {
		JInternalFrame[] allFrames = ((Main) frame).mainPane.getAllFrames();
		if (allFrames.length != 0) {
			for (JInternalFrame project : allFrames) {
				int iOption = JOptionPane.showConfirmDialog(frame,
						"Do you want to save changes to \""
								+ project.getTitle() + "\" before exit?",
						"Save", JOptionPane.YES_NO_CANCEL_OPTION);
				switch (iOption) {
				case JOptionPane.YES_OPTION:
					((Project) project).doSave();
					System.exit(0);
					break;
				case JOptionPane.NO_OPTION:
					System.exit(0);
					break;
				case JOptionPane.CANCEL_OPTION:// do nothing
					break;
				}
			}
		} else {
			System.exit(0);
		}
	}

	/**
	 * This method initializes a new multiple choice qiestion.
	 * 
	 */
	private void doMultipleChoice() {
		if (frame.mainPane.getSelectedFrame() != null) {
			new NewMultiple((Project) frame.mainPane.getSelectedFrame());
		}
	}

	/**
	 * This method showa a preview of how the document is going to be printed.
	 * 
	 */
	private void doPrintPreview() {
		Project project = (Project) frame.mainPane.getSelectedFrame();
		if (project != null) {
			PrintPreview pp = new PrintPreview(project);
			pp.setVisible(true);
		}
	}

	/**
	 * This method is called when the user wants to print the quiz.
	 * 
	 */
	private void doPrint() {
		Project project = (Project) frame.mainPane.getSelectedFrame();
		if (project != null) {
			try {
				project.nextQuestion = 0;
				PrinterJob printJob = PrinterJob.getPrinterJob();
				PrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
				printJob.setPrintable(project);
				printJob.setJobName(project.smd.getCourseCode() + " "
						+ project.smd.getName());
				if (printJob.printDialog(attr)) {
					printJob.print(attr);
				}

			} catch (PrinterException ex) {
				JOptionPane.showMessageDialog(frame,
						"Error: Unable to print the document!", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * This method does the openning action.
	 * 
	 */
	private void doOpen() {
		int ret;
		File file;

		ret = chooser.showOpenDialog(frame);
		while (ret == JFileChooser.APPROVE_OPTION) {
			file = chooser.getSelectedFile();
			if (file.exists() && file.isFile() && file.canRead()) {
				open(file.getAbsolutePath());
				break;
			}
			ret = chooser.showOpenDialog(frame);
		}
	}

	/**
	 * This method starts a new quiz.
	 * 
	 */
	private void doNew() {
		NewQuiz newQuiz = new NewQuiz(frame);
		newQuiz.setVisible(true);
	}

	/**
	 * This method opens a file in the given path.
	 * 
	 * @param path
	 */
	private void open(String path) {
		Project project = new Project(path, frame);
		project.setQuiz(path);
		((Main) frame).mainPane.add(project);
		try {
			project.pack();
			project.setMaximum(true);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		project.setVisible(true);
		project.moveToFront();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
	 */
	public void windowClosing(WindowEvent e) {
		doExit();
	}

}
