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
package org.eclipse.edt.rui.validation.annotation;

import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.annotation.IValueValidationRule;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.rui.messages.RUIResourceKeys;


/**
 * @author svihovec
 */
public class MinimumInputAnnotationValueValidator implements IValueValidationRule {

	@Override
	public void validate(Node errorNode, Node target, Annotation annotation, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		if (annotation.getValue() != null) {
			boolean valid = false;
			try {
				int value = Integer.parseInt(annotation.getValue().toString());
				valid = value >= 0;
			}
			catch(NumberFormatException e) {
			}
			
			if(!valid) {
				problemRequestor.acceptProblem(errorNode,
					RUIResourceKeys.PROPERTY_MINIMUM_INPUT_MUST_BE_GREATER_THAN_ZERO,
					IMarker.SEVERITY_ERROR,
					new String[] { annotation.getValue().toString() },
					RUIResourceKeys.getResourceBundleForKeys());
			}
		}
	}
}
