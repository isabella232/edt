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
package org.eclipse.edt.ide.core.internal.model.compiler.env;


public interface IBinaryFunction extends IGenericType {
	/**
	 * Answer the runtime visible and invisible annotations for this method or null if none.
	 */
//	IBinaryAnnotation[] getAnnotations();

	/**
	 * Return {@link org.eclipse.jdt.internal.compiler.impl.Constant} for compile-time constant of primitive type, as well as String literals.
	 * Return {@link EnumConstantSignature} if value is an enum constant.
	 * Return {@link IBinaryAnnotation} for annotation type.
	 * Return {@link Object}[] for array type.
	 *
	 * @return default value of this annotation method
	 */
	Object getDefaultValue();

	/**
	 * Answer the resolved names of the exception types in the
	 * class file format as specified in section 4.2 of the Java 2 VM spec
	 * or null if the array is empty.
	 *
	 * For example, java.lang.String is java/lang/String.
	 */
	char[][] getExceptionTypeNames();

	/**
	 * Answer the receiver's signature which describes the parameter &
	 * return types as specified in section 4.4.4 of the Java 2 VM spec.
	 */
	char[] getGenericSignature();

	/**
	 * Answer the receiver's method descriptor which describes the parameter &
	 * return types as specified in section 4.4.3 of the Java 2 VM spec.
	 *
	 * For example:
	 *   - int foo(String) is (Ljava/lang/String;)I
	 *   - Object[] foo(int) is (I)[Ljava/lang/Object;
	 */
	char[] getFunctionDescriptor();

	/**
	 * Answer the annotations on the <code>index</code>th parameter or null if none
	 * @param index the index of the parameter of interest
	 */
//	IBinaryAnnotation[] getParameterAnnotations(int index);

	/**
	 * Answer the name of the method.
	 *
	 * For a constructor, answer <init> & <clinit> for a clinit method.
	 */
	char[] getSelector();

	/**
	 * Answer the tagbits set according to the bits for annotations.
	 */
	long getTagBits();

	/**
	 * Answer whether the receiver represents a class initializer method.
	 */
	boolean isClinit();
}
