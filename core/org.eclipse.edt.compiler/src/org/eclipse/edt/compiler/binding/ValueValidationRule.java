/*******************************************************************************
 * Copyright © 2013 IBM Corporation and others.
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

public abstract class ValueValidationRule extends AbstractValidationRule {
	
	public ValueValidationRule(String caseSensitiveInternedName) {
		super(caseSensitiveInternedName);
	}

	public abstract void validate(Node errorNode, Node target, Annotation annotationBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions);
}
