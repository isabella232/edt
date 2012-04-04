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
package org.eclipse.edt.ide.rui.editor;

public interface IEditorSelectAndRevealer {

	/**
	 * Selects and reveals the specified line in this text editor.
	 *
	 * @param line the line number to select
	 */
	void selectAndRevealLine(int line);
	
}
