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
package org.eclipse.edt.ide.rui.visualeditor.internal.editor;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.edt.ide.rui.visualeditor.internal.nl.Messages;
import org.eclipse.edt.ide.rui.visualeditor.internal.preferences.EvPreferences;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPainter;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPart;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.layout.WidgetLayout;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.layout.WidgetLayoutRegistry;
import org.eclipse.edt.ide.rui.visualeditor.plugin.Activator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;


/**
 * A class that does all the painting for the overlay.
 * The methods in this class were placed in this file to reduce the size of the overlay class.
 * This class directly reads some of the overlay's protected instance variables.
 */
public class EvDesignOverlayPainter {

	protected final static Rectangle NOT_SHOWN_REC              = new Rectangle( 0, 0, 16, 16 );

	protected boolean			_bPatternSelection				= false;
	protected boolean			_bPatternDropLocationSelected	= false;
	protected Image				_imageTransparencyFixed			= null;
	protected Image				_imageTransparencyVariable		= null;
	protected EvDesignOverlay	_overlay						= null;
	protected WidgetPainter		_widgetPainter					= null;
	protected Point				_mouseDownPoint					= null;
	protected final static Color COLOR_BLACK = Display.getCurrent().getSystemColor( SWT.COLOR_BLACK );
	protected final static Color COLOR_WHITE = Display.getCurrent().getSystemColor( SWT.COLOR_WHITE );
	protected final static Color COLOR_DARK_GRAY = Display.getCurrent().getSystemColor( SWT.COLOR_DARK_GRAY );
	protected final static Color COLOR_GRAY = Display.getCurrent().getSystemColor( SWT.COLOR_GRAY );
	
	public EvDesignOverlayPainter( EvDesignOverlay overlay ) {
		_overlay = overlay;
		_widgetPainter = new WidgetPainter( _overlay );

		_imageTransparencyFixed    = Activator.getImage( "obj16/transparencymaskfixed.gif" );
		_imageTransparencyVariable = Activator.getImage( "obj16/transparencymaskvariable.jpg" );
	}
	
	public void setMouseDownPoint(Point mouseDownPoint){
		_mouseDownPoint = mouseDownPoint;
	}

	/**
	 * Paints the entire overlay with a background color.
	 */
	protected void paintBlank( GC gc ){
		Rectangle rectBounds = _overlay.getBounds();
		gc.fillRectangle( rectBounds );
	}
	
	/**
	 * Paints a drop rectangle
	 */
	protected void paintDropLocationPotential( GC gc, Rectangle rectDrop, int iLocation, int transparent, int lineStyle, int lineWidth ) {
		if( gc.getClipping().intersects( rectDrop ) == false )
			return;

		gc.setAlpha( transparent );
		gc.setBackground( _overlay._colorDropTargetPotential );
		gc.fillRectangle( rectDrop );
		
		gc.setLineStyle( lineStyle );
		gc.setLineWidth( lineWidth );
		gc.setForeground( COLOR_BLACK );
		paintRectangle( gc, rectDrop, iLocation );
		gc.setAlpha( 255 );
	}

