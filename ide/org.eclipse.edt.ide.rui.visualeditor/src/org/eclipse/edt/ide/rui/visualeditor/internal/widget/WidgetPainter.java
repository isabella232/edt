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
package org.eclipse.edt.ide.rui.visualeditor.internal.widget;

import java.util.List;

import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvConstants;
import org.eclipse.edt.ide.rui.visualeditor.internal.preferences.EvPreferences;
import org.eclipse.edt.ide.rui.visualeditor.internal.util.ColorUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;


/**
 * This is a singleton that draws all widget outlines.
 * To use, set this class' bounds.
 */
public class WidgetPainter implements DisposeListener {

	protected Color		BLACK					= null;
	protected Color		GRAY					= null;
	protected Color		WHITE					= null;

	protected boolean	_bDragging				= false;
	protected boolean	_bFocus					= false;
	protected boolean	_bMouseOver				= false;
	protected boolean	_bOutline				= false;
	protected boolean	_bPatternSelection		= false;
	protected boolean	_bPatternSelectionWhite	= false;
	protected boolean	_bSelected				= false;
	protected boolean	_bIsContainer			= false;
	protected boolean	_bIsSelectedWidget		= false;
	protected Color		_colorSelected			= null;
	protected Color		_colorLogicalArea		= null;
	protected Composite	_compositeParent		= null;
	protected Rectangle	_rectBounds				= new Rectangle( 0, 0, 0, 0 );
	protected Rectangle	_rectDragging			= new Rectangle( 0, 0, 0, 0 );
	protected List      _innerRectangles        = null;
	protected Point		_mouseDownPoint			= null;
	
	/**
	 * 
	 */
	public WidgetPainter( Composite compositeParent ) {
		_compositeParent = compositeParent;
		_compositeParent.addDisposeListener( this );

		Display display = compositeParent.getDisplay();

		BLACK = display.getSystemColor( SWT.COLOR_BLACK );
		GRAY = display.getSystemColor( SWT.COLOR_GRAY );
		WHITE = display.getSystemColor( SWT.COLOR_WHITE );
		_colorLogicalArea = new Color( display, 190, 190, 190 );

		updateColors();
	}

	public void setMouseDownPoint(Point mouseDownPoint){
		_mouseDownPoint = mouseDownPoint;
	}
	
	public void setIsSelectedWidget(boolean isSelectedWidget){
		_bIsSelectedWidget = isSelectedWidget;
	}
	
	/**
	 * Returns the bounds of the widget.
	 */
	protected Rectangle getBounds() {
		return new Rectangle( _rectBounds.x, _rectBounds.y, _rectBounds.width, _rectBounds.height );
	}

	/**
	 * Paints the widget's corners.
	 */
	protected void paintCorners( GC gc ) {

		Rectangle rectBounds = getBounds();

		// Compute vertical and horizontal line lengths
		//---------------------------------------------
		final int iS     = 4; // Short
		final int iL     = 6; // Long
		final int iOverS = 5; // Short mouse over
		final int iOverL = 7; // Long  mouse over
		final int iSelS  = 6; // Short selected
		final int iSelL  = 8; // Long  selected
		
		int iLengthX = 0;
		int iLengthY = 0;

		boolean bWidthLongerThanHeight = rectBounds.width > rectBounds.height; 
		
		if( _bSelected ) {
			iLengthX = bWidthLongerThanHeight ? iSelL : iSelS;
			iLengthY = bWidthLongerThanHeight ? iSelS : iSelL;
		}
		else if( _bMouseOver ){
			iLengthX = bWidthLongerThanHeight ? iOverL : iOverS;
			iLengthY = bWidthLongerThanHeight ? iOverS : iOverL;
		}
		
		else{
			iLengthX = bWidthLongerThanHeight ? iL : iS;
			iLengthY = bWidthLongerThanHeight ? iS : iL;
		}

		// Ensure lines are not longer than the widget
		//--------------------------------------------
		iLengthX = Math.min( iLengthX, rectBounds.width / 3 );
		iLengthY = Math.min( iLengthY, rectBounds.height / 3 );

		int iWidth = 2;
		
		if( _bSelected )
			iWidth = 3;
		
		else if( _bMouseOver )
			iWidth = 3;
		
		Color color = BLACK;

		for( int i = 0; i < iWidth; ++i ) {
			if( i == 1 )
				color = GRAY;
			
			else if( i == 2 )
				color = _bSelected ? BLACK : GRAY;

			gc.setForeground( color );

			// Top left
			//---------
			gc.drawLine( rectBounds.x, rectBounds.y, rectBounds.x + iLengthX, rectBounds.y );
			gc.drawLine( rectBounds.x, rectBounds.y, rectBounds.x, rectBounds.y + iLengthY );

			// Top right
			//----------
			gc.drawLine( rectBounds.x + rectBounds.width - 1 - iLengthX, rectBounds.y, rectBounds.x + rectBounds.width - 1, rectBounds.y );
			gc.drawLine( rectBounds.x + rectBounds.width - 1, rectBounds.y, rectBounds.x + rectBounds.width - 1, rectBounds.y + iLengthY );

			// Bottom left
			//------------
			gc.drawLine( rectBounds.x, rectBounds.y + rectBounds.height - 1, rectBounds.x + iLengthX, rectBounds.y + rectBounds.height - 1 );
			gc.drawLine( rectBounds.x, rectBounds.y + rectBounds.height - 1 - iLengthY, rectBounds.x, rectBounds.y + rectBounds.height - 1 );

			// Bottom right
			//-------------
			gc.drawLine( rectBounds.x + rectBounds.width - 1 - iLengthX, rectBounds.y + rectBounds.height - 1, rectBounds.x + rectBounds.width - 1, rectBounds.y + rectBounds.height - 1 );
			gc.drawLine( rectBounds.x + rectBounds.width - 1, rectBounds.y + rectBounds.height - 1 - iLengthY, rectBounds.x + rectBounds.width - 1, rectBounds.y + rectBounds.height - 1 );

			rectBounds.x++;
			rectBounds.y++;
			rectBounds.width -= 2;
			rectBounds.height -= 2;
			iLengthX--;
			iLengthY--;
		}
	}

