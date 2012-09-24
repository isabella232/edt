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

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.annotation.IValueValidationRule;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.utils.TypeUtils;
import org.eclipse.edt.mof.eglx.rui.messages.RUIResourceKeys;
import org.eclipse.edt.mof.utils.NameUtile;

/**
 * @author svihovec
 */
public class RetrieveValidStateHelperValidator implements IValueValidationRule {
	
	@Override
	public void validate(Node errorNode, Node target, Annotation annotation, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		Object value = annotation.getValue(NameUtile.getAsName(IEGLConstants.PROPERTY_RETRIEVEVALIDSTATEHELPER));
		if (value instanceof FunctionMember) {
			FunctionMember function = (FunctionMember)value;
			if (!isValidRetrieveValidStateHelperFunction(function)) {
				problemRequestor.acceptProblem(
						errorNode,
						RUIResourceKeys.RETRIEVEVALIDSTATEHELPER_FUNCTION_INVALID,
						IMarker.SEVERITY_ERROR,
						new String[] {function.getCaseSensitiveName()},
						RUIResourceKeys.getResourceBundleForKeys());
			}
		}
	}
	
	public boolean isValidRetrieveValidStateHelperFunction(FunctionMember function) {
		if (function.getParameters().size() != 0) {
			return false;
		}
		
		if (function.getReturnType() == null) {
			return false;
		}
		
		if (!function.isNullable() || !TypeUtils.Type_STRING.equals(function.getReturnType())) {
			return false;
		}
		
		return true;
	}
}
