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

import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import org.eclipse.edt.ide.rui.utils.Util;
import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvConstants;
import org.eclipse.edt.ide.rui.visualeditor.internal.util.BidiUtils;
import org.eclipse.jface.resource.ImageDescriptor;


public class WidgetDescriptor implements VEPropertyContainer {
	public final static int EXCLUDE_ALL = 1;
	public final static int EXCLUDE_ALL_EXCEPT = 2;
	public final static int INCLUDE_ALL = 3;
	public final static int INCLUDE_ALL_EXCEPT = 4;
	
	public boolean			_bContainer					= false;		// Widget may parent child widgets
	public ImageDescriptor	_iconLarge					= null;
	public ImageDescriptor	_iconSmall					= null;
	public String			_strDescription				= null;
	public String			_strProvider				= null;
	public String			_strGroup					= null;
	public String			_strLabel					= null;
	public String			_strPackage					= null;
	public String			_strProjectName				= null;
	public String			_strTemplate				= null;
	public String			_strDataTemplate			= null;
	public String			_strDataFunctionTemplate	= null;
	public List<DataTemplate> _DataTemplates		    = null;
	public String			_strType					= null; 		// Example: "Button"
	public String           _strChildLayoutDataTemplate = null;
	public int 			_eventFilterType			= INCLUDE_ALL;
	public int 			_propertyFilterType			= INCLUDE_ALL;
	public Vector			_vectorEventDescriptors		= new Vector();
	public Vector			_vectorPropertyDescriptors	= new Vector();
	public Vector<String>	_propertyFilter				= new Vector<String>();
	public Vector			_eventFilter				= new Vector();
	public WidgetLayoutDescriptor _layoutDescriptor		= null;
	/**
	 * 
	 */
	public void addEventDescriptor( WidgetEventDescriptor eventDescriptor ) {
		_vectorEventDescriptors.add( eventDescriptor );
	}

	/**
	 * 
	 */
	public void addPropertyDescriptor( WidgetPropertyDescriptor propertyDescriptor ) {
		_vectorPropertyDescriptors.add( 0, propertyDescriptor );
	}

	/**
	 * 
	 */
	public String getDescription() {
		return _strDescription;
	}
	
	/**
	 * 
	 */
	public String getProvider() {
		return _strProvider;
	}

	/**
	 * Returns an event descriptor given its ID.
	 */
	public WidgetEventDescriptor getEventDescriptor( String strEventID ) {
		for( int i = 0; i < _vectorEventDescriptors.size(); ++i ) {
			WidgetEventDescriptor eventDescriptor = (WidgetEventDescriptor)_vectorEventDescriptors.elementAt( i );

			if( eventDescriptor.getLabel().equalsIgnoreCase( strEventID ) == true )
				return eventDescriptor;
		}

		return null;
	}
	
	/**
	 * Returns an array of event descriptors for this widget type.
	 * @param filterType:  1 - all events, included the excluded ones
	 *                      2 - excluded the events which is specified by Widget author.
	 */

	public WidgetEventDescriptor[] getEventDescriptors( int filterType ) {
		WidgetEventDescriptor[] descriptors = new WidgetEventDescriptor[_vectorEventDescriptors.size()];
		Enumeration enumDescriptors = _vectorEventDescriptors.elements();

		int i = 0;
		while( enumDescriptors.hasMoreElements() ) {
			WidgetEventDescriptor event = (WidgetEventDescriptor)enumDescriptors.nextElement();
			if ( filterType == 1 || ( filterType == 2 && !event._excluded ) ) {
				descriptors[ i++ ] = event;
			}
		}
		WidgetEventDescriptor[] event_descriptors = new WidgetEventDescriptor[i];
		System.arraycopy( descriptors, 0, event_descriptors, 0, i );
		return event_descriptors;
	}

	/**
	 * Returns the group (or category) that this widget belongs to.  The Palette view will arrange the widget by group.
	 */
	public String getGroup() {
		return _strGroup;
	}

	/**
	 * 
	 */
	public ImageDescriptor getIconLarge() {
		return (_iconLarge == null ? _iconSmall : _iconLarge );
	}

	/**
	 * 
	 */
	public ImageDescriptor getIconSmall() {
		return _iconSmall;
	}

	/**
	 * The ID is a combined widget project name, separator, package, separator, and widget type, such as:
	 * <ul>
	 * <li>com.ibm.egl.rui_1.0.0@@org.eclipse.edt.rui.widgets@@Image</li>
	 * </ul>
	 */
	public String getID(){
		return Util.combineWidgetId(_strProjectName, _strPackage, _strType);
	}
	
	/**
	 * 
	 */
	public String getLabel() {
		return _strLabel;
	}
	
	/**
	 * Returns the package of the widget.
	 */
	public String getPackage(){
		return _strPackage;
	}

	/**
	 * Returns the project name that this widget is defined in.
	 */
	public String getProjectName(){
		return _strProjectName;
	}
	
