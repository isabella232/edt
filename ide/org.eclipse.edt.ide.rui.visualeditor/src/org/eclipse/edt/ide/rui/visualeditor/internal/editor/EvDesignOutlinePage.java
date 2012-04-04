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

import org.eclipse.edt.ide.rui.visualeditor.internal.outline.WidgetDragListener;
import org.eclipse.edt.ide.rui.visualeditor.internal.outline.WidgetDropAdapter;
import org.eclipse.edt.ide.rui.visualeditor.internal.outline.WidgetTransfer;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPart;
import org.eclipse.edt.ide.rui.visualeditor.plugin.Activator;
import org.eclipse.gef.dnd.TemplateTransfer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;


public class EvDesignOutlinePage extends ContentOutlinePage implements KeyListener {


	protected EvDesignPage		_designPage					= null;
	protected WidgetPart		_model						= null;
	protected boolean			_bProcessingSelectionChange	= false;

	protected EvEditorOutlinePage _editorOutlinePage		= null;

	/**
	 * Constructor.
	 */
	public EvDesignOutlinePage( EvDesignPage editor ) {
		super();

		_designPage = editor;
	}

	/**
	 * Creates the visibile user interface.
	 */
	public void createControl( Composite parent ) {
		super.createControl( parent );

		// Create actions
		//---------------
		/*
				_actionRefreshView = new ISeriesEditorRefreshAction();
				IActionBars actionBars = getSite().getActionBars();
				IToolBarManager toolBar = actionBars.getToolBarManager();
				toolBar.add( _actionRefreshView );
		*/
		// Create a tree viewer for drag and drop
		//---------------------------------------
		//	treeViewer = new LanguageTreeViewer(new Tree(parent, SWT.BORDER + SWT.MULTI), this);
		TreeViewer treeViewer = getTreeViewer();

		//add drag and drop support
		int ops = DND.DROP_MOVE;
		WidgetTransfer transfer = new WidgetTransfer( _designPage.getWidgetManager() );
		Transfer[] transfers = new Transfer[] { transfer };
		treeViewer.addDragSupport( ops, transfers, new WidgetDragListener( treeViewer, transfer ) );
		transfers = new Transfer[] { transfer, TemplateTransfer.getInstance() };
		WidgetDropAdapter dropAdapter = new WidgetDropAdapter( treeViewer, _designPage, transfer, _designPage._overlay );
		treeViewer.addDropSupport( ops, transfers, dropAdapter );

		
		// Add tree viewer listeners
		//--------------------------
		treeViewer.addSelectionChangedListener( this );
		treeViewer.getTree().addKeyListener(this);

		// Create content providers for the tree viewer
		//---------------------------------------------
		EvDesignOutlineProvider provider = new EvDesignOutlineProvider();
		treeViewer.setContentProvider( provider );
		treeViewer.setLabelProvider( provider );

		// Set initial expansion level
		//----------------------------
		treeViewer.setAutoExpandLevel( 4 );

		// Give the root element to the tree viewer
		//-----------------------------------------
		_model = _designPage.getWidgetManager().getWidgetRoot();

		if( _model != null ) {
			treeViewer.setInput( _model );
			ISelection selection = new StructuredSelection( _model );
			setSelection( selection );
		}
		
		createContextMenu();
	}
	
	protected void createContextMenu() {

		// Create menu manager
		MenuManager manager = new MenuManager( "", Activator.PLUGIN_ID + ".outline.manu" );

		// set menu manager options and add listener
		manager.setRemoveAllWhenShown(true);
		manager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				contextMenuAboutToShow(manager);
			}
		});

		// Register menu manager (needs to be after listeners are added)
	
		_editorOutlinePage.getSite().registerContextMenu( Activator.PLUGIN_ID + ".outline", manager, getTreeViewer() ); //$NON-NLS-1$

		// Create context menu
		Control c = getTreeViewer().getControl();
		Menu menu = manager.createContextMenu(c);
		c.setMenu(menu);
	}
	
	protected void contextMenuAboutToShow(IMenuManager menu) {
		IStructuredSelection selection = (IStructuredSelection) getSelection();
		boolean actionEnabled = (selection!= null && selection.size() == 1);
		
		Action deleteAction = _designPage._overlay._actionWidgetDelete;
		menu.add( deleteAction );
		
		menu.add(new Separator());
		
		Action propertyAction = _designPage._overlay._actionWidgetProperties;
		menu.add( propertyAction );
		propertyAction.setEnabled( actionEnabled );
	}

	/**
	 * Overrides the super class because it doesn't check for widgetDisposed.
	 */
	public ISelection getSelection() {
		if( getTreeViewer().getTree() != null && getTreeViewer().getTree().isDisposed() == false ) {
			return getTreeViewer().getSelection();
		}

		return null;
	}

	/**
	 * 
	 */
	public void partSelected( WidgetPart nodeSelected, String strSource ) {
		StructuredSelection selection = new StructuredSelection( nodeSelected );
		removeSelectionChangedListener( this );
		setSelection( selection );
		addSelectionChangedListener( this );
	}

	
	/**
	 * 
	 *
	 */
	public void selectView() {
		TreeViewer treeViewer = getTreeViewer();

		TreeItem[] items0 = treeViewer.getTree().getItems();
		TreeItem[] items1 = items0[ 0 ].getItems();

		TreeItem[] itemsSelect = new TreeItem[1];

		// View
		//-----
		if( items1.length > 0 )
			itemsSelect[ 0 ] = items1[ 0 ];

		// Form
		//-----
		else
			itemsSelect[ 0 ] = items0[ 0 ];

		treeViewer.getTree().setSelection( itemsSelect );
	}

	/**
	 * 
	 */
	public void setEnabled( boolean bEnable ) {

		if( getTreeViewer() != null ) {
			Control control = getTreeViewer().getControl();

			if( control.isDisposed() == false ) {
				control.setEnabled( bEnable );
			}
		}
	}

	/**
	 * Sets the input of the outline page
	 */
	public void setInput( Object input ) {
		if( input instanceof WidgetPart ) {
			_model = (WidgetPart)input;
		}

		update();
	}

	/**
	 * Overrides the super class because it doesn't check for widgetDisposed.
	 */
	public void setSelection( ISelection selection ) {

//		System.out.println( "DesignEditorContentOutlinePage.setSelection " + selection );

		if( getTreeViewer() == null )
			return;

		if( getTreeViewer().getTree() != null && getTreeViewer().getTree().isDisposed() == false ) {
			getTreeViewer().setSelection( selection );
		}
	}

	/**
	 * Updates the outline page.
	 */
	public void update() {
		if( getTreeViewer() != null ) {
			Control control = getTreeViewer().getControl();

			if( control.isDisposed() == false ) {
				control.setRedraw( false );
				_model = _designPage.getWidgetManager().getWidgetRoot();

				getTreeViewer().setInput( _model );
				
				// Update the selection
				//---------------------
				WidgetPart part = _designPage.getEditor().getWidgetSelected();
				if( part != null ){
					StructuredSelection selection = new StructuredSelection( part );
					setSelection( selection );
				}

				control.setRedraw( true );
			}
		}
	}

	public void keyPressed(KeyEvent keyEvent) {
		if( keyEvent.keyCode == SWT.DEL ){
			_designPage._overlay._actionWidgetDelete.run();
		}
	}

	public void keyReleased(KeyEvent keyEvent) {
		// do nothing
	}
	
}
