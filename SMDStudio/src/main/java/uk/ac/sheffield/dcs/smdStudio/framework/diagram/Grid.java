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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import uk.ac.sheffield.dcs.smdStudio.framework.gui.theme.ThemeManager;


/**
 * A grid to which points and rectangles can be "snapped". The snapping operation moves a point to the nearest grid point.
 */
public class Grid
{
    /**
     * Constructs a grid with no grid points.
     */
    public Grid()
    {
        setGrid(0, 0);
    }

    /**
     * Sets the grid point distances in x- and y-direction
     * 
     * @param x the grid point distance in x-direction
     * @param y the grid point distance in y-direction
     */
    public void setGrid(double x, double y)
    {
        gridx = x;
        gridy = y;
    }

    /**
     * Draws this grid inside a rectangle.
     * 
     * @param g2 the graphics context
     * @param bounds the bounding rectangle
     */
    public void draw(Graphics2D g2, Rectangle2D bounds)
    {
        if (gridx == 0 || gridy == 0) return;
        Color oldColor = g2.getColor();
        g2.setColor(ThemeManager.getInstance().getTheme().getGRID_COLOR());
        Stroke oldStroke = g2.getStroke();
        for (double x = bounds.getX(); x < bounds.getMaxX(); x += gridx)
            g2.draw(new Line2D.Double(x, bounds.getY(), x, bounds.getMaxY()));
        for (double y = bounds.getY(); y < bounds.getMaxY(); y += gridy)
            g2.draw(new Line2D.Double(bounds.getX(), y, bounds.getMaxX(), y));
        g2.setStroke(oldStroke);
        g2.setColor(oldColor);
    }

    /**
     * Snaps a point to the nearest grid point
     * 
     * @param p the point to snap. After the call, the coordinates of p are changed so that p falls on the grid.
     */
    public void snap(Point2D p)
    {
        double x;
        if (gridx == 0) x = p.getX();
        else x = Math.round(p.getX() / gridx) * gridx;
        double y;
        if (gridy == 0) y = p.getY();
        else y = Math.round(p.getY() / gridy) * gridy;

        p.setLocation(x, y);
    }

    /**
     * Snaps a rectangle to the nearest grid points
     * 
     * @param r the rectangle to snap. After the call, the coordinates of r are changed so that all of its corners falls on the
     *            grid.
     * @return r (for convenience)
     */
    public Rectangle2D snap(Rectangle2D r)
    {
        double x;
        double w;
        if (gridx == 0)
        {
            x = r.getX();
            w = r.getWidth();
        }
        else
        {
            x = Math.round(r.getX() / gridx) * gridx;
            w = Math.ceil(r.getWidth() / (2 * gridx)) * (2 * gridx);
        }
        double y;
        double h;
        if (gridy == 0)
        {
            y = r.getY();
            h = r.getHeight();
        }
        else
        {
            y = Math.round(r.getY() / gridy) * gridy;
            h = Math.ceil(r.getHeight() / (2 * gridy)) * (2 * gridy);
        }

        r.setFrame(x, y, w, h);
        return r;
    }
    
    public double getX() { return gridx; }
    public double getY() { return gridy; }

    private double gridx;
    private double gridy;
}