	/**
	 * 
	 */
	protected void paintDropLocations( GC gc ) {
		if( _overlay._listDropLocations == null )
			return;

		// Paint all potential drop locations in backward order
		// to paint the children before the parents
		//-----------------------------------------------------
		Object[] objArray = _overlay._listDropLocations.toArray();

		for( int i = objArray.length - 1; i >= 0; i-- )
			if( objArray[i] != _overlay._dropLocation )
				paintDropLocationPotential( gc, ( (EvDesignOverlayDropLocation)objArray[ i ] ).rectDrop, 
						( (EvDesignOverlayDropLocation)objArray[ i ] ).iLocation, 
						( (EvDesignOverlayDropLocation)objArray[ i ] ).selectedTransparent, 
						( (EvDesignOverlayDropLocation)objArray[ i ] ).lineStyle,
						( (EvDesignOverlayDropLocation)objArray[ i ] ).lineWidth);

		// Paint the selected drop location
		//---------------------------------
		if( _overlay._dropLocation != null ) {
			Rectangle rectDrop = _overlay._dropLocation.rectDrop;
			
			gc.setAlpha( _overlay._dropLocation.selectedTransparent );

			// Fill with color
			//----------------
			gc.setBackground( _overlay._colorDropTargetSelected );
			gc.fillRectangle( rectDrop );

			// Pattern lines
			// Determine whether to use black or white lines
			// Luminance = 0.30Red + 0.59Grn + 0.11Blu
			//----------------------------------------------
			if( _bPatternDropLocationSelected == true ) {
				int iRed = _overlay._colorDropTargetSelected.getRed();
				int iGrn = _overlay._colorDropTargetSelected.getGreen();
				int iBlu = _overlay._colorDropTargetSelected.getBlue();

				double dLuminance = 0.30 * iRed + 0.59 * iGrn + 0.11 * iBlu;
				boolean bWhite = dLuminance < EvConstants.LUMINOSITY_WHITE_BLACK_BOUNDARY;

				gc.setLineStyle( SWT.LINE_SOLID );
				gc.setLineWidth( 1 );
				gc.setForeground( bWhite ? COLOR_WHITE : COLOR_BLACK );
				

				if( rectDrop.width > 8 ) // Vertical lines
					for( int i = 1; i < rectDrop.width; i += 4 )
						gc.drawLine( rectDrop.x + i, rectDrop.y + 1, rectDrop.x + i, rectDrop.y + rectDrop.height - 2 );

				if( rectDrop.height > 8 ) // Horizontal lines
					for( int i = 1; i < rectDrop.height; i += 4 )
						gc.drawLine( rectDrop.x + 1, rectDrop.y + i, rectDrop.x + rectDrop.width - 2, rectDrop.y + i );
			}
			
			// Outer rectangle
			//----------------
			gc.setLineStyle( _overlay._dropLocation.lineStyle );
			gc.setLineWidth( 2 );
			gc.setForeground( COLOR_BLACK );
			paintRectangle( gc, rectDrop, _overlay._dropLocation.iLocation );
			
			gc.setAlpha( 255 );
		}
	}

