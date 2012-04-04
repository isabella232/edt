/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal;

import java.util.Set;

/**
 * @author fDollar
 *
 * This is the public interface to for all EGL SQL keywords
 */
public class EGLSQLKeywordHandler {

	// ***IF YOU MODIFY THIS LIST YOU NEED TO REGEN THE CONSTANTS IN IEGLConstants USING***
	// org.eclipse.edt.compiler.internal.dev.tools.EGLSQLKeywordTool
	// You also need to post a note in the Team Room so everyone is aware.
	// VG to EGL migration can't use this list, so they need to make 
	// appropriate analogous changes. 

	
	/**
	 * @return
	 */
	public static String[] getSQLKeywordNames() {
		return org.eclipse.edt.compiler.core.EGLSQLKeywordHandler.getSQLKeywordNames();
	}

	/**
	 * return the List of SQL keyword names in lowercase
	 */
	public static String[] getSQLKeywordNamesToLowerCase() {
		return org.eclipse.edt.compiler.core.EGLSQLKeywordHandler.getSQLKeywordNamesToLowerCase();
	}

	/**
	 * @return
	 */
	public static String[] getSQLClauseKeywordNames() {
		return org.eclipse.edt.compiler.core.EGLSQLKeywordHandler.getSQLClauseKeywordNames();
	}

	/**
	 * return the List of SQL keyword names in lowercase
	 */
	public static String[] getSQLClauseKeywordNamesToLowerCase() {
		return org.eclipse.edt.compiler.core.EGLSQLKeywordHandler.getSQLClauseKeywordNamesToLowerCase();
	}

	/**
	 * return the List of SQL keyword names as a comma separated string
	 */
	public static String getSQLClauseKeywordNamesCommaSeparatedString() {
		return org.eclipse.edt.compiler.core.EGLSQLKeywordHandler.getSQLClauseKeywordNamesCommaSeparatedString();
	}

	/**
	 * return the List of SQL keyword names in lowercase
	 */
	public static Set getSQLClauseKeywordNamesToLowerCaseAsSet() {	
		return org.eclipse.edt.compiler.core.EGLSQLKeywordHandler.getSQLClauseKeywordNamesToLowerCaseAsSet();
	}

}
