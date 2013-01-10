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

import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvDesignOverlay;
import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvDesignOverlayDropLocation;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;


public class RootWidgetLayout extends AbstractWidgetLayout {

	public void setupDropLocations(Collection listDropLocations) {
		List listChildren = widgetRoot.getChildren();

		if( listChildren.size() > 0 ){			
			// First child
			//------------
			WidgetPart widget = (WidgetPart)listChildren.get( 0 );
			
			if( widget != _widgetDragging ) {
				String strType = widget.getTypeName();
				Rectangle rectBounds = widget.getBounds();

				if( strType.equalsIgnoreCase( "Box" ) == true ) {
					// Top side
					//---------
					EvDesignOverlayDropLocation location = new EvDesignOverlayDropLocation();
					location.setBounds( rectBounds.x, rectBounds.y, rectBounds.width - 1, EvDesignOverlay.DROP_THICKNESS );
					location.iIndex = 0;
					location.iLocation = SWT.TOP;
					location.widgetParent = widgetRoot;
					listDropLocations.add( location );
				}
				else {
					// Left side
					//----------
					EvDesignOverlayDropLocation location = new EvDesignOverlayDropLocation();
					location.setBounds( rectBounds.x, rectBounds.y, DROP_THICKNESS, rectBounds.height - 1 );
					location.iIndex = 0;
					location.iLocation = SWT.LEFT;
					location.widgetParent = widgetRoot;
					listDropLocations.add( location );
				}
			}
			
			// Middle children
			//----------------
			for( int i=0; i<listChildren.size() - 1; ++i ){
				WidgetPart widgetA = (WidgetPart)listChildren.get( i );
				WidgetPart widgetB = (WidgetPart)listChildren.get( i + 1 );

				if( widgetA != _widgetDragging && widgetB != _widgetDragging ) {
					Rectangle rectA = widgetA.getBounds();
					String strTypeA = widgetA.getTypeName();
					boolean bBoxA = strTypeA.equalsIgnoreCase( "Box" ) == true;

					Rectangle rectB = widgetB.getBounds();
					String strTypeB = widgetB.getTypeName();
					boolean bBoxB = strTypeB.equalsIgnoreCase( "Box" ) == true;

					// Box next to Box
					//----------------
					if( bBoxA && bBoxB ) {
						// Between
						//--------
						EvDesignOverlayDropLocation location = new EvDesignOverlayDropLocation();
						location.rectDrop.y = rectA.y + rectA.height;
						location.rectDrop.x = Math.min( rectA.x, rectB.x );
						location.rectDrop.height = rectB.y - ( rectA.y + rectA.height );
						location.rectDrop.height = Math.max( location.rectDrop.height, DROP_THICKNESS );
						location.rectDrop.width = Math.max( rectA.x + rectA.width - 1, rectB.x + rectB.width - 1 ) - location.rectDrop.x;
						location.rectDrop.width = Math.max( location.rectDrop.width, DROP_THICKNESS );
						location.iIndex = i + 1;
						location.iLocation = SWT.CENTER;
						location.widgetParent = widgetRoot;
						listDropLocations.add( location );
					}

					// Box next to non-Box: Left side of non-Box
					//------------------------------------------
					else if( bBoxA && !bBoxB ) {
						EvDesignOverlayDropLocation location = new EvDesignOverlayDropLocation();
						location.setBounds( rectB.x, rectB.y, DROP_THICKNESS, rectB.height - 1 );
						location.iIndex = i + 1;
						location.iLocation = SWT.CENTER;
						location.widgetParent = widgetRoot;
						listDropLocations.add( location );
					}

					// Non-Box next to Box: Right side of non-Box
					//-------------------------------------------
					else if( !bBoxA && bBoxB ) {
						EvDesignOverlayDropLocation location = new EvDesignOverlayDropLocation();
						location.setBounds( rectA.x + rectA.width - 1 - DROP_THICKNESS, rectA.y, DROP_THICKNESS, rectA.height - 1 );
						location.iIndex = i + 1;
						location.iLocation = SWT.CENTER;
						location.widgetParent = widgetRoot;
						listDropLocations.add( location );
					}

					// Non-Boxes: Between
					//-------------------
					else {// !BoxA && !BoxB
						EvDesignOverlayDropLocation location = new EvDesignOverlayDropLocation();
						location.rectDrop.x = rectA.x + rectA.width;
						location.rectDrop.y = Math.min( rectA.y, rectB.y );
						location.rectDrop.width = rectB.x - ( rectA.x + rectA.width );
						
						if( location.rectDrop.width < 0 ){
							location.rectDrop.width = DROP_THICKNESS;
							location.rectDrop.height = rectA.height;
						}
						else{
							location.rectDrop.height = Math.max( rectA.y + rectA.height - 1, rectB.y + rectB.height - 1 ) - location.rectDrop.y;
							location.rectDrop.height = Math.max( location.rectDrop.height, DROP_THICKNESS );
						}
						
						location.iIndex = i + 1;
						location.iLocation = SWT.CENTER;
						location.widgetParent = widgetRoot;
						listDropLocations.add( location );
					}
				}
			}
			
			// Last child
			//-----------
			widget = (WidgetPart)listChildren.get( listChildren.size() - 1 );

			if( widget != _widgetDragging ) {
				String strType = widget.getTypeName();
				Rectangle rectBounds = widget.getBounds();

				if( strType.equalsIgnoreCase( "Box" ) == true ) {
					// Bottom side
					//------------
					EvDesignOverlayDropLocation location = new EvDesignOverlayDropLocation();
					location.setBounds( rectBounds.x, rectBounds.y + rectBounds.height - 1 - DROP_THICKNESS, rectBounds.width - 1, DROP_THICKNESS );
					location.iIndex = listChildren.size();
					location.iLocation = SWT.BOTTOM;
					location.widgetParent = widgetRoot;
					listDropLocations.add( location );
				}
				else {
					// Right side
					//-----------
					EvDesignOverlayDropLocation location = new EvDesignOverlayDropLocation();
					location.setBounds( rectBounds.x + rectBounds.width - 1 - DROP_THICKNESS, rectBounds.y, DROP_THICKNESS, rectBounds.height - 1 );
					location.iIndex = listChildren.size();
					location.iLocation = SWT.RIGHT;
					location.widgetParent = widgetRoot;
					listDropLocations.add( location );
				}
			}
		}
		
		// Below all other widgets
		//------------------------
		setupDropLocationsForRootEnd( widgetRoot, listDropLocations );
	}
	
