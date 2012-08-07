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
import org.eclipse.edt.mof.egl.FixedPrecisionType;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.MofConversion;

public abstract class StructDecimalValidator extends
		AbstractStructParameterAnnotationValidator {

	public void validate(Annotation annotation, Node errorNode, Member targetBinding, IProblemRequestor problemRequestor) {
		super.validate(annotation, errorNode, targetBinding, problemRequestor);
		
		if (targetBinding != null && isValidType(targetBinding.getType())) {
			if(targetBinding.getType() instanceof FixedPrecisionType ) {
				validateLengthAndDecimalsNotSpecified(annotation, errorNode, problemRequestor);
			}
			else {
				//Must specify both length and decimals
				validateLengthAndDecimalsSpecified(annotation, errorNode, problemRequestor);
				validateLength(annotation, errorNode, problemRequestor);
				validateDecimals(annotation, errorNode, problemRequestor);
			}
		}
	}
	
	private void validateLength(Annotation ann, Node errorNode, IProblemRequestor problemRequestor) {
		Integer length = (Integer)ann.getValue("length");
		if (length != null) {
			if (length.intValue() < 1 || length.intValue() > 32) {
				problemRequestor.acceptProblem(errorNode, IProblemRequestor.AS400_BAD_LENGTH, new String[] {length.toString(), getName(), "32"});
			}
		}
	}

	protected void validateDecimals(Annotation ann, Node errorNode, IProblemRequestor problemRequestor) {
		Integer decimals = (Integer)ann.getValue("decimals");
		if (decimals != null) {
			if (decimals.intValue() < 0) {
				problemRequestor.acceptProblem(errorNode, IProblemRequestor.AS400_NEGATIVE_DECIMAL, new String[] {decimals.toString(), getName()});
			}
			Integer length = (Integer)ann.getValue("length");
			if (length != null && decimals.intValue() > length.intValue()) {
				problemRequestor.acceptProblem(errorNode, IProblemRequestor.AS400_BAD_DECIMAL, new String[] {decimals.toString(), getName(), length.toString()});
			}
		}
	}

	protected void validateLengthAndDecimalsNotSpecified(Annotation ann, Node errorNode, IProblemRequestor problemRequestor) {
		if (ann.getValue("length") != null) {
			problemRequestor.acceptProblem(errorNode, IProblemRequestor.AS400_PROPERTY_NOT_ALLOWED, new String[] {"length", getName()});
		}
		if (ann.getValue("decimals") != null) {
			problemRequestor.acceptProblem(errorNode, IProblemRequestor.AS400_PROPERTY_NOT_ALLOWED, new String[] {"decimals", getName()});
		}
	}

	protected void validateLengthAndDecimalsSpecified(Annotation ann, Node errorNode, IProblemRequestor problemRequestor) {
		if (ann.getValue("length") == null) {
			problemRequestor.acceptProblem(errorNode, IProblemRequestor.AS400_PROPERTY_REQUIRED, new String[] {"length", getName()});
		}
		if (ann.getValue("decimals") == null) {
			problemRequestor.acceptProblem(errorNode, IProblemRequestor.AS400_PROPERTY_REQUIRED, new String[] {"decimals", getName()});
		}
	}
	
	
	protected List<String> getSupportedTypes() {
		List<String> list = new ArrayList<String>();
		list.add(MofConversion.Type_EGLDecimal);
		return list;
	}
}
