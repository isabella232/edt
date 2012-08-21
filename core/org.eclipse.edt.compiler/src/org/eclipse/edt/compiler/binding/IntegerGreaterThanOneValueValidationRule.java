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
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.utils.NameUtile;


public class IntegerGreaterThanOneValueValidationRule extends ValueValidationRule {
	
	public static IntegerGreaterThanOneValueValidationRule INSTANCE = new IntegerGreaterThanOneValueValidationRule();
	
	private IntegerGreaterThanOneValueValidationRule() {
		super(NameUtile.getAsName("IntegerGreaterThanOneValueValidationRule"));
	}

	public void validate(Node errorNode, Node target, Annotation annotationBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		boolean valid = false;
		try {
			int value = Integer.parseInt(annotationBinding.getValue().toString());
			valid = value >= 1;
		}
		catch(NumberFormatException e) {
		}
		
		if(!valid) {
			problemRequestor.acceptProblem(errorNode,
				IProblemRequestor.PROPERTY_REQUIRES_INTEGER_GREATER_THAN_ZERO,
				new String[] { annotationBinding.getEClass().getETypeSignature() });
		}
	}
}
