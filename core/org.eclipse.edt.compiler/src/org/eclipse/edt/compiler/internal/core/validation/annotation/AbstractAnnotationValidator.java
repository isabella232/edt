/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
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

import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.ast.AnnotationExpression;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;



/**
 * @author svihovec
 *
 */
public abstract class AbstractAnnotationValidator {
	
	protected ITypeBinding type;
	protected AnnotationExpression annotation;
	protected IProblemRequestor problemRequestor;
	
	public AbstractAnnotationValidator(AnnotationExpression annotation, ITypeBinding type, IProblemRequestor problemRequestor) {
		this.annotation = annotation;
		this.type = type;
		this.problemRequestor = problemRequestor;
	}
	
	public abstract void validate();	
}
