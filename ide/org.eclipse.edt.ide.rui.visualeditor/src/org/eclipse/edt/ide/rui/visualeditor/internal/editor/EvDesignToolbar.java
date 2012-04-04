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

import java.text.DecimalFormat;

import org.eclipse.edt.compiler.internal.EGLBasePlugin;
import org.eclipse.edt.ide.rui.visualeditor.internal.actions.EvActionBidiSettings;
import org.eclipse.edt.ide.rui.visualeditor.internal.actions.EvActionPreferences;
import org.eclipse.edt.ide.rui.visualeditor.internal.nl.Tooltips;
import org.eclipse.edt.ide.rui.visualeditor.internal.preferences.EvPreferences;
import org.eclipse.edt.ide.rui.visualeditor.internal.util.BidiFormat;
import org.eclipse.edt.ide.rui.visualeditor.internal.util.BidiUtils;
import org.eclipse.edt.ide.rui.visualeditor.internal.util.TranslationTestMode;
import org.eclipse.edt.ide.rui.visualeditor.plugin.Activator;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;


public class EvDesignToolbar extends Composite implements SelectionListener, IPropertyChangeListener {
	protected Button		_buttonOverlayOnTop				= null;
	protected Button		_buttonBrowserOnTop				= null;
	protected Button		_buttonTranslationTest			= null;
	protected Composite		_compositeTransparencyControls	= null;
	protected DecimalFormat	_decimalFormat					= new DecimalFormat( "####%" );
	protected ToolItem		_itemPreferences				= null;
	protected ToolItem		_itemRefreshPalette				= null;
	protected ToolItem		_itemRefreshWebPage				= null;
	protected ToolItem		_itemShowBrowserSizeControls	= null;
	protected ToolItem		_itemShowTransparencyControls	= null;
	protected ToolItem		_itemTransparencyDisabled		= null;
	protected ToolItem		_itemTransparencyFixed			= null;
	protected ToolItem		_itemTransparencyVariable		= null;
	protected Label			_labelTransparencyPercent		= null;
	protected EvDesignPage	_pageDesign						= null;
	protected Slider		_sliderTransparencyAmount		= null;

	// IBM BIDI Append Start
	protected ToolItem		_itemBidiPreferences			= null;

	// IBM BIDI Append End

