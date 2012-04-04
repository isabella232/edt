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

import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author svihovec
 *
 */
public class RequiredFieldInComplexPropertyAnnotationTypeBinding extends AnnotationValidationAnnotationTypeBinding {

	private String[] requiredAnnotations;
	private String subTypeName;
	
	public RequiredFieldInComplexPropertyAnnotationTypeBinding(String[] annotationNames, String subTypeName) {
		super(InternUtil.internCaseSensitive("RequiredAnnotation"));
		
		this.requiredAnnotations = annotationNames;
		this.subTypeName = subTypeName;
	}

	public void validate(Node errorNode, Node target, ITypeBinding targetTypeBinding, Map allAnnotations, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions){
		for (int i = 0; i < requiredAnnotations.length; i++) {
			if(!allAnnotations.containsKey(InternUtil.intern(requiredAnnotations[i]))){
				problemRequestor.acceptProblem(
					errorNode,
					IProblemRequestor.REQUIRED_FIELD_IN_COMPLEX_ANNOTATION,
					new String[] {
						requiredAnnotations[i],
						subTypeName
					});
			}
		}
	}
}
