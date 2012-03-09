/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
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

import org.eclipse.edt.compiler.core.ast.FunctionParameter;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.annotation.IFieldContentAnnotationValidationRule;
import org.eclipse.edt.compiler.internal.core.validation.annotation.IInstantiationValidationRule;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author svihovec
 *
 */
public class UserDefinedInstantiationValidationRule extends InstantiationValidationRule {

	private Class validatorClass;

	public UserDefinedInstantiationValidationRule(Class validatorClass) {
		super(InternUtil.internCaseSensitive("UserDefinedInstantiationValidationRule"));
		
		this.validatorClass = validatorClass;
	}

		

	@Override
	public void validate(Node node, ITypeBinding typeBinding,
			IPartBinding declaringPart, IProblemRequestor problemRequestor,
			ICompilerOptions compilerOptions) {
		try {
			((IInstantiationValidationRule)validatorClass.newInstance()).validate(node, typeBinding, declaringPart, problemRequestor, compilerOptions);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}

	}
}
