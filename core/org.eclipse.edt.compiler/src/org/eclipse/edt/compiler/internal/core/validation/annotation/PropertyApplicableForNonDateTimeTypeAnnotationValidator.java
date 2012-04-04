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

import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IAnnotationTypeBinding;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;


/**
 * @author Dave Murray
 */
public class PropertyApplicableForNonDateTimeTypeAnnotationValidator extends PropertyApplicableForCertainPrimitiveTypeOnlyAnnotationValidator {
	
	public PropertyApplicableForNonDateTimeTypeAnnotationValidator(IAnnotationTypeBinding annotationType, String canonicalAnotationName) {
		super(annotationType, canonicalAnotationName);
	}
	
	protected void validatePrimitiveType(Node errorNode, IAnnotationBinding annotationBinding, IProblemRequestor problemRequestor, Primitive primitive, String canonicalItemName) {
		if(primitive == Primitive.DATE || primitive == Primitive.TIME) {
			problemRequestor.acceptProblem(
				errorNode,
				IProblemRequestor.INVALID_FORM_FIELD_NOT_SUPPORTED_FOR_DATETIME,
				new String[] {canonicalAnnotationName, canonicalItemName});
		}
	}
}