	/**
	 * Draws a dashed rectangle
	 */
	protected void paintRectangleDotted( GC gc, int iWidth, Color color, Rectangle rectB ) {

		Rectangle rectBounds = new Rectangle( rectB.x, rectB.y, rectB.width, rectB.height );
		rectBounds.width--;
		rectBounds.height--;

		gc.setForeground( color );
		gc.setLineWidth( iWidth );
		gc.setLineStyle( SWT.LINE_DOT );
		gc.drawRectangle( rectBounds );
		gc.setLineStyle( SWT.LINE_SOLID );
	}

	/**
	 * 
	 */
	protected void paintRectangleSelected( GC gc, Rectangle rectBounds ) {
		
		// Solid line in black or white
		//-----------------------------
		gc.setLineStyle( SWT.LINE_SOLID );
		gc.setLineWidth( 1 );

		// No pattern, solid color
		//------------------------
		if( _bPatternSelection == false ){
			gc.setForeground( _colorSelected );
			Rectangle rect = new Rectangle( rectBounds.x, rectBounds.y, rectBounds.width - 1, rectBounds.height - 1 );
			gc.drawRectangle( rect );
			rect = new Rectangle( rectBounds.x + 1, rectBounds.y + 1, rectBounds.width - 3, rectBounds.height - 3 );
			gc.drawRectangle( rect );
			return;
		}
		
		// Patterned lines
		//----------------
		if( _bFocus && _bDragging == false ) {

			// Solid line in black or white
			//-----------------------------
			gc.setLineStyle( SWT.LINE_CUSTOM );
			gc.setLineDash( new int[]{4} );
			gc.setLineWidth( 2 );
			Rectangle rect = new Rectangle( rectBounds.x + 1, rectBounds.y + 1, rectBounds.width - 2, rectBounds.height - 2 );
			gc.setForeground( _colorSelected );
			gc.drawRectangle( rect );

			gc.setLineDash( new int[] {} );
		}
	}
	
	/**
	 *  
	 */
	protected void paintRectangleSolid( GC gc, int iWidth, Color color, Rectangle rectB ) {
		gc.setLineStyle( SWT.LINE_SOLID );
		gc.setLineWidth( 1 );

		Rectangle rectBounds = new Rectangle( rectB.x, rectB.y, rectB.width, rectB.height );
		rectBounds.width--;
		rectBounds.height--;

		gc.setForeground( color );

		for( int i = 0; i < iWidth; ++i ) {
			gc.drawRectangle( rectBounds );

			rectBounds.x++;
			rectBounds.y++;
			rectBounds.width -= 2;
			rectBounds.height -= 2;
		}
	}

