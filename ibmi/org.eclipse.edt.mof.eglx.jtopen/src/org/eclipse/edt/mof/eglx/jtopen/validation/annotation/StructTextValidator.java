/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.mof.eglx.jtopen.validation.annotation;

import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.SequenceType;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;
import org.eclipse.edt.mof.eglx.jtopen.messages.IBMiResourceKeys;

public class StructTextValidator extends AbstractStructParameterAnnotationValidator {

	@Override
	protected void validateType(Annotation annotation, Node errorNode, Node target, Type type) {
		super.validateType(annotation, errorNode, target, type);
		
		if (type != null && isValidType(type)) {
			if (type instanceof SequenceType) {
				validateLengthNotSpecified(annotation, errorNode, target);
			}
			else {
				//Must specify both length
				validateLengthSpecified(annotation, errorNode, target);
				validateLength(annotation, errorNode, target);
			}
		}
	}
	
	
	private void validateLength(Annotation ann, Node errorNode, Node target) {
		Integer length = (Integer)ann.getValue("length");
		if (length != null) {
			if (length.intValue() < 1 || length.intValue() > Integer.MAX_VALUE) {
				problemRequestor.acceptProblem(errorNode, IBMiResourceKeys.AS400_BAD_LENGTH, IMarker.SEVERITY_ERROR, new String[] {length.toString(), getName(), Integer.toString(Integer.MAX_VALUE)}, IBMiResourceKeys.getResourceBundleForKeys());
			}
		}
	}

	protected void validateLengthNotSpecified(Annotation ann, Node errorNode, Node target) {
		if (ann.getValue("length") != null) {
			problemRequestor.acceptProblem(errorNode, IBMiResourceKeys.AS400_PROPERTY_NOT_ALLOWED, IMarker.SEVERITY_ERROR, new String[] {"length", getName()}, IBMiResourceKeys.getResourceBundleForKeys());
		}
	}

	protected void validateLengthSpecified(Annotation ann, Node errorNode, Node target) {
		if (ann.getValue("length") == null) {
			problemRequestor.acceptProblem(errorNode, IBMiResourceKeys.AS400_PROPERTY_REQUIRED, IMarker.SEVERITY_ERROR, new String[] {"length", getName()}, IBMiResourceKeys.getResourceBundleForKeys());
		}
	}
	
	
	@Override
	protected Type getSupportedType() {
		return TypeUtils.Type_STRING;
	}
	@Override
	protected String getName() {
		return "StructText";
	}
}
