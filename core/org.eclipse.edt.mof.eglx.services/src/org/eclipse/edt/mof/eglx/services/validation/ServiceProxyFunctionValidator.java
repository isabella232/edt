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
package org.eclipse.edt.mof.eglx.services.validation;

import org.eclipse.edt.compiler.binding.IRPartBinding;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Statement;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.validation.AbstractFunctionValidator;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.Handler;
import org.eclipse.edt.mof.egl.Library;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Program;
import org.eclipse.edt.mof.egl.Service;
import org.eclipse.edt.mof.eglx.services.messages.ResourceKeys;


/**
 * @author demurray
 */
public abstract class ServiceProxyFunctionValidator extends AbstractFunctionValidator {
	
	public boolean visit(NestedFunction nestedFunction){
		if(nestedFunction.getName().resolveMember() instanceof Function){
			validateContainerIsCorrect(((IRPartBinding)declaringPart).getIrPart(), nestedFunction, problemRequestor);		
			validateFunctionBodyIsEmpty((Function)nestedFunction.getName().resolveMember(), nestedFunction);
			validate(nestedFunction);
		}
		return true;
	}

	private void validateFunctionBodyIsEmpty(Function function, NestedFunction nestedFunction) {
		if (nestedFunction != null && nestedFunction.getStmts() != null) {
			for(Object stmt : nestedFunction.getStmts()) {
				if(stmt instanceof Statement){
					problemRequestor.acceptProblem((Statement)stmt, IProblemRequestor.PROXY_FUNCTIONS_CANNOT_HAVE_STMTS, IMarker.SEVERITY_ERROR, new String[] {function.getCaseSensitiveName()});
				}
			}
		}
	}
	
	private void validateContainerIsCorrect(Part part, NestedFunction errorNode, IProblemRequestor problemRequestor) {
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
		
		Annotation annotation = getAnnotation((Function)errorNode.getName().resolveMember());
		
		//If we got this far, the container is invalid!
		problemRequestor.acceptProblem(errorNode, ResourceKeys.INVALID_CONTAINER, IMarker.SEVERITY_ERROR, new String[] {(annotation == null ? "" : annotation.getEClass().getETypeSignature()), errorNode.getName().getCaseSensitiveIdentifier()}, ResourceKeys.getResourceBundleForKeys());
	}
	
	protected abstract void validate(NestedFunction nestedFunction);
	protected abstract String getName();
	protected abstract Annotation getAnnotation(Function function);
}
