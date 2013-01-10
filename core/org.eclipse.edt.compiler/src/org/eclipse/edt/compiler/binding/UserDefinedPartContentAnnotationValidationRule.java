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
package org.eclipse.edt.compiler.binding;

import java.util.Map;

import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.validation.annotation.IPartContentAnnotationValidationRule;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.utils.NameUtile;


/**
 * @author svihovec
 */
public class UserDefinedPartContentAnnotationValidationRule extends PartContentValidationRule {

	private Class validatorClass;

	public UserDefinedPartContentAnnotationValidationRule(Class validatorClass) {
		super(NameUtile.getAsName("PartDefinedFieldContentAnnotationValidationRule"));
		
		this.validatorClass = validatorClass;
	}
	
	@Override
	public void validate(Node errorNode, Node target, Map<String, Map<Annotation, Object[]>> allAnnotations, IProblemRequestor problemRequestor) {
		try {
			((IPartContentAnnotationValidationRule)validatorClass.newInstance()).validate(errorNode, target, allAnnotations, problemRequestor);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

}
