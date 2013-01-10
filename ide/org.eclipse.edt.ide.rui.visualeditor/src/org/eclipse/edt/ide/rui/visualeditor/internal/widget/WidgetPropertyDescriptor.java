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

import org.eclipse.core.runtime.IConfigurationElement;

public class WidgetPropertyDescriptor {
	
	public final static int WIDGET_PROPERTY                 = 1;
	public final static int LAYOUT_PROPERTY                 = 2;
	public boolean					_bMultiple				= false;
	public IConfigurationElement	_element				= null;
	protected ArrayList				_listChoices			= null;
	public String					_strCategory			= null;
	public String					_strDefault				= null;
	public String					_strID					= null;
	public String					_strLabel				= null;
	public String					_strPropertyEditorID	= null;
	public String					_strType				= null;
	public boolean					_excluded				= false;
	public int                      _propertyContainerType  = WIDGET_PROPERTY;

	public void addChoice( WidgetPropertyChoice choice ) {
		if( _listChoices == null )
			_listChoices = new ArrayList();

		_listChoices.add( choice );
	}

	public String getCategory() {
		return _strCategory;
	}

	public ArrayList getChoices() {
		if( _listChoices == null ) {
			return new ArrayList();
		}

		return _listChoices;
		//		Object[] objArray = _listChoices.toArray();
		//		String[] straChoices = new String[objArray.length];
		//		System.arraycopy( objArray, 0, straChoices, 0, objArray.length );
		//		return straChoices;
	}

	public IConfigurationElement getConfigurationElement() {
		return _element;
	}

	public String getDefault() {
		return _strDefault;
	}

	public String getID() {
		return _strID;
	}

	public String getLabel() {
		return _strLabel;
	}

	public String getPropertyEditorID() {
		return _strPropertyEditorID;
	}

	public String getType() {
		return _strType;
	}

	public boolean isMultiple() {
		return _bMultiple;
	}
	
	public boolean isExcluded() {
		return _excluded;
	}
	
	public int getPropertyContainerType() {
		return _propertyContainerType;
	}
}
