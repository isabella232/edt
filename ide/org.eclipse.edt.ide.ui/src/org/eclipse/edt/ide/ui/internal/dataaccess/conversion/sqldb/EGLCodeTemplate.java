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

package org.eclipse.edt.ide.ui.internal.dataaccess.conversion.sqldb;

import org.eclipse.osgi.util.NLS;

public class EGLCodeTemplate extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.edt.ide.ui.internal.dataaccess.conversion.sqldb.EGLCodeTemplate"; //$NON-NLS-1$

	private EGLCodeTemplate() {
		// Do not instantiate
	}

	static {
		NLS.initializeMessages(BUNDLE_NAME, EGLCodeTemplate.class);
	}

	public static String mdd_recordPackageDefine;
	public static String mdd_statusRecord;
	public static String mdd_ControlStructures;
	public static String mdd_conditionHandlingLib;
	public static String mdd_addMethods;
	public static String mdd_getmethod;
	public static String mdd_updatemethod;
	public static String mdd_deleteMethod;
	public static String mdd_existMethod;
	public static String mdd_isValidmethod;
	public static String mdd_service_serviceHeader;
	public static String mdd_library_libraryHeader;
	public static String mdd_sql_recordHeader;
}
