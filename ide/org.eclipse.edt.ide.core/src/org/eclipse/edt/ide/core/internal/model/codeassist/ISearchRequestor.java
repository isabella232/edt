/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.model.codeassist;

/**
 * This is the internal requestor passed to the searchable name environment
 * so as to process the multiple search results as they are discovered.
 *
 * It is used to allow the code assist engine to add some more information
 * to the raw name environment results before answering them to the UI.
 */
public interface ISearchRequestor {
	
	/**
	 * One result of the search consists of a new part.
	 *
	 * NOTE - All package and part names are presented in their readable form:
	 *    Package names are in the form "a.b.c".
	 *    Nested type names are in the qualified form "A.M".
	 *    The default package is represented by an empty array.
	 */
	public void acceptPart(char[] packageName, char[] typeName, int modifiers);

	/**
	 * One result of the search consists of a new package.
	 *
	 * NOTE - All package names are presented in their readable form:
	 *    Package names are in the form "a.b.c".
	 *    The default package is represented by an empty array.
	 */
	public void acceptPackage(char[] packageName);

}
