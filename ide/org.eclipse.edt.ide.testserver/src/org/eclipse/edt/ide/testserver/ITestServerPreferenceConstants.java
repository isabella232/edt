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
package org.eclipse.edt.ide.testserver;


public interface ITestServerPreferenceConstants {
	
	public static final String PREFERENCE_TESTSERVER_ENABLE_DEBUG = TestServerPlugin.PLUGIN_ID + ".EnableDebug"; //$NON-NLS-1$
	
	public static final String PREFERENCE_TESTSERVER_OBSOLETE_METHODS = TestServerPlugin.PLUGIN_ID + ".ObsoleteMethods"; //$NON-NLS-1$
	public static final String PREFERENCE_TESTSERVER_HCR_FAILED = TestServerPlugin.PLUGIN_ID + ".HCRFailed"; //$NON-NLS-1$
	public static final String PREFERENCE_TESTSERVER_HCR_UNSUPPORTED = TestServerPlugin.PLUGIN_ID + ".HCRUnsupported"; //$NON-NLS-1$
	public static final String PREFERENCE_TESTSERVER_CLASSPATH_CHANGED = TestServerPlugin.PLUGIN_ID + ".ClasspathChanged"; //$NON-NLS-1$
	
	// the following are the values for the above preferences, but not preferences themselves
	public static final int TESTSERVER_PROMPT = 0;
	public static final int TESTSERVER_TERMINATE = 1;
	public static final int TESTSERVER_IGNORE = 2;
	
	// Help context IDs
	public static final String RUI_TEST_SERVER_PREFERENCE_PAGE = TestServerPlugin.PLUGIN_ID + ".testserver0001"; //$NON-NLS-1$
}
