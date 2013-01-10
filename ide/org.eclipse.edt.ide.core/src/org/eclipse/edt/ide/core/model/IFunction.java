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
 * Represents a function (or constructor) declared in a type.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface IFunction extends IMember, IPart {
/**
 * Returns the simple name of this function.
 * For a constructor, this returns the simple name of the declaring type.
 * Note: This holds whether the constructor appears in a source or binary type
 * (even though class files internally define constructor names to be <code>"&lt;init&gt;"</code>).
 * For the class initialization functions in binary types, this returns
 * the special name <code>"&lt;clinit&gt;"</code>.
 * This is a handle-only function.
 */
String getElementName();
/**
 * Returns the number of parameters of this function.
 * This is a handle-only function.
 * 
 * @return the number of parameters of this function
 */
int getNumberOfParameters();
/**
 * Returns the names of parameters in this function.
 * For binary types, these names are invented as "arg"+i, where i starts at 1 
 * (even if source is associated with the binary).
 * Returns an empty array if this function has no parameters.
 *
 * <p>For example, a function declared as <code>public void foo(String text, int length)</code>
 * would return the array <code>{"text","length"}</code>.
 *
 * @exception EGLModelException if this element does not exist or if an
 *      exception occurs while accessing its corresponding resource.
 * @return the names of parameters in this function, an empty array if this function has no parameters
 */
String[] getParameterNames() throws EGLModelException;
/**
 * Returns the type signatures for the parameters of this function.
 * Returns an empty array if this function has no parameters.
 * This is a handle-only function.
 *
 * <p>For example, a source function declared as <code>public void foo(String text, int length)</code>
 * would return the array <code>{"QString;","I"}</code>.
 * 
 * @return the type signatures for the parameters of this function, an empty array if this function has no parameters
 * @see Signature
 */
String[] getParameterTypes();
/**
 * Returns the type signature of the return value of this function.
 * For constructors, this returns the signature for void.
 *
 * <p>For example, a source function declared as <code>public String getName()</code>
 * would return <code>"QString;"</code>.
 *
 * @exception EGLModelException if this element does not exist or if an
 *      exception occurs while accessing its corresponding resource.
 * @return the type signature of the return value of this function, void  for constructors
 * @see Signature
 */
String getReturnTypeName() throws EGLModelException;
/**
 * Returns the signature of the function. This includes the signatures for the parameter
 * types and return type, but does not include the function name or exception types.
 *
 * <p>For example, a source function declared as <code>public void foo(String text, int length)</code>
 * would return <code>"(QString;I)V"</code>.
 *
 * @exception EGLModelException if this element does not exist or if an
 *      exception occurs while accessing its corresponding resource.
 *
 * @see Signature
 */
String getSignature() throws EGLModelException;
/**
 * Returns whether this function is similar to the given function.
 * Two functions are similar if:
 * <ul>
 * <li>their element names are equal</li>
 * <li>they have the same number of parameters</li>
 * <li>the simple names of their parameter types are equal</li>
 * </ul>
 * This is a handle-only function.
 * 
 * @param function the given function
 * @return true if this function is similar to the given function.
 * @see Signature#getSimpleName
 * @since 2.0
 */
boolean isSimilar(IFunction function);

String[] getUseTypes() throws EGLModelException; 

boolean[] getNullable() throws EGLModelException;

String[] getParameterPackages() throws EGLModelException;

String getReturnTypePackage() throws EGLModelException;
}
