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
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author demurray
 */
public class PositionForConsoleFieldValidator extends DefaultFieldContentAnnotationValidationRule {
	
	public void validate(Node errorNode, Node container, IDataBinding containerBinding, String canonicalContainerName, Map allAnnotations, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		IAnnotationBinding annotationBinding = (IAnnotationBinding) allAnnotations.get(InternUtil.intern(IEGLConstants.PROPERTY_POSITION));
		if(annotationBinding != null) {
			boolean valueIsValid = false;
			Object[] value = (Object[]) annotationBinding.getValue();		
			if(value.length == 2) {
				Integer[] intAry = (Integer[]) value;
				if(intAry[0].intValue() > 0 && intAry[1].intValue() > 0) {
					valueIsValid = true;
				}
			}
			
			if(!valueIsValid) {
				problemRequestor.acceptProblem(
					errorNode,
					IProblemRequestor.INVALID_PROPERTY_MUST_BE_TWO_POSITIVE_INTEGERS,
					new String[] {
						IEGLConstants.PROPERTY_POSITION
					});
			}
		}
	}	
}
