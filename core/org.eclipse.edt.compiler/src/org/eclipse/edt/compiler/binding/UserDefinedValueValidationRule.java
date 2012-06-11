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
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author svihovec
 *
 */
public class UserDefinedValueValidationRule extends	ValueValidationAnnotationTypeBinding {

	private Class validatorClass;

	public UserDefinedValueValidationRule(Class validatorClass) {
		super(InternUtil.internCaseSensitive("UserDefinedValueValidationRule"));
		
		this.validatorClass = validatorClass;
	}

	public void validate(Node errorNode, Node target, IAnnotationBinding annotationBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		try {
			if(annotationBinding.getValue() != null) {
				((IValueValidationRule)validatorClass.newInstance()).validate(errorNode, target, annotationBinding, problemRequestor, compilerOptions);
			}
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
