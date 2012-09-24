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

import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.annotation.IValueValidationRule;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.utils.NameUtile;


/**
 * @author svihovec
 *
 */
public class UserDefinedValueValidationRule extends	ValueValidationRule {

	private Class validatorClass;

	public UserDefinedValueValidationRule(Class validatorClass) {
		super(NameUtile.getAsName("UserDefinedValueValidationRule"));
		
		this.validatorClass = validatorClass;
	}
	
	@Override
	public void validate(Node errorNode, Node target, Annotation annotationBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		try {
			((IValueValidationRule)validatorClass.newInstance()).validate(errorNode, target, annotationBinding, problemRequestor, compilerOptions);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

}
