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
package org.eclipse.edt.compiler.internal.core.validation.annotation;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.ParameterizedType;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;


/**
 * @author svihovec
 */
public class PropertyApplicableForTimestampTypeOnlyAnnotationValidator extends PropertyApplicableForCertainPrimitiveTypeOnlyAnnotationValidator {
	
	public PropertyApplicableForTimestampTypeOnlyAnnotationValidator(String canonicalAnnotationName) {
		super(canonicalAnnotationName);
	}
	
	@Override
	protected void validateType(final Node errorNode, final Annotation annotationBinding, final IProblemRequestor problemRequestor, Type type, String canonicalItemName) {
		if (type instanceof ParameterizedType) {
			type = ((ParameterizedType)type).getParameterizableType();
		}
		
		if(!TypeUtils.Type_TIMESTAMP.equals(type)){
			problemRequestor.acceptProblem(
				errorNode,
				IProblemRequestor.PROPERTY_ONLY_VALID_FOR_PRIMITIVE_TYPE,
				new String[]{canonicalAnnotationName, IEGLConstants.KEYWORD_TIMESTAMP});
		}
	}
}
