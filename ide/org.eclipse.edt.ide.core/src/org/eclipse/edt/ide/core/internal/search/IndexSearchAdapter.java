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

import org.eclipse.core.runtime.IPath;

public class IndexSearchAdapter implements IIndexSearchRequestor {
/**
 * @see IIndexSearchRequestor
 */
public void acceptPartDeclaration(IPath projectPath, String resourcePath, char[] simpleTypeName, char partType, char[][] enclosingTypeNames, char[] packageName) {
}
/**
 * @see IIndexSearchRequestor
 */
public void acceptFieldDeclaration(String resourcePath, char[] fieldName) {
}
/**
 * @see IIndexSearchRequestor
 */
public void acceptFieldReference(String resourcePath, char[] fieldName) {
}
/**
 * @see IIndexSearchRequestor
 */
public void acceptFunctionDeclaration(String resourcePath, char[] methodName, int parameterCount) {
}
/**
 * @see IIndexSearchRequestor
 */
public void acceptFunctionReference(String resourcePath, char[] methodName, int parameterCount) {
}
/**
 * @see IIndexSearchRequestor
 */
public void acceptPackageReference(String resourcePath, char[] packageName) {
}
/**
 * @see IIndexSearchRequestor
 */
public void acceptSuperTypeReference(String resourcePath, char[] qualification, char[] typeName, char[] enclosingTypeName, char classOrInterface, char[] superQualification, char[] superTypeName, char superClassOrInterface, int modifiers){
}
/**
 * @see IIndexSearchRequestor
 */
public void acceptPartReference(String resourcePath, char[] typeName) {
}
}
