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
package org.eclipse.edt.ide.rui.visualeditor.internal.preferences;

import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvConstants;
import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvHelp;
import org.eclipse.edt.ide.rui.visualeditor.internal.nl.Messages;
import org.eclipse.edt.ide.rui.visualeditor.internal.util.ColorUtil;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Slider;


/**
 * Preference page for the EGL RichUI Visual Editor
 */
public class EvPreferencePageBrowserSize extends Composite implements DisposeListener, PaintListener, SelectionListener, IEvPreferencePage {

	protected static final int	HORZ_MAXIMUM_WIDTH		= 3000;
	protected static final int	HORZ_MINIMUM_WIDTH		= 200;
	protected static final int	VERT_MAXIMUM_HEIGHT		= 5000;
	protected static final int	VERT_MINIMUM_HEIGHT		= 200;

	protected Button			_checkShowControls		= null;
	protected Color				_colorDefault			= null;
	protected Composite			_compositeVisualSize	= null; // Remembered for redrawing
	protected Label				_labelXDef				= null; // Labels that display the slider values
	protected Label				_labelXMax				= null;
	protected Label				_labelXMin				= null;
	protected Label				_labelYDef				= null;
	protected Label				_labelYMax				= null;
	protected Label				_labelYMin				= null;
	protected Slider			_sliderXDef				= null; // Sliders for controlling browser sizes
	protected Slider			_sliderXMax				= null;
	protected Slider			_sliderXMin				= null;
	protected Slider			_sliderYDef				= null;
	protected Slider			_sliderYMax				= null;
	protected Slider			_sliderYMin				= null;

	/**
	 *
	 */
	public EvPreferencePageBrowserSize( Composite compositeParent, int iStyle ) {
		super( compositeParent, iStyle );
		
		_colorDefault = ColorUtil.getColorFromRGBString( Display.getCurrent(), "200 200 255" );
		
		createControls();

		Dialog.applyDialogFont( compositeParent );
		EvHelp.setHelp( this, EvHelp.PREFERENCES_BROWSER_SIZE );
		
		addDisposeListener( this ); // Dispose the color
	}

