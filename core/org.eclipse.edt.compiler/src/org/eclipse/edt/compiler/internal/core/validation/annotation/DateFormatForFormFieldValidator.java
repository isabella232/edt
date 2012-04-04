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

import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.AbstractBinder;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.statement.StatementValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author demurray
 */
public class DateFormatForFormFieldValidator extends DefaultFieldContentAnnotationValidationRule {

	public void validate(Node errorNode, Node container, IDataBinding containerBinding, String canonicalContainerName, Map allAnnotations, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		IAnnotationBinding annotationBinding = (IAnnotationBinding) allAnnotations.get(InternUtil.intern(IEGLConstants.PROPERTY_DATEFORMAT));
		if(annotationBinding != null && annotationBinding.getValue() != null) {
			try {
				int fieldLength = Integer.parseInt(((IAnnotationBinding) allAnnotations.get(InternUtil.intern(IEGLConstants.PROPERTY_FIELDLEN))).getValue().toString());
				int[] allowedLengths = null;
				Object value = annotationBinding.getValue();
				
				if(value instanceof IDataBinding) {
					IDataBinding dBinding = (IDataBinding) value;
					if(AbstractBinder.dataBindingIs(dBinding, new String[] {"egl", "vg"}, "VGVar", "SYSTEMJULIANDATEFORMAT")) {
						allowedLengths = new int[] {6,8};
					}
					else if(AbstractBinder.dataBindingIs(dBinding, new String[] {"egl", "vg"}, "VGVar", "SYSTEMGREGORIANDATEFORMAT")) {
						allowedLengths = new int[] {8,10};
					}
				}
			
				if(allowedLengths != null) {
					if(fieldLength != allowedLengths[0] && fieldLength != allowedLengths[1]) {
						problemRequestor.acceptProblem(
							errorNode,
							IProblemRequestor.PROPERTY_INVALID_VALUE_DATEFORMAT_GREGORIAN_AND_JULIAN,
							new String[] {
								IEGLConstants.PROPERTY_DATEFORMAT,
								((IDataBinding) value).getCaseSensitiveName(),							
								canonicalContainerName,
	                            Integer.toString(allowedLengths[0]),
	                            Integer.toString(allowedLengths[1]),							
	                            StatementValidator.getShortTypeString(containerBinding.getType())});
					}
				}
			}
			catch(NumberFormatException e) {}
		}
	}
}
