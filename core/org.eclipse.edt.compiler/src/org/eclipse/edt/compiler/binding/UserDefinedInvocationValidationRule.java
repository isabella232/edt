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

import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.annotation.IInvocationValidationRule;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author svihovec
 *
 */
public class UserDefinedInvocationValidationRule extends InvocationValidationRule {

	private Class validatorClass;

	public UserDefinedInvocationValidationRule(Class validatorClass) {
		super(InternUtil.internCaseSensitive("UserDefinedInvocationValidationRule"));
		
		this.validatorClass = validatorClass;
	}	

	@Override
	public void validate(Node node, IBinding binding,
			IPartBinding declaringPart, IProblemRequestor problemRequestor,
			ICompilerOptions compilerOptions) {
		try {
			((IInvocationValidationRule)validatorClass.newInstance()).validate(node, binding, declaringPart, problemRequestor, compilerOptions);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}

	}
}
