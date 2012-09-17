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

import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;


public class PropertyApplicableForNonDateTimeTypeAnnotationValidator extends PropertyApplicableForCertainPrimitiveTypeOnlyAnnotationValidator {
	
	public PropertyApplicableForNonDateTimeTypeAnnotationValidator(String canonicalAnotationName) {
		super(canonicalAnotationName);
	}
	
	@Override
	protected void validateType(final Node errorNode, final Annotation annotationBinding, final IProblemRequestor problemRequestor, Type type, String canonicalItemName) {
		if(TypeUtils.Type_DATE.equals(type) || TypeUtils.Type_TIME.equals(type)){
			problemRequestor.acceptProblem(
				errorNode,
				IProblemRequestor.INVALID_FORM_FIELD_NOT_SUPPORTED_FOR_DATETIME,
				new String[]{canonicalAnnotationName, canonicalItemName});
		}
	}
}
