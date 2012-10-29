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

import java.util.Map;

import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.annotation.IAnnotationValidationRule;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.utils.NameUtile;


/**
 * @author svihovec
 */
public class UserDefinedAnnotationValidationRule extends AnnotationValidationRule {

	private Class validatorClass;

	public UserDefinedAnnotationValidationRule(Class validatorClass) {
		super(NameUtile.getAsName("UserDefinedAnnotationValidationRule"));
		
		this.validatorClass = validatorClass;
	}
	
	@Override
	public void validate(Node errorNode, Node target, Element targetElement, Map<String, Object> allAnnotationsAndFields, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		try {
			((IAnnotationValidationRule)validatorClass.newInstance()).validate(errorNode, target, targetElement, allAnnotationsAndFields, problemRequestor, compilerOptions);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

}