	/**
	 * Creates the user interface for the browser size tab page
	 */
	protected void createControls() {

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		setLayout( gridLayout );

		// Instructions
		//-------------
		Label label = new Label( this, SWT.NULL );
		GridData gridData = new GridData(  );
		gridData.horizontalSpan = 3;
		label.setLayoutData( gridData );
		label.setText( Messages.NL_Configure_the_design_area_browser_size_controls );
		
		// Separator
		//----------
		label = new Label( this, SWT.NULL );
		gridData = new GridData( GridData.HORIZONTAL_ALIGN_FILL );
		gridData.horizontalSpan = 3;
		label.setLayoutData( gridData );

		// Hide browser size controls
		//---------------------------
		_checkShowControls = new Button( this, SWT.CHECK );
		gridData = new GridData( GridData.HORIZONTAL_ALIGN_FILL );
		gridData.horizontalSpan = 3;
		_checkShowControls.setLayoutData( gridData );
		_checkShowControls.setText( Messages.NL_Show_the_browser_size_controls_when_the_editor_is_opened );
		boolean bHide = EvPreferences.getBoolean( EvConstants.PREFERENCE_BROWSER_SIZE_CONTROLS_VISIBLE );
		_checkShowControls.setSelection( bHide );
		_checkShowControls.addSelectionListener( this );

		// Composite for vertical and separator
		//-------------------------------------
		Composite composite = new Composite( this, SWT.NULL );
		gridData = new GridData( GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL );
		gridData.horizontalSpan = 2;
		composite.setLayoutData( gridData );
		
		gridLayout = new GridLayout();
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		composite.setLayout( gridLayout );

		// Vertical
		//---------
		label = new Label( composite, SWT.NULL );
		gridData = new GridData( GridData.FILL_VERTICAL | GridData.FILL_HORIZONTAL );
		gridData.horizontalAlignment = SWT.CENTER;
		gridData.verticalAlignment = SWT.BOTTOM;
		label.setLayoutData( gridData );
		label.setText( Messages.NL_Vertical );

		// Separator
		//----------
		label = new Label( composite, SWT.SEPARATOR | SWT.HORIZONTAL );
		gridData = new GridData( GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_END );
		label.setLayoutData( gridData );

		Composite compositeHorizontal = new Composite( this, SWT.NULL );
		gridData = new GridData();
		gridData.verticalAlignment = SWT.BOTTOM;
		compositeHorizontal.setLayoutData( gridData );
		
		gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		compositeHorizontal.setLayout( gridLayout );
		
		// Horizontal
		//-----------
		label = new Label( compositeHorizontal, SWT.NULL );
		label.setText( Messages.NL_Horizontal );
		gridData = new GridData();
		gridData.horizontalSpan = 3;
		gridData.horizontalAlignment = SWT.CENTER;
		label.setLayoutData( gridData );

		// Separator
		//----------
		label = new Label( compositeHorizontal, SWT.SEPARATOR | SWT.HORIZONTAL );
		gridData = new GridData( GridData.HORIZONTAL_ALIGN_FILL );
		gridData.horizontalSpan = 3;
		label.setLayoutData( gridData );

		// Minimum
		//--------
		label = new Label( compositeHorizontal, SWT.CENTER );
		label.setLayoutData( new GridData( GridData.HORIZONTAL_ALIGN_CENTER ) );
		label.setText( Messages.NL_Minimum );

		// Default
		//--------
		label = new Label( compositeHorizontal, SWT.CENTER );
		label.setLayoutData( new GridData( GridData.HORIZONTAL_ALIGN_CENTER ) );
		label.setText( Messages.NL_Default );

		// Maximum
		//--------
		label = new Label( compositeHorizontal, SWT.CENTER );
		label.setLayoutData( new GridData( GridData.HORIZONTAL_ALIGN_CENTER ) );
		label.setText( Messages.NL_Maximum );

		_labelXMin = new Label( compositeHorizontal, SWT.CENTER );
		gridData = new GridData( );
		gridData.horizontalAlignment = SWT.CENTER;
		_labelXMin.setLayoutData( gridData );
		_labelXMin.setText( "0000" ); //$NON-NLS-1$

		_labelXDef = new Label( compositeHorizontal, SWT.CENTER );
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.CENTER;
		_labelXDef.setLayoutData( gridData );
		_labelXDef.setText( "0000" ); //$NON-NLS-1$

		_labelXMax = new Label( compositeHorizontal, SWT.CENTER );
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.CENTER;
		_labelXMax.setLayoutData( gridData );
		_labelXMax.setText( "0000" ); //$NON-NLS-1$

		_sliderXMin = new Slider( compositeHorizontal, SWT.HORIZONTAL );
		gridData = new GridData();
		gridData.widthHint = 100;
		_sliderXMin.setLayoutData( gridData );

		_sliderXDef = new Slider( compositeHorizontal, SWT.HORIZONTAL );
		gridData = new GridData();
		gridData.widthHint = 100;
		_sliderXDef.setLayoutData( gridData );

		_sliderXMax = new Slider( compositeHorizontal, SWT.HORIZONTAL );
		gridData = new GridData();
		gridData.widthHint = 100;
		_sliderXMax.setLayoutData( gridData );

		composite = new Composite( this, SWT.NULL );
		gridData = new GridData(  );
		composite.setLayoutData( gridData );
		gridLayout = new GridLayout();
		composite.setLayout( gridLayout );

		// Minimum
		//--------
		label = new Label( composite, SWT.NULL );
		gridData = new GridData(  );
		gridData.horizontalAlignment = SWT.CENTER;
		label.setLayoutData( gridData );
		label.setText( Messages.NL_Minimum );

		_labelYMin = new Label( composite, SWT.CENTER );
		gridData = new GridData(  );
		gridData.horizontalAlignment = SWT.CENTER;
		_labelYMin.setLayoutData( gridData );
		_labelYMin.setText( "0000" ); //$NON-NLS-1$

		_sliderYMin = new Slider( this, SWT.VERTICAL );
		gridData = new GridData();
		gridData.heightHint = 100;
		_sliderYMin.setLayoutData( gridData );

		// Graphic
		//--------
		_compositeVisualSize = new Composite( this, SWT.BORDER | SWT.NO_BACKGROUND );
		gridData = new GridData( GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL );
		gridData.verticalSpan = 3;
		_compositeVisualSize.setLayoutData( gridData );

		composite = new Composite( this, SWT.NULL );
		gridData = new GridData(  );
		composite.setLayoutData( gridData );
		gridLayout = new GridLayout();
		composite.setLayout( gridLayout );

		// Default
		//--------
		label = new Label( composite, SWT.NULL );
		gridData = new GridData(  );
		gridData.horizontalAlignment = SWT.CENTER;
		label.setLayoutData( gridData );
		label.setText( Messages.NL_Default );

		_labelYDef = new Label( composite, SWT.NULL );
		gridData = new GridData(  );
		gridData.horizontalAlignment = SWT.CENTER;
		_labelYDef.setLayoutData( gridData );
		_labelYDef.setText( "0000" ); //$NON-NLS-1$

		_sliderYDef = new Slider( this, SWT.VERTICAL );
		gridData = new GridData();
		gridData.heightHint = 100;
		_sliderYDef.setLayoutData( gridData );

		composite = new Composite( this, SWT.NULL );
		gridData = new GridData(  );
		composite.setLayoutData( gridData );
		gridLayout = new GridLayout();
		composite.setLayout( gridLayout );

		// Maximum
		//--------
		label = new Label( composite, SWT.NULL );
		gridData = new GridData(  );
		gridData.horizontalAlignment = SWT.CENTER;
		label.setLayoutData( gridData );
		label.setText( Messages.NL_Maximum );

		_labelYMax = new Label( composite, SWT.CENTER );
		gridData = new GridData( );
		gridData.horizontalAlignment = SWT.CENTER;
		_labelYMax.setLayoutData( gridData );
		_labelYMax.setText( "0000" ); //$NON-NLS-1$

		_sliderYMax = new Slider( this, SWT.VERTICAL );
		gridData = new GridData();
		gridData.heightHint = 100;
		_sliderYMax.setLayoutData( gridData );

		// Obtain the current preference values
		//-------------------------------------
		int iXMin = EvPreferences.getInt( EvConstants.PREFERENCE_BROWSER_SIZE_MINIMUM_WIDTH );
		int iXMax = EvPreferences.getInt( EvConstants.PREFERENCE_BROWSER_SIZE_MAXIMUM_WIDTH );
		int iXDef = EvPreferences.getInt( EvConstants.PREFERENCE_BROWSER_SIZE_DEFAULT_WIDTH );

		int iYMin = EvPreferences.getInt( EvConstants.PREFERENCE_BROWSER_SIZE_MINIMUM_HEIGHT );
		int iYMax = EvPreferences.getInt( EvConstants.PREFERENCE_BROWSER_SIZE_MAXIMUM_HEIGHT );
		int iYDef = EvPreferences.getInt( EvConstants.PREFERENCE_BROWSER_SIZE_DEFAULT_HEIGHT );

		updateSliders( iXMin, iXDef, iXMax, iYMin, iYDef, iYMax );
		updateLabels();

		_sliderXMin.addSelectionListener( this );
		_sliderXDef.addSelectionListener( this );
		_sliderXMax.addSelectionListener( this );

		_sliderYMin.addSelectionListener( this );
		_sliderYDef.addSelectionListener( this );
		_sliderYMax.addSelectionListener( this );

		_compositeVisualSize.addPaintListener( this );
	}


