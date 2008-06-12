package uk.ac.shef.dcs.smdStudio.gui.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;

import uk.ac.shef.dcs.smdStudio.domain.AbstractModule;
import uk.ac.shef.dcs.smdStudio.gui.controller.DeletableSMDController;
import uk.ac.shef.dcs.smdStudio.gui.controller.SMDController;

/**
 * <p>
 * Title: Multiple Test Edit
 * </p>
 * 
 * <p>
 * Description: Allows editing {@link MultipleTest} type of the description
 * </p>
 * 
 * 
 * @author <a href=mailto:a.dorri@genesys.shef.ac.uk>Aria Dorri</a>
 * @version 1.0.5
 */
@SuppressWarnings("serial")
public class SMDPane extends JDialog {

	/**
	 * Description Text
	 * 
	 * @see MultipleTest#module
	 */
	private JTextArea descriptionText;

	/**
	 * A temporary instance of class {@link java.util.List} that holds the
	 * {@link MultipleTest#tests}
	 */
	private List<String> list;

	/**
	 * Name Text
	 * 
	 * @see MultipleTest#module
	 */
	private JTextField nameText;

	private SMDController smdController;

	/**
	 * List of the Tests of the description
	 * 
	 * @see MultipleTest#tests
	 */
	private JList testList;

	public static void showSMDPane(Component parentComponent,
			SMDController smdController) {
		SMDPane pane = new SMDPane(parentComponent, smdController);
		pane.setVisible(true);
	}

	/**
	 * Initialises an instance of {@link SMDPane} dialog
	 * 
	 * @param parent
	 *            The parent frame that called for this dialog
	 * @param panel
	 *            The {@link DescriptionGenerator} panel that contains the
	 *            description
	 * @param listener
	 *            ActionListener
	 */
	private SMDPane(Component parentComponent, SMDController smdController) {
		super(JOptionPane.getRootFrame(), true);

		this.smdController = smdController;
		AbstractModule module = smdController.getSMD();

		SMDPaneListener listener = new SMDPaneListener();
		setUndecorated(true);

		initList(module);
		initTestList();

		boolean insert = module == null;

		JPanel actionPanel = initActionPanel(listener, insert);
		JPanel namePanel = initName(module);
		JPanel descriptionPanel = initDescription(module);
		JPanel listPanel = initListPanel(listener);

		JPanel mainPanel = new JPanel(new BorderLayout(2, 2));
		mainPanel.add(namePanel, BorderLayout.NORTH);
		mainPanel.add(descriptionPanel, BorderLayout.CENTER);
		mainPanel.add(listPanel, BorderLayout.EAST);
		mainPanel.add(actionPanel, BorderLayout.SOUTH);
		mainPanel.setBorder(BorderFactory.createTitledBorder(insert ? "Insert"
				: "Edit"));

		add(mainPanel);
		pack();
		setLocationRelativeTo(parentComponent);
	}

	/**
	 * @param text
	 */
	public void addNewTest(String test) {
		this.list.add(test);
		this.testList.setListData(new Vector<String>(this.list));
		this.testList.setSelectedIndex(0);
		this.testList.repaint();
		this.pack();
		this.repaint();
	}

	public AbstractModule getModule() {
		AbstractModule module = new AbstractModule();
		module.setName(this.nameText.getText());
		module.setDescription(this.descriptionText.getText());
		module.setTests(list);
		return module;
	}

	/**
	 * 
	 */
	public void removeSelectedTest() {
		this.list.remove(this.testList.getSelectedValue().toString());
		this.testList.setListData(new Vector<String>(this.list));
		this.testList.setSelectedIndex(0);
		this.testList.repaint();
		this.pack();
		this.repaint();
	}

