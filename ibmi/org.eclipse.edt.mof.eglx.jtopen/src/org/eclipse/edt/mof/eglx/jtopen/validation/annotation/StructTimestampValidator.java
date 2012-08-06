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
import org.eclipse.edt.compiler.internal.core.validation.type.PrimitiveTypeValidator.DateTimePattern;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.MofConversion;
import org.eclipse.edt.mof.egl.TimestampType;


public class StructTimestampValidator extends
		AbstractStructParameterAnnotationValidator {

	
	public void validate(Annotation annotation, Node errorNode, Member targetBinding, IProblemRequestor problemRequestor) {
		super.validate(annotation, errorNode, targetBinding, problemRequestor);
		
		if (targetBinding != null && isValidType(targetBinding)) {
			if (targetBinding.getType() instanceof TimestampType &&
					!"null".equalsIgnoreCase(((TimestampType)targetBinding.getType()).getPattern())) {
				validatePatternNotSpecified(annotation, errorNode, problemRequestor);
			}
			else {
				//Must specify pattern
				validatePatternSpecified(annotation, errorNode, problemRequestor);
				validatePattern(annotation, errorNode, problemRequestor);
			}
		}
	}
	
	
	private void validatePattern(Annotation ann, Node errorNode, IProblemRequestor problemRequestor) {
		String pattern = (String)ann.getValue("eglPattern");
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

	protected void validatePatternNotSpecified(Annotation ann, Node errorNode, IProblemRequestor problemRequestor) {
		if (ann.getValue("eglPattern") != null) {
			problemRequestor.acceptProblem(errorNode, IProblemRequestor.AS400_PROPERTY_NOT_ALLOWED, new String[] {"eglPattern", getName()});
		}
	}

	protected void validatePatternSpecified(Annotation ann, Node errorNode, IProblemRequestor problemRequestor) {
		if (ann.getValue("eglPattern") == null) {
			problemRequestor.acceptProblem(errorNode, IProblemRequestor.AS400_PROPERTY_REQUIRED, new String[] {"eglPattern", getName()});
		}
	}

	@Override
	protected List<String> getSupportedTypes() {
		List<String> list = new ArrayList<String>();
		list.add(MofConversion.Type_EGLTimestamp);
		return list;
	}
	@Override
	protected String getName() {
		return "StructTimestamp";
	}
}
