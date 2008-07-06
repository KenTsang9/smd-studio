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

public class FileChooserServiceFactory
{

    /**
     * Gets a service that is appropriate for the mode in which this program works.
     * 
     * @return a service for local dialogs or for Java Web Start
     */
    public static synchronized FileChooserService getInstance()
    {
        if (FileChooserServiceFactory.fileChooserService != null)
        {
            return FileChooserServiceFactory.fileChooserService;
        }
        try
        {
            FileChooserServiceFactory.fileChooserService = new JFileChooserService();
            return FileChooserServiceFactory.fileChooserService;
        }
        catch (SecurityException exception)
        {
            // that happens when we run under Web Start
        }
        try
        {
            // we load this lazily so that the JAR can load without WebStart
            FileChooserServiceFactory.fileChooserService = (FileChooserService) Class.forName(
                    "uk.ac.sheffield.dcs.smdStudio.framework.file.JNLPFileChooserService").newInstance();
            return FileChooserServiceFactory.fileChooserService;
        }
        catch (Throwable exception)
        {
            // that happens when we are an applet
        }
        return null;
    }

    /**
     * FileChooser unique instance
     */
    private static FileChooserService fileChooserService;

}
