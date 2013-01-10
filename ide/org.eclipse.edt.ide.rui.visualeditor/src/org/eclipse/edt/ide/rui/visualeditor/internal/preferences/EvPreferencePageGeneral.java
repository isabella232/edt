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

import java.text.DecimalFormat;

import org.eclipse.core.runtime.Platform;
import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvConstants;
import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvHelp;
import org.eclipse.edt.ide.rui.visualeditor.internal.nl.Messages;
import org.eclipse.edt.ide.rui.visualeditor.internal.util.BrowserManager;
import org.eclipse.edt.ide.rui.visualeditor.internal.util.ColorSelectorButton;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Slider;


/**
 * Preference page for the EGL RichUI Visual Editor
 */
public class EvPreferencePageGeneral extends Composite implements SelectionListener, IEvPreferencePage {

	protected ColorSelectorButton	_buttonColorDropLocationPotential	= null;
	protected ColorSelectorButton	_buttonColorDropLocationSelected	= null;
	protected ColorSelectorButton	_buttonColorSelection				= null;
	protected Button				_checkPromptForNewWidgetName		= null;
	protected Button				_checkShowTransparencyControls		= null;
	protected Button				_checkSemiTransparencyWhileDragging	= null;
	protected Button				_checkPatternDropLocationSelected	= null;
	protected Button				_checkPatternSelection				= null;
	protected DecimalFormat			_decimalFormat						= new DecimalFormat( "####%" ); //$NON-NLS-1$
	protected Label					_labelTransparencyPercent			= null;
	protected Button				_radioEditorTabDesign				= null;
	protected Button				_radioEditorTabPreview				= null;
	protected Button				_radioEditorTabSource				= null;
	protected Button				_radioOptimizeResources				= null;
	protected Button				_radioOptimizeSpeed					= null;
	protected Button				_radioRenderEngineWebKit			= null;
	protected Button				_radioRenderEngineXULRunner			= null;
	protected Button				_radioRenderEngineIE				= null;
	protected Button				_radioRenderEngineUserConfigured	= null;
	protected Button				_radioTransparencyNone				= null;
	protected Button				_radioTransparencyFixed				= null;
	protected Button				_radioTransparencyVariable			= null;
	protected Slider				_sliderTransparencyAmount			= null;

	/**
	 *
	 */
	public EvPreferencePageGeneral( Composite compositeParent, int iStyle ) {
		super( compositeParent, iStyle );

		createControlsEditorOpeningTab();
		createControlsWidgetCreation();
		createControlsTransparency();
		createControlsColors();
		createControlsPerformance();
		createControlsRenderEngine();
		
		Dialog.applyDialogFont( compositeParent );
		EvHelp.setHelp( this, EvHelp.PREFERENCES_GENERAL );
	}

