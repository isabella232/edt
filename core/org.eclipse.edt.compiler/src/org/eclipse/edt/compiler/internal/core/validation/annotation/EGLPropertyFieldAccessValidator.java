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
package org.eclipse.edt.compiler.internal.core.validation.annotation;

import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;

public class EGLPropertyFieldAccessValidator extends
		PropertyFieldAccessValidator {
	
	protected IAnnotationBinding getAnnotation(IDataBinding binding) {
		return binding.getAnnotation(new String[] {"eglx", "lang"}, "EGLProperty");
	}

	protected boolean hasValue(Object obj) {
		
		return (obj != null);
	}

}
