/*******************************************************************************
 * Copyright © 2008, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
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
package org.eclipse.edt.ide.rui.internal;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.edt.ide.rui.preferences.IRUIPreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;

public class RUIPReferenceInitializer extends AbstractPreferenceInitializer {

	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(IRUIPreferenceConstants.PREFERENCE_TESTSERVER_HCR_FAILED, IRUIPreferenceConstants.TESTSERVER_PROMPT);
		store.setDefault(IRUIPreferenceConstants.PREFERENCE_TESTSERVER_HCR_UNSUPPORTED, IRUIPreferenceConstants.TESTSERVER_PROMPT);
		store.setDefault(IRUIPreferenceConstants.PREFERENCE_TESTSERVER_OBSOLETE_METHODS, IRUIPreferenceConstants.TESTSERVER_PROMPT);
		store.setDefault(IRUIPreferenceConstants.PREFERENCE_TESTSERVER_CLASSPATH_CHANGED, IRUIPreferenceConstants.TESTSERVER_PROMPT);
	}
}
