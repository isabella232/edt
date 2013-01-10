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

import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;


/**
 * @author svihovec
 */
public class PropertyApplicableForStringTypeOnlyAnnotationValidator extends PropertyApplicableForCertainPrimitiveTypeOnlyAnnotationValidator {
	
	public PropertyApplicableForStringTypeOnlyAnnotationValidator(String canonicalAnnotationName) {
		super(canonicalAnnotationName);
	}
	
	@Override
	protected void validateType(final Node errorNode, final Annotation annotationBinding, final IProblemRequestor problemRequestor, Type type, String canonicalItemName) {
		if(!TypeUtils.isTextType(type)){
			problemRequestor.acceptProblem(
				errorNode,
				IProblemRequestor.PROPERTY_STRING_PRIMITIVE_REQUIRED,
				new String[]{canonicalAnnotationName, BindingUtil.getShortTypeString(type, false)});
		}
	}
}
