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
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.IRPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.Constructor;
import org.eclipse.edt.mof.egl.ParameterizedType;
import org.eclipse.edt.mof.egl.StructPart;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

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
	
	//TODO this check is also needed for NewExpressions
	public static void validateInstantiatable(Type type, IPartBinding declaringPart, boolean isNullable, IProblemRequestor problemRequestor) {
		// Non-nullable reference types must have a public default constructor in order to be instantiatable.
		if (!isNullable) {
			org.eclipse.edt.mof.egl.Type typeBinding = type.resolveType();
			//TODO allowed without constructor if it's a type being used within itself.
			if (typeBinding != null && !(typeBinding instanceof ParameterizedType)
					&& TypeUtils.isReferenceType(typeBinding) && !hasPublicDefaultConstructor(typeBinding)) {
				// Don't need to throw error if the field is in an ExternalType, or if the field's type is the same as the declaring part's type.
				if (declaringPart == null || (declaringPart.getKind() != ITypeBinding.EXTERNALTYPE_BINDING
						&& !(declaringPart instanceof IRPartBinding && typeBinding.equals(((IRPartBinding)declaringPart).getIrPart())))) {
					problemRequestor.acceptProblem(type,
							IProblemRequestor.TYPE_NOT_INSTANTIABLE,
							new String[] {type.getCanonicalName()});
				}
			}
		}
	}
	
	private static boolean hasPublicDefaultConstructor(org.eclipse.edt.mof.egl.Type typeBinding) {
		if (typeBinding.getClassifier() instanceof StructPart) {
			for (Constructor con : ((StructPart)typeBinding.getClassifier()).getConstructors()) {
				if (con.getParameters().size() == 0 && !BindingUtil.isPrivate(con)) {
					return true;
				}
			}
		}
		return false;
	}
}
