/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.handlers.folding;

import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.EDTUIPreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;

public class FoldingToggleHandler extends FoldingHandler {
	
	public void run() {
		IPreferenceStore store = EDTUIPlugin.getDefault().getPreferenceStore();
		boolean current= store.getBoolean(EDTUIPreferenceConstants.EDITOR_FOLDING_ENABLED);
		store.setValue(EDTUIPreferenceConstants.EDITOR_FOLDING_ENABLED, !current);
	}
	
	public boolean isEnabled(){
		return(true);
	}
	
}
