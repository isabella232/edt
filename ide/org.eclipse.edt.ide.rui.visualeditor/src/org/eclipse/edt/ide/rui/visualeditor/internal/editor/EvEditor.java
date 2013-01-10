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
package org.eclipse.edt.ide.rui.visualeditor.internal.editor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.internal.EGLBasePlugin;
import org.eclipse.edt.ide.core.internal.model.document.EGLDocument;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.core.model.document.IEGLModelChangeListener;
import org.eclipse.edt.ide.rui.editor.IEditorSelectAndRevealer;
import org.eclipse.edt.ide.rui.server.EvEditorProvider;
import org.eclipse.edt.ide.rui.server.EvServer;
import org.eclipse.edt.ide.rui.server.EventValue;
import org.eclipse.edt.ide.rui.server.PropertyValue;
import org.eclipse.edt.ide.rui.utils.WorkingCopyGenerationResult;
import org.eclipse.edt.ide.rui.visualeditor.internal.nl.Messages;
import org.eclipse.edt.ide.rui.visualeditor.internal.palette.EvPaletteRoot;
import org.eclipse.edt.ide.rui.visualeditor.internal.preferences.EvPreferences;
import org.eclipse.edt.ide.rui.visualeditor.internal.properties.IEvPropertySheetPageAdapter;
import org.eclipse.edt.ide.rui.visualeditor.internal.properties.PropertyChange;
import org.eclipse.edt.ide.rui.visualeditor.internal.properties.PropertySheetPage;
import org.eclipse.edt.ide.rui.visualeditor.internal.util.BidiUtils;
import org.eclipse.edt.ide.rui.visualeditor.internal.util.BrowserManager;
import org.eclipse.edt.ide.rui.visualeditor.internal.util.EvWidgetNameDialog;
import org.eclipse.edt.ide.rui.visualeditor.internal.util.Mnemonics;
import org.eclipse.edt.ide.rui.visualeditor.internal.views.dataview.IPageDataViewPage;
import org.eclipse.edt.ide.rui.visualeditor.internal.views.dataview.PageDataViewPage;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.IWidgetDescriptorRegistryListener;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetDescriptor;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetDescriptorGroup;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetDescriptorRegistry;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetEventDescriptor;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPart;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPropertyDescriptor;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPropertyValue;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.gen.GenManager;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.gen.GenModel;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.gen.GenModelBuilder;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.layout.WidgetLayout;
import org.eclipse.edt.ide.rui.visualeditor.plugin.Activator;
import org.eclipse.edt.ide.ui.editor.IEGLEditor;
import org.eclipse.edt.ide.ui.internal.editor.EGLEditor;
import org.eclipse.edt.ide.ui.internal.editor.IEGLEditorWrapper;
import org.eclipse.edt.ide.ui.internal.editor.IEvEditor;
import org.eclipse.gef.ui.views.palette.PaletteView;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.text.undo.DocumentUndoManagerRegistry;
import org.eclipse.text.undo.IDocumentUndoManager;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.part.MultiPageEditorSite;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.IPropertySheetPage;


/**
 *
 */
public class EvEditor extends MultiPageEditorPart implements IEGLEditorWrapper, IEGLModelChangeListener, IEditorSelectAndRevealer, IEvPropertySheetPageAdapter, IPartListener, IPropertyChangeListener, IResourceChangeListener, IResourceDeltaVisitor, ISelectionChangedListener, IWidgetDescriptorRegistryListener, SelectionListener, IEvEditor, ITextEditor {

	protected boolean				_bRuiHandler				= true;
	protected boolean				_bTranslationTestMode		= false;
	protected boolean				_bOperationInProgress		= false;
	protected boolean				_bWebPageGenerationRequired	= true;
	protected EvEditorProvider		_editorProvider				= null;	
	protected EvEditorErrorUpdater	_errorUpdater				= null;
	protected int					_iPerformance				= EvConstants.PREFERENCE_PERFORMANCE_OPTIMIZE_SPEED;
	protected EvEditorOutlinePage	_outlinePage				= null;
	protected PageDataViewPage   	_eglPageDataViewPage		= null;
	protected EvDesignPage			_pageDesign					= null;
	protected EvPreviewPage			_pagePreview				= null;
	protected EGLEditor				_pageSource					= null;
	protected EvEditorUndoManager	_undoManager				= null;
	
	protected String				_widgetId					= null;
	public static int				_iRenderEngine				= EvPreferences.getInt( EvConstants.PREFERENCE_RENDERENGINE );
	/**
	 * Closes the editor
	 */
	public void close( final boolean bSave ) {
		Display display = getSite().getShell().getDisplay();
		display.asyncExec( new Runnable() {
			public void run() {
				getSite().getPage().closeEditor( EvEditor.this, bSave );
			}
		} );
	}
	
	public EvDesignPage getDesignPage(){
		return this._pageDesign;
	}

	/**
	 * Creates the browser for the design and preview pages.
	 * Handles browser sharing if the 'optimize for resources' user preference is set.
	 */
	public Browser createBrowser( Composite compositeParent ) {

		Browser browser = null;

		// Optimize for resources by sharing a browser between the design and preview pages
		//---------------------------------------------------------------------------------
		if( _iPerformance == EvConstants.PREFERENCE_PERFORMANCE_OPTIMIZE_RESOURCES ) {

			// Find an existing browser
			//-------------------------
			Browser browserDesign  = _pageDesign .getBrowser();
			Browser browserPreview = _pagePreview.getBrowser();

			browser = browserDesign != null ? browserDesign : browserPreview;

			// A browser exists, transfer the browser to the other page
			//---------------------------------------------------------
			if( browser != null ) {
				boolean bReparented = browser.setParent( compositeParent );

				// Reset the browser references.
				// After returning, the page requesting the browser
				// will restore its browser reference
				//-------------------------------------------------
				if( bReparented == true ) {
					_pageDesign .resetBrowserToNull();
					_pagePreview.resetBrowserToNull();
				}

				// If the re-parenting failed, create a new browser
				//-------------------------------------------------
				else
					browser = null;
			}
		}

		// Optimize for resources and browser does not yet exist, or
		// Optimize for resources and re-parenting failed, or
		// Optimize for speed
		//----------------------------------------------------------
		if( browser == null ) {
			browser = BrowserManager.getInstance().createBrowser(compositeParent);
		}

		return browser;
	}

	/**
	 * Declared in IEvPropertySheetAdapater.
	 * Called by the property sheet to create a new event handling function
	 */
	public void createEventHandlingFunction( String strFunctionName ) {
		doSourceOperationFunctionCreate( strFunctionName );
	}

