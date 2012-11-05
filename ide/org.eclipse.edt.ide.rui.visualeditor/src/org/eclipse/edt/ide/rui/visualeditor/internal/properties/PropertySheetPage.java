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

import org.eclipse.edt.ide.rui.visualeditor.internal.actions.EvActionPropertyViewFilter;
import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvConstants;
import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvEditor;
import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvHelp;
import org.eclipse.edt.ide.rui.visualeditor.internal.nl.Messages;
import org.eclipse.edt.ide.rui.visualeditor.internal.util.EvFunctionNameDialog;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetDescriptor;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetDescriptorRegistry;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetEventDescriptor;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPart;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPropertyDescriptor;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPropertyValue;
import org.eclipse.edt.ide.rui.visualeditor.plugin.Activator;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IPartSelectionListener;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.views.properties.IPropertySheetPage;


public class PropertySheetPage extends Page implements DisposeListener, IPartSelectionListener, IPropertySheetPage, SelectionListener {

	public static int filter_Type = EvConstants.FILTER_HIDE_EXCLUDED;
	
	private IMenuManager submenu;
	private EvActionPropertyViewFilter filter_ShowAll;
	private EvActionPropertyViewFilter filter_HideExcluded;
	private boolean layoutChanged                   = false;
	protected static PropertySheetPage	_instance	= null;

	/**
	 * Returns the singleton instance of this class.
	 */
	public static PropertySheetPage getInstance() {
		if( _instance == null )
			_instance = new PropertySheetPage();

		return _instance;
	}

	protected Composite						_composite					= null;
	protected Cursor						_cursorWait					= null;
	protected IEvPropertySheetPageAdapter	_editorAdapter				= null;
	protected Label							_labelWidgetTypeImage		= null;
	protected Label							_labelWidgetTypeLabel		= null;
	protected String						_selectedTabName			= "";
	protected PropertyPage					_propertyPage				= null;
	protected WidgetPart					_widgetSelected				= null;

	/**
	 * Construction of this class is not allowed by other classes.  Use the getInstance method to obtain the singleton instance.
	 */
	private PropertySheetPage() {
	}

	/**
	 * Creates the user interface and adds the part listener. 
	 */
	public void createControl( Composite compositeParent ) {

		if( _composite != null && _composite.isDisposed() == false )
			return;

		_composite = new Composite( compositeParent, SWT.NONE );
		GridLayout gridLayout = new GridLayout();
		_composite.setLayout( gridLayout );
	
		Composite compositeHeader = new Composite( _composite, SWT.NONE );
		GridData gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		compositeHeader.setLayoutData( gridData );
		gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		compositeHeader.setLayout( gridLayout );
		compositeHeader.setBackground( _composite.getBackground() );

		// A label that displays the widget icon
		//--------------------------------------
		_labelWidgetTypeImage = new Label( compositeHeader, SWT.NULL );
		gridData = new GridData();
		_labelWidgetTypeImage.setLayoutData( gridData );
		_labelWidgetTypeImage.setBackground( _composite.getBackground() );

		// A label that displays the widget type
		//--------------------------------------
		_labelWidgetTypeLabel = new Label( compositeHeader, SWT.NULL );
		gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		_labelWidgetTypeLabel.setLayoutData( gridData );
		_labelWidgetTypeLabel.setBackground( _composite.getBackground() );

		// Separator line
		//---------------
		Label label = new Label( _composite, SWT.SEPARATOR | SWT.HORIZONTAL );
		gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		label.setLayoutData( gridData );

		//Add listeners
		//--------------
		compositeParent.addDisposeListener( this );
		
		//Register the actions
		registerActions();

		EvHelp.setHelp( _composite, EvHelp.PROPERTY_PAGE );
	}

	/**
	 * Displays a dialog where a person can type in the name of a function.
	 * The adapter is asked to create the function.
	 * A true is returned if a new function is created.
	 */
	public boolean createNewEventHandlingFunction( TableItem tableItem ) {
		if( _editorAdapter == null )
			return false;

		// If an event is selected, then the title is the event name
		//----------------------------------------------------------
		String strEventName = _widgetSelected.getVariableName() + "_";
		if( tableItem != null )
			strEventName += tableItem.getText( 0 );
		
		EvFunctionNameDialog dialog = new EvFunctionNameDialog( _composite.getShell(), _editorAdapter.getEditorProvider(), strEventName );
		dialog.open();

		String strFunctionName = dialog.getName();

		if( dialog.getReturnCode() == Dialog.OK ) {
			// First insert the value to the function being created. The function insertion can add an import which throws off the offsets so it must come afterwards.
			if( tableItem != null ){
				setEventValue( tableItem, strFunctionName, false );
			}
			
			_editorAdapter.createEventHandlingFunction( strFunctionName );
			
			int offset = _editorAdapter.getEditorProvider().getFunctionFirstLineOffset( strFunctionName );
			_editorAdapter.selectAndRevealRange( offset, 0 );
			return true;
		}

		return false;
	}

