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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

/**
 * A FileSaverHandler object handles the stream and name of the file that the user selected for saving.
 */
public interface FileSaverHandler
{
    /**
     * Gets the name of the file that the user selected.
     * 
     * @return the file name, or null if the file dialog is only displayed when the output stream is closed.
     */
    String getName() throws IOException;

    /**
     * Gets the output stream corresponding to the user selection.
     * 
     * @return the output stream
     */
    OutputStream getOutputStream() throws IOException;

    /**
     * Initialise handler this the selected file
     * 
     * @param selectedFile
     * @throws FileNotFoundException
     */
    void initialize(File selectedFile) throws FileNotFoundException;
}