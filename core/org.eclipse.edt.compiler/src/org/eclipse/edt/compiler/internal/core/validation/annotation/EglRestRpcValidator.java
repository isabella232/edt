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
package org.eclipse.edt.compiler.internal.core.validation.annotation;

import java.util.Map;

import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;



public class EglRestRpcValidator extends XXXrestValidator {
	protected String getName() {
		return "EglRestRpc";
	}
	@Override
	protected boolean methodIsValid() {
		return true;
	}
	public void validate(Node errorNode, Node target, ITypeBinding targetTypeBinding, Map allAnnotations, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {

	}
}