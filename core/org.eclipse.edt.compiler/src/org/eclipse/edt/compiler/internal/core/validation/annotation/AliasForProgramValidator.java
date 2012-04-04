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
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;



/**
 * @author Dave Murray
 */
public class AliasForProgramValidator implements IValueValidationRule {
	
	public void validate(final Node errorNode, Node target, IAnnotationBinding annotationBinding, final IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		if(annotationBinding.getValue() != null  && annotationBinding.getValue()!= IBinding.NOT_FOUND_BINDING){
			final String value = (String) annotationBinding.getValue();
			checkNotEmpty(value, errorNode, problemRequestor);
		}
	}
	
	private void checkNotEmpty(String value, Node errorNode, IProblemRequestor problemRequestor) {
		if(value.length() == 0) {
			problemRequestor.acceptProblem(errorNode, IProblemRequestor.ALIAS_CANNOT_BE_EMPTY);
		}
	}
}
