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

import java.util.Collection;
import java.util.List;

import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvDesignOverlayDropLocation;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;


public class DefaultWidgetLayout extends AbstractWidgetLayout {

	public void setupDropLocations(Collection listDropLocations) {

		// Determine if the parent widget can contain the widget being dragged
		//--------------------------------------------------------------------
		if( _widgetDragging != null && isContainable( _widgetDragging.getTypeName(), widgetRoot.getTypeName() ) == false )
			return;

		List listChildren = widgetRoot.getChildren();

		// If there are no children, allow dropping onto this box
		//-------------------------------------------------------
		if( listChildren.size() == 0 ){
			setupDropLocationsForInsideContainer( widgetRoot, listDropLocations );
			return;
		}

		// Left of first child
		//--------------------
		WidgetPart widgetChild = (WidgetPart)listChildren.get( 0 );
		
		// can not dnd a widget into a tree
		if("DojoTreeNode".equalsIgnoreCase(widgetChild.getTypeName())){
			return;
		}
		
		// can not dnd a widget beside a DojoContentPane
		if("DojoContentPane".equalsIgnoreCase(widgetChild.getTypeName())){
			return;
		}

		if( widgetChild != _widgetDragging ) {
			Rectangle rectChild = widgetChild.getBounds();			
			Rectangle rectParent = widgetRoot.getBounds();
			
			Rectangle rectDrop = new Rectangle( 0, 0, 0, 0 );
			rectDrop.x = rectParent.x + DROP_THICKNESS + 3;
			rectDrop.y = Math.max( rectChild.y, rectParent.y + DROP_THICKNESS );
			rectDrop.width = rectChild.x - rectParent.x - DROP_THICKNESS - 5;
			int iBottom = Math.min( rectChild.y + rectChild.height - 1, rectParent.y + rectParent.height - 1 - DROP_THICKNESS );
			rectDrop.height = iBottom - rectDrop.y;

			EvDesignOverlayDropLocation location = new EvDesignOverlayDropLocation();
			location.iIndex = 0;
			location.iLocation = SWT.LEFT;
			location.widgetParent = widgetRoot;

			if( rectDrop.width > DROP_THICKNESS && rectDrop.height > DROP_THICKNESS )
				location.setBounds( rectDrop.x, rectDrop.y, rectDrop.width, rectDrop.height );
			else
				location.setBounds( rectChild.x, rectChild.y, DROP_THICKNESS, rectChild.height - 1 );

			listDropLocations.add( location );
		}

		// Between children
		//-----------------
		for( int i = 0; i < listChildren.size() - 1; i++ ) {
			WidgetPart widgetA = (WidgetPart)listChildren.get( i );
			WidgetPart widgetB = (WidgetPart)listChildren.get( i + 1 );

			if( widgetA != _widgetDragging && widgetB != _widgetDragging ) {
				Rectangle rectA = widgetA.getBounds();
				Rectangle rectB = widgetB.getBounds();

				EvDesignOverlayDropLocation location = new EvDesignOverlayDropLocation();
				location.rectDrop.x = rectA.x + rectA.width;
				location.rectDrop.y = Math.min( rectA.y, rectB.y );
				location.rectDrop.width = rectB.x - ( rectA.x + rectA.width );
				location.rectDrop.width = Math.max( location.rectDrop.width, DROP_THICKNESS );
				location.rectDrop.height = Math.max( rectA.y + rectA.height - 1, rectB.y + rectB.height - 1 ) - location.rectDrop.y;
				location.rectDrop.height = Math.max( location.rectDrop.height, DROP_THICKNESS );
				location.iIndex = i + 1;
				location.iLocation = SWT.CENTER;
				location.widgetParent = widgetRoot;
				listDropLocations.add( location );
			}
		}

		// Right of last child
		//--------------------
		widgetChild = (WidgetPart)listChildren.get( listChildren.size() - 1 );

		if( widgetChild != _widgetDragging ) {
			Rectangle rectChild = widgetChild.getBounds();
			Rectangle rectParent = widgetRoot.getBounds();

			Rectangle rectDrop = new Rectangle( 0, 0, 0, 0 );
			rectDrop.x = rectChild.x + rectChild.width + 1;
			rectDrop.y = Math.max( rectChild.y, rectParent.y + DROP_THICKNESS );
			rectDrop.width = ( rectParent.x + rectParent.width ) - ( rectChild.x + rectChild.width ) - DROP_THICKNESS - 5;
			int iBottom = Math.min( rectChild.y + rectChild.height - 1, rectParent.y + rectParent.height - 1 - DROP_THICKNESS );
			rectDrop.height = iBottom - rectDrop.y;

			EvDesignOverlayDropLocation location = new EvDesignOverlayDropLocation();
			location.iLocation = SWT.RIGHT;
			location.widgetParent = widgetRoot;
			location.iIndex = listChildren.size();

			if( rectDrop.width > DROP_THICKNESS && rectDrop.height > DROP_THICKNESS )
				location.setBounds( rectDrop.x, rectDrop.y, rectDrop.width, rectDrop.height );
			else
				location.setBounds( rectChild.x + rectChild.width - DROP_THICKNESS - 1, rectChild.y, DROP_THICKNESS, rectChild.height - 1 );
			
			listDropLocations.add( location );
		}

		// Do children
		//------------
		setupDropLocationsForChildren( listDropLocations );
	}

}
