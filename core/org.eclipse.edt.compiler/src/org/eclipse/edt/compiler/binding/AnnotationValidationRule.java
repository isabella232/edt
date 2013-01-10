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
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.mof.egl.Element;


/**
 * @author svihovec
 *
 * validate the fields of an annotation
 */
public abstract class AnnotationValidationRule extends AbstractValidationRule {

	public AnnotationValidationRule(String caseSensitiveInternedName) {
		super(caseSensitiveInternedName);
	}
	
	/**
	 * 
	 * @param errorNode
	 * @param target
	 * @param targetElement
	 * @param allAnnotationsAndFields Map of annotations and annotation fields. For the fields it contains their resolved values.
	 * @param problemRequestor
	 * @param compilerOptions
	 */
	public abstract void validate(Node errorNode, Node target, Element targetElement, Map<String, Object> allAnnotationsAndFields, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions);

}
