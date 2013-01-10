/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.visualeditor.internal.widget;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Each descriptor group has a list of widget descriptors. 
 */
public interface IWidgetDescriptorRegistry {

	/**
	 * Returns the descriptor for a widget given its package name and type
	 */
	public WidgetDescriptor getDescriptor( String strPackageName, String strWidgetType );
	
	/**
	 * Returns the descriptor for a widget project name and widget type separated by EvConstants.WIDGET_ID_SEPARATOR
	 */
	public WidgetDescriptor getDescriptor( String strWidgetID );
	
	/**
	 * Returns the dataTemplates for data mapping
	 */
	public List<DataTemplate> getMappingDescriptorDataTemplates(String purpose, String eglDataType, Set<String> eglDataTypeDetails, boolean forArray, boolean isContainer);
	
	/**
	 * Returns the set of descriptor groups.
	 */
	public Iterator getDescriptorGroups();

}
