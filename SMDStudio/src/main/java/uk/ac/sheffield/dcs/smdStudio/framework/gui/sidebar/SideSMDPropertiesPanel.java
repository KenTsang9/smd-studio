package uk.ac.sheffield.dcs.smdStudio.framework.gui.sidebar;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Hashtable;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.ac.sheffield.dcs.smdStudio.framework.gui.DiagramPanel;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.theme.ThemeManager;

@SuppressWarnings("serial")
public class SideSMDPropertiesPanel extends JPanel {

	/**
	 * Current diagram panel
	 */
	private DiagramPanel diagramPanel;

	/**
	 * Component(s panel
	 */
	private JPanel panel;

	private JSlider teamQuality;

	private ChangeListener teamQualityListener;

	private JSpinner trainingCost;

	private ChangeListener trainingCostListener;

	/**
	 * Default contructor
	 */
	public SideSMDPropertiesPanel(DiagramPanel diagramPanel,
			ResourceBundle sideBarResourceBundle) {
		this.diagramPanel = diagramPanel;

		setBackground(ThemeManager.getInstance().getTheme()
				.getSIDEBAR_ELEMENT_BACKGROUND_COLOR());

		JPanel trainingPanel = getTrainingPanel(sideBarResourceBundle);
		getPanel().add(trainingPanel, BorderLayout.NORTH);
		JPanel teamQualityPanel = getTeamQualityPanel(sideBarResourceBundle);
		getPanel().add(teamQualityPanel, BorderLayout.CENTER);

		this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		add(getPanel());
		panel.getSize();
	}

	private void doTeamQualityChanged(double teamQuality) {
		diagramPanel.getGraph().changeTeamQuality(teamQuality);

	}

	private void doTrainingCostChanged(Number trainingCost) {
		diagramPanel.getGraph().changeTrainingCost(trainingCost.doubleValue());
	}

	private JPanel getPanel() {
		if (this.panel == null) {
			this.panel = new JPanel();
			this.panel.setOpaque(false);
			this.panel.setBorder(new EmptyBorder(0, 5, 0, 0));
			this.panel.setLayout(new BorderLayout());
		}
		return this.panel;
	}

	private JPanel getTeamQualityPanel(ResourceBundle bundle) {
		JPanel teamQualityPanel = new JPanel(new GridBagLayout());

		initTeamQuality(bundle);
		JLabel label = new JLabel(bundle.getString("teamQuality.text"));
		label.setFont(new Font(label.getFont().getName(), Font.BOLD, label
				.getFont().getSize() + 5));
		// label.setForeground(Color.gray);

		GridBagConstraints c = new GridBagConstraints();
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 0.1;
		c.fill = GridBagConstraints.BOTH;
		teamQualityPanel.add(label, c);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(10, 0, 0, 0);
		teamQualityPanel.add(teamQuality, c);
		teamQualityPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createRaisedBevelBorder(), BorderFactory
						.createEmptyBorder(10, 10, 10, 10)));
		return teamQualityPanel;
	}

	private JPanel getTrainingPanel(ResourceBundle bundle) {
		JPanel trainingPanel = new JPanel(new GridBagLayout());

		initTrainingCost();
		JLabel label = new JLabel(bundle.getString("trainingCost.text"));
		label.setFont(new Font(label.getFont().getName(), Font.BOLD, label
				.getFont().getSize() + 5));
		// label.setForeground(Color.gray);

		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 0.1;
		c.fill = GridBagConstraints.BOTH;
		trainingPanel.add(label, c);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(0, 10, 0, 0);
		trainingPanel.add(trainingCost, c);
		trainingPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createRaisedBevelBorder(), BorderFactory
						.createEmptyBorder(10, 10, 10, 10)));
		return trainingPanel;
	}

	private void initTeamQuality(ResourceBundle bundle) {
		teamQuality = new JSlider(JSlider.HORIZONTAL, 1, 20, 10);

		// Turn on labels at major tick marks.
		teamQuality.setMajorTickSpacing(10);
		teamQuality.setMinorTickSpacing(1);
		// Create the label table
		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put(new Integer(1), new JLabel(bundle
				.getString("teamQuality.expert.text")));
		labelTable.put(new Integer(10), new JLabel(bundle
				.getString("teamQuality.medium.text")));
		labelTable.put(new Integer(20), new JLabel(bundle
				.getString("teamQuality.low.text")));
		teamQuality.setLabelTable(labelTable);
		teamQuality.setPaintTicks(true);
		teamQuality.setPaintLabels(true);

		teamQualityListener = new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				if (!teamQuality.getValueIsAdjusting()) {
					doTeamQualityChanged(teamQuality.getValue() / 10d);
				}

			}
		};
		teamQuality.addChangeListener(teamQualityListener);
	}

	private void initTrainingCost() {
		trainingCost = new JSpinner(new SpinnerNumberModel(0, 0, 1000, 0.5));
		trainingCostListener = new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				SpinnerModel numberModel = trainingCost.getModel();
				if (numberModel instanceof SpinnerNumberModel) {
					doTrainingCostChanged(((SpinnerNumberModel) numberModel)
							.getNumber());
				}

			}
		};
		trainingCost.addChangeListener(trainingCostListener);
	}

	public void setTeamQuality(double teamQuality) {
		this.teamQuality.removeChangeListener(teamQualityListener);
		this.teamQuality.getModel().setValue((int) (teamQuality * 10));
		this.teamQuality.addChangeListener(teamQualityListener);
	}

	public void setTrainingCost(double cost) {
		this.trainingCost.removeChangeListener(trainingCostListener);
		this.trainingCost.setValue(cost);
		this.trainingCost.addChangeListener(trainingCostListener);
	}

}
