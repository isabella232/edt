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

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

/**
 * Dummy edit part factory as part of the code that fools GEF into thinking
 * that this is a GEF editor
 */
public class EvEditPartFactory implements EditPartFactory {

	/**
	 * Does nothing.
	 */
	public EditPart createEditPart( EditPart arg0, Object arg1 ) {
		return null;
	}
}
