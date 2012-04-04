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
package org.eclipse.edt.ide.core.internal.search;

import org.eclipse.core.runtime.IPath;

public interface IIndexSearchRequestor {
/**
 * Accepts the declaration of a part in the EGL file with the given resource path.
 * The part is declared in the given package and with the given type name. 
 * <p>
 * Note that the resource path can be null if the search query doesn't require it (eg. get all class names).
 */
//add an IPath parameter, to pass the project info, which is used for external eglar file. 08/25/2010
void acceptPartDeclaration(IPath projectPath, String resourcePath, char[] simplePartName, char partType, char[][] enclosingPartNames, char[] packageName);
//void acceptPartDeclaration(String resourcePath, char[] simplePartName, char partType, char[][] enclosingPartNames, char[] packageName);
///**
// * Accepts the declaration of a field in the compilation unit with the given resource path.
// * <p>
// * Note that the resource path can be null if the search query doesn't require it (eg. get all class names).
// * Likewise, the declaring package name and the declaring type names if the query doesn't require them.
// */
//void acceptFieldDeclaration(String resourcePath, char[] fieldName);
///**
// * Accepts the reference to a field in the compilation unit with the given resource path.
// * The field is referenced using the given name 
// */
//void acceptFieldReference(String resourcePath, char[] fieldName);
/**
 * Accepts the declaration of a function in the compilation unit with the given resource path.
 * The function is declared with a given function name and number of arguments.
 */
void acceptFunctionDeclaration(String resourcePath, char[] functionName, int parameterCount);
/**
 * Accepts the reference to a function in the compilation unit with the given resource path.
 * The function is referenced using the given selector and a number of arguments.
 *
 * Note that the resource path can be null if the search query doesn't require it.
 */
void acceptFunctionReference(String resourcePath, char[] functionName, int parameterCount);
/**
 * Accepts the reference to a package in the egl file with the given resource path.
 * The package is referenced using the given package name.
 *
 * Note that the resource path can be null if the search query doesn't require it.
 */
void acceptPackageReference(String resourcePath, char[] packageName);
/**
 * Accepts the reference to a part in the egl file with the given resource path.
 * The part is referenced using the given type name.
 * <p>
 * Note that the resource path can be null if the search query doesn't require it.
 */
void acceptPartReference(String resourcePath, char[] partName);
}
