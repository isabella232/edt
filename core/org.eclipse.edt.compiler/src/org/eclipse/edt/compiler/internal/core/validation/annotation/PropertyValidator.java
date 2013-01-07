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
package org.eclipse.edt.compiler.internal.core.validation.annotation;

import java.util.Map;

import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.ExternalType;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Part;

public class PropertyValidator implements IAnnotationValidationRule {

	@Override
	public void validate(Node errorNode, Node target, Element targetElement, Map<String, Object> allAnnotationsAndFields, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		// @Property is only allowed on fields of external types.
		if (targetElement instanceof Member) {
			Part declarer = BindingUtil.getDeclaringPart((Member)targetElement);
			if (declarer != null && !(declarer instanceof ExternalType)) {
				problemRequestor.acceptProblem(errorNode, IProblemRequestor.PROPERTY_ANNOTATION_MUST_BE_IN_EXTERNALTYPE, new String[]{"@Property"}); //$NON-NLS-1$
			}
		}
	}
}
