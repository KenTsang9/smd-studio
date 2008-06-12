package uk.ac.shef.dcs.smdStudio.gui.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.Serializable;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import uk.ac.shef.dcs.smdStudio.domain.Module;
import uk.ac.shef.dcs.smdStudio.util.FileFilterEx;
import uk.ac.shef.dcs.smdStudio.util.XMLUtilities;

/**
 * <p>
 * Title: Project
 * </p>
 * 
 * <p>
 * Description: {@link JInternalFrame} containing individual Modules
 * </p>
 * 
 * @author <a href=mailto:a.dorri@genesys.shef.ac.uk>Aria Dorri</a>
 * @version 1.0.53
 */
@SuppressWarnings("serial")
public class Project extends JInternalFrame implements Serializable, Printable {
	/**
	 * Module that is shown
	 */
	public Module smd;

	/**
	 * The main program window
	 */
	public Main parent;

	/**
	 * Top Panel showing information about the smd
	 */
	public JPanel smdPanel;

	/**
	 * University
	 */
	public JLabel university;

	/**
	 * Department
	 */
	public JLabel department;

	/**
	 * Course Code
	 */
	public JLabel courseCode;

	/**
	 * Module Name
	 */
	public JLabel name;

	/**
	 * Module Date
	 */
	public JLabel date;

	/**
	 * Module Duration
	 */
	public JLabel duration;

	/**
	 * Next Question to be printed
	 */
	public int nextQuestion;

	/**
	 * Number of the last page that has been printed
	 */
	int lastPage;

	/**
	 * Number of the last question that has been printed
	 */
	int lastQuestion;

	/**
	 * Initializes instance of {@link Project} frame
	 * 
	 * @param title
	 *            String
	 * @param parent
	 *            Main
	 */
	public Project(String title, Main parent) {
		super(title, true, true, true, true);
		this.parent = parent;
		nextQuestion = 0;
		this.addInternalFrameListener(new ModuleListener());
		this.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
	}

