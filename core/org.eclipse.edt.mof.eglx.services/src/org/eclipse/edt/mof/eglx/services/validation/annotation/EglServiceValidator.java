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

import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.annotation.IAnnotationValidationRule;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.eglx.services.messages.ResourceKeys;




public class EglServiceValidator implements IAnnotationValidationRule {
	@Override
	public void validate(Node errorNode, Node target, Element targetElement, Annotation annotation, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		if(annotation == null){
			return;
		}
		String method = (String)annotation.getValue("method");
		if(!(method != null && "post".equalsIgnoreCase(method.toLowerCase()))){
			problemRequestor.acceptProblem(errorNode, ResourceKeys.XXXREST_NO_METHOD, IMarker.SEVERITY_ERROR, new String[] {}, ResourceKeys.getResourceBundleForKeys());
		}
		
	}
}