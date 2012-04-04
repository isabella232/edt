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
package org.eclipse.edt.ide.core.internal.search.matching;

import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.compiler.internal.core.utils.CharOperation;
import org.eclipse.edt.ide.core.internal.model.util.HashtableOfObject;
import org.eclipse.edt.ide.core.internal.search.IIndexSearchRequestor;

public class OrNameCombiner implements IIndexSearchRequestor {

	IIndexSearchRequestor targetRequestor;
	HashtableOfObject acceptedAnswers = new HashtableOfObject(5);
		
public OrNameCombiner(IIndexSearchRequestor targetRequestor){
	this.targetRequestor = targetRequestor;
}

public void acceptPartDeclaration(IPath projectPath, String resourcePath, char[] simplePartName, char partType, char[][] enclosingTypeNames, char[] packageName){

	if (!this.acceptedAnswers.containsKey(CharOperation.concat(packageName, simplePartName, '.'))){
		this.targetRequestor.acceptPartDeclaration(null, resourcePath, simplePartName, partType, enclosingTypeNames, packageName);
	}
}
//public void acceptConstructorDeclaration(String resourcePath, char[] typeName, int parameterCount) {}
//public void acceptConstructorReference(String resourcePath, char[] typeName, int parameterCount) {}
//public void acceptFieldDeclaration(String resourcePath, char[] fieldName) {}
//public void acceptFieldReference(String resourcePath, char[] fieldName) {}
//public void acceptInterfaceDeclaration(String resourcePath, char[] simpleTypeName, char[][] enclosingTypeNames, char[] packageName) {}
public void acceptFunctionDeclaration(String resourcePath, char[] methodName, int parameterCount) {}
public void acceptFunctionReference(String resourcePath, char[] methodName, int parameterCount) {}
public void acceptPackageReference(String resourcePath, char[] packageName) {}
public void acceptPartReference(String resourcePath, char[] typeName) {}
}
