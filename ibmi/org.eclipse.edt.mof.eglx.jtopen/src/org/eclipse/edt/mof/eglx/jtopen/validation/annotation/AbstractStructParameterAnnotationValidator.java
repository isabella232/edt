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

import java.util.Map;

import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.annotation.IAnnotationValidationRule;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.eglx.jtopen.messages.IBMiResourceKeys;
import org.eclipse.edt.mof.utils.NameUtile;


public abstract class AbstractStructParameterAnnotationValidator implements IAnnotationValidationRule {
	
	protected abstract Type getSupportedType();
	
	private String annotationPackage = "eglx.jtopen.annotations.";
	protected IProblemRequestor problemRequestor;
	protected ICompilerOptions compilerOptions;
	
	
	@Override
	public void validate(Node errorNode, Node target, Element targetBinding, Map<String, Object> allAnnotationsAndFields, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		if (targetBinding instanceof Field || 
				targetBinding instanceof FunctionParameter ||
				targetBinding instanceof Type) {
			Annotation annotation = targetBinding.getAnnotation(annotationPackage + getName());
			if(annotation == null){
				annotation = (Annotation)allAnnotationsAndFields.get(NameUtile.getAsName(annotationPackage + getName()));
			}
			if(annotation == null){
				return;
			}
			this.compilerOptions = compilerOptions;
			this.problemRequestor = problemRequestor;
			if (targetBinding instanceof Field || targetBinding instanceof FunctionParameter) {
				validateMember(annotation, errorNode, (Member)targetBinding);
				validateType(annotation, errorNode, ((Member)targetBinding).getType());
			}
			else if(targetBinding instanceof Type){
				validateType(annotation, errorNode, (Type)targetBinding);
			}
		}
	}


	protected void validateMember(Annotation annotation, Node errorNode, Member member) {
		if (member.isNullable()) {
			problemRequestor.acceptProblem(errorNode, IBMiResourceKeys.AS400_ANNOTATION_NULLABLE_TYPE_INVALID, IMarker.SEVERITY_ERROR, new String[] {getName(), member.getCaseSensitiveName() + "?"}, IBMiResourceKeys.getResourceBundleForKeys());
		}
	}
	
	protected void validateType(Annotation annotation, Node errorNode, Type type) {
		if (!isValidType(type)) {
			problemRequestor.acceptProblem(errorNode, IBMiResourceKeys.AS400_ANNOTATION_TYPE_MISMATCH, IMarker.SEVERITY_ERROR, new String[] {getName(), type.getClassifier().getName()}, IBMiResourceKeys.getResourceBundleForKeys());
		}
	}
	
	protected abstract String getName();
	
	protected String getInternedName() {
		return NameUtile.getAsName(getName());
	}
	
	protected boolean isValidType(Type typeBinding) {
		if (typeBinding != null && getSupportedType() != null &&
				typeBinding.getClassifier() instanceof EGLClass) {
			return getSupportedType().equals(typeBinding.getClassifier());
		}
		else {
			return true;  //return true to avoid excess error messages
		}
	}
	
	
}
