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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.jnlp.FileContents;
import javax.jnlp.FileOpenService;
import javax.jnlp.FileSaveService;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;

/**
 * This class provides a FileService for Java Web Start. Note that file saving is strange under Web Start. You first save the data,
 * and the dialog is only displayed when the output stream is closed. Therefore, the file name is not available until after the file
 * has been written.
 */
public class JNLPFileChooserService implements FileChooserService
{

    public JNLPFileChooserService()
    {
        try
        {
            openService = (FileOpenService) ServiceManager.lookup("javax.jnlp.FileOpenService");
            saveService = (FileSaveService) ServiceManager.lookup("javax.jnlp.FileSaveService");
        }
        catch (UnavailableServiceException ex)
        {
            ex.printStackTrace();
        }
    }

    public boolean isWebStart()
    {
        return true;
    }

    public FileOpenerHandler open(String defaultDirectory, String defaultFile, ExtensionFilter[] filters) throws IOException
    {
        if (defaultDirectory == null) defaultDirectory = ".";
        ArrayList<String> fileExtensions = new ArrayList<String>();
        for (int i = 0; i < filters.length; i++)
        {
            ExtensionFilter aFilter = filters[i];
            String[] filterExtensions = aFilter.getExtensions();
            for (int j = 0; j < filterExtensions.length; j++)
            {
                fileExtensions.add(filterExtensions[j]);
            }
        }
        String[] fileExtensionsStrings = (String[]) fileExtensions.toArray(new String[fileExtensions.size()]);
        final FileContents contents = openService.openFileDialog(defaultDirectory, fileExtensionsStrings);
        return new FileOpenerHandler()
        {
            public InputStream getInputStream() throws IOException
            {
                return contents.getInputStream();
            }

            public String getName() throws IOException
            {
                return contents.getName();
            }

            public void initialize(File selectedFile) throws FileNotFoundException
            {
                // TODO Auto-generated method stub

            }
        };
    }

    public FileSaverHandler save(final String defaultDirectory, final String defaultFile, final ExtensionFilter filter,
            final String removeExtension, final String addExtension) throws IOException
    {
        return new FileSaverHandler()
        {
            private ByteArrayOutputStream bout = new ByteArrayOutputStream();

            private FileContents contents;

            private OutputStream out = new FilterOutputStream(bout)
            {
                public void close()
                {
                    try
                    {
                        super.close();
                        showDialog();
                    }
                    catch (IOException ex)
                    {
                        ex.printStackTrace();
                    }
                }
            };

            public String getName() throws IOException
            {
                if (contents == null) return null;
                else return contents.getName();
            }

            public OutputStream getOutputStream() throws IOException
            {
                return out;
            }

            public void showDialog() throws IOException
            {
                contents = saveService.saveFileDialog(defaultDirectory, filter.getExtensions(), new ByteArrayInputStream(bout
                        .toByteArray()), FileService.editExtension(defaultFile, removeExtension, addExtension));
            }

            public void initialize(File selectedFile) throws FileNotFoundException
            {
                // TODO Auto-generated method stub

            }
        };
    }

    private FileOpenService openService;

    private FileSaveService saveService;
}
