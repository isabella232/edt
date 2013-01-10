/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.debug.internal.ui.java;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.debug.core.IEGLDebugCoreConstants;
import org.eclipse.edt.debug.core.PreferenceUtil;
import org.eclipse.edt.debug.core.java.filters.FilterStepType;
import org.eclipse.edt.debug.core.java.filters.ITypeFilterCategory;
import org.eclipse.edt.debug.core.java.filters.TypeFilterUtil;
import org.eclipse.edt.ide.ui.internal.preferences.AbstractPreferencePage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferenceLinkArea;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

public class EGLJavaDebugPreferencePage extends AbstractPreferencePage implements IWorkbenchPreferencePage
{
	private static final String[] STEP_COMBO_CHOICES = { EGLJavaMessages.FilterStepIntoLabel, EGLJavaMessages.FilterStepReturnLabel };
	
	private Button enableFilters;
	private TableModel[] tableInput;
	private CheckboxTableViewer tableViewer;
	private List<Control> filterControls;
	
	@Override
	protected Control createContents( Composite parent )
	{
		GridData gd;
		
		filterControls = new ArrayList<Control>();
		
		Composite composite = createComposite( parent, 1 );
		
		Label l = new Label( composite, SWT.WRAP );
		l.setText( EGLJavaMessages.PreferencePageMessage );
		l.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
		
		PreferenceLinkArea link = new PreferenceLinkArea( composite, SWT.NONE, "org.eclipse.jdt.debug.ui.JavaDebugPreferencePage", //$NON-NLS-1$
				EGLJavaMessages.JavaPreferencePageLink, (IWorkbenchPreferenceContainer)getContainer(), null );
		link.getControl().setFont( composite.getFont() );
		
		// spacer
		new Label( composite, SWT.WRAP );
		
		Group group = createGroup( composite, 1 );
		group.setText( EGLJavaMessages.TypeFiltersGroupLabel );
		
		Composite groupComposite = createComposite( group, 1 );
		
		l = new Label( groupComposite, SWT.WRAP );
		l.setText( EGLJavaMessages.TypeFiltersDescription );
		l.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false, 1, 1 ) );
		
		link = new PreferenceLinkArea( groupComposite, SWT.NONE, "org.eclipse.jdt.debug.ui.JavaStepFilterPreferencePage", //$NON-NLS-1$
				EGLJavaMessages.JavaFilterPreferencePageLink, (IWorkbenchPreferenceContainer)getContainer(), null );
		link.getControl().setFont( composite.getFont() );
		
		// spacer
		new Label( groupComposite, SWT.WRAP );
		
		enableFilters = new Button( groupComposite, SWT.CHECK );
		enableFilters.setText( EGLJavaMessages.TypeFiltersEnableButtonLabel );
		enableFilters.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
		enableFilters.addSelectionListener( new SelectionAdapter() {
			@Override
			public void widgetSelected( SelectionEvent e )
			{
				initFilterEnablement();
			}
		} );
		
		tableViewer = createTableViewer( groupComposite );
		filterControls.add( tableViewer.getTable() );
		
		l = new Label( groupComposite, SWT.WRAP );
		l.setText( EGLJavaMessages.TypeFilterDescriptionLabel );
		l.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
		filterControls.add( l );
		
		final Text desc = new Text( groupComposite, SWT.WRAP | SWT.READ_ONLY | SWT.BORDER | SWT.MULTI | SWT.V_SCROLL );
		gd = new GridData( GridData.FILL_BOTH );
		gd.minimumHeight = convertHeightInCharsToPixels( 2 );
		desc.setLayoutData( gd );
		filterControls.add( desc );
		
		tableViewer.addSelectionChangedListener( new ISelectionChangedListener() {
			@Override
			public void selectionChanged( SelectionChangedEvent event )
			{
				String text = ""; //$NON-NLS-1$
				ISelection sel = event.getSelection();
				if ( sel instanceof StructuredSelection )
				{
					Object element = ((StructuredSelection)sel).getFirstElement();
					if ( element instanceof TableModel )
					{
						text = ((TableModel)element).category.getDescription();
					}
				}
				desc.setText( text );
			}
		} );
		
		initializeValues();
		initFilterEnablement();
		
		Dialog.applyDialogFont( parent );
		PlatformUI.getWorkbench().getHelpSystem().setHelp( parent, IEGLJavaUIConstants.HELP_ID_JAVA_PREF_PAGE );
		
		return composite;
	}
	
	private TableModel[] createTableInput()
	{
		ITypeFilterCategory[] categories = TypeFilterUtil.INSTANCE.getTypeFilterCategories();
		List<TableModel> items = new ArrayList<TableModel>( categories.length );
		
		for ( ITypeFilterCategory category : categories )
		{
			if ( category.isVisible() )
			{
				items.add( new TableModel( category ) );
			}
		}
		
		return items.toArray( new TableModel[ items.size() ] );
	}
	
	private CheckboxTableViewer createTableViewer( Composite parent )
	{
		// For TableColumnLayout to work, it must be applied to a composite that contains nothing but the table.
		Composite tableComp = new Composite( parent, SWT.NONE );
		tableComp.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
		
		CheckboxTableViewer viewer = CheckboxTableViewer.newCheckList( tableComp, SWT.SINGLE | SWT.BORDER );
		Table table = viewer.getTable();
		table.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
		table.setLinesVisible( true );
		table.setHeaderVisible( true );
		
		TableViewerColumn categoryColumn = new TableViewerColumn( viewer, SWT.NONE );
		categoryColumn.getColumn().setText( EGLJavaMessages.TypeFiltersCategoryColumn );
		categoryColumn.getColumn().setResizable( true );
		categoryColumn.getColumn().setMoveable( false );
		
		TableViewerColumn stepTypeColumn = new TableViewerColumn( viewer, SWT.NONE );
		stepTypeColumn.getColumn().setText( EGLJavaMessages.TypeFiltersBehaviorColumn );
		stepTypeColumn.getColumn().setResizable( true );
		stepTypeColumn.getColumn().setMoveable( false );
		stepTypeColumn.setEditingSupport( new EditingSupport( viewer ) );
		
		viewer.setLabelProvider( new LabelProvider() );
		viewer.setContentProvider( new ArrayContentProvider() );
		viewer.setCheckStateProvider( new CheckStateProvider() );
		viewer.addCheckStateListener( new CheckStateListener() );
		
		TableColumnLayout layout = new TableColumnLayout();
		tableComp.setLayout( layout );
		layout.setColumnData( categoryColumn.getColumn(), new ColumnWeightData( 50, true ) );
		layout.setColumnData( stepTypeColumn.getColumn(), new ColumnWeightData( 40, true ) );
		
		return viewer;
	}
	
	private void initFilterEnablement()
	{
		boolean state = enableFilters.getSelection();
		
		for ( Control c : filterControls )
		{
			c.setEnabled( state );
		}
	}
	
	@Override
	protected void initializeValues()
	{
		super.initializeValues();
		
		enableFilters.setSelection( PreferenceUtil.getBoolean( IEGLDebugCoreConstants.PREFERENCE_TYPE_FILTERS_ENABLED, true ) );
		tableInput = createTableInput();
		tableViewer.setInput( tableInput );
	}
	
	@Override
	protected void performDefaults()
	{
		super.performDefaults();
		enableFilters.setSelection( PreferenceUtil.getDefaultBoolean( IEGLDebugCoreConstants.PREFERENCE_TYPE_FILTERS_ENABLED, true ) );
		
		for ( TableModel m : tableInput )
		{
			m.applyDefault();
		}
		
		tableViewer.refresh();
		
		initFilterEnablement();
	}
	
	@Override
	public boolean performOk()
	{
		PreferenceUtil.setBoolean( IEGLDebugCoreConstants.PREFERENCE_TYPE_FILTERS_ENABLED, enableFilters.getSelection() );
		
		for ( TableModel m : tableInput )
		{
			m.save();
		}
		
		// For the next two preferences we create the strings based on all categories, not just the visible categories.
		
		// Comma-separated list of disabled category IDs.
		StringBuilder enablementBuf = new StringBuilder( 100 );
		
		// Comma-separated list of "categoryId=stepType"
		StringBuilder stepTypeBuf = new StringBuilder( 100 );
		
		for ( ITypeFilterCategory category : TypeFilterUtil.INSTANCE.getTypeFilterCategories() )
		{
			if ( enablementBuf.length() > 0 )
			{
				enablementBuf.append( ',' );
			}
			enablementBuf.append( category.getId() );
			enablementBuf.append( '=' );
			enablementBuf.append( Boolean.toString( category.isEnabled() ) );
			
			if ( stepTypeBuf.length() > 0 )
			{
				stepTypeBuf.append( ',' );
			}
			stepTypeBuf.append( category.getId() );
			stepTypeBuf.append( '=' );
			stepTypeBuf.append( category.getStepType( null ).toString() );
		}
		
		PreferenceUtil.setString( IEGLDebugCoreConstants.PREFERENCE_TYPE_FILTER_ENABLEMENT, enablementBuf.toString() );
		PreferenceUtil.setString( IEGLDebugCoreConstants.PREFERENCE_TYPE_FILTER_STEP_TYPES, stepTypeBuf.toString() );
		
		PreferenceUtil.savePreferences();
		
		return super.performOk();
	}
	
	private static class TableModel
	{
		final ITypeFilterCategory category;
		boolean enabled;
		FilterStepType type;
		
		public TableModel( ITypeFilterCategory category )
		{
			this.category = category;
			this.enabled = category.isEnabled();
			this.type = category.getStepType( null );
		}
		
		void save()
		{
			category.setEnabled( enabled );
			category.setStepType( type );
		}
		
		void applyDefault()
		{
			enabled = category.getDefaultEnablement();
			type = category.getDefaultStepType();
		}
	}
	
	private static class LabelProvider extends org.eclipse.jface.viewers.LabelProvider implements ITableLabelProvider
	{
		@Override
		public String getColumnText( Object element, int columnIndex )
		{
			TableModel model = (TableModel)element;
			switch ( columnIndex )
			{
				case 0:
					return model.category.getName();
				case 1:
					switch ( model.type )
					{
						case STEP_RETURN:
							return EGLJavaMessages.FilterStepReturnLabel;
							
						case STEP_INTO:
						default:
							return EGLJavaMessages.FilterStepIntoLabel;
					}
			}
			return ""; //$NON-NLS-1$
		}
		
		@Override
		public Image getColumnImage( Object element, int columnIndex )
		{
			return null;
		}
		
		@Override
		public String getText( Object element )
		{
			return getColumnText( element, 0 );
		}
	}
	
	private static class CheckStateProvider implements ICheckStateProvider
	{
		@Override
		public boolean isChecked( Object element )
		{
			return ((TableModel)element).enabled;
		}
		
		@Override
		public boolean isGrayed( Object element )
		{
			return false;
		}
	}
	
	private static class CheckStateListener implements ICheckStateListener
	{
		@Override
		public void checkStateChanged( CheckStateChangedEvent event )
		{
			TableModel model = (TableModel)event.getElement();
			model.enabled = event.getChecked();
		}
	}
	
	private static class EditingSupport extends org.eclipse.jface.viewers.EditingSupport
	{
		private ComboBoxCellEditor cellEditor;
		
		public EditingSupport( TableViewer viewer )
		{
			super( viewer );
			this.cellEditor = new ComboBoxCellEditor( viewer.getTable(), STEP_COMBO_CHOICES );
		}
		
		@Override
		protected CellEditor getCellEditor( Object element )
		{
			return cellEditor;
		}
		
		@Override
		protected boolean canEdit( Object element )
		{
			return true;
		}
		
		@Override
		protected Object getValue( Object element )
		{
			switch ( ((TableModel)element).type )
			{
				case STEP_INTO:
				default:
					return 0;
					
				case STEP_RETURN:
					return 1;
			}
		}
		
		@Override
		protected void setValue( Object element, Object value )
		{
			switch ( (Integer)value )
			{
				case 0:
				default:
					((TableModel)element).type = FilterStepType.STEP_INTO;
					break;
				
				case 1:
					((TableModel)element).type = FilterStepType.STEP_RETURN;
					break;
			}
			
			getViewer().update( element, null );
		}
	}
}
