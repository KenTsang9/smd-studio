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

package uk.ac.sheffield.dcs.smdStudio.framework.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import uk.ac.sheffield.dcs.smdStudio.framework.gui.DialogFactory;
import uk.ac.sheffield.dcs.smdStudio.framework.resources.ResourceBundleConstant;


/**
 * This class implements a FileService with a JFileChooser
 */
class JFileChooserService implements FileChooserService
{
    private JFileChooser fileChooser;

    public JFileChooserService()
    {
        fileChooser = new JFileChooser();
        File initialDirectory = FileService.getLastDir();
        fileChooser.setCurrentDirectory(initialDirectory);
    }

    public boolean isWebStart()
    {
        return false;
    }

    public FileOpenerHandler open(String defaultDirectory, String defaultFile, ExtensionFilter[] filters)
            throws FileNotFoundException
    {
        fileChooser.resetChoosableFileFilters();
        for (int i = 0; i < filters.length; i++)
        {
            fileChooser.addChoosableFileFilter(filters[i]);
        }
        if (defaultDirectory != null)
        {
            fileChooser.setCurrentDirectory(new File(defaultDirectory));
        }
        if (defaultFile == null)
        {
            fileChooser.setSelectedFile(null);
        }
        if (defaultFile != null)
        {
            fileChooser.setSelectedFile(new File(defaultFile));
        }
        int response = fileChooser.showOpenDialog(null);
        File selectedFile = null;
        if (response == JFileChooser.APPROVE_OPTION)
        {
            selectedFile = fileChooser.getSelectedFile();
        }
        FileOpenerHandler foh = new FileOpenerHandler()
        {
            public void initialize(File f) throws FileNotFoundException
            {
                if (f != null)
                {
                    name = f.getPath();
                    in = new FileInputStream(f);
                }
            }

            public InputStream getInputStream()
            {
                return in;
            }

            public String getName()
            {
                return name;
            }

            InputStream in;

            String name;
        };
        foh.initialize(selectedFile);
        return foh;
    }

    public FileSaverHandler save(String defaultDirectory, String defaultFile, ExtensionFilter filter, String removeExtension,
            String addExtension) throws FileNotFoundException
    {
        fileChooser.resetChoosableFileFilters();
        fileChooser.setFileFilter(filter);
        if (defaultDirectory == null)
        {
            fileChooser.setCurrentDirectory(new File("."));
        }
        if (defaultDirectory != null)
        {
            fileChooser.setCurrentDirectory(new File(defaultDirectory));
        }
        if (defaultFile == null)
        {
            fileChooser.setSelectedFile(new File(""));
        }
        if (defaultFile != null)
        {
            File f = new File(FileService.editExtension(defaultFile, removeExtension, addExtension));
            fileChooser.setSelectedFile(f);
        }
        int response = fileChooser.showSaveDialog(null);
        File selectedFile = null;
        if (response == JFileChooser.APPROVE_OPTION)
        {
            selectedFile = fileChooser.getSelectedFile();
            if (addExtension != null && selectedFile.getName().indexOf(".") < 0) // no extension supplied
            selectedFile = new File(selectedFile.getPath() + addExtension);
            if (selectedFile.exists())
            {
                ResourceBundle resourceBundle = ResourceBundle.getBundle(ResourceBundleConstant.MENU_STRINGS, Locale.getDefault());
                String message = resourceBundle.getString("dialog.overwrite.ok");
                String title = resourceBundle.getString("dialog.overwrite.title");
                ImageIcon icon = new ImageIcon(this.getClass().getResource(resourceBundle.getString("dialog.overwrite.icon")));
                JOptionPane optionPane = new JOptionPane();
                optionPane.setMessage(message);
                optionPane.setOptionType(JOptionPane.YES_NO_OPTION);
                optionPane.setIcon(icon);
                DialogFactory.getInstance().showDialog(optionPane, title, true);

                int result = JOptionPane.NO_OPTION;
                if (!JOptionPane.UNINITIALIZED_VALUE.equals(optionPane.getValue()))
                {
                    result = ((Integer) optionPane.getValue()).intValue();
                }

                if (result == JOptionPane.NO_OPTION)
                {
                    selectedFile = null;
                }
            }

        }
        FileSaverHandler fsh = new FileSaverHandler()
        {

            public void initialize(File f) throws FileNotFoundException
            {
                if (f != null)
                {
                    name = f.getPath();
                    out = new FileOutputStream(f);
                }
            }

            public String getName()
            {
                return name;
            }

            public OutputStream getOutputStream()
            {
                return out;
            }

            String name;

            OutputStream out;
        };
        fsh.initialize(selectedFile);
        return fsh;
    }
}