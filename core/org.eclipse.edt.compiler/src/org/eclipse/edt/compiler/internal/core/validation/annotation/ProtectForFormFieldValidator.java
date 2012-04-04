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

import org.eclipse.edt.compiler.binding.EnumerationDataBinding;
import org.eclipse.edt.compiler.binding.FormFieldBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.AbstractBinder;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author demurray
 */
public class ProtectForFormFieldValidator extends DefaultFieldContentAnnotationValidationRule {

	public void validate(Node errorNode, Node container, IDataBinding containerBinding, String canonicalContainerName, Map allAnnotations, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		IAnnotationBinding annotationBinding = (IAnnotationBinding) allAnnotations.get(InternUtil.intern(IEGLConstants.PROPERTY_PROTECT));
		if(annotationBinding != null && annotationBinding.getValue() != null) {
			if(containerBinding != null && IDataBinding.FORM_FIELD == containerBinding.getKind()) {
				FormFieldBinding fieldBinding = (FormFieldBinding) containerBinding;
				if(fieldBinding.isConstant()) {
					if(AbstractBinder.enumerationIs((EnumerationDataBinding) annotationBinding.getValue(), new String[] {"egl", "ui", "text"}, "ProtectKind", "NoProtect")) {
						problemRequestor.acceptProblem(
							errorNode,
							IProblemRequestor.INVALID_FORM_FIELD_PROPERTY_VALUE_MUST_BE_ONE_OF,
							new String[] {
								IEGLConstants.PROPERTY_PROTECT,
								canonicalContainerName,
								fieldBinding.getDeclaringPart().getCaseSensitiveName(),
								"protect, skipProtect"
							});
					}
				}
			}
		}
	}
}
