/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
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

import org.eclipse.edt.compiler.binding.AnnotationFieldBinding;
import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.annotationType.DedicatedServiceAnnotationTypeBinding;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;



public class DedicatedServiceValidator implements IAnnotationValidationRule {
	
	public void validate(Node errorNode, Node target, ITypeBinding targetTypeBinding, Map allAnnotations, final IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		if (Binding.isValidBinding(targetTypeBinding) && Binding.isValidBinding(targetTypeBinding.getBaseType())) {

			if (targetTypeBinding.getBaseType().getKind() == ITypeBinding.SERVICE_BINDING) {
				return;
			}
			
			if (targetTypeBinding.getBaseType().getKind() == ITypeBinding.INTERFACE_BINDING) {
				IAnnotationBinding ann = (IAnnotationBinding)allAnnotations.get(DedicatedServiceAnnotationTypeBinding.name);
				if (ann == null) {
					return;
				}
				//if specified for an interface, serviceName must be specified
				IDataBinding db = ann.findData("serviceName");
				if (Binding.isValidBinding(db) && ((AnnotationFieldBinding)db).getValue() != null) {
					return;
				}
			}
			
			problemRequestor.acceptProblem(
					errorNode,
					IProblemRequestor.DEDICATEDSERIVE_VALID_ONLY_FOR_SERVICE,
					new String[] {});
		}
	}
}

