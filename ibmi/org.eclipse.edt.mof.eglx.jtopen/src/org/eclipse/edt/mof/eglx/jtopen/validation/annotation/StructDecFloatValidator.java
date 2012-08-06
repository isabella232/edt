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

import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.mof.egl.Annotation;

public class StructDecFloatValidator extends StructDecimalValidator {
	
	@Override
	protected String getName() {
		return "StructDecFloat";
	}
	
	protected void validateLengthAndDecimalsSpecified(Annotation ann, Node errorNode, IProblemRequestor problemRequestor) {
		if ((Integer)ann.getValue("length") == null) {
			problemRequestor.acceptProblem(errorNode, IProblemRequestor.AS400_PROPERTY_REQUIRED, new String[] {"length", getName()});
		}
	}

	protected void validateLengthAndDecimalsNotSpecified(Annotation ann, Node errorNode, IProblemRequestor problemRequestor) {
		if ((Integer)ann.getValue("length") != null) {
			problemRequestor.acceptProblem(errorNode, IProblemRequestor.AS400_PROPERTY_NOT_ALLOWED, new String[] {"length", getName()});
		}
	}
	
	protected void validateDecimals(Annotation ann, Node errorNode, IProblemRequestor problemRequestor) {
		//do nothing
	}

	

}
