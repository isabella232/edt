/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.internal.testserver;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.edt.ide.testserver.TestServerPlugin;
import org.eclipse.edt.ide.testserver.ITestServerPreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Initializes the default preferences.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {
	
	public void initializeDefaultPreferences() {
		IPreferenceStore store = TestServerPlugin.getDefault().getPreferenceStore();
		store.setDefault(ITestServerPreferenceConstants.PREFERENCE_TESTSERVER_ENABLE_DEBUG, false);
		store.setDefault(ITestServerPreferenceConstants.PREFERENCE_TESTSERVER_HCR_FAILED, ITestServerPreferenceConstants.TESTSERVER_PROMPT);
		store.setDefault(ITestServerPreferenceConstants.PREFERENCE_TESTSERVER_HCR_UNSUPPORTED, ITestServerPreferenceConstants.TESTSERVER_PROMPT);
		store.setDefault(ITestServerPreferenceConstants.PREFERENCE_TESTSERVER_OBSOLETE_METHODS, ITestServerPreferenceConstants.TESTSERVER_PROMPT);
		store.setDefault(ITestServerPreferenceConstants.PREFERENCE_TESTSERVER_CLASSPATH_CHANGED, ITestServerPreferenceConstants.TESTSERVER_PROMPT);
	}
}
