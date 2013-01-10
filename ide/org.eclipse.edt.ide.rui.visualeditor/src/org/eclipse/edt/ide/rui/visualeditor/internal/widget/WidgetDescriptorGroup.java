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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * This class represents a group of part descriptors.  A group has a name and icon
 * and a list of part descriptors that belong to the group.
 * Instances of the class are created by the part descriptor registry.
 */
public class WidgetDescriptorGroup {
	protected ImageDescriptor				_iconLarge				= null;
	protected ImageDescriptor				_iconSmall				= null;
	protected ArrayList						_listWidgetDescriptors	= new ArrayList();
	protected String						_strID					= null;
	protected String						_strName				= null;

	protected class WidgetComparator implements Comparator {

		public int compare(Object widget1, Object widget2) {
			return String.CASE_INSENSITIVE_ORDER.compare( ((WidgetDescriptor)widget1).getLabel(), ((WidgetDescriptor)widget2).getLabel() );
		}
	}
	
	/**
	 * 
	 */
	public void addWidgetDescriptor( WidgetDescriptor descriptor ) {
		for(int i=0; i<_listWidgetDescriptors.size(); i++){
			WidgetDescriptor widgetDescriptor = (WidgetDescriptor)_listWidgetDescriptors.get(i);
			if((widgetDescriptor.getProvider() != null && descriptor.getProvider() != null) 
					&& widgetDescriptor.getProvider().equalsIgnoreCase(descriptor.getProvider()) 
					&& widgetDescriptor.getLabel().equalsIgnoreCase(descriptor.getLabel())){
				return;
			}
		}
		_listWidgetDescriptors.add( descriptor );
	}
	
	/**
	 * Sort the widgets in this group.
	 */
	public void sortWidgets() {
		Collections.sort( _listWidgetDescriptors, new WidgetComparator() );
	}

	/**
	 * 
	 */
	public ImageDescriptor getIconLarge() {
		return _iconLarge;
	}

	/**
	 * 
	 */
	public ImageDescriptor getIconSmall() {
		return _iconSmall;
	}

	/**
	 * 
	 */
	public String getID() {
		return _strID;
	}

	/**
	 * 
	 */
	public String getName() {
		return _strName;
	}

	/**
	 * 
	 */
	public ArrayList getWidgetDescriptors() {
		return _listWidgetDescriptors;
	}

	/**
	 * 
	 */
	public void setIconLarge( ImageDescriptor iconLarge ) {
		_iconLarge = iconLarge;
	}

	/**
	 * 
	 */
	public void setIconSmall( ImageDescriptor iconSmall ) {
		_iconSmall = iconSmall;
	}

	/**
	 * 
	 */
	public void setID( String strID ) {
		_strID = strID;
	}

	/**
	 * 
	 */
	public void setName( String strName ) {
		_strName = strName;
	}
}
