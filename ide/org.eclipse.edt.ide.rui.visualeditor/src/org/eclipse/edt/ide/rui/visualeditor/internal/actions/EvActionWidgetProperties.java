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
package org.eclipse.edt.ide.rui.visualeditor.internal.actions;

import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvDesignOverlay;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;


public class EvActionWidgetProperties extends Action {
	
	protected EvDesignOverlay	_designOverlay	= null;

	/**
	 * 
	 */
	public EvActionWidgetProperties( EvDesignOverlay designOverlay ) {
		_designOverlay = designOverlay;
	}

	/**
	 * Declared in IAction.
	 */
	public void run() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		if( workbench == null )
			return;

		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		if( window == null )
			return;

		IWorkbenchPage page = window.getActivePage();
		if( page == null )
			return;

		try {
			page.showView( "org.eclipse.ui.views.PropertySheet" );
		}
		catch( PartInitException ex ) {
		}
	}
}
