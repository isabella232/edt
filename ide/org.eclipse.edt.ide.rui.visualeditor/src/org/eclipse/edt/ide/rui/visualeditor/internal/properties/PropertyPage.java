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
import java.util.Hashtable;
import java.util.Vector;

import org.eclipse.edt.ide.rui.document.utils.IVEConstants;
import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvConstants;
import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvEditor;
import org.eclipse.edt.ide.rui.visualeditor.internal.nl.Messages;
import org.eclipse.edt.ide.rui.visualeditor.internal.nl.Tooltips;
import org.eclipse.edt.ide.rui.visualeditor.internal.util.TableCellEditorComboBox;
import org.eclipse.edt.ide.rui.visualeditor.internal.util.TableCellEditorComboBoxListener;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetDescriptor;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetDescriptorRegistry;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetEventDescriptor;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetLayoutDescriptor;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPart;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPropertyDescriptor;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPropertyValue;
import org.eclipse.edt.ide.rui.visualeditor.plugin.Activator;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;


/**
 * Presents a tab folder with properties and events tab items.
 */
public class PropertyPage extends ScrolledComposite implements MouseListener, SelectionListener, TableCellEditorComboBoxListener {

	protected TableCellEditorComboBox	_comboCellEditor		= null;
	protected Composite					_composite				= null;
	protected Hashtable					_hashTabs				= new Hashtable();
	protected PropertySheetPage			_propertySheet			= null;
	protected String[]					_straFunctionNames		= null;
	protected CTabFolder				_tabFolder				= null;
	protected Table						_table					= null;
	protected TableEditor				_tableEditor			= null;
	protected TableEditor				_tableAddButtonEditor	= null;
	protected ToolBar					_toolbar				= null;
	protected Vector					_vectorPropertyEditors	= new Vector();
	protected Vector					_vectorLayoutPropertyEditors	= new Vector();
	protected WidgetPart				_widgetPart				= null;
	protected PropertyTabComposite      _layoutComposite		= null;
	protected boolean					_propertyChangedFromPropertiesView = false;
	
	//The minimum width of the "+" button in the Event table
	private final static int 			EVENT_TABLE_ADD_COLUMN_WIDTH = 20;
	//the zero-based column index of "+" button in the Event table
	private final static int 			EVENT_TABLE_EDITABLE_COLUMN  = 2;

	/**
	 * Inner class to hold expansion state of a section 
	 */
	public class SectionExpansionState {
		public boolean	bExpanded		= false;
		public String	strSectionName	= null;
	}
	
	/**
	 *
	 */
	public PropertyPage( Composite compositeParent, PropertySheetPage propertySheet, WidgetPart widgetPart, String[] straFunctionNames, EvEditor editor ) {
		super( compositeParent, SWT.H_SCROLL | SWT.V_SCROLL );

		_propertySheet = propertySheet;
		_straFunctionNames = straFunctionNames;
		_widgetPart = widgetPart;

		// This
		//-----
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		setLayout( gridLayout );
		
		GridData gridData = new GridData( GridData.FILL_BOTH );
		setLayoutData( gridData );

		// Composite within the scrolled composite
		//----------------------------------------
		_composite = new Composite( this, SWT.NULL );
		
		// I am a scrolled composite
		//--------------------------
		setContent( _composite );

		gridLayout = new GridLayout();
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		_composite.setLayout( gridLayout );
		
		createControls( widgetPart, editor );

		// Set size of composite
		//----------------------
		Point ptSize = _composite.computeSize( SWT.DEFAULT, SWT.DEFAULT );
		ptSize.x = Math.max( ptSize.x, 500 );
		_composite.setSize( ptSize );
	}

	/**
	 * Activate a cell editor.
	 */
	protected void activateCellEditor( TableItem tableItem ) {
		// Activate the appropriate cell editor for the column
		// If the value of the cell changes, then one of the cell changed methods will be called
		//--------------------------------------------------------------------------------------
		String[] straNames = _propertySheet.getEventHandlingFunctionNames();
		//		String[] straNames = { "abcd", "efgh" };
		String[] straNamesExtended = new String[straNames.length + 1];
		straNamesExtended[ 0 ] = "";
		System.arraycopy( straNames, 0, straNamesExtended, 1, straNames.length );

		_comboCellEditor.activate( _table, _tableEditor, 1, tableItem, straNamesExtended );
	}
	
