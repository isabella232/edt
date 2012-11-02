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

import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.annotation.IAnnotationValidationRule;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.eglx.persistence.sql.ext.Utils;
import org.eclipse.edt.mof.eglx.persistence.sql.messages.SQLResourceKeys;

public class SQLResultSetControlValidator  implements IAnnotationValidationRule {
	
	@Override
	public void validate(Node errorNode, Node target, Element targetElement, Map<String, Object> allAnnotationsAndFields, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		if (targetElement instanceof Field) {
			if (!Utils.isSQLResultSet(((Field)targetElement).getType()) && !Utils.isSQLStatement(((Field)targetElement).getType())) {
				problemRequestor.acceptProblem(
						errorNode,
						SQLResourceKeys.SQLRESULTSET_ANNOTATION_TYPE_ERROR,
						IStatus.ERROR,
						new String[] {},
						SQLResourceKeys.getResourceBundleForKeys());
			}
		}
	}

}
