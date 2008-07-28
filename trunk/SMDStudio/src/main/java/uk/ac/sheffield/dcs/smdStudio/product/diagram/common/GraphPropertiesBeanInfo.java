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

package uk.ac.sheffield.dcs.smdStudio.product.diagram.common;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

/**
 * The bean info for the SimpleModuleNode type.
 */
public class GraphPropertiesBeanInfo extends SimpleBeanInfo {
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.beans.BeanInfo#getPropertyDescriptors()
	 */
	public PropertyDescriptor[] getPropertyDescriptors() {
		try {
			PropertyDescriptor trainingCostDescriptor = new PropertyDescriptor(
					"trainingCost", GraphProperties.class);
			trainingCostDescriptor.setHidden(true);
			PropertyDescriptor teamQualityDescriptor = new PropertyDescriptor(
					"teamQuality", GraphProperties.class);
			teamQualityDescriptor.setHidden(true);
			return new PropertyDescriptor[] { trainingCostDescriptor,
					teamQualityDescriptor, };
		} catch (IntrospectionException exception) {
			return null;
		}
	}
}
