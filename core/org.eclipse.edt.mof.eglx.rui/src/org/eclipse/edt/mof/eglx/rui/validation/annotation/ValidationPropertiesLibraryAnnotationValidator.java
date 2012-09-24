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
package org.eclipse.edt.mof.eglx.rui.validation.annotation;

import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.annotation.IValueValidationRule;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Library;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;
import org.eclipse.edt.mof.eglx.rui.messages.RUIResourceKeys;


public class ValidationPropertiesLibraryAnnotationValidator implements IValueValidationRule{
	
	@Override
	public void validate(final Node annotation, Node container, Annotation annotationBinding, final IProblemRequestor problemRequestor, ICompilerOptions compilerOptions){
		if (!(annotationBinding.getValue() instanceof Type)) {
			return;
		}
		
		if (!(annotationBinding.getValue() instanceof Library)
				|| ((Library)annotationBinding.getValue()).getSubType() == null
				|| !((Library)annotationBinding.getValue()).getSubType().getEClass().equals(TypeUtils.getEGLType("eglx.ui.rui.RUIPropertiesLibrary"))) {
			problemRequestor.acceptProblem(
					annotation,
					RUIResourceKeys.VALIDATION_PROPERTIES_LIBRARY_WRONG_TYPE,
					IMarker.SEVERITY_ERROR,
					new String[] {BindingUtil.getShortTypeString((Type)annotationBinding.getValue(), false)},
					RUIResourceKeys.getResourceBundleForKeys());
			return;
		}
	}
}
