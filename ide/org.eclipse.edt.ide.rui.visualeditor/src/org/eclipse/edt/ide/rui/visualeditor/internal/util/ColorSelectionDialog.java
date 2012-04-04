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
import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvHelp;
import org.eclipse.edt.ide.rui.visualeditor.internal.nl.Messages;
import org.eclipse.edt.ide.rui.visualeditor.internal.nl.Tooltips;
import org.eclipse.edt.ide.rui.visualeditor.plugin.Activator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
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
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;


public class ColorSelectionDialog extends Dialog implements DisposeListener, ModifyListener, SelectionListener {

	protected static int IMAGE_WIDTH = 50;
	protected Button				_buttonColorSelection	= null;
	protected Color[]				_colors					= null;
	protected ColorSelectorButton	_colorSelector			= null;
	protected Combo					_comboNames				= null;
	protected Image[]				_images					= null;
	protected Button				_radioCustom  			= null;
	protected Button				_radioName				= null;
	protected Button				_radioNumbered			= null;
	protected Button				_radioNumberedHex		= null;
	protected Button				_radioNumberedRGB		= null;
	protected String				_strColorValue			= null;
	protected Table					_tableNames				= null;
	protected Text					_textColorValue			= null;
	
	protected Text					_textCustom    			= null;
	
	/**
	 * The color selection dialog works with color strings without the surrounding string quotes.
	 * Surrounding quotes are managed by the property page's color editor.
	 */
	public ColorSelectionDialog( Shell shellParent, String strColorValue ) {
		super( shellParent );
		
		_strColorValue = strColorValue != null ? strColorValue : "";
		
		EvHelp.setHelp( shellParent, EvHelp.COLOR_SELECTION_DIALOG );
	}

