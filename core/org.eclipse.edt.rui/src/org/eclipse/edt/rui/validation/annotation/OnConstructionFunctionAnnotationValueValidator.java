/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.rui.validation.annotation;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.annotation.IValueValidationRule;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.utils.NameUtile;


public class OnConstructionFunctionAnnotationValueValidator implements IValueValidationRule {

	public void validate(Node errorNode, Node target, Annotation annotation, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		Object value = annotation.getValue(NameUtile.getAsName(IEGLConstants.PROPERTY_ONCONSTRUCTIONFUNCTION));
		if (value instanceof FunctionMember) {
			FunctionMember func = (FunctionMember)value;
			
			Part valueDeclarer = BindingUtil.getDeclaringPart(func);
			Part annotDeclarer = BindingUtil.getDeclaringPart(target);
			if (valueDeclarer != null && !valueDeclarer.equals(annotDeclarer)) {
				problemRequestor.acceptProblem(
					errorNode,
					IProblemRequestor.EXTERNAL_FUNCTION_NOT_ALLOWED_FOR_PROPERTY,
					new String[] {IEGLConstants.PROPERTY_ONCONSTRUCTIONFUNCTION, annotDeclarer.getCaseSensitiveName()});
			}
		}
	}
}
