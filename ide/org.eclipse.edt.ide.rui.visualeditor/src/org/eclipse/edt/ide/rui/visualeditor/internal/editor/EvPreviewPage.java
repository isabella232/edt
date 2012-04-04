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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.edt.ide.rui.utils.WorkingCopyGenerationResult;
import org.eclipse.edt.ide.rui.visualeditor.internal.util.AnimatedBusyPainter;
import org.eclipse.edt.ide.rui.visualeditor.plugin.Activator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;


public class EvPreviewPage extends EditorPart implements ProgressListener, SelectionListener {
	protected AnimatedBusyPainter			_animatedBusyPainter	= null;
	protected boolean						_bUpdateRequired		= true;
	protected boolean						_bFullRefresh			= true;
	protected Browser						_browser				= null;
	protected EvPreviewBrowserManager		_browserManager			= null;
	protected Composite						_compositePreview		= null;
	protected EvEditor						_editor					= null;
	protected WorkingCopyGenerationResult	_generationResult		= null;
	protected CTabFolder					_tabFolder				= null;
	protected EvPreviewToolbar				_toolbar				= null;

	/**
	 * 
	 */
	public EvPreviewPage( EvEditor editor ) {
		_editor = editor;
	}

	/**
	 * Declared in ProgressListener.  Notified by the browser widget.
	 * This method does nothing.
	 */
	public void changed( ProgressEvent event ) {
	}

	/**
	 * Called by the editor when it is being disposed.
	 */
	public void terminate() {
		if( _browserManager != null )
			_browserManager.terminate();
	}

	/**
	 * Declared in ProgressListener.  Notified by the browser widget.
	 */
	public void completed( ProgressEvent event ) {
		_animatedBusyPainter.animationStop();
	}

	/**
	 * Creates the browser and browser manager. 
	 */
	protected void createBrowser() {
		_browser = _editor.createBrowser( _compositePreview );

		if( _browser == null )
			return;

		GridData gridData = new GridData( GridData.FILL_BOTH );
		gridData.horizontalSpan = 2;
		_browser.setLayoutData( gridData );

		// Prevent adding ourselves as a listener 
		// more than once in shared browser scenario
		//------------------------------------------
		_browser.removeProgressListener( this );
		_browser.addProgressListener( this );

		// Only create the browser manager once
		// in shared browser scenario
		//-------------------------------------
		if( _browserManager == null )
			_browserManager = new EvPreviewBrowserManager( _browser, _editor.getURL(), this, _editor.getEditorProvider() );

		// Layout the parent composite to size the browser
		//------------------------------------------------
		_compositePreview.layout();
	}

	/**
	 * Creates the user interface for the preview page.
	 */
	public void createPartControl( Composite compositeParent ) {
		_compositePreview = new Composite( compositeParent, SWT.BORDER );
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.numColumns = 2;
		_compositePreview.setLayout( gridLayout );

		// Animated busy indicator
		//------------------------
		_animatedBusyPainter = new AnimatedBusyPainter( _compositePreview );
		GridData gridData = new GridData();
		gridData.widthHint = 16;
		gridData.heightHint = 16;
		gridData.horizontalIndent = 4;
		_animatedBusyPainter.setLayoutData( gridData );

		// Toolbar
		//--------
		_toolbar = new EvPreviewToolbar( _compositePreview, SWT.NULL, this );
		gridData = new GridData( GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL );
		_toolbar.setLayoutData( gridData );

		// Separator
		//----------
		Label label = new Label( _compositePreview, SWT.SEPARATOR | SWT.HORIZONTAL );
		gridData = new GridData( GridData.FILL_HORIZONTAL );
		gridData.horizontalSpan = 2;
		label.setLayoutData( gridData );

		// Listen for the first selection of this page
		// for lazy creation of the browser
		//--------------------------------------------
		_editor.getPageFolder().addSelectionListener( this );
	}

	/**
	 * 
	 */
	public EvPreviewBrowserManager getBrowserManager() {
		return this._browserManager;
	}

	/**
	 * Does nothing. 
	 */
	public void doSave( IProgressMonitor arg0 ) {
	}