	/**
	 * Returns the help ID for this page.
	 */
	public String getHelpID(){
		return EvHelp.PREFERENCES_BROWSER_SIZE;
	}

	/**
	 * 
	 */
	public void paintControl( PaintEvent e ) {
		final int PADDING = 8;

		Image image = new Image( e.widget.getDisplay(), ( (Composite)e.widget ).getBounds() );
		GC gc = new GC( image );

		Rectangle rect = ( (Composite)e.widget ).getBounds();
		int iX = PADDING;
		int iY = PADDING;
		int iW = rect.width - 2 * PADDING;
		int iH = rect.height - 2 * PADDING;

		gc.setBackground( e.widget.getDisplay().getSystemColor( SWT.COLOR_WHITE ) );
		gc.fillRectangle( 0, 0, rect.width, rect.height );

		// Default
		//--------
		int max = _sliderXMax.getSelection();
		int defaultXCurrent = _sliderXDef.getSelection();
		int defaultX = defaultXCurrent * iW / max;

		max = _sliderYMax.getSelection();
		int defaultYCurrent = _sliderYDef.getSelection();
		int defaultY = defaultYCurrent * iH / max;

		gc.setBackground( _colorDefault );
		gc.fillRectangle( iX + 1, iY + 1, defaultX - 1, defaultY - 1 );

		gc.setForeground( e.widget.getDisplay().getSystemColor( SWT.COLOR_BLACK ) );
		gc.drawLine( iX + defaultX, iY, iX + defaultX, iY + defaultY );
		gc.drawLine( iX, iY + defaultY, iX + defaultX, iY + defaultY );

		// Minimum
		//--------
		max = _sliderXMax.getSelection();
		int minXCurrent = _sliderXMin.getSelection();
		int minX = minXCurrent * iW / max;

		max = _sliderYMax.getSelection();
		int minYCurrent = _sliderYMin.getSelection();
		int minY = minYCurrent * iH / max;

		gc.setForeground( e.widget.getDisplay().getSystemColor( SWT.COLOR_BLACK ) );
		gc.setLineStyle( SWT.LINE_DASH );
		gc.drawLine( iX + minX, iY, iX + minX, iY + minY );
		gc.drawLine( iX, iY + minY, iX + minX, iY + minY );

		// Arrow lines, horizontal
		//------------------------
		gc.setForeground( e.widget.getDisplay().getSystemColor( SWT.COLOR_BLACK ) );
		gc.setLineStyle( SWT.LINE_SOLID );

		gc.drawLine( iX, iY, iX + iW, iY );
		gc.drawLine( iX + iW - 4, iY - 2, iX + iW, iY );
		gc.drawLine( iX + iW - 4, iY - 1, iX + iW, iY );
		gc.drawLine( iX + iW - 4, iY + 1, iX + iW, iY );
		gc.drawLine( iX + iW - 4, iY + 2, iX + iW, iY );

		// Arrow lines, vertical
		//----------------------
		gc.drawLine( iX, iY, iX, iY + iH );
		gc.drawLine( iX - 2, iY + iH - 4, iX, iY + iH );
		gc.drawLine( iX - 1, iY + iH - 4, iX, iY + iH );
		gc.drawLine( iX + 1, iY + iH - 4, iX, iY + iH );
		gc.drawLine( iX + 2, iY + iH - 4, iX, iY + iH );

		e.gc.drawImage( image, 0, 0 );
		
		image.dispose();
		gc.dispose();
	}

