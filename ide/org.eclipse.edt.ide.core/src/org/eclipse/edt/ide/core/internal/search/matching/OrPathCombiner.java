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

import java.util.HashSet;

import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.ide.core.internal.search.IIndexSearchRequestor;

public class OrPathCombiner implements IIndexSearchRequestor {

	IIndexSearchRequestor targetRequestor;
	HashSet acceptedAnswers = new HashSet(5);
public OrPathCombiner(IIndexSearchRequestor targetRequestor){
	this.targetRequestor = targetRequestor;
}

public void acceptPartDeclaration(IPath projectPath, String resourcePath, char[] simplePartName, char partType, char[][] enclosingTypeNames, char[] packageName){

	if (this.acceptedAnswers.add(resourcePath)){
		this.targetRequestor.acceptPartDeclaration(null, resourcePath, simplePartName, partType, enclosingTypeNames, packageName);
	}
}
//public void acceptConstructorDeclaration(String resourcePath, char[] typeName, int parameterCount) {
//	if (this.acceptedAnswers.add(resourcePath)){
//		this.targetRequestor.acceptConstructorDeclaration(resourcePath, typeName, parameterCount);
//	}		
//}
//public void acceptConstructorReference(String resourcePath, char[] typeName, int parameterCount) {
//	if (this.acceptedAnswers.add(resourcePath)){
//		this.targetRequestor.acceptConstructorReference(resourcePath, typeName, parameterCount);
//	}			
//}
//public void acceptFieldDeclaration(String resourcePath, char[] fieldName) {
//	if (this.acceptedAnswers.add(resourcePath)){
//		this.targetRequestor.acceptFieldDeclaration(resourcePath, fieldName);
//	}	
//}
//public void acceptFieldReference(String resourcePath, char[] fieldName) {
//	if (this.acceptedAnswers.add(resourcePath)){
//		this.targetRequestor.acceptFieldReference(resourcePath, fieldName);
//	}		
//}
//public void acceptInterfaceDeclaration(String resourcePath, char[] simpleTypeName, char[][] enclosingTypeNames, char[] packageName) {
//	if (this.acceptedAnswers.add(resourcePath)){
//		this.targetRequestor.acceptInterfaceDeclaration(resourcePath, simpleTypeName, enclosingTypeNames, packageName);
//	}		
//}
public void acceptFunctionDeclaration(String resourcePath, char[] methodName, int parameterCount) {
	if (this.acceptedAnswers.add(resourcePath)){
		this.targetRequestor.acceptFunctionDeclaration(resourcePath, methodName, parameterCount);
	}		
}
public void acceptFunctionReference(String resourcePath, char[] methodName, int parameterCount) {
	if (this.acceptedAnswers.add(resourcePath)){
		this.targetRequestor.acceptFunctionReference(resourcePath, methodName, parameterCount);
	}			
}
public void acceptPackageReference(String resourcePath, char[] packageName) {
	if (this.acceptedAnswers.add(resourcePath)){
		this.targetRequestor.acceptPackageReference(resourcePath, packageName);
	}	
}
public void acceptPartReference(String resourcePath, char[] typeName) {
	if (this.acceptedAnswers.add(resourcePath)){
		this.targetRequestor.acceptPartReference(resourcePath, typeName);
	}
}
}