	/**
	 * 
	 */
	public EvDesignToolbar( Composite compositeParent, int style, EvDesignPage pageDesign ) {
		super( compositeParent, style );

		_pageDesign = pageDesign;

		// For translation verification test, an
		// extra TVT button is placed on the toolbar
		// This happens when the file TVT_TVT.egl is opened
		//-------------------------------------------------
		boolean bTranslationTest = pageDesign.getEditor().isTranslationTestMode();
		
		// For debugging, it may be handy to
		// change the z-order of the browser widget
		// and the overlay.  Change this variable to
		// true to place the buttons on the tool bar
		//-------------------------------------------
		boolean bShowZOrderControls = false;
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;

		if( bTranslationTest == true )
			gridLayout.numColumns++;

		if( bShowZOrderControls == true )
			gridLayout.numColumns += 2;
		
		gridLayout.marginWidth = 16;
		gridLayout.marginHeight = 1;
		gridLayout.horizontalSpacing = 2;
		setLayout( gridLayout );

		if( bTranslationTest == true ) {
			_buttonTranslationTest = new Button( this, SWT.PUSH );
			_buttonTranslationTest.setText( "TVT" );
			_buttonTranslationTest.addSelectionListener( this );
		}

		if( bShowZOrderControls == true ){
			_buttonBrowserOnTop = new Button( this, SWT.PUSH );
			_buttonBrowserOnTop.setText( "Browser" );
			GridData gridData = new GridData();
			_buttonBrowserOnTop.setLayoutData( gridData );
			_buttonBrowserOnTop.addSelectionListener( this );

			_buttonOverlayOnTop = new Button( this, SWT.PUSH );
			_buttonOverlayOnTop.setText( "Overlay" );
			gridData = new GridData();
			_buttonOverlayOnTop.setLayoutData( gridData );
			_buttonOverlayOnTop.addSelectionListener( this );
			_buttonOverlayOnTop.setEnabled( false );
		}
		
		// 1
		Label label = new Label( this, SWT.NULL );
		GridData gridData = new GridData( GridData.GRAB_HORIZONTAL );
		label.setLayoutData( gridData );

		// 2
		createTransparencyComposite( this );

		// 3
		label = new Label( this, SWT.NULL );
		gridData = new GridData();
		gridData.widthHint = 16;
		label.setLayoutData( gridData );

		// 4
		ToolBar toolbar = new ToolBar( this, SWT.HORIZONTAL | SWT.FLAT );
		gridData = new GridData( GridData.HORIZONTAL_ALIGN_END );
		toolbar.setLayoutData( gridData );

		_itemShowTransparencyControls = createShowTransparencyControls( toolbar );
		_itemShowBrowserSizeControls = createShowBrowserSizeControls( toolbar );

		// Options and preferences
		//------------------------
		new ToolItem( toolbar, SWT.SEPARATOR );

		// IBM BIDI Append Start		
		_itemBidiPreferences = createBidiPreferences( toolbar );
		_itemBidiPreferences.setEnabled( BidiUtils.isBidi() );
		EGLBasePlugin.getPlugin().getPreferenceStore().addPropertyChangeListener(this);
		// IBM BIDI Append End

		_itemPreferences = createPreferences( toolbar );

		// Refresh palette
		//----------------
		new ToolItem( toolbar, SWT.SEPARATOR );

		_itemRefreshPalette = createRefreshPalette( toolbar );
		_itemRefreshWebPage = createRefreshWebPage( toolbar );

		// Help
		//-----
		EvHelp.setHelp( toolbar, EvHelp.DESIGN_TOOLBAR );

		for( int i = 0; i < toolbar.getItemCount(); ++i )
			EvHelp.setHelp( toolbar.getItem( i ).getControl(), EvHelp.DESIGN_TOOLBAR );

		EvHelp.setHelp( _buttonOverlayOnTop, EvHelp.DESIGN_TOOLBAR );
		EvHelp.setHelp( _buttonBrowserOnTop, EvHelp.DESIGN_TOOLBAR );
	}

	// IBM BIDI Append Start
	/**
	 * Creates the bidirectional preferences toolbar item.
	 */
	protected ToolItem createBidiPreferences( ToolBar toolbar ) {
		ToolItem item = new ToolItem( toolbar, SWT.PUSH | SWT.FLAT );
		item.setImage( Activator.getImage( EvConstants.ICON_BIDI_SETTINGS ) );
		item.addSelectionListener( this );
		item.setToolTipText( Tooltips.NL_Configure_bidirectional_options );
		return item;
	}
	// IBM BIDI Append End

	/**
	 * Creates the preferences toolbar item.
	 */
	protected ToolItem createPreferences( ToolBar toolbar ) {
		ToolItem item = new ToolItem( toolbar, SWT.PUSH | SWT.FLAT );
		item.setImage( Activator.getImage( EvConstants.ICON_PREFERENCES ) );
		item.addSelectionListener( this );
		item.setToolTipText( Tooltips.NL_Configure_preferences );
		return item;
	}

	/**
	 * 
	 */
	protected ToolItem createRefreshPalette( ToolBar toolbar ) {
		ToolItem item = new ToolItem( toolbar, SWT.PUSH | SWT.FLAT );
		item.setImage( Activator.getImage( EvConstants.ICON_REFRESH_PALETTE ) );
		item.addSelectionListener( this );
		item.setToolTipText( Tooltips.NL_Refresh_palette );
		return item;
	}

