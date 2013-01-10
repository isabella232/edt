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

import java.util.ArrayList;

import org.eclipse.edt.ide.rui.document.utils.IVEConstants;
import org.eclipse.edt.ide.rui.visualeditor.internal.util.ColorUtil;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPropertyDescriptor;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPropertyValue;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Text;


/**
 *  
 */
public class PropertyEditorPosition extends PropertyEditorAbstract implements DisposeListener, FocusListener, SelectionListener, PaintListener {

	/**
	 * A layout for the x, y, width and height widgets.
	 */
	protected class ControlLayout extends Layout {

		/**
		 *  
		 */
		protected Point computeSize( Composite composite, int wHint, int hHint, boolean flushCache ) {
			String strText = _textX.getText();
			_textX.setText( "MMMM" );
			Point ptSize = _textX.computeSize( SWT.DEFAULT, SWT.DEFAULT );
			_textX.setText( strText );

			// ptSize.x + 32 is the size of the square
			//----------------------------------------
			int iW = 4 + ptSize.x + 16 + ( ptSize.x + 32 ) + 4 + ptSize.x + 4;
			int iH = 4 + ptSize.y + 16 + ( ptSize.x + 32 ) + 4 + ptSize.y + 4;
			
			return new Point( iW, iH );
		}

		/**
		 *  
		 */
		protected void layout( Composite composite, boolean flushCache ) {

			String strText = _textX.getText();
			_textX.setText( "MMMM" );
			Point ptSize = _textX.computeSize( SWT.DEFAULT, SWT.DEFAULT );
			_textX.setText( strText );

			_textX.setBounds( 4, 20 + ptSize.y, ptSize.x, ptSize.y );
			_textY.setBounds( ptSize.x + ptSize.y + 28, 4, ptSize.x, ptSize.y );
			_textWidth.setBounds( ptSize.x + 36, ptSize.y + 20 + ptSize.x + 32 + 4, ptSize.x, ptSize.y );
			_textHeight.setBounds( ptSize.x * 2 + 56, ptSize.y + 20 + ( ptSize.x + 32 - ptSize.y ) / 2, ptSize.x, ptSize.y );
		}
	}
	
	protected Color					_colorCells						= null;
	protected Color					_colorLines						= null;
	protected PropertySheetPage		_page							= null;
	protected WidgetPropertyValue	_propertyValueOriginalHeight	= null;
	protected WidgetPropertyValue	_propertyValueOriginalWidth		= null;
	protected WidgetPropertyValue	_propertyValueOriginalX			= null;
	protected WidgetPropertyValue	_propertyValueOriginalY			= null;
	protected String				_strName						= null;
	protected Text					_textHeight						= null;
	protected Text					_textWidth						= null;
	protected Text					_textX							= null;
	protected Text					_textY							= null;

	/**
	 *  
	 */
	public PropertyEditorPosition( PropertyPage page, WidgetPropertyDescriptor descriptor ) {
		super( page, descriptor );

		_colorCells = ColorUtil.getColorFromRGBString( Display.getCurrent(), "128, 128, 255" );
		_colorLines = Display.getCurrent().getSystemColor( SWT.COLOR_BLACK );
	}

	/**
	 * Creates the user interface for this editor.
	 */
	public void createControl( Composite compositeParent ) {

		Composite composite = new Composite( compositeParent, SWT.NULL );

		GridData gridData = new GridData( GridData.FILL_BOTH );
		gridData.horizontalSpan = 2;
		composite.setLayoutData( gridData );
		composite.setLayout( new ControlLayout() );

		_textX = new Text( composite, SWT.BORDER );
		_textY = new Text( composite, SWT.BORDER );
		_textWidth = new Text( composite, SWT.BORDER );
		_textHeight = new Text( composite, SWT.BORDER );

		_textX.setToolTipText( "x" );
		_textY.setToolTipText( "y" );
		_textWidth.setToolTipText( "width" );
		_textHeight.setToolTipText( "height" );
		
		_textX.addFocusListener( this );
		_textY.addFocusListener( this );
		_textWidth.addFocusListener( this );
		_textHeight.addFocusListener( this );

		_textX.addSelectionListener( this );
		_textY.addSelectionListener( this );
		_textWidth.addSelectionListener( this );
		_textHeight.addSelectionListener( this );

		composite.addDisposeListener( this );
		composite.addPaintListener( this );
	}

