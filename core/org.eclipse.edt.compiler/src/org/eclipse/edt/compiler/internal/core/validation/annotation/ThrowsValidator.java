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
package org.eclipse.edt.compiler.internal.core.validation.annotation;

import java.util.Map;

import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.mof.egl.Container;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.ExternalType;
import org.eclipse.edt.mof.egl.FunctionMember;

public class ThrowsValidator implements IAnnotationValidationRule {
	
	@Override
	public void validate(Node errorNode, Node target, Element targetElement, Map<String, Object> allAnnotationsAndFields, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		//validate that this is only specified in a JavaObject externalType
		if (targetElement instanceof FunctionMember) {
			Container container = ((FunctionMember)targetElement).getContainer();
			if (container != null) {
				if (container instanceof ExternalType && container.getAnnotation("eglx.java.JavaObject") != null) {
					return;
				}
				problemRequestor.acceptProblem(errorNode, IProblemRequestor.THROWS_NOT_VALID_HERE);
			}
		}
	}
}
