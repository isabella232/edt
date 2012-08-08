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
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.validation.type.PrimitiveTypeValidator.DateTimePattern;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.MofConversion;
import org.eclipse.edt.mof.egl.TimestampType;
import org.eclipse.edt.mof.eglx.jtopen.messages.IBMiResourceKeys;


public class StructTimestampValidator extends
		AbstractStructParameterAnnotationValidator {

	
	public void validate(Annotation annotation, Node errorNode, Member targetBinding, IProblemRequestor problemRequestor) {
		super.validate(annotation, errorNode, targetBinding, problemRequestor);
		
		if (targetBinding != null && isValidType(targetBinding.getType())) {
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
	  						IMarker.SEVERITY_ERROR,
							new String[] { pattern}, IBMiResourceKeys.getResourceBundleForKeys());
	  						
	  			}
	  		}
		}
	}

	protected void validatePatternNotSpecified(Annotation ann, Node errorNode, IProblemRequestor problemRequestor) {
		if (ann.getValue("eglPattern") != null) {
			problemRequestor.acceptProblem(errorNode, IBMiResourceKeys.AS400_PROPERTY_NOT_ALLOWED, IMarker.SEVERITY_ERROR, new String[] {"eglPattern", getName()}, IBMiResourceKeys.getResourceBundleForKeys());
		}
	}

	protected void validatePatternSpecified(Annotation ann, Node errorNode, IProblemRequestor problemRequestor) {
		if (ann.getValue("eglPattern") == null) {
			problemRequestor.acceptProblem(errorNode, IBMiResourceKeys.AS400_PROPERTY_REQUIRED, IMarker.SEVERITY_ERROR, new String[] {"eglPattern", getName()}, IBMiResourceKeys.getResourceBundleForKeys());
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