	/**
	 * Notifies the editor that a property value of the selected widget has changed.
	 */
	public void eventValueChanged( WidgetEventDescriptor descriptor, String strValueOld, String strValueNew, boolean refresh ) {
		if( _editorAdapter == null )
			return;

		_editorAdapter.eventChanged( _widgetSelected, descriptor, strValueOld, strValueNew, refresh );
	}

	/**
	 * 
	 */
	public Control getControl() {
		return _composite;
	}

	/**
	 * 
	 */
	public IEvPropertySheetPageAdapter getEditorAdapter() {
		return _editorAdapter;
	}

	/**
	 * Called by the property page when it populates the drop down list of available function names.
	 */
	public String[] getEventHandlingFunctionNames() {
		return _editorAdapter.getEventHandlingFunctionNames();
	}

	/**
	 * Notifies the editor that a property value of the selected widget has changed.
	 */
	public void propertyValueChanged( WidgetPropertyDescriptor descriptor, WidgetPropertyValue valueOld, WidgetPropertyValue valueNew ) {
		if( _editorAdapter == null )
			return;

		Control control = getControl();
		if( _cursorWait == null )
			_cursorWait = new Cursor( control.getDisplay(), SWT.CURSOR_WAIT );

		control.setCursor( _cursorWait );

		_editorAdapter.propertyChanged( _widgetSelected, descriptor, valueOld, valueNew );
		
		control.setCursor( null );
	}
	
	public void setLayoutChanged( boolean changed ) {
		this.layoutChanged = changed;
	}

	/**
	 * Declared in IPartSelectionListener.  Does nothing.
	 */
	public void selectionChanged( IFormPart part, ISelection selection ) {
	}

	/**
	 * Declared in IPartSelectionListener.
	 * Called when the editor is activated during editor open, or when a user switches to the editor. 
	 */
	public void selectionChanged( IWorkbenchPart part, ISelection selection ) {
		if( part == null || part instanceof IEditorPart == false )
			return;

		IEvPropertySheetPageAdapter editorAdapter = (IEvPropertySheetPageAdapter)part.getAdapter( IEvPropertySheetPageAdapter.class );

		boolean bRefreshRequired = false;
		
		if( editorAdapter != _editorAdapter ) {
			_editorAdapter = editorAdapter;
			bRefreshRequired = true;
		}

		// This is required when a user switches editors.
		//-----------------------------------------------
		if( _editorAdapter != null ) {
			WidgetPart widget = _editorAdapter.getWidgetSelected();
			widgetSelected( widget, editorAdapter, bRefreshRequired );
		}
	}

	/**
	 * Called by the property page whenever a person selects a function for an event.
	 */
	public void setEventValue( TableItem tableItem, String strFunctionName, boolean refresh ) {
		WidgetEventDescriptor descriptor = (WidgetEventDescriptor)tableItem.getData( "descriptor" );
		tableItem.setText( 1, strFunctionName != null ? strFunctionName : "" );

		// Obtain the current value
		//-------------------------
		WidgetPropertyValue propertyValue = _editorAdapter.getEventValue( _widgetSelected, descriptor.getID() );
		if( propertyValue == null && strFunctionName == null )
			return;

		String strValueCurrent = null;
		ArrayList listValues = null;

		if( propertyValue != null )
			listValues = propertyValue.getValues();

		if( listValues != null && listValues.size() > 0 )
			strValueCurrent = (String)listValues.get( 0 );

		if( strValueCurrent == null && strFunctionName == null )
			return;

		// Both non null and equal
		//------------------------
		if( strValueCurrent != null && strFunctionName != null )
			if( strValueCurrent.equals( strFunctionName ) == true )
				return;

		// Only one is null, or both not null and not equal
		//-------------------------------------------------
		_editorAdapter.eventChanged( _widgetSelected, descriptor, strValueCurrent, strFunctionName, refresh );
	}

	/**
	 * Declared in Page.  Does nothing.
	 */
	public void setFocus() {
	}

	/**
	 * Called when the property page tab has been selected.
	 */
	public void tabFolderPageNameChanged() {
		// Obtain the tab folder page name of the page the user is viewing
		//-----------------------------------------------------------------
		_selectedTabName = _propertyPage.getTabFolderPageName();
	}

