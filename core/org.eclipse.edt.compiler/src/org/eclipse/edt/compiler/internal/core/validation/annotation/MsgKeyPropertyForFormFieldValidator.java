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
public class MsgKeyPropertyForFormFieldValidator extends FieldContentValidationAnnotationTypeBinding {
	
	IAnnotationTypeBinding annotationType;
	String canonicalAnnotationName;
	
	public MsgKeyPropertyForFormFieldValidator(IAnnotationTypeBinding annotationType, String canonicalAnnotationName) {
		super(InternUtil.internCaseSensitive("XXXMsgKey for form field rule"));
		this.annotationType = annotationType;
		this.canonicalAnnotationName = canonicalAnnotationName;
	}

	public void validate(Node errorNode, Node container, IDataBinding containerBinding, String canonicalContainerName, Map allAnnotations, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		IAnnotationBinding annotationBinding = (IAnnotationBinding) allAnnotations.get(annotationType.getName());
		if(annotationBinding != null && annotationBinding.getValue() != null) {
			if(containerBinding != null && IDataBinding.FORM_FIELD == containerBinding.getKind()) {
				boolean valueValid;
				try {
					int valueAsInt = Integer.parseInt(annotationBinding.getValue().toString());
					valueValid = valueAsInt >= -9999 && valueAsInt <= 9999;
				}
				catch(NumberFormatException e) {
					valueValid = false;
				}
								
				if(!valueValid) {
					problemRequestor.acceptProblem(
						errorNode,
						IProblemRequestor.INVALID_FORM_FIELD_MSG_KEY_INTEGER_PROPERTY_VALUE,
						new String[] {
							canonicalAnnotationName,
							canonicalContainerName,
							containerBinding.getDeclaringPart().getCaseSensitiveName()
						});
				}
			}
		}
	}
}
