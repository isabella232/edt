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
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.LibraryBinding;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.AbstractBinder;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;


public class ValidationPropertiesLibraryAnnotationValidator implements IValueValidationRule{
	
	public void validate(final Node annotation, Node container, IAnnotationBinding annotationBinding, final IProblemRequestor problemRequestor, ICompilerOptions compilerOptions){
		if (!(annotationBinding.getValue() instanceof IBinding)) {
			return;
		}
		
		if (!(annotationBinding.getValue() instanceof LibraryBinding)
				|| !AbstractBinder.annotationIs(((LibraryBinding)annotationBinding.getValue()).getSubType(), new String[] {"eglx", "ui", "rui"}, "RUIPropertiesLibrary")) {
			problemRequestor.acceptProblem(
					annotation,
					IProblemRequestor.VALIDATION_PROPERTIES_LIBRARY_WRONG_TYPE,
					new String[] {((IBinding)annotationBinding.getValue()).getCaseSensitiveName()});
			return;
		}
	}
}