	/**
	 * Declared in TableCellEditorComboBoxListener.
	 */
	public void cellComboBoxChanged( TableItem tableItem, int column, int iRow, String strOriginalValue, String strNewValue ) {

		switch( iRow ){
			case 0:
				// The 'empty' entry has been selected
				// Remove the property from the source code
				//-----------------------------------------
				_propertySheet.setEventValue( tableItem, null, false );
				break;

			default:
				// A function name was selected
				// Associate it with the event
				//-----------------------------
				_propertySheet.setEventValue( tableItem, strNewValue, false );
		}
	}

	/**
	 * Creates an expandable section.
	 */
	protected CTabItem createCategoryTab( String strLabel, int index ) {
		
		strLabel = translateCategoryName( strLabel );
		
		CTabItem tabItem = new CTabItem( _tabFolder, SWT.NULL, index );
		tabItem.setText( strLabel );
		
		PropertyTabComposite compositeCategory = new PropertyTabComposite( _tabFolder, SWT.NONE );
		GridLayout gridLayout = new GridLayout();
		compositeCategory.setLayout( gridLayout );
		tabItem.setControl( compositeCategory );

		return tabItem;
	}

	/**
	 * 
	 */
	protected void createControls( WidgetPart widgetPart, EvEditor editor ) {
		String strTypeID = widgetPart.getTypeID();

		// Other properties
		//-----------------
		WidgetDescriptorRegistry registry = WidgetDescriptorRegistry.getInstance(editor.getProject());
		WidgetDescriptor descriptor = registry.getDescriptor( strTypeID );

		if( descriptor == null )
			return;

		_tabFolder = new CTabFolder( _composite, SWT.NONE );
		_tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
	
		// Properties
		//-----------
		createControlsForProperties( descriptor, widgetPart, registry );

		// Events
		//-------
		CTabItem itemEvents = new CTabItem( _tabFolder, SWT.NULL );
		itemEvents.setText( Messages.NL_Events );
		_hashTabs.put(Messages.NL_Events, itemEvents);

		Composite compositeEvents = new Composite( _tabFolder, SWT.NULL );
		compositeEvents.setBackground( getDisplay().getSystemColor( SWT.COLOR_LIST_BACKGROUND ) );
		itemEvents.setControl( compositeEvents );
		GridLayout gridLayout = new GridLayout();
		compositeEvents.setLayout( gridLayout );

		createControlsForEvents( compositeEvents, descriptor, widgetPart );
		
		createControlsForLayout(widgetPart, registry );
		
		// Initialize scroll bars
		//-----------------------
		ScrollBar scrollBar = getVerticalBar();
		scrollBar.setIncrement( 16 );
		scrollBar.setPageIncrement( 200 );
		
		scrollBar = getHorizontalBar();
		scrollBar.setIncrement( 16 );
		scrollBar.setPageIncrement( 200 );
		
		_tabFolder.setSelection( 0 );
		_tabFolder.addSelectionListener( this );
		
		
	}

	/**
	 * Creates the event controls which include a label and a drop down combo for each event.
	 */
	protected void createControlsForEvents( Composite compositeParent, WidgetDescriptor descriptor, WidgetPart widgetPart ) {
		// Create cell editors
		//--------------------
		_comboCellEditor = new TableCellEditorComboBox();
		_comboCellEditor.addTableCellEditorComboListener( this );

		_table = new Table( compositeParent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION );
		GridData gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH );
		gridData.heightHint = 200;
		gridData.minimumWidth = 250;
		_table.setLayoutData( gridData );

		String[] straColumnTitles = { Messages.NL_Event, Messages.NL_Function, "  " };
		int[] iaColumnWidths = new int[3];

		GC gc = new GC( _table );
		Font f = _table.getFont();
		gc.setFont( f );

		for( int i = 0; i < straColumnTitles.length; ++i )
			iaColumnWidths[ i ] = gc.textExtent( straColumnTitles[ i ] ).x;

		gc.dispose();

		// Column weights 
		//---------------------------------------
		int[] iaColumnWeights = new int[3];
		iaColumnWeights[ 0 ] = 50;
		iaColumnWeights[ 1 ] = 42;
		iaColumnWeights[ 2 ] = 8;

