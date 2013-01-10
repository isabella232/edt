/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
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

/**
 * Constants used for the test server's preferences.
 */
public interface ITestServerPreferenceConstants {
	
	/**
	 * Preference key for the boolean value indicating if debug messages should be printed in the test server's console.
	 */
	public static final String PREFERENCE_TESTSERVER_ENABLE_DEBUG = TestServerPlugin.PLUGIN_ID + ".EnableDebug"; //$NON-NLS-1$
	
	/**
	 * Preference key for the integer value indicating what to do when obsolete methods are found by hot code replace.
	 * @see #TESTSERVER_IGNORE
	 * @see #TESTSERVER_PROMPT
	 * @see #TESTSERVER_TERMINATE
	 */
	public static final String PREFERENCE_TESTSERVER_OBSOLETE_METHODS = TestServerPlugin.PLUGIN_ID + ".ObsoleteMethods"; //$NON-NLS-1$
	
	/**
	 * Preference key for the integer value indicating what to do when hot code replace fails.
	 * @see #TESTSERVER_IGNORE
	 * @see #TESTSERVER_PROMPT
	 * @see #TESTSERVER_TERMINATE
	 */
	public static final String PREFERENCE_TESTSERVER_HCR_FAILED = TestServerPlugin.PLUGIN_ID + ".HCRFailed"; //$NON-NLS-1$
	
	/**
	 * Preference key for the integer value indicating what to do when hot code replace is not supported by the vm.
	 * @see #TESTSERVER_IGNORE
	 * @see #TESTSERVER_PROMPT
	 * @see #TESTSERVER_TERMINATE
	 */
	public static final String PREFERENCE_TESTSERVER_HCR_UNSUPPORTED = TestServerPlugin.PLUGIN_ID + ".HCRUnsupported"; //$NON-NLS-1$
	
	/**
	 * Preference key for the integer value indicating what to do when the classpath of the server has changed.
	 * @see #TESTSERVER_IGNORE
	 * @see #TESTSERVER_PROMPT
	 * @see #TESTSERVER_TERMINATE
	 */
	public static final String PREFERENCE_TESTSERVER_CLASSPATH_CHANGED = TestServerPlugin.PLUGIN_ID + ".ClasspathChanged"; //$NON-NLS-1$
	
	/**
	 * Value for the preferences that indicates that the user should be prompted.
	 */
	public static final int TESTSERVER_PROMPT = 0;
	
	/**
	 * Value for the preferences that indicates that the user should not be prompted and the server should be terminated.
	 */
	public static final int TESTSERVER_TERMINATE = 1;
	
	/**
	 * Value for the preferences that indicates that the condition should be ignored.
	 */
	public static final int TESTSERVER_IGNORE = 2;
	
	/**
	 * Help ID for the preference page.
	 */
	public static final String TEST_SERVER_PREFERENCE_PAGE_HELP_ID = TestServerPlugin.PLUGIN_ID + ".testserver0001"; //$NON-NLS-1$
}
