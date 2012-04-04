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
package org.eclipse.edt.ide.ui.bidi;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IStorageEditorInput;

/**
 * Contributors to the
 * <code>org.eclipse.edt.ide.ui.bidiProvider</code> extension
 * point must specify an implementation of this interface which will present a BIDI Visual Editor
 * popup dialog as well as provide metadata about the current bidi language.
 * <p>
 * Clients may implement this interface.
 * </p>
 *
 * @since 3.0
 */

public interface IBIDIProvider {
	/**
	 * Installs this BIDI provider on the given control.
	 * 
	 * @param control the Control that this provider works on
	 */
	public abstract void install(IStorageEditorInput input, Control control);
	
	public abstract Text createText(IStorageEditorInput input, Composite parent, int style, boolean useVisualMode, boolean useRTLMode, boolean allowVisualLogicalSwitching);
	

	/**
	 * Uninstalls this bidi provider. Any references to editors 
	 * should be cleared.
	 */
	public abstract void uninstall();

	/**
	 * (Re-)initializes the structure provided by the receiver.
	 */
	public abstract void initialize();
}	

