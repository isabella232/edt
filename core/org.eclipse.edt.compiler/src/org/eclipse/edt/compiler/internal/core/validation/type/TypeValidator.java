/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.core.validation.type;

import org.eclipse.edt.compiler.ICompiler;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;

public class TypeValidator {
	
	public static void validate(Type type, final IProblemRequestor problemRequestor, final ICompilerOptions compilerOptions, final ICompiler compiler) {
		if (type == null) {
			return;
		}
		
		type.accept(new AbstractASTVisitor() {
			public boolean visit(org.eclipse.edt.compiler.core.ast.NameType nameType) {
				org.eclipse.edt.mof.egl.Type typeBinding = nameType.resolveType();
				if (typeBinding != null) {
					org.eclipse.edt.compiler.TypeValidator validator = compiler.getValidatorFor(typeBinding);
					if (validator != null) {
						validator.validateType(nameType, typeBinding, problemRequestor, compilerOptions);
					}
				}
				return false;
			};
		});
	}
}
