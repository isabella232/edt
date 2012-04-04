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

import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.ValueValidationAnnotationTypeBinding;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author demurray
 */
public class NonNegativeIntegerAnnotationValidator extends ValueValidationAnnotationTypeBinding {

	private String annotationName;
	private int problemKindForError;

	public NonNegativeIntegerAnnotationValidator(String annotationName, int problemKindForError) {
		super(InternUtil.internCaseSensitive("NonNegativeIntegerAnnotationValidator"));
		this.problemKindForError = problemKindForError;
		this.annotationName = annotationName;
	}
	
	public void validate(Node errorNode, Node target, IAnnotationBinding annotationBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {		
		boolean valueIsValid = false;
		try {
			if( Integer.parseInt(annotationBinding.getValue().toString()) >= 0) {
				valueIsValid = true;
			}
		}
		catch(NumberFormatException e) {			
		}
		
		if(!valueIsValid) {
			problemRequestor.acceptProblem(
				errorNode,
				problemKindForError,
				new String[] {annotationName, getCanonicalName(target)});
		}
	}
	
	private String getCanonicalName(Node target) {
		return getEnclosingPart(target).getName().getCanonicalName();
	}
	
	private Part getEnclosingPart(Node node) {
		if(node instanceof Part) {
			return (Part) node;			
		}
		return getEnclosingPart(node.getParent());
	}
}
