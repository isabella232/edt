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
import java.util.Map;

import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.annotation.IAnnotationValidationRule;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.utils.NameUtile;


public abstract class AbstractStructParameterAnnotationValidator implements IAnnotationValidationRule {
	
	protected abstract List<String> getSupportedTypes();
	
	public void validate(Node errorNode, Node target, Member targetBinding, Map allAnnotations, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		
		Annotation annotation = (Annotation)allAnnotations.get(getInternedName());
		validate(annotation, errorNode, targetBinding, problemRequestor);
	}


	public void validate(Annotation annotation, Node errorNode, Member targetBinding, IProblemRequestor problemRequestor) {
		validateTypeIsSupported(errorNode, targetBinding, problemRequestor);
	}
	
	protected void validateTypeIsSupported(Node errorNode, Member targetBinding, IProblemRequestor problemRequestor) {
		if (targetBinding != null && targetBinding.isNullable()) {
			problemRequestor.acceptProblem(errorNode, IProblemRequestor.AS400_ANNOTATION_NULLABLE_TYPE_INVALID, new String[] {getName(), targetBinding.getCaseSensitiveName() + "?"});
		}
		
		if (!isValidType(targetBinding)) {
			problemRequestor.acceptProblem(errorNode, IProblemRequestor.AS400_ANNOTATION_TYPE_MISMATCH, new String[] {getName(), targetBinding.getCaseSensitiveName()});
		}
	}
	
	protected abstract String getName();
	
	protected String getInternedName() {
		return NameUtile.getAsName(getName());
	}
	
	protected boolean isValidType(Member typeBinding) {
				
		if (typeBinding != null) {
			return getSupportedTypes().contains(typeBinding.getType().getMofSerializationKey());
		}
		else {
			return true;  //return true to avoid excess error messages
		}
	}
	
	
}
