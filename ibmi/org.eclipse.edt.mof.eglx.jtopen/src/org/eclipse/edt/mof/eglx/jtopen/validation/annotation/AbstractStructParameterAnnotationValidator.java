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
package org.eclipse.edt.mof.eglx.jtopen.validation.annotation;

import java.util.List;

import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.annotation.IAnnotationValidationRule;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.eglx.jtopen.messages.IBMiResourceKeys;
import org.eclipse.edt.mof.utils.NameUtile;


public abstract class AbstractStructParameterAnnotationValidator implements IAnnotationValidationRule {
	
	protected abstract List<String> getSupportedTypes();
	
	public void validate(Node errorNode, Node target, Element targetElement, Annotation annotation, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		if (!(targetElement instanceof Field || targetElement instanceof FunctionParameter)) {
			return;
		}
		validate(annotation, errorNode, targetElement, problemRequestor);
	}


	protected void validate(Annotation annotation, Node errorNode, Element targetElement, IProblemRequestor problemRequestor) {
		validateTypeIsSupported(errorNode, (Member)targetElement, problemRequestor);
	}
	
	protected void validateTypeIsSupported(Node errorNode, Member targetElement, IProblemRequestor problemRequestor) {

		if (((Member)targetElement).isNullable()) {
			problemRequestor.acceptProblem(errorNode, IBMiResourceKeys.AS400_ANNOTATION_NULLABLE_TYPE_INVALID, IMarker.SEVERITY_ERROR, new String[] {getName(), ((Member)targetElement).getCaseSensitiveName() + "?"}, IBMiResourceKeys.getResourceBundleForKeys());
		}
		
		if (!isValidType(((Member)targetElement).getType())) {
			problemRequestor.acceptProblem(errorNode, IBMiResourceKeys.AS400_ANNOTATION_TYPE_MISMATCH, IMarker.SEVERITY_ERROR, new String[] {getName(), ((Member)targetElement).getCaseSensitiveName()}, IBMiResourceKeys.getResourceBundleForKeys());
		}
	}
	
	protected abstract String getName();
	
	protected String getInternedName() {
		return NameUtile.getAsName(getName());
	}
	
	protected boolean isValidType(Type typeBinding) {
				
		if (typeBinding != null) {
			return getSupportedTypes().contains(typeBinding.getMofSerializationKey());
		}
		else {
			return true;  //return true to avoid excess error messages
		}
	}
	
	
}
