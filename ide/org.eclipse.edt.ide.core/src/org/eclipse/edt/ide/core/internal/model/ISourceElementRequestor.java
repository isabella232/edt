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
package org.eclipse.edt.ide.core.internal.model;

/**
 * Part of the source element parser responsible for building the output.
 * It gets notified of structural information as they are detected, relying
 * on the requestor to assemble them together, based on the notifications it got.
 *
 * The structural investigation includes:
 * - package statement
 * - import statements
 * - top-level types: package member, member types (member types of member types...)
 * - fields
 * - functions
 *
 * If reference information is requested, then all source constructs are
 * investigated and type, field & function references are provided as well.
 *
 * Any (parsing) problem encountered is also provided.
 *
 * All positions are relative to the exact source fed to the parser.
 *
 * Elements which are complex are notified in two steps:
 * - enter<Element> : once the element header has been identified
 * - exit<Element> : once the element has been fully consumed
 *
 * other simpler elements (package, import) are read all at once:
 * - accept<Element>
 */
 
public interface ISourceElementRequestor {
	/**
	 * Table of line separator position. This table is passed once at the end of
	 * the parse action, so as to allow computation of normalized ranges.
	 * 
	 * A line separator might corresponds to several characters in the source,
	 * 
	 */
	void acceptLineSeparatorPositions(int[] positions);

	void acceptFunctionReference(char[] functionName, int argCount,
			int sourcePosition);

	void acceptPartReference(char[][] typeName, int sourceStart, int sourceEnd);

	void acceptPartReference(char[] typeName, int sourcePosition);

	void acceptUnknownReference(char[][] name, int sourceStart, int sourceEnd);

	void acceptUnknownReference(char[] name, int sourcePosition);

	void acceptProperty(int declarationStart, int declarationEnd, char[] name);

	public void acceptPropertyLiteralName(int declarationStart,
			int declarationEnd, char[] name);

	void acceptField(int declarationStart, int declarationEnd, int modifiers,
			char[] type, char[] name, int nameSourceStart, int nameSourceEnd);

	/**
	 * @param declarationStart
	 *            This is the position of the first character of the import
	 *            keyword.
	 * @param declarationEnd
	 *            This is the position of the ';' ending the import statement or
	 *            the end of the comment following the import.
	 * @param name
	 *            This is the name of the import like specified in the source
	 *            including the dots. The '.*' is never included in the name.
	 * @param onDemand
	 *            set to true if the import is an import on demand (e.g. import
	 *            java.io.*). False otherwise.
	 */
	void acceptImport(int declarationStart, int declarationEnd, char[] name, boolean onDemand);

	void acceptPackage(int declarationStart, int declarationEnd, char[] name);

	void acceptUse(int declarationStart, int declarationEnd, char[] name);

	void enterPart(int partType, char[] subType, int contentCode,
			int declarationStart, int modifiers, char[] name,
			int nameSourceStart, int nameSourceEnd, char[][] interfaces,
			char[][] parameterNames, char[][] parameterTypes,
			char[][] usagePartTypes, char[][] usagePartPackages,
			String eglFileName);

	void enterEGLFile();

	void enterField(int declarationStart, int modifiers, char[] type,
			char[] typeDeclaredPackage, char[] name, int nameSourceStart,
			int nameSourceEnd, boolean hasOccurs, int declarationEnd);

	void enterFunction(int declarationStart, int modifiers, char[] returnType,
			char[] returnTypePackage, char[] name, int nameSourceStart,
			int nameSourceEnd, char[][] parameterTypes,
			char[][] parameterNames, char[][] parameterUseTypes,
			boolean[] areNullable, char[][] parameterPackages);

	void enterPropertyBlock(int declarationStart, char[] name);

	void exitPropertyBlock(int declarationEnd);

	void exitPart(int declarationEnd);

	void exitEGLFile(int declarationEnd);

	void exitUse(int declarationEnd);

	/**
	 * initializationStart denotes the source start of the expression used for
	 * initializing the field if any (-1 if no initialization).
	 */
	void exitField(int declarationEnd);

	void exitFunction(int declarationEnd);

	// purpose: for function parameter type identifier. May extend to common field, etc.
	public static final int UNKNOWN_TYPE = 0;
	public static final int PRIMITIVE_TYPE = 1;
	public static final int DATAITEM_TYPE = 2;
	public static final int RECORD_TYPE = 3;
}