	/**
	 * Cleans up resources used in this preference page. 
	 */
	protected void cleanup() {
	}

	
	/**
	 * Creates the color controls for this tab page
	 */
	protected void createControlsColors() {

		GridLayout gridLayout = new GridLayout();
		setLayout( gridLayout );

		Group groupColors = new Group( this, SWT.NULL );
		GridData gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		groupColors.setLayoutData( gridData );
		gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		groupColors.setLayout( gridLayout );
		groupColors.setText( Messages.NL_Colors );

		// Selection border
		//-----------------
		Label label = new Label( groupColors, SWT.NULL );
		label.setText( Messages.NL_Selection_borderXcolonX );
		gridData = new GridData();
		label.setLayoutData( gridData );

		_buttonColorSelection = new ColorSelectorButton();
		Button button = _buttonColorSelection.createWidget( groupColors );
		gridData = new GridData();
		button.setLayoutData( gridData );
		_buttonColorSelection.setColor( EvPreferences.getString( EvConstants.PREFERENCE_COLOR_SELECTION ) );

		_checkPatternSelection = new Button( groupColors, SWT.CHECK );
		gridData = new GridData();
		gridData.horizontalIndent = 16;
		_checkPatternSelection.setLayoutData( gridData );
		_checkPatternSelection.setText( Messages.NL_Superimpose_a_pattern );
		_checkPatternSelection.addSelectionListener( this );
		
		boolean bPattern = EvPreferences.getBoolean( EvConstants.PREFERENCE_PATTERN_SELECTION );
		_checkPatternSelection.setSelection( bPattern );
		_buttonColorSelection.setPattern( bPattern == true ? ColorSelectorButton.PATTERN_DENTAL : ColorSelectorButton.PATTERN_NONE );

		// Potential drop Location
		//----------------------
		label = new Label( groupColors, SWT.NULL );
		label.setText( Messages.NL_Potential_drop_locationXcolonX );
		gridData = new GridData();
		label.setLayoutData( gridData );

		_buttonColorDropLocationPotential = new ColorSelectorButton();
		button = _buttonColorDropLocationPotential.createWidget( groupColors );
		gridData = new GridData();
		button.setLayoutData( gridData );
		_buttonColorDropLocationPotential.setColor( EvPreferences.getString( EvConstants.PREFERENCE_COLOR_DROP_LOCATION_POTENTIAL ) );

		new Label( groupColors, SWT.NULL );
		gridData = new GridData();
		label.setLayoutData( gridData );
		
		// Selected drop target
		//---------------------
		label = new Label( groupColors, SWT.NULL );
		label.setText( Messages.NL_Selected_drop_locationXcolonX );
		gridData = new GridData();
		label.setLayoutData( gridData );

		_buttonColorDropLocationSelected = new ColorSelectorButton();
		button = _buttonColorDropLocationSelected.createWidget( groupColors );
		gridData = new GridData();
		button.setLayoutData( gridData );
		_buttonColorDropLocationSelected.setColor( EvPreferences.getString( EvConstants.PREFERENCE_COLOR_DROP_LOCATION_SELECTED ) );
		
		_checkPatternDropLocationSelected = new Button( groupColors, SWT.CHECK );
		gridData = new GridData();
		gridData.horizontalIndent = 16;
		_checkPatternDropLocationSelected.setLayoutData( gridData );
		_checkPatternDropLocationSelected.setText( Messages.NL_Superimpose_a_pattern );
		_checkPatternDropLocationSelected.addSelectionListener( this );
		
		bPattern = EvPreferences.getBoolean( EvConstants.PREFERENCE_PATTERN_DROP_LOCATION_SELECTED );
		_checkPatternDropLocationSelected.setSelection( bPattern );
		_buttonColorDropLocationSelected.setPattern( bPattern == true ? ColorSelectorButton.PATTERN_HATCH : ColorSelectorButton.PATTERN_NONE );
	}

	/**
	 * Creates the controls for which tab is to be active when the editor is opened.
	 */
	protected void createControlsEditorOpeningTab(){
		GridLayout gridLayout = new GridLayout();
		setLayout( gridLayout );

		Group groupEditorTab = new Group( this, SWT.NULL );
		GridData gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		groupEditorTab.setLayoutData( gridData );
		gridLayout = new GridLayout();
		groupEditorTab.setLayout( gridLayout );
		groupEditorTab.setText( Messages.NL_Editor_tab );

		// Instructions
		//-------------
		Label label = new Label( groupEditorTab, SWT.NULL );
		gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		label.setLayoutData( gridData );
		label.setText( Messages.NL_Select_which_tab_is_shown_when_the_editor_is_opened );

		_radioEditorTabDesign = new Button( groupEditorTab, SWT.RADIO );
		gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		gridData.horizontalIndent = 16;
		_radioEditorTabDesign.setLayoutData( gridData );
		_radioEditorTabDesign.setText( Messages.NL_Design );

		_radioEditorTabSource = new Button( groupEditorTab, SWT.RADIO );
		gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		gridData.horizontalIndent = 16;
		_radioEditorTabSource.setLayoutData( gridData );
		_radioEditorTabSource.setText( Messages.NL_Source );

		_radioEditorTabPreview = new Button( groupEditorTab, SWT.RADIO );
		gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		gridData.horizontalIndent = 16;
		_radioEditorTabPreview.setLayoutData( gridData );
		_radioEditorTabPreview.setText( Messages.NL_Preview );

		// Reflect the current preference
		//-------------------------------
		int iTabIndex = EvPreferences.getInt( EvConstants.PREFERENCE_EDITOR_OPENING_TAB );

		switch( iTabIndex ){
			case 0:
				_radioEditorTabDesign.setSelection( true );
				break;

			case 1:
				_radioEditorTabSource.setSelection( true );
				break;

			case 2:
				_radioEditorTabPreview.setSelection( true );
				break;

			default:
				_radioEditorTabDesign.setSelection( true );
		}
	}
	