	/**
	 * Declared in PreferencePage and overridden here.  Called when a person presses the Restore Defaults button.
	 */
	public void performDefaults() {
		// Show browser size controls
		//---------------------------
		_checkShowControls.setSelection( EvConstants.PREFERENCE_DEFAULT_BROWSER_SIZE_CONTROLS_VISIBLE );

		// Obtain the default sizes that are never changed
		//------------------------------------------------
		int iXMin = EvConstants.PREFERENCE_DEFAULT_BROWSER_SIZE_MINIMUM_WIDTH;
		int iXMax = EvConstants.PREFERENCE_DEFAULT_BROWSER_SIZE_MAXIMUM_WIDTH;
		int iXDef = EvConstants.PREFERENCE_DEFAULT_BROWSER_SIZE_DEFAULT_WIDTH;

		int iYMin = EvConstants.PREFERENCE_DEFAULT_BROWSER_SIZE_MINIMUM_HEIGHT;
		int iYMax = EvConstants.PREFERENCE_DEFAULT_BROWSER_SIZE_MAXIMUM_HEIGHT;
		int iYDef = EvConstants.PREFERENCE_DEFAULT_BROWSER_SIZE_DEFAULT_HEIGHT;
		
		// Reset the sliders and labels
		//-----------------------------
		updateSliders( iXMin, iXDef, iXMax, iYMin, iYDef, iYMax );
		updateLabels();

		_compositeVisualSize.redraw();
	}

	/**
	 * Called when either the Apply or Ok buttons are pressed.
	 */
	public boolean performOk() {

		// Always do maximum before minimum.  Sliders will not set the minimum unless
		// the value is less than the maximum value
		//---------------------------------------------------------------------------
		EvPreferences.setInt( EvConstants.PREFERENCE_BROWSER_SIZE_MAXIMUM_WIDTH, _sliderXMax.getSelection() );
		EvPreferences.setInt( EvConstants.PREFERENCE_BROWSER_SIZE_MINIMUM_WIDTH, _sliderXMin.getSelection() );
		EvPreferences.setInt( EvConstants.PREFERENCE_BROWSER_SIZE_DEFAULT_WIDTH, _sliderXDef.getSelection() );

		EvPreferences.setInt( EvConstants.PREFERENCE_BROWSER_SIZE_MAXIMUM_HEIGHT, _sliderYMax.getSelection() );
		EvPreferences.setInt( EvConstants.PREFERENCE_BROWSER_SIZE_MINIMUM_HEIGHT, _sliderYMin.getSelection() );
		EvPreferences.setInt( EvConstants.PREFERENCE_BROWSER_SIZE_DEFAULT_HEIGHT, _sliderYDef.getSelection() );

		return true;
	}

	/**
	 * Updates the label with the value of the slider.
	 * The label is always given a number with 4 digits.
	 * Otherwise, the label doesn't take up enough room if given a three digit number first.
	 * None of the slider values can be less than 200.
	 */
	protected void updateLabel( Slider slider, Label label ) {
		String strValue = Integer.toString( slider.getSelection() );

		while( strValue.length() < 4 )
			strValue = "0" + strValue; //$NON-NLS-1$
		
		label.setText( strValue );
	}

