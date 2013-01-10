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
package org.eclipse.edt.ide.rui.visualeditor.internal.properties;

import org.eclipse.edt.ide.rui.server.EvEditorProvider;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetEventDescriptor;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPart;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPropertyDescriptor;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPropertyValue;


public interface IEvPropertySheetPageAdapter {

	/**
	 * Creates a new event handling function in the EGL source. 
	 */
	public void createEventHandlingFunction( String strFunctionName );
	
	/**
	 * Return the editor provider.
	 */
	public EvEditorProvider getEditorProvider();
	
	/**
	 * Returns the function name value for a given event name 
	 */
	public WidgetPropertyValue getEventValue( WidgetPart widget, String strEventName );
	
	/**
	 * Returns the property value for a given property name 
	 */
	public WidgetPropertyValue getPropertyValue( WidgetPart widget, String strPropertyName, String strPropertyType );

	/**
	 * Returns the layout property value for a given property name 
	 */
	public WidgetPropertyValue getLayoutPropertyValue( WidgetPart widget, String strPropertyName, String strPropertyType );

	
	/**
	 * Returns a list of event handling function names. 
	 */
	public String[] getEventHandlingFunctionNames();
	
	/**
	 * Returns the currently selected widget. 
	 */
	public WidgetPart getWidgetSelected();
	
	/**
	 * Called by the property sheet when an event value has changed. 
	 */
	public void eventChanged( WidgetPart widget, WidgetEventDescriptor descriptor, String strValueOld, String strValueNew, boolean refresh );
	
	/**
	 * Called by the property sheet when a property value has changed. 
	 */
	public void propertyChanged( WidgetPart widget, WidgetPropertyDescriptor descriptor, WidgetPropertyValue valueOld, WidgetPropertyValue valueNew );
	
	/**
	 * The codes from offset to (offset + length) are selected and the source tab is shown. 
	 */
	public void selectAndRevealRange( int offset, int length );

	
	// temporary
	public String getDocumentStatement( WidgetPart widgetPart );
}