	/**
	 * Creates the controls for less resources versus greater speed
	 */
	protected void createControlsPerformance(){
		GridLayout gridLayout = new GridLayout();
		setLayout( gridLayout );

		Group groupEditorTab = new Group( this, SWT.NULL );
		GridData gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		groupEditorTab.setLayoutData( gridData );
		gridLayout = new GridLayout();
		groupEditorTab.setLayout( gridLayout );
		groupEditorTab.setText( Messages.NL_Performance );

		// Instructions
		//-------------
		Label label = new Label( groupEditorTab, SWT.NULL );
		gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		label.setLayoutData( gridData );
		label.setText( Messages.NL_This_setting_is_applied_when_the_editor_is_opened );

		_radioOptimizeSpeed = new Button( groupEditorTab, SWT.RADIO );
		gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		gridData.horizontalIndent = 16;
		_radioOptimizeSpeed.setLayoutData( gridData );
		_radioOptimizeSpeed.setText( Messages.NL_Optimize_for_better_responsiveness );

		_radioOptimizeResources = new Button( groupEditorTab, SWT.RADIO );
		gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		gridData.horizontalIndent = 16;
		_radioOptimizeResources.setLayoutData( gridData );
		_radioOptimizeResources.setText( Messages.NL_Optimize_to_use_fewer_resources );

		// Reflect the current preference
		//-------------------------------
		int iTabIndex = EvPreferences.getInt( EvConstants.PREFERENCE_PERFORMANCE );

		switch( iTabIndex ){
			case EvConstants.PREFERENCE_PERFORMANCE_OPTIMIZE_SPEED:
				_radioOptimizeSpeed.setSelection( true );
				break;

			case EvConstants.PREFERENCE_PERFORMANCE_OPTIMIZE_RESOURCES:
				_radioOptimizeResources.setSelection( true );
				break;

			default:
				_radioOptimizeSpeed.setSelection( true );
		}
	}
	
