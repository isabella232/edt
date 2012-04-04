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

import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;


/**
 * @author demurray
 */
public abstract class FieldAccessValidationAnnotationTypeBinding extends ContentValidationAnnotationTypeBinding {

	public FieldAccessValidationAnnotationTypeBinding(String caseSensitiveInternedName) {
		super(caseSensitiveInternedName);
	}
	
	public abstract boolean validateLValue(Expression lValue, IDataBinding fieldBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions);
	public abstract boolean validateRValue(Expression rValue, IDataBinding fieldBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions);
}
