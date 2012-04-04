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
package org.eclipse.edt.ide.rui.visualeditor.internal.util;

import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;


/**
 *
 */
public class ColorSelectorButton implements DisposeListener, SelectionListener {
	public static int			PATTERN_NONE	= 0;
	public static int			PATTERN_HATCH	= 1;
	public static int			PATTERN_DENTAL	= 2;

	protected boolean			_bEnabled		= true;
	protected Button			_button			= null;
	protected Color				_color			= null;
	protected Image				_image			= null;
	protected int				_iPattern		= PATTERN_NONE;
	protected Point				_ptExtents		= null;
	protected ModifyListener	_modifyListener	= null;
	protected RGB				_rgbColorValue	= new RGB( 128, 128, 128 );

	/**
	 * Constructor for RichButton.
	 */
	public ColorSelectorButton() {
	}

	/**
	 * Computes the size for the image.
	 * 
	 * @param window the window on which to render the image
	 * @return the point with the image size
	 */
	protected Point computeImageSize( Control window ) {
		GC gc = new GC( window );
		Font f = JFaceResources.getFontRegistry().get( JFaceResources.DEFAULT_FONT );
		gc.setFont( f );
		int height = gc.getFontMetrics().getHeight();
		gc.dispose();
		Point p = new Point( height * 3 - 6, height );
		return p;
	}

	/**
	 * Creates a button.
	 */
	public Button createWidget( Object objParent ) {

		if( objParent instanceof Composite == false )
			return null;

		_button = new Button( (Composite)objParent, SWT.PUSH );

		_rgbColorValue = _button.getBackground().getRGB();
		_color = new Color( _button.getDisplay(), _rgbColorValue );

		_ptExtents = computeImageSize( (Composite)objParent );
		_image = new Image( _button.getDisplay(), _ptExtents.x, _ptExtents.y );

		// Fill the background with the button color
		//------------------------------------------
		GC gc = new GC( _image );
		gc.setBackground( _button.getBackground() );
		gc.fillRectangle( 0, 0, _ptExtents.x, _ptExtents.y );
		gc.dispose();

		// Fill the image part of the button
		//----------------------------------
		updateColorImage();

		_button.setImage( _image );
		_button.addDisposeListener( this );
		_button.addSelectionListener( this );

		return _button;
	}

	/**
	 * Returns an RGB color string in the form r, g, b.
	 */
	public String getColor() {
		return ColorUtil.getRGBString( _rgbColorValue );
	}
	
	/**
	 * Returns the current RGB value.
	 */
	public RGB getRGB(){
		return _rgbColorValue;
	}
	
	/**
	 * 
	 */
	public void initialize() {
		updateColorImage();
		_button.setEnabled( _bEnabled );
	}

	/**
	 * 
	 */
	public void setColor( String strColor ) {
		_rgbColorValue = ColorUtil.convertStringToRGB( strColor );

		if( _button != null && strColor != null )
			updateColorImage();
	}

	/**
	 * 
	 */
	public void setEnabled( boolean bEnabled ) {
		_bEnabled = bEnabled;

		if( _button != null )
			_button.setEnabled( _bEnabled );
	}

	/**
	 * Called by the user of this class, such as a color selection dialog.
	 * The listener will be notified when the color value has changed.
	 */
	public void setModifyListener( ModifyListener modifyListener ) {
		_modifyListener = modifyListener;
	}

	/**
	 * Sets whether a pattern should be drawn.
	 */
	public void setPattern( int iPattern ){
		_iPattern = iPattern;
		updateColorImage();
	}
	
	/**
	 * Sets the initial color value.
	 */
	public void setRGB( RGB rgb ){
		_rgbColorValue = rgb;
		updateColorImage();
	}
	
	/**
	 * Sets the button's tooltip text.
	 */
	public void setTooltip( String strTooltip ) {
		_button.setToolTipText( strTooltip );
	}
	
	/**
	 * Updates the color of the button image.
	 */
	protected void updateColorImage() {

		Display display = _button.getDisplay();

		GC gc = new GC( _image );

		// Fill color
		//-----------
		if( _color != null && _color.isDisposed() == false )
			_color.dispose();

		_color = new Color( display, _rgbColorValue );

		gc.setBackground( _color );
		gc.fillRectangle( 1, 3, _ptExtents.x - 2, _ptExtents.y - 5 );
		
		// Pattern lines
		// Determine whether to use black or white lines
		// Luminance = 0.30Red + 0.59Grn + 0.11Blu
		//----------------------------------------------
		if( _iPattern != PATTERN_NONE ) {
			int iRed = _color.getRed();
			int iGrn = _color.getGreen();
			int iBlu = _color.getBlue();

			double dLuminance = 0.30 * iRed + 0.59 * iGrn + 0.11 * iBlu;
			boolean bWhite = dLuminance < EvConstants.LUMINOSITY_WHITE_BLACK_BOUNDARY;

			gc.setLineStyle( SWT.LINE_SOLID );
			gc.setLineWidth( 1 );
			gc.setForeground( Display.getCurrent().getSystemColor( bWhite ? SWT.COLOR_WHITE : SWT.COLOR_BLACK ) );

			if( _iPattern == PATTERN_HATCH ) {
				// Vertical lines
				//---------------
				for( int i = 4; i < _ptExtents.x - 4; i += 4 )
					gc.drawLine( i, 2, i, _ptExtents.y - 2 );

				// Horizontal lines
				//-----------------
				for( int i = 4; i < _ptExtents.y - 5; i += 4 )
					gc.drawLine( 0, 2 + i, _ptExtents.x - 2, 2 + i );
			}

			else if( _iPattern == PATTERN_DENTAL ) {
				gc.setLineStyle( SWT.LINE_DOT );
				gc.setLineWidth( 3 );
				gc.drawLine( 0, 3, _ptExtents.x, 3 );
				gc.drawLine( 0, _ptExtents.y - 3, _ptExtents.x, _ptExtents.y - 3 );
			}
		}
		
		// Outer border
		//-------------
		gc.setLineStyle( SWT.LINE_SOLID );
		gc.setLineWidth( 1 );
		gc.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
		gc.drawRectangle( 0, 2, _ptExtents.x - 1, _ptExtents.y - 4 );

		gc.dispose();

		_button.setImage( _image );
	}

	/**
	 * Declared in SelectionListener.  Defers to the widgetSelected method.
	 */
	public void widgetDefaultSelected( SelectionEvent event ) {
		widgetSelected( event );
	}

	/**
	 * Declared in DisposeListener.  Called when the button is disposed.
	 * The color and image are disposed.
	 */
	public void widgetDisposed( DisposeEvent e ) {
		if( _color != null && _color.isDisposed() == false ) {
			_color.dispose();
			_color = null;
		}

		if( _image != null && _image.isDisposed() == false ) {
			_image.dispose();
			_image = null;
		}
	}

	/**
	 * Declared in SelectionListener.
	 * Called when the button is pushed.  The color dialog is presented.
	 * After the dialog closes, the modify listener is notified.
	 */
	public void widgetSelected( SelectionEvent selectionEvent ) {
		if( _button.isDisposed() )
			return;

		ColorDialog colorDialog = new ColorDialog( _button.getShell() );

		colorDialog.setRGB( _rgbColorValue );

		RGB rgbSelected = colorDialog.open();

		if( rgbSelected != null ) {
			_rgbColorValue = rgbSelected;

			updateColorImage();

			if( _modifyListener != null ) {
				Event event = new Event();
				event.widget = _button;
				_modifyListener.modifyText( new ModifyEvent( event ) );
			}
		}
	}
}