	/**
	 * 
	 */
	protected void doValueChange( Text text ) {
		// Disabled if it is a variable
		//-----------------------------
		if( text.getEnabled() == false )
			return;

		WidgetPropertyDescriptor descriptor = new WidgetPropertyDescriptor();

		if( text == _textX ) {
			descriptor._strType = "integer";
			descriptor._strID = descriptor._strLabel = "x";
			WidgetPropertyValue propertyValueNew = new WidgetPropertyValue( _textX.getText() );
			propertyValueChanged( descriptor, _propertyValueOriginalX, propertyValueNew );
		}

		else if( text == _textY ) {
			descriptor._strType = "integer";
			descriptor._strID = descriptor._strLabel = "y";
			WidgetPropertyValue propertyValueNew = new WidgetPropertyValue( _textY.getText() );
			propertyValueChanged( descriptor, _propertyValueOriginalY, propertyValueNew );
		}

		else if( text == _textWidth ) {
			descriptor._strType = "string";
			descriptor._strID = descriptor._strLabel = "width";
			WidgetPropertyValue propertyValueNew = new WidgetPropertyValue( _textWidth.getText() );
			propertyValueChanged( descriptor, _propertyValueOriginalWidth, propertyValueNew );
		}

		else if( text == _textHeight ) {
			descriptor._strType = "string";
			descriptor._strID = descriptor._strLabel = "height";
			WidgetPropertyValue propertyValueNew = new WidgetPropertyValue( _textHeight.getText() );
			propertyValueChanged( descriptor, _propertyValueOriginalHeight, propertyValueNew );
		}
	}

	/**
	 * Declared in FocusListener.  Does nothing.
	 */
	public void focusGained( FocusEvent e ) {
	}

	/**
	 * Declared in FocusListener.
	 */
	public void focusLost( FocusEvent e ) {
		doValueChange( (Text)e.widget );
	}

	/**
	 * Initializes the user interface with the given value.
	 */
	public void initialize() {
		_propertyValueOriginalX      = getPropertyValue( "x", IVEConstants.INTEGER_TYPE );
		_propertyValueOriginalY      = getPropertyValue( "y", IVEConstants.INTEGER_TYPE );
		_propertyValueOriginalWidth  = getPropertyValue( "width", IVEConstants.STRING_TYPE );
		_propertyValueOriginalHeight = getPropertyValue( "height", IVEConstants.STRING_TYPE );

		ArrayList listX      = null;
		ArrayList listY      = null;
		ArrayList listWidth  = null;
		ArrayList listHeight = null;

		if( _propertyValueOriginalX != null )
			listX = _propertyValueOriginalX.getValues();
		
		if( _propertyValueOriginalY != null )
			listY = _propertyValueOriginalY.getValues();
		
		if( _propertyValueOriginalWidth != null )
			listWidth = _propertyValueOriginalWidth.getValues();
		
		if( _propertyValueOriginalHeight != null )
			listHeight = _propertyValueOriginalHeight.getValues();
		
		if( listX == null || listX.size() == 0 )
			_textX.setText( "" );
		else {
			_textX.setText( (String)listX.get( 0 ) );
			_textX.setEnabled( _propertyValueOriginalX.isEditable() );
		}

		if( listY == null || listY.size() == 0 )
			_textY.setText( "" );
		else {
			_textY.setText( (String)listY.get( 0 ) );
			_textY.setEnabled( _propertyValueOriginalY.isEditable() );
		}

		if( listWidth == null || listWidth.size() == 0 )
			_textWidth.setText( "" );
		else {
			_textWidth.setText( (String)listWidth.get( 0 ) );
			_textWidth.setEnabled( _propertyValueOriginalWidth.isEditable() );
		}

		if( listHeight == null || listHeight.size() == 0 )
			_textHeight.setText( "" );
		else {
			_textHeight.setText( (String)listHeight.get( 0 ) );
			_textHeight.setEnabled( _propertyValueOriginalHeight.isEditable() );
		}
	}

	/**
	 *  
	 */
	protected void paintArrowDown( GC gc, int iX, int iY ) {
		gc.drawLine( iX - 2, iY - 4, iX, iY );
		gc.drawLine( iX - 1, iY - 4, iX, iY );
		gc.drawLine( iX + 1, iY - 4, iX, iY );
		gc.drawLine( iX + 2, iY - 4, iX, iY );
	}