	/**
	 * Creates the design page.
	 */
	protected void createPageDesign() {
		if( _pageDesign != null )
			return;
		
		_pageDesign = new EvDesignPage( this );

		try {
			addPage( 0, _pageDesign, getEditorInput() );
			setPageText( 0, Messages.NL_Design );
		}

		catch( PartInitException e ) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates the preview page.
	 */
	protected void createPagePreview() {
		if( _pagePreview != null )
			return;

		_pagePreview = new EvPreviewPage( this );

		try {
			addPage( 2, _pagePreview, getEditorInput() );
			setPageText( 2, Messages.NL_Preview );
		}

		catch( PartInitException e ) {
			e.printStackTrace();
		}
	}

	/**
	 * Required by MultiPageEditorPart
	 */
	public void createPages() {
		_errorUpdater = new EvEditorErrorUpdater( this );

		// Performance mode
		//-----------------
		_iPerformance = EvPreferences.getInt( EvConstants.PREFERENCE_PERFORMANCE );
		_iRenderEngine = EvPreferences.getInt( EvConstants.PREFERENCE_RENDERENGINE );

		IPreferenceStore preferences = EGLBasePlugin.getPlugin().getPreferenceStore();
		preferences.addPropertyChangeListener( this );

		createPageSource();

		initializeSource();

		createPageDesign();
		createPagePreview();

		CTabFolder tabFolder = (CTabFolder)getContainer();
		
		new Mnemonics().setMnemonics( tabFolder );
		
		EvHelp.setHelp( tabFolder, EvHelp.VISUAL_EDITOR );

		tabFolder.addSelectionListener( this );

		if( isRuiHandler() == true )
			this.setActivePage( EvPreferences.getInt( EvConstants.PREFERENCE_EDITOR_OPENING_TAB ) );
		else
			this.setActivePage( 1 );

		// Listen to changes in the document to refresh the browser
		//---------------------------------------------------------
		IEGLDocument currentDocument = getDocument();
		if( currentDocument != null )
			currentDocument.addModelChangeListener( this );

		// Generate the web page and display the web page
		// in the design and preview pages
		//-----------------------------------------------
		updateBrowsers();
	}

	/**
	 *
	 */
	protected void createPageSource() {
		if( _pageSource != null )
			return;

		_pageSource = new EGLEditor();

		try {
			addPage( 0, (IEditorPart)_pageSource, getEditorInput() );
			setPageText( 0, Messages.NL_Source );
		}

		catch( PartInitException e ) {
			e.printStackTrace();
		}

		_editorProvider = new EvEditorProvider( _pageSource );

		initializeUndoManager();
	}

	/**
	 * Called by WorkbenchPartReference during close of this part.
	 */
	public void dispose() {
		// Remove listeners from singleton classes
		//----------------------------------------
		ResourcesPlugin.getWorkspace().removeResourceChangeListener( this );
		WidgetDescriptorRegistry.getInstance(this.getProject()).removeWidgetDescriptorRegistryListener( this );

		IEGLDocument currentDocument = getDocument();
		if( currentDocument != null )
			cleanModelChangeListeners( currentDocument );

		IEditorSite editorSite = getEditorSite();
		if( editorSite != null )
			editorSite.getPage().removePartListener( this );

		if( _pageDesign != null )
			_pageDesign.terminate();
		
		if( _pagePreview != null )
			_pagePreview.terminate();
		
		if( _editorProvider != null )
			_editorProvider.cleanupGeneratedJavaScript();
		
		if( _errorUpdater != null )
			_errorUpdater.dispose();
				
		super.dispose();
	}

	/**
	 * Required by MultiPageEditorPart.  The EGL editor is asked to save the file.
	 */
	public void doSave( IProgressMonitor progressMonitor ) {
		_pageSource.doSave( progressMonitor );
	}

	/**
	 * Required by MultiPageEditorPart.
	 * Calls the EGLEditor to do the save as.  Changes this editor part's title to the EGLEditor's new title which has changed.
	 */
	public void doSaveAs() {
		// Ask the source page to do the save as
		//--------------------------------------
		_pageSource.doSaveAs();

		// The source page will have changed its input
		// Obtain the new input and set this editor's input
		// This will give the new file to this editor
		//-------------------------------------------------
		setInput( _pageSource.getEditorInput() );

		// Change the title bar file name
		//-------------------------------
		setPartName( _pageSource.getTitle() );

		// Update the design and preview pages
		//------------------------------------
		_bWebPageGenerationRequired = true;
		updateBrowsers();
	}

	/**
	 * Called by the property sheet to create a new event handling function.
	 */
	public void doSourceOperationFunctionCreate( String strFunctionName ) {
		if( _undoManager != null )
			_undoManager.operationStarting();

		_bOperationInProgress = true;

		NestedFunction function = null;

		try {
			function = _editorProvider.createEventHandlingFunction( strFunctionName );
		}
		catch( Exception ex ) {
		}
		finally {
			_bOperationInProgress = false;

			if( _undoManager != null )
				_undoManager.operationEnded( "" );

			// Have the source editor move the cursor to the new function
			//-----------------------------------------------------------
			if( function == null )
				return;

			StructuredSelection selection = new StructuredSelection( function );
			SelectionChangedEvent event = new SelectionChangedEvent( _outlinePage, selection );
			_pageSource.doSelectionChanged( event );
		}
	}
	
	public void doSourceOperationFieldCreate(String packageName, String fieldName, String fieldType, String template ) {
		if( _undoManager != null )
			_undoManager.operationStarting();

		try {
			_editorProvider.createField(packageName, fieldName, fieldType, template);
		}
		catch( Exception ex ) {
		}
		finally {

			if( _undoManager != null )
				_undoManager.operationEnded( "" );
		}
	}

	public IProject getProject() {

		IFile fileInput = this.getFile();

		IProject projectEditorInput = null;
		if( fileInput != null )
			projectEditorInput = fileInput.getProject();
		
		return projectEditorInput;
	}
	
	/**
	 * Called by the overlay to create a widget in the source.
	 * Inserts a new widget of the specified type at the specified index position.
	 */
	public void doSourceOperationWidgetCreate( WidgetDescriptor descriptor, EvDesignOverlayDropLocation location ) {

		boolean bIncremental = true;
		
		// Obtain the project of the widget
		//---------------------------------
		String strProjectName = null;
		if( descriptor != null )
			strProjectName = descriptor.getProjectName();
		
		IProject projectWidget = null;
		if( strProjectName != null ){
			IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
			projectWidget = workspaceRoot.getProject( strProjectName );
		}
		
		// Prompt for a widget name
		//-------------------------
		String strWidgetName = null;

		boolean bPrompt = EvPreferences.getBoolean( EvConstants.PREFERENCE_PROMPT_FOR_A_NEW_WIDGET_NAME );
		
		if( !GenManager.getInstance().isGenFromDataView() && bPrompt == true ) {
			EvWidgetNameDialog dialog = new EvWidgetNameDialog( Display.getCurrent().getActiveShell(),  descriptor.getLabel(), _editorProvider );
			int iRC = dialog.open();
			
			if( iRC == Dialog.CANCEL )
				return;

			strWidgetName = dialog.getName();
		}
		
		if(GenManager.getInstance().isGenFromDataView()){
			GenModel genModel = GenModelBuilder.getInstance().getGenModel();
			if(genModel != null){
				strWidgetName = genModel.getName();
			}
		}
		
		
		if( _undoManager != null )
			_undoManager.operationStarting();

		_bOperationInProgress = true;

		// Obtain the widget template
		//---------------------------
		String strWidgetCreationTemplate = null;
		String strfunctionCreationTemplate = null;
		if( descriptor != null ){
			if(GenManager.getInstance().isGenFromDataView()){
				strWidgetCreationTemplate = descriptor.getDataTemplate();
				strfunctionCreationTemplate = descriptor.getDataFunctionTemplate();
			}else{
				strWidgetCreationTemplate = descriptor.getTemplate();
			}
		}
		
		
		if( bIncremental == true ) {
			IEGLDocument currentDocument = getDocument();
			if( currentDocument != null ) {
				cleanModelChangeListeners( currentDocument );
			}
		}
		
		try {
			int iParentStatementOffset = 0;
			int iParentStatementLength = 0;

			if( location.widgetParent != null ) {
				iParentStatementOffset = location.widgetParent.getStatementOffset();
				iParentStatementLength = location.widgetParent.getStatementLength();
			}
			if( location.widgetLayoutData != null ) {
				String[] layoutWidget = location.widgetLayoutData.getWidgetLayout().getLayoutWidgetQualifiedName();
				WidgetDescriptor widgetDescriptor = WidgetDescriptorRegistry.getInstance(this.getProject()).getDescriptor(layoutWidget[0], layoutWidget[1]);
				String layoutDataTemplate = "new " + widgetDescriptor.getChildLayoutDataTemplate();

				String layoutProperty = location.widgetLayoutData.getWidgetLayout().processNewLayoutData( location.widgetLayoutData.getLayoutData(), layoutDataTemplate );
				int startIndex = 0;
				if(GenManager.getInstance().isGenFromDataView() && GenManager.getInstance().getGenRootWidgetName() != null) {
					String rootWidgetname = GenManager.getInstance().getGenRootWidgetName();
					startIndex = strWidgetCreationTemplate.indexOf( rootWidgetname );
					GenManager.getInstance().setGenRootWidgetName( null );
				}
				startIndex = strWidgetCreationTemplate.indexOf( "{", startIndex );
				while ( strWidgetCreationTemplate.charAt( startIndex - 1) == '$' ) {
					startIndex = strWidgetCreationTemplate.indexOf( "{", startIndex + 1 );
				}
				int hasProperty = strWidgetCreationTemplate.lastIndexOf( "=" );
				strWidgetCreationTemplate = strWidgetCreationTemplate.substring( 0, startIndex + 1 ) 
							+ " layoutData = " + layoutProperty + (hasProperty < 0 ? "" : ",")
							+ strWidgetCreationTemplate.substring( startIndex + 1 );
			}
			if(GenManager.getInstance().isGenFromDataView()) {
				// remove the root widget variable name, the name will be added in _editorProvider.create
				strWidgetCreationTemplate = strWidgetCreationTemplate.substring(strWidgetCreationTemplate.indexOf(" ") + 1, strWidgetCreationTemplate.length());  
			}

			// IBMBIDI Append Start
			if( BidiUtils.isBidi() )
				strWidgetCreationTemplate = BidiUtils.updateTemplateWithBidi( strWidgetCreationTemplate, _pageDesign.getBidiFormat() );
			// IBMBIDI Append End

			_editorProvider.create( projectWidget, descriptor.getPackage(), descriptor.getType(), iParentStatementOffset, iParentStatementLength, location.iIndex, strWidgetCreationTemplate, strfunctionCreationTemplate, strWidgetName );

		}
		catch( Exception ex ) {
		}
		finally {
			_bOperationInProgress = false;

			if( _undoManager != null )
				_undoManager.operationEnded( "" );

			if( bIncremental == true ) {
				updateBrowsersIncremental();

				IEGLDocument currentDocument = getDocument();
				if( currentDocument != null ) {
					restoreModelChangeListeners( currentDocument );
				}
			}
			else {
				updateBrowsers();
			}

			_pageDesign.setFocus();
		}
	}
	
	/**
	 * Called by the overlay to delete a widget from the source.
	 */
	public void doSourceOperationWidgetDelete( WidgetPart widget ) {

		boolean bIncremental = true;
		int totalCharactersRemoved = 0;

		if( _undoManager != null )
			_undoManager.operationStarting();

		_bOperationInProgress = true;

		if( bIncremental == true ) {
			IEGLDocument currentDocument = getDocument();
			if( currentDocument != null )
				cleanModelChangeListeners( currentDocument );
		}

		try {
			WidgetPart widgetParent = widget.getParent();
			int iWidgetIndex = widgetParent.getChildIndex( widget );
			totalCharactersRemoved = _editorProvider.delete( widgetParent.getStatementOffset(), widgetParent.getStatementLength(), iWidgetIndex );
		}
		catch( Exception ex ) {
		}
		finally {
			_bOperationInProgress = false;

			if( _undoManager != null )
				_undoManager.operationEnded( "" );

			if( bIncremental == true ) {
				updateBrowsersIncremental();
				//There still has some issue with DOM operation directly, such can not keep 
				//the offset and length info of the widget in EGL file update.
				//deleteWidget( widget, totalCharactersRemoved );
				
				IEGLDocument currentDocument = getDocument();
				if( currentDocument != null ) {
					restoreModelChangeListeners( currentDocument );
				}
			}
			else {
				updateBrowsers();
			}

		}
	}

	/**
	 * Called by the property sheet when an event value has changed, where the value is the name of a function. 
	 */
	public void doSourceOperationWidgetEventValueChange( WidgetPart widget, WidgetEventDescriptor descriptor, String strValueOld, String strValueNew, boolean refresh ) {

		boolean bIncremental = true;

		if( _undoManager != null )
			_undoManager.operationStarting();

		_bOperationInProgress = true;

		if( refresh == true && bIncremental == true ) {
			IEGLDocument currentDocument = getDocument();
			if( currentDocument != null )
				cleanModelChangeListeners( currentDocument );
		}

		try {
			_editorProvider.setEventValue( widget.getStatementOffset(), widget.getStatementLength(), descriptor.getID(), strValueNew );
		}
		catch( Exception ex ) {
		}
		finally {
			_bOperationInProgress = false;

			if( _undoManager != null )
				_undoManager.operationEnded( "" );

			if ( refresh ) {
				if( bIncremental == true ) {
					updateBrowsersIncremental();
	
					IEGLDocument currentDocument = getDocument();
					if( currentDocument != null ) {
						restoreModelChangeListeners( currentDocument );
					}
				}
				else {
					updateBrowsers();
				}
			}
		}
	}

	/**
	 * Called by the overlay to relocate a widget in the source.
	 */
	public void doSourceOperationWidgetMove( WidgetPart widget, EvDesignOverlayDropLocation location ) {

		boolean bIncremental = true;

		if( _undoManager != null )
			_undoManager.operationStarting();

		_bOperationInProgress = true;

		if( bIncremental == true ) {
			IEGLDocument currentDocument = getDocument();
			if( currentDocument != null )
				cleanModelChangeListeners( currentDocument );
		}
		
		int[] charactersChanged = new int[2];
		int iWidgetIndex = 0;
		try {
			WidgetPart widgetParent = widget.getParent();
			iWidgetIndex = widgetParent.getChildIndex( widget );
			PropertyValue layoutData = _editorProvider.getPropertyValue( widget.getStatementOffset(), widget.getStatementLength(), "layoutData", "" );
			charactersChanged = _editorProvider.move( widgetParent.getStatementOffset(), widgetParent.getStatementLength(), iWidgetIndex, location.widgetParent.getStatementOffset(), location.widgetParent.getStatementLength(), location.iIndex );

			int statementOffset = widget.getStatementOffset();
			if ( statementOffset > widgetParent.getStatementOffset() && statementOffset > location.widgetParent.getStatementOffset() ) {
				statementOffset = statementOffset - charactersChanged[0] + charactersChanged[1];
			} else if ( statementOffset > widgetParent.getStatementOffset() && statementOffset < location.widgetParent.getStatementOffset() ) {
				statementOffset = statementOffset - charactersChanged[0];
			} else if ( statementOffset < widgetParent.getStatementOffset() && statementOffset > location.widgetParent.getStatementOffset() ) {
				statementOffset = statementOffset + charactersChanged[1];
			}
			if ( location.widgetLayoutData != null ) {
				WidgetLayout layout = location.widgetLayoutData.getWidgetLayout();
				String[] layoutWidget = location.widgetLayoutData.getWidgetLayout().getLayoutWidgetQualifiedName();
				WidgetDescriptor widgetDescriptor = WidgetDescriptorRegistry.getInstance(this.getProject()).getDescriptor(layoutWidget[0], layoutWidget[1]);
				String layoutDataTemplate = "new " + widgetDescriptor.getChildLayoutDataTemplate();
				layout.updateLayoutData( _editorProvider, statementOffset, widget, location.widgetLayoutData.getLayoutData(), layoutData, layoutDataTemplate );
			} else {
				PropertyValue propertyValue = new PropertyValue( "", true );
				_editorProvider.setPropertyValue( statementOffset, widget.getStatementLength(), "layoutData", "", propertyValue );
			}
			PropertySheetPage.getInstance().setLayoutChanged( true );
		}
		catch( Exception ex ) {
		}
		finally {
			_bOperationInProgress = false;
			
			if( _undoManager != null )
				_undoManager.operationEnded( "" );

			if( bIncremental == true ) {
				updateBrowsersIncremental();
				//There still has some issue with DOM operation directly, such can not keep 
				//the offset and length info of the widget in EGL file update.
				//moveWidget( widget, location.widgetParent, iWidgetIndex, location.iIndex, charactersChanged);
				
				IEGLDocument currentDocument = getDocument();
				if( currentDocument != null ) {
					restoreModelChangeListeners( currentDocument );
				}
			}
			else {
				updateBrowsers();
			}
		}
	}

	/**
	 * Called when a single property value has changed. 
	 * Scenario: The user has changed a property value while using the properties view.
	 */
	public void doSourceOperationWidgetPropertyValueChange( WidgetPart widget, String strPropertyID, String strPropertyType, WidgetPropertyValue valueOld, WidgetPropertyValue valueNew, int propertyContainerType ) {

		boolean bIncremental = true;

		if( _undoManager != null )
			_undoManager.operationStarting();

		_bOperationInProgress = true;

		if( bIncremental == true ) {
			IEGLDocument currentDocument = getDocument();
			if( currentDocument != null )
				cleanModelChangeListeners( currentDocument );
		}
		PropertyValue propertyValue = null;
		int totalCharactersChanged = 0;
		try {
			propertyValue = valueNew == null ? null : new PropertyValue( valueNew.getValues(), valueNew.isEditable() );
			if ( propertyContainerType == WidgetPropertyDescriptor.WIDGET_PROPERTY ) {
				totalCharactersChanged = _editorProvider.setPropertyValue( widget.getStatementOffset(), widget.getStatementLength(), strPropertyID, strPropertyType, propertyValue );
			} else {
				totalCharactersChanged = _editorProvider.setLayoutPropertyValue( widget.getStatementOffset(), widget.getStatementLength(), strPropertyID, strPropertyType, propertyValue );
			}
		}
		catch( Exception ex ) {
		}
		finally {
			_bOperationInProgress = false;

			if( _undoManager != null )
				_undoManager.operationEnded( "" );

			if( bIncremental == true ) {
				updateBrowsersIncremental();

				// For futuer's improvement
				//changeProperty(widget, strPropertyID, (String)valueNew.getValues().get(0), totalCharactersChanged );

				IEGLDocument currentDocument = getDocument();
				if( currentDocument != null ) {
					restoreModelChangeListeners( currentDocument );
				}
			}
			else {
				updateBrowsers();
			}
		}
	}

	/**
	 * Called when multiple property values have changed.
	 * Scenario: A widget using absolute, fixed or relative position has changed both its x and y position.
	 */
	public void doSourceOperationWidgetPropertyValueChanges( List listPropertyChanges ) {
		
		if( listPropertyChanges == null || listPropertyChanges.size() == 0 )
			return;

		boolean bIncremental = true;

		if( _undoManager != null )
			_undoManager.operationStarting();

		_bOperationInProgress = true;

		if( bIncremental == true ) {
			IEGLDocument currentDocument = getDocument();
			if( currentDocument != null )
				cleanModelChangeListeners( currentDocument );
		}

		try {
			for( int i = 0; i < listPropertyChanges.size(); i++ ) {
				PropertyChange propertyChange = (PropertyChange)listPropertyChanges.get( i );				
				PropertyValue propertyValue = propertyChange.valueNew == null ? null : new PropertyValue( propertyChange.valueNew.getValues(), propertyChange.valueNew.isEditable() );
				_editorProvider.setPropertyValue( propertyChange.widget.getStatementOffset(), propertyChange.widget.getStatementLength(), propertyChange.strPropertyID, propertyChange.strPropertyType, propertyValue );
			}
		}
		catch( Exception ex ) {
		}
		finally {
			_bOperationInProgress = false;

			if( _undoManager != null )
				_undoManager.operationEnded( "" );

			if( bIncremental == true ) {
				updateBrowsersIncremental();

				IEGLDocument currentDocument = getDocument();
				if( currentDocument != null ) {
					restoreModelChangeListeners( currentDocument );
				}
			}
			else {
				updateBrowsers();
			}
		}
	}
	
	public void doSourceOperation( EvSourceOperation operation ) {

		boolean bIncremental = true;
		int totalCharactersRemoved = 0;

		if( _undoManager != null )
			_undoManager.operationStarting();

		_bOperationInProgress = true;

		if( bIncremental == true ) {
			IEGLDocument currentDocument = getDocument();
			if( currentDocument != null )
				cleanModelChangeListeners( currentDocument );
		}

		try {
			operation.doSourceOperation( _editorProvider );
		}
		catch( Exception ex ) {
		}
		finally {
			_bOperationInProgress = false;

			if( _undoManager != null )
				_undoManager.operationEnded( "" );

			if( bIncremental == true ) {
				updateBrowsersIncremental();
				//There still has some issue with DOM operation directly, such can not keep 
				//the offset and length info of the widget in EGL file update.
				//deleteWidget( widget, totalCharactersRemoved );
				
				IEGLDocument currentDocument = getDocument();
				if( currentDocument != null ) {
					restoreModelChangeListeners( currentDocument );
				}
			}
			else {
				updateBrowsers();
			}

		}
	}

	/**
	 * Called by the property sheet when an event value has changed.  The event value is a function name, or an empty string.
	 */
	public void eventChanged( WidgetPart widget, WidgetEventDescriptor descriptor, String strValueOld, String strValueNew, boolean refresh ) {
		doSourceOperationWidgetEventValueChange( widget, descriptor, strValueOld, strValueNew, refresh );
	}

	/**
	 * Asks the editor provider to generate the javascript
	 * Returns if the web page has no errors. 
	 */
	public WorkingCopyGenerationResult generateJavaScript() {
		_editorProvider.generateJavaScript();
		WorkingCopyGenerationResult result = _editorProvider.getLastGenerationResult();
		return result;
	}

	/**
	 * 
	 */
	public Object getAdapter( Class classRequested ) {
		if (classRequested == IEGLEditor.class || classRequested == EGLEditor.class) {
			return _pageSource;
		}
		
		// Page Data View
		//----------------
		if(classRequested.equals( IPageDataViewPage.class )){
			if( _eglPageDataViewPage == null ) {
				IDocument doc = getDocumentProvider().getDocument( getEditorInput() );
				if( doc instanceof IEGLDocument ) {
					_eglPageDataViewPage = new PageDataViewPage( (IEGLDocument)doc, this);
				}
			}

			return _eglPageDataViewPage;
		}
		
		
		// Content outline
		//----------------
		if( classRequested.equals( IContentOutlinePage.class ) == true ) {
			if( _outlinePage == null ) {
				IDocument doc = getDocumentProvider().getDocument( getEditorInput() );
				if( doc instanceof IEGLDocument ) {
					EvDesignOutlinePage outlineDesign = _pageDesign.getContentOutline();
					_outlinePage = new EvEditorOutlinePage( (IEGLDocument)doc, "#EGLOutlinerContext", _pageSource, outlineDesign );
					_outlinePage.addSelectionChangedListener( this );
					outlineDesign._editorOutlinePage = _outlinePage;
				}
			}

			return _outlinePage;
		}
		
		else if( classRequested.equals( IAnnotationModel.class ) == true ){
			return getDocumentProvider().getAnnotationModel( getEditorInput() );
		}

		// Property sheet page
		//--------------------
		else if( classRequested.equals( IPropertySheetPage.class ) == true ) {
			return PropertySheetPage.getInstance();
		}

		// Asked for by the property sheet page
		// so it can notify us about property changes
		//-------------------------------------------
		else if( classRequested.equals( IEvPropertySheetPageAdapter.class ) == true )
			return this;

		return super.getAdapter( classRequested );
	}

	/**
	 * Returns the EGL document. 
	 */
	public IEGLDocument getDocument() {
		IEditorInput input = getEditorInput();
		IDocumentProvider provider = getDocumentProvider();
		IDocument document = provider.getDocument( input );
		if (document instanceof EGLDocument) {
			return (IEGLDocument) document;
		} else {
			return new EGLDocument();
		}
	}

	/**
	 * 
	 */
	public IDocumentProvider getDocumentProvider() {
		return _pageSource.getDocumentProvider();
	}

	/**
	 * 
	 */
	public String getDocumentStatement( WidgetPart widget ) {
		if( widget == null )
			return "";

		IEGLDocument document = getDocument();
		if( document == null )
			return "";

		String strStatement = null;

		try {
			strStatement = document.get( widget.getStatementOffset(), widget.getStatementLength() );
		}
		catch( BadLocationException ex ) {
		}

		if( strStatement == null )
			return "";

		return strStatement;
	}

	/**
	 * Returns the document manager for the current input's document. 
	 */
	protected IDocumentUndoManager getDocumentUndoManager() {
		return DocumentUndoManagerRegistry.getDocumentUndoManager( getDocument() );
	}

	/**
	 * This is called by the design and preview pages.
	 * The editor provider is given to the preview context by the browser managers.
	 */
	public EvEditorProvider getEditorProvider() {
		return _editorProvider;
	}

	/**
	 * Declared in IEvPropertySheetAdapater.
	 * Called by the property sheet to obtain a list of event function handling names.
	 */
	public String[] getEventHandlingFunctionNames() {
		return _editorProvider.getEventHandlingFunctionNames();
	}

	/**
	 * Obtains the function name for the given event name
	 */
	public WidgetPropertyValue getEventValue( WidgetPart widget, String strEventName ) {
		EventValue eventValue = _editorProvider.getEventValue( widget.getStatementOffset(), widget.getStatementLength(), strEventName );
		if( eventValue == null )
			return null;

		ArrayList list = new ArrayList();
		list.add( eventValue.getValue() );
		return new WidgetPropertyValue( list, eventValue.isEditable() );
	}

	/**
	 * Returns the file from the editor input.
	 */
	public IFile getFile() {
		IEditorInput editorInput = getEditorInput();
		
		if( editorInput instanceof IFileEditorInput == false )
			return null;

		return ( (IFileEditorInput)editorInput ).getFile();
	}

	/**
	 * Returns the tab folder that contains the pages (0:Design, 1:Source, 2:Preview) 
	 */
	public CTabFolder getPageFolder() {
		return (CTabFolder)getContainer();
	}

	/**
	 * Returns the index of the currently displayed page.
	 */
	public int getPageIndex(){
		CTabFolder tabFolder = (CTabFolder)getContainer();
		return tabFolder.getSelectionIndex();
	}
	
	/**
	 * Returns the EGL Editor.
	 */
	public EGLEditor getPageSource() {
		return _pageSource;
	}

	/**
	 * Called by the properties view.
	 */
	public WidgetPropertyValue getPropertyValue( WidgetPart widget, String strPropertyName, String strPropertyType ) {
		PropertyValue propertyValue = _editorProvider.getPropertyValue( widget.getStatementOffset(), widget.getStatementLength(), strPropertyName, strPropertyType );
		if( propertyValue == null )
			return null;

		return new WidgetPropertyValue( propertyValue.getValues(), propertyValue.isEditable() );
	}
	
	/**
	 * Called by the properties view.
	 */
	public WidgetPropertyValue getLayoutPropertyValue( WidgetPart widget, String strPropertyName, String strPropertyType ) {
		PropertyValue propertyValue = _editorProvider.getLayoutPropertyValue( widget.getStatementOffset(), widget.getStatementLength(), strPropertyName, strPropertyType );
		if( propertyValue == null )
			return null;

		return new WidgetPropertyValue( propertyValue.getValues(), propertyValue.isEditable() );
	}

	/**
	 * http://localhost:5598/com.ibm.etools.egl.rui.samples/buttons/ManyButtons.html
	 */
	public String getURL() {
		int iPortNumber = EvServer.getInstance().getPortNumber();
		return _editorProvider.getWebPageURL( Integer.toString( iPortNumber ) );
	}

	/**
	 * Returns the name of a project that the widget is declared in given its package name and type name.
	 */
	public String getWidgetProjectName( String strWidgetPackageName, String strWidgetTypeName ){
		return _editorProvider.getWidgetProjectName( strWidgetPackageName, strWidgetTypeName );
	}
	
	/**
	 * Declared in IEvPropertySheetPageAdapter.
	 * The currently selected widget is returned.
	 * If the active page is the source page, then a null is returned
	 * to disable the properties page.
	 */
	public WidgetPart getWidgetSelected() {
		WidgetPart widgetSelected = null;
		
		if( getPageIndex() == 0 )
			widgetSelected = _pageDesign.getWidgetSelected();

		return widgetSelected;
	}

	/**
	 * 
	 */
	public void init( IEditorSite site, IEditorInput input ) throws PartInitException {
		super.init( site, input );

		if( input instanceof IFileEditorInput ){
			String strFileName = ( (IFileEditorInput)input ).getFile().getName(); 
			setPartName( strFileName );

			// Determine if translation test mode
			//-----------------------------------
			 _bTranslationTestMode = strFileName.equalsIgnoreCase( "TVT_TVT.egl" );
		}
		
		site.getPage().addPartListener( this );
		
		// Listen for file renames
		//------------------------
		ResourcesPlugin.getWorkspace().addResourceChangeListener( this, IResourceChangeEvent.POST_CHANGE | IResourceChangeEvent.PRE_DELETE );
		
		// Listen for changes to the widget descriptor registry
		//-----------------------------------------------------
		WidgetDescriptorRegistry.getInstance(this.getProject()).addWidgetDescriptorRegistryListener( this );
	
	}

	/**
	 * Attempts to create a RUI handler if there isn't one.
	 * If a RUI handler cannot exist in the source, then portions of the user interface are disabled. 
	 */
	protected void initializeSource() {
		_bRuiHandler = _editorProvider.isVESupportType();
		if(_editorProvider.isRUIWidget()){
			_widgetId = _editorProvider.getRUIWidgetId();
		}
	}
	
	public String getWidgetId() {
		return _widgetId;
	}

	/**
	 * 
	 */
	protected void initializeUndoManager() {
		IDocument document = getDocument();
		if( document == null )
			return;

		_undoManager = new EvEditorUndoManager( document );
	}

	/**
	 * Returns whether the file is a RUI Handler.
	 */
	public boolean isRuiHandler() {
		return _bRuiHandler;
	}

	/**
	 * Required by MultiPageEditorPart
	 */
	public boolean isSaveAsAllowed() {
		return true;
	}

	/**
	 * Returns whether we are in translation test mode.
	 */
	public boolean isTranslationTestMode(){
		return _bTranslationTestMode;
	}
	
	/**
	 * Declared in IEGLModelChangeListener.
	 * Called when the model has changed.  The browsers are updated and
	 * the dirty property is updated so that a save is not required after all undos are undone.
	 */
	public void modelChanged() {
		_bWebPageGenerationRequired = true;
		updateBrowsers();
		
		// Update the dirty status to indicate whether a save is required
		// Each of the editor's isDirty method will be called.  The
		// design and preview pages always return false.  So the source
		// page indicates whether a save is needed.
		//---------------------------------------------------------------
		getContainer().getDisplay().asyncExec( new Runnable() {
			public void run() {
				firePropertyChange( IEditorPart.PROP_DIRTY );
			}
		} );
	}

	/**
	 * Declared in IPartListener.
	 * Scenario of two editors open on the same document (EGL RUI Visual Editor + EGL Editor).
	 * A change made to the document while in the EGL Editor is recorded here by the modelChanged method.
	 * The browsers are not updated since the editor is not active.
	 * When the editor becomes active, the browsers are then updated.
	 */
	public void partActivated( IWorkbenchPart part ) {
		if( part != this )
			return;
		
		selectOutlinePage();
		updateBrowsers();
	}

	/**
	 * Declared in IPartListener.  Not used.
	 */
	public void partBroughtToTop( IWorkbenchPart part ) {
	}

	/**
	 * Declared in IPartListener.  Not used. 
	 */
	public void partClosed( IWorkbenchPart part ) {
	}

	/**
	 * Declared in IPartListener.  Not used. 
	 */
	public void partDeactivated( IWorkbenchPart part ) {
	}

	/**
	 * Declared in IPartListener.  Not used. 
	 */
	public void partOpened( IWorkbenchPart part ) {
	}

	/**
	 * Declared in IPropertyChangeListener
	 * We listen for changes to the generation mode.
	 */
	public void propertyChange( PropertyChangeEvent event ) { 
//TODO EDT BIDI
//		// IBMBIDI Append Start
//		if( EDTCoreIDEPlugin.BIDI_ENABLED_OPTION.equals( event.getProperty() ) ) {
//			IEditorSite site = getEditorSite();
//			if( site == null )
//				return;
//			
//			IWorkbenchWindow window = site.getWorkbenchWindow();
//			if( window == null )
//				return;
//			
//			IWorkbenchPage page = window.getActivePage();
//			if( page == null )
//				return;
//			
//			IEditorPart editor = page.getActiveEditor();
//			
//			if( editor == this )
//				PropertySheetPage.getInstance().widgetSelected(getWidgetSelected(), PropertySheetPage.getInstance().getEditorAdapter(), true );
//		}
//		// IBMBIDI Append End			
	}

	/**
	 * Declared in IPropertySheetPageAdapter.
	 * Called by the property sheet when a property value has changed.
	 * This method monitors changes to properties for special processing.
	 */
	public void propertyChanged( WidgetPart widget, WidgetPropertyDescriptor descriptor, WidgetPropertyValue valueOld, WidgetPropertyValue valueNew ) {
		
		// Changes to "position" requires changes to "x" and "y"
		// An empty x and y places the widget in its usual static location
		// but, a subsequent drag assumes the widget is originally at
		//  pixel location (0,0).  Therefore we specifically specify
		// "x"=0 and "y"=0.
		//----------------------------------------------------------------
		if( descriptor.getID().equalsIgnoreCase( "position" ) == true ){
			propertyChangedPosition( widget, descriptor, valueOld, valueNew );
			return;
		}

		try {
			doSourceOperationWidgetPropertyValueChange( widget, descriptor.getID(), descriptor.getType(), valueOld, valueNew, descriptor.getPropertyContainerType() );
		}
		finally {
		}
	}

	/**
	 * The "position" property value has changed.
	 * For a "position" of "static", or not specified, the "x" and "y" attributes are removed.
	 * For all other values of "position", the "x" and "y" attribute values are set to 0.
	 */
	public void propertyChangedPosition( WidgetPart widget, WidgetPropertyDescriptor descriptor, WidgetPropertyValue valueOld, WidgetPropertyValue valueNew ) {
		ArrayList listValues = valueNew.getValues();
		String strValue = null;
		if( listValues != null && listValues.size() > 0 )
			strValue = (String)listValues.get( 0 );
		
		ArrayList listChanges = new ArrayList();

		// "absolute", "relative", "fixed"
		//--------------------------------
		if ( strValue != null && ( strValue.equalsIgnoreCase( "\"absolute\"" ) 
				|| strValue.equalsIgnoreCase( "\"relative\"" ) || strValue.equalsIgnoreCase( "\"fixed\"" ) ) ){
			PropertyChange change = new PropertyChange();
			change.strPropertyID = descriptor.getID();
			change.strPropertyType = descriptor.getType();
			change.valueNew = valueNew;
			change.valueOld = valueOld;
			change.widget = widget;
			listChanges.add( change );
			
			// Initialize x and y to zero
			//---------------------------
			change = new PropertyChange();
			change.strPropertyID = "x";
			change.strPropertyType = "integer";
			change.valueNew = new WidgetPropertyValue( "0" );
			change.valueOld = null;
			change.widget = widget;
			listChanges.add( change );

			change = new PropertyChange();
			change.strPropertyID = "y";
			change.strPropertyType = "integer";
			change.valueNew = new WidgetPropertyValue( "0" );
			change.valueOld = null;
			change.widget = widget;
			listChanges.add( change );
		}
		// Static or (none) or others
		//-----------------
		else {
			PropertyChange change = new PropertyChange();
			change.strPropertyID = descriptor.getID();
			change.strPropertyType = descriptor.getType();
			change.valueNew = valueNew;
			change.valueOld = valueOld;
			change.widget = widget;
			listChanges.add( change );
			
			// Remove x and y
			//---------------
			change = new PropertyChange();
			change.strPropertyID = "x";
			change.strPropertyType = "integer";
			change.valueNew = new WidgetPropertyValue( "" );
			change.valueOld = null;
			change.widget = widget;
			listChanges.add( change );

			change = new PropertyChange();
			change.strPropertyID = "y";
			change.strPropertyType = "integer";
			change.valueNew = new WidgetPropertyValue( "" );
			change.valueOld = null;
			change.widget = widget;
			listChanges.add( change );
		}
		
		doSourceOperationWidgetPropertyValueChanges( listChanges );
	}
	
	/**
	 * Declared in IResourceChangeListener for listening to all workspace resource changes.
	 * Checks for a change in the file name.  If changed, the editor tab text is updated.
	 */
	public void resourceChanged( IResourceChangeEvent event ) {
		// Call our visit method to handle file deletion and rename
		//---------------------------------------------------------
		IResourceDelta delta = event.getDelta();
		try {
			if( delta != null )
				delta.accept( this );
		}
		catch( CoreException x ) {
			return;
		}
	}

	/**
	 * Declared in IEditorSelectAndRevealer.
	 * Called by the server when the user clicks on a stack dump.
	 * The specified source line is selected and the source tab is shown. 
	 */
	public void selectAndRevealLine( int iLine ) {
		IDocumentProvider provider = getDocumentProvider();
		IDocument document = provider.getDocument( getEditorInput() );

		try {
			int iStart = document.getLineOffset( iLine - 1 );
			int iEnd = document.getLineOffset( iLine );
			_pageSource.selectAndReveal( iStart, iEnd - iStart );
			showSourcePage();
		}
		catch( BadLocationException e ) {
			e.printStackTrace();
		}
	}
	
	/**
	 * The codes from offset to (offset + length) are selected and the source tab is shown. 
	 */
	public void selectAndRevealRange( int offset, int length ) {
		_pageSource.selectAndReveal( offset, length );
		showPage( 1 );
		this.getContainer().getDisplay().asyncExec( new Runnable() {
			public void run() {
				_pageSource.setFocus();
			}
		} );
	}

	/**
	 * Declared in ISelectionChangedListener.
	 * Called from outline page whenever a selection is changed.
	 * The corresponding widget part is found and the widget part is
	 * sent to the property page and design page.
	 */
	public void selectionChanged( SelectionChangedEvent event ) {
		TreeSelection selection = (TreeSelection)event.getSelection();
		Object objElement = selection.getFirstElement();

		// Graphical editor outline
		//-------------------------
		if( objElement instanceof WidgetPart ) {
			_pageDesign.selectWidget( (WidgetPart)objElement );
			PropertySheetPage.getInstance().widgetSelected( (WidgetPart)objElement, this );
		}

		// Pass this event on to the EGL source editor
		// to move the cursor to a source line
		//--------------------------------------------
		else
			_pageSource.doSelectionChanged( event );

		// Source editor outline
		//-------------------------------------------------------------
		// Commented out
		// The source page is active, do not activate the property page
		//-------------------------------------------------------------
		if( false && objElement instanceof ClassDataDeclaration ) {
			ClassDataDeclaration classData = (ClassDataDeclaration)objElement;
			int iOffset = classData.getOffset();
			int iLength = classData.getLength();

			WidgetPart widget = _pageDesign.getWidget( iOffset, iLength );

			if( widget != null ) {
				_pageDesign.selectWidget( widget );

				PropertySheetPage.getInstance().widgetSelected( widget, this );
			}
		}
	}
	
	/**
	 * Shows the outline page depending on which editor tab is active.
	 */
	protected void selectOutlinePage(){
		if( _outlinePage == null )
			return;
		
		// Update outline view
		//--------------------
		switch( getPageIndex() ){
			case 0:
				(_outlinePage).showPage( EvEditorOutlinePage.DESIGN_PAGE );
				break;

			case 1:
				(_outlinePage).showPage( EvEditorOutlinePage.SOURCE_PAGE );
				break;

			case 2:
				(_outlinePage).showPage( EvEditorOutlinePage.PREVIEW_PAGE );
				break;
		}
	}
	
	/**
	 * Shows the page data view page depending on which editor tab is active.
	 */
	protected void selectPageDataViewPage(){
		if( _eglPageDataViewPage == null )
			return;
		
		// Update outline view
		//--------------------
		switch( getPageIndex() ){
			case 0:
				_eglPageDataViewPage.showPage( EvEditorOutlinePage.DESIGN_PAGE );
				break;

			case 1:
				_eglPageDataViewPage.showPage( EvEditorOutlinePage.SOURCE_PAGE );
				break;

			case 2:
				_eglPageDataViewPage.showPage( EvEditorOutlinePage.PREVIEW_PAGE );
				break;
		}
	}
    
	/**
	 * Override for file rename.
	 */
	protected void setInput( IEditorInput input ) {
		// Editor startup
		//---------------
		if( getEditorInput() == null ) {
			super.setInput( input );
			return;
		}
		
		// Remove listener from old document
		//----------------------------------
		IEGLDocument currentDocument = getDocument();
		if( currentDocument != null )
			cleanModelChangeListeners( currentDocument );

		super.setInput( input );

		// Add listener to new document
		//-----------------------------
		currentDocument = getDocument();
		if( currentDocument != null )
			currentDocument.addModelChangeListener( this );

		// Rename of input file
		//---------------------
		if( _editorProvider != null )
			_editorProvider.cleanupGeneratedJavaScript();

		_editorProvider = new EvEditorProvider( _pageSource );
		
		// Create new browser managers
		//----------------------------
		_pageDesign.inputChanged( input );
		_pagePreview.inputChanged( input );

		// Update the editor tab with the new file name
		// Update the browsers
		//---------------------------------------------
		final String strTitle = _pageSource.getTitle();
		
		Display display= getSite().getShell().getDisplay();
		display.asyncExec(new Runnable() {
			public void run() {
				// Editor tab
				//-----------
				setPartName( strTitle );

				// Browsers
				//---------
				_bWebPageGenerationRequired = true;
				_pageDesign._bFullRefresh = true;
				_pagePreview._bFullRefresh = true;
				updateBrowsers();
			}
		});
		
	}

	/**
	 * Turns to the specified tab folder page where 0 is Design, 1 is Source, 2 is Preview.
	 */
	public void showPage( int iPageIndex ) {
		if( iPageIndex < 0 || iPageIndex > 2 )
			return;
		
		if(_eglPageDataViewPage != null){
			_eglPageDataViewPage.showPage(iPageIndex);
		}
		setActivePage(iPageIndex);
		PropertySheetPage.getInstance().widgetSelected( getWidgetSelected(), this );
	}
	
	public void showSourcePage() {
		// Source tab is in the middle. Non-RUI Handler files only have a source tab.
		if ( getPageCount() > 1 && getPageIndex() != 1 ) {
			showPage( 1 );
		}
	}

	/**
	 * If a web page generation is not required, nothing is done.
	 * Otherwise, if either the design page or preview page are active, then a web
	 * page generation is done, then the design page and preview pages are updated. 
	 * The active page is updated before the inactive page.
	 */
	protected void updateBrowsers() {
		if( _bWebPageGenerationRequired == false )
			return;
		
		// Wait until the operation has ended
		//-----------------------------------
		if( _bOperationInProgress == true )
			return;
		
		int iPageIndex = getPageIndex();

		if( iPageIndex != 1 ){
			// Handle change from RUIHandler to/from non-RUIHandler
			//-----------------------------------------------------
			boolean bRuiHandlerCurrent = _editorProvider.isVESupportType();
		
			// Refresh the browsers if it either is, or is no longer a RUI Handler
			//--------------------------------------------------------------------
			if( bRuiHandlerCurrent == true || bRuiHandlerCurrent != _bRuiHandler ){
				_bRuiHandler = bRuiHandlerCurrent;
				
				switch( iPageIndex ){
					case 0: // Design page
						WorkingCopyGenerationResult result = generateJavaScript();
						_pageDesign.updateBrowser( result );
						_pagePreview.updateBrowser( result );
						_bWebPageGenerationRequired = false;
						
						// If an error occurred, nullify the selected widget
						// so the property page doesn't show properties
						//--------------------------------------------------
						if( result.hasError() )
							_pageDesign.selectWidget( null );
						_editorProvider.clearCache();						
						break;

					case 2: // Preview page
						result = generateJavaScript();
						_pagePreview.updateBrowser( result );
						_pageDesign.updateBrowser( result );
						_bWebPageGenerationRequired = false;

						// If an error occurred, nullify the selected widget
						// so the property page doesn't show properties
						//--------------------------------------------------
						if( result.hasError() )
							_pageDesign.selectWidget( null );
						break;
				}

				// If there is no handler, update the outline view.
				// If there is a handler, the outline view will be updated
				// when the widget information is received from the web browser
				//-------------------------------------------------------------
				_bRuiHandler = bRuiHandlerCurrent;				
				if( _bRuiHandler == false && _outlinePage != null )
					(_outlinePage).update();
			}
		}
	}

	/**
	 * If a web page generation is not required, nothing is done.
	 * Otherwise, if either the design page or preview page are active, then a web
	 * page generation is done, then the design page and preview pages are updated. 
	 * The active page is updated before the inactive page.
	 */
	protected void updateBrowsersIncremental() {
		// Wait until the operation has ended
		//-----------------------------------
		if( _bOperationInProgress == true )
			return;

		// Refresh the browsers only if the design or preview page is active
		//------------------------------------------------------------------
		int iPageIndex = getPageIndex();

		switch( iPageIndex ){
			case 0: // Design page
				WorkingCopyGenerationResult result = generateJavaScript();
				_pageDesign.updateBrowserIncremental( result );
				_pagePreview.updateBrowserIncremental( result );
				_bWebPageGenerationRequired = false;
				break;

			case 2: // Preview page
				result = generateJavaScript();
				_pagePreview.updateBrowserIncremental( result );
				_pageDesign.updateBrowserIncremental( result );
				_bWebPageGenerationRequired = false;
				break;
		}
	}

	/**
     * Update for the property change
     */
	protected void changeProperty(WidgetPart widget, String property, String value, int totalCharactersChanged) {
		// Wait until the operation has ended
		//-----------------------------------
		if( _bOperationInProgress == true )
			return;

		// Refresh the browsers only if the design or preview page is active
		//------------------------------------------------------------------
		int iPageIndex = getPageIndex();

		switch( iPageIndex ){
			case 0: // Design page
				WorkingCopyGenerationResult result = generateJavaScript();
				_pageDesign.changeProperty(result, widget, property, value, totalCharactersChanged);
//				_pageDesign.updateBrowserIncremental( result );
//				_pagePreview.updateBrowserIncremental( result );
				_bWebPageGenerationRequired = false;
				break;

			case 2: // Preview page
				result = generateJavaScript();
				_pagePreview.updateBrowserIncremental( result );
//				_pageDesign.updateBrowserIncremental( result );
				_bWebPageGenerationRequired = false;
				break;
		}
	}
	
	/**
     * Update for the widget move.
     */
	protected void moveWidget(WidgetPart widget, WidgetPart targetParent, int oldIndex, int newIndex, int[] charactersChanged) {
		// Wait until the operation has ended
		//-----------------------------------
		if( _bOperationInProgress == true )
			return;

		// Refresh the browsers only if the design or preview page is active
		//------------------------------------------------------------------
		int iPageIndex = getPageIndex();

		switch( iPageIndex ){
			case 0: // Design page
				WorkingCopyGenerationResult result = generateJavaScript();
				_pageDesign.moveWidget(result, widget, targetParent, oldIndex, newIndex, charactersChanged);
//				_pageDesign.updateBrowserIncremental( result );
				_pagePreview.updateBrowserIncremental( result );
				_bWebPageGenerationRequired = false;
				break;

			case 2: // Preview page
				result = generateJavaScript();
//				_pagePreview.updateBrowserIncremental( result );
				_pageDesign.updateBrowserIncremental( result );
				_bWebPageGenerationRequired = false;
				break;
		}
	}
	
	/**
     * Update for the widget delete
     */
	protected void deleteWidget(WidgetPart widget, int totalCharactersRemoved) {
		// Wait until the operation has ended
		//-----------------------------------
		if( _bOperationInProgress == true )
			return;

		// Refresh the browsers only if the design or preview page is active
		//------------------------------------------------------------------
		int iPageIndex = getPageIndex();

		switch( iPageIndex ){
			case 0: // Design page
				WorkingCopyGenerationResult result = generateJavaScript();
				_pageDesign.deleteWidget(result, widget, totalCharactersRemoved);
//				_pageDesign.updateBrowserIncremental( result );
				_pagePreview.updateBrowserIncremental( result );
				_bWebPageGenerationRequired = false;
				break;

			case 2: // Preview page
				result = generateJavaScript();
//				_pagePreview.updateBrowserIncremental( result );
				_pageDesign.updateBrowserIncremental( result );
				_bWebPageGenerationRequired = false;
				break;
		}
	}

	
	/*
	 * Update the icon used on the editor's tab
	 */
	public void updateTitleImage( Image image ) {
		setTitleImage( image );
	}

	/**
	 * This is called because we called delta.accept in resourceChanged.
	 * This is called whenever a resource in the workspace changes.
	 */
	public boolean visit( IResourceDelta delta ) throws CoreException {
		if( delta == null )
			return false;

		// The incoming delta is for the workspace
		// Obtain the delta for the file we are editing
		//---------------------------------------------
		IFile file = getFile();
		if( file == null )
			return false;
		
		IPath path = file.getFullPath();
		if( path == null )
			return false;
			
		// Check to see if the EGLPath has changed for this file
		IResourceDelta fileDelta = delta.findMember( file.getProject().getFile(".eglPath").getFullPath() );
		
		if(fileDelta != null){
			switch( fileDelta.getKind() ){
				case IResourceDelta.CHANGED:
					// reset the page
					if((fileDelta.getFlags() & IResourceDelta.CONTENT) != 0){
						//something has changed
						IEditorInput inputSource = _pageSource.getEditorInput();
						if( inputSource == null ){
							return false;
						}
						setInput(inputSource);
					}
					return false;
			}
		}
		
		// Check to see if the file being edited has changed
		fileDelta = delta.findMember( path );

		// If the delta is null, the file no longer exists due to a rename
		//----------------------------------------------------------------
		if( fileDelta == null ){
			// On a rename, the source page will have changed its input
			// Obtain the new input and set this editor's input
			// This will give the new file to this editor
			//---------------------------------------------------------
			if( _pageSource == null )
				return false;
			
			IEditorInput inputSource = _pageSource.getEditorInput();
			if( inputSource == null )
				return false;
			
			IEditorInput input = getEditorInput();
			if( input == null )
				return false;
			
			if( input != inputSource )
				setInput( inputSource );
			
			return false;
		}

		// Moved or deleted
		//-----------------
		switch( fileDelta.getKind() ){
			case IResourceDelta.REMOVED:
				// Move and rename
				// Rename: Do nothing since it is handled above later
				//---------------------------------------------------
				if( ( fileDelta.getFlags() & IResourceDelta.MOVED_TO ) != 0 ){
					
					// Determine whether the package has changed
					// Move: Package path has changed
					// Rename: Package path has not changed
					//------------------------------------------
					IPath pathA = path;
					IPath pathB = fileDelta.getMovedToPath();
					
					if( pathB == null )
						return false;

					// Remove trailing file name
					//--------------------------
					IPath pathA2 = pathA.removeLastSegments( 1 );
					IPath pathB2 = pathB.removeLastSegments( 1 );
					
					if( pathA2 == null || pathB2 == null )
						return false;

					// Moved to another project or package: Close for now
					//---------------------------------------------------
					if( pathA2.equals( pathB2 ) == false )
						close( false );
				}
				
				// Deletion
				//---------
				else
					close( false );
				break;
		}

		// Do not visit the resources children
		//------------------------------------
		return false;
	}

	/**
	 * Declared in SelectionListener.
	 * Called when the tab folder changes pages.
	 */
	public void widgetDefaultSelected( SelectionEvent event ) {
		widgetSelected( event );
	}

	/**
	 * Called when the widget descriptor registry has changed.
	 * This happens when the design page's refresh palette toolbar button is pressed.
	 * The image registry is cleared and refilled with widget images since a widget may get a new icon.
	 * The outline view is updated.  The palette view is unable to be updated since the GEF palette
	 * has its own image cache which is not accessible.
	 */
	public void widgetDescriptorRegistryChanged() {
		Activator activator = Activator.getDefault();
		if( activator == null )
			return;
		
		ImageRegistry imageRegistry = activator.getImageRegistry();
		if( imageRegistry == null )
			return;
		
		WidgetDescriptorRegistry widgetDescriptorRegistry = WidgetDescriptorRegistry.getInstance(this.getProject());
		if( widgetDescriptorRegistry == null )
			return;
		
		// Remove the widget images from the image registry
		//-------------------------------------------------
		Iterator iterGroups = widgetDescriptorRegistry.getDescriptorGroups();
		if( iterGroups == null )
			return;
		
		while( iterGroups.hasNext() ){
			WidgetDescriptorGroup group = (WidgetDescriptorGroup)iterGroups.next();

			ArrayList listDescriptors = group.getWidgetDescriptors();
			if( listDescriptors == null )
				continue;
			
			for( int i=0; i<listDescriptors.size(); ++i ){
				WidgetDescriptor widgetDescriptor = (WidgetDescriptor)listDescriptors.get( i );
				if( widgetDescriptor == null )
					continue;
				
				String strWidgetID = widgetDescriptor.getID();
				if( strWidgetID == null )
					continue;
				
				// Remove the old image and add the image
				//---------------------------------------
				ImageDescriptor imageDescriptor = EvPaletteRoot.getImageDescriptorForNodeType( strWidgetID );				
				if( imageDescriptor == null )
					continue;
				
				Image image = imageDescriptor.createImage();
				if( image == null )
					continue;

				imageRegistry.remove( strWidgetID );
				imageRegistry.put( strWidgetID, image );
			}
		}

		// Update the outline view
		//------------------------		
		if( _outlinePage != null )
			(_outlinePage).update();
	}

	/**
	 * Declared in SelectionListener.
	 * Called when the tab folder changes pages.
	 */
	public void widgetSelected( SelectionEvent event ) {

		if( event.getSource() instanceof CTabFolder ) {
			// Update browsers
			//----------------
			updateBrowsers();
			
			// Update properties view
			//-----------------------
			PropertySheetPage.getInstance().widgetSelected( getWidgetSelected(), this );			

			// Reveal the appropriate outline page
			//------------------------------------
			selectOutlinePage();

			// Reveal the page data view page
			//------------------------------------
			selectPageDataViewPage();
			
			// Populate the palette if the design page is being activated
			//-----------------------------------------------------------
			if( getPageIndex() == 0 ){
				// Workaround scenario:
				// Editor is opened with the user choosing to show the
				// source or preview page first.  The Palette view is not populated.
				// The user selects the Design tab.  The palette view is not populated.
				// Therefore we notify it that we have become active to populate its content.
				// If the palette view is not present, then the internal palette is being
				// used, and the palette view will not be in the list.  The internal palette
				// is always populated properly.
				//--------------------------------------------------------------------------
				IViewReference[] references = getEditorSite().getPage().getViewReferences();
				for( int i = 0; i < references.length; ++i ) {
					if( references[ i ].getId().equals( "org.eclipse.gef.ui.palette_view" ) == true ) {
						PaletteView paletteView = (PaletteView)references[ i ].getPart( true );
						paletteView.partActivated( getEditorSite().getPart() );
						break;
					}
				}
			}
		}
	}

	/**
	 * Notifies the property page and outline view.
	 * Moves the cursor on the source page to the statement.
	 */
	public void widgetSelectedFromDesignPage( WidgetPart widget ) {
		// Notify the outline view
		//------------------------	
		if( _outlinePage != null )
			(_outlinePage).widgetSelectedFromDesignPage( widget );

		// Notify the property page
		//-------------------------
		PropertySheetPage.getInstance().widgetSelected( widget, this );
		
		// Move the cursor on the source page to the statement
		//----------------------------------------------------
		if( widget != null ){
			int iStatementOffset = widget.getStatementOffset();
		
			if( iStatementOffset > -1 )
				_pageSource.selectAndReveal( iStatementOffset, 0 );
		}
	}

	public void selectAndReveal(int start, int length) {
		showSourcePage();
		_pageSource.selectAndReveal(start, length);		
	}
	
	private void cleanModelChangeListeners( IEGLDocument currentDocument ) {
		currentDocument.removeModelChangeListener( this );
		if ( _eglPageDataViewPage != null ) {
			currentDocument.removeModelChangeListener( _eglPageDataViewPage );
		}
	}
	
	private void restoreModelChangeListeners( IEGLDocument currentDocument ) {
		currentDocument.addModelChangeListener( this );
		if ( _eglPageDataViewPage != null ) {
			currentDocument.addModelChangeListener( _eglPageDataViewPage );
		}
	}
	
	protected IEditorSite createSite(IEditorPart editor) {
		return new MultiPageEditorSite(this, editor) {
			@Override
			public String getId() {
				return EvEditor.this.getSite().getId();
			}
		};
	}

	@Override
	public EGLEditor getEGLEditor() {
		return _pageSource;
	} 
	

	@Override
	public boolean isEditable() {
		return(_pageSource.isEditable());
	}

	@Override
	public void doRevertToSaved() {
		_pageSource.doRevertToSaved();
		
	}

	@Override
	public void setAction(String actionID, IAction action) {
		_pageSource.setAction(actionID, action);
	}

	@Override
	public IAction getAction(String actionId) {
		return(_pageSource.getAction(actionId));
	}

	@Override
	public void setActionActivationCode(String actionId,
			char activationCharacter, int activationKeyCode,
			int activationStateMask) {
		_pageSource.setActionActivationCode(actionId, activationCharacter, activationKeyCode, activationStateMask);
	}

	@Override
	public void removeActionActivationCode(String actionId) {
		_pageSource.removeActionActivationCode(actionId);
		
	}

	@Override
	public boolean showsHighlightRangeOnly() {
		return(_pageSource.showsHighlightRangeOnly());
	}

	@Override
	public void showHighlightRangeOnly(boolean showHighlightRangeOnly) {
		_pageSource.showHighlightRangeOnly(showHighlightRangeOnly);
		
	}

	@Override
	public void setHighlightRange(int offset, int length, boolean moveCursor) {
		_pageSource.setHighlightRange(offset, length, moveCursor);
		
	}

	@Override
	public IRegion getHighlightRange() {
		return(_pageSource.getHighlightRange());
	}

	@Override
	public void resetHighlightRange() {
		_pageSource.resetHighlightRange();
	}

	@Override
	public ISelectionProvider getSelectionProvider() {
		return(_pageSource.getSelectionProvider());
	} 
	
}
