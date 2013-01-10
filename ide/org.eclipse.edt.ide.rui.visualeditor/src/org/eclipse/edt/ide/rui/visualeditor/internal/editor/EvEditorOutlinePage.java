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

import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPart;
import org.eclipse.edt.ide.ui.internal.editor.EGLEditor;
import org.eclipse.edt.ide.ui.internal.outline.OutlinePage;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.PageBook;

/**
 * This class creates a composite with a stack layout.  The composite children are the
 * composites of the EGL source outline page, and the design page outline page.
 */
public class EvEditorOutlinePage extends OutlinePage {

	public final static int				DESIGN_PAGE		= 0;
	public final static int				SOURCE_PAGE		= 1;
	public final static int				PREVIEW_PAGE	= 3;

	protected Control				_controlDesign	= null;
	protected Control				_controlSource	= null;
	protected Control				_controlPrevew	= null;
	protected EvDesignOutlinePage	_outlineDesign	= null;
	protected PageBook				_pageBook		= null;
	/**
	 * Constructs the EGL outline page super class.  The design outline page is added when the control is created.
	 */
	public EvEditorOutlinePage( IEGLDocument document, String strOutlineContextMenuID, EGLEditor eglEditor, EvDesignOutlinePage outlineDesign ) {
		super( document, strOutlineContextMenuID, eglEditor );

		_outlineDesign = outlineDesign;
	}
	
	/**
	 * Adds selection listeners to both the source and design page outline views. 
	 */
	public void addSelectionChangedListener( ISelectionChangedListener listener ) {
		if( _outlineDesign == null )
			return;
		
		_outlineDesign.addSelectionChangedListener( listener );
		super.addSelectionChangedListener( listener );
	}

	/**
	 * Creates the super EGL outline page class' content.  Then adds the design page's outline page content to
	 * the parent page book.
	 */
	public void createControl( Composite compositeParent ) {
		_pageBook = (PageBook)compositeParent;

		// Create the content control for the design outline
		//--------------------------------------------------
		if( _outlineDesign != null )
			_outlineDesign.createControl( compositeParent );
		
		// Create the content control for the source outline
		//--------------------------------------------------
		super.createControl( compositeParent );

		// Obtain the created design outline and source outline controls
		// This will allow us to tell the parent page book to show the page we want to show
		//---------------------------------------------------------------------------------
		if( _outlineDesign == null )
			return;
		
		_controlDesign = _outlineDesign.getControl();
		_controlSource = super.getControl();
		_controlPrevew = new Composite( compositeParent, SWT.NORMAL );
	}

	/**
	 * Overrides source outline page superclass to dispose of the design outline page.
	 */
	public void dispose() {
		// Dispose the design outline page
		//--------------------------------
		if( _controlDesign != null && _controlDesign.isDisposed() == false )
			_controlDesign = _outlineDesign.getControl();

		if( _outlineDesign != null )
			_outlineDesign.dispose();
		
		// Dispose the source outline page
		// This disposes of three handles
		//-----------------------------------
		if( _controlSource != null && _controlSource.isDisposed() == false )
			_controlSource.dispose();
		
		if( _controlPrevew != null && _controlPrevew.isDisposed() == false )
			_controlPrevew.dispose();
		
		super.dispose();
	}
	
	/**
	 * This is called when the editor is opened.
	 */
	public Control getControl() {
		return _controlDesign;
	}

	/**
	 * Removes selection listeners from both the source and design page outline views. 
	 */
	public void removeSelectionChangedListener( ISelectionChangedListener listener ) {
		_outlineDesign.removeSelectionChangedListener( listener );
		super.removeSelectionChangedListener( listener );
	}

	/**
	 * Called by the editor to show the outline page denoted by the static constant as defined above.
	 */
	public void showPage( int iPage ) {
		if( _pageBook != null ) {
			switch ( iPage ) {
				case DESIGN_PAGE:
					_pageBook.showPage( _controlDesign );
					break;
				case SOURCE_PAGE:
					_pageBook.showPage( _controlSource );
					break;
				case PREVIEW_PAGE:
					_pageBook.showPage( _controlPrevew );
					break;
			}
		}
		
		// Set the visibility of the source page's outline view tool bar controls
		// based on the page that is about to be shown
		//-----------------------------------------------------------------------
		IActionBars actionBars = getSite().getActionBars();
		if( actionBars == null )
			return;
		
		IToolBarManager toolBarManager = actionBars.getToolBarManager();
		if( toolBarManager == null )
			return;
		
		IContributionItem[] items = toolBarManager.getItems();
		if( items == null )
			return;
		
		for( int i=0; i<items.length; ++i )
			items[i].setVisible( iPage == SOURCE_PAGE );

		toolBarManager.update( true );
	}
	
	/**
	 * Called by the editor when the widget descriptor registry has changed. 
	 */
	public void update(){
		_outlineDesign.update();
	}
	
	/**
	 * Notifies the property page and outline view.
	 */
	public void widgetSelectedFromDesignPage( WidgetPart widget ) {
		if( widget != null ){
			StructuredSelection selection = new StructuredSelection( widget );
			_outlineDesign.setSelection( selection );
		}
		else
			_outlineDesign.setSelection( new StructuredSelection() );
	}
}
