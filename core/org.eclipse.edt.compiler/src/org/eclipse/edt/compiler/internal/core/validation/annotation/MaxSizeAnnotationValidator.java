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
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;


/**
 * @author svihovec
 *
 */
public class MaxSizeAnnotationValidator implements IValueValidationRule{
	
	/**
	 * value must be positive
	 */
	public void validate(Node annotation, Node container, IAnnotationBinding annotationBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions){
		Integer value = (Integer)annotationBinding.getValue();
			
		if(value.intValue() < 0) {
			problemRequestor.acceptProblem(
				annotation,
				IProblemRequestor.MAXSIZE_NOT_POSITIVE,
				new String[] {value.toString()});
		}
	}
}
