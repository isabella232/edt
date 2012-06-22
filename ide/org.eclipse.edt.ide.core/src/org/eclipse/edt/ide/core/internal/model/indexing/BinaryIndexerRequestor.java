/*******************************************************************************
 * Copyright © 2010, 2012 IBM Corporation and others.
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

import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.ide.core.internal.model.IRPartType;
import org.eclipse.edt.ide.core.internal.model.ISourceElementRequestor;
import org.eclipse.edt.ide.core.internal.model.index.IDocument;
import org.eclipse.edt.ide.core.model.IIndexConstants;

import com.ibm.icu.util.StringTokenizer;

public class BinaryIndexerRequestor implements ISourceElementRequestor, IIndexConstants {
	private BinaryIndexer indexer;
	private IDocument document;
	
	char[] packageName;
	char[][] enclosingTypeNames = new char[5][];
	int[] partTypes = new int[5];
	int partTypesDepth = 0;
	int depth = 0;
	int methodDepth = 0;
	/**
	 * 
	 * @param indexer
	 * @param document
	 */
	public BinaryIndexerRequestor(BinaryIndexer indexer, IDocument document) {
		super();
		this.indexer = indexer;
		this.document = document;
	}
	
	public void acceptField(int declarationStart, int declarationEnd, int modifiers, char[] type, char[] name, int nameSourceStart, int nameSourceEnd) {
		this.indexer.addFieldDeclaration(type, name);
	}

	public void acceptFunctionReference(char[] functionName, int argCount, int sourcePosition) {
		this.indexer.addFunctionReference(functionName, argCount);
	}

	public void acceptImport(int declarationStart, int declarationEnd, char[] name, boolean onDemand) {
		//None for the IRs
	}

	public void acceptLineSeparatorPositions(int[] positions) {
		//None for the IR
	}

	public void acceptPackage(int declarationStart, int declarationEnd, char[] name) {
		this.packageName = name;
	}

	public void acceptPartReference(char[][] typeName, int sourceStart, int sourceEnd) {
		int length = typeName.length;
		for (int i = 0; i < length - 1; i++)
			acceptUnknownReference(typeName[i], 0); 
		acceptPartReference(typeName[length - 1], 0);
	}

	public void acceptPartReference(char[] typeName, int sourcePosition) {
		this.indexer.addPartReference(typeName);
	}

	public void acceptProperty(int declarationStart, int declarationEnd, char[] name) {
		StringTokenizer tokenizer = new StringTokenizer(new String(name),"=\t\f\n\r"); //$NON-NLS-1$
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

	public void acceptUnknownReference(char[][] name, int sourceStart, int sourceEnd) {
		for (int i = 0; i < name.length; i++) {
			acceptUnknownReference(name[i], 0);
		}
	}

	public void acceptUnknownReference(char[] name, int sourcePosition) {
		this.indexer.addNameReference(name);
	}

	public void acceptUse(int declarationStart, int declarationEnd, char[] name) {
		//None for IR files
	}

	public void enterEGLFile() {
		
	}

	public void enterField(int declarationStart, int modifiers, char[] type, char[] typeDeclaredPackage, char[] name, int nameSourceStart, int nameSourceEnd, boolean hasOccurs,
			int declarationEnd) {
		this.methodDepth++;
		if (methodDepth > 1)
			exitField(declarationEnd);
	}

	public void enterFunction(int declarationStart, int modifiers, char[] returnType, char[] returnTypePackage, char[] name, int nameSourceStart, int nameSourceEnd,
			char[][] parameterTypes, char[][] parameterNames, char[][] parameterUseTypes, boolean[] areNullable, char[][] parameterPackages) {
		this.indexer.addFunctionDeclaration(name, parameterTypes, returnType);
		this.methodDepth++;
	}

	public void enterPart(int partType, char[] subType, int contentCode, int declarationStart, int modifiers, char[] name, int nameSourceStart,
			int nameSourceEnd, char[][] interfaces, char[][] parameterNames, char[][] parameterTypes, char[][] usagePartTypes, char[][] usagePartPackages,String eglFileName) {
		char[][] enclosingTypeNames = new char[0][0];
		enclosingTypeNames = null;
		enclosingTypeNames = enclosingTypeNames();
		
		char partChar = PART_SUFFIX;
		switch (partType) {
			case IRPartType.PART_FUNCTION : 	partChar = FUNCTION_SUFFIX; break;
			case IRPartType.PART_FORM : 		partChar = FORM_SUFFIX; break;
			case IRPartType.PART_FORMGROUP : 	partChar = FORMGRP_SUFFIX; break;
			case IRPartType.PART_LIBRARY : 	    partChar = LIBRARY_SUFFIX; break;
			case IRPartType.PART_PROGRAM : 	    partChar = PROGRAM_SUFFIX; break;
			case IRPartType.PART_DATATABLE : 	partChar = TABLE_SUFFIX; break;
			case IRPartType.PART_RECORD : 	    partChar = RECORD_SUFFIX; break;
			case IRPartType.PART_DATAITEM :	    partChar = ITEM_SUFFIX; break;
			case IRPartType.PART_HANDLER : 	    partChar = HANDLER_SUFFIX; break;
			case IRPartType.PART_INTERFACE : 	partChar = INTERFACE_SUFFIX; break;
			case IRPartType.PART_DELEGATE : 	partChar = DELEGATE_SUFFIX; break;
			case IRPartType.PART_EXTERNALTYPE : partChar = EXTERNALTYPE_SUFFIX; break;
			case IRPartType.PART_ENUMERATION :  partChar = ENUMERATION_SUFFIX; break;
			case IRPartType.PART_SERVICE : 	    partChar = SERVICE_SUFFIX; break;
			case IRPartType.PART_ANNOTATION : 	partChar = ANNOTATION_SUFFIX; break;
			case IRPartType.PART_STEREOTYPE : 	partChar = ANNOTATION_SUFFIX | STEREOTYPE_SUFFIX; break;
			default : 					        partChar = PART_SUFFIX; break;
		}
		this.indexer.addPartDeclaration(partChar, modifiers, packageName, name, enclosingTypeNames, interfaces);
		this.indexer.addNameReference(subType);
//		if(partType == Part.PART_FORMGROUP){
			this.pushPartName(name, partType);
//		}
	}

	public void enterPropertyBlock(int declarationStart, char[] name) {

	}

	public void exitEGLFile(int declarationEnd) {

	}

	public void exitField(int declarationEnd) {
		this.methodDepth--;
	}

	public void exitFunction(int declarationEnd) {
		this.methodDepth--;
	}

	public void exitPart(int declarationEnd) {
			this.popPartName();
	}

	public void exitPropertyBlock(int declarationEnd) {

	}

	public void exitUse(int declarationEnd) {

	}

	public void popPartName(){
		try {
			int partType = partTypes[--partTypesDepth];
			partTypes[partTypesDepth] = 0;
			if(partType == Part.FORMGROUP){
				enclosingTypeNames[--depth] = null;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
	}
	public void pushPartName(char[] typeName, int partType){
		if (depth == enclosingTypeNames.length){
			System.arraycopy(enclosingTypeNames, 0, enclosingTypeNames = new char[depth*2][], 0, depth);
		}
		if (partTypesDepth == partTypes.length){
			System.arraycopy(partTypes, 0, partTypes = new int[partTypesDepth*2], 0, partTypesDepth);
		}
		if(partType == Part.FORMGROUP){
			enclosingTypeNames[depth++] = typeName;
		}
		partTypes[partTypesDepth++] = partType;
	}
	
	public char[][] enclosingTypeNames(){

		if (depth == 0) return null;

		char[][] qualification = new char[this.depth][];
		System.arraycopy(this.enclosingTypeNames, 0, qualification, 0, this.depth);
		return qualification;
	}
}