	/**
	 * Paints the hierarchy and the insertion point.  For each node in the hierarchy
	 * the widget icon and its type string is painted.
	 */
	protected void paintHierarchy( GC gc ) {
		if( _overlay._dropLocation == null || _overlay._listDropLocations == null )
			return;

		if( _overlay._dropLocation.widgetParent == null || _overlay._dropLocation.widgetLayoutData != null )
			return;

		Display display = Display.getCurrent();
		if( display == null )
			return;

		// Obtain the height of widget icons
		//----------------------------------
		WidgetPart widgetRoot = _overlay._pageDesign.getWidgetManager().getWidgetRoot();
		Image image = Activator.getDefault().getWidgetImage( widgetRoot );

		int iFontImageHeight = gc.getFontMetrics().getHeight();
		iFontImageHeight = Math.max( iFontImageHeight, image.getBounds().height );

		// Offset between rows and between image location to widget type string
		//---------------------------------------------------------------------
		int iOffset = iFontImageHeight + 8;

		ScrolledComposite scroll = (ScrolledComposite)_overlay.getParent().getParent();
		Rectangle rectClient = scroll.getClientArea();

		// Obtain a list of parent widgets
		//--------------------------------
		List listParents = new ArrayList();
		WidgetPart widgetParent = _overlay._dropLocation.widgetParent;
		while( widgetParent != widgetRoot ) {
			listParents.add( 0, widgetParent );
			widgetParent = widgetParent.getParent();
		}

		// Find the maximum text length of the children
		//---------------------------------------------
		int iMaxTextWidth = 16;

		List listChildren = _overlay._dropLocation.widgetParent.getChildren();
		for( int i = 0; i < listChildren.size(); i++ ) {
			WidgetPart widgetChild = (WidgetPart)listChildren.get( i );
			iMaxTextWidth = Math.max( iMaxTextWidth, gc.stringExtent( widgetChild.getLabel() ).x );
		}

		// Also do the parent text length
		//-------------------------------
		iMaxTextWidth = Math.max( iMaxTextWidth, gc.stringExtent( _overlay._dropLocation.widgetParent.getLabel() ).x );

		String strWidgetTypeDragging = null;
		Dimension dimHeading = new Dimension( 0, 0 );

		if( _overlay._widgetDragging != null ) {
			strWidgetTypeDragging = _overlay._widgetDragging.getLabel();
			Point ptWidgetTypeSize = gc.stringExtent( strWidgetTypeDragging );
			dimHeading.width = iOffset + ptWidgetTypeSize.x + 8; // Image width plus type string width plus space
			dimHeading.height = ptWidgetTypeSize.y + 8; // Type string plus space
		}

		// Compute the bounds
		//-------------------
		Rectangle rectBounds = new Rectangle( 0, 0, 0, 0 );
		Point ptOrigin = scroll.getOrigin();

		rectBounds.y = ptOrigin.y + 16;
		rectBounds.width = 4 + listParents.size() * 16 + iOffset + iMaxTextWidth + 8;
		rectBounds.width = Math.max( rectBounds.width, dimHeading.width );
		rectBounds.height = dimHeading.height + ( listParents.size() + listChildren.size() ) * iOffset + 16;

		boolean bLeft = _overlay._ptMouse.x > +ptOrigin.x + rectClient.width / 2;
		rectBounds.x = ptOrigin.x + rectClient.width / 2 + 16;
		if( bLeft == true )
			rectBounds.x -= ( rectBounds.width + 32 );

		// Paint the box
		//--------------
		gc.setLineWidth( 1 );
		gc.setLineStyle( SWT.LINE_SOLID );

		gc.setBackground( COLOR_WHITE );
		gc.fillRectangle( rectBounds );

		// Inner line
		//-----------
		gc.setForeground( COLOR_GRAY );
		gc.drawRectangle( rectBounds );

		// Outer line
		//-----------
		rectBounds.x -= 1;
		rectBounds.y -= 1;
		rectBounds.width += 2;
		rectBounds.height += 2;

		gc.setForeground( COLOR_DARK_GRAY );
		gc.drawRectangle( rectBounds );

		rectBounds.x += 1;
		rectBounds.y += 1;
		rectBounds.width -= 2;
		rectBounds.height -= 2;

		// Separator line and widget type name
		//------------------------------------
		if( strWidgetTypeDragging != null ) {
			gc.setForeground( COLOR_GRAY );
			gc.drawLine( rectBounds.x + 1, rectBounds.y + dimHeading.height + 2, rectBounds.x + rectBounds.width - 1, rectBounds.y + dimHeading.height + 2 );
			gc.drawLine( rectBounds.x + 1, rectBounds.y + dimHeading.height + 4, rectBounds.x + rectBounds.width - 1, rectBounds.y + dimHeading.height + 4 );

			image = Activator.getDefault().getWidgetImage( _overlay._widgetDragging );
			gc.drawImage( image, rectBounds.x + 4, rectBounds.y + 4 );

			gc.setForeground( COLOR_BLACK );
			gc.drawString( strWidgetTypeDragging, rectBounds.x + 4 + iOffset, rectBounds.y + 4, true );
		}

		// Paint the insertion point.  Its vertical line has an approximate length that may be too long.
		// We do this first so that an icon or another line can be drawn on top of the vertical line.
		//----------------------------------------------------------------------------------------------
		gc.setLineStyle( SWT.LINE_SOLID );
		gc.setBackground( _overlay._colorDropTargetSelected );
		gc.setForeground( COLOR_BLACK );

		Rectangle rect = new Rectangle( 0, 0, 0, 0 );
		rect.x = rectBounds.x + listParents.size() * 16 + 4;
		rect.y = rectBounds.y + dimHeading.height + 10 + ( listParents.size() + _overlay._dropLocation.iIndex ) * iOffset;
		rect.width = rectBounds.x + rectBounds.width - rect.x - 8;
		rect.height = 4;

		gc.fillRectangle( rect );
		gc.drawRectangle( rect );
		
		// Vertical lines in the insertion point
		//--------------------------------------
		_bPatternDropLocationSelected = EvPreferences.getBoolean( EvConstants.PREFERENCE_PATTERN_DROP_LOCATION_SELECTED );
		if( _bPatternDropLocationSelected == true ){
			for( int i = 4; i < rect.width; i += 4 )
				gc.drawLine( rect.x + i, rect.y, rect.x + i, rect.y + rect.height - 1 );
		}
		
		// Line to the insertion point
		//----------------------------
		gc.setForeground( COLOR_GRAY );

		if( _overlay._dropLocation.widgetParent != widgetRoot ) {
			gc.drawLine( rect.x - 8, rect.y + rect.height / 2 - iFontImageHeight, rect.x - 8, rect.y + rect.height / 2 ); // Vertical
			gc.drawLine( rect.x - 8, rect.y + rect.height / 2, rect.x - 1, rect.y + rect.height / 2 ); // Horizontal
		}

		// Paint the parent hierarchy
		//---------------------------
		rect = new Rectangle( rectBounds.x + 4, rectBounds.y + dimHeading.height + 16, image.getBounds().width, iFontImageHeight );

		for( int i = 0; i < listParents.size(); i++ ) {
			widgetParent = (WidgetPart)listParents.get( i );

			// Vertical, horizontal line
			//--------------------------
			if( i > 0 ) {
				gc.setForeground( COLOR_GRAY );
				gc.drawLine( 4 + rect.x - iOffset / 2, rect.y - ( iOffset - iFontImageHeight ), 4 + rect.x - iOffset / 2, rect.y + iFontImageHeight / 2 );
				gc.drawLine( 4 + rect.x - iOffset / 2, rect.y + iFontImageHeight / 2, 4 + rect.x - iOffset / 2 + iFontImageHeight / 2, rect.y + iFontImageHeight / 2 );
			}

			// Widget icon
			//------------
			image = Activator.getDefault().getWidgetImage( widgetParent );
			gc.drawImage( image, rect.x, rect.y );

			// Widget type
			//------------
			gc.setBackground( COLOR_WHITE );
			gc.setForeground( COLOR_BLACK );
			gc.drawString( widgetParent.getLabel(), rect.x + iOffset, rect.y, true );

			rect.x += 16;
			rect.y += iOffset;
		}

		// Paint the widget's children
		//----------------------------
		for( int i = 0; i < listChildren.size(); i++ ) {
			gc.setBackground( COLOR_WHITE );
			gc.setForeground( COLOR_GRAY );

			WidgetPart widgetChild = (WidgetPart)listChildren.get( i );

			// Vertical, horizontal line
			//--------------------------
			if( widgetChild.getParent() != widgetRoot ) {
				if( i < listChildren.size() - 1 )
					gc.drawLine( 4 + rect.x - iOffset / 2, rect.y - ( iOffset - iFontImageHeight ), 4 + rect.x - iOffset / 2, rect.y + iOffset );
				else
					gc.drawLine( 4 + rect.x - iOffset / 2, rect.y - ( iOffset - iFontImageHeight ), 4 + rect.x - iOffset / 2, rect.y + iFontImageHeight / 2 );

				gc.drawLine( 4 + rect.x - iOffset / 2, rect.y + iFontImageHeight / 2, 4 + rect.x - iOffset / 2 + iFontImageHeight / 2, rect.y + iFontImageHeight / 2 );
			}

			// Widget icon
			//------------
			image = Activator.getDefault().getWidgetImage( widgetChild );
			gc.drawImage( image, rect.x, rect.y );

			// Widget type
			//------------
			gc.setForeground( COLOR_BLACK );
			gc.drawString( widgetChild.getLabel(), rect.x + iOffset, rect.y, true );

			// Rectangle around the widget being dragged
			// Draw gray solid line under selection color dotted line
			// so the line is always visible for all selection colors
			//-------------------------------------------------------
			if( widgetChild == _overlay._widgetDragging ) {
				Rectangle rectBorder = new Rectangle( rect.x - 1, rect.y - 1, iOffset + iMaxTextWidth + 3, rect.height + 3 );
				gc.setLineWidth( 2 );

				gc.setLineStyle( SWT.LINE_SOLID );
				gc.setForeground( COLOR_GRAY );
				gc.drawRectangle( rectBorder );

				gc.setLineStyle( SWT.LINE_DOT );
				gc.setForeground( _overlay._colorWidgetSelected );
				gc.drawRectangle( rectBorder );

				gc.setLineStyle( SWT.LINE_SOLID );
				gc.setLineWidth( 1 );
			}

			rect.y += iOffset;
		}
	}

