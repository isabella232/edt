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
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.validation.DefaultTypeValidator.DateTimePattern;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.TimestampType;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;
import org.eclipse.edt.mof.eglx.jtopen.messages.IBMiResourceKeys;


public class StructTimestampValidator extends AbstractStructParameterAnnotationValidator {

	@Override
	protected void validateType(Annotation annotation, Node errorNode, Node target, Type type) {
		super.validateType(annotation, errorNode, target, type);
		
		if (type != null && isValidType(type)) {
			if (type instanceof TimestampType &&
					!"null".equalsIgnoreCase(((TimestampType)type).getPattern())) {
				validatePatternNotSpecified(annotation, errorNode, target);
			}
			else {
				//Must specify pattern
				validatePatternSpecified(annotation, errorNode, target);
				validatePattern(annotation, errorNode, target);
			}
		}
	}
	
	
	private void validatePattern(Annotation ann, Node errorNode, Node target) {
		String pattern = (String)ann.getValue("eglPattern");
		if (pattern != null) {
	  		DateTimePattern dtPat = new DateTimePattern( pattern );
	  		if( !dtPat.isValidTimeStampPattern() ) {
	  			Integer[] errors = dtPat.getErrorMessageNumbers();
	  			for( int i = 0; i < errors.length; i++ ) {
	  				problemRequestor.acceptProblem(errorNode,
	  						errors[i].intValue(), 
	  						IMarker.SEVERITY_ERROR,
							new String[] { pattern});
	  						
	  			}
	  		}
		}
	}

	protected void validatePatternNotSpecified(Annotation ann, Node errorNode, Node target) {
		if (ann.getValue("eglPattern") != null) {
			problemRequestor.acceptProblem(errorNode, IBMiResourceKeys.AS400_PROPERTY_NOT_ALLOWED, IMarker.SEVERITY_ERROR, new String[] {"eglPattern", getName()}, IBMiResourceKeys.getResourceBundleForKeys());
		}
	}

	protected void validatePatternSpecified(Annotation ann, Node errorNode, Node target) {
		if (ann.getValue("eglPattern") == null) {
			problemRequestor.acceptProblem(errorNode, IBMiResourceKeys.AS400_PROPERTY_REQUIRED, IMarker.SEVERITY_ERROR, new String[] {"eglPattern", getName()}, IBMiResourceKeys.getResourceBundleForKeys());
		}
	}

	@Override
	protected Type getSupportedType() {
		return TypeUtils.Type_TIMESTAMP;
	}
	@Override
	protected String getName() {
		return "StructTimestamp";
	}
}
