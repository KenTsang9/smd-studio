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

import java.io.IOException;

public interface FileChooserService
{

    /**
     * Tests whether the service is provided by WebStart
     * 
     * @return true if this service is provided by WebStart
     */
    public boolean isWebStart();

    /**
     * Gets an Open object that encapsulates the stream and name of the file that the user selected
     * 
     * @param defaultDirectory the default directory for the file chooser
     * @param defaultFile the default file for the file chooser
     * @param extensions the extension filter
     * @return the Open object for the selected file
     * @throws IOException
     */
    public FileOpenerHandler open(String defaultDirectory, String defaultFile, ExtensionFilter[] extensions) throws IOException;

    /**
     * Gets a Save object that encapsulates the stream and name of the file that the user selected (or will select)
     * 
     * @param defaultDirectory the default directory for the file chooser
     * @param defaultFile the default file for the file chooser
     * @param extensions the extension filter
     * @param removeExtension the extension to remove from the default file name
     * @param addExtension the extension to add to the file name
     * @return the Save object for the selected file
     * @throws IOException
     */
    public FileSaverHandler save(String defaultDirectory, String defaultFile, ExtensionFilter extensions, String removeExtension,
            String addExtension) throws IOException;

}
