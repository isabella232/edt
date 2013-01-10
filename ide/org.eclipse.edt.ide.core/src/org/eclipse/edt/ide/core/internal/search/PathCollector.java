/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.search;

import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * Collects the resource paths reported by a client to this search requestor.
 */
public class PathCollector implements IIndexSearchRequestor {
	
	/* a set of resource paths */
	public HashSet paths = new HashSet(5);
/**
 * @see IIndexSearchRequestor
 */
public void acceptPartDeclaration(IPath projectPath, String resourcePath, char[] simpleTypeName, char partType, char[][] enclosingTypeNames, char[] packageName) {
	this.paths.add( resourcePath);
}
/**
 * @see IIndexSearchRequestor
 */
public void acceptFieldDeclaration(String resourcePath, char[] fieldName) {
	this.paths.add(resourcePath);
}
/**
 * @see IIndexSearchRequestor
 */
public void acceptFieldReference(String resourcePath, char[] fieldName) {
		
	this.paths.add(resourcePath);
}
/**
 * @see IIndexSearchRequestor
 */
public void acceptFunctionDeclaration(String resourcePath, char[] methodName, int parameterCount) {
		
	this.paths.add(resourcePath);
}
/**
 * @see IIndexSearchRequestor
 */
public void acceptFunctionReference(String resourcePath, char[] methodName, int parameterCount) {
		
	this.paths.add(resourcePath);
}
/**
 * @see IIndexSearchRequestor
 */
public void acceptPackageReference(String resourcePath, char[] packageName) {
		
	this.paths.add(resourcePath);
}
/**
 * @see IIndexSearchRequestor
 */
public void acceptPartReference(String resourcePath, char[] typeName) {
	this.paths.add(resourcePath);
}
/**
 * Returns the files that correspond to the paths that have been collected.
 */
public IFile[] getFiles(IWorkspace workspace) {
	IFile[] result = new IFile[this.paths.size()];
	int i = 0;
	for (Iterator iter = this.paths.iterator(); iter.hasNext();) {
		String resourcePath = (String)iter.next();
		IPath path = new Path(resourcePath);
		result[i++] = workspace.getRoot().getFile(path);
	}
	return result;
}
/**
 * Returns the paths that have been collected.
 */
public String[] getPaths() {
	String[] result = new String[this.paths.size()];
	int i = 0;
	for (Iterator iter = this.paths.iterator(); iter.hasNext();) {
		result[i++] = (String)iter.next();
	}
	return result;
}
}
