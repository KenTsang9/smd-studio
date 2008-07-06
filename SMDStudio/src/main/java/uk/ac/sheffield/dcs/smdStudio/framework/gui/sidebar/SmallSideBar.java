package uk.ac.sheffield.dcs.smdStudio.framework.gui.sidebar;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import uk.ac.sheffield.dcs.smdStudio.framework.gui.DiagramPanel;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.theme.Theme;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.theme.ThemeManager;
import uk.ac.sheffield.dcs.smdStudio.framework.resources.ResourceBundleConstant;

import com.l2fprod.common.swing.JTaskPane;

@SuppressWarnings("serial")
public class SmallSideBar extends JPanel implements ISideBar
{

    public SmallSideBar(DiagramPanel diagramPanel)
    {
        super();
        setLayout(new GridBagLayout());
        GridBagConstraints c1 = new GridBagConstraints();
        c1.gridx = 0;
        c1.gridy = 0;
        c1.anchor = GridBagConstraints.NORTH;
        c1.weighty = 0;
        GridBagConstraints c2 = new GridBagConstraints();
        c2.gridx = 0;
        c2.gridy = 1;
        c2.anchor = GridBagConstraints.NORTH;
        c2.weighty = 0;
        GridBagConstraints c3 = new GridBagConstraints();
        c3.gridx = 0;
        c3.gridy = 2;
        c3.fill = GridBagConstraints.VERTICAL;
        c3.anchor = GridBagConstraints.SOUTH;
        c3.weighty = 1;
        this.diagramPanel = diagramPanel;
        add(this.getSideShortcutMandatoryPanel(), c1);
        add(this.getSideToolPanel(), c2);
        add(new JLabel(), c3);
        
        Theme cLAF = ThemeManager.getInstance().getTheme();
        setBackground(cLAF.getSIDEBAR_ELEMENT_BACKGROUND_COLOR());
        setBorder(new EmptyBorder(0, 0, 0, 0));
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.ac.sheffield.dcs.smdStudio.framework.plugin.VisitableSideBar#addElement(java.awt.Component, java.lang.String)
     */
    public void addElement(final Component c, String title)
    {
        taskPane.add(c);
    }

    public SideToolPanel getSideToolPanel()
    {
        if (this.sideToolPanel == null)
        {
            this.sideToolPanel = new SideToolPanel(this.diagramPanel.getGraphPanel().getGraph());
            this.sideToolPanel.setToolsTitlesVisible(false);
        }
        return this.sideToolPanel;
    }

    private SideShortcutMandatoryPanel getSideShortcutMandatoryPanel()
    {
        if (this.sideShortcutMandatoryPanel == null)
        {
            this.sideShortcutMandatoryPanel = new SideShortcutMandatoryPanel(this.diagramPanel, this.getSideBarResourceBundle(), false);
        }
        return this.sideShortcutMandatoryPanel;
    }


    /**
     * @return resource bundle
     */
    private ResourceBundle getSideBarResourceBundle()
    {
        if (this.resourceBundle == null)
        {
            this.resourceBundle = ResourceBundle.getBundle(ResourceBundleConstant.SIDEBAR_STRINGS, Locale.getDefault());
        }
        return this.resourceBundle;
    }
    
    /* (non-Javadoc)
     * @see uk.ac.sheffield.dcs.smdStudio.framework.gui.sidebar.ISideBar#getAWTComponent()
     */
    public Component getAWTComponent()
    {
        return this;
    }

    private DiagramPanel diagramPanel;
    private SideToolPanel sideToolPanel;
    private SideShortcutMandatoryPanel sideShortcutMandatoryPanel;
    private JTaskPane taskPane;
    private ResourceBundle resourceBundle;

}