	/**
	 * Updates the existing property page with current widget property values.
	 * This method is not being used.  It is possible to call this from the editor's
	 * modelChanged method so that the property page is updated whenever the source
	 * changes.  However, it would also require changing the widget selection whenever
	 * the cursor moves, which isn't implemented.
	 */
	public void update() {
		if( _propertyPage == null || _widgetSelected == null || _editorAdapter == null )
			return;

		String strStatement = _editorAdapter.getDocumentStatement( _widgetSelected );
		if( strStatement != null ) {
			String strStatement1 = strStatement.replaceAll( "\r", "" );
			_labelWidgetTypeLabel.setToolTipText( strStatement1 );
		}

		String[] straFunctionNames = _editorAdapter.getEventHandlingFunctionNames();
		_propertyPage.reloadValues( _widgetSelected, straFunctionNames );
	}

	/**
	 * Declared in SelectionListener
	 */
	public void widgetDefaultSelected( SelectionEvent event ) {
		widgetSelected( event );
	}

	/**
	 * Declared in DisposeListener.
	 * Called when the composite is disposed.  The wait cursor is disposed.
	 */
	public void widgetDisposed( DisposeEvent e ) {
		if( _cursorWait != null && _cursorWait.isDisposed() == false ){
			_cursorWait.dispose();
			_cursorWait = null;
		}
	}

	/**
	 * Declared in SelectionListener.
	 * Called when the create new event handling function tool bar item is pressed.
	 */
	public void widgetSelected( SelectionEvent event ) {
		createNewEventHandlingFunction( null );
	}

	/**
	 * Called by the editor when a widget is selected.
	 */
	public void widgetSelected( WidgetPart widgetSelected, IEvPropertySheetPageAdapter editorAdapter ) {
		widgetSelected( widgetSelected, editorAdapter, false );
	}
	
	/**
	 * Called by the Filter action.
	 */
	public void refreshWidgetControl() {
		widgetSelected( _widgetSelected, _editorAdapter, true );
	}

	/**
	 * Called by the editor.  A refresh is required if the bidirectional enable preference has changed.
	 */
	public void widgetSelected( final WidgetPart widgetSelected, final IEvPropertySheetPageAdapter editorAdapter, final boolean bRefreshRequired ) {
		if( _labelWidgetTypeLabel == null || _labelWidgetTypeLabel.isDisposed() == true )
			return;

		this.getControl().getDisplay().asyncExec( new Runnable() {
			public void run()
			{
				widgetSelectedOperation( widgetSelected, editorAdapter, bRefreshRequired );
			}
		});
	}