		// Table layout
		//-------------
		TableLayout layoutTable = new TableLayout();

		for( int i = 0; i < straColumnTitles.length; i++ ) {
			TableColumn tableColumn = new TableColumn( _table, SWT.NONE );
			tableColumn.setText( straColumnTitles[ i ] );
			ColumnLayoutData cLayout = new ColumnWeightData( iaColumnWeights[ i ], iaColumnWidths[ i ], true );
			layoutTable.addColumnData( cLayout );
		}

		_table.setLinesVisible( true );
		_table.setHeaderVisible( true );
		_table.setLayout( layoutTable );
		_table.addMouseListener( this );
		_table.addSelectionListener( this );

		_tableEditor = new TableEditor( _table );
		_tableAddButtonEditor  = new TableEditor( _table );
		_tableAddButtonEditor.horizontalAlignment = SWT.CENTER;
		_tableAddButtonEditor.verticalAlignment = SWT.CENTER;
		_tableAddButtonEditor.minimumWidth = EVENT_TABLE_ADD_COLUMN_WIDTH;

		// Add events to the table
		//------------------------
		WidgetEventDescriptor[] events = descriptor.getEventDescriptors( PropertySheetPage.filter_Type );

		for( int i = 0; i < events.length; i++ ) {
			// Set up values for each cell in the table row
			//---------------------------------------------
			String[] straData = new String[2];
			straData[ 0 ] = events[ i ].getID();

			WidgetPropertyValue propertyValue = _propertySheet.getEditorAdapter().getEventValue( widgetPart, events[ i ].getID() );
			String strValue = null;
			
			if( propertyValue != null ){
				ArrayList listValues = propertyValue.getValues();
				if( listValues != null && listValues.size() > 0 )
					strValue = (String)listValues.get( 0 );
			}
			
			straData[ 1 ] = strValue != null ? strValue : "";

			// Create a table item row and set the row data
			//---------------------------------------------
			TableItem item = new TableItem( _table, SWT.NULL );
			item.setText( straData );

			// Store the event descriptor with the table item
			//-----------------------------------------------
			item.setData( "descriptor", events[ i ] );
			item.setData( "propertyvalue", propertyValue );
		}
	}

	/**
	 * Creates the user interface for the Properties tab.
	 */
	protected void createControlsForProperties( WidgetDescriptor descriptor, WidgetPart widgetPart, WidgetDescriptorRegistry registry ) {

		if( descriptor == null )
			return;

		_vectorPropertyEditors.clear();
		
		WidgetPropertyDescriptor[] properties = descriptor.getPropertyDescriptors( PropertySheetPage.filter_Type );

		if( properties.length == 0 )
			return;
		
		for( int i = 0; i < properties.length; ++i ) {
			CTabItem tabItem;
			
			// Create a composite for the category if one doesn't exist
			//---------------------------------------------------------
			String strCategory = properties[ i ].getCategory();
			if( strCategory == null || strCategory.length() == 0 ) {
				strCategory = "General";
			}
			
			if( _hashTabs.containsKey( strCategory ) == false ) {
				String strCategoryLabel = strCategory;
				tabItem = createCategoryTab( strCategoryLabel, _tabFolder.getItemCount() );
				_hashTabs.put( strCategory, tabItem );
			}
			else{
				tabItem = (CTabItem)_hashTabs.get( strCategory );
			}
			
			PropertyTabComposite compositeParent = (PropertyTabComposite)tabItem.getControl();
			String strType = properties[ i ].getType();

			createPropertyEditor(compositeParent, properties[i], _vectorPropertyEditors);
		}
	}
	
	private void createPropertyEditor(PropertyTabComposite compositeParent, WidgetPropertyDescriptor property, Vector propertyEditors ){
		PropertyEditorAbstract editor = null;
		String strType = property.getType();

		// Otherwise create single editors for each property
		//--------------------------------------------------
		if( strType.equals( IVEConstants.BOOLEAN_TYPE ) == true )
			editor = createEditorBoolean( compositeParent.getBooleanElementsComposite(), property );

		else if( strType.equals( IVEConstants.CHOICE_TYPE ) == true )
			editor = createEditorChoice( compositeParent.getNonBooleanElementComposite(), property );

		else if( strType.equals( IVEConstants.COLOR_TYPE ) == true )
			editor = createEditorColor( compositeParent.getNonBooleanElementComposite(), property );

		else if( strType.equals( IVEConstants.INTEGER_TYPE ) == true )
			editor = createEditorInteger( compositeParent.getNonBooleanElementComposite(), property );

		else if( strType.equals( IVEConstants.STRING_TYPE ) == true )
			editor = createEditorString( compositeParent.getNonBooleanElementComposite(), property );

		else if( strType.equals( IVEConstants.STRING_ARRAY_TYPE ) == true )
			editor = createEditorStringArray( compositeParent.getNonBooleanElementComposite(), property );

		if( editor == null )
			return;

		if(propertyEditors != null){
			propertyEditors.add( editor );
		}
		editor.initialize();
	}
	/**
	 * Creates the user interface for the Layout tab.  This method MUST be invoked AFTER the events tab is created
	 */
	protected void createControlsForLayout( WidgetPart widgetPart, WidgetDescriptorRegistry registry ) {
		boolean widgetHasLayout = true;
		
		WidgetDescriptor parentDescriptor = null;
		WidgetPropertyDescriptor[] properties = null;
		if ( widgetPart.getParent() == null || (parentDescriptor = registry.getDescriptor(widgetPart.getParent().getPackageName(), widgetPart.getParent().getTypeName()) ) == null || parentDescriptor._layoutDescriptor == null ) {
			widgetHasLayout = false;
		}else{
			WidgetLayoutDescriptor descriptor = parentDescriptor._layoutDescriptor;
			properties = descriptor.getPropertyDescriptors();

			if( properties.length == 0 ){
				widgetHasLayout = false;
			}
		}
		
		if(!widgetHasLayout){
			// This widget does not have a layout, remove the tab if it exists
			if ( _layoutComposite != null && !_layoutComposite.isDisposed() ) {
				CTabItem layoutTab = (CTabItem)_hashTabs.remove("Layout");
				layoutTab.setControl(null);
				layoutTab.dispose();
				_layoutComposite.dispose();
				_layoutComposite = null;		
				_vectorLayoutPropertyEditors.clear();
			}
		}else{
			// This widget has a layout.  Add a layout tab if one doesn't exist
			CTabItem layoutTab = (CTabItem)_hashTabs.get("Layout");
			if(layoutTab == null){
				// The layout tab doe snot exist, so create it
				int layoutTabIndex = _tabFolder.getItemCount();
				CTabItem eventsTab = (CTabItem)_hashTabs.get(Messages.NL_Events);
				if(eventsTab != null){
					layoutTabIndex = _tabFolder.indexOf(eventsTab);
				}
				layoutTab = createCategoryTab( "Layout", layoutTabIndex );  // Always try to put this tab before the Events tab
				_hashTabs.put( "Layout", layoutTab );
				_layoutComposite = (PropertyTabComposite)layoutTab.getControl();
				
				for( int i = 0; i < properties.length; ++i ) {
					String strType = properties[ i ].getType();
					createPropertyEditor(_layoutComposite, properties[i], _vectorLayoutPropertyEditors);
				}
			}				
		}
	}
	
	public void recreateControlsForLayout( WidgetPart widgetPart, WidgetDescriptorRegistry registry ) {
		createControlsForLayout( widgetPart, registry );
		_composite.pack();
	}

	/**
	 * 
	 */
	protected PropertyEditorAbstract createEditorBoolean( Composite compositeParent, WidgetPropertyDescriptor descriptor ) {

		PropertyEditorBoolean editor = new PropertyEditorBoolean( this, descriptor, descriptor.getLabel() );
		editor.createControl( compositeParent );
		return editor;
	}

	/**
	 * 
	 */

	protected PropertyEditorAbstract createEditorChoice( Composite compositeParent, WidgetPropertyDescriptor descriptor ) {

		Label label = new Label( compositeParent, SWT.NULL );
		label.setBackground( compositeParent.getBackground() );

		PropertyEditorChoice editor = new PropertyEditorChoice( this, descriptor );
		editor.createControl( compositeParent );

		label.setText( descriptor.getLabel() + ":");

		GridData gridData = new GridData();
		label.setLayoutData( gridData );

		return editor;
	}

	/**
	 * 
	 */
	protected PropertyEditorAbstract createEditorColor( Composite compositeParent, WidgetPropertyDescriptor descriptor ) {

		Label label = new Label( compositeParent, SWT.NULL );
		label.setBackground( compositeParent.getBackground() );

		PropertyEditorColor editor = new PropertyEditorColor( this, descriptor );
		editor.createControl( compositeParent );

		label.setText( descriptor.getLabel() + ":" );

		GridData gridData = new GridData();
		label.setLayoutData( gridData );

		return editor;
	}

	/**
	 * 
	 */
	protected PropertyEditorAbstract createEditorInteger( Composite compositeParent, WidgetPropertyDescriptor descriptor ) {
		Label label = new Label( compositeParent, SWT.NULL );
		label.setBackground( compositeParent.getBackground() );
		
		PropertyEditorInteger editor = new PropertyEditorInteger( this, descriptor );
		editor.createControl( compositeParent );

		String strLabel = descriptor.getLabel();
		if( strLabel != null )
			label.setText( strLabel + ":" );

		GridData gridData = new GridData();
		label.setLayoutData( gridData );

		return editor;
	}

	/**
	 * No longer used.
	 */
	protected PropertyEditorAbstract createEditorPosition( Composite compositeParent, WidgetPropertyDescriptor descriptor ) {
		PropertyEditorPosition editor = new PropertyEditorPosition( this, descriptor );
		editor.createControl( compositeParent );

		return editor;
	}

	/**
	 * 
	 */
	protected PropertyEditorAbstract createEditorString( Composite compositeParent, WidgetPropertyDescriptor descriptor ) {
		Label label = new Label( compositeParent, SWT.NULL );
		label.setBackground( compositeParent.getBackground() );

		PropertyEditorString editor = new PropertyEditorString( this, descriptor );
		editor.createControl( compositeParent );

		String strLabel = descriptor.getLabel();
		if( strLabel != null )
			label.setText( descriptor.getLabel() + ":" );

		GridData gridData = new GridData();
		label.setLayoutData( gridData );

		return editor;
	}

	/**
	 * Creates an editor that has a button which displays a dialog where a person can add/remove items
	 * from a list.
	 */
	protected PropertyEditorAbstract createEditorStringArray( Composite compositeParent, WidgetPropertyDescriptor descriptor ) {
		Label label = new Label( compositeParent, SWT.NULL );
		label.setBackground( compositeParent.getBackground() );

		PropertyEditorStringArray editor = new PropertyEditorStringArray( this, descriptor );
		editor.createControl( compositeParent );

		String strLabel = descriptor.getLabel();
		if( strLabel != null )
			label.setText( descriptor.getLabel() + ":" );

		GridData gridData = new GridData();
		label.setLayoutData( gridData );

		return editor;
	}

	/**
	 * Called by the property editors to obtain the current property value. 
	 */
	public WidgetPropertyValue getPropertyValue( String strPropertyID, String strPropertyType ){
		return _propertySheet.getEditorAdapter().getPropertyValue( _widgetPart, strPropertyID, strPropertyType );
	}
	
	/**
	 * Called by the property editors to obtain the current property value. 
	 */
	public WidgetPropertyValue getLayoutPropertyValue( String strPropertyID, String strPropertyType ){
		return _propertySheet.getEditorAdapter().getLayoutPropertyValue( _widgetPart, strPropertyID, strPropertyType );
	}
	
	/**
	 * Returns the currently shown page name.
	 * This is used when a different widget is selected.  This class is disposed
	 * and a new one is created.  We show the same page with the new widget as the user was viewing with the previous widget.
	 */
	public String getTabFolderPageName() {
		return ( _tabFolder != null && _tabFolder.isDisposed() == false ) ? _tabFolder.getSelection().getText() : "";
	}

	/**
	 * Declared in MouseListener.  Does nothing.
	 */
	public void mouseDoubleClick( MouseEvent event ) {
		Item[] items = _table.getSelection();

		// Obtain the first selected item
		//-------------------------------
		TableItem tableItemSelected = (TableItem)items[ 0 ];

		// Do not edit if no item selected
		//--------------------------------
		if( tableItemSelected == null || tableItemSelected.isDisposed() )
			return;

		
		// Map the mouse posiiton to a column number
		//------------------------------------------
		int iColumn = -1;

		for( int i = 0; i < 2; ++i ) {
			if( tableItemSelected.getBounds( i ).contains( event.x, event.y ) == true ) {
				iColumn = i;
				break;
			}
		}

		if( iColumn == 0 ) {
			String functionName = tableItemSelected.getText( 1 );
			
			// Open the New Event Function dialog if there is no event function associated with the selected Event handler.
			if ( functionName == null || functionName.length() == 0 ) {
				_propertySheet.createNewEventHandlingFunction( tableItemSelected );
			}
			// otherwise, jump into the event function in source view.
			else {
				IEvPropertySheetPageAdapter adapter = _propertySheet.getEditorAdapter();
	
				int[] range = adapter.getEditorProvider().getFunctionNameRange( functionName );
				adapter.selectAndRevealRange( range[0], range[1] );
			}
		}
	}

	/**
	 * Declared in MouseListener.  Activates the cell editor.
	 */
	public void mouseDown( MouseEvent event ) {
		if( event.button != 1 )
			return;

		Item[] items = _table.getSelection();

		// Do not edit if more than one row is selected
		//---------------------------------------------
		if( items.length != 1 )
			return;

		// Obtain the first selected item
		//-------------------------------
		TableItem tableItemSelected = (TableItem)items[ 0 ];

		// Do not edit if no item selected
		//--------------------------------
		if( tableItemSelected == null || tableItemSelected.isDisposed() )
			return;

		// Map the mouse posiiton to a column number
		//------------------------------------------
		int iColumn = -1;

		for( int i = 0; i < 2; ++i ) {
			if( tableItemSelected.getBounds( i ).contains( event.x, event.y ) == true ) {
				iColumn = i;
				break;
			}
		}

		if( iColumn == 1 )
			activateCellEditor( tableItemSelected );
	}

	/**
	 * Declared in MouseListener.  Does nothing.
	 */
	public void mouseUp( MouseEvent e ) {
	}

	/**
	 * Notifies the property sheet that a property value of the selected widget has changed.
	 */
	public void propertyValueChanged( PropertyEditorAbstract propertyEditor, WidgetPropertyDescriptor descriptor, WidgetPropertyValue propertyValueOld, WidgetPropertyValue propertyValueNew ) {
		if( _propertySheet == null )
			return;
		
		_propertyChangedFromPropertiesView = true;
		_propertySheet.propertyValueChanged( descriptor, propertyValueOld, propertyValueNew );

		// Update the editor with the current value
		//-----------------------------------------
		propertyEditor.initialize();
	}

	/**
	 * This is used in the scenario where a selection a widget has occurred.  The
	 * widget type is the same as the current widget type, but the widget is different.
	 * The same property editors are used, but they are updated with the values of
	 * the newly selected widget. 
	 */
	public void reloadValues( WidgetPart widgetPart, String[] straFunctionNames ) {
		_widgetPart = widgetPart;
		
		// Properties
		//-----------
		for( int i = 0; i < _vectorPropertyEditors.size(); ++i ) {
			PropertyEditorAbstract editor = (PropertyEditorAbstract)_vectorPropertyEditors.get( i );
			editor.initialize();
		}
		
		// Layout Properties
		//-----------
		for( int i = 0; i < _vectorLayoutPropertyEditors.size(); ++i ) {
			PropertyEditorAbstract editor = (PropertyEditorAbstract)_vectorLayoutPropertyEditors.get( i );
			editor.initialize();
		}

		// Events
		//-------
		if( _table == null )
			return;

		TableItem[] items = _table.getItems();
		for( int i = 0; i < items.length; ++i ) {
			WidgetEventDescriptor descriptor = (WidgetEventDescriptor)items[ i ].getData( "descriptor" );
			WidgetPropertyValue propertyValue = _propertySheet.getEditorAdapter().getEventValue( widgetPart, descriptor.getID() );
			
			String strValue = null;
			
			if( propertyValue != null ){
				ArrayList listValues = propertyValue.getValues();
				if( listValues != null && listValues.size() > 0 )
					strValue = (String)listValues.get( 0 );
			}
			
			items[ i ].setText( 1, strValue != null ? strValue : "" );			
			items[ i ].setData( "propertyvalue", propertyValue );
		}
	}

	/**
	 * Changes the displayed tab folder page to the name
	 */
	public void setTabFolderPageName( String name ) {
		if( _tabFolder != null && !_tabFolder.isDisposed()){
			int i = 0;
			for ( i = 0; i < _tabFolder.getItemCount(); i ++ ) {
				if ( name.equals( _tabFolder.getItem(i).getText() ) ) {
					_tabFolder.setSelection( i );
					break;
				}
			}
			if ( i > 0 && i == _tabFolder.getItemCount() ) {
				_tabFolder.setSelection( 0 );
			}
		}			
	}

	/**
	 * Translates a widget property category into a translated name for presenting as a label.
	 */
	protected String translateCategoryName( String strCategoryName ){
		if( strCategoryName == null )
			return null;
		
		if( strCategoryName.equals( "Accessibility" ) == true )
			return Messages.NL_Accessibility;

		if( strCategoryName.equals( "Appearance" ) == true )
			return Messages.NL_Appearance;

		if( strCategoryName.equals( "Bidi" ) == true )
			return Messages.NL_Bidi;
		
		if( strCategoryName.equals( "Border" ) == true )
			return Messages.NL_Border;

		if( strCategoryName.equals( "Position" ) == true )
			return Messages.NL_Position;

		if( strCategoryName.equals( "Spacing" ) == true )
			return Messages.NL_Spacing;

		if( strCategoryName.equals( "Layout" ) == true )
			return Messages.NL_Layout;
		
		if( strCategoryName.equals( "General" ) == true )
			return Messages.NL_General;

		return strCategoryName;
	}

	/**
	 * Declared in SelectionListener.
	 * Listens to an 'enter' key press in order to activate the drop down cell editor.
	 */
	public void widgetDefaultSelected( SelectionEvent event ) {
		if( event.getSource() == _table ) {
			int iItem = _table.getSelectionIndex();

			if( iItem >= 0 )
				activateCellEditor( _table.getItem( iItem ) );
		}
	}

	/**
	 * Declared in SelectionListener.
	 * Listens for a drop down cell editor selection
	 */
	public void widgetSelected( SelectionEvent event ) {
		if( event.widget instanceof Combo ) {
			Combo combo = (Combo)event.getSource();
			String strValueOriginal = (String)combo.getData( "originalValue" );
			String strValueNew = combo.getText();

			if( strValueOriginal.equals( strValueNew ) == false ) {
				WidgetEventDescriptor descriptor = (WidgetEventDescriptor)combo.getData( "descriptor" );
				_propertySheet.eventValueChanged( descriptor, strValueOriginal, strValueNew, false );
			}
		}

		// Notify the property sheet that the
		// tab folder page has changed
		//-----------------------------------
		else if( event.widget == _tabFolder ) {
			_propertySheet.tabFolderPageNameChanged();
		}

		// Create new event handling function button
		//------------------------------------------
		else if( event.widget instanceof Button ){
			TableItem tableItem = null;
			
			TableItem[] tableItems = _table.getSelection();
			if( tableItems.length != 0 )
				tableItem = tableItems[0];
	
			_propertySheet.createNewEventHandlingFunction( tableItem );
		}
		
		// Show the "+" button in the selected row in the Event table.
		else if( event.widget instanceof Table ) {
			Control oldEditor = _tableAddButtonEditor.getEditor();
			if (oldEditor != null) oldEditor.dispose();
	
			// Identify the selected row
			TableItem item = (TableItem)event.item;
			if (item == null) return;
	
			// The control that will be the editor must be a child of the Table
			Button button = new Button(_table, SWT.PUSH);
			button.setAlignment( SWT.CENTER );
			button.setImage( Activator.getImage( EvConstants.ICON_PROPERTY_EVENT_PLUS ) );
			button.setToolTipText( Tooltips.NL_Create_a_new_event_handling_function );
			button.addSelectionListener( this );

			_tableAddButtonEditor.setEditor(button, item, EVENT_TABLE_EDITABLE_COLUMN);
		}
	}
}