	/**
	 * 
	 */
	protected ToolItem createRefreshWebPage( ToolBar toolbar ) {
		ToolItem item = new ToolItem( toolbar, SWT.PUSH | SWT.FLAT );
		item.setImage( Activator.getImage( EvConstants.ICON_REFRESH_WEB_PAGE ) );
		item.addSelectionListener( this );
		item.setToolTipText( Tooltips.NL_Refresh_web_page );
		
		// Disable if no RUI handler
		//--------------------------
		item.setEnabled( _pageDesign.getEditor().isRuiHandler() );
		return item;
	}

	/**
	 * Creates the 'Show browser size controls' toolbar item.
	 */
	protected ToolItem createShowBrowserSizeControls( ToolBar toolbar ) {
		ToolItem item = new ToolItem( toolbar, SWT.CHECK | SWT.FLAT );
		item.setImage( Activator.getImage( EvConstants.ICON_SHOW_BROWSER_SIZE_CONTROLS ) );
		item.setToolTipText( Tooltips.NL_Show_browser_size_controls );
		boolean bShowBrowserSizeControls = EvPreferences.getBoolean( EvConstants.PREFERENCE_BROWSER_SIZE_CONTROLS_VISIBLE );
		item.setSelection( bShowBrowserSizeControls );
		item.addSelectionListener( this );
		return item;
	}

	/**
	 * Creates the 'Show transparency controls' toolbar item.
	 */
	protected ToolItem createShowTransparencyControls( ToolBar toolbar ) {
		ToolItem item = new ToolItem( toolbar, SWT.CHECK | SWT.FLAT );
		item.setImage( Activator.getImage( EvConstants.ICON_SHOW_TRANSPARENCY_CONTROLS ) );
		item.setToolTipText( Tooltips.NL_Show_transparency_controls );
		boolean bShowBrowserSizeControls = EvPreferences.getBoolean( EvConstants.PREFERENCE_TRANSPARENCY_CONTROLS_VISIBLE );
		item.setSelection( bShowBrowserSizeControls );
		item.addSelectionListener( this );
		return item;
	}

