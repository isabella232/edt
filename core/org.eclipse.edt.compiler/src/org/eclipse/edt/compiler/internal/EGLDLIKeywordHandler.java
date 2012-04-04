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

/**
 * @author jshavor
 *
 * This is the public interface to for all EGL DLI keywords
 */
public class EGLDLIKeywordHandler {
	// ***IF YOU MODIFY THIS LIST YOU NEED TO REGEN THE CONSTANTS IN IEGLConstants USING***
	// org.eclipse.edt.compiler.internal.dev.tools.EGLDLIKeywordTool

	private static String[] dliKeywordNames =
		{
			//Jon - needs to be expanded
			"dli", //$NON-NLS-1$

			"clse", //$NON-NLS-1$
			"deq", //$NON-NLS-1$
			"dlet", //$NON-NLS-1$
			"fld", //$NON-NLS-1$
			"ghn", //$NON-NLS-1$
			"ghnp", //$NON-NLS-1$
			"ghu", //$NON-NLS-1$
			"gn", //$NON-NLS-1$
			"gnp", //$NON-NLS-1$
			"gu", //$NON-NLS-1$
			"isrt", //$NON-NLS-1$
			"open", //$NON-NLS-1$
			"pos", //$NON-NLS-1$
			"repl", //$NON-NLS-1$
			
			"chkp", //$NON-NLS-1$
			"gmsg", //$NON-NLS-1$
			"gscd", //$NON-NLS-1$
			"icmd", //$NON-NLS-1$
			"init", //$NON-NLS-1$
			"inqy", //$NON-NLS-1$
			"log", //$NON-NLS-1$
			"rcmd", //$NON-NLS-1$
			"rolb", //$NON-NLS-1$
			"roll", //$NON-NLS-1$
			"rols", //$NON-NLS-1$
			"sets", //$NON-NLS-1$
			"setu", //$NON-NLS-1$
			"snap", //$NON-NLS-1$
			"stat", //$NON-NLS-1$
			"sync", //$NON-NLS-1$
			"term", //$NON-NLS-1$
			"xrst", //$NON-NLS-1$
			};

	public static String[] getDLIKeywordNames() {
		return dliKeywordNames;
	}

	/**
	 * return the array of DLI keyword names in lowercase
	 */
	public static String[] getDLIKeywordNamesToLowerCase() {
		String[] lowercaseKeywordsNames = new String[dliKeywordNames.length];
		for (int i = 0; i < lowercaseKeywordsNames.length; i++) {
			lowercaseKeywordsNames[i] = dliKeywordNames[i].toLowerCase();
		}
		return lowercaseKeywordsNames;
	}

	/**
	 * return the List of DLI keyword names in lowercase
	 */
//	public static ArrayList getDLIKeywordNamesToLowerCaseAsArrayList() {
//		ArrayList lowercaseKeywordsNames = new ArrayList();
//		for (int i = 0; i < dliKeywordNames.length; i++) {
//			lowercaseKeywordsNames.add(dliKeywordNames[i].toLowerCase());
//		}		
//		return lowercaseKeywordsNames;
//	}

}
