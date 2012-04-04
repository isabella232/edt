/*******************************************************************************
 * Copyright © 2009, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.deployment.rui.internal.editor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.deployment.core.IDeploymentConstants;
import org.eclipse.edt.ide.deployment.internal.Logger;
import org.eclipse.edt.ide.deployment.rui.internal.HelpContextIDs;
import org.eclipse.edt.ide.deployment.rui.internal.nls.Messages;
import org.eclipse.edt.ide.deployment.rui.internal.preferences.HandlerLocalesList;
import org.eclipse.edt.ide.deployment.rui.internal.util.DeployLocale;
import org.eclipse.edt.ide.deployment.rui.internal.util.Utils;
import org.eclipse.edt.ide.rui.internal.nls.ILocalesListViewer;
import org.eclipse.edt.ide.rui.internal.nls.Locale;
import org.eclipse.edt.ide.rui.internal.nls.LocaleUtility;
import org.eclipse.edt.ide.ui.internal.deployment.Deployment;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentFactory;
import org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot;
import org.eclipse.edt.ide.ui.internal.deployment.Parameter;
import org.eclipse.edt.ide.ui.internal.deployment.Parameters;
import org.eclipse.edt.ide.ui.internal.deployment.RUIApplication;
import org.eclipse.edt.ide.ui.internal.deployment.RUIHandler;
import org.eclipse.edt.ide.ui.internal.deployment.ui.DeploymentUtilities;
import org.eclipse.edt.ide.ui.internal.deployment.ui.EGLDDBaseDetailPage;
import org.eclipse.edt.ide.ui.internal.deployment.ui.EGLDDBaseFormPage;
import org.eclipse.edt.ide.ui.internal.deployment.ui.EGLDDRootHelper;
import org.eclipse.edt.ide.ui.internal.deployment.ui.EGLDeploymentDescriptorEditor;
import org.eclipse.edt.ide.ui.internal.deployment.ui.SOAMessages;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;


public class RUIFormPage extends EGLDDBaseFormPage implements ILocalesListViewer
{
	private CheckboxTableViewer fHandlerTableViewer;
	private CheckboxTableViewer fLocalesTableViewer;
	private Button fCheckbox;
	private Button fBtnOpen;
	private Button fBtnEnable;
	private Button fBtnDisable;
	private Link fLocalesLink;
	private RUIApplication fApplication;
	private List cachedModelLocales;
	private HandlerLocalesList localesList;

	private static final int COLINDEX_HANDLER = 0;
	private static final int COLINDEX_HTML = 1;
//	private static final int COLINDEX_DYAMIC = 0;
	private static final String[] HANDLER_TABLE_COLUMN_PROPERTIES = { "COL_HANDLER", "COL_HTML" }; //$NON-NLS-1$ //$NON-NLS-2$
	
	private static final int COLINDEX_LOCALE_DESC = 0;
	private static final int COLINDEX_LOCALE_CODE = 1;
	private static final int COLINDEX_LOCALE_RT = 2;
	private static final String[] LOCALE_TABLE_COLUMN_PROPERTIES = { "COL_HANDLER", "COL_HTML" }; //$NON-NLS-1$ //$NON-NLS-2$
	
	private static final String VALIDATION_KEY_LOCALES = "locales";
	
	public RUIFormPage( FormEditor editor, String id, String title )
	{
		super( editor, id, title );
		
		Deployment deployment = getModelRoot().getDeployment();
		if ( deployment != null )
		{
			fApplication = deployment.getRuiapplication();
		}
	}
	
	public void setActive( boolean active )
	{
		super.setActive( active );
		if ( active )
		{
			fHandlerTableViewer.refresh();
			fLocalesTableViewer.refresh();
		}
	}
	
	private RUIApplication getApplication()
	{
		if ( fApplication == null )
		{
			// First time they make any changes on this page, we'll create the <RUIApplication/>
			fApplication = DeploymentFactory.eINSTANCE.createRUIApplication();
			fApplication.setDeployAllHandlers(true);

			// Add locales currently selected in the table.
			Object[] elements = fLocalesTableViewer.getCheckedElements();
			if ( elements.length != 0 )
			{
				String localesString = Utils.getLocalesString( elements );
				if ( localesString.length() > 0 )
				{
					Parameters parms = DeploymentFactory.eINSTANCE.createParameters();
					fApplication.setParameters( parms );
					EGLDDRootHelper.addOrUpdateParameter( parms, IDeploymentConstants.PARAMETER_LOCALES, localesString );
				}
			}
			
			getModelRoot().getDeployment().setRuiapplication( fApplication );
		}
		
		return fApplication;
	}
	
	protected void createFormContent( IManagedForm managedForm )
	{
		super.createFormContent( managedForm );
		final ScrolledForm form = managedForm.getForm();
		form.setText( Messages.editor_title );
		managedForm.setInput( getModelRoot() );
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		form.getBody().setLayout( layout );
		
		FormToolkit toolkit = managedForm.getToolkit();
		
		Composite client = createNonExpandableSection( form, toolkit, Messages.editor_master_page_title, Messages.editor_master_page_desc, 2 );
		createRUIHandlerSection( toolkit, client );
		createLocaleSection( toolkit, client );
		
		init();
		
		PlatformUI.getWorkbench().getHelpSystem().setHelp( form.getBody(), getHelpID() );
	}
	
	private void init()
	{
		boolean selection;
		if (fApplication == null) {
			selection = true;
		} else {
			selection = fApplication.isDeployAllHandlers();
		}
		fCheckbox.setSelection(selection);
		fHandlerTableViewer.getTable().setEnabled(!selection);
		updateButtons();
		
		validateLocales();
		
		// On smaller displays, the locales section will not be visible. This
		// will cause the form to display a vertical scrollbar if needed.
		getManagedForm().reflow( true );
	}
	
	private static RUIHandler getMatchingHandlerFromModel( String impl, RUIApplication application )
	{
		if ( application == null )
		{
			return null;
		}
		
		EList handlers = application.getRuihandler();
		Iterator it = handlers.iterator();
		int size = handlers.size();
		for ( int i = 0; i < size; i++ )
		{
			RUIHandler next = (RUIHandler)it.next();
			if ( impl.equals( next.getImplementation() ) )
			{
				return next;
			}
		}
		return null;
	}
	
	private void createRUIHandlerSection( FormToolkit toolkit, Composite parent )
	{
		Section section = toolkit.createSection( parent, Section.TWISTIE | Section.EXPANDED );
		GridData gd = new GridData( GridData.FILL_BOTH );
		section.setText( Messages.rui_handlers_section_title );
		section.setLayoutData( gd );
		
		Composite separator = toolkit.createCompositeSeparator( section );
		gd = new GridData( GridData.FILL_BOTH );
		gd.heightHint = 3;
		separator.setLayoutData( gd );
		
		Composite client = toolkit.createComposite( section, SWT.WRAP );
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		client.setLayout( layout );
		
		Composite fieldComposite = toolkit.createComposite( client );
		int layoutColumn = 2;
		layout = new GridLayout( layoutColumn, false );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		fieldComposite.setLayout( layout );
		fieldComposite.setLayoutData( gd );
		
		Label label = toolkit.createLabel( fieldComposite, Messages.editor_detail_page_handlers_label );
		gd = new GridData();
		gd.horizontalSpan = layoutColumn;
		label.setLayoutData( gd );
		
		fCheckbox = toolkit.createButton( fieldComposite, Messages.rui_handlers_deploy_all_label, SWT.CHECK );
		fCheckbox.addSelectionListener( new SelectionAdapter() {
			public void widgetSelected( SelectionEvent event )
			{
				RUIApplication application = getApplication();
				boolean checked = fCheckbox.getSelection();
				application.setDeployAllHandlers( checked );
				fHandlerTableViewer.getTable().setEnabled( !checked );
				updateButtons();
				fHandlerTableViewer.refresh();
				
				if ( !checked )
				{
					// Add each handler to the model that wasn't already there.
					// Default it to enabled when 0 handlers are in the model,
					// otherwise false.
					TableItem[] items = fHandlerTableViewer.getTable().getItems();
					for ( int i = 0; i < items.length; i++ )
					{
						Object data = items[ i ].getData();
						if ( data instanceof RUIHandlerRowItem )
						{
							RUIHandlerRowItem item = (RUIHandlerRowItem)data;
							RUIHandler fromModel = getMatchingHandlerFromModel( item.handler.getImplementation(), application );
							if ( fromModel == null )
							{
								application.getRuihandler().add( item.handler );
							}
						}
					}
				}
				validateLocales();
			}
		} );
		gd = new GridData();
		gd.verticalIndent = 10;
		gd.horizontalSpan = layoutColumn;
		fCheckbox.setLayoutData( gd );
		
		Table t = createRUIHandlerTableControl( toolkit, fieldComposite, true );
		fHandlerTableViewer = new CheckboxTableViewer( t ) {
			protected void doUpdateItem( Widget widget, Object element, boolean fullMap )
			{
				super.doUpdateItem( widget, element, fullMap );
				((TableItem)widget).setChecked( fCheckbox.getSelection() || ((RUIHandlerRowItem)element).handler.isEnableGeneration() );
			}
			protected void preservingSelection( Runnable updateCode )
			{
		        updateCode.run();
		    }
		};
		CellEditor[] cellEditors = new CellEditor[ HANDLER_TABLE_COLUMN_PROPERTIES.length ];
		cellEditors[ COLINDEX_HTML ] = new TextCellEditor( t );
		fHandlerTableViewer.setCellEditors( cellEditors );
		fHandlerTableViewer.setCellModifier( new RUIHandlerCellModifier() );
		fHandlerTableViewer.setColumnProperties( HANDLER_TABLE_COLUMN_PROPERTIES );
		fHandlerTableViewer.setContentProvider( new RUIHandlerContentProvider( getEditorProject() ) );
		fHandlerTableViewer.setLabelProvider( new RUIHandlerLabelProvider() );
		fHandlerTableViewer.addCheckStateListener( new ICheckStateListener() {
			public void checkStateChanged( CheckStateChangedEvent event )
			{
				Object element = event.getElement();
				if ( element instanceof RUIHandlerRowItem )
				{
					RUIHandlerRowItem item = (RUIHandlerRowItem)element;
					if ( getMatchingHandlerFromModel( item.handler.getImplementation(), fApplication ) == null )
					{
						// This row was not previously in the model, so add it.
						getApplication().getRuihandler().add( item.handler );
					}
					item.handler.setEnableGeneration( event.getChecked() );
					validateLocales();
				}
			}
		} );
		fHandlerTableViewer.addSelectionChangedListener( new ISelectionChangedListener() {
			public void selectionChanged( SelectionChangedEvent event )
			{
				updateButtons();
			}
		} );
		fHandlerTableViewer.setSorter( new ViewerSorter() );
		t.addKeyListener( new KeyAdapter() {
			public void keyReleased( KeyEvent e )
			{
				if ( e.keyCode == SWT.F3 )
				{
					HandleOpenPressed();
				}
			}
		} );
		
		t.addSelectionListener(new SelectionAdapter(){
			public void widgetDefaultSelected(SelectionEvent e) {
				HandleOpenPressed();
			}	
		});
		fHandlerTableViewer.setInput( getModelRoot() );
		
		Composite buttonComposite = toolkit.createComposite( fieldComposite );
		layout = new GridLayout( 1, true );
		gd = new GridData( GridData.VERTICAL_ALIGN_BEGINNING );
		buttonComposite.setLayout( layout );
		buttonComposite.setLayoutData( gd );
		
		fBtnEnable = toolkit.createButton( buttonComposite, Messages.enable_all_btn_label, SWT.PUSH );
		gd = new GridData( GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_BEGINNING );
		fBtnEnable.setLayoutData( gd );
		fBtnEnable.addSelectionListener( new SelectionAdapter() {
			public void widgetSelected( SelectionEvent event )
			{
				HandleEnableAllPressed();
			}
		} );
		
		fBtnDisable = toolkit.createButton( buttonComposite, Messages.disable_all_btn_label, SWT.PUSH );
		gd = new GridData( GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_BEGINNING );
		fBtnDisable.setLayoutData( gd );
		fBtnDisable.addSelectionListener( new SelectionAdapter() {
			public void widgetSelected( SelectionEvent event )
			{
				HandleDisableAllPressed();
			}
		} );
		
		fBtnOpen = toolkit.createButton( buttonComposite, SOAMessages.OpenLabel, SWT.PUSH );
		gd = new GridData( GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_BEGINNING );
		fBtnOpen.setLayoutData( gd );
		fBtnOpen.setEnabled( false );
		fBtnOpen.addSelectionListener( new SelectionAdapter() {
			public void widgetSelected( SelectionEvent e )
			{
				HandleOpenPressed();
			}
		});

		createSpacer(toolkit, fieldComposite, layoutColumn);
		toolkit.paintBordersFor(fieldComposite);
		section.setClient(client);
	}
	
	private void createLocaleSection( FormToolkit toolkit, Composite parent )
	{
		Section section = toolkit.createSection( parent, Section.TWISTIE | Section.EXPANDED );
		GridData gd = new GridData( GridData.FILL_BOTH );
		section.setText( Messages.locale_section_title );
		section.setLayoutData( gd );
		
		Composite separator = toolkit.createCompositeSeparator( section );
		gd = new GridData( GridData.FILL_BOTH );
		gd.heightHint = 3;
		separator.setLayoutData( gd );
		
		Composite client = toolkit.createComposite( section, SWT.WRAP );
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		client.setLayout( layout );
		
		Composite fieldComposite = toolkit.createComposite( client );
		int layoutColumn = 1;
		layout = new GridLayout( layoutColumn, false );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		fieldComposite.setLayout( layout );
		fieldComposite.setLayoutData( gd );
		
		fLocalesLink = new Link( fieldComposite, SWT.NONE );
		fLocalesLink.setText( NLS.bind( Messages.editor_detail_page_locales_label, SOAMessages.ConfigureLinkLabel ) );
		fLocalesLink.addSelectionListener( new SelectionAdapter() {
			public void widgetSelected( SelectionEvent e )
			{
				run();
			}
			public void widgetDefaultSelected( SelectionEvent e )
			{
				run();
			}
			private void run()
			{
				String id = "org.eclipse.edt.ide.rui.ruiPreferences"; //$NON-NLS-1$
				PreferencesUtil.createPreferenceDialogOn( null, id, null, null ).open();
			}
		} );
		fLocalesLink.setBackground( toolkit.getColors().getBackground() );
		gd = new GridData();
		gd.horizontalSpan = layoutColumn;
		fLocalesLink.setLayoutData( gd );
		
		Table t = createLocalesTable( toolkit, fieldComposite, layoutColumn );
		fLocalesTableViewer = new CheckboxTableViewer( t ) {
			protected void doUpdateItem( Widget widget, Object element, boolean fullMap )
			{
				super.doUpdateItem( widget, element, fullMap );
				((TableItem)widget).setChecked( checkLocale( (DeployLocale)element ) );
			}
			protected void preservingSelection( Runnable updateCode )
			{
		        updateCode.run();
		    }
		};
		fLocalesTableViewer.setColumnProperties( LOCALE_TABLE_COLUMN_PROPERTIES );
		
		// Must be created before passed into the content provider.
		localesList = new HandlerLocalesList();
		localesList.buildLocalesList();
		localesList.addChangeListener( this );
		
		fLocalesTableViewer.setContentProvider( new LocalesContentProvider( localesList ) );
		fLocalesTableViewer.setLabelProvider( new LocalesLabelProvider() );
		fLocalesTableViewer.addCheckStateListener( new ICheckStateListener() {
			public void checkStateChanged( CheckStateChangedEvent event )
			{
				// intentionally don't call the getter - if we haven't started caching, don't start now.
				if ( cachedModelLocales != null )
				{
					if ( event.getChecked() )
					{
						cachedModelLocales.add( event.getElement() );
					}
					else
					{
						cachedModelLocales.remove( event.getElement() );
					}
				}
				updateLocalesParameterInModel();
				validateLocales();
			}
		});
		fLocalesTableViewer.setSorter(new ViewerSorter());
		fLocalesTableViewer.setInput(""); //$NON-NLS-1$

		createSpacer(toolkit, fieldComposite, layoutColumn);
		toolkit.paintBordersFor(fieldComposite);
		section.setClient(client);
	}

	private void validateLocales() {
		RUIApplication app = getModelRoot().getDeployment().getRuiapplication();
		if ( app != null )
		{
			// No locale selected is only an error if some handlers are enabled.
			if ( app.isDeployAllHandlers() || (app.getRuihandler().size() != 0 && !allHandlersAreDisabled( app )) )
			{
				String locales = EGLDDRootHelper.getParameterValue( app.getParameters(), IDeploymentConstants.PARAMETER_LOCALES );
				if ( locales == null || locales.length() == 0 )
				{
					getManagedForm().getMessageManager().addMessage( VALIDATION_KEY_LOCALES, Messages.no_locale_selected_error, null, IMessageProvider.ERROR, fLocalesLink );
				}
				else
				{
					getManagedForm().getMessageManager().removeMessage( VALIDATION_KEY_LOCALES, fLocalesLink );
				}
			}
			else
			{
				getManagedForm().getMessageManager().removeMessage( VALIDATION_KEY_LOCALES, fLocalesLink );
			}
		}
	}
	
	private boolean allHandlersAreDisabled( RUIApplication app )
	{
		EList list = app.getRuihandler();
		for ( Iterator it = list.iterator(); it.hasNext(); )
		{
			if ( ((RUIHandler)it.next()).isEnableGeneration() )
			{
				return false;
			}
		}
		return true;
	}
	
	public static Table createRUIHandlerTableControl( FormToolkit toolkit, Composite parent, boolean checked )
	{
		int style = SWT.MULTI | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER;
		if ( checked )
		{
			style |= SWT.CHECK;
		}
		Table t = toolkit.createTable( parent, style );
		t.setHeaderVisible( true );
		t.setLinesVisible( true );
		
		GridData gd = new GridData( GridData.FILL_BOTH );
		gd.heightHint = 160;
		gd.widthHint = 100;
		t.setLayoutData( gd );
		toolkit.paintBordersFor( parent );
		
		TableLayout tableLayout = new TableLayout();
		
		int maxWidth = EGLDDBaseFormPage.DEFAULT_COLUMN_WIDTH;
		maxWidth = EGLDDBaseFormPage.createTableColumn( t, tableLayout, maxWidth, Messages.rui_handlers_table_handler_column_label, COLINDEX_HANDLER );
		maxWidth = EGLDDBaseFormPage.createTableColumn( t, tableLayout, maxWidth, Messages.rui_handlers_table_html_column_label, COLINDEX_HTML );
		t.setLayout( tableLayout );
		
		return t;
	}

	private void updateButtons() {
		if (fCheckbox.getSelection()) {
			fBtnDisable.setEnabled(false);
			fBtnEnable.setEnabled(false);
			fBtnOpen.setEnabled(false);
		}
		else
		{
			fBtnDisable.setEnabled(true);
			fBtnEnable.setEnabled(true);
			fBtnOpen.setEnabled(fHandlerTableViewer.getTable().getSelectionCount() != 0);
		}
	}

	protected void HandleOpenPressed()
	{
		IStructuredSelection ssel = (IStructuredSelection)fHandlerTableViewer.getSelection();
		if ( ssel.size() > 0 )
		{
			EGLDeploymentDescriptorEditor editor = (EGLDeploymentDescriptorEditor)getEditor();
			for ( Iterator it = ssel.iterator(); it.hasNext(); )
			{
				Object obj = it.next();
				if ( obj instanceof RUIHandlerRowItem )
				{
					EGLDDBaseDetailPage.try2OpenPartInEGLEditor( editor, ((RUIHandlerRowItem)obj).handler.getImplementation(), "org.eclipse.edt.ide.rui.visualeditor.EvEditor" ); //$NON-NLS-1$
				}
			}
		}
	}
	
	
	protected void HandleEnableAllPressed()
	{
		((CheckboxTableViewer)fHandlerTableViewer).setAllChecked( true );
		
		// setAllChecked doesn't fire notifications, so we have to manually update the model.
		setAllHandlersChecked( true );
		validateLocales();
	}
	
	protected void HandleDisableAllPressed()
	{
		((CheckboxTableViewer)fHandlerTableViewer).setAllChecked( false );
		
		// setAllChecked doesn't fire notifications, so we have to manually update the model.
		setAllHandlersChecked( false );
		validateLocales();
	}
	
	private void setAllHandlersChecked( boolean state )
	{
		TableItem[] items = fHandlerTableViewer.getTable().getItems();
		int size = items.length;
		List<RUIHandler> addToModel = new ArrayList<RUIHandler>( size );
		for ( int i = 0; i < size; i++ )
		{
			Object data = items[ i ].getData();
			if ( data instanceof RUIHandlerRowItem )
			{
				RUIHandlerRowItem row = (RUIHandlerRowItem)data;
				row.handler.setEnableGeneration( state );
				if ( getMatchingHandlerFromModel( row.handler.getImplementation(), fApplication ) == null )
				{
					// Not in the model - add it below (not now, that forces
					// fApplication non-null and can slow down further processing).
					addToModel.add( row.handler );
				}
			}
		}
		
		size = addToModel.size();
		if ( size != 0 )
		{
			for ( int i = 0; i < size; i++ )
			{
				getApplication().getRuihandler().add( addToModel.get( i ) );
			}
		}
	}
	
	private Table createLocalesTable( FormToolkit toolkit, Composite parent, int horizontalSpan )
	{
		Table t = toolkit.createTable( parent, SWT.CHECK | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );
		t.setHeaderVisible( true );
		t.setLinesVisible( true );
		
		GridData gd = new GridData( GridData.FILL_BOTH );
		gd.heightHint = 160;
		gd.widthHint = 100;
		gd.horizontalSpan = horizontalSpan;
		t.setLayoutData( gd );
		toolkit.paintBordersFor( parent );
		
		TableLayout tableLayout = new TableLayout();
		
		int maxWidth = EGLDDBaseFormPage.DEFAULT_COLUMN_WIDTH;
		maxWidth = EGLDDBaseFormPage.createTableColumn( t, tableLayout, maxWidth, Messages.RUIDeployPreferencePage_4, COLINDEX_LOCALE_DESC );
		maxWidth = EGLDDBaseFormPage.createTableColumn( t, tableLayout, maxWidth, Messages.RUIDeployPreferencePage_5, COLINDEX_LOCALE_CODE );
		maxWidth = EGLDDBaseFormPage.createTableColumn( t, tableLayout, maxWidth, Messages.Globalization_7, COLINDEX_LOCALE_RT );
		t.setLayout( tableLayout );
		
		return t;
	}
	
	protected void createSpacer( FormToolkit toolkit, Composite parent, int span )
	{
		Label spacer = toolkit.createLabel( parent, "" ); //$NON-NLS-1$
		GridData gd = new GridData();
		gd.horizontalSpan = span;
		spacer.setLayoutData( gd );
	}
	
	private boolean checkLocale( DeployLocale locale )
	{
		if ( fApplication == null )
		{
			// When there's no RUIApplication in the model, check them based on preference defaults.
			return locale.isDefault();
		}
		else
		{
			List cached = getCachedModelLocales();
			int size = cached.size();
			for ( int i = 0; i < size; i++ )
			{
				DeployLocale next = (DeployLocale)cached.get( i );
				if ( locale.getCode().equals( next.getCode() )
						&& locale.getDescription().equals( next.getDescription() )
						&& locale.getRuntimeLocaleCode().equals( next.getRuntimeLocaleCode() ) )
				{
					return true;
				}
			}
		}
		return false;
	}
	
	private List getCachedModelLocales()
	{
		if ( cachedModelLocales == null )
		{
			cachedModelLocales = new ArrayList();
			
			Parameters parms = getApplication().getParameters();
			if ( parms != null )
			{
				String locales = EGLDDRootHelper.getParameterValue( parms, IDeploymentConstants.PARAMETER_LOCALES );
				String[] fields = locales.split( "," );
				if ( fields.length % 3 == 0 )
				{
					int length = fields.length / 3;
					for ( int i = 0; i < length; i++ )
					{
						int offset = i * 3;
						String code = fields[ offset ];
						String description = fields[ 1 + offset ];
						String runtimeLocaleCode = fields[ 2 + offset ];
						cachedModelLocales.add( new DeployLocale( code, description, runtimeLocaleCode ) );
					}
				}
			}
		}
		return cachedModelLocales;
	}
	
	private void updateLocalesParameterInModel()
	{
		RUIApplication application = getApplication();
		
		String value;
		Parameters parms = application.getParameters();
		Object[] checked = fLocalesTableViewer.getCheckedElements();
		if ( checked.length == 0 )
		{
			value = null;
		}
		else
		{
			StringBuffer buf = new StringBuffer( 100 );
			for ( int i = 0; i < checked.length; i++ )
			{
				DeployLocale locale = (DeployLocale)checked[ i ];
				if ( i != 0 )
				{
					buf.append( "," );
				}
				buf.append( locale.getCode() );
				buf.append( ',' );
				buf.append( locale.getDescription() );
				buf.append( ',' );
				buf.append( locale.getRuntimeLocaleCode() );
			}
			
			if ( parms == null )
			{
				parms = DeploymentFactory.eINSTANCE.createParameters();
				application.setParameters( parms );
			}
			value = buf.toString();
		}
		
		if ( parms != null )
		{
			EGLDDRootHelper.addOrUpdateParameter( parms, IDeploymentConstants.PARAMETER_LOCALES, value );
			
			// If it's now empty (from removing the locales), we can remove it from the model to clean up the XML.
			if ( parms.getParameter().size() == 0 )
			{
				application.setParameters( null );
			}
		}
	}
	
	public boolean selectReveal( Object object )
	{
		fHandlerTableViewer.setSelection( (ISelection)object, true );
		return super.selectReveal( object ) ;
	}
	
	public void addLocale( Locale locale )
	{
		fLocalesTableViewer.refresh();
	}

	public void removeLocale( Locale locale )
	{
		fLocalesTableViewer.refresh();
	}

	public void updateLocale( Locale locale )
	{
		fLocalesTableViewer.refresh();
	}
	
	public void clear()
	{
	}
	
	public void setFocus()
	{
		if ( fHandlerTableViewer != null && fHandlerTableViewer.getTable().isEnabled() )
		{
			fHandlerTableViewer.getTable().setFocus();
		}
		else if ( fCheckbox != null )
		{
			fCheckbox.setFocus();
		}
	}
	
	private static String getHTMLFileName( RUIHandler handler, String defaultValue )
	{
		Parameters parms = handler.getParameters();
		if ( parms != null )
		{
			for ( Iterator it = parms.getParameter().iterator(); it.hasNext(); )
			{
				Parameter p = (Parameter)it.next();
				if ( p.getName().equals( IDeploymentConstants.PARAMETER_HTML_FILE_NAME ) )
				{
					return p.getValue();
				}
			}
		}
		return defaultValue;
	}
	
	public void dispose()
	{
		if ( this.localesList != null )
		{
			this.localesList.dispose();
		}
	}
	
	private class RUIHandlerCellModifier implements ICellModifier
	{
		public boolean canModify( Object element, String property )
		{
			return property.equals( HANDLER_TABLE_COLUMN_PROPERTIES[ COLINDEX_HTML ] );
		}
		
		public Object getValue( Object element, String property )
		{
			if ( element instanceof RUIHandlerRowItem )
			{
				if ( property.equals( HANDLER_TABLE_COLUMN_PROPERTIES[ COLINDEX_HTML ] ) )
				{
					return getHTMLFileName( ((RUIHandlerRowItem)element).handler, null );
				}
			}
			return null;
		}
		
		public void modify( Object element, String property, Object value )
		{
			if ( element instanceof TableItem )
			{
				TableItem tableitem = (TableItem)element;
				Object tableitemdata = tableitem.getData();
				if ( tableitemdata instanceof RUIHandlerRowItem )
				{
					if ( property.equals( HANDLER_TABLE_COLUMN_PROPERTIES[ COLINDEX_HTML ] ) )
					{
						RUIHandler handler = ((RUIHandlerRowItem)tableitemdata).handler;
						String newValue = (String)value;
						String currentValue = getHTMLFileName( handler, null );
						
						if ( (currentValue == null || !currentValue.equals( newValue ))
								&& ResourcesPlugin.getWorkspace().validateName( newValue, IResource.FILE ) == Status.OK_STATUS )
						{
							Parameters parms = handler.getParameters();
							if ( parms == null )
							{
								parms = DeploymentFactory.eINSTANCE.createParameters();
								handler.setParameters( parms );
							}
							
							EGLDDRootHelper.addOrUpdateParameter( parms, IDeploymentConstants.PARAMETER_HTML_FILE_NAME, newValue );
							tableitem.setText( COLINDEX_HTML, newValue );
						}
					}
				}
			}
		}
	}
	
	public static class RUIHandlerContentProvider implements IStructuredContentProvider
	{
		private IEGLProject project;
		
		public RUIHandlerContentProvider( IProject project )
		{
			this.project = EGLCore.create( project );
		}
		
		public Object[] getElements( Object inputElement )
		{
			List children = new ArrayList();
			
			if ( inputElement instanceof EGLDeploymentRoot )
			{
				EGLDeploymentRoot root = (EGLDeploymentRoot)inputElement;
				try
				{
					Map map = DeploymentUtilities.getAllRUIHandlersInProject( project );
					int i = 0;
					RUIApplication application = root.getDeployment().getRuiapplication();
					DeploymentFactory factory = DeploymentFactory.eINSTANCE;
					for ( Iterator it = map.entrySet().iterator(); it.hasNext(); )
					{
						Map.Entry next = (Map.Entry)it.next();
						String impl = (String)next.getKey();
						
						// If the handler already exists in the model, use that instance. Otherwise use a new instance.
						RUIHandler handler = getMatchingHandlerFromModel( impl, application );
						if ( handler == null )
						{
							handler = factory.createRUIHandler();
							handler.setEnableGeneration( false );
							handler.setImplementation( impl );
							
							Parameters parms = factory.createParameters();
							EGLDDRootHelper.addOrUpdateParameter( parms, IDeploymentConstants.PARAMETER_HTML_FILE_NAME, (String)next.getValue() );
							handler.setParameters( parms );
						}
						
						RUIHandlerRowItem row = new RUIHandlerRowItem( handler, i );
						children.add( row );
						i++;
					}
				}
				catch ( EGLModelException e )
				{
					e.printStackTrace();
					Logger.logException( e );
				}
			}
			
			return children.toArray();
		}
		
		public void dispose() {}
		public void inputChanged( Viewer viewer, Object oldInput, Object newInput ) {}
	}
	
	public static class RUIHandlerLabelProvider extends LabelProvider implements ITableLabelProvider
	{		
		public Image getColumnImage( Object element, int columnIndex )
		{
			return null;
		}

		public String getColumnText( Object element, int columnIndex )
		{
			if ( element instanceof RUIHandlerRowItem )
			{
				switch ( columnIndex )
				{
					case COLINDEX_HANDLER:
						return ((RUIHandlerRowItem)element).handler.getImplementation();
					case COLINDEX_HTML:
						return getHTMLFileName( ((RUIHandlerRowItem)element).handler, "" ); //$NON-NLS-1$
				}
			}
			return ""; //$NON-NLS-1$
		}
		
		public String getText( Object element )
		{
			return getColumnText( element, COLINDEX_HANDLER );
		}
	}
	
	public static class RUIHandlerRowItem
	{
		int index;
		RUIHandler handler;
		
		public RUIHandlerRowItem( RUIHandler handler, int index )
		{
			this.handler = handler;
			this.index = index;
		}
		
		public boolean equals( Object obj )
		{
			if ( obj instanceof RUIHandlerRowItem )
			{
				RUIHandlerRowItem other = (RUIHandlerRowItem)obj;	
				return obj == this || (index == other.index && handler.equals( other.handler ));
			}
			return super.equals( obj );
		}
	}
	
	public static class LocalesContentProvider implements IStructuredContentProvider
	{
		private HandlerLocalesList list;
		public LocalesContentProvider( HandlerLocalesList list )
		{
			this.list = list;
		}
		public Object[] getElements( Object inputElement )
		{
			return list.getLocales().toArray();
		}
		
		public void dispose() {}
		public void inputChanged( Viewer viewer, Object oldInput, Object newInput ) {}
	}
	
	public static class LocalesLabelProvider extends LabelProvider implements ITableLabelProvider
	{
		public Image getColumnImage( Object element, int columnIndex )
		{
			return null;
		}
		
		public String getColumnText( Object element, int columnIndex )
		{
			if ( element instanceof DeployLocale )
			{
				DeployLocale locale = (DeployLocale)element;
				switch ( columnIndex )
				{
					case COLINDEX_LOCALE_CODE:
						return locale.getCode();
					case COLINDEX_LOCALE_DESC:
						return locale.getDescription();
					case COLINDEX_LOCALE_RT:
						return LocaleUtility.getRuntimeDescriptionForCode( locale.getRuntimeLocaleCode() );
				}
			}
			return ""; //$NON-NLS-1$
		}
		
		public String getText( Object element )
		{
			return getColumnText( element, COLINDEX_LOCALE_DESC );
		}
	}
	
	protected String getHelpID() {
		return HelpContextIDs.RUI_DD_EDITOR_MAIN_PAGE;
	}

	
}
