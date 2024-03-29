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

import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Expression;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class CustomPersistenceDelegate extends DefaultPersistenceDelegate
{

    protected Expression instantiate(Object oldInstance, Encoder out)
    {
        try
        {
            Class<?> cl = oldInstance.getClass();
            Field[] fields = cl.getFields();
            for (int i = 0; i < fields.length; i++)
            {
                if (Modifier.isStatic(fields[i].getModifiers()) && fields[i].get(null) == oldInstance)
                {
                    return new Expression(fields[i], "get", new Object[]
                    {
                        null
                    });
                }
            }
        }
        catch (IllegalAccessException ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    protected boolean mutatesTo(Object oldInstance, Object newInstance)
    {
        return oldInstance == newInstance;
    }

}