	/**
	 * Creates the transparency controls
	 */
	protected void createTransparencyComposite( Composite compositeParent ) {
		_compositeTransparencyControls = new Composite( compositeParent, SWT.NULL );
		GridData gridData = new GridData( GridData.HORIZONTAL_ALIGN_END );
		_compositeTransparencyControls.setLayoutData( gridData );
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.horizontalSpacing = 2;
		gridLayout.marginWidth = 4;
		gridLayout.marginHeight = 0;
		_compositeTransparencyControls.setLayout( gridLayout );

		ToolBar toolbar = new ToolBar( _compositeTransparencyControls, SWT.HORIZONTAL | SWT.FLAT );
		gridData = new GridData( GridData.HORIZONTAL_ALIGN_END );
		toolbar.setLayoutData( gridData );

		int iTransparencyMode = EvPreferences.getInt( EvConstants.PREFERENCE_SEMITRANSPARENCY_MODE );

		_itemTransparencyDisabled = new ToolItem( toolbar, SWT.RADIO );
		_itemTransparencyDisabled.setImage( Activator.getImage( EvConstants.ICON_TRANSPARENCY_NONE ) );
		_itemTransparencyDisabled.setSelection( iTransparencyMode == EvConstants.SEMITRANSPARENCY_NONE );
		_itemTransparencyDisabled.addSelectionListener( this );
		_itemTransparencyDisabled.setToolTipText( Tooltips.NL_Fully_transparent );

		_itemTransparencyFixed = new ToolItem( toolbar, SWT.RADIO );
		_itemTransparencyFixed.setImage( Activator.getImage( EvConstants.ICON_TRANSPARENCY_FIXED ) );
		_itemTransparencyFixed.setSelection( iTransparencyMode == EvConstants.SEMITRANSPARENCY_FIXED );
		_itemTransparencyFixed.addSelectionListener( this );
		_itemTransparencyFixed.setToolTipText( Tooltips.NL_Dotted_transparency_pattern );

		// The variable transparency controls are not available if the operating
		// system doesn't support transparency
		//----------------------------------------------------------------------
		if( _pageDesign.isGraphicsTransparencyAvailable() == true ) {
			_itemTransparencyVariable = new ToolItem( toolbar, SWT.RADIO );
			_itemTransparencyVariable.setImage( Activator.getImage( EvConstants.ICON_TRANSPARENCY_VARIABLE ) );
			_itemTransparencyVariable.setSelection( iTransparencyMode == EvConstants.SEMITRANSPARENCY_VARIABLE );
			_itemTransparencyVariable.addSelectionListener( this );
			_itemTransparencyVariable.setToolTipText( Tooltips.NL_Variable_transparency );

			_sliderTransparencyAmount = new Slider( _compositeTransparencyControls, SWT.NULL );
			gridData = new GridData();
			gridData.widthHint = 100;
			_sliderTransparencyAmount.setLayoutData( gridData );
			_sliderTransparencyAmount.setToolTipText( Tooltips.NL_Variable_transparency );
			int iTransparencyAmount = EvPreferences.getInt( EvConstants.PREFERENCE_SEMITRANSPARENCY_AMOUNT );
			_sliderTransparencyAmount.setValues( iTransparencyAmount, 0, 255 + 1, 1, 1, 10 );
			_sliderTransparencyAmount.addSelectionListener( this );
		
			_labelTransparencyPercent = new Label( _compositeTransparencyControls, SWT.NULL );
			gridData = new GridData();
			_labelTransparencyPercent.setLayoutData( gridData );
			_labelTransparencyPercent.setText( "000%" );
		}

		EvHelp.setHelp( _compositeTransparencyControls, EvHelp.DESIGN_TOOLBAR );
		EvHelp.setHelp( toolbar, EvHelp.DESIGN_TOOLBAR );
		
		if( _sliderTransparencyAmount != null )
			EvHelp.setHelp( _sliderTransparencyAmount, EvHelp.DESIGN_TOOLBAR );

		for( int i = 0; i < toolbar.getItemCount(); ++i )
			EvHelp.setHelp( toolbar.getItem( i ).getControl(), EvHelp.DESIGN_TOOLBAR );

		updateTransparencyControls();

		// Show/hide the transparency controls
		//------------------------------------
		boolean bShowTransparencyControls = EvPreferences.getBoolean( EvConstants.PREFERENCE_TRANSPARENCY_CONTROLS_VISIBLE );
		_compositeTransparencyControls.setVisible( bShowTransparencyControls );
	}

	// IBM BIDI Append Start
	public void propertyChange(PropertyChangeEvent event) {
		if (EGLBasePlugin.BIDI_ENABLED_OPTION.equals(event.getProperty())){
			if (!_itemBidiPreferences.isDisposed())
				_itemBidiPreferences.setEnabled( ((Boolean)event.getNewValue()).booleanValue() );			
		}
	}
	// IBM BIDI Append End

	/**
	 * Updates the transparency amount slider position and transparency percentage amount
	 */
	public void updateTransparencyControls() {
		if( _sliderTransparencyAmount == null )
			return;
		
		_sliderTransparencyAmount.setEnabled( _itemTransparencyVariable.getSelection() == true );

		int iPercent = ( _sliderTransparencyAmount.getSelection() * 100 ) / 255;

		// Space is added to the end, otherwise the label not wide enough if the value is single/double digit
		//---------------------------------------------------------------------------------------------------
		StringBuffer strb = new StringBuffer();
		double dPercentage = new Double( iPercent ).doubleValue();

		if( dPercentage < 100d ) // 0 to 100
			strb.append( "  " );
		if( dPercentage < 0.1d )
			strb.append( "  " );

		_labelTransparencyPercent.setText( _decimalFormat.format( dPercentage / 100 ) + strb.toString() );
	}

	/**
	 * Declared in SelectionListener.
	 * Defers an 'enter' key press to the widgetSelected method.
	 */
	public void widgetDefaultSelected( SelectionEvent event ) {
		widgetSelected( event );
	}

