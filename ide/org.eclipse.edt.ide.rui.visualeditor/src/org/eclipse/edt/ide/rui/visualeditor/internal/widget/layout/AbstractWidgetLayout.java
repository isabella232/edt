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
package org.eclipse.edt.ide.rui.visualeditor.internal.widget.layout;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.ide.rui.server.EvEditorProvider;
import org.eclipse.edt.ide.rui.server.PropertyValue;
import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvDesignOverlayDropLocation;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetDescriptor;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetDescriptorRegistry;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetManager;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Rectangle;


public abstract class AbstractWidgetLayout implements WidgetLayout {

	protected final static int DROP_THICKNESS = 5;
	
	protected ScrolledComposite parentComposite;
	protected WidgetPart widgetRoot;
	protected WidgetPart _widgetDragging;
	protected WidgetManager widgetManager;
	protected WidgetDescriptorRegistry widgetDescriptorRegistry;
	protected EvEditorProvider evEditorProvider;
	
	public void initialize(ScrolledComposite parentComposite, WidgetPart widget, WidgetPart _widgetDragging, WidgetManager widgetManager, WidgetDescriptorRegistry widgetDescriptorRegistry, EvEditorProvider evEditorProvider) {
		this.parentComposite = parentComposite;
		this.widgetRoot = widget;
		this._widgetDragging = _widgetDragging;
		this.widgetManager = widgetManager;
		this.widgetDescriptorRegistry = widgetDescriptorRegistry;
		this.evEditorProvider = evEditorProvider;
	}
	
	protected Iterator getWidgets() {
		return widgetManager.getWidgetList().iterator();
	}
	
	/**
	 * Creates drop locations for a widget container, such as a Box. 
	 */
	protected void setupDropLocationsForChildren(Collection listDropLocations ) {

		List listChildren = widgetRoot.getChildren();
		
		for( int i = 0; i < listChildren.size(); i++ ) {
			WidgetPart widget = (WidgetPart)listChildren.get( i );

			// A dragged widget cannot become a sibling of its children
			//---------------------------------------------------------
			if( widget == _widgetDragging )
				return;
	
			// Determine if the parent widget can contain the widget being dragged
			//--------------------------------------------------------------------
			if( _widgetDragging != null && isContainable( _widgetDragging.getTypeName(), widget.getTypeName() ) == false )
				return;
	
			WidgetLayout handlerLayout = WidgetLayoutRegistry.getInstance().getWidgetLayout( WidgetLayoutRegistry.getLayoutName(widget), isContainer(widget) );
			if ( handlerLayout != null ) {
				handlerLayout.initialize( parentComposite, widget, _widgetDragging, widgetManager, widgetDescriptorRegistry, evEditorProvider );
				handlerLayout.setupDropLocations(listDropLocations);
			}
		}
	}
	
	/**
	 * Returns whether a widget type can be contained (parented) by another widget type.
	 */
	protected boolean isContainable( String strType, String strContainerType ){
		if( strType.equalsIgnoreCase( "treenode" ) == true ){
			if( strContainerType.equalsIgnoreCase( "treenode" ) == false && strContainerType.equalsIgnoreCase( "tree" ) == false )
				return false;
		}
		
		else if( strContainerType.equalsIgnoreCase( "treenode" ) == true || strContainerType.equalsIgnoreCase( "tree" ) == true )
			return false;
		
		return true;
	}
	
	/**
	 * Returns whether a widget type is a container.
	 */
	protected boolean isContainer( WidgetPart widget ){
		// Legacy support for widgets prior to 1.0.2
		if( widget.getTypeName().equalsIgnoreCase( "div" ) || widget.getTypeName().equalsIgnoreCase( "span" ) || widget.getTypeName().equalsIgnoreCase( "grouping" ) || widget.getTypeName().equalsIgnoreCase( "treenode" ) || widget.getTypeName().equalsIgnoreCase( "tree" ) ){
			return true;
		}
		
		WidgetDescriptor descriptor = widgetDescriptorRegistry.getDescriptor( widget.getTypeID() );
		if(descriptor != null){
			return descriptor.isContainer();
		}
		return false;
	}
	
	public PropertyValue getPropertyValue( String strPropertyName, String strPropertyType ) {
		return evEditorProvider.getPropertyValue( widgetRoot.getStatementOffset(), widgetRoot.getStatementLength(), strPropertyName, strPropertyType );
	}
	
	/**
	 * Sets up a single drop location for the interior of a container such as a Box
	 */
	protected void setupDropLocationsForInsideContainer( WidgetPart widget, Collection listDropLocations ) {
		// Disallow dropping onto a RUI Handler
		//-------------------------------------
		if( widget.getStatementLength() <= 0 )
			return;

		Rectangle rectBounds = widget.getBounds();

		EvDesignOverlayDropLocation location = new EvDesignOverlayDropLocation();
		location.setBounds( rectBounds.x + DROP_THICKNESS, rectBounds.y + DROP_THICKNESS, rectBounds.width - 2 * DROP_THICKNESS, rectBounds.height - 2 * DROP_THICKNESS );
		location.iIndex = 0;
		location.iLocation = SWT.CENTER;
		location.widgetParent = widget;
		listDropLocations.add( location );
		return;
	}
	
	public void updateLayoutData(EvEditorProvider editorProvider, int statementOffset, WidgetPart widget, Object layoutData, PropertyValue oldLayoutData, String layoutDataTemplte ) {
		// do nothing for default
	}
	
	public String processNewLayoutData( Object layoutData, String template ) {
		// do nothing for default
		return null;
	}

	public String[] getLayoutDataQualifiedName() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public  String[] getLayoutWidgetQualifiedName() {
		return null;
	}
	
	public List getInnerRectanglesForPaintingWidget(WidgetPart widget) {
		return null;
	}
}