	/**
	 *  
	 */
	protected void paintArrowLeft( GC gc, int iX, int iY ) {
		gc.drawLine( iX, iY, iX + 4, iY - 2 );
		gc.drawLine( iX, iY, iX + 4, iY - 1 );
		gc.drawLine( iX, iY, iX + 4, iY + 1 );
		gc.drawLine( iX, iY, iX + 4, iY + 2 );
	}

	/**
	 *  
	 */
	protected void paintArrowRight( GC gc, int iX, int iY ) {
		gc.drawLine( iX - 4, iY - 2, iX, iY );
		gc.drawLine( iX - 4, iY - 1, iX, iY );
		gc.drawLine( iX - 4, iY + 1, iX, iY );
		gc.drawLine( iX - 4, iY + 2, iX, iY );
	}

	/**
	 *  
	 */
	protected void paintArrowUp( GC gc, int iX, int iY ) {
		gc.drawLine( iX, iY, iX - 2, iY + 4 );
		gc.drawLine( iX, iY, iX - 1, iY + 4 );
		gc.drawLine( iX, iY, iX + 1, iY + 4 );
		gc.drawLine( iX, iY, iX + 2, iY + 4 );
	}

	/**
	 *  
	 */
	public void paintControl( PaintEvent e ) {


		e.gc.setBackground( _colorCells );
		e.gc.setForeground( _colorLines );

		Point ptSize = _textX.getSize();

		int iX = ptSize.x + 20;
		int iY = ptSize.y + 20;
		int iW = ptSize.x + 32;
		int iH = ptSize.x + 32; // Height is same as width (square)

		e.gc.fillRectangle( iX, iY, iW, iH );

		// Guide lines
		//------------
		e.gc.drawLine( iX, iY, iX + iW + 4 + ptSize.x, iY );
		e.gc.drawLine( iX, iY, iX, iY + iH + 4 + ptSize.y );
		e.gc.drawLine( iX, iY + iH, iX + iW + 4 + ptSize.x, iY + iH );
		e.gc.drawLine( iX + iW, iY, iX + iW, iY + iH + 4 + ptSize.y );

		// OriginX
		//--------
		iX = 4;
		iY = 24 + ptSize.y * 2;
		iW = ptSize.x + 16;
		iH = 0;

		e.gc.drawLine( iX, iY, iX + iW, iY + iH );
		paintArrowRight( e.gc, iX + iW, iY );

		// OriginY
		//--------
		iX = ptSize.x + ptSize.y + 24;
		iY = 4;
		iW = 0;
		iH = ptSize.y + 16;

		e.gc.drawLine( iX, iY, iX + iW, iY + iH );
		paintArrowDown( e.gc, iX, iY + iH );

		// ExtentX
		//--------
		iX = ptSize.x + 20;
		iY = ptSize.y + 20 + ptSize.x + 32 + 4 + ptSize.y / 2;
		iW = ptSize.x + 32;
		iH = 0;

		e.gc.drawLine( iX, iY, iX + iW, iY + iH );
		paintArrowLeft( e.gc, iX, iY );
		paintArrowRight( e.gc, iX + iW, iY );

		// ExtentY
		//--------
		iX = ptSize.x + 20 + ptSize.x + 32 + 4 + ptSize.x / 2;
		iY = ptSize.y + 20;
		iW = 0;
		iH = ptSize.x + 32;

		e.gc.drawLine( iX, iY, iX + iW, iY + iH );
		paintArrowUp( e.gc, iX, iY );
		paintArrowDown( e.gc, iX, iY + iH );
	}

	/**
	 * Declared in SelectionListener.
	 */
	public void widgetDefaultSelected( SelectionEvent e ) {
		doValueChange( (Text)e.widget );
	}

	/**
	 * Declared in DisposeListener.
	 * Called when the composite is disposed.  All non-system colors are disposed.
	 */
	public void widgetDisposed( DisposeEvent e ) {
		if( _colorCells != null && _colorCells.isDisposed() == false ) {
			_colorCells.dispose();
			_colorCells = null;
		}
	}

	/**
	 * Declared in SelectionListener.  Does nothing.
	 */
	public void widgetSelected( SelectionEvent e ) {
	}
}
