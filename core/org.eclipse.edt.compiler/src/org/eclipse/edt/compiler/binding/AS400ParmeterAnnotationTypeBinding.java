/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.binding;

import org.eclipse.edt.compiler.internal.core.validation.annotation.AbstractAS400ParameterAnnotaionValidator;


public abstract class AS400ParmeterAnnotationTypeBinding extends AnnotationTypeBinding {

	public AS400ParmeterAnnotationTypeBinding(String caseSensitiveInternedName) {
		super(caseSensitiveInternedName);
	}
	
	public abstract AbstractAS400ParameterAnnotaionValidator getValidator();
	
}