	/**
	 * Called by the editor.  A refresh is required if the bidirectional enable preference has changed.
	 */
	private void widgetSelectedOperation( WidgetPart widgetSelected, IEvPropertySheetPageAdapter editorAdapter, boolean bRefreshRequired ) {
		if( _labelWidgetTypeLabel == null || _labelWidgetTypeLabel.isDisposed() == true )
			return;
		
		//just set the _widgetSelected, if the change is from Properties View.
		if ( _propertyPage != null && _propertyPage._propertyChangedFromPropertiesView ) {
			_propertyPage._propertyChangedFromPropertiesView = false;
			_widgetSelected = widgetSelected;
			return;
		}
		
		_editorAdapter = editorAdapter;
		EvEditor editor = (EvEditor)editorAdapter;

		// Disallow widgets with no statement (external RUI Handler)
		//----------------------------------------------------------
		if( widgetSelected != null && ( widgetSelected.getStatementOffset() < 0 || widgetSelected.getStatementLength() <= 0 ) )
			widgetSelected = null;

		// Check for property page disposal
		//---------------------------------
		if( _propertyPage != null && _propertyPage.isDisposed() == true )
			_propertyPage = null;

		//
		WidgetDescriptorRegistry registry = WidgetDescriptorRegistry.getInstance(editor.getProject());
		// Do nothing if the current widget is the same as the requested
		//--------------------------------------------------------------
		if( _propertyPage != null && widgetSelected != null && widgetSelected.isSameWith(_widgetSelected)  && bRefreshRequired == false ) {
			if ( layoutChanged ) {
				_propertyPage.recreateControlsForLayout( widgetSelected, registry );
				_propertyPage.setTabFolderPageName( _selectedTabName );
				layoutChanged = false;
				_composite.layout();
				_composite.getParent().layout();
			}
			return;
		}
		
		// Remember the existing type
		//---------------------------
		String strTypeOld = null;
		String strTypeNew = null;

		if( _widgetSelected != null )
			strTypeOld = _widgetSelected.getTypeID();

		if( widgetSelected != null )
			strTypeNew = widgetSelected.getTypeID();

		// Remove any existing property page
		//----------------------------------
		// Determine if the widget type has changed
		//-----------------------------------------
		boolean bTypeChanged = false;

		if( strTypeOld != null && strTypeNew != null ) {
			if( strTypeOld.equals( strTypeNew ) == false || bRefreshRequired ) // IBMBIDI Change
				bTypeChanged = true;
		}

		else if( widgetSelected == null && _widgetSelected != null || _widgetSelected == null && widgetSelected != null )
			bTypeChanged = true;

		// Send the widget back to editor when a property changes
		//-------------------------------------------------------
		_widgetSelected = widgetSelected;

		if( _propertyPage != null ) {
			// Obtain the current set of section expansion states
			//---------------------------------------------------
			_selectedTabName = _propertyPage.getTabFolderPageName();

			if ( bTypeChanged ) {
				// Dispose of the old property page
				//---------------------------------
				_propertyPage.dispose();
				_propertyPage = null;
			}
		}

		// Display the new widget type name and variable name
		//---------------------------------------------------
		if( _widgetSelected != null ){
			_labelWidgetTypeImage.setImage( Activator.getDefault().getWidgetImage( widgetSelected ) );
			_labelWidgetTypeLabel.setText( widgetSelected.getLabel() );
		}
		else{
			_labelWidgetTypeImage.setImage( null );
			_labelWidgetTypeLabel.setText( "" );
			_labelWidgetTypeLabel.setToolTipText( "" );
			return;
		}

		// Show the source code statement as tooltip text
		//-----------------------------------------------
		String strStatement = editorAdapter.getDocumentStatement( _widgetSelected );
		if( strStatement != null ) {
			String strStatement1 = strStatement.replaceAll( "\r", "" );
			_labelWidgetTypeLabel.setToolTipText( strStatement1 );
		}

		WidgetDescriptor descriptor = registry.getDescriptor( strTypeNew );
		if( descriptor == null )
			return;

		WidgetPropertyDescriptor[] propertyDescriptors = descriptor.getPropertyDescriptors( filter_Type );
		WidgetEventDescriptor[] eventDescriptors = descriptor.getEventDescriptors( filter_Type );

		// Check for no descriptors
		//-------------------------
		if( propertyDescriptors == null || eventDescriptors == null )
			return;

		if( propertyDescriptors.length == 0 && eventDescriptors.length == 0 )
			return;

		// Create a new property page
		//---------------------------
		String[] straFunctionNames = _editorAdapter.getEventHandlingFunctionNames();

		// If there is no property page, create one
		//-----------------------------------------
		if( _propertyPage == null ) {
			_propertyPage = new PropertyPage( _composite, this, _widgetSelected, straFunctionNames, editor );
		}

		// Reload the existing property page with new values
		//--------------------------------------------------
		else {
			// IBMBIDI Append Start
			if( bRefreshRequired ) {
				_propertyPage.createControlsForProperties( descriptor, widgetSelected, registry );
			}
			// IBMBIDI Append End
			
			_propertyPage.reloadValues( _widgetSelected, straFunctionNames );
			_propertyPage.recreateControlsForLayout( widgetSelected, registry );
		}
		_composite.layout();
		_composite.getParent().layout();
		
		_propertyPage.setTabFolderPageName(_selectedTabName);
	}
	
	private void registerActions() {

		IActionBars actionBars = getSite().getActionBars();
		IMenuManager menu = actionBars.getMenuManager();

		submenu = new MenuManager( Messages.NL_Menu_Filter );
		menu.add(submenu);
		filter_ShowAll = new EvActionPropertyViewFilter( this, EvConstants.FILTER_SHOW_ALL );
		filter_ShowAll.setText( Messages.NL_Menu_Filter_show_all );
		submenu.add( filter_ShowAll );
		
		filter_HideExcluded = new EvActionPropertyViewFilter( this, EvConstants.FILTER_HIDE_EXCLUDED );
		filter_HideExcluded.setText( Messages.NL_Menu_Filter_hide_excluded );
		submenu.add( filter_HideExcluded );

		menu.addMenuListener( new IMenuListener() {

			public void menuAboutToShow(IMenuManager manager) {
				filter_ShowAll.setChecked( false );
				filter_HideExcluded.setChecked( false );
				
				switch ( PropertySheetPage.filter_Type ) {
					case EvConstants.FILTER_SHOW_ALL :
						filter_ShowAll.setChecked( true );
						break;
					case EvConstants.FILTER_HIDE_EXCLUDED :
						filter_HideExcluded.setChecked( true );
						break;
				}
				
				if (submenu != null)
					submenu.updateAll(true);
			}
			
		});

	}

}
