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

import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.EditorActionBarContributor;

/**
 * This is the contributor for the preview page editor.  It is instantiated and called by the editor's multi-page
 * action bar contributor.
 */
public class EvPreviewActionBarContributor extends EditorActionBarContributor {
	
	/**
	 * Updates the action bar handler with our handlers.
	 */
	public void setActiveEditor( IEditorPart part ) {
		if( part instanceof EvPreviewPage == false )
			return;

		IActionBars actionBars = getActionBars();
		if( actionBars == null )
			return;
		
		EvPreviewPage designPage = (EvPreviewPage)part;

		// Revert: Use the source page's revert action
		//--------------------------------------------
		actionBars.setGlobalActionHandler( ActionFactory.REVERT.getId(), designPage.getEditor().getPageSource().getAction( ActionFactory.REVERT.getId() ) );		
		
		actionBars.updateActionBars();
	}
}
