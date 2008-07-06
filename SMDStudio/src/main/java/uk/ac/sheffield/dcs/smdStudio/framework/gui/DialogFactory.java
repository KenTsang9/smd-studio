/*
 Violet - A program for editing UML diagrams.

 Copyright (C) 2007 Cay S. Horstmann (http://horstmann.com)
 Alexandre de Pellegrin (http://alexdp.free.fr);

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package uk.ac.sheffield.dcs.smdStudio.framework.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import uk.ac.sheffield.dcs.smdStudio.framework.gui.theme.ThemeManager;
import uk.ac.sheffield.dcs.smdStudio.framework.resources.ResourceBundleConstant;
import uk.ac.sheffield.dcs.smdStudio.framework.resources.ResourceFactory;


/**
 * This factory manages dialog. It could create dialogs or call a listener if the dialog constrction is delegated to an external
 * program such as an Eclipse plugin
 * 
 * @author Alexandre de Pellegrin
 * 
 */
public class DialogFactory
{

    /**
     * Delegates dialog to external code when showDialog() is called
     */
    public static final int DELEGATED_MODE = 1;

    /**
     * Unique instance
     */
    private static DialogFactory instance;

    /**
     * Default mode. Keeps the responsability to create and display dialogs.
     */
    public static final int INTERNAL_MODE = 2;

    /**
     * JPanel default background color
     */
    private static final Color JPANEL_BG_COLOR = (new JPanel()).getBackground();

    /**
     * Delegated dialog cache (Used if the dialog mode is set to delegated and no listener is registered. So, the foactory stores
     * doalogs that will be displayed when a new listener will be registered
     */
    private List<Object[]> delegatedDialogCache = new ArrayList<Object[]>();

    /**
     * The frame that owns all dialogs
     */
    private Frame dialogOwner;

    /**
     * Working mode (set setDialofMode() comments)
     */
    private int factoryMode;

    /**
     * Listener that have the responsability to create and display dialogs Note that ther is one and only one listener. Why? Because
     * of the Eclipse framework that create an instance of the application for each document opened, and in the same JVM.
     */
    private DialogFactoryListener listener;

    /**
     * Resource bundle
     */
    private ResourceBundle resourceBundle;

    /**
     * Resource factory
     */
    private ResourceFactory resourceFactory;

    /**
     * @return unique instance
     */
    public static DialogFactory getInstance()
    {
        if (instance == null)
        {
            instance = new DialogFactory();
        }
        return instance;
    }

    /**
     * Singleton private constructor.
     */
    private DialogFactory()
    {
        this.factoryMode = INTERNAL_MODE;
        this.resourceBundle = ResourceBundle.getBundle(ResourceBundleConstant.OTHER_STRINGS, Locale.getDefault());
        this.resourceFactory = new ResourceFactory(this.resourceBundle);

    }

    /**
     * Set dialog location to the center of the owner frame repscting the screen bound
     * 
     * @param dialog
     * @param owner
     */
    private void centerDialog(JDialog dialog, Frame owner)
    {
        Rectangle b = owner.getBounds();

        double x = b.getX() + b.getWidth() / 2 - dialog.getWidth() / 2;
        double y = b.getY() + b.getHeight() / 2 - dialog.getHeight() / 2;
        Dimension screenSize = owner.getToolkit().getScreenSize();
        if (x + dialog.getWidth() > screenSize.getWidth())
        {
            x = screenSize.getWidth() - dialog.getWidth();
        }
        if (y + dialog.getHeight() > screenSize.getHeight())
        {
            y = screenSize.getHeight() - dialog.getHeight();
        }
        Point newLocation = new Point(Math.max((int) x, 0), Math.max((int) y, 0));
        dialog.setLocation(newLocation);
    }
    
    /**
     * Creates (and displays) a dialog
     * 
     * @param optionPane
     * @param title
     * @param isModal
     */
    private void createDialog(final JOptionPane optionPane, String title, boolean isModal)
    {
        if (this.dialogOwner == null)
        {
            throw new RuntimeException("Before invoking showDialog, you must call setDialogOwner() to defile the dialog owner");
        }
        final JDialog dialog = new JDialog(this.dialogOwner, title, isModal);
        dialog.setTitle(title);
        dialog.getContentPane().add(optionPane);
        dialog.pack();
        dialog.setResizable(false);
        optionPane.addPropertyChangeListener(new PropertyChangeListener()
        {
            public void propertyChange(PropertyChangeEvent event)
            {
                if (dialog.isVisible() && (event.getPropertyName().equals(JOptionPane.VALUE_PROPERTY))
                        && event.getNewValue() != null && event.getNewValue() != JOptionPane.UNINITIALIZED_VALUE)
                {
                    dialog.setVisible(false);
                    dialog.dispose();
                }
            }
        });
        if (this.dialogOwner != null) centerDialog(dialog, this.dialogOwner);
        dialog.setVisible(true);
    }
    
    /**
     * Calls listener that must display a dialog
     * 
     * @param optionPane
     * @param title
     * @param isModal
     */
    private void invokeListener(JOptionPane optionPane, String title, boolean isModal)
    {
        if (listener != null)
        {
            listener.mustDisplayPanel(optionPane, title, isModal);
        }
        if (listener == null)
        {
            this.delegatedDialogCache.add(new Object[]
            {
                    optionPane,
                    title,
                    new Boolean(isModal)
            });
        }
    }
    
    

