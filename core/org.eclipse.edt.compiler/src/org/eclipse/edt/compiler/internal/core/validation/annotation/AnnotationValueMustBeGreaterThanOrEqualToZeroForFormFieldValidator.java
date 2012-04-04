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

import org.eclipse.edt.compiler.binding.FieldContentValidationAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author demurray
 */
public class AnnotationValueMustBeGreaterThanOrEqualToZeroForFormFieldValidator extends FieldContentValidationAnnotationTypeBinding {
	
	String canonicalAnnotationName;
	IAnnotationTypeBinding annotationTypeBinding;
	
	public AnnotationValueMustBeGreaterThanOrEqualToZeroForFormFieldValidator(String canonicalAnnotationName, IAnnotationTypeBinding annotationTypeBinding) {
		super(InternUtil.internCaseSensitive("AnnotationValueMustBeGreaterThanOrEqualToZeroForFormFieldValidator"));
		this.canonicalAnnotationName = canonicalAnnotationName;
		this.annotationTypeBinding = annotationTypeBinding;
	}

	public void validate(Node errorNode, Node container, IDataBinding containerBinding, String canonicalContainerName, Map allAnnotations, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		IAnnotationBinding annotationBinding = (IAnnotationBinding) allAnnotations.get(annotationTypeBinding.getName());
		if(annotationBinding != null) {
			boolean valid = false;
			try {
				int value = Integer.parseInt(annotationBinding.getValue().toString());
				valid = value >= 0;
			}
			catch(NumberFormatException e) {				
			}
				
			if(!valid) {
				problemRequestor.acceptProblem(
					errorNode,
					IProblemRequestor.INVALID_FORM_FIELD_GREATER_ZERO_PROPERTY_VALUE,
					new String[] {
						canonicalAnnotationName,
						canonicalContainerName,
						containerBinding.getDeclaringPart().getCaseSensitiveName()
					});
			}
		}
	}
}
