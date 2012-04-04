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


/**
 * An action that deletes a widget from the overlay.
 */
public class EvActionWidgetDelete extends Action {

	protected EvDesignOverlay	_designOverlay	= null;

	/**
	 * 
	 */
	public EvActionWidgetDelete( EvDesignOverlay designOverlay ){
		_designOverlay = designOverlay;
	}
	
	/**
	 * Declared in IAction.  Tells the overlay to delete the widget.
	 */
	public void run() {
		_designOverlay.deleteSelected();
	}
}