	/**
	 * Displays how to start instructions when there are no widgets.
	 */
	protected void paintInstructions( GC gc ) {
		// Disable for now.  The instructions sometimes gets drawn when
		// a runtime message appears instead of the web page
		//-------------------------------------------------------------
		if( true /* _overlay._bShowInstructions == false */)
			return;

		gc.setBackground( Display.getCurrent().getSystemColor( SWT.COLOR_WHITE ) );
		gc.setForeground( Display.getCurrent().getSystemColor( SWT.COLOR_BLACK ) );

		gc.drawString( Messages.NL_Drag_an_item_from_the_palette_and_drop_it_here, 16, 16 );
	}

	/**
	 * Paints a white dotted pattern over the entire client area.
	 */
	protected void paintOpaque( GC gc ) {

		// Copy the transparency mode in case
		// we need to override it for dragging
		//------------------------------------
		int iTransparencyMode = _overlay._iTransparencyMode;

		// Override the transparency mode to dotted pattern if we are
		// dragging and the 'half transparency while dragging' preference is on
		//---------------------------------------------------------------------
		if( _overlay._listDropLocations != null ) {
			boolean bSemiTransparentWhileDragging = EvPreferences.getBoolean( EvConstants.PREFERENCE_SEMITRANSPARENCY_WHILE_DRAGGING );
			if( bSemiTransparentWhileDragging == true )
				iTransparencyMode = EvConstants.SEMITRANSPARENCY_FIXED;
		}

		// Do nothing if there is no transparency pattern to paint
		//--------------------------------------------------------
		if( iTransparencyMode == EvConstants.SEMITRANSPARENCY_NONE )
			return;

		int iImageW = _imageTransparencyFixed.getBounds().width;
		int iImageH = _imageTransparencyVariable.getBounds().height;

		ScrolledComposite compositeParent = (ScrolledComposite)_overlay.getParent().getParent();
		Rectangle rectClient = compositeParent.getClientArea();
		Rectangle rectParent = _overlay.getParent().getBounds();
		Rectangle rectBounds = new Rectangle( -rectParent.x - iImageW, -rectParent.y - iImageH, rectClient.width + iImageW, rectClient.height + iImageH );

		// Start on image boundary
		//------------------------
		rectBounds.x = ( rectBounds.x / 2 ) * 2;
		rectBounds.y = ( rectBounds.y / 2 ) * 2;

		if( rectBounds.intersects( gc.getClipping() ) == false )
			return;

		boolean bUseAlpha = iTransparencyMode == EvConstants.SEMITRANSPARENCY_VARIABLE;

		// Variable amount of transparency
		//--------------------------------
		if( bUseAlpha == true ) {
			gc.setAlpha( 255 - _overlay._iTransparencyAmount );
			gc.drawImage( _imageTransparencyVariable, 0, 0, iImageW, iImageH, rectBounds.x, rectBounds.y, rectBounds.width, rectBounds.height );
			gc.setAlpha( 255 );
		}

		// Fixed amount of transparency
		//-----------------------------
		else {
			for( int y = rectBounds.y; y < rectBounds.y + rectBounds.height; y += iImageH )
				for( int x = rectBounds.x; x < rectBounds.x + rectBounds.width; x += iImageW )
					gc.drawImage( _imageTransparencyFixed, x, y );
		}
	}

