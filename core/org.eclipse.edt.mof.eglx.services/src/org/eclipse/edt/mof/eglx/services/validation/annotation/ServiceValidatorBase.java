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

import java.util.Map;

import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.annotation.IAnnotationValidationRule;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Container;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.Handler;
import org.eclipse.edt.mof.egl.Library;
import org.eclipse.edt.mof.egl.Program;
import org.eclipse.edt.mof.egl.Service;
import org.eclipse.edt.mof.eglx.services.messages.ResourceKeys;




public abstract class ServiceValidatorBase implements IAnnotationValidationRule {
	 protected IProblemRequestor problemRequestor;
	 protected ICompilerOptions compilerOptions;
	@Override
	public void validate(Node errorNode, Node target, Element targetBinding, Map<String, Object> allAnnotationsAndFields, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		Annotation annotation = targetBinding.getAnnotation(getAnnotationName());
		if(annotation == null){
			return;
		}
		this.problemRequestor = problemRequestor;
		this.compilerOptions = compilerOptions;
		validateAnnotation(annotation, errorNode, (NestedFunction)target, targetBinding);
	}
	
	protected void validateAnnotation(Annotation annotation, Node errorNode, NestedFunction target, Element targetBinding) {
		if(targetBinding instanceof Function){
			validateContainerIsCorrect(((Function)targetBinding).getContainer(), target);
		}
	}
	private void validateContainerIsCorrect(Container part, NestedFunction errorNode) {
		// Only allowed on function of Programs, Libraries, Services, and Basic Handlers

		if (part != null) {
			if (part instanceof Program) {
				return;
			}
			if (part instanceof Library) {
				return;
			}
			if (part instanceof Service) {
				return;
			}
			if (part instanceof Handler && ((Handler) part).getStereotype() == null) {
				//must be basic handler!
				return;
			}
		}
		
		//If we got this far, the container is invalid!
		problemRequestor.acceptProblem(errorNode, ResourceKeys.INVALID_CONTAINER, IMarker.SEVERITY_ERROR, new String[] {errorNode.getName().getCaseSensitiveIdentifier()}, ResourceKeys.getResourceBundleForKeys());
	}
	
	protected abstract String getAnnotationName();
}