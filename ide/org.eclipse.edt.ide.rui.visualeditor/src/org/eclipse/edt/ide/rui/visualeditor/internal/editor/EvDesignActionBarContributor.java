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

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.EditorActionBarContributor;

/**
 * This is the contributor for the design page editor.  It is instantiated and called by the editor's multi-page
 * action bar contributor.
 */
public class EvDesignActionBarContributor extends EditorActionBarContributor {

	/**
	 * Replaces the editor's menu bar delete action handler with our own. 
	 */
	public void setActiveEditor( IEditorPart part ) {
		if( part instanceof EvDesignPage == false )
			return;

		IActionBars actionBars = getActionBars();
		if( actionBars == null )
			return;
		
		EvDesignPage designPage = (EvDesignPage)part;

		// Delete
		//-------
		IAction actionDelete = designPage.getAction( ActionFactory.DELETE.getId() );
		actionBars.setGlobalActionHandler( ActionFactory.DELETE.getId(), actionDelete );
		
		// Use the source page's revert action
		//------------------------------------
		actionBars.setGlobalActionHandler( ActionFactory.REVERT.getId(), designPage.getEditor().getPageSource().getAction( ActionFactory.REVERT.getId() ) );		
		actionBars.setGlobalActionHandler( ActionFactory.UNDO.getId(), designPage.getEditor().getPageSource().getAction( ActionFactory.UNDO.getId() ) );
		actionBars.setGlobalActionHandler( ActionFactory.REDO.getId(), designPage.getEditor().getPageSource().getAction( ActionFactory.REDO.getId() ) );

		actionBars.updateActionBars();
	}
}
