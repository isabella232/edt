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
package org.eclipse.edt.ide.core.internal.model.indexing;

import org.eclipse.edt.ide.core.internal.model.ISourceElementRequestor;
import org.eclipse.edt.ide.core.internal.model.index.IDocument;
import org.eclipse.edt.ide.core.model.IIndexConstants;

import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.internal.core.utils.CharOperation;
import com.ibm.icu.util.StringTokenizer;

/**
 * This class is used by the EGLParserIndexer. When parsing the egl file, the requestor
 * recognises the egl elements (functions, fields, ...) and adds them to an index.
 */
public class SourceIndexerRequestor implements ISourceElementRequestor, IIndexConstants {
	SourceIndexer indexer;
	IDocument document;

	char[] packageName;
	char[][] enclosingTypeNames = new char[5][];
	int depth = 0;
	int methodDepth = 0;
	
public SourceIndexerRequestor(SourceIndexer indexer, IDocument document) {
	super();
	this.indexer = indexer;
	this.document= document;
}
/**
 * acceptField method comment.
 */
public void acceptField(int declarationStart, int declarationEnd, int modifiers, char[] type, char[] name, int nameSourceStart, int nameSourceEnd) {
	this.indexer.addFieldDeclaration(type, name);
}
/**
 * acceptFieldReference method comment.
 */
public void acceptFieldReference(char[] fieldName, int sourcePosition) {
	this.indexer.addFieldReference(fieldName);
}
/**
 * acceptImport method comment.
 */
public void acceptImport(int declarationStart, int declarationEnd, char[] name, boolean onDemand) {
	char[][] qualification = CharOperation.splitOn('.', CharOperation.subarray(name, 0, CharOperation.lastIndexOf('.', name)));
	for (int i = 0, length = qualification.length; i < length; i++) {
		this.indexer.addNameReference(qualification[i]);
	}
}
/**
 * acceptUse method comment.
 */
public void acceptUse(int declarationStart, int declarationEnd, char[] name) {
	char[][] names = CharOperation.splitOn('.', name);
	acceptPartReference(names, declarationStart, declarationEnd);
}/**
 * acceptLineSeparatorPositions method comment.
 */
public void acceptLineSeparatorPositions(int[] positions) {
}
/**
 * acceptFunctionReference method comment.
 */
public void acceptFunctionReference(char[] methodName, int argCount, int sourcePosition) {
	this.indexer.addFunctionReference(methodName, argCount);
}
/**
 * acceptPackage method comment.
 */
public void acceptPackage(int declarationStart, int declarationEnd, char[] name) {
	this.packageName = name;
}
/**
 * acceptTypeReference method comment.
 */
public void acceptPartReference(char[][] typeName, int sourceStart, int sourceEnd) {
	int length = typeName.length;
	for (int i = 0; i < length - 1; i++)
		acceptUnknownReference(typeName[i], 0); // ?
	acceptPartReference(typeName[length - 1], 0);
}
/**
 * acceptTypeReference method comment.
 */
public void acceptPartReference(char[] simpleTypeName, int sourcePosition) {
	this.indexer.addPartReference(simpleTypeName);
}
/**
 * acceptUnknownReference method comment.
 */
public void acceptUnknownReference(char[][] name, int sourceStart, int sourceEnd) {
	for (int i = 0; i < name.length; i++) {
		acceptUnknownReference(name[i], 0);
	}
}
/**
 * acceptUnknownReference method comment.
 */
public void acceptUnknownReference(char[] name, int sourcePosition) {
	this.indexer.addNameReference(name);
}
/*
 * Rebuild the proper qualification for the current source type:
 *
 * java.lang.Object ---> null
 * java.util.Hashtable$Entry --> [Hashtable]
 * x.y.A$B$C --> [A, B]
 */
public char[][] enclosingTypeNames(){

	if (depth == 0) return null;

	char[][] qualification = new char[this.depth][];
	System.arraycopy(this.enclosingTypeNames, 0, qualification, 0, this.depth);
	return qualification;
}
/**
 * enterClass method comment.
 */
public void enterPart(int partType, char[] subType, int contentCode, int declarationStart, int modifiers, char[] name, int nameSourceStart, int nameSourceEnd, char[][] interfaceNames, char[][] parameterNames, char[][] parameterTypes, char[][] usagePartTypes, char[][] usagePartPackages, String eglFileName) {

	char[][] enclosingTypeNames;
	if (this.methodDepth > 0) {
		enclosingTypeNames = ONE_ZERO_CHAR;
	} else {
		enclosingTypeNames = this.enclosingTypeNames();
	}
	char partChar = PART_SUFFIX;
	// TODO Handle parttype searches
	switch (partType) {
		case Part.FUNCTION : 	partChar = FUNCTION_SUFFIX; break;
		case Part.FORM : 		partChar = FORM_SUFFIX; break;
		case Part.FORMGROUP : 	partChar = FORMGRP_SUFFIX; break;
		case Part.LIBRARY : 	partChar = LIBRARY_SUFFIX; break;
		case Part.PROGRAM : 	partChar = PROGRAM_SUFFIX; break;
		case Part.DATATABLE : 	partChar = TABLE_SUFFIX; break;
		case Part.RECORD : 		partChar = RECORD_SUFFIX; break;
		case Part.DATAITEM :	partChar = ITEM_SUFFIX; break;
		case Part.HANDLER : 	partChar = HANDLER_SUFFIX; break;
		case Part.INTERFACE : 	partChar = INTERFACE_SUFFIX; break;
		case Part.DELEGATE : 	partChar = DELEGATE_SUFFIX; break;
		case Part.EXTERNALTYPE : partChar = EXTERNALTYPE_SUFFIX; break;
		case Part.ENUMERATION : partChar = ENUMERATION_SUFFIX; break;
		case Part.SERVICE : 	partChar = SERVICE_SUFFIX; break;
		case Part.CLASS :   	partChar = CLASS_SUFFIX; break;

		default : 					partChar = PART_SUFFIX; break;
	}
	this.indexer.addPartDeclaration(partChar, modifiers, packageName, name, enclosingTypeNames, interfaceNames);
	this.indexer.addNameReference(subType);
	this.pushPartName(name);
}
/**
 * enterEGLFile method comment.
 */
public void enterEGLFile() {
}

/**
 * enterField method comment.
 */
public void enterField(int declarationStart, int modifiers, char[] type,  char[] typeDeclaredPackage, char[] name, int nameSourceStart, int nameSourceEnd, boolean hasOccurs,int declEnd) {
	this.indexer.addFieldDeclaration(type, name);
	this.methodDepth++;
	if (methodDepth > 1)
		exitField(declEnd);
}
/**
 * enterMethod method comment.
 */
public void enterFunction(int declarationStart, int modifiers, char[] returnType, char[] returnTypePackage, char[] name, int nameSourceStart, int nameSourceEnd, char[][] parameterTypes, char[][] parameterNames, char[][] parameterUseTypes, boolean[] areNullable, char[][] parameterPackages) {
	this.indexer.addFunctionDeclaration(name, parameterTypes, returnType);
	this.methodDepth++;
}
/**
 * exitClass method comment.
 */
public void exitPart(int declarationEnd) {
	popPartName();
}
/**
 * exitCompilationUnit method comment.
 */
public void exitEGLFile(int declarationEnd) {
}
/**
 * exitConstructor method comment.
 */
public void exitConstructor(int declarationEnd) {
	this.methodDepth--;
}
/**
 * exitField method comment.
 */
public void exitField(int declarationEnd) {
	this.methodDepth--;
}
/**
 * exitField method comment.
 */
public void exitUse(int declarationEnd) {
	this.methodDepth--;
}
/**
 * exitMethod method comment.
 */
public void exitFunction(int declarationEnd) {
	this.methodDepth--;
}
public void popPartName(){
	try {
	enclosingTypeNames[--depth] = null;
	} catch (ArrayIndexOutOfBoundsException e) {
		e.printStackTrace();
	}
}
public void pushPartName(char[] typeName){
	if (depth == enclosingTypeNames.length){
		System.arraycopy(enclosingTypeNames, 0, enclosingTypeNames = new char[depth*2][], 0, depth);
	}
	enclosingTypeNames[depth++] = typeName;
}
	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.core.internal.model.ISourceElementRequestor#acceptProperty(int, int, char[])
	 */
	public void acceptProperty(
		int declarationStart,
		int declarationEnd,
		char[] text) {
		StringTokenizer tokenizer = new StringTokenizer(new String(text),"=\t\f\n\r"); //$NON-NLS-1$
		// TODO Assumes simple property only handle boolean and string types
		String key = tokenizer.nextToken().trim();
		String value = null;
		
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if (!token.equals("=")) value = token.trim(); //$NON-NLS-1$
		}
		// TODO Temporary handling of valueType
		if (value != null && !value.equals("yes") && !value.equals("no")){ //$NON-NLS-1$ //$NON-NLS-2$
			acceptUnknownReference(value.toCharArray(),declarationStart);
		}
		
		if (key != null && key.length() != 0) {
			acceptUnknownReference(key.toCharArray(), declarationStart);
		}
		
		// TODO Handle references but now just do nothing

	}
	
	public void acceptPropertyLiteralName(int declarationStart, int declarationEnd, char[] name) {
		// name is just the property name
		if (name != null && name.length != 0) {
			acceptUnknownReference(name, declarationStart);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.core.internal.model.ISourceElementRequestor#enterPropertyBlock(int, char[])
	 */
	public void enterPropertyBlock(int declarationStart, char[] name) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.core.internal.model.ISourceElementRequestor#exitPropertyBlock(int)
	 */
	public void exitPropertyBlock(int declarationEnd) {
	}

}
