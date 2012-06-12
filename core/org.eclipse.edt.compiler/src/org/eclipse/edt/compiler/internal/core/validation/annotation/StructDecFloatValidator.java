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
package org.eclipse.edt.compiler.internal.core.validation.annotation;

import org.eclipse.edt.compiler.binding.StructDecFloatAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;

public class StructDecFloatValidator extends StructDecimalValidator {
	
	@Override
	protected String getName() {
		return StructDecFloatAnnotationTypeBinding.caseSensitiveName;
	}
	
	@Override
	protected String getInternedName() {
		return StructDecFloatAnnotationTypeBinding.name;
	}
	
	protected void validateLengthAndDecimalsSpecified(IAnnotationBinding ann, Node errorNode, IProblemRequestor problemRequestor) {
		if (getLength(ann) == null) {
			problemRequestor.acceptProblem(errorNode, IProblemRequestor.AS400_PROPERTY_REQUIRED, new String[] {"length", getName()});
		}
	}

	protected void validateLengthAndDecimalsNotSpecified(IAnnotationBinding ann, Node errorNode, IProblemRequestor problemRequestor) {
		if (getLength(ann) != null) {
			problemRequestor.acceptProblem(errorNode, IProblemRequestor.AS400_PROPERTY_NOT_ALLOWED, new String[] {"length", getName()});
		}
	}
	
	protected void validateDecimals(IAnnotationBinding ann, Node errorNode, IProblemRequestor problemRequestor) {
		//do nothing
	}

	

}