	/**
	 * Creates the controls for VE render engine
	 */
	protected void createControlsRenderEngine(){
		GridLayout gridLayout = new GridLayout();
		setLayout( gridLayout );

		Group groupRenderEngineTab = new Group( this, SWT.NULL );
		GridData gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		groupRenderEngineTab.setLayoutData( gridData );
		gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		groupRenderEngineTab.setLayout( gridLayout );
		groupRenderEngineTab.setText( Messages.NL_VisualEditor_RenderEngion );
		
		// Do not display xulrunner or webkit for win64 - only IE will work.
		if (!Platform.getOSArch().equals(Platform.ARCH_X86_64) || !Platform.getOS().equals(Platform.OS_WIN32)) {
			// Webkit is only for Eclipse 3.7 or later
			if (!BrowserManager.getInstance().ECLIPSE_36) {
				_radioRenderEngineWebKit = new Button( groupRenderEngineTab, SWT.RADIO );
				gridData = new GridData();
				gridData.horizontalIndent = 16;
				_radioRenderEngineWebKit.setLayoutData( gridData );
				_radioRenderEngineWebKit.setText( Messages.NL_VisualEditor_RenderEngion_WEBKIT );
			}
			
			_radioRenderEngineXULRunner = new Button( groupRenderEngineTab, SWT.RADIO );
			gridData = new GridData();
			gridData.horizontalIndent = 16;
			_radioRenderEngineXULRunner.setLayoutData( gridData );
			_radioRenderEngineXULRunner.setText( Messages.NL_VisualEditor_RenderEngion_XULRUNNER );
		}

		if (Platform.getOS().equals(Platform.OS_WIN32)) {
			_radioRenderEngineIE = new Button( groupRenderEngineTab, SWT.RADIO );
			gridData = new GridData();
			gridData.horizontalIndent = 16;
			_radioRenderEngineIE.setLayoutData( gridData );
			_radioRenderEngineIE.setText( Messages.NL_VisualEditor_RenderEngion_IE );
		}
		
		if (Platform.OS_LINUX.equals(Platform.getOS())) {
			_radioRenderEngineUserConfigured = new Button( groupRenderEngineTab, SWT.RADIO );
			gridData = new GridData();
			gridData.horizontalIndent = 16;
			_radioRenderEngineUserConfigured.setLayoutData( gridData );
			_radioRenderEngineUserConfigured.setText( Messages.NL_VisualEditor_RenderEngion_USER_CONFIGURED );
		}

		// Reflect the current preference
		//-------------------------------
		int iTabIndex = EvPreferences.getInt( EvConstants.PREFERENCE_RENDERENGINE );

		switch( iTabIndex ){
			case EvConstants.PREFERENCE_RENDERENGINE_USER_CONFIGURED:
				if (_radioRenderEngineUserConfigured != null) {
					_radioRenderEngineUserConfigured.setSelection( true );
				}
				break;

			case EvConstants.PREFERENCE_RENDERENGINE_WEBKIT:
				if ( _radioRenderEngineWebKit != null ) { 
					_radioRenderEngineWebKit.setSelection( true );
				}
				break;

			case EvConstants.PREFERENCE_RENDERENGINE_XULRUNNER:
				if (_radioRenderEngineXULRunner != null) {
					_radioRenderEngineXULRunner.setSelection( true );
				}
				break;

			case EvConstants.PREFERENCE_RENDERENGINE_IE:
				if ( _radioRenderEngineIE != null ) {
					_radioRenderEngineIE.setSelection( true );
				}
				break;

			default:
				if (_radioRenderEngineUserConfigured != null) {
					_radioRenderEngineUserConfigured.setSelection( true );
				}
		}
	}
	
