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
package org.eclipse.edt.ide.rui.visualeditor.internal.properties;

import java.util.ArrayList;

import org.eclipse.edt.ide.rui.visualeditor.internal.nl.Tooltips;
import org.eclipse.edt.ide.rui.visualeditor.internal.util.ColorSelectionDialog;
import org.eclipse.edt.ide.rui.visualeditor.internal.util.ColorUtil;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPropertyDescriptor;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPropertyValue;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;


public class PropertyEditorColor extends PropertyEditorAbstract implements DisposeListener, FocusListener, SelectionListener {

	protected Button				_button					= null;
	protected Color					_color					= null;
	protected Image					_image					= null;
	protected Point					_ptExtents				= null;
	protected WidgetPropertyValue	_propertyValueOriginal	= null;
	protected RGB					_rgbColorValue			= null;
	protected Text					_textValue				= null;

	/**
	 * 
	 */
	public PropertyEditorColor( PropertyPage page, WidgetPropertyDescriptor descriptor ) {
		super( page, descriptor );
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
	 * Creates the user interface for this editor.
	 */
	public void createControl( Composite compositeParent ) {
		Composite composite = new Composite( compositeParent, SWT.NULL );
		GridData gridData = new GridData( GridData.FILL_HORIZONTAL );
		composite.setLayoutData( gridData );
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		composite.setLayout( gridLayout );
		composite.setBackground( compositeParent.getBackground() );

		_button = new Button( composite, SWT.PUSH );
		_button.setBackground( compositeParent.getBackground() );
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.END;
		_button.setLayoutData( gridData );
		_button.setToolTipText( Tooltips.NL_Press_to_select_a_color );

		_textValue = new Text( composite, SWT.BORDER | SWT.READ_ONLY );
		_textValue.setBackground( compositeParent.getBackground() );
		gridData = new GridData( GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL );
		gridData.widthHint = WIDTH_HINT - 20;
		_textValue.setLayoutData( gridData );
		_textValue.addSelectionListener( this );
		_textValue.addFocusListener( this );

		_ptExtents = computeImageSize( compositeParent );
		_image = new Image( compositeParent.getDisplay(), _ptExtents.x, _ptExtents.y );

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

		return;
	}

	/**
	 * Declared in focus listener.  Does nothing.
	 */
	public void focusGained( FocusEvent e ) {
	}

	/**
	 * Declared in focus listener.
	 */
	public void focusLost( FocusEvent e ) {

		String strColorValue = _textValue.getText();
		if( strColorValue.length() > 0 )
			_rgbColorValue = ColorUtil.convertStringToRGB( strColorValue );
		else
			_rgbColorValue = null;
		
		updateColorImage();

		WidgetPropertyValue propertyValue = new WidgetPropertyValue( _textValue.getText() );
		super.propertyValueChanged( getPropertyDescriptor(), _propertyValueOriginal, propertyValue );
	}

	/**
	 * Initializes the user interface with the given value.
	 */
	public void initialize() {

		_propertyValueOriginal = getPropertyValue( getPropertyDescriptor().getID(), getPropertyDescriptor().getType() );

		if( _propertyValueOriginal == null ){
			reset();
			return;
		}
		
		ArrayList listValues = _propertyValueOriginal.getValues();
		
		if( listValues == null || listValues.size() == 0 ){
			reset();
			return;
		}
		
		String strValue = (String)listValues.get( 0 );

		if( strValue == null || strValue.length() == 0 ){
			reset();
			return;
		}
		
		_rgbColorValue = ColorUtil.convertStringToRGB( strValue );
		_textValue.setText( strValue );
		updateColorImage();

		// Enable the button if the property is editable
		//----------------------------------------------
		_button.setEnabled( _propertyValueOriginal.isEditable() );
	}
	
	/**
	 * Resets the text to an empty string and the color image to the button background color.
	 */
	protected void reset(){
		_textValue.setText( "" );
		_rgbColorValue = null;
		updateColorImage();
	}

	/**
	 * Updates the color of the button image.
	 */
	protected void updateColorImage() {
		Display display = _button.getDisplay();

		GC gc = new GC( _image );

		if( _rgbColorValue != null ) {
			gc.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
			gc.drawRectangle( 0, 2, _ptExtents.x - 1, _ptExtents.y - 4 );
		}

		if( _color != null && _color.isDisposed() == false ) {
			_color.dispose();
			_color = null;
		}

		if( _rgbColorValue != null ) {
			_color = new Color( display, _rgbColorValue );
			gc.setBackground( _color );
			gc.fillRectangle( 1, 3, _ptExtents.x - 2, _ptExtents.y - 5 );
		}
		else {
			gc.setBackground( _button.getBackground() );
			gc.fillRectangle( 0, 0, _ptExtents.x, _ptExtents.y );
		}

		gc.dispose();

		_button.setImage( _image );
	}

	/**
	 * Declared in SelectionListener.  Listens to the enter key pressed on the text field.
	 */
	public void widgetDefaultSelected( SelectionEvent e ) {
		String strColorValue = _textValue.getText();
		_rgbColorValue = ColorUtil.convertStringToRGB( strColorValue );
		updateColorImage();

		WidgetPropertyValue propertyValue = new WidgetPropertyValue( strColorValue );
		super.propertyValueChanged( getPropertyDescriptor(), _propertyValueOriginal, propertyValue );
	}

	/**
	 * Declared in DisposeListener.
	 * Called when the button is disposed.  The color and image are disposed.
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
	 * Declared in SelectionListener.  Called when the color selection button is pressed.
	 */
	public void widgetSelected( SelectionEvent event ) {

		// Open the color selection dialog
		//--------------------------------
		if( event.widget == _button ) {
			if( _button.isDisposed() )
				return;

			String strValue = _textValue.getText();

			// Since literal strings do not have surrounding quotes,
			// we shouldn't find any leading / trailing quotes.
			// Strip off leading quote
			//------------------------------------------------------
			if( strValue.length() > 0 && strValue.charAt( 0 ) == '\"' )
				strValue = strValue.substring( 1 );

			// Strip off trailing quote
			//-------------------------
			if( strValue.length() > 0 && strValue.charAt( strValue.length() - 1 ) == '\"' )
				strValue = strValue.substring( 0, strValue.length() - 1 );

			// Open the color selection dialog
			//--------------------------------
			ColorSelectionDialog colorDialog = new ColorSelectionDialog( _button.getShell(), strValue );
			int iRC = colorDialog.open();
			if( iRC == ColorSelectionDialog.CANCEL )
				return;

			// Obtain the new value
			//---------------------
			String strColorValue = colorDialog.getColorValue();

			// We no longer add surrounding quotes
			//------------------------------------
			_textValue.setText( strColorValue );

			if( strColorValue.length() > 0 )
				_rgbColorValue = ColorUtil.convertStringToRGB( strColorValue );
			else
				_rgbColorValue = null;
				
			updateColorImage();

			WidgetPropertyValue propertyValue = new WidgetPropertyValue( strColorValue );
			super.propertyValueChanged( getPropertyDescriptor(), _propertyValueOriginal, propertyValue );
			
			_textValue.setFocus();
		}
	}
}