	private JPanel initActionPanel(ActionListener listener, boolean insert) {

		JButton actionButton = insert ? initInsertButton(listener)
				: initUpdate(listener);
		JButton cancel = initCancel(listener);
		JPanel actionPanel = new JPanel(new BorderLayout());
		actionPanel.add(actionButton, BorderLayout.WEST);
		actionPanel.add(cancel, BorderLayout.EAST);
		if (smdController instanceof DeletableSMDController) {
			JButton delete = initDelete(listener);

			JPanel tempPanel = new JPanel(new BorderLayout());
			tempPanel.add(actionPanel, BorderLayout.EAST);
			tempPanel.add(delete, BorderLayout.WEST);
			actionPanel = tempPanel;
		}
		return actionPanel;
	}

	/**
	 * Initialises the add button.
	 * 
	 * @param listener
	 */
	private JButton initAdd(ActionListener listener) {
		JButton add = new JButton("Add");
		add.setMnemonic('A');
		add.addActionListener(listener);
		add.setActionCommand(String.valueOf(SMDPaneListener.ADD));
		return add;
	}

	/**
	 * Initialises the cancel button.
	 * 
	 * @param listener
	 */
	private JButton initCancel(ActionListener listener) {
		JButton cancel = new JButton("Cancel");
		cancel.setMnemonic('C');
		cancel.addActionListener(listener);
		cancel.setActionCommand(String.valueOf(SMDPaneListener.CANCEL));
		return cancel;
	}

	/**
	 * Initialises the Delete button.
	 * 
	 * @param listener
	 */
	private JButton initDelete(ActionListener listener) {
		JButton delete = new JButton("Delete");
		delete.setMnemonic('D');
		delete.addActionListener(listener);
		delete.setActionCommand(String.valueOf(SMDPaneListener.DELETE));
		return delete;
	}

	/**
	 * Initialises the components holding the description.
	 * 
	 */
	private JPanel initDescription(AbstractModule module) {
		JPanel descriptionPanel = new JPanel(new BorderLayout());
		JLabel descriptionLabel = new JLabel("Description:");
		descriptionText = new JTextArea(module == null ? "" : module
				.getDescription());
		descriptionText.setColumns(20);
		descriptionText.setRows(3);
		descriptionText.setLineWrap(true);
		descriptionPanel.add(descriptionLabel, BorderLayout.NORTH);
		descriptionPanel.add(new JScrollPane(descriptionText),
				BorderLayout.CENTER);
		return descriptionPanel;
	}

	private JButton initInsertButton(ActionListener listener) {
		JButton insert = new JButton("Insert");
		insert.setMnemonic('I');
		insert.addActionListener(listener);
		insert.setActionCommand(String.valueOf(SMDPaneListener.INSERT));
		insert.setEnabled(false);
		return insert;
	}

	/**
	 * Populates and initialises the list on the tests.
	 * 
	 */
	private void initList(AbstractModule module) {
		list = new ArrayList<String>();
		if (module != null) {
			for (String test : module.getTests()) {
				list.add(test);
			}
		}
		testList = new JList(new Vector<String>(list)) {
			@Override
			public Point getToolTipLocation(MouseEvent e) {
				int row = locationToIndex(e.getPoint());
				return indexToLocation(row);
			}

			@Override
			public String getToolTipText(MouseEvent e) {
				int row = locationToIndex(e.getPoint());
				return getModel().getElementAt(row).toString();
			}
		};
	}