	/**
	 * Does nothing.
	 */
	public void doSaveAs() {
	}

	/**
	 * Called by the editor if the design and preview
	 * pages share a common web browser.
	 */
	public Browser getBrowser() {
		return _browser;
	}

	/**
	 * Returns the visual editor.
	 */
	public EvEditor getEditor() {
		return _editor;
	}

	/**
	 * 
	 */
	public void init( IEditorSite site, IEditorInput input ) throws PartInitException {
		super.setSite( site );
		super.setInput( input );
	}

	/**
	 * Called by the editor when its setInput is called.
	 * This happens when the filename has changed. 
	 */
	public void inputChanged( IEditorInput input ) {
		if( _browserManager != null )
			_browserManager = new EvPreviewBrowserManager( _browser, _editor.getURL(), this, _editor.getEditorProvider() );
	}

	/**
	 * Always returns false.
	 */
	public boolean isDirty() {
		return false;
	}

	/**
	 * Always returns false.
	 */
	public boolean isSaveAsAllowed() {
		return false;
	}

	/**
	 * Called by the editor whenever the web browser is shared
	 * between the design and preview pages during a web browser
	 * re-parenting operation.
	 */
	public void resetBrowserToNull() {
		_browser = null;
	}

	/**
	 * Does nothing.
	 */
	public void setFocus() {
	}

	/**
	 * Updates the browser if the preview page is visible, and the model has changed.
	 */
	protected void updateBrowser( WorkingCopyGenerationResult result ) {
		// Save the generation results in case
		// the browser will be created later
		//------------------------------------
		_generationResult = result;

		// Do nothing if this page is not visible
		//---------------------------------------
		if( getEditor().getPageIndex() != 2 ) {
			_bUpdateRequired = true;
			return;
		}

		// Create the browser and browser manager if not yet created
		//----------------------------------------------------------
		if( _browser == null )
			createBrowser();

		// Display blank page if no RUI Handler
		//-------------------------------------
		if( _editor.isRuiHandler() == false ) {
			String strURL = Activator.getStateResourceURL( EvConstants.HTML_EMPTY );

			if( strURL != null )
				_browser.setUrl( strURL );

			return;
		}

		if( _browserManager != null ) {
			if ( result == null || result.hasError() )
				_bFullRefresh = true;
			
			if ( _bFullRefresh  )
				_animatedBusyPainter.animationStart();
			_browserManager.refreshBrowser(_bFullRefresh);
			_bFullRefresh = false;
		}

		_bUpdateRequired = false;
	}

	/**
	 * Updates the browser if the preview page is visible, and a property value changed.
	 */
	protected void updateBrowserIncremental( WorkingCopyGenerationResult result ) {
		// Save the generation results in case
		// the browser will be created later
		//------------------------------------
		_generationResult = result;

		// Do nothing if there is no RUI handler
		//--------------------------------------
		if( _editor.isRuiHandler() == false )
			return;

		// Do nothing if this page is not visible
		//---------------------------------------
		if( getEditor().getPageIndex() != 2 ) {
			_bUpdateRequired = true;
			return;
		}

		// Create the browser and browser manager if not yet created
		//----------------------------------------------------------
		if( _browser == null )
			createBrowser();

		if( _browserManager != null )
			_browserManager.refreshBrowserIncremental();

		_bUpdateRequired = false;
	}

	/**
	 * Declared in SelectionListener.  Does nothing. 
	 */
	public void widgetDefaultSelected( SelectionEvent e ) {
	}

	/**
	 * Declared in SelectionListener.  Listens only to the first selection of the preview page. 
	 */
	public void widgetSelected( SelectionEvent event ) {
		// Lazy creation and lazy update of the browser
		//---------------------------------------------
		CTabFolder tabFolder = (CTabFolder)event.widget;

		if( tabFolder.getSelectionIndex() != 2 )
			return;

		// If the browser has not been created, create the browser
		// and update it with the last generation results
		//--------------------------------------------------------
		if( _browser == null || _bUpdateRequired == true )
			updateBrowser( _generationResult );
	}
}
