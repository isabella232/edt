/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.mof.egl;

import java.util.List;

/**
 * A GenericType is a type that has TypeArguments that specialize a Classifier
 * that has been defined with TypeParameters.  A typical usage of this type
 * in EGL is <code>egl.lang.List<E></code>.  This classifier has a type parameter
 * defined for it.  References to the List type can be parameterized with a specific
 * type which in the List case would represent the type of objects that the given
 * List can contain.
 * 
 *
 */
public interface GenericType extends Type {
	Classifier getClassifier();
	
	void setClassifier(Classifier value);
	
	List<Type> getTypeArguments();
	
	
	public void addTypeArgument(Type typeArg);
	
	public TypeParameter getTypeParameter();
	public void setTypeParameter(TypeParameter type);
	
	public Type resolveTypeParameter(Type type);
}