	/**
	 * Creates the transparency controls for this tab page
	 */
	protected void createControlsTransparency() {

		GridLayout gridLayout = new GridLayout();
		setLayout( gridLayout );

		Group groupTransparency = new Group( this, SWT.NULL );
		GridData gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		groupTransparency.setLayoutData( gridData );
		gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		groupTransparency.setLayout( gridLayout );
		groupTransparency.setText( Messages.NL_Tranparency );

		// Instructions
		//-------------
		Label label = new Label( groupTransparency, SWT.NULL );
		gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		gridData.horizontalSpan = 2;
		label.setLayoutData( gridData );
		label.setText( Messages.NL_These_settings_are_applied_when_the_editor_is_opened );

		_checkShowTransparencyControls = new Button( groupTransparency, SWT.CHECK );
		gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		gridData.horizontalSpan = 2;
		gridData.horizontalIndent = 16;
		_checkShowTransparencyControls.setLayoutData( gridData );
		_checkShowTransparencyControls.setText( Messages.NL_Show_transparency_controls );
		_checkShowTransparencyControls.setSelection( EvPreferences.getBoolean( EvConstants.PREFERENCE_TRANSPARENCY_CONTROLS_VISIBLE ) );
		_checkShowTransparencyControls.addSelectionListener( this );

		label = new Label( groupTransparency, SWT.SEPARATOR | SWT.HORIZONTAL );
		gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		gridData.horizontalSpan = 2;
		gridData.horizontalIndent = 16;
		label.setLayoutData( gridData );

		_radioTransparencyNone = new Button( groupTransparency, SWT.RADIO );
		gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		gridData.horizontalSpan = 2;
		gridData.horizontalIndent = 16;
		_radioTransparencyNone.setLayoutData( gridData );
		_radioTransparencyNone.setText( Messages.NL_Fully_transparent );

		_radioTransparencyFixed = new Button( groupTransparency, SWT.RADIO );
		gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		gridData.horizontalSpan = 2;
		gridData.horizontalIndent = 16;
		_radioTransparencyFixed.setLayoutData( gridData );
		_radioTransparencyFixed.setText( Messages.NL_Dotted_transparency_pattern );

		_radioTransparencyVariable = new Button( groupTransparency, SWT.RADIO );
		gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		gridData.horizontalSpan = 2;
		gridData.horizontalIndent = 16;
		_radioTransparencyVariable.setLayoutData( gridData );
		_radioTransparencyVariable.setText( Messages.NL_Variable_transparency );

		_sliderTransparencyAmount = new Slider( groupTransparency, SWT.None );
		gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		gridData.horizontalIndent = 32;
		_sliderTransparencyAmount.setLayoutData( gridData );

		_labelTransparencyPercent = new Label( groupTransparency, SWT.NULL );
		gridData = new GridData();
		_labelTransparencyPercent.setLayoutData( gridData );
		_labelTransparencyPercent.setText( "000%" ); //$NON-NLS-1$

		// Disable variable transparency for operating system that don't support it
		//-------------------------------------------------------------------------
		if( Platform.getOS().equals( Platform.OS_WIN32) == false ){
			_radioTransparencyVariable.setEnabled( false );
			_sliderTransparencyAmount.setEnabled( false );
			_labelTransparencyPercent.setEnabled( false );
		}

		// Reflect the current preference
		//-------------------------------
		int iTransparencyMode = EvPreferences.getInt( EvConstants.PREFERENCE_SEMITRANSPARENCY_MODE );

		switch( iTransparencyMode ){
			case EvConstants.SEMITRANSPARENCY_NONE:
				_radioTransparencyNone.setSelection( true );
				break;

			case EvConstants.SEMITRANSPARENCY_FIXED:
				_radioTransparencyFixed.setSelection( true );
				break;

			case EvConstants.SEMITRANSPARENCY_VARIABLE:
			default:
				_radioTransparencyVariable.setSelection( true );
				break;
		}

		int iTransparencyAmount = EvPreferences.getInt( EvConstants.PREFERENCE_SEMITRANSPARENCY_AMOUNT );
		_sliderTransparencyAmount.setValues( iTransparencyAmount, 0, 255 + 1, 1, 1, 10 );

		updateTransparencyControls();

		label = new Label( groupTransparency, SWT.SEPARATOR | SWT.HORIZONTAL );
		gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		gridData.horizontalSpan = 2;
		label.setLayoutData( gridData );

		_checkSemiTransparencyWhileDragging = new Button( groupTransparency, SWT.CHECK );
		gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		gridData.horizontalSpan = 2;
		_checkSemiTransparencyWhileDragging.setLayoutData( gridData );
		_checkSemiTransparencyWhileDragging.setText( Messages.NL_Enable_semiXhyphenXtransparency_while_dragging );
		_checkSemiTransparencyWhileDragging.setSelection( EvPreferences.getBoolean( EvConstants.PREFERENCE_SEMITRANSPARENCY_WHILE_DRAGGING ) );
		_checkSemiTransparencyWhileDragging.addSelectionListener( this );

		// Add listeners
		//--------------
		_radioTransparencyNone.addSelectionListener( this );
		_radioTransparencyFixed.addSelectionListener( this );
		_radioTransparencyVariable.addSelectionListener( this );
		_sliderTransparencyAmount.addSelectionListener( this );
	}

	/**
	 * Creates the project dependency controls for this tab page 
	 */
	protected void createControlsWidgetCreation(){
		Group groupWidgetCreation = new Group( this, SWT.NULL );
		GridData gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		groupWidgetCreation.setLayoutData( gridData );
		groupWidgetCreation.setLayout( new GridLayout() );
		groupWidgetCreation.setText( Messages.NL_Widget_creation );
		
		_checkPromptForNewWidgetName = new Button( groupWidgetCreation, SWT.CHECK );
		gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		_checkPromptForNewWidgetName.setLayoutData( gridData );
		_checkPromptForNewWidgetName.setText( Messages.NL_Prompt_for_a_variable_name );
		_checkPromptForNewWidgetName.setSelection( EvPreferences.getBoolean( EvConstants.PREFERENCE_PROMPT_FOR_A_NEW_WIDGET_NAME ) );
	}
	