	/**
	 * Paints a rectangle based on its facing orientation.  The location side is painted dashed.
	 */
	protected void paintRectangle( GC gc, Rectangle rect, int iLocation ) {
//		gc.setLineStyle( SWT.LINE_SOLID );

		switch( iLocation ){
			case SWT.LEFT:
				gc.drawLine( rect.x, rect.y, rect.x + rect.width, rect.y );
				gc.drawLine( rect.x + rect.width, rect.y, rect.x + rect.width, rect.y + rect.height );
				gc.drawLine( rect.x, rect.y + rect.height, rect.x + rect.width, rect.y + rect.height );
				gc.setLineStyle( SWT.LINE_DASH );
				gc.drawLine( rect.x, rect.y, rect.x, rect.y + rect.height );
				break;

			case SWT.RIGHT:
				gc.drawLine( rect.x, rect.y, rect.x + rect.width, rect.y );
				gc.drawLine( rect.x, rect.y, rect.x, rect.y + rect.height );
				gc.drawLine( rect.x, rect.y + rect.height, rect.x + rect.width, rect.y + rect.height );
				gc.setLineStyle( SWT.LINE_DASH );
				gc.drawLine( rect.x + rect.width, rect.y, rect.x + rect.width, rect.y + rect.height );
				break;

			case SWT.TOP:
				gc.drawLine( rect.x, rect.y, rect.x, rect.y + rect.height );
				gc.drawLine( rect.x, rect.y + rect.height, rect.x + rect.width, rect.y + rect.height );
				gc.drawLine( rect.x + rect.width, rect.y, rect.x + rect.width, rect.y + rect.height );
				gc.setLineStyle( SWT.LINE_DASH );
				gc.drawLine( rect.x, rect.y, rect.x + rect.width, rect.y );
				break;

			case SWT.BOTTOM:
				gc.drawLine( rect.x, rect.y, rect.x, rect.y + rect.height );
				gc.drawLine( rect.x, rect.y, rect.x + rect.width, rect.y );
				gc.drawLine( rect.x + rect.width, rect.y, rect.x + rect.width, rect.y + rect.height );
				gc.setLineStyle( SWT.LINE_DASH );
				gc.drawLine( rect.x, rect.y + rect.height, rect.x + rect.width, rect.y + rect.height );
				break;

			case SWT.CENTER:
			default:
				gc.drawRectangle( rect );
				break;
		}
	}

