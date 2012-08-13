/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.mof.eglx.persistence.sql.validation;

import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.annotation.IAnnotationValidationRule;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.TypedElement;
import org.eclipse.edt.mof.eglx.persistence.sql.Utils;

public class SQLResultSetControlValidator  implements IAnnotationValidationRule{
	
	@Override
	public void validate(Node errorNode, Node target, Element targetElement, Annotation annotation, final IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		if (targetElement != null) {
			boolean valid = targetElement instanceof TypedElement;
			if (valid) {
				Type type = ((TypedElement)targetElement).getType();
				valid = type != null && (Utils.isSQLResultSet(type) || Utils.isSQLStatement(type));
			}
			
			if (!valid) {
				problemRequestor.acceptProblem(
						errorNode,
						IProblemRequestor.SQLRESULTSET_ANNOTATION_TYPE_ERROR,
						new String[] {});
			}
		}
	}

}