	/**
	 * Loads and generates the Module inside the frame from the given path
	 * 
	 * @param path
	 *            String
	 */
	public void setModule(String path) {
		try {
			smd = (Module) XMLUtilities.read(path);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this,
					"Error: Unable to read the selected file!", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
		setModule(smd);
	}

	/**
	 * Generates the Module inside the frame from the {@link Module} onject
	 * given
	 * 
	 * @param smd
	 *            Module
	 */
	public void setModule(Module smd) {
		this.smd = smd;
		add(getSmd(), BorderLayout.CENTER);
		lockInMinSize();
	}

	private void lockInMinSize() {
		// Ensures user cannot resize frame to be smaller than frame is right
		// now.
		final int origX = 600;
		final int origY = 440;
		this.addComponentListener(new java.awt.event.ComponentAdapter() {
			public void componentResized(ComponentEvent event) {
				Project.this.setSize((Project.this.getWidth() < origX) ? origX
						: Project.this.getWidth(),
						(Project.this.getHeight() < origY) ? origY
								: Project.this.getHeight());
			}
		});
	}

	/**
	 * Returns the smd Panel Used for Printing purposes
	 * 
	 * @return JPanel
	 */
	public JPanel getSmd() {
		smdPanel = new JPanel(new BorderLayout());
		smdPanel.setBackground(Color.WHITE);
		ModuleListener listener = new ModuleListener();
		JPanel topPanel = new JPanel(new GridLayout(4, 1));
		topPanel.setBackground(Color.WHITE);
		topPanel.addMouseListener(listener);
		setLayout(new BorderLayout());
		JPanel coursePanel = new JPanel(new BorderLayout());
		coursePanel.setBackground(Color.WHITE);
		coursePanel.addMouseListener(listener);
		university = new JLabel(smd.getUniversity());
		university.setBackground(Color.WHITE);
		university.setFont(new Font("MS Sans Serif", Font.BOLD, 30));
		university.setHorizontalAlignment(SwingConstants.CENTER);
		university.addMouseListener(listener);
		department = new JLabel(smd.getDepartment());
		department.setBackground(Color.WHITE);
		department.setFont(new Font("MS Sans Serif", Font.BOLD + Font.ITALIC,
				25));
		department.setHorizontalAlignment(SwingConstants.CENTER);
		department.addMouseListener(listener);
		courseCode = new JLabel(smd.getCourseCode());
		courseCode.setBackground(Color.WHITE);
		courseCode.setFont(new Font("MS Sans Serif", Font.BOLD, 20));
		courseCode.setHorizontalAlignment(SwingConstants.RIGHT);
		courseCode.addMouseListener(listener);
		name = new JLabel(smd.getName());
		name.setBackground(Color.WHITE);
		name.setFont(new Font("MS Sans Serif", Font.BOLD, 18));
		name.setHorizontalAlignment(SwingConstants.LEFT);
		name.addMouseListener(listener);
		String val = String.valueOf(smd.getDate().get(Calendar.DATE)) + '.'
				+ String.valueOf(smd.getDate().get(Calendar.MONTH) + 1) + '.'
				+ String.valueOf(smd.getDate().get(Calendar.YEAR));
		date = new JLabel("Date : " + val);
		date.setBackground(Color.WHITE);
		date.setFont(new Font("MS Sans Serif", Font.BOLD, 15));
		date.setHorizontalAlignment(SwingConstants.RIGHT);
		date.addMouseListener(listener);
		duration = new JLabel("Duration " + smd.getDuration() + "min(s)");
		duration.setBackground(Color.WHITE);
		duration.setFont(new Font("MS Sans Serif", Font.BOLD, 15));
		duration.setHorizontalAlignment(SwingConstants.RIGHT);
		duration.addMouseListener(listener);
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(Color.WHITE);
		panel.addMouseListener(listener);
		JPanel panel1 = new JPanel(new BorderLayout());
		panel1.setBackground(Color.WHITE);
		panel1.addMouseListener(listener);
		panel.add(date, BorderLayout.EAST);
		panel.add(name, BorderLayout.WEST);
		panel1.add(duration, BorderLayout.SOUTH);
		questions = new ModuleGenerator(smd, this);
		questions.setBackground(Color.WHITE);
		JScrollPane scroll = new JScrollPane(questions);
		scroll.setBackground(Color.WHITE);
		coursePanel.add(courseCode, BorderLayout.WEST);
		coursePanel.add(panel1, BorderLayout.EAST);
		topPanel.add(university);
		topPanel.add(department);
		topPanel.add(coursePanel);
		topPanel.add(panel);
		topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		smdPanel.add(topPanel, BorderLayout.NORTH);
		smdPanel.add(scroll, BorderLayout.CENTER);
		return smdPanel;
	}

	/**
	 * print method used for printing the smd
	 * 
	 * @param pg
	 *            Graphics
	 * @param pageFormat
	 *            PageFormat
	 * @param pageIndex
	 *            int
	 * @return int
	 * @throws PrinterException
	 */
	public int print(Graphics pg, PageFormat pageFormat, int pageIndex)
			throws PrinterException {
		JFrame printFrame;
		if (nextQuestion != -1 || lastPage == pageIndex) {
			if (lastPage == pageIndex) {
				nextQuestion = lastQuestion;
			} else {
				lastQuestion = nextQuestion;
			}
			printFrame = new ModuleGenerator.ModulePrintMaker(smd, this,
					nextQuestion, pageIndex + 1);
			nextQuestion = ((ModuleGenerator.ModulePrintMaker) printFrame).nextQuestion;
		} else {
			return NO_SUCH_PAGE;
		}
		lastPage = pageIndex;
		printFrame.setSize(610, 800);
		printFrame.setVisible(true);
		printFrame.paint(pg);
		pg.setColor(Color.black);
		pg.drawString(String.valueOf(pageIndex + 1), 515, 705);
		if (pageIndex > 0) {
			pg.setFont(new Font("MS Sans Serif", Font.ITALIC, 10));
			pg.drawString(smd.getCourseCode() + " " + smd.getName(), 85, 100);
		}
		printFrame.dispose();
		return PAGE_EXISTS;
	}

	public void doSave() {
		File file = new File(this.getTitle());
		if (file.exists() && file.isFile() && file.canWrite()) {
			save(file);
		} else {
			int iOption = JOptionPane.showConfirmDialog(this,
					"Cannot acces the file \"" + file.getAbsolutePath()
							+ "\", do you want to change the file name?",
					"Error", JOptionPane.YES_NO_OPTION);
			switch (iOption) {
			case JOptionPane.YES_OPTION:
				doSaveAs();
				break;
			case JOptionPane.NO_OPTION:// do nothing
				break;
			}
		}
	}

	/**
	 * This method saves the smd to a file other than the file to which it is
	 * currently saved.
	 * 
	 */
	public void doSaveAs() {
		File file;
		int ret;

		JFileChooser chooser = new JFileChooser();
		FileFilterEx filter = new FileFilterEx();
		filter.addExtension("exs");
		filter.setDescription("Online Module");
		chooser.setFileFilter(filter);

		chooser.setDialogType(JFileChooser.SAVE_DIALOG);
		chooser.setSelectedFile(new File(this.getTitle()));
		ret = chooser.showDialog(this, "Save");
		while (ret == JFileChooser.APPROVE_OPTION) {
			file = chooser.getSelectedFile();
			if (!(file.getAbsolutePath().endsWith(".exs") || file
					.getAbsolutePath().endsWith(".EXS"))) {
				file = new File(file.getAbsolutePath() + ".exs");
			}
			if (file.exists()) {
				int response = JOptionPane.showConfirmDialog(this, file
						.getAbsolutePath()
						+ " already exists,\ndo you want to replace it?",
						"Save As", JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				if (response == JOptionPane.YES_OPTION) {
					if (!file.canWrite()) {
						JOptionPane.showMessageDialog(this, file
								.getAbsolutePath()
								+ " can not be modified.", "Save As",
								JOptionPane.ERROR_MESSAGE);
					} else {
						save(file);
						break;
					}
				}
			} else {
				save(file);
				break;
			}
			ret = chooser.showDialog(Project.this, "Save");
		}
	}

	/**
	 * This method saves the file to the given file object.
	 * 
	 * @param file
	 */
	private void save(File file) {
		try {
			XMLUtilities.write(file, Project.this.smd);
			Project.this.setTitle(file.getAbsolutePath());
		} catch (Exception eee) {
			JOptionPane.showMessageDialog(this,
					"Error: Unable to save to the file!", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * <p>
	 * Title: Module Listener
	 * </p>
	 * 
	 * <p>
	 * Description: Handels the events on the {@link Project#smdPanel}
	 * </p>
	 * 
	 * <p>
	 * Copyright: Copyright (c) 2005
	 * </p>
	 * 
	 * @author <a href=mailto:a.dorri@genesys.shef.ac.uk>Aria Dorri</a>
	 * @version 1.0.3
	 * @see Project#smdPanel
	 */
	class ModuleListener extends InternalFrameAdapter implements MouseListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.event.InternalFrameAdapter#internalFrameClosing(javax.swing.event.InternalFrameEvent)
		 */
		@Override
		public void internalFrameClosing(InternalFrameEvent e) {
			int iOption = JOptionPane.showConfirmDialog(Project.this,
					"Do you want to save changes before exit?", "Save",
					JOptionPane.YES_NO_CANCEL_OPTION);
			switch (iOption) {
			case JOptionPane.YES_OPTION:
				doSave();
				Project.this.dispose();
				break;
			case JOptionPane.NO_OPTION:
				Project.this.dispose();
				break;
			case JOptionPane.CANCEL_OPTION:// do nothing
				break;
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
		 */
		public void mouseClicked(MouseEvent e) {
		}

		/**
		 * Handels if the mouse is pressed on the ModulePanel
		 * 
		 * @param e
		 *            MouseEvent
		 */
		public void mousePressed(MouseEvent e) {
			new ModuleEdit(parent, Project.this);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
		 */
		public void mouseReleased(MouseEvent e) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
		 */
		public void mouseEntered(MouseEvent e) {
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
		 */
		public void mouseExited(MouseEvent e) {
			setCursor(Cursor.getDefaultCursor());
		}

	}

}
