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

package uk.ac.sheffield.dcs.smdStudio.product.diagram.smd;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

/**
 * The bean info for the SimpleModuleNode type.
 */
public class SimpleModuleNodeBeanInfo extends SimpleBeanInfo {
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.beans.BeanInfo#getPropertyDescriptors()
	 */
	public PropertyDescriptor[] getPropertyDescriptors() {
		try {
			PropertyDescriptor moduleCostDescriptor = new PropertyDescriptor(
					"moduleCost", SimpleModuleNode.class, "getCost", "setCost");
			moduleCostDescriptor.setValue("priority", new Integer(1));
			PropertyDescriptor nameDescriptor = new PropertyDescriptor("name",
					SimpleModuleNode.class);
			nameDescriptor.setValue("priority", new Integer(2));
			PropertyDescriptor descriptionDescriptor = new PropertyDescriptor(
					"description", SimpleModuleNode.class);
			descriptionDescriptor.setValue("priority", new Integer(3));
			PropertyDescriptor idDescriptor = new PropertyDescriptor("id",
					SimpleModuleNode.class, "getSMDId", "setSMDId");
			idDescriptor.setValue("priority", new Integer(4));
			idDescriptor.setHidden(true);
			return new PropertyDescriptor[] {moduleCostDescriptor, nameDescriptor,
					descriptionDescriptor,idDescriptor, };
		} catch (IntrospectionException exception) {
			return null;
		}
	}
}