	/**
	 * Updates the six labels with the values of the six sliders.
	 */
	protected void updateLabels() {
		updateLabel( _sliderXMin, _labelXMin );
		updateLabel( _sliderXMax, _labelXMax );
		updateLabel( _sliderXDef, _labelXDef );

		updateLabel( _sliderYMin, _labelYMin );
		updateLabel( _sliderYMax, _labelYMax );
		updateLabel( _sliderYDef, _labelYDef );
	}

	/**
	 * Updates the specified slider with the minimum, maximum and default values. 
	 */
	protected void updateSlider( Slider slider, int iMin, int iMax, int iDef ) {
		slider.removeSelectionListener( this );
		slider.setValues( iDef, iMin, iMax + 1, 1, 1, 10 );
		slider.addSelectionListener( this );
	}

	/**
	 * Sets the maximum for a slider.  Do not use the setMaximum method.
	 */
	protected void updateSliderMaximum( Slider slider, int iMax ){
		slider.removeSelectionListener( this );
		slider.setValues( slider.getSelection(), slider.getMinimum(), iMax + 1, 1, 1, 10 );
		slider.addSelectionListener( this );
	}

	/**
	 * Sets the minimum for a slider.  Do not use the setMinimum method.
	 */
	protected void updateSliderMinimum( Slider slider, int iMin ){
		slider.removeSelectionListener( this );
		slider.setValues( slider.getSelection(), iMin, slider.getMaximum(), 1, 1, 10 );
		slider.addSelectionListener( this );
	}

	/**
	 * Resets the six sliders to the given values during initialization, and during restore to defaults.
	 */
	protected void updateSliders( int iXMin, int iXDef, int iXMax, int iYMin, int iYDef, int iYMax ) {
		updateSlider( _sliderXMin, HORZ_MINIMUM_WIDTH, iXMax - 200, iXMin );
		updateSlider( _sliderXMax, iXMin + 200, HORZ_MAXIMUM_WIDTH, iXMax );
		updateSlider( _sliderXDef, _sliderXMin.getSelection(), _sliderXMax.getSelection(), iXDef );

		updateSlider( _sliderYMin, VERT_MINIMUM_HEIGHT, iYMax - 200, iYMin );
		updateSlider( _sliderYMax, iYMin + 200, VERT_MAXIMUM_HEIGHT, iYMax );
		updateSlider( _sliderYDef, _sliderYMin.getSelection(), _sliderYMax.getSelection(), iYDef );
	}

	/**
	 * Passes the call to widgetSelected
	 */
	public void widgetDefaultSelected( SelectionEvent event ) {
		widgetSelected( event );
	}

	/**
	 * Declared in DisposeListener.
	 * Called when this composite is disposed.  All colors are disposed.
	 */
	public void widgetDisposed( DisposeEvent e ) {
		if( _colorDefault != null && _colorDefault.isDisposed() == false ) {
			_colorDefault.dispose();
			_colorDefault = null;
		}
	}

	/**
	 * Called when one of the sliders changes.
	 */
	public void widgetSelected( SelectionEvent event ) {
		if( event.widget == _checkShowControls ){
			EvPreferences.setBoolean( EvConstants.PREFERENCE_BROWSER_SIZE_CONTROLS_VISIBLE, _checkShowControls.getSelection() );
		}
		
		// Minimum
		//--------
		else if( event.widget == _sliderXMin ) {
			int minSelection = _sliderXMin.getSelection();
			updateSliderMinimum( _sliderXDef, minSelection );
			updateSliderMinimum( _sliderXMax, minSelection + 200 );
		}

		else if( event.widget == _sliderYMin ) {
			int minSelection = _sliderYMin.getSelection();
			updateSliderMinimum( _sliderYDef, minSelection );
			updateSliderMinimum( _sliderYMax, minSelection + 200 );
		}

		// Maximum
		//--------
		else if( event.widget == _sliderXMax ) {
			int maxSelection = _sliderXMax.getSelection();
			updateSliderMaximum( _sliderXDef, maxSelection );
			updateSliderMaximum( _sliderXMin, maxSelection - 200 );
		}

		else if( event.widget == _sliderYMax ) {
			int maxSelection = _sliderYMax.getSelection();
			updateSliderMaximum( _sliderYDef, maxSelection );
			updateSliderMaximum( _sliderYMin, maxSelection - 200 );
		}

		updateLabels();

		_compositeVisualSize.redraw();
	}
}