	/**
	 * Paints all widgets
	 */
	protected void paintWidgets( GC gc ) {
		Iterator iterWidgets = _overlay.getWidgets();
		
		boolean bDrawOutline = _overlay._listDropLocations != null && _overlay._listDropLocations.size() > 0;

		try {
			while( iterWidgets.hasNext() == true ) {
				WidgetPart widget = (WidgetPart)iterWidgets.next();
	
				if( widget.getBounds().intersects( gc.getClipping() ) == false || widget.getBounds().equals( NOT_SHOWN_REC ) )
					continue;
				
				boolean isContainer = _overlay.isContainer( widget );
				_widgetPainter.setBounds( widget.getBounds() );
				
				_widgetPainter.setDragging( _overlay._widgetDragging == widget );
				_widgetPainter.setMouseOver( widget.getMouseOver() );
				_widgetPainter.setSelected( widget.getSelected() );
				_widgetPainter.setFocus( _overlay.isFocusControl() );
				_widgetPainter.setDrawOutline( bDrawOutline );
				_widgetPainter.setIsContainer( isContainer );
				WidgetLayout layout = WidgetLayoutRegistry.getInstance().getWidgetLayout( WidgetLayoutRegistry.getLayoutName(widget), isContainer );
				if ( layout != null ) {
					if(widget.getSelected()){
						_widgetPainter.setMouseDownPoint(_mouseDownPoint);
						_widgetPainter.setIsSelectedWidget(true);
					}else{
						_widgetPainter.setIsSelectedWidget(false);
					}
					_widgetPainter.setInnerRectangles( layout.getInnerRectanglesForPaintingWidget(widget) );
				} else {
					_widgetPainter.setInnerRectangles( null );
				}
	
				_widgetPainter.paintWidget( gc );
			}
		} catch ( ConcurrentModificationException cme ) {
			//do nothing
		}
	}

	/**
	 * Paints the widget being dragged if any
	 */
	protected void paintWidgetDragging( GC gc ) {
		if ( _overlay._widgetDragging == null )
			return;
		
		Iterator iterWidgets = _overlay.getWidgets();

		while( iterWidgets.hasNext() == true ) {
			WidgetPart widget = (WidgetPart)iterWidgets.next();

			if( widget.getBoundsDragging().isEmpty() == false ) {
				_widgetPainter.setBoundsDragging( widget.getBoundsDragging() );
				_widgetPainter.paintWidgetDragging( gc );
			}
		}
	}

	/**
	 * Updates the selection color and pattern based on what is specified in the preferences. 
	 * Updates the widget painter.
	 */
	public void updateColors() {
		_bPatternDropLocationSelected = EvPreferences.getBoolean( EvConstants.PREFERENCE_PATTERN_DROP_LOCATION_SELECTED );
		_bPatternSelection = EvPreferences.getBoolean( EvConstants.PREFERENCE_PATTERN_SELECTION );

		_widgetPainter.updateColors();
	}
}