	/**
	 * Returns the help ID for this page.
	 */
	public String getHelpID(){
		return EvHelp.PREFERENCES_GENERAL;
	}

	/**
	 * Declared in PreferencePage and overridden here.  Called when a person presses the Restore Defaults button.
	 */
	public void performDefaults() {
		// Editor opening tab
		//-------------------
		_radioEditorTabDesign.setSelection( false );
		_radioEditorTabSource.setSelection( false );
		_radioEditorTabPreview.setSelection( false );
		
		switch( EvConstants.PREFERENCE_DEFAULT_EDITOR_OPENING_TAB ){
			case 0:
				_radioEditorTabDesign.setSelection( true );
				break;

			case 1:
				_radioEditorTabSource.setSelection( true );
				break;

			case 2:
				_radioEditorTabPreview.setSelection( true );
				break;
				
			default:
				_radioEditorTabDesign.setSelection( true );
		}

		// Transparency
		//-------------
		_checkShowTransparencyControls.setSelection( false );
		
		_radioTransparencyNone.setSelection( false );
		_radioTransparencyFixed.setSelection( false );
		_radioTransparencyVariable.setSelection( false );

		switch( EvConstants.PREFERENCE_DEFAULT_SEMITRANSPARENCY_MODE ){
			case EvConstants.SEMITRANSPARENCY_NONE:
				_radioTransparencyNone.setSelection( true );
				break;

			case EvConstants.SEMITRANSPARENCY_FIXED:
				_radioTransparencyFixed.setSelection( true );
				break;

			case EvConstants.SEMITRANSPARENCY_VARIABLE:
			default:
				_radioTransparencyVariable.setSelection( true );
		}

		_sliderTransparencyAmount.setSelection( EvConstants.PREFERENCE_DEFAULT_SEMITRANSPARENCY_AMOUNT );

		_checkSemiTransparencyWhileDragging.setSelection( EvConstants.PREFERENCE_DEFAULT_SEMITRANSPARENCY_WHILE_DRAGGING );
		
		updateTransparencyControls();

		// Colors and pattern
		//-------------------
		_buttonColorSelection.setColor( EvConstants.PREFERENCE_DEFAULT_COLOR_SELECTION );
		_buttonColorDropLocationPotential.setColor( EvConstants.PREFERENCE_DEFAULT_COLOR_DROP_LOCATION_POTENTIAL );
		_buttonColorDropLocationSelected.setColor( EvConstants.PREFERENCE_DEFAULT_COLOR_DROP_LOCATION_SELECTED );
		
		boolean bPattern = EvConstants.PREFERENCE_DEFAULT_PATTERN_DROP_LOCATION_SELECTED;
		_buttonColorDropLocationSelected.setPattern( bPattern == true ? ColorSelectorButton.PATTERN_HATCH : ColorSelectorButton.PATTERN_NONE );
		_checkPatternDropLocationSelected.setSelection( bPattern );
		
		bPattern = EvConstants.PREFERENCE_DEFAULT_PATTERN_SELECTION;
		_buttonColorSelection.setPattern( bPattern == true ? ColorSelectorButton.PATTERN_DENTAL : ColorSelectorButton.PATTERN_NONE );
		_checkPatternSelection.setSelection( bPattern );

		// Performance
		//------------
		_radioOptimizeResources.setSelection( EvConstants.PREFERENCE_DEFAULT_PERFORMANCE == EvConstants.PREFERENCE_PERFORMANCE_OPTIMIZE_RESOURCES );
		_radioOptimizeSpeed.setSelection( EvConstants.PREFERENCE_DEFAULT_PERFORMANCE == EvConstants.PREFERENCE_PERFORMANCE_OPTIMIZE_SPEED );

		// Render Engine
		//------------
		int defaultBrowser = BrowserManager.getInstance().getDefaultRenderEngine();
		if (_radioRenderEngineUserConfigured != null) {
			_radioRenderEngineUserConfigured.setSelection( defaultBrowser == EvConstants.PREFERENCE_RENDERENGINE_USER_CONFIGURED );
		}
		if ( _radioRenderEngineWebKit != null ) {
			_radioRenderEngineWebKit.setSelection( defaultBrowser == EvConstants.PREFERENCE_RENDERENGINE_WEBKIT );
		}
		if (_radioRenderEngineXULRunner != null) {
			_radioRenderEngineXULRunner.setSelection( defaultBrowser == EvConstants.PREFERENCE_RENDERENGINE_XULRUNNER );
		}
		if ( _radioRenderEngineIE != null ) {
			_radioRenderEngineIE.setSelection( defaultBrowser == EvConstants.PREFERENCE_RENDERENGINE_IE );
		}

		// Widget name
		//------------
		_checkPromptForNewWidgetName.setSelection( EvConstants.PREFERENCE_DEFAULT_PROMPT_FOR_A_NEW_WIDGET_NAME );
	}

