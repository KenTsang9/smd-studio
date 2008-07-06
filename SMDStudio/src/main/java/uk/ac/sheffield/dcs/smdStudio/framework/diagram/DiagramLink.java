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

package uk.ac.sheffield.dcs.smdStudio.framework.diagram;

import java.net.URL;

/**
 * This class is a link to a physical file. It is used to perform links between diagrams. Tested successfully in applet mode
 * 
 * @author Alexandre de Pellegrin
 * 
 */
public class DiagramLink
{

    /**
     * The file
     */
    private URL url;

    /**
     * Flag to indicate if file needs to be opened
     */
    private Boolean openFlag = new Boolean(false);

    public DiagramLink()
    {
        super();
    }

    /**
     * Get linked file url
     * 
     * @return
     */
    public URL getURL()
    {
        return url;
    }

    /**
     * Set file a link
     * 
     * @param path
     */
    public void setURL(URL url)
    {
        this.url = url;
    }

    /**
     * Return true if file needs to be opened
     * 
     * @return
     */
    public Boolean getOpenFlag()
    {
        return this.openFlag;
    }

    /**
     * Set flag to indicate if file needs to be opened
     * 
     * @param openFlag
     */
    public void setOpenFlag(Boolean flag)
    {
        this.openFlag = flag;
    }

}
