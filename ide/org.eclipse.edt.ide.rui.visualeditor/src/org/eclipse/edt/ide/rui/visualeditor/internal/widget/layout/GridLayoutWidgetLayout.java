/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.Status;
import org.eclipse.edt.ide.rui.server.EvEditorProvider;
import org.eclipse.edt.ide.rui.server.PropertyValue;
import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvDesignOverlayDropLocation;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetDescriptorRegistry;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetManager;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPart;
import org.eclipse.edt.ide.rui.visualeditor.plugin.Activator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Rectangle;


public class GridLayoutWidgetLayout extends AbstractWidgetLayout {
	
	private int[] cellInfo;
	private final static String[] LAYOUTDATA_NAME = {"org.eclipse.edt.rui.widgets", "GridLayoutData"};
	private final static String[] GRIDLAYOUT = {"org.eclipse.edt.rui.widgets", "GridLayout"}; 
	private final static String LayoutInnerRectanglesInfoCackeKey = "GridLayout-InnerRectangles-LayoutInfoCackeKey";
	private final static String LayoutCellsInfoCackeKey = "GridLayout-Cells-LayoutInfoCackeKey";
	private final static String LayoutLocaionsInfoCackeKey = "GridLayout-Locaions-LayoutInfoCackeKey";
	
	private static class GridLayoutWidgetLayoutData {
		int row;
		int column; 
		
		GridLayoutWidgetLayoutData(int row, int column ) {
			this.row = row;
			this.column = column;
		}
	}
	
	@Override
	public void initialize(ScrolledComposite parentComposite, WidgetPart widget, WidgetPart _widgetDragging, WidgetManager widgetManager, WidgetDescriptorRegistry widgetDescriptorRegistry, EvEditorProvider evEditorProvider) {
		super.initialize(parentComposite, widget, _widgetDragging, widgetManager, widgetDescriptorRegistry, evEditorProvider);
		cellInfo = (int[])widget.getFromCache( LayoutCellsInfoCackeKey );
		if ( cellInfo != null ) {
			return;
		}
		
		String extrainfo = widget.getExtraInfo( "LayoutInfo" );
		if ( extrainfo == null || extrainfo.length() == 0 ) {
			return;
		}
		String[] tdInfo = extrainfo.split(":");
		try {
			cellInfo = new int[tdInfo.length];
			for ( int i = 0; i < tdInfo.length; i ++ ) {
				cellInfo[i] = Integer.parseInt( tdInfo[i] );
			}
		} catch ( Exception e ) {
			
		}
		widget.putIntoCache( LayoutCellsInfoCackeKey, cellInfo );
	}
	
	public void setupDropLocations(Collection listDropLocations) {
		// Determine if the parent widget can contain the widget being dragged
		//--------------------------------------------------------------------
		if( _widgetDragging != null && isContainable( _widgetDragging.getTypeName(), widgetRoot.getTypeName() ) == false )
			return;

		// If there are no children, allow dropping onto this box
		//-------------------------------------------------------
		if( cellInfo == null || cellInfo.length == 0 ){
			setupDropLocationsForInsideContainer( widgetRoot, listDropLocations );
			return;
		}
		
		List locations = (List)widgetRoot.getFromCache( LayoutLocaionsInfoCackeKey );
		if ( locations != null ) {
			listDropLocations.addAll( locations );
			return;
		}
			
		locations = new ArrayList();
		for ( int i = 0; i < cellInfo.length/7; i ++ ) {
			int index = i * 7;
			if ( cellInfo[index + 6] == 0 ) {
				Rectangle rectDrop = new Rectangle( 0, 0, 0, 0 );
				rectDrop.x =  cellInfo[index+2];
				rectDrop.y =  cellInfo[index + 3];
				rectDrop.width = cellInfo[index + 4];
				if ( rectDrop.width < DROP_THICKNESS ) {
					rectDrop.width = DROP_THICKNESS;
				}
				rectDrop.height = cellInfo[index + 5];
				if ( rectDrop.height < DROP_THICKNESS ) {
					rectDrop.height = DROP_THICKNESS;
				}

				EvDesignOverlayDropLocation location = new EvDesignOverlayDropLocation();
				location.iIndex = 0;
				location.iLocation = SWT.CENTER;
				location.widgetParent = widgetRoot;
				location.selectedTransparent = 255;
				location.nonselectedTransparent = 255;
				location.lineStyle = SWT.LINE_DASH;
				location.lineWidth = 1;
				
				location.widgetLayoutData = new WidgetLayoutData( new GridLayoutWidgetLayoutData(cellInfo[index] + 1, cellInfo[index + 1] + 1), this );

				location.setBounds( rectDrop.x, rectDrop.y, rectDrop.width, rectDrop.height );

				locations.add( location );
			}
		}
		widgetRoot.putIntoCache( LayoutLocaionsInfoCackeKey, locations );
		listDropLocations.addAll( locations );
	}
	
