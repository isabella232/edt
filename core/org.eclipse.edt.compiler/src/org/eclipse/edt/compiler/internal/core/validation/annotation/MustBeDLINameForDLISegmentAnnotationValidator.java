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

import java.util.Map;

import org.eclipse.edt.compiler.binding.FieldContentValidationAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author demurray
 */
public class MustBeDLINameForDLISegmentAnnotationValidator extends FieldContentValidationAnnotationTypeBinding {
	
	IAnnotationTypeBinding annotationTypeBinding;
	
	public MustBeDLINameForDLISegmentAnnotationValidator(IAnnotationTypeBinding annotationTypeBinding) {
		super(InternUtil.internCaseSensitive("MustBeDLINameAnnotationValidator"));
		this.annotationTypeBinding = annotationTypeBinding;
	}
	
	public void validate(final Node errorNode, Node target, IDataBinding containerBinding, String canonicalContainerName, Map allAnnotations, final IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		IAnnotationBinding annotationBinding = (IAnnotationBinding) allAnnotations.get(annotationTypeBinding.getName());
		if(annotationBinding != null && annotationBinding.getValue() != null) {
			final String name = annotationBinding.getValue().toString();
			MustBeDLINameAnnotationValidator.checkDLIName(name, new MustBeDLINameAnnotationValidator.DLINameProblemRequestor() {
				
				public void nameTooLong() {
					problemRequestor.acceptProblem(
						errorNode,
						IProblemRequestor.DLI_NAME_TOO_LONG,
						new String[] {name});
				}
				
				public void badFirstCharacter() {
					problemRequestor.acceptProblem(
						errorNode,
						IProblemRequestor.DLI_NAME_BAD_FIRST_CHAR,
						new String[] {name});
				}
				
				public void badCharacter() {
					problemRequestor.acceptProblem(
						errorNode,
						IProblemRequestor.DLI_NAME_BAD_CHAR,
						new String[] {name});
				}
			});
		}
	}
}