	/**
	 * Returns the property descriptor with the given ID for this widget type.
	 */
	public WidgetPropertyDescriptor getPropertyDescriptor( String strID ) {

		for( int i = 0; i < _vectorPropertyDescriptors.size(); ++i ) {
			WidgetPropertyDescriptor descriptor = (WidgetPropertyDescriptor)_vectorPropertyDescriptors.elementAt( i );
			if( descriptor.getID().equals( strID ) == true )
				return descriptor;
		}

		return null;
	}
	
	/**
	 * Returns an array of property descriptors for this widget type.
	 * @param filterType:  1 - all properties, included the excluded ones
	 *                      2 - excluded the properties which is specified by Widget author.
	 */
	public WidgetPropertyDescriptor[] getPropertyDescriptors( int filterType ) {
		boolean isBidi = BidiUtils.isBidi(); // IBMBIDI Append
		Enumeration enumDescriptors = _vectorPropertyDescriptors.elements();

		WidgetPropertyDescriptor[] descriptors = new WidgetPropertyDescriptor[_vectorPropertyDescriptors.size()];

		// Defined properties
		//-------------------
		enumDescriptors = _vectorPropertyDescriptors.elements();

		// IBMBIDI Change Start
		int i = 0;

		while( enumDescriptors.hasMoreElements() ) {
			WidgetPropertyDescriptor element = (WidgetPropertyDescriptor)enumDescriptors.nextElement();

			if ( filterType == 1 || ( filterType == 2 && !element._excluded ) ) {
				if ((!isBidi && !( "Bidi".equals( element._strCategory ) )) ||
					(isBidi && ( "Bidi".equals( element._strCategory ) && shouldAddBidiElement(element)) ||
							  !( "Bidi".equals( element._strCategory ))))
					descriptors[ i++ ] = element;
			}
		}
/*	IBMBIDI Remove
 * 	    if( isBidi )
			return descriptors;
*/
		WidgetPropertyDescriptor[] nonbidi_descriptors = new WidgetPropertyDescriptor[i];
		System.arraycopy( descriptors, 0, nonbidi_descriptors, 0, i );

		return nonbidi_descriptors;
		// IBMBIDI Change End
	}

	private boolean shouldAddBidiElement(WidgetPropertyDescriptor element) {
		// IBMBIDI Ira - temporary remove symSwap & numSwap combos from Properties view
		//ToDo - add bidi support for those two properties and then remove this method completely.
		if (EvConstants.FIELD_NAME_BIDI_SYM_SWAPPING.equals(element.getID()) ||
			EvConstants.FIELD_NAME_BIDI_NUM_SWAPPING.equals(element.getID()))
			return false;
		return true;
	}

	/**
	 * Returns the source code template to be used when creating an instance of this widget type.
	 */
	public String getTemplate() {
		return _strTemplate;
	}
	
	/**
	 * Returns the widget source code template to be used when creating an instance of this widget type from Data View.
	 */
	public String getDataTemplate() {
		return _strDataTemplate;
	}
	
	/**
	 * Returns the function source code template to be used when creating an instance of this widget type from Data View.
	 */
	public String getDataFunctionTemplate() {
		return _strDataFunctionTemplate;
	}

	/**
	 * 
	 */
	public String getType() {
		return _strType;
	}

	/**
	 * Returns whether this widget may contain (as well as parent) children.
	 */
	public boolean isContainer() {
		return _bContainer;
	}

	/**
	 * Sets whether this widget may contain (as well as parent) children.
	 */
	public void setContainer( boolean bContainer ) {
		_bContainer = bContainer;
	}

	/**
	 * 
	 */
	public void setDescription( String strDescription ) {
		_strDescription = strDescription;
	}
	
	/**
	 * 
	 */
	public void setProvider( String strProvider ) {
		_strProvider = strProvider;
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
	public void setLabel( String strLabel ) {
		if( strLabel != null )
			_strLabel = strLabel;
	}

	/**
	 * Sets the package name
	 */
	public void setPackage( String strPackage ){
		_strPackage = strPackage;
	}
	
	/**
	 * Sets the project name that this widget type is defined in.
	 */
	public void setProjectName( String strProjectName ){
		_strProjectName = strProjectName;
	}
	
	/**
	 * Sets the source code template to be used when creating an instance of this widget type.
	 */
	public void setTemplate( String strTemplate ) {
		_strTemplate = strTemplate;
	}
	
	/**
	 * Sets the widget source code template to be used when creating an instance of this widget type from Data View.
	 */
	public void setDataTemplate(String strDataTemplate){
		_strDataTemplate = strDataTemplate;
	}
	
	/**
	 * Sets the function source code template to be used when creating an instance of this widget type from Data View.
	 */
	public void setDataFunctionTemplate(String strDataFunctionTemplate){
		_strDataFunctionTemplate = strDataFunctionTemplate;
	}

	/**
	 * 
	 */
	public void setType( String strType ) {
		_strType = strType;
	}

	/**
	 * 
	 */
	public String toString() {
		return getID();
	}

	public List<DataTemplate> getDataTemplates() {
		return _DataTemplates;
	}
	
	public String getChildLayoutDataTemplate() {
		return _strChildLayoutDataTemplate;
	}
	
	public void setChildLayoutDataTemplate(String childLayoutDataTemplate) {
		this._strChildLayoutDataTemplate = childLayoutDataTemplate;
	}
}
