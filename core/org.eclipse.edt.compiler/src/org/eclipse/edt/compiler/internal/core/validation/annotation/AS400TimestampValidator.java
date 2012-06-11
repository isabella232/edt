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

import org.eclipse.edt.compiler.binding.AS400TimestampAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.validation.type.PrimitiveTypeValidator.DateTimePattern;

public class AS400TimestampValidator extends
		AbstractAS400ParameterAnnotaionValidator {

	
	public void validate(IAnnotationBinding annotation, Node errorNode, ITypeBinding targetTypeBinding, IProblemRequestor problemRequestor) {
		super.validate(annotation, errorNode, targetTypeBinding, problemRequestor);
		
		if (Binding.isValidBinding(targetTypeBinding) && isValidType(targetTypeBinding)) {
			if (((PrimitiveTypeBinding)targetTypeBinding).getPattern()!= null && ((PrimitiveTypeBinding)targetTypeBinding).getPattern().length() > 0) {
				validatePatternNotSpecified(annotation, errorNode, problemRequestor);
			}
			else {
				//Must specify pattern
				validatePatternSpecified(annotation, errorNode, problemRequestor);
				validatePattern(annotation, errorNode, problemRequestor);
			}
		}
	}
	
	
	private void validatePattern(IAnnotationBinding ann, Node errorNode, IProblemRequestor problemRequestor) {
		String pattern = getPattern(ann);
		if (pattern != null) {
	  		DateTimePattern dtPat = new DateTimePattern( pattern );
	  		if( !dtPat.isValidTimeStampPattern() ) {
	  			Integer[] errors = dtPat.getErrorMessageNumbers();
	  			for( int i = 0; i < errors.length; i++ ) {
	  				problemRequestor.acceptProblem(errorNode,
	  						errors[i].intValue(),
							new String[] { pattern});
	  						
	  			}
	  		}
		}
	}

	protected void validatePatternNotSpecified(IAnnotationBinding ann, Node errorNode, IProblemRequestor problemRequestor) {
		if (getPattern(ann) != null) {
			problemRequestor.acceptProblem(errorNode, IProblemRequestor.AS400_PROPERTY_NOT_ALLOWED, new String[] {"eglPattern", getName()});
		}
	}

	protected void validatePatternSpecified(IAnnotationBinding ann, Node errorNode, IProblemRequestor problemRequestor) {
		if (getPattern(ann) == null) {
			problemRequestor.acceptProblem(errorNode, IProblemRequestor.AS400_PROPERTY_REQUIRED, new String[] {"eglPattern", getName()});
		}
	}
	
	
	protected String getPattern(IAnnotationBinding annotation) {
		return (String)getValue(annotation, "eglPattern");
	}
	
	
	@Override
	protected List<Primitive> getSupportedTypes() {
		List<Primitive> list = new ArrayList<Primitive>();
		list.add(Primitive.TIMESTAMP);
		return list;
	}

	@Override
	protected String getName() {
		return AS400TimestampAnnotationTypeBinding.caseSensitiveName;
	}

	@Override
	protected String getInternedName() {
		return AS400TimestampAnnotationTypeBinding.name;
	}

}
