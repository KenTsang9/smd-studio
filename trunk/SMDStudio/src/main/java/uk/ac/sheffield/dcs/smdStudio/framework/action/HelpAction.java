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

package uk.ac.sheffield.dcs.smdStudio.framework.action;

import java.awt.Toolkit;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import uk.ac.sheffield.dcs.smdStudio.framework.gui.AboutDialog;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.DialogFactory;
import uk.ac.sheffield.dcs.smdStudio.framework.resources.ResourceBundleConstant;
import uk.ac.sheffield.dcs.smdStudio.framework.util.BrowserLauncher;
import uk.ac.sheffield.dcs.smdStudio.framework.util.ClipboardPipe;


/**
 * Concentrates help menu actions
 * 
 * @author Alexandre de Pellegrin
 * 
 */
public class HelpAction
{

    public HelpAction()
    {
        this.rs = ResourceBundle.getBundle(ResourceBundleConstant.MENU_STRINGS, Locale.getDefault());
    }

    /**
     * Show the about dialog on the parent frame
     * 
     * @param parentFrame
     */
    public void showAboutDialog(JFrame parentFrame)
    {
        AboutDialog dialog = new AboutDialog(parentFrame);
        dialog.setVisible(true);
    }

    /**
     * Opens online help
     */
    public void openUserGuide()
    {
        String url = this.rs.getString("help.userguide.url");
        boolean isOK = BrowserLauncher.openURL(url);
        if (!isOK)
        {
            openBrowser(url);
        }

    }

    /**
     * Goes to homepage
     */
    public void openHomepage()
    {
        String url = this.rs.getString("help.homepage.url");
        boolean isOK = BrowserLauncher.openURL(url);
        if (!isOK)
        {
            openBrowser(url);
        }

    }

    /**
     * Launch web browser or copy url to clipoard if failed
     * 
     * @param url
     */
    private void openBrowser(String url)
    {
        String message = MessageFormat.format(this.rs.getString("dialog.open_url_failed.ok"), new Object[]
        {
            url
        });
        String title = this.rs.getString("dialog.open_url_failed.title");
        ImageIcon icon = new ImageIcon(this.getClass().getResource(this.rs.getString("dialog.open_url_failed.icon")));
        JOptionPane optionPane = new JOptionPane();
        optionPane.setMessage(message);
        optionPane.setIcon(icon);
        ClipboardPipe pipe = new ClipboardPipe(url);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(pipe, null);
        DialogFactory.getInstance().showDialog(optionPane, title, true);
    }

    /** Resource bundle */
    private ResourceBundle rs;

}
