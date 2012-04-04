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

import org.eclipse.edt.ide.ui.internal.editor.EGLEditorActionContributor;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;

/**
 * The multiple page editor has three child pages, each with its own editor.
 * This multiple page editor action bar contributor initializes each child editor's
 * action bar contributor, and handles adding and removing contributions as the
 * this editor changes pages to one of the child editors.  The source page is an
 * EGLEditor as defined in its own plugin.  Since the child source editor is instantiated
 * by this editor, rather than by Eclipse, the action bar contributor defined in the EGL
 * editor's plugin.xml file is not automatically created.  Therefore, an instance is
 * created in this class' constructor, and initialized when this class is initialized.
 * This usage of this class is declared in this project's plugin.xml file and is automatically created.
 */
public class EvEditorActionBarContributor extends MultiPageEditorActionBarContributor {

	protected EvDesignActionBarContributor	_contributorDesignPage	= null;
	protected EvPreviewActionBarContributor	_contributorPreviewPage	= null;
	protected EGLEditorActionContributor 	_contributorSourcePage = null;
	/**
	 * 
	 */
	public EvEditorActionBarContributor() {
		// Create contributors for the design, source and preview pages
		// Since we create the EGLEditor instance ourselves, its contributor
		// defined in plugin.xml does not automatically get created
		//------------------------------------------------------------------
		_contributorDesignPage = new EvDesignActionBarContributor();
		_contributorSourcePage = new EGLEditorActionContributor();
		_contributorPreviewPage = new EvPreviewActionBarContributor();
	}

	/**
	 * 
	 */
	public void contributeToCoolBar( ICoolBarManager coolbarManager ) {
	}

	/**
	 * 
	 */
	public void contributeToMenu( IMenuManager menuManager ) {
		_contributorSourcePage.contributeToMenu( menuManager );
	}

	/**
	 * 
	 */
	public void contributeToStatusLine( IStatusLineManager statusLineManager ) {
		_contributorSourcePage.contributeToStatusLine( statusLineManager );
	}

	/**
	 * 
	 */
	public void contributeToToolBar( IToolBarManager toolBarManager ) {
		// Do not call the source contributor
		// Otherwise, two toolbar items appear
		//------------------------------------
	}

	/**
	 * Calls the sub-editors init methods.
	 */
	public void init( IActionBars bars, IWorkbenchPage page ) {
		_contributorSourcePage.init( bars, page );
		_contributorDesignPage.init( bars, page );
		_contributorPreviewPage.init( bars, page );
		
		// This must be called to store the page
		//--------------------------------------
		super.init( bars, page );
	}

	/**
	 * This is called when the user selects one of the editor's tabs (Design, Source, Preview).
	 * The editor is the editor page that the user has turned to. 
	 */
	public void setActivePage( IEditorPart editor ) {
		_contributorSourcePage.setActiveEditor( editor );
		_contributorDesignPage.setActiveEditor( editor );
		_contributorPreviewPage.setActiveEditor( editor );
	}
}
