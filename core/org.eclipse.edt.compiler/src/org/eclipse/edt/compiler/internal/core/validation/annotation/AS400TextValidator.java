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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.binding.AS400TextAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;

public class AS400TextValidator extends
		AbstractAS400ParameterAnnotaionValidator {

	
	public void validate(IAnnotationBinding annotation, Node errorNode, ITypeBinding targetTypeBinding, IProblemRequestor problemRequestor) {
		super.validate(annotation, errorNode, targetTypeBinding, problemRequestor);
		
		if (Binding.isValidBinding(targetTypeBinding) && isValidType(targetTypeBinding)) {
			if (((PrimitiveTypeBinding)targetTypeBinding).getLength() > 0) {
				validateLengthNotSpecified(annotation, errorNode, problemRequestor);
			}
			else {
				//Must specify both length
				validateLengthSpecified(annotation, errorNode, problemRequestor);
				validateLength(annotation, errorNode, problemRequestor);
			}
		}
	}
	
	
	private void validateLength(IAnnotationBinding ann, Node errorNode, IProblemRequestor problemRequestor) {
		Integer length = getLength(ann);
		if (length != null) {
			if (length.intValue() < 1 || length.intValue() > Integer.MAX_VALUE) {
				problemRequestor.acceptProblem(errorNode, IProblemRequestor.AS400_BAD_LENGTH, new String[] {length.toString(), getName(), Integer.toString(Integer.MAX_VALUE)});
			}
		}
	}

	protected void validateLengthNotSpecified(IAnnotationBinding ann, Node errorNode, IProblemRequestor problemRequestor) {
		if (getLength(ann) != null) {
			problemRequestor.acceptProblem(errorNode, IProblemRequestor.AS400_PROPERTY_NOT_ALLOWED, new String[] {"length", getName()});
		}
	}

	protected void validateLengthSpecified(IAnnotationBinding ann, Node errorNode, IProblemRequestor problemRequestor) {
		if (getLength(ann) == null) {
			problemRequestor.acceptProblem(errorNode, IProblemRequestor.AS400_PROPERTY_REQUIRED, new String[] {"length", getName()});
		}
	}
	
	
	protected Integer getLength(IAnnotationBinding annotation) {
		return (Integer)getValue(annotation, "length");
	}
	
	
	@Override
	protected List<Primitive> getSupportedTypes() {
		List<Primitive> list = new ArrayList<Primitive>();
		list.add(Primitive.STRING);
		return list;
	}

	@Override
	protected String getName() {
		return AS400TextAnnotationTypeBinding.caseSensitiveName;
	}

	@Override
	protected String getInternedName() {
		return AS400TextAnnotationTypeBinding.name;
	}

}
