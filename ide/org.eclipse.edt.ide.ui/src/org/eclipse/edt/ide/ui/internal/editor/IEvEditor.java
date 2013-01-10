/*******************************************************************************
 * Copyright © 2009, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.editor;

/**
 * Interface for the EvEditor so that code in the RUI plug-in can reference
 * the editor.
 */
public interface IEvEditor
{
	public void showSourcePage();
	public void selectAndReveal(int start, int length);
}
