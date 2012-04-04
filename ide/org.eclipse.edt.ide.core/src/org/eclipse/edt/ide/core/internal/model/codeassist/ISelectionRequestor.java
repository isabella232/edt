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

// import org.eclipse.jdt.core.compiler.IProblem;

/**
 * A selection requestor accepts results from the selection engine.
 */
public interface ISelectionRequestor {
	/**
	 * Code assist notification of a class selection.
	 * @param packageName char[]
	 * 		Declaring package name of the class.
	 * 
	 * @param className char[]
	 * 		Name of the class.
	 * 
	 * @param needQualification boolean
	 * 		Flag indicating if the type name 
	 *    	must be qualified by its package name (depending on imports).
	 *
	 * NOTE - All package and type names are presented in their readable form:
	 *    Package names are in the form "a.b.c".
	 *    Nested type names are in the qualified form "A.M".
	 *    The default package is represented by an empty array.
	 */
	void acceptPart(
		char[] packageName,
		char[] partName,
		boolean needQualification);

	/**
	 * Code assist notification of a compilation error detected during selection.
	 *  @param error org.eclipse.jdt.internal.compiler.IProblem
	 *      Only problems which are categorized as errors are notified to the requestor,
	 *		warnings are silently ignored.
	 *		In case an error got signaled, no other completions might be available,
	 *		therefore the problem message should be presented to the user.
	 *		The source positions of the problem are related to the source where it was
	 *		detected (might be in another compilation unit, if it was indirectly requested
	 *		during the code assist process).
	 *      Note: the problem knows its originating file name.
	 */
	// TODO handle problems with request
//	void acceptError(IProblem error);

	/**
	 * Code assist notification of a field selection.
	 * @param declaringPartPackageName char[]
	 * 		Name of the package in which the type that contains this field is declared.
	 * 
	 * @param declaringPartName char[]
	 * 		Name of the type declaring this new field.
	 * 
	 * @param name char[]
	 * 		Name of the field.
	 *
	 * NOTE - All package and type names are presented in their readable form:
	 *    Package names are in the form "a.b.c".
	 *    Nested type names are in the qualified form "A.M".
	 *    The default package is represented by an empty array.
	 */
	void acceptField(
		char[] declaringPartPackageName,
		char[] declaringPartName,
		char[] name);

	/**
	 * Code assist notification of a method selection.
	 * @param declaringPartPackageName char[]
	 * 		Name of the package in which the type that contains this new method is declared.
	 * 
	 * @param declaringPartName char[]
	 * 		Name of the type declaring this new method.
	 * 
	 * @param selector char[]
	 * 		Name of the new method.
	 * 
	 * @param parameterPackageNames char[][]
	 * 		Names of the packages in which the parameter types are declared.
	 *    	Should contain as many elements as parameterPartNames.
	 * 
	 * @param parameterPartNames char[][]
	 * 		Names of the parameters types.
	 *    	Should contain as many elements as parameterPackageNames.
	 * 
	 *  @param isConstructor boolean
	 * 		Answer if the method is a constructor.
	 *
	 * NOTE - All package and type names are presented in their readable form:
	 *    Package names are in the form "a.b.c".
	 *    Base types are in the form "int" or "boolean".
	 *    Array types are in the qualified form "M[]" or "int[]".
	 *    Nested type names are in the qualified form "A.M".
	 *    The default package is represented by an empty array.
	 */
	void acceptFunction(
		char[] declaringPartPackageName,
		char[] declaringPartName,
		char[] selector,
		char[][] parameterPackageNames,
		char[][] parameterPartNames);

	/**
	 * Code assist notification of a package selection.
	 * @param packageName char[]
	 * 		The package name.
	 *
	 * NOTE - All package names are presented in their readable form:
	 *    Package names are in the form "a.b.c".
	 *    The default package is represented by an empty array.
	 */
	void acceptPackage(char[] packageName);
}
