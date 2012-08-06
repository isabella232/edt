/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.MofConversion;
import org.eclipse.edt.mof.egl.SequenceType;

public class StructTextValidator extends
		AbstractStructParameterAnnotationValidator {

	
	public void validate(Annotation annotation, Node errorNode, Member targetBinding, IProblemRequestor problemRequestor) {
		super.validate(annotation, errorNode, targetBinding, problemRequestor);
		
		if (targetBinding != null && isValidType(targetBinding)) {
			if (targetBinding.getType() instanceof SequenceType) {
				validateLengthNotSpecified(annotation, errorNode, problemRequestor);
			}
			else {
				//Must specify both length
				validateLengthSpecified(annotation, errorNode, problemRequestor);
				validateLength(annotation, errorNode, problemRequestor);
			}
		}
	}
	
	
	private void validateLength(Annotation ann, Node errorNode, IProblemRequestor problemRequestor) {
		Integer length = (Integer)ann.getValue("length");
		if (length != null) {
			if (length.intValue() < 1 || length.intValue() > Integer.MAX_VALUE) {
				problemRequestor.acceptProblem(errorNode, IProblemRequestor.AS400_BAD_LENGTH, new String[] {length.toString(), getName(), Integer.toString(Integer.MAX_VALUE)});
			}
		}
	}

	protected void validateLengthNotSpecified(Annotation ann, Node errorNode, IProblemRequestor problemRequestor) {
		if (ann.getValue("length") != null) {
			problemRequestor.acceptProblem(errorNode, IProblemRequestor.AS400_PROPERTY_NOT_ALLOWED, new String[] {"length", getName()});
		}
	}

	protected void validateLengthSpecified(Annotation ann, Node errorNode, IProblemRequestor problemRequestor) {
		if (ann.getValue("length") == null) {
			problemRequestor.acceptProblem(errorNode, IProblemRequestor.AS400_PROPERTY_REQUIRED, new String[] {"length", getName()});
		}
	}
	
	
	@Override
	protected List<String> getSupportedTypes() {
		List<String> list = new ArrayList<String>();
		list.add(MofConversion.Type_EGLString);
		return list;
	}
	@Override
	protected String getName() {
		return "StructText";
	}
}