	/**
	 * Overrides the superclass to create the dialog content.
	 */
	protected Control createDialogArea( Composite compositeParent ) {
		
		initializeColorImages();
		
		getShell().setText( Messages.NL_Color_selection );
		Image image = Activator.getImage( EvConstants.ICON_COLOR_SELECTION_DIALOG );
		getShell().setImage( image );

		Composite composite = new Composite( compositeParent, SWT.NULL );
		GridLayout gridLayout = new GridLayout();
		composite.setLayout( gridLayout );
		composite.addDisposeListener( this );

		Label labelInstruction = new Label( composite, SWT.NULL );
		GridData gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		labelInstruction.setLayoutData( gridData );
		labelInstruction.setText( Messages.NL_Choose_a_color_and_a_format );

		_radioNumbered = new Button( composite, SWT.RADIO );
		gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		gridData.horizontalIndent = 8;
		_radioNumbered.setLayoutData( gridData );
		_radioNumbered.setText( Messages.NL_Number_format );

		Composite compositeNumberFormat = new Composite( composite, SWT.NULL );
		gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		gridData.horizontalIndent = 16;
		compositeNumberFormat.setLayoutData( gridData );
		gridLayout = new GridLayout();
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.numColumns = 2;
		compositeNumberFormat.setLayout( gridLayout );

		Group group1 = new Group( compositeNumberFormat, SWT.NULL );
		gridData = new GridData( GridData.FILL_VERTICAL | GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		group1.setLayoutData( gridData );
		group1.setText( Messages.NL_Color );
		gridLayout = new GridLayout();
		group1.setLayout( gridLayout );

		_colorSelector = new ColorSelectorButton();
		_buttonColorSelection = _colorSelector.createWidget( group1 );
		gridData = new GridData( GridData.FILL_BOTH );
		gridData.horizontalAlignment = SWT.CENTER;
		gridData.verticalAlignment = SWT.CENTER;
		_buttonColorSelection.setLayoutData( gridData );
		_buttonColorSelection.setToolTipText( Tooltips.NL_Press_to_select_a_color );


		Group group = new Group( compositeNumberFormat, SWT.NULL );
		gridData = new GridData( GridData.FILL_VERTICAL | GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		group.setLayoutData( gridData );
		group.setText( Messages.NL_Format );
		gridLayout = new GridLayout();
		group.setLayout( gridLayout );

		_radioNumberedRGB = new Button( group, SWT.RADIO );
		gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		gridData.horizontalSpan = 2;
		_radioNumberedRGB.setLayoutData( gridData );
		_radioNumberedRGB.setText( Messages.NL_RGB );

		_radioNumberedHex = new Button( group, SWT.RADIO );
		gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		gridData.horizontalSpan = 2;
		_radioNumberedHex.setLayoutData( gridData );
		_radioNumberedHex.setText( Messages.NL_Hexadecimal );

		_radioName = new Button( composite, SWT.RADIO );
		gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		gridData.horizontalIndent = 8;
		_radioName.setLayoutData( gridData );
		_radioName.setText( Messages.NL_Name_format );

		Composite compositeNameFormat = new Composite( composite, SWT.NULL );
		gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		compositeNameFormat.setLayoutData( gridData );
		gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		compositeNameFormat.setLayout( gridLayout );
		
		_tableNames = new Table( compositeNameFormat, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION );
		gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		gridData.heightHint = 200;
		gridData.widthHint = 200;
		gridData.horizontalIndent = 16;
		_tableNames.setLayoutData( gridData );

		TableColumn tableColumn = new TableColumn( _tableNames, SWT.FULL_SELECTION );
		tableColumn.setText( Messages.NL_Color_name );
		TableLayout layoutTable = new TableLayout();
		ColumnLayoutData cLayout = new ColumnWeightData( 100, false );
		layoutTable.addColumnData( cLayout );

		_tableNames.setLinesVisible( true );
		_tableNames.setHeaderVisible( false );
		_tableNames.setLayout( layoutTable );

		String[] straColors = ColorUtil.getColorNames();
		String[] straHex = ColorUtil.getColorHexValues();

		for( int i=0; i<straHex.length; i++ ){
			TableItem item = new TableItem( _tableNames, SWT.NULL );
			item.setText( straColors[i] );
			item.setImage( _images[i] );
		}
		
		_radioCustom = new Button( composite, SWT.RADIO );
		gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		gridData.horizontalIndent = 8;
		_radioCustom.setLayoutData( gridData );
		_radioCustom.setText( Messages.NL_Custom );

		Composite compositeCustom = new Composite( composite, SWT.NULL );
		gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		compositeCustom.setLayoutData( gridData );
		gridLayout = new GridLayout();
		compositeCustom.setLayout( gridLayout );

		_textCustom = new Text( compositeCustom, SWT.BORDER );
		gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		gridData.horizontalIndent = 16;
		_textCustom.setLayoutData( gridData );
		
		Label label = new Label( composite, SWT.SEPARATOR | SWT.HORIZONTAL );
		gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		label.setLayoutData( gridData );

		Composite compositeFormattedValue = new Composite( composite, SWT.NULL );
		gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		compositeFormattedValue.setLayoutData( gridData );
		gridLayout = new GridLayout();
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.numColumns = 2;
		compositeFormattedValue.setLayout( gridLayout );

		label = new Label( compositeFormattedValue, SWT.NULL );
		gridData = new GridData();
		label.setLayoutData( gridData );
		label.setText( Messages.NL_ValueXcolonX );

		_textColorValue = new Text( compositeFormattedValue, SWT.BORDER | SWT.READ_ONLY );
		gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		_textColorValue.setLayoutData( gridData );
		
		initialize();

		// Add listeners
		//--------------
		_colorSelector.setModifyListener( this );
		_tableNames.addSelectionListener( this );
		_radioNumbered.addSelectionListener( this );
		_radioNumberedRGB.addSelectionListener( this );
		_radioNumberedHex.addSelectionListener( this );
		_radioName.addSelectionListener( this );
		_radioCustom.addSelectionListener( this );
		_textCustom.addModifyListener( this );
		
		// Create mnemonics
		//-----------------
		new Mnemonics().setMnemonics( composite );

		return composite;
	}
	
	/**
	 * Returns the current color value
	 */
	public String getColorValue(){
		return _strColorValue;
	}
	
	/**
	 * 
	 */
	protected void initialize() {

		_textColorValue.setText( _strColorValue );

		// Custom, no value
		//-----------------
		if( _strColorValue.length() == 0 ){
			_radioNumbered.setSelection( false );
			_radioNumberedRGB.setSelection( true );
			_radioNumberedHex.setSelection( false );
			_radioName.setSelection( false );
			_radioCustom.setSelection( true );

			_textCustom.setText( _strColorValue );
			
			updateControls();
			return;
		}

		// RGB string
		//-----------
		RGB rgb = ColorUtil.convertRGBStringToRGB( _strColorValue );
		if( rgb != null ) {
			_radioNumbered.setSelection( true );
			_radioNumberedRGB.setSelection( true );
			_radioNumberedHex.setSelection( false );
			_radioName.setSelection( false );
			_radioCustom.setSelection( false );

			_colorSelector.setRGB( rgb );
			
			_strColorValue = ColorUtil.convertRgbToRgbString( rgb );
			_textColorValue.setText( _strColorValue );
			
			updateControls();
			return;
		}

		// Hex string
		//-----------
		rgb = ColorUtil.convertHexStringToRGB( _strColorValue );
		if( rgb != null ) {
			_radioNumbered.setSelection( true );
			_radioNumberedRGB.setSelection( false );
			_radioNumberedHex.setSelection( true );
			_radioName.setSelection( false );
			_radioCustom.setSelection( false );
				
			_colorSelector.setRGB( rgb );

			_strColorValue = ColorUtil.convertRgbToHexString( rgb );
			_textColorValue.setText( _strColorValue );
			
			updateControls();
			return;
		}

		// Color name
		//-----------
		int iIndex = -1;

		String[] straColors = ColorUtil.getColorNames();
				
		for( int i = 0; i < straColors.length; ++i ) {
			if( _strColorValue.equalsIgnoreCase( straColors[ i ] ) == true ) {
				iIndex = i;
				break;
			}
		}

		if( iIndex >= 0 ) {
			_radioNumbered.setSelection( false );
			_radioNumberedRGB.setSelection( true );
			_radioNumberedHex.setSelection( false );
			_radioName.setSelection( true );
			_radioCustom.setSelection( false );

			_tableNames.setSelection( iIndex );

			updateControls();
			return;
		}

		// Custom, non-empty value
		//------------------------
		_radioNumbered.setSelection( false );
		_radioNumberedRGB.setSelection( true );
		_radioNumberedHex.setSelection( false );
		_radioName.setSelection( false );
		_radioCustom.setSelection( true );

		_textCustom.setText( _strColorValue );
		
		updateControls();
	}

	/**
	 * Creates an image for each named color.
	 */
	protected void initializeColorImages(){
		
		GC gc = new GC( getShell() );
		Font f = JFaceResources.getFontRegistry().get( JFaceResources.DEFAULT_FONT );
		gc.setFont( f );
		int iImageHeight = gc.getFontMetrics().getHeight();
		gc.dispose();

		Color colorBlack = getShell().getDisplay().getSystemColor( SWT.COLOR_BLACK );

		String[] straColors = ColorUtil.getColorNames();
		String[] straHex	= ColorUtil.getColorHexValues();

		_colors = new Color[ straColors.length ];
		_images = new Image[ straColors.length ];
		
		for( int i=0; i<straHex.length; i++ ){
			// Convert to decimal
			//-------------------
			String strR = straHex[i].substring( 0, 2 );
			String strG = straHex[i].substring( 2, 4 );
			String strB = straHex[i].substring( 4 );
			
			int iR = Integer.parseInt( strR, 16 );
			int iG = Integer.parseInt( strG, 16 );
			int iB = Integer.parseInt( strB, 16 );
			
			RGB rgb = new RGB( iR, iG, iB );
			
			_colors[i] = new Color( getShell().getDisplay(), rgb );
			_images[i] = new Image( getShell().getDisplay(), IMAGE_WIDTH, iImageHeight );

			// Fill the background with the button color
			//------------------------------------------
			gc = new GC( _images[i] );
			gc.setBackground( _colors[i] );
			gc.fillRectangle( 0, 0, IMAGE_WIDTH, iImageHeight );
			gc.setBackground( colorBlack );
			gc.drawRectangle( 0, 0, IMAGE_WIDTH - 1, iImageHeight - 1 );
			gc.dispose();
		}
	}

	/**
	 * Called when the color selection button has a new value.
	 */
	public void modifyText( ModifyEvent event ) {
		if( event.widget == _buttonColorSelection ){
			
			RGB rgb = _colorSelector.getRGB();
			
			// RGB value in the form RGB(255,255,255)
			///--------------------------------------
			if( _radioNumberedRGB.getSelection() == true ){
				String strRGB = ColorUtil.convertRgbToRgbString( rgb );
				_textColorValue.setText( strRGB );
			}
			
			// Hex value in the form #aabbcc
			//------------------------------
			else{
				String strHex = ColorUtil.convertRgbToHexString( rgb );
				_textColorValue.setText( strHex );
			}
		}
		
		// The color value is the same as the custom value
		//------------------------------------------------
		else if( event.widget == _textCustom ){
			_textColorValue.setText(  _textCustom.getText() );
		}

	}

	/**
	 * Overrides the super class so that we can save the new value.
	 */
	protected void okPressed() {
		
		// Remember the new value before the text widget is disposed
		//----------------------------------------------------------
		_strColorValue = _textColorValue.getText();
		
		// Set the return code to OK and close the dialog
		//-----------------------------------------------
		super.okPressed();
	}

	/**
	 * Enables and disables controls based on the radio buttons.
	 */
	protected void updateControls(){
		boolean bNumbered = _radioNumbered.getSelection();
		boolean bNamed = _radioName.getSelection();
		boolean bCustom = _radioCustom.getSelection();
		
		_buttonColorSelection.setEnabled( bNumbered );
		_radioNumberedRGB.setEnabled( bNumbered );
		_radioNumberedHex.setEnabled( bNumbered );
		_tableNames.setEnabled( bNamed );
		_textCustom.setEnabled( bCustom );
	}

	/**
	 * Declared in SelectionListener.
	 */
	public void widgetDefaultSelected( SelectionEvent event ) {
		widgetSelected( event );
	}

	/**
	 * Declared in DisposeListener.
	 * Disposes of images and colors.
	 */
	public void widgetDisposed( DisposeEvent e ) {
		for( int i=0; i<_colors.length; ++i )
			_colors[i].dispose();
		
		for( int i=0; i<_images.length; ++i )
			_images[i].dispose();
	}

	/**
	 * Declared in SelectionListener.
	 */
	public void widgetSelected( SelectionEvent event ) {
		if( event.getSource() == _tableNames ){
			TableItem[] tableItems = _tableNames.getSelection();
			if( tableItems.length == 0 )
				return;

			String strTableSelection = tableItems[0].getText();
			_textColorValue.setText( strTableSelection );
		}
		
		else if( event.getSource() == _radioNumbered ){
			updateControls();
			
			if( _radioNumberedRGB.getSelection() == true ){
				RGB rgb = _colorSelector.getRGB();
				String strRGB = ColorUtil.convertRgbToRgbString( rgb );
				_textColorValue.setText( strRGB );
			}
			else{
				RGB rgb = _colorSelector.getRGB();
				String strHex = ColorUtil.convertRgbToHexString( rgb );
				_textColorValue.setText( strHex );
			}
		}
			
		else if( event.getSource() == _radioNumberedRGB ){
			RGB rgb = _colorSelector.getRGB();
			String strRGB = ColorUtil.convertRgbToRgbString( rgb );
			_textColorValue.setText( strRGB );
		}
			
		else if( event.getSource() == _radioNumberedHex ){
			RGB rgb = _colorSelector.getRGB();
			String strHex = ColorUtil.convertRgbToHexString( rgb );
			_textColorValue.setText( strHex );
		}

		else if( event.getSource() == _radioName ){
			updateControls();

			TableItem[] tableItems = _tableNames.getSelection();
			if( tableItems.length == 0 ){
				_tableNames.setSelection( 0 );
				tableItems = _tableNames.getSelection();
			}
			
			String strTableSelection = tableItems[0].getText();
			_textColorValue.setText( strTableSelection );
		}
		
		else if( event.getSource() == _radioCustom ){
			updateControls();
			_textColorValue.setText(  _textCustom.getText() );
		}
	}
}
