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
package org.eclipse.edt.compiler.binding;

import java.util.Map;

import org.eclipse.edt.compiler.core.Boolean;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author svihovec
 *
 */
public class MutuallyExclusiveAnnotationAnnotationTypeBinding extends FieldContentValidationAnnotationTypeBinding {

	private String annotationName;
	private String[] mutuallyExclusiveWith;
	
	public MutuallyExclusiveAnnotationAnnotationTypeBinding(String annotationName, String[] mutuallyExclusiveWith) {
		super(InternUtil.internCaseSensitive("MutuallyExclusive"));
		
		this.annotationName = annotationName;
		this.mutuallyExclusiveWith = mutuallyExclusiveWith;
	}

	public void validate(Node errorNode, Node target, IDataBinding containerBinding, String canonicalContainerName, Map allAnnotations, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions){
		
		IAnnotationBinding aBinding = (IAnnotationBinding) allAnnotations.get(InternUtil.intern(annotationName));
		if(aBinding != null && Boolean.NO != aBinding.getValue()){
			for (int i = 0; i < mutuallyExclusiveWith.length; i++) {
				IAnnotationBinding anotherABinding = (IAnnotationBinding) allAnnotations.get(InternUtil.intern(mutuallyExclusiveWith[i]));
				if(anotherABinding != null) {
					if(!isNonApplicable(anotherABinding.getValue())) {
					problemRequestor.acceptProblem(
				            	errorNode,
								IProblemRequestor.PROPERTIES_MUTUALLY_EXCLUSIVE,
								new String[] {annotationName, mutuallyExclusiveWith[i]});	
					}
				}
			}
		}
	}

	private boolean isNonApplicable(Object value) {
		if(Boolean.NO == value) {
			return true;
		}
		if(value instanceof EnumerationDataBinding) {
			EnumerationDataBinding enumBinding = (EnumerationDataBinding) value;
			if(enumBinding.getName() == InternUtil.intern("none")) {
				return true;
			}
		}
		return false;
	}
}
