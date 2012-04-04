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
class AnnotationAssociationAnnotationTypeBinding extends FieldContentValidationAnnotationTypeBinding {

	private String annotationOne;
	private String annotationTwo;
	
	public AnnotationAssociationAnnotationTypeBinding(String annotationOne, String annotationTwo) {
		super(InternUtil.internCaseSensitive("Association"));
		
		this.annotationOne = annotationOne;
		this.annotationTwo = annotationTwo;
	}

	public void validate(Node errorNode, Node target, IDataBinding containerBinding, String canonicalContainerName, Map allAnnotations, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions){
		
		boolean hasAnnotationOne = allAnnotations.containsKey(annotationOne);
		boolean hasAnnotationTwo = allAnnotations.containsKey(annotationTwo);
		
		if((hasAnnotationOne && !hasAnnotationTwo) || (hasAnnotationTwo && !hasAnnotationOne)){ 
			problemRequestor.acceptProblem(
		            	errorNode,
						IProblemRequestor.PROPERTY_VALUE_MUST_BE_A_STRING_ARRAY_ARRAY,
						new String[] {getCaseSensitiveName()});	
		}
	}
}