	/**
	 * Creates a location below the other widgets where a person can drop a new or existing widget for a RUI Handler
	 */
	protected void setupDropLocationsForRootEnd( WidgetPart widgetRoot, Collection listDropLocations ) {
		// If the widget being dragged is the last child, it cannot be added after itself
		//-------------------------------------------------------------------------------
		List listRootChildren = widgetRoot.getChildren();
		if( _widgetDragging != null && listRootChildren.get( listRootChildren.size() - 1 ) == _widgetDragging )
			return;
		
		// Compute union of child widgets
		//-------------------------------
		Rectangle rectUnion = new Rectangle( 0, 0, 0, 0 );
		
		Iterator iterWidgets = getWidgets();
		while( iterWidgets.hasNext() == true ){
			WidgetPart widget = (WidgetPart)iterWidgets.next();
			rectUnion = rectUnion.union( widget.getBounds() );
		}
		
		// Compute bounds which is the client area that is showing minus margins
		//----------------------------------------------------------------------
		int iMargin = 16;

		Rectangle rectBounds = parentComposite.getClientArea();
		Point ptOrigin = parentComposite.getOrigin();
		rectBounds.x = ptOrigin.x + iMargin;
		rectBounds.y = ptOrigin.y + iMargin;
		rectBounds.width -= ( iMargin * 2 + 1 );
		rectBounds.height -= ( iMargin * 2 + 1 );
		
		rectBounds.y = rectUnion.y + rectUnion.height + 16;
		rectBounds.height = Math.max( rectBounds.height, 32 );
		
		// Create the drop location
		//-------------------------
		EvDesignOverlayDropLocation location = new EvDesignOverlayDropLocation();
		location.setBounds( rectBounds.x, rectBounds.y, rectBounds.width, rectBounds.height );
		location.iIndex = widgetRoot.getChildren().size();
		location.iLocation = SWT.CENTER;
		location.widgetParent = widgetRoot;

		listDropLocations.add( location );
	}
}