    /**
     * @return true if a dialog owner has been set
     */
    public boolean isDialogOwnerSet() {
        if (this.dialogOwner != null) {
            return true;
        }
        return false;
    }

    /**
     * If the dialog factory is configured to DELEGATED_MODE and does not still have a listener that listens and display dialogs,
     * all dialogs are put into a sort of cache. Then the listener is registered, all cached dialogs are sent to it to be displayed.
     */
    private void performCachedDialogs()
    {
        while (!this.delegatedDialogCache.isEmpty())
        {
            Object[] object = (Object[]) this.delegatedDialogCache.get(0);
            JOptionPane optionPane = (JOptionPane) object[0];
            String title = (String) object[1];
            Boolean isModal = (Boolean) object[2];
            invokeListener(optionPane, title, isModal.booleanValue());
            this.delegatedDialogCache.remove(0);
        }
    }

    /**
     * Dialog factory mode. To understand it, consider that dialogs could be created internally or could be delegated to external
     * code. For example, it will be delegated if the program is embedded into an Eclipse plugin that uses the SWT API.
     * 
     * @param dialogFactoryConstant INTERNAL_MODE or DELEGATED_MODE
     */
    public void setDialogMode(int dialogFactoryConstant)
    {
        switch (dialogFactoryConstant)
        {
        case DELEGATED_MODE:
            this.factoryMode = DELEGATED_MODE;
            break;

        case INTERNAL_MODE:
            this.factoryMode = INTERNAL_MODE;
            break;

        default:
            throw new RuntimeException("Unknown dialog mode value");
        }
    }

    /**
     * Sets the dialog owner. This argument is mandatory. Anyway, I think that, as it usually never changes, it is not necessary to
     * give it as argument each time we construct a dialog. Only one time is efficient.
     * 
     * @param owner
     */
    public void setDialogOwner(Frame owner)
    {
        this.dialogOwner = owner;
    }

    /**
     * Set a default dialog owner (for emergency cases only!)
     */
    public void setEmergencyDialogOwner() {
        JFrame emergencyFrame = new JFrame();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = (int) screenSize.getWidth();
        int screenHeight = (int) screenSize.getHeight();
        emergencyFrame.setBounds(screenWidth / 16, screenHeight / 16, screenWidth * 7 / 8, screenHeight * 7 / 8);
        this.setDialogOwner(emergencyFrame);
    }

    /**
     * Adds a listener that could be invoked if the factory is set to DELEGATED_MODE
     * 
     * @param listener
     */
    public void setListener(DialogFactoryListener listener)
    {
        this.listener = listener;
        performCachedDialogs();
    }

    /**
     * According to the choosen mode : creates and shows a dialog or invokes via the listeners external program that must create and
     * display this dialog.
     * 
     * @param optionPane
     * @param title
     * @param isModal
     */
    public void showDialog(JOptionPane optionPane, String title, boolean isModal)
    {
        updateBackgroundColor(optionPane);
        if (INTERNAL_MODE == this.factoryMode)
        {
            createDialog(optionPane, title, isModal);
        }
        if (DELEGATED_MODE == this.factoryMode)
        {
            invokeListener(optionPane, title, isModal);
        }
    }

    public void showErrorDialog(String message)
    {
        JOptionPane optionPane = new JOptionPane();
        ImageIcon icon = resourceFactory.createIcon("dialog.generic.error.icon");
        JLabel label = new JLabel(message);
        label.setFont(label.getFont().deriveFont(Font.PLAIN));
        optionPane.setIcon(icon);
        optionPane.setMessage(label);
        String title = resourceBundle.getString("dialog.generic.error.title");
        showDialog(optionPane, title, true);
    }

    public void showInformationDialog(String message)
    {
        JOptionPane optionPane = new JOptionPane();
        ImageIcon icon = resourceFactory.createIcon("dialog.generic.information.icon");
        JLabel label = new JLabel(message);
        label.setFont(label.getFont().deriveFont(Font.PLAIN));
        optionPane.setIcon(icon);
        optionPane.setMessage(label);
        String title = resourceBundle.getString("dialog.generic.information.title");
        showDialog(optionPane, title, true);
    }

    public void showWarningDialog(String message)
    {
        JOptionPane optionPane = new JOptionPane();
        ImageIcon icon = resourceFactory.createIcon("dialog.generic.warning.icon");
        JLabel label = new JLabel(message);
        label.setFont(label.getFont().deriveFont(Font.PLAIN));
        optionPane.setIcon(icon);
        optionPane.setMessage(label);
        String title = resourceBundle.getString("dialog.generic.warning.title");
        showDialog(optionPane, title, true);
    }

    /**
     * Updates the jOptionPane background color for all its components
     * 
     * @param optionPane
     */
    private void updateBackgroundColor(Component component)
    {

        // Replace color if only its color is the default background panel color
        // (Could be really optimized)
        if (component.getBackground().equals(JPANEL_BG_COLOR))
        {
            component.setBackground(ThemeManager.getInstance().getTheme().getBACKGROUND_COLOR());
        }

        if (component instanceof Container)
        {
            Container container = (Container) component;
            for (int i = 0, ub = container.getComponentCount(); i < ub; ++i)
                updateBackgroundColor(container.getComponent(i));
        }

    }
}