	public List getInnerRectanglesForPaintingWidget(WidgetPart widget) {
		List extraRactangles = (List)widget.getFromCache( LayoutInnerRectanglesInfoCackeKey );
		if ( extraRactangles != null ) {
			return extraRactangles;
		}
		String extrainfo = widget.getExtraInfo( "LayoutInfo" );
		if ( extrainfo == null || extrainfo.length() == 0 ) {
			return null;
		}
		String[] tdInfo = extrainfo.split(":");
		try {
			cellInfo = new int[tdInfo.length];
			for ( int i = 0; i < tdInfo.length; i ++ ) {
				cellInfo[i] = Integer.parseInt( tdInfo[i] );
			}
		} catch ( Exception e ) {
			Activator.getDefault().getLog().log(new Status(Status.ERROR,Activator.PLUGIN_ID,"GridLayoutWidgetLayout: Error parse LayoutInfo",e));
		}
		extraRactangles = new ArrayList();

		for ( int i = 0; i < cellInfo.length/7; i ++ ) {
			int index = i * 7;
			Rectangle rect = new Rectangle( 0, 0, 0, 0 );
			rect.x =  cellInfo[index+2];
			rect.y =  cellInfo[index + 3];
			rect.width = cellInfo[index + 4];
			if ( rect.width < DROP_THICKNESS ) {
				rect.width = DROP_THICKNESS;
			}
			rect.height = cellInfo[index + 5];
			if ( rect.height < DROP_THICKNESS ) {
				rect.height = DROP_THICKNESS;
			}

			extraRactangles.add( rect );
		}
		widget.putIntoCache( LayoutInnerRectanglesInfoCackeKey, extraRactangles );
		return extraRactangles;

	}
	
	public String processNewLayoutData( Object layoutData, String template ) {
		if ( layoutData instanceof GridLayoutWidgetLayoutData ) {
			GridLayoutWidgetLayoutData data = (GridLayoutWidgetLayoutData)layoutData;
			template = template.replace( "${row}" ,  data.row + "" );
			template = template.replace( "${column}" ,  data.column + "" );

			return template;
		}
		return "";
	}
	
	public void updateLayoutData( EvEditorProvider editorProvider, int statementOffset, WidgetPart widget, Object layoutData, PropertyValue oldLayoutData, String layoutDataTemplte ) {
		if ( layoutData instanceof GridLayoutWidgetLayoutData ) {
			GridLayoutWidgetLayoutData data = (GridLayoutWidgetLayoutData)layoutData;
			
			if ( oldLayoutData == null || ((String)oldLayoutData.getValues().get(0)).toLowerCase().indexOf( "gridlayoutdata" ) < 0 ) {
				String layoutProperty = processNewLayoutData( layoutData, layoutDataTemplte );
				PropertyValue propertyValue = new PropertyValue( layoutProperty, true );
				editorProvider.setPropertyValue( statementOffset, widget.getStatementLength(), "layoutData", "", propertyValue );
			} else {
				int changed = 0;
				changed = editorProvider.setLayoutPropertyValue( statementOffset, widget.getStatementLength(), "row", "", new PropertyValue( String.valueOf( data.row ), true ) );
				changed += editorProvider.setLayoutPropertyValue( statementOffset, widget.getStatementLength() + changed, "column", "", new PropertyValue( String.valueOf( data.column ), true ) );
//				changed += editorProvider.setLayoutPropertyValue( statementOffset, widget.getStatementLength() + changed, "horizontalSpan", "", new PropertyValue( "", true ) );
//				changed += editorProvider.setLayoutPropertyValue( statementOffset, widget.getStatementLength() + changed, "verticalSpan", "", new PropertyValue( "", true ) );
			}
		}
	}

	public String[] getLayoutDataQualifiedName() {
		return LAYOUTDATA_NAME;
	}
	
	public String[] getLayoutWidgetQualifiedName() {
		return GRIDLAYOUT;
	}
}
