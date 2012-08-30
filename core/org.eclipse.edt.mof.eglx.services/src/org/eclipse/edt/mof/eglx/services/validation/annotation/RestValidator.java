/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.mof.eglx.services.validation.annotation;

import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.eglx.services.messages.ResourceKeys;


public class RestValidator extends ServiceValidatorBase{

	@Override
	protected String getAnnotationName(){
		return "eglx.rest.Rest";
	}
	
	@Override
	protected void validateAnnotation(Annotation annotation, Node errorNode, NestedFunction target, Element targetBinding) {
		super.validateAnnotation(annotation, errorNode, target, targetBinding);
		String method = (String)annotation.getValue("method");
		if(method == null || method.isEmpty()){
			problemRequestor.acceptProblem(errorNode, ResourceKeys.XXXREST_NO_METHOD, IMarker.SEVERITY_ERROR, new String[] {}, ResourceKeys.getResourceBundleForKeys());
		}
	}
	protected boolean methodIsValid(Annotation annotation){
		return annotation.getValue("method") != null;
	}
	
}
