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
package org.eclipse.edt.ide.rui.visualeditor.internal.actions;

import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvDesignOverlay;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPart;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.IWorkbenchPart;


public class EvActionWidgetMove extends SelectionAction {
	public static final String ID = "move";
	
	protected EvDesignOverlay	_designOverlay	= null;

	/**
	 * 
	 */
	public EvActionWidgetMove( IWorkbenchPart part, EvDesignOverlay designOverlay ) {
		super( part );
		_designOverlay = designOverlay;
	}

	/**
	 * Declared in WorkbenchPartAction.  This has to be handled both here, and
	 * in the overlay's menuAboutToShow method in order to work consistently.
	 */
	protected boolean calculateEnabled() {
		WidgetPart widget = _designOverlay.getWidgetSelected();
		if( widget == null )
			return false;
		
		if( _designOverlay.isDraggable( widget.getTypeName() ) == false )
			return false;
		
		return true;
	}

	/**
	 * Declared in IAction.
	 */
	public void run() {
		_designOverlay.initializeMoveWidgetWithKeyboard();
	}
}