	/**
	 * Called when either the Apply or Ok buttons are pressed.
	 */
	public boolean performOk() {

		// Editor opening tab
		//-------------------
		int iValue = 0;

		if( _radioEditorTabSource.getSelection() == true )
			iValue = 1;
		else if( _radioEditorTabPreview.getSelection() == true )
			iValue = 2;

		EvPreferences.setInt( EvConstants.PREFERENCE_EDITOR_OPENING_TAB, iValue );
		
		// Transparency
		//-------------
		iValue = EvConstants.SEMITRANSPARENCY_NONE;

		if( _radioTransparencyFixed.getSelection() == true )
			iValue = EvConstants.SEMITRANSPARENCY_FIXED;
		else if( _radioTransparencyVariable.getSelection() == true )
			iValue = EvConstants.SEMITRANSPARENCY_VARIABLE;

		EvPreferences.setInt( EvConstants.PREFERENCE_SEMITRANSPARENCY_MODE, iValue );
		EvPreferences.setInt( EvConstants.PREFERENCE_SEMITRANSPARENCY_AMOUNT, _sliderTransparencyAmount.getSelection() );

		// Half transparency while dragging
		//---------------------------------
		EvPreferences.setBoolean( EvConstants.PREFERENCE_SEMITRANSPARENCY_WHILE_DRAGGING, _checkSemiTransparencyWhileDragging.getSelection() );

		// Show transparency controls
		//---------------------------
		EvPreferences.setBoolean( EvConstants.PREFERENCE_TRANSPARENCY_CONTROLS_VISIBLE, _checkShowTransparencyControls.getSelection() );

		// Selection border color
		//-----------------------
		String strColor = _buttonColorSelection.getColor();
		String strCurrent = EvPreferences.getString( EvConstants.PREFERENCE_COLOR_SELECTION );

		if( strColor.equals( strCurrent ) == false )
			EvPreferences.setString( EvConstants.PREFERENCE_COLOR_SELECTION, strColor );

		// Selected border pattern
		//------------------------
		boolean bCurrent = EvPreferences.getBoolean( EvConstants.PREFERENCE_PATTERN_SELECTION );
		boolean bPattern = _checkPatternSelection.getSelection();
		if( bPattern != bCurrent )
			EvPreferences.setBoolean( EvConstants.PREFERENCE_PATTERN_SELECTION, bPattern );
		
		// Potential drop target color
		//----------------------------
		strColor = _buttonColorDropLocationPotential.getColor();
		strCurrent = EvPreferences.getString( EvConstants.PREFERENCE_COLOR_DROP_LOCATION_POTENTIAL );

		if( strColor.equals( strCurrent ) == false )
			EvPreferences.setString( EvConstants.PREFERENCE_COLOR_DROP_LOCATION_POTENTIAL, strColor );

		// Selected drop target color
		//---------------------------
		strColor = _buttonColorDropLocationSelected.getColor();
		strCurrent = EvPreferences.getString( EvConstants.PREFERENCE_COLOR_DROP_LOCATION_SELECTED );

		if( strColor.equals( strCurrent ) == false )
			EvPreferences.setString( EvConstants.PREFERENCE_COLOR_DROP_LOCATION_SELECTED, strColor );
		
		// Selected drop target pattern
		//-----------------------------
		bCurrent = EvPreferences.getBoolean( EvConstants.PREFERENCE_PATTERN_DROP_LOCATION_SELECTED );
		bPattern = _checkPatternDropLocationSelected.getSelection();
		if( bPattern != bCurrent )
			EvPreferences.setBoolean( EvConstants.PREFERENCE_PATTERN_DROP_LOCATION_SELECTED, bPattern );
		
		// Performance
		//------------
		iValue = 0;

		if( _radioOptimizeSpeed.getSelection() == true )
			iValue = EvConstants.PREFERENCE_PERFORMANCE_OPTIMIZE_SPEED;
		else if( _radioOptimizeResources.getSelection() == true )
			iValue = EvConstants.PREFERENCE_PERFORMANCE_OPTIMIZE_RESOURCES;

		EvPreferences.setInt( EvConstants.PREFERENCE_PERFORMANCE, iValue );
		
		// Render Engine
		//------------
		iValue = 0;

		if( _radioRenderEngineUserConfigured != null && _radioRenderEngineUserConfigured.getSelection() == true )
			iValue = EvConstants.PREFERENCE_RENDERENGINE_USER_CONFIGURED;
		else if( _radioRenderEngineWebKit != null && _radioRenderEngineWebKit.getSelection() == true )
			iValue = EvConstants.PREFERENCE_RENDERENGINE_WEBKIT;
		else if( _radioRenderEngineXULRunner != null && _radioRenderEngineXULRunner.getSelection() == true )
			iValue = EvConstants.PREFERENCE_RENDERENGINE_XULRUNNER;
		else if( _radioRenderEngineIE != null && _radioRenderEngineIE.getSelection() == true )
			iValue = EvConstants.PREFERENCE_RENDERENGINE_IE;

		EvPreferences.setInt( EvConstants.PREFERENCE_RENDERENGINE, iValue );
		
		// Widget creation
		//----------------
		bCurrent = EvPreferences.getBoolean( EvConstants.PREFERENCE_PROMPT_FOR_A_NEW_WIDGET_NAME );
		boolean bPrompt = _checkPromptForNewWidgetName.getSelection();
		if( bPrompt != bCurrent )
			EvPreferences.setBoolean( EvConstants.PREFERENCE_PROMPT_FOR_A_NEW_WIDGET_NAME, bPrompt );
		
		cleanup();

		return true;
	}