	/**
	 *  
	 */
	public void paintWidget( GC gc ) {
		Rectangle rectBounds = getBounds();

		if( rectBounds.isEmpty() )
			return;
		
		boolean selectInnerRec = false;
		// inner (for example: cell of GridLayout)
		if ( _innerRectangles != null ) {
			for ( int i = 0; i < _innerRectangles.size(); i ++ ) {
				Rectangle rectangle = (Rectangle)_innerRectangles.get(i);
				if(_mouseDownPoint != null && _bIsSelectedWidget && rectangle.contains(_mouseDownPoint)){
					if( _bFocus ) {
						paintRectangleSelected( gc, rectangle );
					} else {
						paintRectangleSolid( gc, 2, _colorSelected, rectangle );
						Rectangle rectOffset2 = new Rectangle( rectangle.x + 2, rectangle.y + 2, rectangle.width - 4, rectangle.height - 4 );
						paintRectangleSolid( gc, 1, _bPatternSelectionWhite ? WHITE : BLACK, rectOffset2 );
					}
					selectInnerRec = true;
				} else {
					paintRectangleDotted( gc, 1, _colorLogicalArea, rectangle );
				}

			}
		}
		
		// Selected
		//---------
		if( _bSelected == true ) {

			// Selected with focus
			//--------------------
			
			if( _bFocus == true && _bDragging == false ){

				if(!selectInnerRec){
					paintRectangleSelected( gc, rectBounds );
				}
			}

			// Selected, no focus
			//-------------------
			else{

				if(!selectInnerRec){
					paintRectangleSolid( gc, 2, _colorSelected, rectBounds );
					Rectangle rectOffset2 = new Rectangle( rectBounds.x + 2, rectBounds.y + 2, rectBounds.width - 4, rectBounds.height - 4 );
					paintRectangleSolid( gc, 1, _bPatternSelectionWhite ? WHITE : BLACK, rectOffset2 );
				}
			}
		}

		// Not selected
		//-------------
		else if( _bOutline == true || _bMouseOver == true )
			paintRectangleSolid( gc, 2, GRAY, rectBounds );

		if ( _bIsContainer ) {
			paintCorners( gc );
		}

	}

	/**
	 * Paints the dragging rectangle for a widget that is being dragged.
	 */
	public void paintWidgetDragging( GC gc ) {
		paintRectangleDotted( gc, 2, _colorSelected, _rectDragging );
	}

	/**
	 * 
	 */
	public void setBounds( Rectangle rectBounds ) {
		_rectBounds.x = rectBounds.x;
		_rectBounds.y = rectBounds.y;
		_rectBounds.width = rectBounds.width;
		_rectBounds.height = rectBounds.height;
	}

	/**
	 * 
	 */
	public void setBoundsDragging( Rectangle rectDragging ) {
		_rectDragging.x = rectDragging.x;
		_rectDragging.y = rectDragging.y;
		_rectDragging.width = rectDragging.width;
		_rectDragging.height = rectDragging.height;
	}

	/**
	 * 
	 */
	public void setDragging( boolean bDragging ) {
		_bDragging = bDragging;
	}
	
	/**
	 * 
	 */
	public void setIsContainer( boolean bIsContainer ) {
		_bIsContainer = bIsContainer;
	}

	/**
	 * 
	 */
	public void setDrawOutline( boolean bOutline ) {
		_bOutline = bOutline;
	}

	/**
	 * 
	 */
	public void setFocus( boolean bFocus ) {
		_bFocus = bFocus;
	}

	/**
	 * 
	 */
	public void setMouseOver( boolean bMouseOver ) {
		_bMouseOver = bMouseOver;
	}

	/**
	 * 
	 */
	public void setSelected( boolean bSelected ) {
		_bSelected = bSelected;
	}
	
	/**
	 * 
	 */
	public void setInnerRectangles( List innerRectangles ) {
		_innerRectangles = innerRectangles;
	}

	/**
	 * Updates the selection color based on what is specified in the preferences. 
	 */
	public void updateColors() {

		// Obtain the color and pattern preferences
		//-----------------------------------------
		String strColorSelection = EvPreferences.getString( EvConstants.PREFERENCE_COLOR_SELECTION );
		_bPatternSelection = EvPreferences.getBoolean( EvConstants.PREFERENCE_PATTERN_SELECTION );

		// Convert from 255, 255, 255 to a color
		//--------------------------------------
		Color color = ColorUtil.getColorFromRGBString( Display.getCurrent(), strColorSelection );
		if( color == null )
			return;

		// Dispose of any existing color
		//------------------------------
		if( _colorSelected != null && _colorSelected.isDisposed() == false )
			_colorSelected.dispose();

		// Establish the new color
		//------------------------
		_colorSelected = color;

		// Determine if the selected pattern color is white or black
		//----------------------------------------------------------
		int iRed = _colorSelected.getRed();
		int iGrn = _colorSelected.getGreen();
		int iBlu = _colorSelected.getBlue();

		double dLuminance = 0.30 * iRed + 0.59 * iGrn + 0.11 * iBlu;
		_bPatternSelectionWhite = dLuminance < EvConstants.LUMINOSITY_WHITE_BLACK_BOUNDARY;
	}

	/**
	 * Declared in DisposeListener.
	 * Disposes of any colors that are not system colors.
	 */
	public void widgetDisposed( DisposeEvent e ) {
		if( _colorSelected != null && _colorSelected.isDisposed() == false ) {
			_colorSelected.dispose();
			_colorSelected = null;
		}
		
		if( _colorLogicalArea != null && _colorLogicalArea.isDisposed() == false ) {
			_colorLogicalArea.dispose();
			_colorLogicalArea = null;
		}
	}

}
