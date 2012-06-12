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
package org.eclipse.edt.compiler.internal.core.validation.annotation;

import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.binding.AnnotationFieldBinding;
import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;


public abstract class AbstractStructParameterAnnotaionValidator implements IAnnotationValidationRule {
	
	protected abstract List<Primitive> getSupportedTypes();
	
	public void validate(Node errorNode, Node target, ITypeBinding targetTypeBinding, Map allAnnotations, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		
		IAnnotationBinding annotation = (IAnnotationBinding)allAnnotations.get(getInternedName());
		validate(annotation, errorNode, targetTypeBinding, problemRequestor);
	}


	public void validate(IAnnotationBinding annotation, Node errorNode, ITypeBinding targetTypeBinding, IProblemRequestor problemRequestor) {
		validateTypeIsSupported(errorNode, targetTypeBinding, problemRequestor);
	}
	
	protected void validateTypeIsSupported(Node errorNode, ITypeBinding targetTypeBinding, IProblemRequestor problemRequestor) {
		if (Binding.isValidBinding(targetTypeBinding) && targetTypeBinding.isNullable()) {
			problemRequestor.acceptProblem(errorNode, IProblemRequestor.AS400_ANNOTATION_NULLABLE_TYPE_INVALID, new String[] {getName(), targetTypeBinding.getCaseSensitiveName() + "?"});
		}
		
		if (!isValidType(targetTypeBinding)) {
			problemRequestor.acceptProblem(errorNode, IProblemRequestor.AS400_ANNOTATION_TYPE_MISMATCH, new String[] {getName(), targetTypeBinding.getCaseSensitiveName()});
		}
	}
	
	protected abstract String getName();
	protected abstract String getInternedName();
	
	protected boolean isValidType(ITypeBinding typeBinding) {
				
		if (Binding.isValidBinding(typeBinding)) {
			if (ITypeBinding.PRIMITIVE_TYPE_BINDING == typeBinding.getKind()) {
				Primitive prim = ((PrimitiveTypeBinding)typeBinding).getPrimitive();
				List<Primitive> supportedPrims = getSupportedTypes();
				return supportedPrims.contains(prim);
			}
			else {
				return false;
			}
		}
		else {
			return true;  //return true to avoid excess error messages
		}
	}
	
	
	protected Object getValue(IAnnotationBinding ann, String fieldName) {
		if (!Binding.isValidBinding(ann)) {
			return null;
		}
		IDataBinding db = ann.findData(fieldName);
		if (!Binding.isValidBinding(db)) {
			return null;
		}
		return ((AnnotationFieldBinding)db).getValue();
	}
	
}