	/**
	 * Updates the transparency amount slider position and transparency percentage amount
	 */
	public void updateTransparencyControls() {
		int iPercent = ( _sliderTransparencyAmount.getSelection() * 100 ) / 255;

		// Space is added to the end, otherwise the label not wide enough if the value is single/double digit
		//---------------------------------------------------------------------------------------------------
		StringBuffer strb = new StringBuffer();
		double dPercentage = new Double( iPercent ).doubleValue();

		if( dPercentage < 100d ) // 0 to 100
			strb.append( "  " ); //$NON-NLS-1$
		if( dPercentage < 0.1d )
			strb.append( "  " ); //$NON-NLS-1$

		_labelTransparencyPercent.setText( _decimalFormat.format( dPercentage / 100 ) + strb.toString() );

		// Enable / disable slider
		//------------------------
		_sliderTransparencyAmount.setEnabled( _radioTransparencyVariable.getSelection() );

	}

	/**
	 * Declared in SelectionListener
	 */
	public void widgetDefaultSelected( SelectionEvent event ) {
		widgetSelected( event );
	}

	/**
	 * Declared in SelectionListener
	 */
	public void widgetSelected( SelectionEvent event ) {
		
		if( event.widget == _checkPatternDropLocationSelected )
			_buttonColorDropLocationSelected.setPattern( _checkPatternDropLocationSelected.getSelection() == true ? ColorSelectorButton.PATTERN_HATCH : ColorSelectorButton.PATTERN_NONE );
		
		else if( event.widget == _checkPatternSelection )
			_buttonColorSelection.setPattern( _checkPatternSelection.getSelection() == true ? ColorSelectorButton.PATTERN_DENTAL : ColorSelectorButton.PATTERN_NONE );

		else
			updateTransparencyControls();
	}
}