	private JPanel initListPanel(ActionListener listener) {
		JButton add = initAdd(listener);
		JButton remove = initRemove(listener);
		JScrollPane listScroll = new JScrollPane(testList,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		JPanel listPanel = new JPanel(new BorderLayout(2, 2));
		listPanel.setBorder(BorderFactory.createTitledBorder("Tests"));

		JPanel panel = new JPanel(new GridLayout(1, 2, 2, 2));
		panel.add(add);
		panel.add(remove);
		listPanel.add(panel, BorderLayout.SOUTH);

		listPanel.add(listScroll, BorderLayout.CENTER);

		return listPanel;
	}

	/**
	 * Initialises the components holding the name.
	 * 
	 */
	private JPanel initName(AbstractModule module) {
		JPanel namePanel = new JPanel(new BorderLayout());
		JLabel nameLabel = new JLabel("Name:");
		nameText = new JTextField(module == null ? "" : module.getName());
		namePanel.add(nameLabel, BorderLayout.NORTH);
		namePanel.add(new JScrollPane(nameText), BorderLayout.CENTER);
		return namePanel;
	}

	/**
	 * initialises the remove button.
	 * 
	 * @param listener
	 */
	private JButton initRemove(ActionListener listener) {
		JButton remove = new JButton("Remove");
		remove.setMnemonic('R');
		remove.addActionListener(listener);
		remove.setActionCommand(String.valueOf(SMDPaneListener.REMOVE));
		return remove;
	}

	/**
	 * Initialises the list of the tests.
	 * 
	 */
	private void initTestList() {
		testList.setSelectedIndex(0);
		testList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		testList.setVisibleRowCount(4);
		testList.setFixedCellWidth(100);
	}

	/**
	 * Initialises the Update button.
	 * 
	 * @param listener
	 */
	private JButton initUpdate(ActionListener listener) {
		JButton update = new JButton("Update");
		update.setMnemonic('U');
		update.addActionListener(listener);
		update.setActionCommand(String.valueOf(SMDPaneListener.UPDATE));
		return update;
	}

	/**
	 * <p>
	 * Title: Multiple Choice Listener
	 * </p>
	 * 
	 * <p>
	 * Description: Handles the events passed from {@link SMDPane} dialog
	 * </p>
	 * 
	 * @author <a href=mailto:a.dorri@genesys.shef.ac.uk>Aria Dorri</a>
	 * @version 1.0.5
	 * 
	 * @see SMDPane
	 */
	private class SMDPaneListener implements ActionListener {

		/**
		 * actionCommand for {@link NewMultiple#add} button
		 */
		private final static int ADD = 1;

		/**
		 * actionCommand for {@link NewMultiple#cancel} button
		 */
		private final static int CANCEL = 2;

		private static final int DELETE = 3;

		/**
		 * actionCommand for {@link NewMultiple#insert} button
		 */
		private final static int INSERT = 4;

		/**
		 * actionCommand for {@link NewMultiple#remove} button
		 */
		private final static int REMOVE = 5;

		/**
		 * actionCommand for {@link NewMultiple#update} button
		 */
		private final static int UPDATE = 6;

		public void actionPerformed(ActionEvent e) {
			switch (Integer.parseInt(e.getActionCommand())) {
			case INSERT:
				doInsert();
				break;
			case UPDATE:
				doUpdate();
				break;
			case CANCEL:
				SMDPane.this.dispose();
				break;
			case DELETE:
				doDelete();
				break;
			case ADD:
				doAdd();
				break;
			case REMOVE:
				doRemove();
				break;
			}
		}

		private void doAdd() {
			while (true) {
				JTextArea text = new JTextArea(3, 15);
				text.setLineWrap(true);
				Object[] msg = { "Please enter text of the new test:\n",
						new JScrollPane(text) };
				int result = JOptionPane.showConfirmDialog(SMDPane.this, msg,
						"New Choice", JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				if (result == JOptionPane.OK_OPTION) {
					SMDPane.this.addNewTest(text.getText());
					break;
				}
				break;
			}
		}

		private void doDelete() {
			try {
				if (smdController instanceof DeletableSMDController) {
					DeletableSMDController deletableSMDController = (DeletableSMDController) smdController;
					deletableSMDController.deleteSMD();
				} else {
					throw new IllegalStateException(
							"Unable to delete a non-deletable SMD!");
				}
			} finally {
				SMDPane.this.dispose();
			}
		}

		private void doInsert() {
			AbstractModule module = SMDPane.this.getModule();
			smdController.createNewSMD(module);
			SMDPane.this.dispose();
		}

		private void doRemove() {
			SMDPane.this.removeSelectedTest();
		}

		private void doUpdate() {
			AbstractModule module = SMDPane.this.getModule();
			smdController.updateSMD(module);
			SMDPane.this.dispose();
		}

	}
}
