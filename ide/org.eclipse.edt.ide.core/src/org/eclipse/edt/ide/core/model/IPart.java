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
package org.eclipse.edt.ide.core.model;


/**
 * @author twilson
 * created	Jul 24, 2003
 */
public interface IPart extends IMember, IParent {

	/** 
	 * Finds the functions in this type that correspond to
	 * the given function.
	 * A function m1 corresponds to another function m2 if:
	 * <ul>
	 * <li>m1 has the same element name as m2.
	 * <li>m1 has the same number of arguments as m2 and
	 *     the simple names of the argument types must be equals.
	 * <li>m1 exists.
	 * </ul>
	 * @param function the given function
	 * @return the found function or <code>null</code> if no such functions can be found.
	 * 
	 * @since 2.0 
	 */
	IFunction[] findFunctions(IFunction function);
	/**
	 * Returns the simple name of this type, unqualified by package or enclosing type.
	 * This is a handle-only function.
	 * 
	 * @return the simple name of this type
	 */
	String getElementName();
	
	/**
	 * Returns the signature of the subtype (i.e. SQLRecord, TextForm, char(10))
	 * This is not required for most types and will often return null.
	 * This is a handle-only function.
	 * 
	 * @return the simple name of this subtype
	 */
	String getSubTypeSignature();
	
	/**
	 * Returns the field with the specified name
	 * in this type (for example, <code>"bar"</code>).
	 * This is a handle-only function.  The field may or may not exist.
	 * 
	 * @param name the given name
	 * @return the field with the specified name in this type
	 */
	// EGLTODO: This does not work for embeds and filler structure items...
	IField getField(String name);
	
	/**
	 * Returns the fields declared by this type.
	 * If this is a source type, the results are listed in the order
	 * in which they appear in the source, otherwise, the results are
	 * in no particular order.  For binary types, this includes synthetic fields.
	 *
	 * @exception EGLModelException if this element does not exist or if an
	 *		exception occurs while accessing its corresponding resource.
	 * @return the fields declared by this type
	 */
	IField[] getFields() throws EGLModelException;
	
	IUseDeclaration getUseDeclaration(String name);
	
	IUseDeclaration[] getUseDeclarations() throws EGLModelException;
	
	String[] getImplementedInterfaceNames() throws EGLModelException;
	
	/**
	 * Returns the fully qualified name of this type, 
	 * including qualification for any containing types and packages.
	 * This is the name of the package, followed by <code>'.'</code>,
	 * followed by the type-qualified name.
	 * This is a handle-only function.
	 *
	 * @see IPart#getTypeQualifiedName()
	 * @return the fully qualified name of this type
	 */
	String getFullyQualifiedName();
	
	/**
	 * Returns the fully qualified name of this type, 
	 * including qualification for any containing types and packages.
	 * This is the name of the package, followed by <code>'.'</code>,
	 * followed by the type-qualified name using the <code>enclosingTypeSeparator</code>.
	 * 
	 * For example:
	 * <ul>
	 * <li>the fully qualified name of a part B defined as a member of a part A in a egl file A.egl 
	 *     in a package x.y using the '.' separator is "x.y.A.B"</li>
	 * <li>the fully qualified name of a part B defined as a member of a part A in a egl file A.egl 
	 *     in a package x.y using the '$' separator is "x.y.A$B"</li>
	 * </ul>
	 * 
	 * This is a handle-only function.
	 *
	 * @param enclosingTypeSeparator the given enclosing type separator
	 * @return the fully qualified name of this type, including qualification for any containing types and packages
	 * @see IType#getTypeQualifiedName(char)
	 * @since 2.0
	 */
	String getFullyQualifiedName(char enclosingTypeSeparator);
	/**
	 * Returns the function with the specified name and parameter types
	 * in this type (for example, <code>"foo", {"I", "QString;"}</code>). To get the
	 * handle for a constructor, the name specified must be the simple
	 * name of the enclosing type.
	 * This is a handle-only function.  The function may or may not be present.
	 * 
	 * @param name the given name
	 * @param parameterTypeSignatures the given parameter types
	 * @return the function with the specified name and parameter types in this type
	 */
	IFunction getFunction(String name, String[] parameterTypeSignatures);
	
	/**
	 * Returns the functions and constructors declared by this type.
	 * For binary types, this may include the special <code>&lt;clinit&gt</code>; function 
	 * and synthetic functions.
	 * If this is a source type, the results are listed in the order
	 * in which they appear in the source, otherwise, the results are
	 * in no particular order.
	 *
	 * @exception EGLModelException if this element does not exist or if an
	 *		exception occurs while accessing its corresponding resource.
	 * @return the functions and constructors declared by this type
	 */
	IFunction[] getFunctions() throws EGLModelException;
	
	/* Place holder for parts defined in parts (inner-parts)
	 * Currently Function almost acts this way.
	 */
	IPart getPart(String name);
	/**
	 * Returns the package fragment in which this element is defined.
	 * This is a handle-only function.
	 * 
	 * @return the package fragment in which this element is defined
	 */
	IPackageFragment getPackageFragment();

	/**
	 * Answer whether or not this part is visible to parts in other packages.
	 * If false, this part is only visible to parts in the current package.
	 */
	boolean isPublic();	
	
	IProperty[] getProperties(String key) throws EGLModelException;
	
	String[] getUsePartPackages() throws EGLModelException;
	String[] getUsePartTypes() throws EGLModelException;
}
