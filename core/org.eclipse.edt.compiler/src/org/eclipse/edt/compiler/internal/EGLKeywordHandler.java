/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
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

import java.util.HashSet;

/**
 * @author jshavor
 *
 * This is the public interface to for all EGL keywords
 */
public class EGLKeywordHandler {

	// IF YOU MODIFY THIS LIST YOU NEED TO:
	// 1 - make sure this is insync with error.flex in org.eclipse.edt.compiler.internal.pgm.errors (see EGLFlex2EGLKeywordTool)
	//     EXCEPTIONS - need to have: and, or, no, yes, true, false, self
	// 2 - regen the constants in IEGLConstants using org.eclipse.edt.compiler.internal.dev.tools.EGLKeywordTool
	// 3 - regen the code in EGLDefinedKeywordCompletions.getDefinedKeywordCompletions() using
	//     org.eclipse.edt.compiler.internal.dev.tools.EGLDefinedKeywordCompletionTool
	// 4 - regen the HashSet in this class using org.eclipse.edt.compiler.internal.dev.tools.EGLKeywordHashSetTool
	// 5 - post a note in the Team Room so everyone is aware.  VG to EGL migration can't use this list,
	//     so they need to make appropriate analogous changes.
	// 6 - since String constants are generated from these, you may need to capitalize
	//     some of the letters if the keyword is multi-word.  (see bigInt below)
	//	

	
	/**
	 * @return
	 */
	public static String[] getKeywordNames() {
		return org.eclipse.edt.compiler.core.EGLKeywordHandler.getKeywordNames();
	}
	
	
	/**
	 * return the List of EGL keyword names in lowercase
	 */
	public static String[] getKeywordNamesToLowerCase() {
		return org.eclipse.edt.compiler.core.EGLKeywordHandler.getKeywordNamesToLowerCase();
	}

	/**
	 * @return
	 */
	public static HashSet getKeywordHashSet() {
		return org.eclipse.edt.compiler.core.EGLKeywordHandler.getKeywordHashSet();
	}

}