	/**
	 * Declared in SelectionListener.
	 */
	public void widgetSelected( SelectionEvent event ) {

		// IBM BIDI Append Start
		if( event.widget == _itemBidiPreferences ) {
			BidiFormat bidiFormat = _pageDesign.getBidiFormat();
			EvActionBidiSettings action = new EvActionBidiSettings( bidiFormat );
			action.run();
			bidiFormat = action.getBidiFormat();
			_pageDesign.setBidiFormat( bidiFormat );
			// Set the focus to allow an F1 press
			// to present the tool bar help
			//-----------------------------------
			_itemBidiPreferences.getParent().setFocus();
		}
		// IBM BIDI Append End

		// Hide browser size controls
		//---------------------------
		else if( event.widget == _itemRefreshPalette )
			_pageDesign.refreshPalette();

		// Refresh browser
		// Tells the editor that the model has changed
		// This will generate a web page and
		// refresh the design and preview pages.
		//--------------------------------------------
		else if( event.widget == _itemRefreshWebPage ) {
			_pageDesign._bFullRefresh = true;
			_pageDesign.getEditor().modelChanged();

		}
		
		// Show preferences
		//-----------------
		else if( event.widget == _itemPreferences ) {
			EvActionPreferences action = new EvActionPreferences();
			action.run();

			// Set the focus to allow an F1 press
			// to present the tool bar help
			//-----------------------------------
			_itemPreferences.getParent().setFocus();
		}

		// Hide browser size controls
		//---------------------------
		else if( event.widget == _itemShowBrowserSizeControls )
			_pageDesign.updateBrowserSizeControlsVisible( _itemShowBrowserSizeControls.getSelection() );

		// Transparency controls
		//----------------------
		else if( event.widget == _itemShowTransparencyControls )
			_compositeTransparencyControls.setVisible( _itemShowTransparencyControls.getSelection() );

		else if (event.widget == _itemTransparencyDisabled ){
			if( _itemTransparencyDisabled.getSelection() == true )
				_pageDesign.setTransparency( EvConstants.SEMITRANSPARENCY_NONE, 0 );

			updateTransparencyControls();
			_itemTransparencyDisabled.getParent().setFocus();
		}
		
		else if (event.widget == _itemTransparencyFixed ){
			if( _itemTransparencyFixed.getSelection() == true )
				_pageDesign.setTransparency( EvConstants.SEMITRANSPARENCY_FIXED, 0 );
			
			updateTransparencyControls();
			_itemTransparencyFixed.getParent().setFocus();
		}

		else if (event.widget == _itemTransparencyVariable ){
			_pageDesign.setTransparency( EvConstants.SEMITRANSPARENCY_VARIABLE, _sliderTransparencyAmount.getSelection() );
			updateTransparencyControls();
			_itemTransparencyVariable.getParent().setFocus();
		}
		
		else if( _sliderTransparencyAmount != null && event.widget == _sliderTransparencyAmount ){
			_pageDesign.setTransparency( EvConstants.SEMITRANSPARENCY_VARIABLE, _sliderTransparencyAmount.getSelection() );
			updateTransparencyControls();
			_sliderTransparencyAmount.setFocus();
		}
		
		// TVT mode
		//---------
		else if( event.widget == _buttonTranslationTest ){
			TranslationTestMode testMode = new TranslationTestMode();
			testMode.execute( _pageDesign.getEditor() );
		}

		// Z-order controls
		//-----------------
		else if( event.widget == _buttonOverlayOnTop ) {
			_buttonOverlayOnTop.setEnabled( false );
			_buttonBrowserOnTop.setEnabled( true );
			_pageDesign.setDesignMode( true );
		}

		else if( event.widget == _buttonBrowserOnTop ) {
			_buttonOverlayOnTop.setEnabled( true );
			_buttonBrowserOnTop.setEnabled( false );
			_pageDesign.setDesignMode( false );
		}
	}
}
